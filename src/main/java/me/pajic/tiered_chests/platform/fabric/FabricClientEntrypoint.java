package me.pajic.tiered_chests.platform.fabric;

import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.menu.ModMenuTypes;
import me.pajic.tiered_chests.ui.TieredChestScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

import me.pajic.tiered_chests.block.entity.ModBlockEntities;
import me.pajic.tiered_chests.client.renderer.TieredChestRenderer;
import me.pajic.tiered_chests.client.renderer.TieredChestSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import me.pajic.tiered_chests.block.ModBlocks;

public class FabricClientEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        try {
            TieredChests.LOGGER.info("Initializing Tiered Chests Client...");
            MenuScreens.register(ModMenuTypes.TIERED_CHEST_MENU, TieredChestScreen::new);

            ModBlockEntities.TIERED_CHESTS.values().forEach(type ->
                BlockEntityRenderers.register(type, TieredChestRenderer::new)
            );


            SpecialModelRenderers.ID_MAPPER.put(TieredChests.id("tiered_chest"), TieredChestSpecialRenderer.Unbaked.MAP_CODEC);
            SpecialModelRenderers.ID_MAPPER.put(TieredChests.id("tiered_barrel"), me.pajic.tiered_chests.client.renderer.TieredBarrelSpecialRenderer.Unbaked.MAP_CODEC);

            TieredChests.LOGGER.info("Tiered Chests Client initialized successfully.");
        } catch (Exception e) {
            TieredChests.LOGGER.error("Failed to initialize Tiered Chests Client", e);
            throw e;
        }
    }
}
