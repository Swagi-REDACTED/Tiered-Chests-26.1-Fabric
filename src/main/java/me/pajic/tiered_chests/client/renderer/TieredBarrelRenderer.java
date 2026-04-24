package me.pajic.tiered_chests.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.block.entity.TieredBarrelBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class TieredBarrelRenderer implements BlockEntityRenderer<TieredBarrelBlockEntity, TieredBarrelRenderer.TieredBarrelRenderState> {
    public TieredBarrelRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static class TieredBarrelRenderState extends BlockEntityRenderState {
        public Direction facing;
        public boolean open;
        public me.pajic.tiered_chests.util.ChestTier tier;
    }

    private static final java.util.Map<Direction, com.mojang.math.Transformation> TRANSFORMATIONS = net.minecraft.util.Util.makeEnumMap(Direction.class, TieredBarrelRenderer::createModelTransformation);

    private static com.mojang.math.Transformation createModelTransformation(Direction direction) {
        switch (direction) {
            case DOWN:
                return new com.mojang.math.Transformation(null, com.mojang.math.Axis.XP.rotationDegrees(180.0F), null, null);
            case UP:
            default:
                return com.mojang.math.Transformation.IDENTITY;
            case NORTH:
                return new com.mojang.math.Transformation(null, com.mojang.math.Axis.XP.rotationDegrees(90.0F), null, null);
            case SOUTH:
                return new com.mojang.math.Transformation(null, com.mojang.math.Axis.XP.rotationDegrees(-90.0F), null, null);
            case WEST:
                return new com.mojang.math.Transformation(null, com.mojang.math.Axis.ZP.rotationDegrees(90.0F), null, null);
            case EAST:
                return new com.mojang.math.Transformation(null, com.mojang.math.Axis.ZP.rotationDegrees(-90.0F), null, null);
        }
    }

    @Override
    public TieredBarrelRenderState createRenderState() {
        return new TieredBarrelRenderState();
    }

    @Override
    public void extractRenderState(TieredBarrelBlockEntity blockEntity, TieredBarrelRenderState state, float partialTicks, net.minecraft.world.phys.Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        BlockState blockState = blockEntity.getBlockState();
        state.facing = blockState.hasProperty(BlockStateProperties.FACING) ? blockState.getValue(BlockStateProperties.FACING) : Direction.NORTH;
        state.open = blockState.hasProperty(BlockStateProperties.OPEN) ? blockState.getValue(BlockStateProperties.OPEN) : false;
        state.tier = blockEntity.getTier();
        state.lightCoords = net.minecraft.client.renderer.LevelRenderer.getLightCoords(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    @Override
    public void submit(TieredBarrelRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        
        // Center for rotation
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(TRANSFORMATIONS.get(state.facing));
        poseStack.translate(-0.5, -0.5, -0.5);

        String tierName = state.tier.getSerializedName();
        
        Identifier woodTop = TieredChests.id("textures/block/wood_barrel_top" + (state.open ? "_open" : "") + ".png");
        Identifier woodBottom = TieredChests.id("textures/block/wood_barrel_bottom.png");
        Identifier woodSide = TieredChests.id("textures/block/wood_barrel_side.png");

        Identifier tierTop = TieredChests.id("textures/block/" + tierName + "_barrel_top.png");
        Identifier tierBottom = TieredChests.id("textures/block/" + tierName + "_barrel_bottom.png");
        Identifier tierSide = TieredChests.id("textures/block/" + tierName + "_barrel_side.png");

        int light = state.lightCoords;

        // Base Wood Cube (0.0 to 1.0)
        renderCube(poseStack, submitNodeCollector, light,
                woodSide, woodSide, woodSide, woodSide, woodTop, woodBottom,
                0f, 0f, 0f, 1f, 1f, 1f);

        // Metal Tier Overlay (slightly larger to avoid z-fighting)
        if (state.tier != me.pajic.tiered_chests.util.ChestTier.WOOD) {
            renderCube(poseStack, submitNodeCollector, light,
                    tierSide, tierSide, tierSide, tierSide, tierTop, tierBottom,
                    -0.001f, -0.001f, -0.001f, 1.001f, 1.001f, 1.001f);
        }

        poseStack.popPose();
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
