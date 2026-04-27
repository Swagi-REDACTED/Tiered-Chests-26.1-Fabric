package me.pajic.tiered_chests.mixin.compat.nemosinventorysorting;

import me.pajic.tiered_chests.ui.TieredChestMenu;
import me.pajic.tiered_chests.util.NemosCompatibilityHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class NemosInventorySortingMixin {

    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected int inventoryLabelY;
    @Shadow protected int imageWidth;

    @Inject(method = "init", at = @At("TAIL"))
    private void tiered_chests$initNemoButtons(CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        if (screen.getMenu() instanceof TieredChestMenu) {
            NemosCompatibilityHelper.initButtons(screen, leftPos, topPos, inventoryLabelY, imageWidth);
        }
    }
}
