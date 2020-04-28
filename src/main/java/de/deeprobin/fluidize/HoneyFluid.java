package de.deeprobin.fluidize;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Random;

public abstract class HoneyFluid extends BaseFluid {

    public Fluid getFlowing() {
        return FluidizeMod.HONEY_FLOWING;
    }

    public Fluid getStill() {
        return FluidizeMod.HONEY_STILL;
    }

    public Item getBucketItem() {
        return FluidizeMod.HONEY_BUCKET;
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
        if (!state.isStill() && !state.get(FALLING)) {
            if (random.nextInt(64) == 0) {
                world.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_BEEHIVE_DRIP, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
            }
        } else if (random.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.LANDING_HONEY, (double)pos.getX() + (double)random.nextFloat(), (double)pos.getY() + (double)random.nextFloat(), (double)pos.getZ() + (double)random.nextFloat(), 0.0D, 0.0D, 0.0D);
        }

    }

    @Environment(EnvType.CLIENT)
    public ParticleEffect getParticle() {
        return ParticleTypes.POOF;
    }

    protected boolean isInfinite() {
        return false;
    }

    protected void beforeBreakingBlock(IWorld world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.getBlock().hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world.getWorld(), pos, blockEntity);
    }

    public int method_15733(WorldView worldView) {
        return 4;
    }

    public BlockState toBlockState(FluidState state) {
        return FluidizeMod.HONEY_BLOCK.getDefaultState().with(Properties.LEVEL_15, method_15741(state));
    }

    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }

    public int getLevelDecreasePerBlock(WorldView world) {
        return 4;
    }

    public int getTickRate(WorldView world) {
        return 70;
    }

    public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    protected float getBlastResistance() {
        return 100.0F;
    }

    public static class Flowing extends HoneyFluid {
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        public boolean isStill(FluidState state) {
            return false;
        }
    }

    public static class Still extends HoneyFluid {
        public int getLevel(FluidState state) {
            return 8;
        }

        public boolean isStill(FluidState state) {
            return true;
        }
    }

}
