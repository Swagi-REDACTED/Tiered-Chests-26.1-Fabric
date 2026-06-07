package me.pajic.tiered_chests.mixin.client;

import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.ui.ScaleState;

import me.pajic.tiered_chests.ui.TieredChestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void tieredchests$onSetScreen(Screen screen, CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        Screen current = client.screen;
        
        boolean wasTiered = current instanceof TieredChestScreen;
        boolean isTiered = screen instanceof TieredChestScreen;
        
        if (wasTiered != isTiered && client.getWindow() != null && TieredChests.CLIENT_CONFIG.autoGuiRescaling.get()) {
            if (isTiered) {
                int guiScale = client.options.guiScale().get();
                boolean forceUnicode = client.options.forceUnicodeFont().get();
                int newScale = client.getWindow().calculateScale(guiScale, forceUnicode);
                
                if (newScale > 2) {
                    ScaleState.savedGuiScale = client.getWindow().getGuiScale();
                    client.getWindow().setGuiScale(2);
                }
            } else {
                if (ScaleState.savedGuiScale != -1) {
                    ScaleState.isRestoring = true;
                    client.getWindow().setGuiScale(ScaleState.savedGuiScale);
                    ScaleState.isRestoring = false;
                    ScaleState.savedGuiScale = -1;
                }
            }
        }
    }
}
