package com.dreamflyer.menhance.sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.dreamflyer.menhance.utils.Utils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ExtendedSecondaryJobSensor extends Sensor<Villager> {
	   private static final int SCAN_RATE = 40;

	   public ExtendedSecondaryJobSensor() {
	      super(SCAN_RATE);
	   }

	   protected void doTick(ServerLevel serverLevel, Villager villager) {
	      ResourceKey<Level> resourcekey = serverLevel.dimension();
	      
	      List<GlobalPos> resultList = Lists.newArrayList();
	     
	      List<BlockPos> positions = positionsAround(villager);
	      
	      tryAddSecondaryJobs(positions, pos -> isSecondaryPoi(serverLevel, villager, pos), resourcekey, resultList);
	      tryAddSecondaryJobs(positions, pos -> isSugarCaneFarmland(serverLevel, villager, pos), resourcekey, resultList);
	      tryAddSecondaryJobs(positions, pos -> isFishFarm(serverLevel, villager, pos), resourcekey, resultList);

	      Brain<?> brain = villager.getBrain();
	      
	      if (!resultList.isEmpty()) {
	         brain.setMemory(MemoryModuleType.SECONDARY_JOB_SITE, resultList);
	      } else {
	         brain.eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);
	      }

	   }
	   
	   private static boolean isSecondaryPoi(ServerLevel level, Villager villager, BlockPos position) {
		   return villager.getVillagerData().getProfession().secondaryPoi().contains(level.getBlockState(position).getBlock());
	   }
	   
	   private static boolean isSugarCaneFarmland(ServerLevel level, Villager villager, BlockPos position) {
		   return villager.getVillagerData().getProfession() == VillagerProfession.FARMER && isUnderSoilNearWater(position, level);
	   }
	   
	   private static boolean isFishFarm(ServerLevel level, Villager villager, BlockPos position) {
		   return villager.getVillagerData().getProfession() == VillagerProfession.FISHERMAN && isWaterNearSolidUnderAllowed(level, position);
	   }

	   public Set<MemoryModuleType<?>> requires() {
	      return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
	   }
	   
	   private static List<BlockPos> positionsAround(Villager villager) {
		   BlockPos villagerBlockPosition = villager.blockPosition();
		   
		   ArrayList<BlockPos> result = new ArrayList<>();
		   
		   for(int i = -4; i <= 4; ++i) {
		         for(int j = -2; j<= 2; ++j) {
		            for(int k = -4; k <= 4; ++k) {
		               BlockPos blockpos = villagerBlockPosition.offset(i, j, k);
		               
		               result.add(blockpos);
		            }
		         }
		      }
		   
		   return result;
	   }
	   
	   private static boolean isUnderSoilNearWater(BlockPos pos, ServerLevel level) {
		   if(!Utils.isSoil(level.getBlockState(pos.below()).getBlock())) {
				return false;
			}
		   
		    if(!level.getBlockState(pos).isAir()) {
		    	return false;
		    }
		    
		    if(isNearWater(level, pos.below())) {
				return true;
			}
			
			return false;
	   }
	   
	   private static boolean isNearWater(LevelReader p_53259_, BlockPos p_53260_) {
		      BlockState state = p_53259_.getBlockState(p_53260_);
		      
		      for(BlockPos blockpos : BlockPos.betweenClosed(p_53260_.offset(-1, 0, -1), p_53260_.offset(1, 1, 1))) {
		         if (state.canBeHydrated(p_53259_, p_53260_, p_53259_.getFluidState(blockpos), blockpos)) {
		            return true;
		         }
		      }

		      return false;
	   }
	   
	   public static boolean isWaterNearSolidUnderAllowed(ServerLevel level, BlockPos position) {
		   Block block = Utils.posToBlock(level, position);
		   
		   if(!isWater(block)) {
			   return false;
		   }
		   
		   if(!isAllowedToBeAbove(level, position.above())) {
			   return false;
		   }
		   
		   if(Utils.isSolid(Utils.posToBlock(level, position.west()))) {
			   return true;
		   }
		   
		   if(Utils.isSolid(Utils.posToBlock(level, position.east()))) {
			   return true;
		   }
		   
		   if(Utils.isSolid(Utils.posToBlock(level, position.north()))) {
			   return true;
		   }
		   
		   if(Utils.isSolid(Utils.posToBlock(level, position.south()))) {
			   return true;
		   }
		   
		   return false;
	   }
	   
	   private static boolean isAllowedToBeAbove(ServerLevel level, BlockPos position) {
		   Block block = Utils.posToBlock(level, position);
		   
		   BlockState state = Utils.posToState(level, position);
		   
		   if(state.isAir()) {
			   return true;
		   }
		   
		   if(Utils.isWaterlogged(level, position)) {
			   return false;
		   }
		   
		   if(block instanceof FenceBlock) {
			   return true;
		   }
		   
		   return false;
	   }
	   
	   private static boolean isWater(Block block) {
			if(block == Blocks.WATER) {
				return true;
			}
			
			return false;
	   }
	   
	   private static void tryAddSecondaryJobs(List<BlockPos> blocksToCheck, Predicate<BlockPos> predicate, ResourceKey<Level> resourcekey, List<GlobalPos> resultList) {
		   blocksToCheck.forEach(item -> {
			   if(predicate.test(item)) {
				   GlobalPos result = GlobalPos.of(resourcekey, item);
				   
				   if(!resultList.contains(result)) {
					   resultList.add(result);
				   }
			   }
		   });
	   }
}
