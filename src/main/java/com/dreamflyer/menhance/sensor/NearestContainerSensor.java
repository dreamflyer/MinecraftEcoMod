package com.dreamflyer.menhance.sensor;

import java.util.List;
import java.util.Set;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class NearestContainerSensor extends Sensor<Villager> {
	   private static final int SCAN_RATE = 40;

	   public NearestContainerSensor() {
	      super(40);
	   }

	   protected void doTick(ServerLevel p_26754_, Villager p_26755_) {
	      ResourceKey<Level> resourcekey = p_26754_.dimension();
	      BlockPos blockpos = p_26755_.blockPosition();
	      List<GlobalPos> list = Lists.newArrayList();
	      int i = 4;

	      for(int j = -4; j <= 4; ++j) {
	         for(int k = -2; k <= 2; ++k) {
	            for(int l = -4; l <= 4; ++l) {
	               BlockPos blockpos1 = blockpos.offset(j, k, l);
	               
	               if (ImmutableSet.of(Blocks.CHEST).contains(p_26754_.getBlockState(blockpos1).getBlock())) {
	                  list.add(GlobalPos.of(resourcekey, blockpos1));
	               }
	            }
	         }
	      }

	      Brain<?> brain = p_26755_.getBrain();
	      if (!list.isEmpty()) {
	         brain.setMemory(Utils.NEAREST_CONTAINERS.get(), list);
	      } else {
	         brain.eraseMemory(Utils.NEAREST_CONTAINERS.get());
	      }

	   }

	   public Set<MemoryModuleType<?>> requires() {
	      return ImmutableSet.of(Utils.NEAREST_CONTAINERS.get());
	   }
	}
