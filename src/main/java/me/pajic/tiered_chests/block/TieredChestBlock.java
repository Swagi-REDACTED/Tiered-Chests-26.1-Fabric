package me.pajic.tiered_chests.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.pajic.tiered_chests.block.entity.ModBlockEntities;
import me.pajic.tiered_chests.block.entity.TieredChestBlockEntity;
import me.pajic.tiered_chests.network.ModNetworking;
import me.pajic.tiered_chests.ui.TieredChestMenu;
import me.pajic.tiered_chests.util.ChestTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import java.util.Optional;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;

public class TieredChestBlock extends BaseEntityBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final MapCodec<TieredChestBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ChestTier.CODEC.fieldOf("tier").forGetter(TieredChestBlock::getTier),
            propertiesCodec()).apply(inst, TieredChestBlock::new));

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.column(14.0, 0.0, 14.0);
    private static final Map<Direction, VoxelShape> HALF_SHAPES = Shapes
            .rotateHorizontal(Block.boxZ(14.0, 0.0, 14.0, 0.0, 15.0));

    private final ChestTier tier;

    public TieredChestBlock(ChestTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, ChestType.SINGLE)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends TieredChestBlock> codec() {
        return CODEC;
    }

    public ChestTier getTier() {
        return tier;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(TYPE)) {
            case SINGLE -> SHAPE;
            case LEFT, RIGHT -> HALF_SHAPES.get(getConnectedDirection(state));
        };
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TieredChestBlockEntity(tier, pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        return level.isClientSide()
                ? createTickerHelper(type, ModBlockEntities.TIERED_CHESTS.get(tier),
                        TieredChestBlockEntity::lidAnimateTick)
                : createTickerHelper(type, ModBlockEntities.TIERED_CHESTS.get(tier),
                        TieredChestBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
            if (menuProvider != null) {
                player.openMenu(menuProvider);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @Nullable
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return this.combine(state, level, pos, false).apply(MENU_PROVIDER_COMBINER).orElse(null);
    }

    public DoubleBlockCombiner.NeighborCombineResult<? extends TieredChestBlockEntity> combine(
            BlockState state, Level level, BlockPos pos, boolean ignoreBeingBlocked) {
        BiPredicate<LevelAccessor, BlockPos> predicate;
        if (ignoreBeingBlocked) {
            predicate = (levelAccessor, blockPos) -> false;
        } else {
            predicate = TieredChestBlock::isChestBlockedAt;
        }

        return DoubleBlockCombiner.combineWithNeigbour(
                ModBlockEntities.TIERED_CHESTS.get(tier),
                TieredChestBlock::getBlockType,
                TieredChestBlock::getConnectedDirection,
                FACING,
                state,
                level,
                pos,
                predicate);
    }

    public static DoubleBlockCombiner.BlockType getBlockType(BlockState state) {
        ChestType type = state.getValue(TYPE);
        if (type == ChestType.SINGLE) {
            return DoubleBlockCombiner.BlockType.SINGLE;
        } else {
            return type == ChestType.RIGHT ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
        }
    }

    public static Direction getConnectedDirection(BlockState state) {
        Direction facing = state.getValue(FACING);
        return state.getValue(TYPE) == ChestType.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
    }

    public static boolean isChestBlockedAt(LevelAccessor level, BlockPos pos) {
        return isBlockedChestByBlock(level, pos);
    }

    private static boolean isBlockedChestByBlock(BlockGetter level, BlockPos pos) {
        BlockPos above = pos.above();
        return level.getBlockState(above).isRedstoneConductor(level, above);
    }

    private static final DoubleBlockCombiner.Combiner<TieredChestBlockEntity, Optional<MenuProvider>> MENU_PROVIDER_COMBINER = new DoubleBlockCombiner.Combiner<>() {
        public Optional<MenuProvider> acceptDouble(final TieredChestBlockEntity first,
                final TieredChestBlockEntity second) {
            final Container container = new CompoundContainer(first, second);
            return Optional.of(new ExtendedMenuProvider<ModNetworking.S2CTieredChestPayload>() {
                @Override
                public ModNetworking.S2CTieredChestPayload getScreenOpeningData(ServerPlayer player) {
                    return new ModNetworking.S2CTieredChestPayload(first.getBlockPos(), first.getTier(), true);
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(final int containerId, final Inventory inventory,
                        final Player player) {
                    if (first.canOpen(player) && second.canOpen(player)) {
                        first.unpackLootTable(inventory.player);
                        second.unpackLootTable(inventory.player);
                        return new TieredChestMenu(containerId, inventory, container, first.getTier(),
                                first.getBlockPos());
                    } else {
                        return null;
                    }
                }

                @Override
                public Component getDisplayName() {
                    if (first.hasCustomName()) {
                        return first.getDisplayName();
                    } else {
                        return second.hasCustomName() ? second.getDisplayName()
                                : Component.translatable("container.chestDouble");
                    }
                }
            });
        }

        public Optional<MenuProvider> acceptSingle(final TieredChestBlockEntity single) {
            return Optional.of(single);
        }

        public Optional<MenuProvider> acceptNone() {
            return Optional.empty();
        }
    };

    public static DoubleBlockCombiner.Combiner<TieredChestBlockEntity, Float2FloatFunction> opennessCombiner(
            final net.minecraft.world.level.block.entity.LidBlockEntity entity) {
        return new DoubleBlockCombiner.Combiner<>() {
            public Float2FloatFunction acceptDouble(final TieredChestBlockEntity first,
                    final TieredChestBlockEntity second) {
                return partialTickTime -> Math.max(first.getOpenNess(partialTickTime),
                        second.getOpenNess(partialTickTime));
            }

            public Float2FloatFunction acceptSingle(final TieredChestBlockEntity single) {
                return single::getOpenNess;
            }

            public Float2FloatFunction acceptNone() {
                return entity::getOpenNess;
            }
        };
    }

    public static interface Float2FloatFunction {
        float get(float value);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ChestType type = ChestType.SINGLE;
        Direction direction = context.getHorizontalDirection().getOpposite();
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        boolean secondaryUse = context.isSecondaryUseActive();
        Direction clickedFace = context.getClickedFace();

        if (clickedFace.getAxis().isHorizontal() && secondaryUse) {
            Direction neighbourFacing = this.candidatePartnerFacing(context.getLevel(), context.getClickedPos(),
                    clickedFace.getOpposite());
            if (neighbourFacing != null && neighbourFacing.getAxis() != clickedFace.getAxis()) {
                direction = neighbourFacing;
                type = neighbourFacing.getCounterClockWise() == clickedFace.getOpposite() ? ChestType.RIGHT
                        : ChestType.LEFT;
            }
        }

        if (type == ChestType.SINGLE && !secondaryUse) {
            type = this.getChestType(context.getLevel(), context.getClickedPos(), direction);
        }

        return this.defaultBlockState()
                .setValue(FACING, direction)
                .setValue(TYPE, type)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    protected ChestType getChestType(Level level, BlockPos pos, Direction facingDirection) {
        if (facingDirection == this.candidatePartnerFacing(level, pos, facingDirection.getClockWise())) {
            return ChestType.LEFT;
        } else {
            return facingDirection == this.candidatePartnerFacing(level, pos, facingDirection.getCounterClockWise())
                    ? ChestType.RIGHT
                    : ChestType.SINGLE;
        }
    }

    @Nullable
    private Direction candidatePartnerFacing(Level level, BlockPos pos, Direction neighbourDirection) {
        BlockState state = level.getBlockState(pos.relative(neighbourDirection));
        return this.chestCanConnectTo(state) && state.getValue(TYPE) == ChestType.SINGLE ? state.getValue(FACING)
                : null;
    }

    public boolean chestCanConnectTo(BlockState blockState) {
        return blockState.is(this);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (this.chestCanConnectTo(neighborState) && direction.getAxis().isHorizontal()) {
            ChestType neighborType = neighborState.getValue(TYPE);
            if (state.getValue(TYPE) == ChestType.SINGLE
                    && neighborType != ChestType.SINGLE
                    && state.getValue(FACING) == neighborState.getValue(FACING)
                    && getConnectedDirection(neighborState) == direction.getOpposite()) {
                return state.setValue(TYPE, neighborType.getOpposite());
            }
        } else if (getConnectedDirection(state) == direction) {
            return state.setValue(TYPE, ChestType.SINGLE);
        }

        return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, WATERLOGGED);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TieredChestBlockEntity tieredBe) {
            tieredBe.recheckOpen();
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos,
            boolean movedByPiston) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }
}