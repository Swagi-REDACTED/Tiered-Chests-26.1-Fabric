package me.pajic.tiered_chests.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WorldlyCompoundContainer extends CompoundContainer implements WorldlyContainer {

    public WorldlyCompoundContainer(Container container1, Container container2) {
        super(container1, container2);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        int size = this.getContainerSize();
        int[] slots = new int[size];
        for (int i = 0; i < size; i++) {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
    }
}
