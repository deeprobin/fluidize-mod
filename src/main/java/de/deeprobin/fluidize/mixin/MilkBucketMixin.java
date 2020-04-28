package de.deeprobin.fluidize.mixin;

import de.deeprobin.fluidize.FluidizeMod;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MilkBucketItem.class)
public abstract class MilkBucketMixin extends Item {
    public MilkBucketMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
        ItemStack itemStack = user.getStackInHand(hand);
        HitResult hitResult = rayTrace(world, user, RayTraceContext.FluidHandling.NONE);
        if (hitResult.getType() == HitResult.Type.MISS) {
            user.setCurrentHand(hand);
            info.setReturnValue(TypedActionResult.success(user.getStackInHand(hand)));
        } else if (hitResult.getType() != HitResult.Type.BLOCK) {
            info.setReturnValue(TypedActionResult.pass(itemStack));
        } else {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getSide();
            BlockPos blockPos2 = blockPos.offset(direction);
            if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos2, direction, itemStack)) {
                BlockState blockState;
                blockState = world.getBlockState(blockPos);
                BlockPos blockPos3 = blockState.getBlock() instanceof FluidFillable ? blockPos : blockPos2;
                if (this.placeFluid(user, world, blockPos3, blockHitResult)) {
                    if (user instanceof ServerPlayerEntity) {
                        Criterions.PLACED_BLOCK.trigger((ServerPlayerEntity) user, blockPos3, itemStack);
                    }

                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                    info.setReturnValue(TypedActionResult.success(this.getEmptiedStack(itemStack, user)));
                } else {
                    user.setCurrentHand(hand);
                    info.setReturnValue(TypedActionResult.success(user.getStackInHand(hand)));
                }

            } else {
                user.setCurrentHand(hand);
                info.setReturnValue(TypedActionResult.success(user.getStackInHand(hand)));
            }
        }
    }

    protected ItemStack getEmptiedStack(ItemStack stack, PlayerEntity player) {
        return !player.abilities.creativeMode ? new ItemStack(Items.BUCKET) : stack;
    }

    public boolean placeFluid(PlayerEntity player, World world, BlockPos pos, BlockHitResult hitResult) {
        BlockState blockState = world.getBlockState(pos);
        Material material = blockState.getMaterial();
        boolean bl = blockState.canBucketPlace(FluidizeMod.MILK_STILL);
        if (!blockState.isAir() && !bl && (!(blockState.getBlock() instanceof FluidFillable) || !((FluidFillable) blockState.getBlock()).canFillWithFluid(world, pos, blockState, FluidizeMod.MILK_STILL))) {
            return hitResult != null && this.placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null);
        } else {
            if (!world.isClient && bl && !material.isLiquid()) {
                world.breakBlock(pos, true);
            }

            this.playEmptyingSound(player, world, pos);
            world.setBlockState(pos, FluidizeMod.MILK_STILL.getDefaultState().getBlockState(), 11);


            return true;
        }

    }

    protected void playEmptyingSound(PlayerEntity player, IWorld world, BlockPos pos) {
        SoundEvent soundEvent = SoundEvents.ITEM_BUCKET_EMPTY;
        world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}
