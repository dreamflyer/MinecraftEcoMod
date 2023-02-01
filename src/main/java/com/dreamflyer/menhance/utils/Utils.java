package com.dreamflyer.menhance.utils;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.dreamflyer.menhance.entity.crawfish.Crawfish;
import com.example.examplemod.ExampleMod;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Utils {
	public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, ExampleMod.MODID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPE_REGISTER = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExampleMod.MODID);
	public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);
	
	public static final RegistryObject<MemoryModuleType<BlockPos>> CONTAINER_INTERACTION_TARGET = register("container_interaction_target");
	public static final RegistryObject<MemoryModuleType<List<GlobalPos>>> NEAREST_CONTAINERS = register("nearest_container");
	public static final RegistryObject<MemoryModuleType<List<GlobalPos>>> FISH_PLACE = register("fish_place");
	
	public static final RegistryObject<EntityType<Crawfish>> CRAWFISH = registerEntity("crawfish", EntityType.Builder.of(new EntityType.EntityFactory<Crawfish>() {
		@Override
		public Crawfish create(EntityType<Crawfish> p_20722_, Level p_20723_) {
			return new Crawfish(p_20722_, p_20723_);
		}
	}, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
	
	public static final RegistryObject<Item> CRAWFISH_BUCKET = Utils.ITEM_REGISTER.register("crawfish_bucket", () -> new MobBucketItem(CRAWFISH.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, (new Item.Properties()).stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
	
	public static String DebugVillagerId = null;
	
	public static <U> RegistryObject<MemoryModuleType<U>> register(String p_26389_) {
		return MEMORY_MODULE_TYPE_REGISTER.register(p_26389_, () -> {
			return new MemoryModuleType<U>(Optional.empty());
		});
	}
	
	public static <U extends Entity> RegistryObject<EntityType<U>> registerEntity(String name, EntityType.Builder<U> builder) {
		return ENTITY_TYPE_REGISTER.register(name, () -> builder.build(name));
	}
	
	public static Block posToBlock(ServerLevel level, BlockPos pos) {
		return posToState(level, pos).getBlock();
	}
	
	public static BlockState posToState(ServerLevel level, BlockPos pos) {
		return level.getBlockState(pos.south());
	}
	
	public static boolean isWaterlogged(ServerLevel level, BlockPos pos) {
		ImmutableMap<Property<?>, Comparable<?>> values = level.getBlockState(pos.south()).getValues();
		
		if(!values.containsKey(BlockStateProperties.WATERLOGGED)) {
			return false;
		}
		
		return "true".equals(values.get(BlockStateProperties.WATERLOGGED).toString());
	}
	
	public static boolean isSolid(Block block) {
		if(isPlanks(block)) {
	   		return true;
	   	}
		
		if(block == Blocks.DIRT) {
			return true;
		}
		
		if(block == Blocks.GRASS_BLOCK) {
			return true;
		}
		
		if(block == Blocks.COARSE_DIRT) {
			return true;
		}
		
		if(block == Blocks.ROOTED_DIRT) {
			return true;
		}
		
		if(block == Blocks.PODZOL) {
			return true;
		}
		
		if(block == Blocks.MYCELIUM) {
			return true;
		}
		
		if(block == Blocks.SAND) {
			return true;
		}
		
		if(block == Blocks.RED_SAND) {
			return true;
		}
		
		if(block == Blocks.MOSS_BLOCK) {
			return true;
		}
		
		if(block == Blocks.MUD) {
			return true;
		}
		
		if(block == Blocks.GLASS) {
			return true;
		}
		
		return false;
   }
	
   public static boolean isSoil(Block block) {
	   	if(block == Blocks.DIRT) {
			return true;
		}
		
		if(block == Blocks.GRASS_BLOCK) {
			return true;
		}
		
		if(block == Blocks.COARSE_DIRT) {
			return true;
		}
		
		if(block == Blocks.ROOTED_DIRT) {
			return true;
		}
		
		if(block == Blocks.PODZOL) {
			return true;
		}
		
		if(block == Blocks.MYCELIUM) {
			return true;
		}
		
		if(block == Blocks.SAND) {
			return true;
		}
		
		if(block == Blocks.RED_SAND) {
			return true;
		}
		
		if(block == Blocks.MOSS_BLOCK) {
			return true;
		}
		
		if(block == Blocks.MUD) {
			return true;
		}
		
		return false;
   }
   
   public static boolean isPlanks(Block block) {
	   if(block == Blocks.OAK_PLANKS) {
		   return true;  
	   }
	   
	   if(block == Blocks.SPRUCE_PLANKS) {
		   return true;  
	   }
	   
	   if(block == Blocks.BIRCH_PLANKS) {
			return true;
	   }
	   
	   if(block == Blocks.JUNGLE_PLANKS) {
			return true;
	   }
	   
	   if(block == Blocks.ACACIA_PLANKS) {
			return true;
	   }
	   
	   if(block == Blocks.DARK_OAK_PLANKS) {
			return true;
	   }
	   
	   if(block == Blocks.MANGROVE_PLANKS) {
			return true;
	   }
	   
	   return false;
   }
}
