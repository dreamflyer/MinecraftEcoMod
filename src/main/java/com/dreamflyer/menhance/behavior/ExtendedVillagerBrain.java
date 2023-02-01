package com.dreamflyer.menhance.behavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import com.dreamflyer.menhance.sensor.ExtendedSecondaryJobSensor;
import com.dreamflyer.menhance.sensor.NearestContainerSensor;
import com.dreamflyer.menhance.utils.Utils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.TradeWithVillager;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.SecondaryPoiSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;

public class ExtendedVillagerBrain extends Brain<Villager> {
	private static SensorType<SecondaryPoiSensor> EXTENDED_SECONDARY_POI_SENSOR = new SensorType(() -> {
		return new ExtendedSecondaryJobSensor();
	});
	
	private static SensorType<SecondaryPoiSensor> NEAREST_CONTAINER_SENSOR = new SensorType(() -> {
		return new NearestContainerSensor();
	});
	
	public ExtendedVillagerBrain(Collection<? extends MemoryModuleType<?>> p_21855_, Collection<? extends SensorType<? extends Sensor<Villager>>> p_21856_, ImmutableList<Brain.MemoryValue<?>> p_21857_, Supplier<Codec<Brain<Villager>>> p_21858_) {
		super(p_21855_, p_21856_, p_21857_, p_21858_);
	}
	
	public ExtendedVillagerBrain(Set<MemoryModuleType<?>> keySet, Set<SensorType<? extends Sensor<? super Villager>>> keySet2, ImmutableList<MemoryValue<?>> of, Supplier<Codec<Brain<Villager>>> codec) {
		super(keySet, keySet2, of, codec);
	}
	
	@Override
	public void addActivityWithConditions(Activity p_21904_, ImmutableList<? extends Pair<Integer, ? extends Behavior<? super Villager>>> p_21905_, Set<Pair<MemoryModuleType<?>, MemoryStatus>> p_21906_) {
		if(Activity.WORK != p_21904_) {
			super.addActivityWithConditions(p_21904_, p_21905_, p_21906_);
			
			return;
		}
		
		ArrayList<Pair<Integer, Behavior<Villager>>> patchedList = new ArrayList();
		
		p_21905_.forEach(item -> {
			Integer priority = item.getFirst();
			Behavior<Villager> villagerBehaviour = (Behavior<Villager>) item.getSecond();
			
			if(villagerBehaviour instanceof RunOne) {
				RunOne<Villager> runOne = (RunOne<Villager>) villagerBehaviour;
				
				ArrayList newBehaviors = new ArrayList();
				
				runOne.behaviors.entries.forEach(entry -> {
					newBehaviors.add(Pair.of(entry.getData(), entry.getWeight()));
					
					if(entry.getData() instanceof HarvestFarmland) {
						newBehaviors.add(Pair.of(new HarvestCocoa(), entry.getWeight()));
						newBehaviors.add(Pair.of(new HarvestShugarCane(), entry.getWeight()));
						newBehaviors.add(Pair.of(new FisherBehavior(), (entry.getWeight() == 5) ? 2 : 5));
						newBehaviors.add(Pair.of(new ContainerInteraction(), entry.getWeight() / 3));
					}
				});
				
				RunOne<Villager> newRunOne = new ExtendedRunOne(newBehaviors);
				
				villagerBehaviour = newRunOne;
			} else if(villagerBehaviour instanceof GateBehavior) {
				GateBehavior<Villager> runOne = (GateBehavior<Villager>) villagerBehaviour;
				
//				if(runOne.behaviors.entries.stream().filter(item1 -> item1.getData() instanceof TradeWithVillager).findFirst().isPresent()) {
//					patchedList.add(Pair.of(3, new ExtendedGateBehavior(
//							ImmutableMap.of(),
//							ImmutableSet.of(Utils.CONTAINER_INTERACTION_TARGET.get()),
//							GateBehavior.OrderPolicy.ORDERED,
//							GateBehavior.RunningPolicy.RUN_ONE,
//							ImmutableList.of(Pair.of(new ContainerInteraction(), 1)))));
//				}
			} else if((Behavior)villagerBehaviour instanceof SetLookAndInteract) {
				//patchedList.add(Pair.of(3, new FindContainer()));
			}
			
			Pair<Integer, Behavior<Villager>> p = Pair.of(priority, villagerBehaviour);
			
			patchedList.add(p);
		});
		
		patchedList.add(Pair.of(1, new FindContainer()));
		
		super.addActivityWithConditions(p_21904_, ImmutableList.copyOf(patchedList), p_21906_);
	}
	
	@Override
	public Brain<Villager> copyWithoutBehaviors() {
		return from(this);
	}
	
	public static Brain<Villager> from(Brain<Villager> villagerBrain) {
		Set<SensorType<? extends Sensor<? super Villager>>> newSensorTypes = new HashSet<>();
		
		boolean[] needToAdd = new boolean[1];
		
		needToAdd[0] = true;
		
		villagerBrain.sensors.keySet().forEach(item -> {
			if(item == NEAREST_CONTAINER_SENSOR) {
				needToAdd[0] = false;
			}
			if(item == SensorType.SECONDARY_POIS) {
				newSensorTypes.add(EXTENDED_SECONDARY_POI_SENSOR);
				
				return;
			}
			
			newSensorTypes.add(item);
		});
		
		if(needToAdd[0]) {
			newSensorTypes.add(NEAREST_CONTAINER_SENSOR);
		}
		
		Brain<Villager> brain = new ExtendedVillagerBrain(villagerBrain.memories.keySet(), newSensorTypes, ImmutableList.of(), villagerBrain.codec);
		
		for(Map.Entry<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> entry : villagerBrain.memories.entrySet()) {
			MemoryModuleType<?> memorymoduletype = entry.getKey();
			
			if(entry.getValue().isPresent()) {
				brain.memories.put(memorymoduletype, entry.getValue());
			}
		}
		
		if(!brain.memories.containsKey(Utils.CONTAINER_INTERACTION_TARGET.get())) {
			brain.memories.put(Utils.CONTAINER_INTERACTION_TARGET.get(), Optional.empty());
		}
		
		if(!brain.memories.containsKey(Utils.NEAREST_CONTAINERS.get())) {
			brain.memories.put(Utils.NEAREST_CONTAINERS.get(), Optional.empty());
		}
		
		return brain;
	}
	
	public boolean checkMemory(MemoryModuleType<?> p_21877_, MemoryStatus p_21878_) {
		Optional<? extends ExpirableValue<?>> optional = this.memories.get(p_21877_);
		
		if(optional == null) {
			return false;
		} else {
			return p_21878_ == MemoryStatus.REGISTERED || p_21878_ == MemoryStatus.VALUE_PRESENT && optional.isPresent() || p_21878_ == MemoryStatus.VALUE_ABSENT && !optional.isPresent();
		}
	}
}
