package me.pajic.tiered_chests.block.entity;

import me.pajic.tiered_chests.network.ModNetworking;
import me.pajic.tiered_chests.ui.TieredChestMenu;
import me.pajic.tiered_chests.util.ChestTier;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import java.util.Objects;

import java.util.List;

public class TieredChestBlockEntity extends RandomizableContainerBlockEntity
        implements ExtendedMenuProvider<ModNetworking.S2CTieredChestPayload>, LidBlockEntity {
    private final ChestTier tier;
    private NonNullList<ItemStack> items;

    private final ChestLidController chestLidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            level.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F,
                    level.getRandom().nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            level.playSound(null, pos, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F,
                    level.getRandom().nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int previousCount,
                int newCount) {
            level.blockEvent(pos, state.getBlock(), 1, newCount);
        }

        @Override
        public boolean isOwnContainer(Player player) {
            if (!(player.containerMenu instanceof TieredChestMenu menu)) {
                return false;
            } else {
                Container container = menu.getContainer();
                return container == TieredChestBlockEntity.this || container instanceof CompoundContainer && ((CompoundContainer)container).contains(TieredChestBlockEntity.this);
            }
        }
    };

    public TieredChestBlockEntity(BlockEntityType<?> type, ChestTier tier, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.tier = tier;
        this.items = NonNullList.withSize(tier.getSlotCount(), ItemStack.EMPTY);
    }

    public ChestTier getTier() {
        return tier;
    }

    public TieredChestBlockEntity(ChestTier tier, BlockPos pos, BlockState state) {
        this(ModBlockEntities.TIERED_CHESTS.get(tier), tier, pos, state);
    }

    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, TieredChestBlockEntity blockEntity) {
        blockEntity.chestLidController.tickLid();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TieredChestBlockEntity blockEntity) {
        blockEntity.openersCounter.recheckOpeners(level, pos, state);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            chestLidController.shouldBeOpen(type > 0);
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    public void startOpen(ContainerUser player) {
        if (!this.remove && !player.getLivingEntity().isSpectator()) {
            this.openersCounter.incrementOpeners(player.getLivingEntity(), this.getLevel(), this.getBlockPos(),
                    this.getBlockState(), player.getContainerInteractionRange());
        }
    }

    @Override
    public void stopOpen(ContainerUser player) {
        if (!this.remove && !player.getLivingEntity().isSpectator()) {
            this.openersCounter.decrementOpeners(player.getLivingEntity(), this.getLevel(), this.getBlockPos(),
                    this.getBlockState());
        }
    }

    @Override
    public List<ContainerUser> getEntitiesWithContainerOpen() {
        return this.openersCounter.getEntitiesWithContainerOpen(this.getLevel(), this.getBlockPos());
    }

    @Override
    public float getOpenNess(float tickDelta) {
        return chestLidController.getOpenness(tickDelta);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.tiered_chests." + tier.getSerializedName() + "_chest");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return tier.getSlotCount();
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new TieredChestMenu(containerId, inventory, this, tier, worldPosition);
    }

    @Override
    public ModNetworking.S2CTieredChestPayload getScreenOpeningData(ServerPlayer player) {
        return new ModNetworking.S2CTieredChestPayload(worldPosition, tier, false);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        this.unpackLootTable(player);
        return createMenu(i, inventory);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level != null) {
            this.openersCounter.recheckOpeners(this.level, this.worldPosition, this.getBlockState());
        }
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
}
