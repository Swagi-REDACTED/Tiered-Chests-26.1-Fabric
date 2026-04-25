package me.pajic.tiered_chests.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.block.entity.TieredBarrelBlockEntity;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;

public class TieredBarrelRenderer
                implements BlockEntityRenderer<TieredBarrelBlockEntity, TieredBarrelRenderer.TieredBarrelRenderState> {
        private final SpriteGetter sprites;

        public TieredBarrelRenderer(BlockEntityRendererProvider.Context context) {
                this.sprites = context.sprites();
        }

        public static class TieredBarrelRenderState extends BlockEntityRenderState {
                public Direction facing;
                public boolean open;
                public me.pajic.tiered_chests.util.ChestTier tier;
        }

        private static final java.util.Map<Direction, com.mojang.math.Transformation> TRANSFORMATIONS = net.minecraft.util.Util
                        .makeEnumMap(Direction.class, TieredBarrelRenderer::createModelTransformation);

        private static com.mojang.math.Transformation createModelTransformation(Direction direction) {
                switch (direction) {
                        case DOWN:
                                return new com.mojang.math.Transformation(null,
                                                com.mojang.math.Axis.XP.rotationDegrees(180.0F), null,
                                                null);
                        case NORTH:
                                return new com.mojang.math.Transformation(null,
                                                com.mojang.math.Axis.XP.rotationDegrees(-90.0F), null,
                                                null);
                        case SOUTH:
                                return new com.mojang.math.Transformation(null,
                                                com.mojang.math.Axis.XP.rotationDegrees(90.0F), null,
                                                null);
                        case WEST:
                                return new com.mojang.math.Transformation(null,
                                                com.mojang.math.Axis.ZP.rotationDegrees(90.0F), null,
                                                null);
                        case EAST:
                                return new com.mojang.math.Transformation(null,
                                                com.mojang.math.Axis.ZP.rotationDegrees(-90.0F), null,
                                                null);
                        case UP:
                        default:
                                return com.mojang.math.Transformation.IDENTITY;
                }
        }

        @Override
        public TieredBarrelRenderState createRenderState() {
                return new TieredBarrelRenderState();
        }

        @Override
        public void extractRenderState(TieredBarrelBlockEntity blockEntity, TieredBarrelRenderState state,
                        float partialTicks, net.minecraft.world.phys.Vec3 cameraPosition,
                        net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress) {
                BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
                BlockState blockState = blockEntity.getBlockState();
                state.facing = blockState.hasProperty(BlockStateProperties.FACING)
                                ? blockState.getValue(BlockStateProperties.FACING)
                                : Direction.NORTH;
                state.open = blockState.hasProperty(BlockStateProperties.OPEN)
                                ? blockState.getValue(BlockStateProperties.OPEN)
                                : false;
                state.tier = blockEntity.getTier();
        }

        @Override
        public void submit(TieredBarrelRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                        CameraRenderState camera) {
                poseStack.pushPose();

                // Center for rotation
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(TRANSFORMATIONS.get(state.facing));
                poseStack.translate(-0.5, -0.5, -0.5);

                String tierName = state.tier.getSerializedName();

                boolean overrideOn = TieredChests.CONFIG.texturePackOverride.get();
                Identifier woodTop;
                Identifier woodBottom;
                Identifier woodSide;

                if (overrideOn) {
                        woodTop = Identifier.fromNamespaceAndPath("minecraft",
                                        "block/barrel_top" + (state.open ? "_open" : ""));
                        woodBottom = Identifier.fromNamespaceAndPath("minecraft", "block/barrel_bottom");
                        woodSide = Identifier.fromNamespaceAndPath("minecraft", "block/barrel_side");
                } else if (TieredChests.CONFIG.fancyBarrels.get()) {
                        woodTop = TieredChests.id("block/wood_barrel_top" + (state.open ? "_open" : "") + "_fancy");
                        woodBottom = TieredChests.id("block/wood_barrel_bottom_fancy");
                        woodSide = TieredChests.id("block/wood_barrel_side_fancy");
                } else {
                        woodTop = TieredChests.id("block/wood_barrel_top" + (state.open ? "_open" : ""));
                        woodBottom = TieredChests.id("block/wood_barrel_bottom");
                        woodSide = TieredChests.id("block/wood_barrel_side");
                }

                Identifier tierTop = TieredChests.id("block/" + tierName + "_barrel_top");
                Identifier tierBottom = TieredChests.id("block/" + tierName + "_barrel_bottom");
                Identifier tierSide = TieredChests.id("block/" + tierName + "_barrel_side");

                int light = state.lightCoords;
                RenderType blocksType = RenderTypes.entityCutout(Sheets.BLOCKS_MAPPER.sheet());

                // Base Wood Cube
                renderCube(poseStack, submitNodeCollector, blocksType, light,
                                woodSide, woodSide, woodSide, woodSide, woodTop, woodBottom,
                                0f, 0f, 0f, 1f, 1f, 1f);

                // Metal Tier Overlay (submitted after base to draw on top)
                if (state.tier != me.pajic.tiered_chests.util.ChestTier.WOOD) {
                        float offset = 0f;
                        float zO = 0.00018f; // Tiny Z-offset to prevent Z-fighting

                        renderFace(submitNodeCollector, poseStack, blocksType, light, tierSide,
                                        1f - offset, offset, -zO, offset, offset, -zO, offset, 1f - offset, -zO,
                                        1f - offset, 1f - offset, -zO,
                                        0, 0, -1); // North
                        renderFace(submitNodeCollector, poseStack, blocksType, light, tierSide,
                                        offset, offset, 1f + zO, 1f - offset, offset, 1f + zO, 1f - offset, 1f - offset,
                                        1f + zO, offset, 1f - offset, 1f + zO,
                                        0, 0, 1); // South
                        renderFace(submitNodeCollector, poseStack, blocksType, light, tierSide,
                                        -zO, offset, offset, -zO, offset, 1f - offset, -zO, 1f - offset, 1f - offset,
                                        -zO, 1f - offset, offset,
                                        -1, 0, 0); // West
                        renderFace(submitNodeCollector, poseStack, blocksType, light, tierSide,
                                        1f + zO, offset, 1f - offset, 1f + zO, offset, offset, 1f + zO, 1f - offset,
                                        offset, 1f + zO, 1f - offset, 1f - offset,
                                        1, 0, 0); // East
                        renderFace(submitNodeCollector, poseStack, blocksType, light, tierTop,
                                        offset, 1f + zO, 1f - offset, 1f - offset, 1f + zO, 1f - offset, 1f - offset,
                                        1f + zO, offset, offset, 1f + zO, offset,
                                        0, 1, 0); // Top
                        renderFace(submitNodeCollector, poseStack, blocksType, light, tierBottom,
                                        offset, -zO, offset, 1f - offset, -zO, offset, 1f - offset, -zO, 1f - offset,
                                        offset, -zO, 1f - offset,
                                        0, -1, 0); // Bottom
                }

                // Breaking Animation
                if (state.breakProgress != null) {
                        RenderType breakingType = net.minecraft.client.resources.model.ModelBakery.DESTROY_TYPES
                                        .get(state.breakProgress.progress());
                        renderCubeBreaking(poseStack, submitNodeCollector, breakingType, state.breakProgress, light,
                                        -0.001f, -0.001f, -0.001f, 1.001f, 1.001f, 1.001f);
                }

                poseStack.popPose();
        }

        private void renderCube(PoseStack poseStack, SubmitNodeCollector collector, RenderType renderType, int light,
                        Identifier north, Identifier south, Identifier west, Identifier east,
                        Identifier top, Identifier bottom,
                        float x0, float y0, float z0, float x1, float y1, float z1) {
                renderFace(collector, poseStack, renderType, light, north,
                                x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0,
                                0, 0, -1);
                renderFace(collector, poseStack, renderType, light, south,
                                x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1,
                                0, 0, 1);
                renderFace(collector, poseStack, renderType, light, west,
                                x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0,
                                -1, 0, 0);
                renderFace(collector, poseStack, renderType, light, east,
                                x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1,
                                1, 0, 0);
                renderFace(collector, poseStack, renderType, light, top,
                                x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0,
                                0, 1, 0);
                renderFace(collector, poseStack, renderType, light, bottom,
                                x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1,
                                0, -1, 0);
        }

        private void renderFace(SubmitNodeCollector collector, PoseStack poseStack, RenderType renderType, int light,
                        Identifier texture,
                        float x0, float y0, float z0,
                        float x1, float y1, float z1,
                        float x2, float y2, float z2,
                        float x3, float y3, float z3,
                        int nx, int ny, int nz) {

                TextureAtlasSprite sprite = sprites.get(new SpriteId(Sheets.BLOCKS_MAPPER.sheet(), texture));
                float u0 = sprite.getU0();
                float u1 = sprite.getU1();
                float v0 = sprite.getV0();
                float v1 = sprite.getV1();

                collector.submitCustomGeometry(poseStack, renderType, (pose, buf) -> {
                        buf.addVertex(pose, x0, y0, z0).setColor(-1).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY)
                                        .setLight(light).setNormal(pose, nx, ny, nz);
                        buf.addVertex(pose, x1, y1, z1).setColor(-1).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY)
                                        .setLight(light).setNormal(pose, nx, ny, nz);
                        buf.addVertex(pose, x2, y2, z2).setColor(-1).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY)
                                        .setLight(light).setNormal(pose, nx, ny, nz);
                        buf.addVertex(pose, x3, y3, z3).setColor(-1).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY)
                                        .setLight(light).setNormal(pose, nx, ny, nz);
                });
        }

        private static void renderCubeBreaking(PoseStack poseStack, SubmitNodeCollector collector,
                        RenderType breakingType,
                        net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress,
                        int light,
                        float x0, float y0, float z0, float x1, float y1, float z1) {
                renderFaceBreaking(collector, poseStack, breakingType, breakProgress, light,
                                x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0, 0, 0, -1);
                renderFaceBreaking(collector, poseStack, breakingType, breakProgress, light,
                                x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1, 0, 0, 1);
                renderFaceBreaking(collector, poseStack, breakingType, breakProgress, light,
                                x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0, -1, 0, 0);
                renderFaceBreaking(collector, poseStack, breakingType, breakProgress, light,
                                x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1, 1, 0, 0);
                renderFaceBreaking(collector, poseStack, breakingType, breakProgress, light,
                                x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0, 0, 1, 0);
                renderFaceBreaking(collector, poseStack, breakingType, breakProgress, light,
                                x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1, 0, -1, 0);
        }

        private static void renderFaceBreaking(SubmitNodeCollector collector, PoseStack poseStack,
                        RenderType renderType,
                        net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress,
                        int light,
                        float x0, float y0, float z0,
                        float x1, float y1, float z1,
                        float x2, float y2, float z2,
                        float x3, float y3, float z3,
                        int nx, int ny, int nz) {

                collector.submitCustomGeometry(poseStack, renderType, (pose, buf) -> {
                        com.mojang.blaze3d.vertex.VertexConsumer breakingBuf = new com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator(
                                        buf, breakProgress.cameraPose(), 1.0F);
                        breakingBuf.addVertex(pose, x0, y0, z0).setColor(-1).setUv(0, 1)
                                        .setOverlay(OverlayTexture.NO_OVERLAY)
                                        .setLight(light).setNormal(pose, nx, ny, nz);
                        breakingBuf.addVertex(pose, x1, y1, z1).setColor(-1).setUv(1, 1)
                                        .setOverlay(OverlayTexture.NO_OVERLAY)
                                        .setLight(light).setNormal(pose, nx, ny, nz);
                        breakingBuf.addVertex(pose, x2, y2, z2).setColor(-1).setUv(1, 0)
                                        .setOverlay(OverlayTexture.NO_OVERLAY)
                                        .setLight(light).setNormal(pose, nx, ny, nz);
                        breakingBuf.addVertex(pose, x3, y3, z3).setColor(-1).setUv(0, 0)
                                        .setOverlay(OverlayTexture.NO_OVERLAY)
                                        .setLight(light).setNormal(pose, nx, ny, nz);
                });
        }
}
