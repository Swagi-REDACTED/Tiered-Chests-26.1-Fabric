package me.pajic.tiered_chests.client;

import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.config.ModConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ChestTextureManager {

    public static Identifier getSingleWoodTexture() {
        return TieredChests.CONFIG.fancyChests.get() ? TieredChests.id("entity/chest/wood2") : TieredChests.id("entity/chest/wood");
    }

    public static Identifier getFancyDoubleTexture(ChestType type) {
        String side = type == ChestType.LEFT ? "left" : "right";
        return TieredChests.id("entity/chest/fancy_oak_" + side);
    }
}
