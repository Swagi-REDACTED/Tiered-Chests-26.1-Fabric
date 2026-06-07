package me.pajic.tiered_chests.mixin.client;

import me.pajic.tiered_chests.ui.TieredChestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(com.mojang.blaze3d.platform.Window.class)
public class WindowMixin {

    @Inject(method = "calculateScale", at = @At("RETURN"), cancellable = true)
    private void tieredchests$capGuiScale(int guiScale, boolean forceUnicode, CallbackInfoReturnable<Integer> cir) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof TieredChestScreen) {
            int current = cir.getReturnValue();
            if (current > 2) {
                cir.setReturnValue(2);
            }
        }
    }
}
