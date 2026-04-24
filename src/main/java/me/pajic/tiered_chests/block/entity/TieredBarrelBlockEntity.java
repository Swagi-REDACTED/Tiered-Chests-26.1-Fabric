package me.pajic.tiered_chests.block.entity;

import me.pajic.tiered_chests.block.TieredBarrelBlock;
import me.pajic.tiered_chests.network.ModNetworking;
import me.pajic.tiered_chests.ui.TieredChestMenu;
import me.pajic.tiered_chests.util.ChestTier;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TieredBarrelBlockEntity extends RandomizableContainerBlockEntity
        implements ExtendedMenuProvider<ModNetworking.S2CTieredChestPayload> {
    private final ChestTier tier;
    private NonNullList<ItemStack> items;

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            TieredBarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
            TieredBarrelBlockEntity.this.updateBlockState(state, true);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            TieredBarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
            TieredBarrelBlockEntity.this.updateBlockState(state, false);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int previousCount,
                int newCount) {
        }

        @Override
        public boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof TieredChestMenu menu && menu.getPos().equals(worldPosition);
        }
    };

    public TieredBarrelBlockEntity(BlockEntityType<?> type, ChestTier tier, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.tier = tier;
        this.items = NonNullList.withSize(tier.getSlotCount(), ItemStack.EMPTY);
    }

    public TieredBarrelBlockEntity(ChestTier tier, BlockPos pos, BlockState state) {
        this(ModBlockEntities.TIERED_BARRELS.get(tier), tier, pos, state);
    }

    public ChestTier getTier() {
        return tier;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.tiered_chests." + tier.getSerializedName() + "_barrel");
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

    @Override
    public void startOpen(net.minecraft.world.entity.ContainerUser player) {
        if (!this.remove && !player.getLivingEntity().isSpectator()) {
            this.openersCounter.incrementOpeners(player.getLivingEntity(), this.getLevel(), this.getBlockPos(),
                    this.getBlockState(), player.getContainerInteractionRange());
        }
    }

    @Override
    public void stopOpen(net.minecraft.world.entity.ContainerUser player) {
        if (!this.remove && !player.getLivingEntity().isSpectator()) {
            this.openersCounter.decrementOpeners(player.getLivingEntity(), this.getLevel(), this.getBlockPos(),
                    this.getBlockState());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(input)) {
            ContainerHelper.loadAllItems(input, this.items);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.trySaveLootTable(output)) {
            ContainerHelper.saveAllItems(output, this.items);
        }
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    private void updateBlockState(BlockState state, boolean open) {
        this.level.setBlock(this.worldPosition, state.setValue(TieredBarrelBlock.OPEN, open), 3);
    }

    private void playSound(BlockState state, net.minecraft.sounds.SoundEvent sound) {
        net.minecraft.core.Vec3i vec3i = state.getValue(TieredBarrelBlock.FACING).getUnitVec3i();
        double d = (double) this.worldPosition.getX() + 0.5 + (double) vec3i.getX() / 2.0;
        double e = (double) this.worldPosition.getY() + 0.5 + (double) vec3i.getY() / 2.0;
        double f = (double) this.worldPosition.getZ() + 0.5 + (double) vec3i.getZ() / 2.0;
        this.level.playSound(null, d, e, f, sound, SoundSource.BLOCKS, 0.5F,
                this.level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TieredBarrelBlockEntity blockEntity) {
        blockEntity.recheckOpen();
    }
}
