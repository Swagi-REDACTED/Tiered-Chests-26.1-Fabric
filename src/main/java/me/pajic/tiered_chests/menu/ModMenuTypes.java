package me.pajic.tiered_chests.menu;

import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.network.ModNetworking;
import me.pajic.tiered_chests.ui.TieredChestMenu;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModMenuTypes {

    public static final ExtendedMenuType<TieredChestMenu, ModNetworking.S2CTieredChestPayload> TIERED_CHEST_MENU = 
        new ExtendedMenuType<>(TieredChestMenu::new, ModNetworking.S2CTieredChestPayload.CODEC);

    public static void init() {
        Registry.register(BuiltInRegistries.MENU, TieredChests.id("tiered_chest_menu"), TIERED_CHEST_MENU);
    }
}
