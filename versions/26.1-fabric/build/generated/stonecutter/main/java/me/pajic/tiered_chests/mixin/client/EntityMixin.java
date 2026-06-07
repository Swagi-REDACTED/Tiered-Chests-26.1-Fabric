package me.pajic.tiered_chests.mixin.client;

import me.pajic.tiered_chests.block.TieredBarrelBlock;
import me.pajic.tiered_chests.block.TieredChestBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Redirect(method = "spawnSprintParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape tiered_chests$allowInvisibleSprintingParticles(BlockState state) {
        RenderShape shape = state.getRenderShape();
        if (shape == RenderShape.INVISIBLE && (state.getBlock() instanceof TieredChestBlock || state.getBlock() instanceof TieredBarrelBlock)) {
            return RenderShape.MODEL; // Trick the engine into thinking it's a model so it spawns particles
        }
        return shape;
    }
}
