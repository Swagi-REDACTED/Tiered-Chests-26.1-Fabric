package me.pajic.tiered_chests.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ChestTier implements StringRepresentable {
    WOOD("wood", 27, 9),
    COPPER("copper", 45, 9),
    IRON("iron", 54, 9),
    GOLDEN("golden", 81, 9),
    DIAMOND("diamond", 108, 12),
    NETHERITE("netherite", 135, 15);

    public static final Codec<ChestTier> CODEC = StringRepresentable.fromEnum(ChestTier::values);
    public static final StreamCodec<ByteBuf, ChestTier> STREAM_CODEC = ByteBufCodecs.idMapper(
            id -> ChestTier.values()[id],
            ChestTier::ordinal
    );

    private final String name;
    private final int slotCount;
    private final int rowLength;

    ChestTier(String name, int slotCount, int rowLength) {
        this.name = name;
        this.slotCount = slotCount;
        this.rowLength = rowLength;
    }

    public int getSlotCount() {
        return slotCount;
    }

    public int getRowLength() {
        return rowLength;
    }

    public int getBaseRows() {
        return slotCount / rowLength;
    }

    public int getBaseCols() {
        return rowLength;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
