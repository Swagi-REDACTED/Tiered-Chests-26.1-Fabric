package me.pajic.tiered_chests.mixin.client;

import me.pajic.tiered_chests.ui.TieredChestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    private static int savedGuiScale = -1;
    public static boolean isRestoring = false;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void tieredchests$onSetScreen(Screen screen, CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        Screen current = client.screen;
        
        boolean wasTiered = current instanceof TieredChestScreen;
        boolean isTiered = screen instanceof TieredChestScreen;
        
        if (wasTiered != isTiered && client.getWindow() != null) {
            if (isTiered) {
                int guiScale = client.options.guiScale().get();
                boolean forceUnicode = client.options.forceUnicodeFont().get();
                int newScale = client.getWindow().calculateScale(guiScale, forceUnicode);
                
                if (newScale > 2) {
                    savedGuiScale = client.getWindow().getGuiScale();
                    client.getWindow().setGuiScale(2);
                }
            } else {
                if (savedGuiScale != -1) {
                    isRestoring = true;
                    client.getWindow().setGuiScale(savedGuiScale);
                    isRestoring = false;
                    savedGuiScale = -1;
                }
            }
        }
    }
}
