package me.pajic.tiered_chests.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "<init>(Lnet/minecraft/core/Holder;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("RETURN"))
    private void tiered_chests$removeEmptyContainerComponent(CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.isEmpty() && stack.has(DataComponents.CONTAINER)) {
            ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
            if (contents != null && !contents.nonEmptyItemCopyStream().findAny().isPresent()) {
                stack.set(DataComponents.CONTAINER, null);
            }
        }
    }
}
