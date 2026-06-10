package me.pajic.tiered_chests.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "isSecondaryUseActive", at = @At("RETURN"), cancellable = true)
    private void tieredchests$allowSneakChestUpgrade(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            Player player = (Player) (Object) this;
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            if ((mainHand.is(Items.SHULKER_SHELL) && mainHand.getCount() >= 2) || 
                (offHand.is(Items.SHULKER_SHELL) && offHand.getCount() >= 2)) {
                cir.setReturnValue(false);
            }
        }
    }
}
