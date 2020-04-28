package de.deeprobin.fluidize;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Map;

public class MilkBottleItem extends Item {

    public MilkBottleItem() {
        super(new Item.Settings().recipeRemainder(Items.GLASS_BOTTLE).food(new FoodComponent.Builder().hunger(0).saturationModifier(0).alwaysEdible().build()).group(ItemGroup.FOOD).maxCount(16));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if (user instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)user;
            Criterions.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (!world.isClient) {
            for (Map.Entry<StatusEffect, StatusEffectInstance> entry : user.getActiveStatusEffects().entrySet()) {
                if (!entry.getValue().isPermanent()) {
                    // It reduces the effects (dividing trough 8 because 8 is the content of a bucket which clears everything - difficult to understand - it is right)
                    entry.setValue(new StatusEffectInstance(entry.getKey(), entry.getValue().getDuration() / 8, entry.getValue().getAmplifier() / 8, entry.getValue().isAmbient(), entry.getValue().shouldShowParticles(), entry.getValue().shouldShowIcon()));
                }
            }
        }

        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (user instanceof PlayerEntity && !((PlayerEntity)user).abilities.creativeMode) {
                ItemStack itemStack = new ItemStack(Items.GLASS_BOTTLE);
                PlayerEntity playerEntity = (PlayerEntity)user;
                if (!playerEntity.inventory.insertStack(itemStack)) {
                    playerEntity.dropItem(itemStack, false);
                }
            }

            return stack;
        }
    }

    public int getMaxUseTime(ItemStack stack) {
        return 40;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    public SoundEvent getDrinkSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
