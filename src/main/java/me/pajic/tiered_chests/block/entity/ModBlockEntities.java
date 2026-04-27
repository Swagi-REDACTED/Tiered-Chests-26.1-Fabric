package me.pajic.tiered_chests.block.entity;

import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.block.ModBlocks;
import me.pajic.tiered_chests.util.ChestTier;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.EnumMap;
import java.util.Map;

public class ModBlockEntities {

    public static final Map<ChestTier, BlockEntityType<TieredChestBlockEntity>> TIERED_CHESTS = new EnumMap<>(ChestTier.class);
    public static final Map<ChestTier, BlockEntityType<TieredBarrelBlockEntity>> TIERED_BARRELS = new EnumMap<>(ChestTier.class);

    public static void init() {
        for (ChestTier tier : ChestTier.values()) {
            BlockEntityType<TieredChestBlockEntity> chestType = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                TieredChests.id(tier.getSerializedName() + "_chest"),
                FabricBlockEntityTypeBuilder.create((pos, state) -> new TieredChestBlockEntity(tier, pos, state), ModBlocks.TIERED_CHESTS.get(tier)).build()
            );
            TIERED_CHESTS.put(tier, chestType);

            BlockEntityType<TieredBarrelBlockEntity> barrelType = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                TieredChests.id(tier.getSerializedName() + "_barrel"),
                FabricBlockEntityTypeBuilder.create((pos, state) -> new TieredBarrelBlockEntity(tier, pos, state), ModBlocks.TIERED_BARRELS.get(tier)).build()
            );
            TIERED_BARRELS.put(tier, barrelType);
        }
    }
}
