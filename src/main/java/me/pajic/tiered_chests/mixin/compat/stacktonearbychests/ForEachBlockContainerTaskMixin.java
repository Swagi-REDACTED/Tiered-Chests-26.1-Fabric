package me.pajic.tiered_chests.mixin.compat.stacktonearbychests;

import me.pajic.tiered_chests.block.TieredChestBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Pseudo
@Mixin(targets = "io.github.xiaocihua.stacktonearbychests.ForEachBlockContainerTask")
public abstract class ForEachBlockContainerTaskMixin {

    @Inject(method = "getTheOtherHalfOfLargeChest(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Ljava/util/Optional;", 
            at = @At("HEAD"), cancellable = true, remap = true)
    private void onGetTheOtherHalfOfLargeChest(Level world, BlockPos pos, CallbackInfoReturnable<Optional<BlockPos>> cir) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof TieredChestBlock && state.getValue(TieredChestBlock.TYPE) != ChestType.SINGLE) {
            BlockPos offsetPos = pos.relative(TieredChestBlock.getConnectedDirection(state));
            BlockState theOtherHalf = world.getBlockState(offsetPos);
            if (theOtherHalf.getBlock() == state.getBlock()
                    && state.getValue(TieredChestBlock.FACING) == theOtherHalf.getValue(TieredChestBlock.FACING)
                    && TieredChestBlock.getConnectedDirection(state) == TieredChestBlock.getConnectedDirection(theOtherHalf).getOpposite()) {
                cir.setReturnValue(Optional.of(offsetPos));
            }
        }
    }

    @Inject(method = "canOpen(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z", 
            at = @At("HEAD"), cancellable = true, remap = true)
    private void onCanOpen(Level world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof TieredChestBlock) {
            if (TieredChestBlock.isChestBlockedAt(world, pos)) {
                cir.setReturnValue(false);
            } else {
                // Check other half
                if (state.getValue(TieredChestBlock.TYPE) != ChestType.SINGLE) {
                    BlockPos offsetPos = pos.relative(TieredChestBlock.getConnectedDirection(state));
                    BlockState theOtherHalf = world.getBlockState(offsetPos);
                    if (theOtherHalf.getBlock() == state.getBlock()
                            && state.getValue(TieredChestBlock.FACING) == theOtherHalf.getValue(TieredChestBlock.FACING)
                            && TieredChestBlock.getConnectedDirection(state) == TieredChestBlock.getConnectedDirection(theOtherHalf).getOpposite()) {
                        if (TieredChestBlock.isChestBlockedAt(world, offsetPos)) {
                            cir.setReturnValue(false);
                            return;
                        }
                    }
                }
                cir.setReturnValue(true);
            }
        }
    }
}
