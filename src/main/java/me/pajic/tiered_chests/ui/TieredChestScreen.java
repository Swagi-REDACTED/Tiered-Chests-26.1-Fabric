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
import me.pajic.tiered_chests.util.GuiColorMatcher;
import org.jetbrains.annotations.NotNull;

public class TieredChestScreen extends AbstractContainerScreen<TieredChestMenu> {

        private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");
        private static final Identifier GENERIC_SLOT = TieredChests.id("textures/gui/generic_slot.png");

        // Easy configuration constants
        private static final int RIGHT_TRIM = 2; // Reduced from 5 since it was too thin
        private static final int BOTTOM_TRIM = 2; // Changed to TRIM to shrink the bottom thickness
        private static final int EDGE_SIZE = 7;

        private final int rows;

        public TieredChestScreen(TieredChestMenu menu, Inventory playerInventory, Component title) {
                super(menu, playerInventory, title, menu.getImageWidth() - RIGHT_TRIM,
                                menu.getImageHeight() - BOTTOM_TRIM);
                ChestTier tier = menu.getTier();
                this.rows = menu.getRows();

                this.titleLabelY = 6;
                this.titleLabelX = 8;
                // Anchor label to the menu's height so it doesn't shift when we expand the
                // screen height
                this.inventoryLabelY = 20 + menu.getRows() * 18;
                this.inventoryLabelX = 8;
        }

        @Override
        protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
                // If matching is enabled, use vanilla default color so it follows resource pack overrides (e.g. Dark Mode)
                // If matching is disabled, use a slightly offset color (0xFF404041) to bypass resource pack overrides 
                // and stay dark gray on the light background.
                int color = GuiColorMatcher.isMatchingEnabled() ? -12566464 : 0xFF404041;
                
                graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, color, false);
                graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY,
                                color, false);
        }

        @Override
        public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY,
                        float partialTick) {
                int x = (width - imageWidth) / 2;
                int y = (height - imageHeight) / 2;

                int edge = EDGE_SIZE;
                int centerWidth = imageWidth - (edge * 2);

                // Dynamically calculate texture coordinates so changing 'edge' doesn't break
                // the right side!
                int texWidth = 176;
                int texCenterWidth = texWidth - (edge * 2);
                int rightU = texWidth - edge;

                Identifier guiTexture = GuiColorMatcher.getMatchedTexture();

                // Top section
                graphics.blit(RenderPipelines.GUI_TEXTURED, guiTexture, x, y, 0.0F, 0.0F, edge, 17, 256, 256);
                graphics.blit(RenderPipelines.GUI_TEXTURED, guiTexture, x + edge, y, (float) edge, 0.0F, centerWidth,
                                17,
                                texCenterWidth,
                                17, 256, 256);
                graphics.blit(RenderPipelines.GUI_TEXTURED, guiTexture, x + imageWidth - edge, y, (float) rightU, 0.0F,
                                edge, 17,
                                256,
                                256);

                // Chest/barrel rows
                for (int i = 0; i < rows; i++) {
                        int rowY = y + 17 + i * 18;

                        graphics.blit(RenderPipelines.GUI_TEXTURED, guiTexture, x, rowY, 0.0F, 17.0F, edge, 18, 256,
                                        256);
                        graphics.blit(RenderPipelines.GUI_TEXTURED, guiTexture, x + edge, rowY, (float) edge, 17.0F,
                                        centerWidth,
                                        18,
                                        texCenterWidth, 18, 256, 256);
                        graphics.blit(RenderPipelines.GUI_TEXTURED, guiTexture, x + imageWidth - edge, rowY,
                                        (float) rightU,
                                        17.0F, edge, 18,
                                        256, 256);
                }

                // Bottom section (Dynamic height)
                int footerY = y + 17 + rows * 18;
                int footerTotalHeight = imageHeight - (17 + rows * 18);
                int bottomBorderHeight = 7;
                int footerBodyHeight = footerTotalHeight - bottomBorderHeight;

                // Draw the inventory background area
                int srcBodyHeight = Math.min(footerBodyHeight, 215 - 126);

                graphics.blit(RenderPipelines.GUI_TEXTURED, guiTexture, x, footerY, 0.0F, 126.0F, edge,
                                footerBodyHeight, 256, 256);
                graphics.blit(RenderPipelines.GUI_TEXTURED, guiTexture, x + edge, footerY, (float) edge, 126.0F,
                                centerWidth,
                                footerBodyHeight, texCenterWidth, footerBodyHeight, 256, 256);
                graphics.blit(RenderPipelines.GUI_TEXTURED, guiTexture, x + imageWidth - edge, footerY, (float) rightU,
                                126.0F,
                                edge,
                                footerBodyHeight, 256, 256);

                // Draw the bottom border (always sourced from v=215 where the true border is)
                int borderY = footerY + footerBodyHeight;
                int borderV = 215;

                graphics.blit(RenderPipelines.GUI_TEXTURED, guiTexture, x, borderY, 0.0F, (float) borderV, edge,
                                bottomBorderHeight, 256,
                                256);
                graphics.blit(RenderPipelines.GUI_TEXTURED, guiTexture, x + edge, borderY, (float) edge,
                                (float) borderV, centerWidth,
                                bottomBorderHeight, texCenterWidth, bottomBorderHeight, 256, 256);
                graphics.blit(RenderPipelines.GUI_TEXTURED, guiTexture, x + imageWidth - edge, borderY, (float) rightU,
                                (float) borderV, edge,
                                bottomBorderHeight, 256, 256);

                // Slot overlays
                for (Slot slot : getMenu().slots) {
                        if (GuiColorMatcher.isMatchingEnabled()) {
                                // Use vanilla slot sprite when matching is enabled (matches the resource pack)
                                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, x + slot.x - 1,
                                                y + slot.y - 1, 18, 18);
                        } else {
                                // Use our static light slot when matching is disabled (stays light even with dark packs)
                                graphics.blit(RenderPipelines.GUI_TEXTURED, GENERIC_SLOT, x + slot.x - 1, y + slot.y - 1,
                                                0.0F, 0.0F, 18, 18, 18, 18);
                        }
                }
        }
}
