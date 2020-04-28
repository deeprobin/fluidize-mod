package de.deeprobin.fluidize;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.*;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluidizeMod implements ModInitializer {

	public static BaseFluid MILK_STILL = new MilkFluid.Still();
	public static BaseFluid MILK_FLOWING = new MilkFluid.Flowing();
	public static MilkBottleItem MILK_BOTTLE = new MilkBottleItem();
	public static Block MILK_BLOCK = new MilkBlock();

	public static BaseFluid HONEY_STILL = new HoneyFluid.Still();
	public static BaseFluid HONEY_FLOWING = new HoneyFluid.Flowing();
	public static BucketItem HONEY_BUCKET = new HoneyBucketItem();
	public static Block HONEY_BLOCK = new HoneyBlock();


	@Override
	public void onInitialize() {
		Registry.register(Registry.FLUID, new Identifier("fluidize", "milk"), MILK_STILL);
		Registry.register(Registry.FLUID, new Identifier("fluidize", "milk_flowing"), MILK_FLOWING);
		Registry.register(Registry.BLOCK, new Identifier("fluidize", "milk"), MILK_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("fluidize", "milk_bottle"), MILK_BOTTLE);
		Registry.register(Registry.FLUID, new Identifier("fluidize", "honey"), HONEY_STILL);
		Registry.register(Registry.FLUID, new Identifier("fluidize", "honey_flowing"), HONEY_FLOWING);
		Registry.register(Registry.BLOCK, new Identifier("fluidize", "honey"), HONEY_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("fluidize", "honey_bucket"), HONEY_BUCKET);

	}


}
