package com.github.tacowasa059.snakeplayer.common.utils;

import com.github.tacowasa059.snakeplayer.common.Config;
import com.github.tacowasa059.snakeplayer.common.Interface.IPlayerData;
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

/**
 * respawn position manager
 */
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
            for(PlayerPart playerPart : playerData.snakePlayer$getPlayerParts()){
                updateGrid(playerPart);
            }
        }

        falseList = Collections.synchronizedList(new ArrayList<>());
    }

    public <T extends Entity> void updateGrid(T entity){
        Vec3 pos = entity.position();
        if(entity instanceof Player){
            markLineOfSightInGrid(entity, 8, 2);
        }
        int index = getGridIndexFromPos(pos.x, pos.z);

        if (index != -1) {
            grid.set(index, true);
        }
    }

    /**
     * 視線方向にmaxDistanceマス, 幅2*halfWidthの分を登録する。視線の先にスポーンするのを防ぎたい。
     * @param entity entity
     * @param maxDistance maxDistance
     * @param halfWidth halfWidth
     */
    public void markLineOfSightInGrid(Entity entity, int maxDistance, int halfWidth) {
        Vec3 eyePos = entity.getEyePosition();
        Vec3 look = entity.getLookAngle().normalize();
        Vec3 horizontalLook = new Vec3(look.x, 0, look.z).normalize();

        Vec3 normal = new Vec3(-horizontalLook.z, 0, horizontalLook.x);

        Vec3 end = eyePos.add(horizontalLook.scale(maxDistance));

        int x1 = (int) eyePos.x;
        int z1 = (int) eyePos.z;
        int x2 = (int) end.x;
        int z2 = (int) end.z;

        int dx = Math.abs(x2 - x1);
        int dz = Math.abs(z2 - z1);
        int sx = Integer.compare(x2, x1);
        int sz = Integer.compare(z2, z1);

        int err = dx - dz;

        int x = x1;
        int z = z1;

        for (int i = 0; i <= maxDistance; i++) {
            // 横幅方向に±halfWidthずらす
            for (int offset = -halfWidth; offset <= halfWidth; offset++) {
                double offsetX = x + normal.x * offset;
                double offsetZ = z + normal.z * offset;

                int index = getGridIndexFromPos(offsetX, offsetZ);
                if (index != -1) {
                    grid.set(index, true);
                }
            }

            if (x == x2 && z == z2) break;

            int e2 = 2 * err;
            if (e2 > -dz) {
                err -= dz;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                z += sz;
            }
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

    /**
     * スポーン位置Yを定めるバニラ実装
     * @param blockGetter blockGetter
     * @param maxHeight 最大高さ
     * @param x x座標
     * @param z z座標
     * @return y座標
     */
    public int getSpawnY(BlockGetter blockGetter, int maxHeight, double x, double z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, maxHeight + 1, z);
        boolean flag = blockGetter.getBlockState(blockpos$mutableblockpos).isAir();
        blockpos$mutableblockpos.move(Direction.DOWN);

        boolean flag2;
        for(boolean flag1 = blockGetter.getBlockState(blockpos$mutableblockpos).isAir(); blockpos$mutableblockpos.getY() > blockGetter.getMinBuildHeight(); flag1 = flag2) {
            blockpos$mutableblockpos.move(Direction.DOWN);
            flag2 = blockGetter.getBlockState(blockpos$mutableblockpos).isAir();
            if (!flag2 && flag1 && flag) {
                return blockpos$mutableblockpos.getY() + 1;
            }

            flag = flag1;
        }

        return maxHeight + 1;
    }
}
