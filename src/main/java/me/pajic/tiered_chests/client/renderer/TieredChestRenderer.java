package me.pajic.tiered_chests.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.block.entity.TieredChestBlockEntity;
import me.pajic.tiered_chests.block.TieredChestBlock;
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
    private static final Map<Direction, Transformation> TRANSFORMATIONS = Util.makeEnumMap(Direction.class, TieredChestRenderer::createModelTransformation);
    public static final MultiblockChestResources<ModelLayerLocation> LAYERS = new MultiblockChestResources<>(
            ModelLayers.CHEST, ModelLayers.DOUBLE_CHEST_LEFT, ModelLayers.DOUBLE_CHEST_RIGHT
    );
    private final SpriteGetter sprites;
    private final MultiblockChestResources<ChestModel> models;

    public TieredChestRenderer(BlockEntityRendererProvider.Context context) {
        this.sprites = context.sprites();
        this.models = LAYERS.map(layer -> new ChestModel(context.bakeLayer(layer)));
    }

    @Override
    public TieredChestRenderState createRenderState() {
        return new TieredChestRenderState();
    }

    @Override
    public void extractRenderState(TieredChestBlockEntity blockEntity, TieredChestRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        BlockState blockState = blockEntity.getBlockState();
        state.facing = blockState.getValue(TieredChestBlock.FACING);
        state.type = blockState.getValue(TieredChestBlock.TYPE);
        state.tier = blockEntity.getTier();
        DoubleBlockCombiner.NeighborCombineResult<? extends TieredChestBlockEntity> combineResult = ((TieredChestBlock)blockState.getBlock()).combine(blockState, blockEntity.getLevel(), blockEntity.getBlockPos(), true);
        state.open = combineResult.apply(TieredChestBlock.opennessCombiner(blockEntity)).get(partialTicks);
    }

    @Override
    public void submit(TieredChestRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(modelTransformation(state.facing));
        
        float open = state.open;
        open = 1.0F - open;
        open = 1.0F - open * open * open;
        
        BlockEntityRenderState baseState = (BlockEntityRenderState)state;
        ChestModel model = this.models.select(state.type);
        
        String prefix = "";
        if (state.type == ChestType.LEFT) prefix = "left_";
        else if (state.type == ChestType.RIGHT) prefix = "right_";

        // Pass 1: Base Wood
        String woodType = state.type == ChestType.SINGLE ? "wood" : "oak";
        SpriteId baseSprite = new SpriteId(Sheets.CHEST_SHEET, TieredChests.id("entity/chest/" + prefix + woodType));
        submitNodeCollector.submitModel(model, open, poseStack, baseState.lightCoords, OverlayTexture.NO_OVERLAY, -1, baseSprite, this.sprites, 0, baseState.breakProgress);
        
        // Pass 2: Metal Tier Overlay (also contains the lock for wood)
        String tierName = state.tier.getSerializedName();
        SpriteId tierSprite = new SpriteId(Sheets.CHEST_SHEET, TieredChests.id("entity/chest/" + prefix + tierName + "_tier"));
        submitNodeCollector.submitModel(model, open, poseStack, baseState.lightCoords, OverlayTexture.NO_OVERLAY, -1, tierSprite, this.sprites, 0, baseState.breakProgress);
        
        poseStack.popPose();
    }

    public static Transformation modelTransformation(Direction facing) {
        return TRANSFORMATIONS.get(facing);
    }

    private static Transformation createModelTransformation(Direction facing) {
        return new Transformation(new Matrix4f().rotationAround(Axis.YP.rotationDegrees(-facing.toYRot()), 0.5F, 0.0F, 0.5F));
    }
}
