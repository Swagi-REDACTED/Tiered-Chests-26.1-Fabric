package me.pajic.tiered_chests.ui;

import me.pajic.tiered_chests.TieredChests;
import me.pajic.tiered_chests.util.ChestTier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class TieredChestScreen extends AbstractContainerScreen<TieredChestMenu> {

    private static final Identifier GUI_TEXTURE = TieredChests.id("textures/gui/generic_54_blank.png");
    private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");

    // Easy configuration constants
    private static final int RIGHT_TRIM = 2; // Reduced from 5 since it was too thin
    private static final int BOTTOM_TRIM = 2; // Changed to TRIM to shrink the bottom thickness
    private static final int EDGE_SIZE = 7;

    private final int rows;

    public TieredChestScreen(TieredChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, menu.getImageWidth() - RIGHT_TRIM, menu.getImageHeight() - BOTTOM_TRIM);
        ChestTier tier = menu.getTier();
        this.rows = menu.getRows();

        this.titleLabelY = 6;
        this.titleLabelX = 8;
        // Anchor label to the menu's height so it doesn't shift when we expand the
        // screen height
        this.inventoryLabelY = menu.getImageHeight() - 94;
        this.inventoryLabelX = 8;
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        int edge = EDGE_SIZE;
        int centerWidth = imageWidth - (edge * 2);

        // Dynamically calculate texture coordinates so changing 'edge' doesn't break
        // the right side!
        int texWidth = 176;
        int texCenterWidth = texWidth - (edge * 2);
        int rightU = texWidth - edge;

        // Top section
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, edge, 17, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + edge, y, edge, 0, centerWidth, 17, texCenterWidth,
                17, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + imageWidth - edge, y, rightU, 0, edge, 17, 256,
                256);

        // Chest/barrel rows
        for (int i = 0; i < rows; i++) {
            int rowY = y + 17 + i * 18;

            graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, rowY, 0, 17, edge, 18, 256, 256);
            graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + edge, rowY, edge, 17, centerWidth, 18,
                    texCenterWidth, 18, 256, 256);
            graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + imageWidth - edge, rowY, rightU, 17, edge, 18,
                    256, 256);
        }

        // Bottom section (Dynamic height)
        int footerY = y + 17 + rows * 18;
        int footerTotalHeight = imageHeight - (17 + rows * 18);
        int bottomBorderHeight = 7;
        int footerBodyHeight = footerTotalHeight - bottomBorderHeight;

        // Draw the inventory background area
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, footerY, 0, 126, edge, footerBodyHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + edge, footerY, edge, 126, centerWidth,
                footerBodyHeight, texCenterWidth, footerBodyHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + imageWidth - edge, footerY, rightU, 126, edge,
                footerBodyHeight, 256, 256);

        // Draw the bottom border (always sourced from v=215 where the true border is)
        int borderY = footerY + footerBodyHeight;
        int borderV = 215;

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, borderY, 0, borderV, edge, bottomBorderHeight, 256,
                256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + edge, borderY, edge, borderV, centerWidth,
                bottomBorderHeight, texCenterWidth, bottomBorderHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x + imageWidth - edge, borderY, rightU, borderV, edge,
                bottomBorderHeight, 256, 256);

        // Slot overlays
        for (Slot slot : getMenu().slots) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, x + slot.x - 1, y + slot.y - 1, 18, 18);
        }
    }
}
