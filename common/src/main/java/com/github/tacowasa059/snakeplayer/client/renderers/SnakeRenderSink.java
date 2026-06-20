package com.github.tacowasa059.snakeplayer.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * 1.21.6 で描画が「即時描画(MultiBufferSource)」から「submit パイプライン(SubmitNodeCollector)」へ
 * 変わったため、第三者視点（エンティティの submit 内）と一人称視点（ワールド描画の MultiBufferSource）の
 * 両方を同じ描画ロジックで扱えるように抽象化する。
 *
 * - 第三者視点: {@code collector::submitCustomGeometry}（描画は遅延実行＝pose はスナップショット）
 * - 一人称視点: {@code (ps, rt, geom) -> geom.render(ps.last(), bufferSource.getBuffer(rt))}（即時描画）
 */
@FunctionalInterface
public interface SnakeRenderSink {
    void submit(PoseStack poseStack, RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer geometry);
}
