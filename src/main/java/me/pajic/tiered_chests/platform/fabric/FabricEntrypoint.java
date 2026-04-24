package me.pajic.tiered_chests.platform.fabric;

import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.block.ModBlocks;
import me.pajic.tiered_chests.block.entity.ModBlockEntities;
import me.pajic.tiered_chests.item.ModItems;
import me.pajic.tiered_chests.menu.ModMenuTypes;
import me.pajic.tiered_chests.util.ChestTier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

public class FabricEntrypoint implements ModInitializer {
    public static final ResourceKey<CreativeModeTab> TIERED_CHESTS_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, TieredChests.id("tiered_chests_tab"));
    public static final CreativeModeTab TIERED_CHESTS_TAB = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 7)
            .title(Component.translatable("itemGroup.tiered_chests"))
            .icon(() -> new ItemStack(ModItems.TIERED_CHESTS.get(ChestTier.IRON)))
            .displayItems((parameters, output) -> {
                for (ChestTier tier : ChestTier.values()) {
                    output.accept(ModItems.TIERED_CHESTS.get(tier));
                    output.accept(ModItems.TIERED_BARRELS.get(tier));
                }
            })
            .build();

    @Override
    public void onInitialize() {
        try {
            TieredChests.LOGGER.info("Initializing Tiered Chests...");
            
            TieredChests.onInitialize();
            ModBlocks.init();
            ModBlockEntities.init();
            ModItems.init();
            ModMenuTypes.init();

            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TIERED_CHESTS_TAB_KEY, TIERED_CHESTS_TAB);
            initCreativeTabs();
            
            TieredChests.LOGGER.info("Tiered Chests initialized successfully.");
        } catch (Exception e) {
            TieredChests.LOGGER.error("Failed to initialize Tiered Chests", e);
            throw e;
        }
    }

    private void initCreativeTabs() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            for (ChestTier tier : ChestTier.values()) {
                entries.accept(ModItems.TIERED_CHESTS.get(tier));
                entries.accept(ModItems.TIERED_BARRELS.get(tier));
            }
        });
    }
}
