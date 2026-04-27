package me.pajic.tiered_chests.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.util.ChestTier;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class TieredBarrelSpecialRenderer implements SpecialModelRenderer<Integer> {
    private final ChestTier tier;

    public TieredBarrelSpecialRenderer(ChestTier tier) {
        this.tier = tier;
    }

    @Override
    public @Nullable Integer extractArgument(ItemStack stack) {
        int state = 0;
        if (TieredChests.CONFIG.texturePackOverride.get())
            state |= 1;
        if (TieredChests.CONFIG.fancyBarrels.get())
            state |= 2;
        return state;
    }

    @Override
    public void submit(@Nullable Integer argument, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
            int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();

        String tierName = tier.getSerializedName();
        boolean overrideOn = TieredChests.CONFIG.texturePackOverride.get();
        Identifier woodTop;
        Identifier woodBottom;
        Identifier woodSide;

        if (overrideOn) {
            woodTop = Identifier.fromNamespaceAndPath("minecraft", "textures/block/barrel_top.png");
            woodBottom = Identifier.fromNamespaceAndPath("minecraft", "textures/block/barrel_bottom.png");
            woodSide = Identifier.fromNamespaceAndPath("minecraft", "textures/block/barrel_side.png");
        } else if (TieredChests.CONFIG.fancyBarrels.get()) {
            woodTop = TieredChests.id("textures/block/wood_barrel_top_fancy.png");
            woodBottom = TieredChests.id("textures/block/wood_barrel_bottom_fancy.png");
            woodSide = TieredChests.id("textures/block/wood_barrel_side_fancy.png");
        } else {
            woodTop = TieredChests.id("textures/block/wood_barrel_top.png");
            woodBottom = TieredChests.id("textures/block/wood_barrel_bottom.png");
            woodSide = TieredChests.id("textures/block/wood_barrel_side.png");
        }

        Identifier tierTop = TieredChests.id("textures/block/" + tierName + "_barrel_top.png");
        Identifier tierBottom = TieredChests.id("textures/block/" + tierName + "_barrel_bottom.png");
        Identifier tierSide = TieredChests.id("textures/block/" + tierName + "_barrel_side.png");

        // Base Wood Cube
        renderCube(poseStack, submitNodeCollector, lightCoords,
                woodSide, woodSide, woodSide, woodSide, woodTop, woodBottom,
                0f, 0f, 0f, 1f, 1f, 1f);

        // Metal Tier Overlay
        if (tier != ChestTier.WOOD) {
            float offset = 0f;
            float zO = 0.001f; // Slightly larger for items to be safe

            renderFace(submitNodeCollector, poseStack, RenderTypes.entityCutout(tierSide), lightCoords,
                    1f - offset, offset, -zO, offset, offset, -zO, offset, 1f - offset, -zO, 1f - offset, 1f - offset,
                    -zO,
                    1f, 0f, 0f, 1f, 0, 0, -1); // North
            renderFace(submitNodeCollector, poseStack, RenderTypes.entityCutout(tierSide), lightCoords,
                    offset, offset, 1f + zO, 1f - offset, offset, 1f + zO, 1f - offset, 1f - offset, 1f + zO, offset,
                    1f - offset, 1f + zO,
                    0f, 1f, 1f, 0f, 0, 0, 1); // South
            renderFace(submitNodeCollector, poseStack, RenderTypes.entityCutout(tierSide), lightCoords,
                    -zO, offset, offset, -zO, offset, 1f - offset, -zO, 1f - offset, 1f - offset, -zO, 1f - offset,
                    offset,
                    0f, 1f, 1f, 0f, -1, 0, 0); // West
            renderFace(submitNodeCollector, poseStack, RenderTypes.entityCutout(tierSide), lightCoords,
                    1f + zO, offset, 1f - offset, 1f + zO, offset, offset, 1f + zO, 1f - offset, offset, 1f + zO,
                    1f - offset, 1f - offset,
                    1f, 0f, 0f, 1f, 1, 0, 0); // East
            renderFace(submitNodeCollector, poseStack, RenderTypes.entityCutout(tierTop), lightCoords,
                    offset, 1f + zO, 1f - offset, 1f - offset, 1f + zO, 1f - offset, 1f - offset, 1f + zO, offset,
                    offset, 1f + zO, offset,
                    0f, 1f, 1f, 0f, 0, 1, 0); // Top
            renderFace(submitNodeCollector, poseStack, RenderTypes.entityCutout(tierBottom), lightCoords,
                    offset, -zO, offset, 1f - offset, -zO, offset, 1f - offset, -zO, 1f - offset, offset, -zO,
                    1f - offset,
                    0f, 1f, 1f, 0f, 0, -1, 0); // Bottom
        }

        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new org.joml.Vector3f(0.0f, 0.0f, 0.0f));
        output.accept(new org.joml.Vector3f(1.0f, 1.0f, 1.0f));
    }

    public record Unbaked(ChestTier tier) implements SpecialModelRenderer.Unbaked<Integer> {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ChestTier.CODEC.fieldOf("tier").forGetter(Unbaked::tier)).apply(inst, Unbaked::new));

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
                x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0,
                1f, 0f, 0f, 1f, 0, 0, -1);
        renderFace(collector, poseStack, RenderTypes.entityCutout(south), light,
                x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1,
                0f, 1f, 1f, 0f, 0, 0, 1);
        renderFace(collector, poseStack, RenderTypes.entityCutout(west), light,
                x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0,
                0f, 1f, 1f, 0f, -1, 0, 0);
        renderFace(collector, poseStack, RenderTypes.entityCutout(east), light,
                x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1,
                1f, 0f, 0f, 1f, 1, 0, 0);
        renderFace(collector, poseStack, RenderTypes.entityCutout(top), light,
                x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0,
                0f, 1f, 1f, 0f, 0, 1, 0);
        renderFace(collector, poseStack, RenderTypes.entityCutout(bottom), light,
                x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1,
                0f, 1f, 1f, 0f, 0, -1, 0);
    }

    private static void renderFace(SubmitNodeCollector collector, PoseStack poseStack, RenderType renderType, int light,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float u0, float u1, float v0, float v1,
            int nx, int ny, int nz) {

        collector.submitCustomGeometry(poseStack, renderType, (pose, buf) -> {
            buf.addVertex(pose, x0, y0, z0).setColor(-1).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light).setNormal(pose, nx, ny, nz);
            buf.addVertex(pose, x1, y1, z1).setColor(-1).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light).setNormal(pose, nx, ny, nz);
            buf.addVertex(pose, x2, y2, z2).setColor(-1).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light).setNormal(pose, nx, ny, nz);
            buf.addVertex(pose, x3, y3, z3).setColor(-1).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light).setNormal(pose, nx, ny, nz);
        });
    }
}
