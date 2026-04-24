package me.pajic.tiered_chests.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.pajic.tiered_chests.block.entity.TieredBarrelBlockEntity;
import me.pajic.tiered_chests.util.ChestTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;


public class TieredBarrelBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    protected static final VoxelShape SHAPE = Block.column(14.0, 0.0, 14.0);

    public static final MapCodec<TieredBarrelBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ChestTier.CODEC.fieldOf("tier").forGetter(TieredBarrelBlock::getTier),
            propertiesCodec()).apply(inst, TieredBarrelBlock::new));

    private final ChestTier tier;

    public TieredBarrelBlock(ChestTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }

    @Override
    protected MapCodec<? extends TieredBarrelBlock> codec() {
        return CODEC;
    }

    public ChestTier getTier() {
        return tier;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }


    @Override
    protected float getShadeBrightness(BlockState state, net.minecraft.world.level.BlockGetter level,
            net.minecraft.core.BlockPos pos) {
        return 1.0F;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TieredBarrelBlockEntity(tier, pos, state);
    }

    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level,
            BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return level.isClientSide() ? null
                : createTickerHelper(type,
                        me.pajic.tiered_chests.block.entity.ModBlockEntities.TIERED_BARRELS.get(tier),
                        TieredBarrelBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TieredBarrelBlockEntity barrel) {
                player.openMenu(barrel);
                player.awardStat(Stats.OPEN_BARREL);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos,
            boolean movedByPiston) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }

}
