package me.pajic.tiered_chests.ui;

import me.pajic.tiered_chests.menu.ModMenuTypes;
import me.pajic.tiered_chests.network.ModNetworking;
import me.pajic.tiered_chests.util.ChestTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TieredChestMenu extends AbstractContainerMenu {
    private static final int SLOT_SIZE = 18;
    private static final int GUI_PADDING = 8;
    private static final int PLAYER_INV_COLUMNS = 9;
    private static final int PLAYER_INV_ROWS = 3;
    private static final int PLAYER_INV_WIDTH = GUI_PADDING * 2 + PLAYER_INV_COLUMNS * SLOT_SIZE; // 176

    private final ChestTier tier;
    private final BlockPos pos;
    private final Container container;
    private final int rows;
    private final int columns;
    private final int imageWidth;
    private final int imageHeight;
    private final boolean fancyChests;

    // Client-side constructor
    public TieredChestMenu(int containerId, Inventory inventory, ModNetworking.S2CTieredChestPayload payload) {
        this(containerId, inventory, new SimpleContainer(payload.rows() * payload.cols()), payload.tier(), payload.pos(), payload.rows(), payload.cols(), payload.fancyChests());
    }

    // Server-side constructor (legacy)
    public TieredChestMenu(int containerId, Inventory inventory, Container container, ChestTier tier, BlockPos pos) {
        this(containerId, inventory, container, tier, pos, container.getContainerSize() / tier.getRowLength(), tier.getRowLength(), true);
    }

    // Main constructor
    public TieredChestMenu(int containerId, Inventory inventory, Container container, ChestTier tier, BlockPos pos, int rows, int columns, boolean fancyChests) {
        super(ModMenuTypes.TIERED_CHEST_MENU, containerId);
        this.tier = tier;
        this.pos = pos;
        this.container = container;
        this.rows = rows;
        this.columns = columns;
        this.fancyChests = fancyChests;

        int chestWidth = GUI_PADDING * 2 + this.columns * SLOT_SIZE;
        this.imageWidth = Math.max(chestWidth, PLAYER_INV_WIDTH);
        this.imageHeight = 114 + this.rows * SLOT_SIZE;

        int leftOverhang = (int) Math.ceil((this.columns - PLAYER_INV_COLUMNS) / 2.0);
        int chestLeft = 0;
        int playerLeft = chestLeft + leftOverhang * SLOT_SIZE;

        // Chest / barrel slots
        for (int j = 0; j < rows; ++j) {
            for (int k = 0; k < columns; ++k) {
                this.addSlot(new Slot(
                        container,
                        k + j * columns,
                        chestLeft + GUI_PADDING + k * SLOT_SIZE,
                        18 + j * SLOT_SIZE));
            }
        }

        // Player inventory slots
        int playerInvY = 30 + rows * SLOT_SIZE;
        for (int j = 0; j < PLAYER_INV_ROWS; ++j) {
            for (int k = 0; k < PLAYER_INV_COLUMNS; ++k) {
                this.addSlot(new Slot(
                        inventory,
                        k + j * PLAYER_INV_COLUMNS + PLAYER_INV_COLUMNS,
                        playerLeft + GUI_PADDING + k * SLOT_SIZE,
                        playerInvY + j * SLOT_SIZE));
            }
        }

        // Hotbar
        for (int j = 0; j < PLAYER_INV_COLUMNS; ++j) {
            this.addSlot(new Slot(
                    inventory,
                    j,
                    playerLeft + GUI_PADDING + j * SLOT_SIZE,
                    playerInvY + 58));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int containerSize = container.getContainerSize();
            if (index < containerSize) {
                if (!this.moveItemStackTo(itemstack1, containerSize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, containerSize, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.container.stillValid(player);
    }

    public ChestTier getTier() {
        return tier;
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Container getContainer() {
        return container;
    }

    public boolean isFancyChests() {
        return fancyChests;
    }
}
