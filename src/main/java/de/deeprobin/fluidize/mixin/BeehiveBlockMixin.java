package de.deeprobin.fluidize.mixin;

import de.deeprobin.fluidize.FluidizeMod;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeehiveBlock.class)
public abstract class BeehiveBlockMixin extends BlockWithEntity {

    protected BeehiveBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
        ItemStack itemStack = player.getStackInHand(hand);
        ItemStack itemStack2 = itemStack.copy();
        int i = (Integer)state.get(BeehiveBlock.HONEY_LEVEL);
        boolean bl = false;
        if (i >= 20) {
            if (itemStack.getItem() == Items.BUCKET) {
                itemStack.decrement(1);
                world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                if (itemStack.isEmpty()) {
                    player.setStackInHand(hand, new ItemStack(FluidizeMod.HONEY_BUCKET));
                } else if (!player.inventory.insertStack(new ItemStack(FluidizeMod.HONEY_BUCKET))) {
                    player.dropItem(new ItemStack(FluidizeMod.HONEY_BUCKET), false);
                }

                bl = true;
            }
        }

        if (bl) {
            if (!CampfireBlock.isLitCampfireInRange(world, pos, 5)) {
                if (this.hasBees(world, pos)) {
                    this.angerNearbyBees(world, pos);
                }

                this.takeHoney(world, state, pos, player, BeehiveBlockEntity.BeeState.EMERGENCY);
            } else {
                this.takeHoney(world, state, pos);
                if (player instanceof ServerPlayerEntity) {
                    Criterions.SAFELY_HARVEST_HONEY.test((ServerPlayerEntity)player, pos, itemStack2);
                }
            }

            info.setReturnValue(ActionResult.SUCCESS);
        } else {
            info.setReturnValue(ActionResult.PASS);
        }
    }

    @Shadow
    public abstract void takeHoney(World world, BlockState state, BlockPos pos);

    @Shadow
    public abstract void takeHoney(World world, BlockState state, BlockPos pos, PlayerEntity player, BeehiveBlockEntity.BeeState emergency);
    @Shadow
    protected abstract void angerNearbyBees(World world, BlockPos pos);

    @Shadow
    protected abstract boolean hasBees(World world, BlockPos pos);
}
