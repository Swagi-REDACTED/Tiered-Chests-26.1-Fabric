package me.pajic.tiered_chests.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.util.ChestTier;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class TieredBarrelSpecialRenderer implements NoDataSpecialModelRenderer {
    private final ChestTier tier;

    public TieredBarrelSpecialRenderer(ChestTier tier) {
        this.tier = tier;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();
        
        String tierName = tier.getSerializedName();
        
        Identifier woodTop = TieredChests.id("textures/block/wood_barrel_top.png");
        Identifier woodBottom = TieredChests.id("textures/block/wood_barrel_bottom.png");
        Identifier woodSide = TieredChests.id("textures/block/wood_barrel_side.png");

        Identifier tierTop = TieredChests.id("textures/block/" + tierName + "_barrel_top.png");
        Identifier tierBottom = TieredChests.id("textures/block/" + tierName + "_barrel_bottom.png");
        Identifier tierSide = TieredChests.id("textures/block/" + tierName + "_barrel_side.png");

        // Base Wood Cube
        renderCube(poseStack, submitNodeCollector, lightCoords,
                woodSide, woodSide, woodSide, woodSide, woodTop, woodBottom,
                0f, 0f, 0f, 1f, 1f, 1f);

        // Metal Tier Overlay
        if (tier != ChestTier.WOOD) {
            renderCube(poseStack, submitNodeCollector, lightCoords,
                    tierSide, tierSide, tierSide, tierSide, tierTop, tierBottom,
                    -0.001f, -0.001f, -0.001f, 1.001f, 1.001f, 1.001f);
        }

        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new org.joml.Vector3f(0.0f, 0.0f, 0.0f));
        output.accept(new org.joml.Vector3f(1.0f, 1.0f, 1.0f));
    }

    public record Unbaked(ChestTier tier) implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ChestTier.CODEC.fieldOf("tier").forGetter(Unbaked::tier)
        ).apply(inst, Unbaked::new));

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public TieredBarrelSpecialRenderer bake(BakingContext context) {
            return new TieredBarrelSpecialRenderer(tier);
        }
    }

    private static void renderCube(PoseStack poseStack, SubmitNodeCollector collector, int light,
                                   Identifier north, Identifier south, Identifier west, Identifier east,
                                   Identifier top, Identifier bottom,
                                   float x0, float y0, float z0, float x1, float y1, float z1) {
        renderFace(collector, poseStack, RenderTypes.entityCutout(north), light,
                x1, y0, z0,  x0, y0, z0,  x0, y1, z0,  x1, y1, z0,
                1f, 0f, 0f, 1f,  0, 0, -1);
        renderFace(collector, poseStack, RenderTypes.entityCutout(south), light,
                x0, y0, z1,  x1, y0, z1,  x1, y1, z1,  x0, y1, z1,
                0f, 1f, 1f, 0f,  0, 0, 1);
        renderFace(collector, poseStack, RenderTypes.entityCutout(west), light,
                x0, y0, z0,  x0, y0, z1,  x0, y1, z1,  x0, y1, z0,
                0f, 1f, 1f, 0f,  -1, 0, 0);
        renderFace(collector, poseStack, RenderTypes.entityCutout(east), light,
                x1, y0, z1,  x1, y0, z0,  x1, y1, z0,  x1, y1, z1,
                1f, 0f, 0f, 1f,  1, 0, 0);
        renderFace(collector, poseStack, RenderTypes.entityCutout(top), light,
                x0, y1, z1,  x1, y1, z1,  x1, y1, z0,  x0, y1, z0,
                0f, 1f, 1f, 0f,  0, 1, 0);
        renderFace(collector, poseStack, RenderTypes.entityCutout(bottom), light,
                x0, y0, z0,  x1, y0, z0,  x1, y0, z1,  x0, y0, z1,
                0f, 1f, 1f, 0f,  0, -1, 0);
    }

    private static void renderFace(SubmitNodeCollector collector, PoseStack poseStack, RenderType renderType, int light,
                                   float x0, float y0, float z0,
                                   float x1, float y1, float z1,
                                   float x2, float y2, float z2,
                                   float x3, float y3, float z3,
                                   float u0, float u1, float v0, float v1,
                                   int nx, int ny, int nz) {

        collector.submitCustomGeometry(poseStack, renderType, (pose, buf) -> {
            buf.addVertex(pose, x0, y0, z0).setColor(-1).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, nx, ny, nz);
            buf.addVertex(pose, x1, y1, z1).setColor(-1).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, nx, ny, nz);
            buf.addVertex(pose, x2, y2, z2).setColor(-1).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, nx, ny, nz);
            buf.addVertex(pose, x3, y3, z3).setColor(-1).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, nx, ny, nz);
        });
    }
}
