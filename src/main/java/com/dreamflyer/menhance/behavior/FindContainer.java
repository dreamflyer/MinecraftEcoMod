package com.dreamflyer.menhance.behavior;

import java.util.ArrayList;
import java.util.List;

import com.dreamflyer.menhance.utils.Utils;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.PositionImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;

public class FindContainer extends Behavior<Villager> {
	   public FindContainer() {
	      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, Utils.CONTAINER_INTERACTION_TARGET.get(), MemoryStatus.VALUE_ABSENT, Utils.NEAREST_CONTAINERS.get(), MemoryStatus.VALUE_PRESENT));
	   }

	   public boolean checkExtraStartConditions(ServerLevel p_23950_, Villager p_23951_) {
	      return this.getVisibleContainers(p_23951_).size() > 0;
	   }

	   public void start(ServerLevel level, Villager villager, long p_23955_) {
	      super.start(level, villager, p_23955_);
	      
	      BlockPos containerPos = selectProperContainer(level, villager);
	      
	      if(containerPos != null) {
	    	  if((villager.getId() + "") == Utils.DebugVillagerId) {
	    		  System.out.println("Container found.");
	    	  }
	    	  
	    	  villager.getBrain().setMemory(Utils.CONTAINER_INTERACTION_TARGET.get(), containerPos);
	    	  villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(containerPos));
	      }
	   }
	   
	   private BlockPos selectProperContainer(ServerLevel level, Villager villager) {
		   List<BlockPos> visibleContainers = getVisibleContainers(villager);
		   
		   BlockPos jobPos = null;
		   
		   if(villager.getBrain().getMemory(MemoryModuleType.JOB_SITE).isPresent()) {
			   jobPos = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE).get().pos();
			   
			   if(visibleContainers.isEmpty()) {
				   return null;
			   }
			   
			   BlockPos result = null;
			   
			   for(BlockPos blockPos: visibleContainers) {
				   if(blockPos.distSqr(jobPos) > 49.0) {
					   continue;
				   }
				   
				   if(result == null) {
					   result = blockPos;
				   }
				   
				   if(blockPos.distSqr(jobPos) < result.distSqr(jobPos)) {
					   result = blockPos;
				   }
			   }
		   }
		   
		   return visibleContainers.isEmpty() ? null : visibleContainers.get(level.getRandom().nextInt(visibleContainers.size()));
	   }

	   private List<BlockPos> getVisibleContainers(Villager villager) {
		   ArrayList<BlockPos> result = new ArrayList();
		   
		   if(!villager.getBrain().getMemory(Utils.NEAREST_CONTAINERS.get()).isPresent()) {
			   return result;
		   }
		   
		   villager.getBrain().getMemory(Utils.NEAREST_CONTAINERS.get()).get().forEach(item -> result.add(item.pos()));
		   
		   return result;
	   }
}
