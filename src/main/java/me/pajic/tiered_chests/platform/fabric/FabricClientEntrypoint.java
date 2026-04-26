package me.pajic.tiered_chests.platform.fabric;

import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.menu.ModMenuTypes;
import me.pajic.tiered_chests.ui.TieredChestScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import me.pajic.tiered_chests.network.ModNetworking;
import me.pajic.tiered_chests.config.ModConfig;
import me.pajic.tiered_chests.util.ChestTier;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.world.level.block.Block;

import java.util.List;
import me.pajic.tiered_chests.block.entity.ModBlockEntities;
import me.pajic.tiered_chests.client.renderer.TieredChestRenderer;
import me.pajic.tiered_chests.client.renderer.TieredChestSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import me.pajic.tiered_chests.block.ModBlocks;

public class FabricClientEntrypoint implements ClientModInitializer {

    public record ParticleResult(int color, boolean wash) {
    }

    public static ParticleResult getTierParticleColor(ChestTier tier, boolean isFancy) {
        if (TieredChests.CONFIG.texturePackOverride.get() && tier == ChestTier.WOOD) {
            return new ParticleResult(-1, false);
        }

        if (tier == ChestTier.NETHERITE) {
            return new ParticleResult(0x5E565A, false); // Dark, slightly warm gray
        }

        if (tier == ChestTier.DIAMOND) {
            return new ParticleResult(0x7BFFFF, true); // Your original pale cyan + WASH
        }

        if (tier == ChestTier.GOLDEN) {
            return new ParticleResult(0xE3C65A, true); // Soft gold
        }

        if (tier == ChestTier.IRON) {
            return new ParticleResult(0xC6C6C6, true); // Light neutral gray
        }

        if (tier == ChestTier.COPPER) {
            return new ParticleResult(0xC47A4A, false); // Warm copper
        }

        // Wood/Generic Tints
        if (isFancy) {
            return new ParticleResult(0xE3B581, false); // Subtle Fancy Wood
        } else {
            return new ParticleResult(0xE3B581, true); // Subtle Standard Wood - WASH = TRUE
        }
    }

    private static final java.util.Map<Block, ChestTier> BLOCK_TO_TIER = new java.util.HashMap<>();

    public static ChestTier getTier(Block block) {
        if (BLOCK_TO_TIER.isEmpty()) {
            ModBlocks.TIERED_CHESTS.forEach((tier, b) -> BLOCK_TO_TIER.put(b, tier));
            ModBlocks.TIERED_BARRELS.forEach((tier, b) -> BLOCK_TO_TIER.put(b, tier));
        }
        return BLOCK_TO_TIER.get(block);
    }

    public static boolean shouldWash(Block block) {
        ChestTier tier = getTier(block);
        if (tier == null)
            return false;

        boolean isFancy = TieredChests.CONFIG.fancyChests.get() || TieredChests.CONFIG.fancyBarrels.get();
        if (tier == ChestTier.WOOD && block instanceof me.pajic.tiered_chests.block.TieredBarrelBlock) {
            isFancy = TieredChests.CONFIG.fancyBarrels.get();
        }

        return getTierParticleColor(tier, isFancy).wash();
    }

    @Override
    public void onInitializeClient() {
        try {
            TieredChests.LOGGER.info("Initializing Tiered Chests Client...");
            MenuScreens.register(ModMenuTypes.TIERED_CHEST_MENU, TieredChestScreen::new);

            ModBlockEntities.TIERED_CHESTS.values()
                    .forEach(type -> BlockEntityRenderers.register(type, TieredChestRenderer::new));
            ModBlockEntities.TIERED_BARRELS.values()
                    .forEach(type -> BlockEntityRenderers.register(type,
                            me.pajic.tiered_chests.client.renderer.TieredBarrelRenderer::new));

            SpecialModelRenderers.ID_MAPPER.put(TieredChests.id("tiered_chest"),
                    TieredChestSpecialRenderer.Unbaked.MAP_CODEC);
            SpecialModelRenderers.ID_MAPPER.put(TieredChests.id("tiered_barrel"),
                    me.pajic.tiered_chests.client.renderer.TieredBarrelSpecialRenderer.Unbaked.MAP_CODEC);

            // Particle Tinting Registration
            for (ChestTier tier : ChestTier.values()) {
                Block chest = ModBlocks.TIERED_CHESTS.get(tier);
                Block barrel = ModBlocks.TIERED_BARRELS.get(tier);

                BlockColorRegistry.register(List.of(state -> {
                    boolean isFancy = TieredChests.CONFIG.fancyChests.get() || TieredChests.CONFIG.fancyBarrels.get();
                    if (tier == ChestTier.WOOD
                            && state.getBlock() instanceof me.pajic.tiered_chests.block.TieredBarrelBlock) {
                        isFancy = TieredChests.CONFIG.fancyBarrels.get();
                    }
                    return getTierParticleColor(tier, isFancy).color();
                }), chest, barrel);
            }

            net.fabricmc.fabric.api.resource.ResourceManagerHelper.get(net.minecraft.server.packs.PackType.CLIENT_RESOURCES).registerReloadListener(new net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener() {
                @Override
                public net.minecraft.resources.Identifier getFabricId() {
                    return TieredChests.id("resource_reload_listener");
                }

                @Override
                public void onResourceManagerReload(net.minecraft.server.packs.resources.ResourceManager manager) {
                    me.pajic.tiered_chests.util.GuiColorMatcher.reset();
                }
            });

            tryInjectStackToNearbyChests();

            TieredChests.LOGGER.info("Tiered Chests Client initialized successfully.");
        } catch (Exception e) {
            TieredChests.LOGGER.error("Failed to initialize Tiered Chests Client", e);
            throw e;
        }
    }

    private void tryInjectStackToNearbyChests() {
        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("stack-to-nearby-chests")) {
            try {
                Class<?> modOptionsClass = Class.forName("io.github.xiaocihua.stacktonearbychests.ModOptions");
                Object options = modOptionsClass.getMethod("get").invoke(null);
                Object behavior = modOptionsClass.getField("behavior").get(options);

                java.lang.reflect.Field stackingTargetsField = behavior.getClass().getField("stackingTargets");
                java.util.Set<String> stackingTargets = (java.util.Set<String>) stackingTargetsField.get(behavior);
                java.util.Set<String> newStackingTargets = new java.util.HashSet<>(stackingTargets);

                java.lang.reflect.Field restockingSourcesField = behavior.getClass().getField("restockingSources");
                java.util.Set<String> restockingSources = (java.util.Set<String>) restockingSourcesField.get(behavior);
                java.util.Set<String> newRestockingSources = new java.util.HashSet<>(restockingSources);

                boolean changed = false;
                for (ChestTier tier : ChestTier.values()) {
                    String name = tier.getSerializedName();
                    if (newStackingTargets.add("tiered_chests:" + name + "_chest")) changed = true;
                    if (newStackingTargets.add("tiered_chests:" + name + "_barrel")) changed = true;
                    if (newRestockingSources.add("tiered_chests:" + name + "_chest")) changed = true;
                    if (newRestockingSources.add("tiered_chests:" + name + "_barrel")) changed = true;
                }

                if (changed) {
                    stackingTargetsField.set(behavior, newStackingTargets);
                    restockingSourcesField.set(behavior, newRestockingSources);
                    modOptionsClass.getMethod("write").invoke(options);
                    TieredChests.LOGGER.info("Successfully injected Tiered Chests into Stack To Nearby Chests config.");
                }
            } catch (Exception e) {
                TieredChests.LOGGER.error("Failed to inject into Stack To Nearby Chests", e);
            }
        }
    }
}
