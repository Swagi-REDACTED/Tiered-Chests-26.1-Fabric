package me.pajic.tiered_chests.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.client.ChestTextureManager;
import me.pajic.tiered_chests.util.ChestTier;
import me.pajic.tiered_chests.config.ModConfig;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class TieredChestSpecialRenderer implements NoDataSpecialModelRenderer {
    private final SpriteGetter sprites;
    private final ChestModel model;
    private final ChestTier tier;

    public TieredChestSpecialRenderer(SpecialModelRenderer.BakingContext context, ChestTier tier) {
        this.sprites = context.sprites();
        this.model = new ChestModel(context.entityModelSet().bakeLayer(ModelLayers.CHEST));
        this.tier = tier;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        float openness = 0.0f; // Static for item

        // Pass 1: Base Wood
        Identifier woodId = TieredChests.CONFIG.fancyChests.get() ? ChestTextureManager.getSingleWoodTexture() : TieredChests.id("entity/chest/wood");
        SpriteId baseSprite = new SpriteId(Sheets.CHEST_SHEET, woodId);
        submitNodeCollector.submitModel(model, openness, poseStack, lightCoords, overlayCoords, -1, baseSprite, this.sprites, outlineColor, null);
 
        // Pass 2: Metal Tier Overlay
        SpriteId tierSprite = new SpriteId(Sheets.CHEST_SHEET, TieredChests.id("entity/chest/" + tier.getSerializedName() + "_tier"));
        submitNodeCollector.submitModel(model, openness, poseStack, lightCoords, overlayCoords, -1, tierSprite, this.sprites, outlineColor, null);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        this.model.setupAnim(0.0f);
        this.model.root().getExtentsForGui(poseStack, output);
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
        public NoDataSpecialModelRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new TieredChestSpecialRenderer(context, tier);
        }
    }
}
