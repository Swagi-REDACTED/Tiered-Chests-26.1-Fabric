package me.pajic.tiered_chests.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.client.ChestTextureManager;
import me.pajic.tiered_chests.util.ChestTier;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Implements SpecialModelRenderer<Integer> so that
 * extractArgument() can return the current config state. This value is appended
 * to the
 * ItemStackRenderState model identity, busting the inventory render cache
 * whenever the
 * config value changes — ensuring instant live updates in the inventory.
 */
public class TieredChestSpecialRenderer implements SpecialModelRenderer<Integer> {
    private final SpriteGetter sprites;
    private final ChestModel model;
    private final ChestModel modelNoLock;
    private final ChestTier tier;

    public TieredChestSpecialRenderer(SpecialModelRenderer.BakingContext context, ChestTier tier) {
        this.sprites = context.sprites();
        this.model = new ChestModel(context.entityModelSet().bakeLayer(ModelLayers.CHEST));
        this.modelNoLock = new ChestModel(context.entityModelSet().bakeLayer(ModelLayers.CHEST));
        try {
            this.modelNoLock.root().getChild("lock").visible = false;
        } catch (Exception e) {
        }
        this.tier = tier;
    }

    /**
     * Return the config state as the argument. Minecraft appends this to the
     * ItemStackRenderState identity so the cache is busted whenever the value
     * changes.
     */
    @Override
    public @Nullable Integer extractArgument(ItemStack stack) {
        // Encode booleans into a single unique integer state
        int state = 0;
        if (TieredChests.CONFIG.texturePackOverride.get())
            state |= 1;
        if (TieredChests.CONFIG.fancyChests.get())
            state |= 2;
        if (TieredChests.CONFIG.fancyLocks.get())
            state |= 4;
        return state;
    }

    @Override
    public void submit(
            @Nullable Integer argument,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            int overlayCoords,
            boolean hasFoil,
            int outlineColor) {
        float openness = 0.0f; // Static for item

        boolean overrideOn = TieredChests.CONFIG.texturePackOverride.get();
        boolean useFancyLock = TieredChests.CONFIG.fancyLocks.get() && !overrideOn;

        // Pass 1: Base Wood
        Identifier woodId;
        if (overrideOn) {
            woodId = Identifier.fromNamespaceAndPath("minecraft", "entity/chest/normal");
        } else if (TieredChests.CONFIG.fancyChests.get()) {
            woodId = ChestTextureManager.getSingleWoodTexture();
        } else {
            woodId = TieredChests.id("entity/chest/wood");
        }
        SpriteId baseSprite = new SpriteId(Sheets.CHEST_SHEET, woodId);
        submitNodeCollector.submitModel(model, openness, poseStack, lightCoords, overlayCoords, -1, baseSprite,
                this.sprites, outlineColor, null);

        // Pass 2: Metal Tier Overlay
        SpriteId tierSprite = new SpriteId(Sheets.CHEST_SHEET,
                TieredChests.id("entity/chest/" + tier.getSerializedName() + "_tier"));
        ChestModel tierModel = (overrideOn || useFancyLock) ? modelNoLock : model;
        submitNodeCollector.submitModel(tierModel, openness, poseStack, lightCoords, overlayCoords, -1, tierSprite,
                this.sprites, outlineColor, null);

        // Pass 3: Fancy Lock Overlay
        if (useFancyLock) {
            SpriteId lockSprite = new SpriteId(Sheets.CHEST_SHEET,
                    ChestTextureManager.getFancyLockTexture(ChestType.SINGLE));
            submitNodeCollector.submitModel(model, openness, poseStack, lightCoords, overlayCoords, -1, lockSprite,
                    this.sprites, outlineColor, null);
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        this.model.setupAnim(0.0f);
        this.model.root().getExtentsForGui(poseStack, output);
    }

    public record Unbaked(ChestTier tier) implements SpecialModelRenderer.Unbaked<Integer> {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ChestTier.CODEC.fieldOf("tier").forGetter(Unbaked::tier)).apply(inst, Unbaked::new));

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<Integer> bake(SpecialModelRenderer.BakingContext context) {
            return new TieredChestSpecialRenderer(context, tier);
        }
    }
}
