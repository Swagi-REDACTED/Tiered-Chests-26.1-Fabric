package me.pajic.tiered_chests.item;

import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.block.ModBlocks;
import me.pajic.tiered_chests.util.ChestTier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.EnumMap;
import java.util.Map;

public class ModItems {

    public static final Map<ChestTier, BlockItem> TIERED_CHESTS = new EnumMap<>(ChestTier.class);
    public static final Map<ChestTier, BlockItem> TIERED_BARRELS = new EnumMap<>(ChestTier.class);

    public static void init() {
        for (ChestTier tier : ChestTier.values()) {
            Identifier chestId = TieredChests.id(tier.getSerializedName() + "_chest");
            ResourceKey<Item> chestKey = ResourceKey.create(Registries.ITEM, chestId);
            BlockItem chestItem = new BlockItem(ModBlocks.TIERED_CHESTS.get(tier), new Item.Properties().setId(chestKey));
            TIERED_CHESTS.put(tier, chestItem);
            Registry.register(BuiltInRegistries.ITEM, chestKey, chestItem);

            Identifier barrelId = TieredChests.id(tier.getSerializedName() + "_barrel");
            ResourceKey<Item> barrelKey = ResourceKey.create(Registries.ITEM, barrelId);
            BlockItem barrelItem = new BlockItem(ModBlocks.TIERED_BARRELS.get(tier), new Item.Properties().setId(barrelKey));
            TIERED_BARRELS.put(tier, barrelItem);
            Registry.register(BuiltInRegistries.ITEM, barrelKey, barrelItem);
        }
    }
}
