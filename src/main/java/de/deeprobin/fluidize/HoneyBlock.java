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
        if (this.isSliding(pos, entity)) {
            this.updateSlidingVelocity(entity);
            this.addCollisionEffects(world, entity);
        }

        super.onEntityCollision(state, world, pos, entity);
    }

    private boolean isSliding(BlockPos pos, Entity entity) {
        if (entity.onGround) {
            return false;
        } else if (entity.getY() > (double)pos.getY() + 0.9375D - 1.0E-7D) {
            return false;
        } else if (entity.getVelocity().y >= -0.08D) {
            return false;
        } else {
            double d = Math.abs((double)pos.getX() + 0.5D - entity.getX());
            double e = Math.abs((double)pos.getZ() + 0.5D - entity.getZ());
            double f = 0.4375D + (double)(entity.getWidth() / 2.0F);
            return d + 1.0E-7D > f || e + 1.0E-7D > f;
        }
    }

    private void updateSlidingVelocity(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
        if (vec3d.y < -0.13D) {
            double d = -0.05D / vec3d.y;
            entity.setVelocity(new Vec3d(vec3d.x * d, -0.05D, vec3d.z * d));
        } else {
            entity.setVelocity(new Vec3d(vec3d.x, -0.05D, vec3d.z));
        }

        entity.fallDistance = 0.0F;
    }

    private void addCollisionEffects(World world, Entity entity) {
        if (hasHoneyBlockEffects(entity)) {
            if (world.random.nextInt(5) == 0) {
                entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
            }

            if (!world.isClient && world.random.nextInt(5) == 0) {
                world.sendEntityStatus(entity, (byte)53);
            }
        }

    }

    private static boolean hasHoneyBlockEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecartEntity || entity instanceof TntEntity || entity instanceof BoatEntity;
    }
}
