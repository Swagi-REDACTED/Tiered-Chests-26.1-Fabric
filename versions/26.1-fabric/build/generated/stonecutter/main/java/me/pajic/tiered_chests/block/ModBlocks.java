package me.pajic.tiered_chests.block;

import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.util.ChestTier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.EnumMap;
import java.util.Map;

public class ModBlocks {

    public static final Map<ChestTier, TieredChestBlock> TIERED_CHESTS = new EnumMap<>(ChestTier.class);
    public static final Map<ChestTier, TieredBarrelBlock> TIERED_BARRELS = new EnumMap<>(ChestTier.class);
    public static final Map<ChestTier, ShulkerInfusedTieredChestBlock> SHULKER_INFUSED_TIERED_CHESTS = new EnumMap<>(ChestTier.class);
    public static final Map<ChestTier, ShulkerInfusedTieredBarrelBlock> SHULKER_INFUSED_TIERED_BARRELS = new EnumMap<>(ChestTier.class);

    public static void init() {
        for (ChestTier tier : ChestTier.values()) {
            Identifier chestId = TieredChests.id(tier.getSerializedName() + "_chest");
            ResourceKey<Block> chestKey = ResourceKey.create(Registries.BLOCK, chestId);
            float hardness = switch (tier) {
                case WOOD -> 2.5F;
                case COPPER -> 3.5F;
                case IRON -> 4.5F;
                case GOLDEN -> 5.25F;
                case DIAMOND -> 6.25F;
                case NETHERITE -> 7.5F;
            };

            TieredChestBlock chest = new TieredChestBlock(tier, BlockBehaviour.Properties.of()
                .setId(chestKey)
                .mapColor(MapColor.WOOD)
                .strength(hardness)
                .noOcclusion());
            TIERED_CHESTS.put(tier, chest);
            Registry.register(BuiltInRegistries.BLOCK, chestKey, chest);

            Identifier shulkerChestId = TieredChests.id("shulker_infused_" + tier.getSerializedName() + "_chest");
            ResourceKey<Block> shulkerChestKey = ResourceKey.create(Registries.BLOCK, shulkerChestId);
            ShulkerInfusedTieredChestBlock shulkerChest = new ShulkerInfusedTieredChestBlock(tier, BlockBehaviour.Properties.of()
                .setId(shulkerChestKey)
                .mapColor(MapColor.WOOD)
                .strength(hardness)
                .noOcclusion());
            SHULKER_INFUSED_TIERED_CHESTS.put(tier, shulkerChest);
            Registry.register(BuiltInRegistries.BLOCK, shulkerChestKey, shulkerChest);

            Identifier barrelId = TieredChests.id(tier.getSerializedName() + "_barrel");
            ResourceKey<Block> barrelKey = ResourceKey.create(Registries.BLOCK, barrelId);
            TieredBarrelBlock barrel = new TieredBarrelBlock(tier, BlockBehaviour.Properties.of()
                .setId(barrelKey)
                .mapColor(MapColor.WOOD)
                .strength(hardness)
                .noOcclusion());
            TIERED_BARRELS.put(tier, barrel);
            Registry.register(BuiltInRegistries.BLOCK, barrelKey, barrel);

            Identifier shulkerBarrelId = TieredChests.id("shulker_infused_" + tier.getSerializedName() + "_barrel");
            ResourceKey<Block> shulkerBarrelKey = ResourceKey.create(Registries.BLOCK, shulkerBarrelId);
            ShulkerInfusedTieredBarrelBlock shulkerBarrel = new ShulkerInfusedTieredBarrelBlock(tier, BlockBehaviour.Properties.of()
                .setId(shulkerBarrelKey)
                .mapColor(MapColor.WOOD)
                .strength(hardness)
                .noOcclusion());
            SHULKER_INFUSED_TIERED_BARRELS.put(tier, shulkerBarrel);
            Registry.register(BuiltInRegistries.BLOCK, shulkerBarrelKey, shulkerBarrel);
        }
    }
}
