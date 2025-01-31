package com.github.tacowasa059.snakeplayer.utils;

import com.github.tacowasa059.snakeplayer.Config;
import com.github.tacowasa059.snakeplayer.Interface.IPlayerData;
import com.github.tacowasa059.snakeplayer.common.entity.PlayerPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GridManager {
    public static MinecraftServer server;
    public static double cx, cz = 0;
    public static double L = 50;
    public static double r = 5;
    public static int n = 10;
    public static List<Boolean> grid = Collections.synchronizedList(new ArrayList<>());

    public static List<int[]> falseList = Collections.synchronizedList(new ArrayList<>());


    // プレイヤーの位置を取得してグリッドを更新
    public void updateGrid() {
        cx = Config.CX.get();
        cz = Config.CZ.get();
        L = Config.L.get();
        r = Math.min(Config.R.get(),L);
        Config.R.set(r);
        n = (int) (L / r);
        // グリッドをクリア
        grid = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(n * n, false)));

        // プレイヤーの座標をグリッドに反映
        for(Player player : server.overworld().getPlayers(player -> !player.isSpectator())){
            IPlayerData playerData = (IPlayerData) player;

            updateGrid(player);
            for(PlayerPart playerPart : playerData.getPlayerParts()){
                updateGrid(playerPart);
            }
        }

        falseList = Collections.synchronizedList(new ArrayList<>());
    }

    public <T extends Entity> void updateGrid(T entity){
        Vec3 pos = entity.position();
        int index = getGridIndexFromPos(pos.x, pos.z);
        if (index != -1) {
            grid.set(index, true);
        }
    }

    private int getGridIndex(int gx, int gz) {
        if (gx < 0 || gx >= n || gz < 0 || gz >= n) return -1;
        return gx + gz * n;
    }

    private int getGridIndexFromPos(double x, double z) {
        int gx = (int) ((x - (cx - L / 2)) / r);
        int gz = (int) ((z - (cz - L / 2)) / r);
        if (gx < 0 || gx >= n || gz < 0 || gz >= n) return -1;
        return gx + gz * n;
    }

    private boolean isNeighborhoodEmpty(int gx, int gz) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int index = getGridIndex(gx + dx, gz + dz);
                if (index != -1 && grid.get(index)) {
                    return false;
                }
            }
        }
        return true;
    }

    public double[] sampleValidPoint(Player player) {

        if(falseList.isEmpty()){
            // false なセルをリストアップ
            for (int gz = 0; gz < n; gz++) {
                for (int gx = 0; gx < n; gx++) {
                    int index = getGridIndex(gx, gz);
                    if (!grid.get(index)) {
                        falseList.add(new int[]{gx, gz});
                    }
                }
            }
            Collections.shuffle(falseList);
        }

        // すべて埋まっていた場合は null を返す
        if (falseList.isEmpty()) return null;

        // ランダムに false なセルを選んで 3×3 をチェック
        for (int attempt = falseList.size()-1; attempt >=0; attempt--) {
            int[] cell = falseList.get(attempt);
            int gx = cell[0], gz = cell[1];

            if (isNeighborhoodEmpty(gx, gz)) {
                double x = cx - L / 2 + (gx + Math.random()) * r;
                double z = cz - L / 2 + (gz + Math.random()) * r;

                ServerLevel serverLevel = (ServerLevel) player.level();
                int maxHeight = 312;
                int height = getSpawnY(serverLevel,maxHeight, x, z);
                if(height == maxHeight +1){
                    continue;
                }

                falseList.remove(attempt);
                return new double[]{x, height, z};
            }
            falseList.remove(attempt);
        }

        // 見つからなかった場合にランダムに選ぶ
        for (int i = 0; i < 10; i++) { // 10回までランダムに試行
            double x = cx - L / 2 + Math.random() * L;
            double z = cz - L / 2 + Math.random() * L;

            ServerLevel serverLevel = (ServerLevel) player.level();
            int maxHeight = 312;
            int height = getSpawnY(serverLevel, maxHeight, x, z);
            if (height != maxHeight + 1) {
                return new double[]{x, height, z};
            }
        }
        return null;
    }

    public int getSpawnY(BlockGetter p_138759_, int maxHeight, double x, double z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, maxHeight + 1, z);
        boolean flag = p_138759_.getBlockState(blockpos$mutableblockpos).isAir();
        blockpos$mutableblockpos.move(Direction.DOWN);

        boolean flag2;
        for(boolean flag1 = p_138759_.getBlockState(blockpos$mutableblockpos).isAir(); blockpos$mutableblockpos.getY() > p_138759_.getMinBuildHeight(); flag1 = flag2) {
            blockpos$mutableblockpos.move(Direction.DOWN);
            flag2 = p_138759_.getBlockState(blockpos$mutableblockpos).isAir();
            if (!flag2 && flag1 && flag) {
                return blockpos$mutableblockpos.getY() + 1;
            }

            flag = flag1;
        }

        return maxHeight + 1;
    }
}
