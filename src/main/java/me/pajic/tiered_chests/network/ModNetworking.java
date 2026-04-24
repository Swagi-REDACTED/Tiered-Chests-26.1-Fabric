package me.pajic.tiered_chests.network;

import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.util.ChestTier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class ModNetworking {

    public static final Identifier TIERED_CHEST_SCREEN = TieredChests.id("tiered_chest_screen");

    public record S2CTieredChestPayload(BlockPos pos, ChestTier tier, boolean isDouble) implements CustomPacketPayload {
        public static final Type<S2CTieredChestPayload> TYPE = new Type<>(TIERED_CHEST_SCREEN);
        
        public static final StreamCodec<FriendlyByteBuf, S2CTieredChestPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, S2CTieredChestPayload::pos,
            ChestTier.STREAM_CODEC.cast(), S2CTieredChestPayload::tier,
            ByteBufCodecs.BOOL, S2CTieredChestPayload::isDouble,
            S2CTieredChestPayload::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void init() {}
}
