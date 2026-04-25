package me.pajic.tiered_chests.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.block.entity.TieredChestBlockEntity;
import me.pajic.tiered_chests.block.TieredChestBlock;
import me.pajic.tiered_chests.client.ChestTextureManager;
import me.pajic.tiered_chests.client.model.TieredChestModel;
import me.pajic.tiered_chests.config.ModConfig;
import me.pajic.tiered_chests.platform.fabric.FabricClientEntrypoint;
import me.pajic.tiered_chests.util.ChestTier;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TieredChestRenderer implements BlockEntityRenderer<TieredChestBlockEntity, TieredChestRenderState> {
    private static final Map<Direction, Transformation> TRANSFORMATIONS = Util.makeEnumMap(Direction.class,
            TieredChestRenderer::createModelTransformation);
    public static final MultiblockChestResources<ModelLayerLocation> LAYERS = new MultiblockChestResources<>(
            ModelLayers.CHEST, ModelLayers.DOUBLE_CHEST_LEFT, ModelLayers.DOUBLE_CHEST_RIGHT);
    private final SpriteGetter sprites;
    private final MultiblockChestResources<ChestModel> models;
    private final MultiblockChestResources<ChestModel> modelsNoLock;

    public TieredChestRenderer(BlockEntityRendererProvider.Context context) {
        this.sprites = context.sprites();
        this.models = LAYERS.map(layer -> new ChestModel(context.bakeLayer(layer)));
        this.modelsNoLock = LAYERS.map(layer -> {
            ChestModel m = new ChestModel(context.bakeLayer(layer));
            try {
                m.root().getChild("lock").visible = false;
            } catch (Exception e) {
            }
            return m;
        });
    }

    @Override
    public TieredChestRenderState createRenderState() {
        return new TieredChestRenderState();
    }

    @Override
    public void extractRenderState(TieredChestBlockEntity blockEntity, TieredChestRenderState state, float partialTicks,
            Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        BlockState blockState = blockEntity.getBlockState();
        state.facing = blockState.getValue(TieredChestBlock.FACING);
        state.type = blockState.getValue(TieredChestBlock.TYPE);
        state.tier = blockEntity.getTier();
        DoubleBlockCombiner.NeighborCombineResult<? extends TieredChestBlockEntity> combineResult = ((TieredChestBlock) blockState
                .getBlock()).combine(blockState, blockEntity.getLevel(), blockEntity.getBlockPos(), true);
        state.open = combineResult.apply(TieredChestBlock.opennessCombiner(blockEntity)).get(partialTicks);
    }

    @Override
    public void submit(TieredChestRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(modelTransformation(state.facing));

        float open = state.open;
        open = 1.0F - open;
        open = 1.0F - open * open * open;

        BlockEntityRenderState baseState = (BlockEntityRenderState) state;
        ChestModel model = this.models.select(state.type);

        String prefix = "";
        if (state.type == ChestType.LEFT)
            prefix = "left_";
        else if (state.type == ChestType.RIGHT)
            prefix = "right_";

        boolean overrideOn = TieredChests.CONFIG.texturePackOverride.get();
        Identifier woodId;
        if (overrideOn) {
            String suffix = state.type == ChestType.LEFT ? "left" : state.type == ChestType.RIGHT ? "right" : "";
            woodId = Identifier.fromNamespaceAndPath("minecraft",
                    "entity/chest/normal" + (suffix.isEmpty() ? "" : "_" + suffix));
        } else if (TieredChests.CONFIG.fancyChests.get()) {
            woodId = state.type == ChestType.SINGLE ? ChestTextureManager.getSingleWoodTexture()
                    : ChestTextureManager.getFancyDoubleTexture(state.type);
        } else {
            String woodType = state.type == ChestType.SINGLE ? "wood" : "oak";
            woodId = TieredChests.id("entity/chest/" + prefix + woodType);
        }
        SpriteId baseSprite = new SpriteId(Sheets.CHEST_SHEET, woodId);
        submitNodeCollector.submitModel(model, open, poseStack, baseState.lightCoords, OverlayTexture.NO_OVERLAY, -1,
                baseSprite, this.sprites, 0, baseState.breakProgress);

        // Pass 2: Metal Tier Overlay
        String tierName = state.tier.getSerializedName();
        SpriteId tierSprite = new SpriteId(Sheets.CHEST_SHEET,
                TieredChests.id("entity/chest/" + prefix + tierName + "_tier"));
        // Always use the standard 64x64 model for the tier overlay as it's still split
        ChestModel tierModel = overrideOn ? this.modelsNoLock.select(state.type) : this.models.select(state.type);

        submitNodeCollector.submitModel(tierModel, open, poseStack, baseState.lightCoords, OverlayTexture.NO_OVERLAY,
                -1, tierSprite, this.sprites, 0, baseState.breakProgress);

        poseStack.popPose();
    }

    public static Transformation modelTransformation(Direction facing) {
        return TRANSFORMATIONS.get(facing);
    }

    private static Transformation createModelTransformation(Direction facing) {
        return new Transformation(
                new Matrix4f().rotationAround(Axis.YP.rotationDegrees(-facing.toYRot()), 0.5F, 0.0F, 0.5F));
    }
}
