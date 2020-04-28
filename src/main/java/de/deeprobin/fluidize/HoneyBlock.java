package de.deeprobin.fluidize;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HoneyBlock extends FluidBlock {
    public HoneyBlock() {
        super(FluidizeMod.HONEY_STILL, Block.Settings.of(Material.WATER).noCollision().strength(100.0F, 100.0F));
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        entity.slowMovement(state, new Vec3d(0.2D, 0.2D, 0.2D));
        if (entity instanceof BoatEntity && world.random.nextInt(5) == 0) {
            entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
        } else if (entity instanceof TntEntity) {
            entity.setSilent(true);
        }
    }
}
