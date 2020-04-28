package de.deeprobin.fluidize;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MilkBlock extends FluidBlock {
    public MilkBlock() {
        super(FluidizeMod.MILK_STILL, Block.Settings.of(Material.WATER).noCollision().strength(100.0F, 100.0F));
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.clearStatusEffects();
        }
    }


}
