package com.dreamflyer.menhance.behavior;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;

public class ExtendedRunOne extends RunOne<Villager> {
	public ExtendedRunOne(Map<MemoryModuleType<?>, MemoryStatus> p_23834_,
			List<Pair<Behavior<? super Villager>, Integer>> p_23835_) {
		super(p_23834_, p_23835_);
	}

	public ExtendedRunOne(List<Pair<Behavior<? super Villager>, Integer>> p_23832_) {
		super(p_23832_);
	}
	
	public String toString() {
		Set<String> set = this.behaviors.stream().map((p_22890_) -> {
			return "[" + p_22890_ + " -> " + (p_22890_.getStatus() == Behavior.Status.RUNNING ? "R" : "I") + "]";
		}).collect(Collectors.toSet());
		
		return "(" + this.getClass().getSimpleName() + "): " + set;
	}
	
	protected void start(ServerLevel p_22881_, Villager p_22882_, long p_22883_) {
		this.orderPolicy.apply(this.behaviors);
		this.getExtendedRunningPolicy().apply(this.behaviors.stream(), p_22881_, p_22882_, p_22883_);
	}
	
	private ExtendedRunningPolicy getExtendedRunningPolicy() {
		if(this.runningPolicy == RunningPolicy.RUN_ONE) {
			return ExtendedRunningPolicy.RUN_ONE;
		}
		
		return ExtendedRunningPolicy.TRY_ALL;
	}
	
	static <E extends LivingEntity> boolean tryStartBehaviour(Behavior<? super E> behavior, ServerLevel level, LivingEntity entity, long longParam, int entityId) {
		return behavior.tryStart(level, (E) entity, longParam);
	}

	public static enum ExtendedRunningPolicy {
		RUN_ONE {
			public <E extends LivingEntity> void apply(Stream<Behavior<? super E>> p_147537_, ServerLevel p_147538_, E p_147539_, long p_147540_) {
				p_147537_.filter((p_22965_) -> {
					return p_22965_.getStatus() == Behavior.Status.STOPPED;
				}).filter((p_22963_) -> {
					return tryStartBehaviour((Behavior<LivingEntity>) p_22963_, p_147538_, p_147539_, p_147540_, p_147539_.getId());
				}).findFirst();
	       }
	    },
	    TRY_ALL {
	       public <E extends LivingEntity> void apply(Stream<Behavior<? super E>> p_147542_, ServerLevel p_147543_, E p_147544_, long p_147545_) {
	          p_147542_.filter((p_22980_) -> {
	             return p_22980_.getStatus() == Behavior.Status.STOPPED;
	          }).forEach((p_22978_) -> {
	             p_22978_.tryStart(p_147543_, p_147544_, p_147545_);
	          });
	       }
	    };

	    public abstract <E extends LivingEntity> void apply(Stream<Behavior<? super E>> p_147532_, ServerLevel p_147533_, E p_147534_, long p_147535_);
	}
}