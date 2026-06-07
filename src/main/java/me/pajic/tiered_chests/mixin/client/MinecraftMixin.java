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

    private boolean tieredchests$wasTiered = false;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void tieredchests$onSetScreenHead(Screen screen, CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        this.tieredchests$wasTiered = client.screen instanceof TieredChestScreen;
    }

    @Inject(method = "setScreen", at = @At("RETURN"))
    private void tieredchests$onSetScreenReturn(Screen screen, CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        boolean isTiered = screen instanceof TieredChestScreen;
        
        if (this.tieredchests$wasTiered != isTiered && client.getWindow() != null) {
            int guiScale = client.options.guiScale().get();
            boolean forceUnicode = client.options.forceUnicodeFont().get();
            int newScale = client.getWindow().calculateScale(guiScale, forceUnicode);
            
            if (isTiered && newScale > 2) {
                newScale = 2;
            }
            
            if (newScale != client.getWindow().getGuiScale()) {
                client.getWindow().setGuiScale(newScale);
            }
        }
    }
}
