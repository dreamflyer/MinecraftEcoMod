package com.dreamflyer.menhance.behavior;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.dreamflyer.menhance.sensor.ExtendedSecondaryJobSensor;
import com.dreamflyer.menhance.utils.Utils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class FisherBehavior extends Behavior<Villager> {
	public static final float SPEED_MODIFIER = 0.5F;
	
	@Nullable
	private BlockPos fishingPlace;
	
	private long nextOkStartTime;
	
	private int timeWorkedSoFar;
	
	private final List<BlockPos> validCoastAroundVillager = Lists.newArrayList();
	
	public FisherBehavior() {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
	}
	
	protected boolean checkExtraStartConditions(ServerLevel level, Villager villager) {
		if(!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, villager)) {
			return false;
		} else if(villager.getVillagerData().getProfession() != VillagerProfession.FISHERMAN) {
			return false;
		} else {
			this.validCoastAroundVillager.clear();
			
			tryAddNearestPositions(villager, position -> isValidPos(level, position), validCoastAroundVillager);
			
			this.fishingPlace = this.getValidFishingPlace(level);
			
			return this.fishingPlace != null;
		}
	}
	
	private static void tryAddNearestPositions(Villager villager, Predicate<BlockPos> predicate, List<BlockPos> resultList) {
		BlockPos.MutableBlockPos blockpos$mutableblockpos = villager.blockPosition().mutable();
		
		for(int i = -1; i <= 1; ++i) {
			for(int j = -1; j <= 1; ++j) {
				for(int k = -1; k <= 1; ++k) {
					if(predicate.test(blockpos$mutableblockpos.set(villager.getX() + (double)i, villager.getY() + (double)j, villager.getZ() + (double)k))) {
						resultList.add(new BlockPos(blockpos$mutableblockpos));
					}
				}
			}
		}
	}
	
	@Nullable
	private BlockPos getValidFishingPlace(ServerLevel p_23165_) {
		return this.validCoastAroundVillager.isEmpty() ? null : this.validCoastAroundVillager.get(p_23165_.getRandom().nextInt(this.validCoastAroundVillager.size()));
	}
	
	protected void start(ServerLevel level, Villager villager, long time) {
		if(time > this.nextOkStartTime && this.fishingPlace != null) {
			villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.fishingPlace));
			
			villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.fishingPlace), 0.5F, 1));
		}
	}
	
	protected void tick(ServerLevel level, Villager villager, long time) {
		if(isNearJobOrNothingToDo(villager)) {
			if(canWork(time)) {
				boolean isValidFishingPlace = ExtendedSecondaryJobSensor.isWaterNearSolidUnderAllowed(level, fishingPlace);
				
				if(isValidFishingPlace && isEnoughFishForFishing()) {
					doFishing();
		        }
				
				if(isValidFishingPlace && !isEnoughFishForBreeding(villager) && hasFishBait(villager)) {
					doBreed(level, villager);
	            }

	            if(isValidFishingPlace && !isEnoughFishForFishing()) {
	            	goToNextJob(level, villager, time);
	            }
			}
			
			++this.timeWorkedSoFar;
		}
	}
	
	private void doFishing() {
		
	}
	
	private static long countFish(Villager villager) {
		Optional<List<LivingEntity>> optional = villager.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
		
		if(!optional.isPresent()) {
			return 0;
		}
		
		return optional.get().stream().filter(item -> {
			return item instanceof Cod;
		}).count(); 
	}
	
	private static boolean isEnoughFishForBreeding(Villager villager) {
		if(countFish(villager) < 30) {
			return false;
		}
		
		return true;
	}
	
	private static boolean isEnoughFishForFishing() {
		return false;
	}
	
	private boolean isValidPos(ServerLevel level, BlockPos position) {
	      return ExtendedSecondaryJobSensor.isWaterNearSolidUnderAllowed(level, position);
	   }
	
	private boolean isNearJobOrNothingToDo(Villager villager) {
		return this.fishingPlace == null || this.fishingPlace.closerToCenterThan(villager.position(), 2.0D);
	}
	
	private boolean canWork(long time) {
		return this.fishingPlace != null && time > this.nextOkStartTime;
	}
	
	protected void stop(ServerLevel level, Villager villager, long time) {
		villager.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
		villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
		
		this.timeWorkedSoFar = 0;
		
		this.nextOkStartTime = time + 40L;
	}
	
	private void doBreed(ServerLevel level, Villager villager) {
		//SimpleContainer simplecontainer = villager.getInventory();
		BlockState state = Utils.posToState(level, fishingPlace);
		Block block = Utils.posToBlock(level, fishingPlace);
		
		
		
		Entity cod = new Cod(EntityType.COD, level);
		
		//Cod cod = (Cod) EntityType.COD.spawn(level, null, (Player)null, fishingPlace, MobSpawnType.SPAWN_EGG, false, false);
		
		if(cod != null) {
			cod.setPos((double)this.fishingPlace.getX() + 0.5d, (double)this.fishingPlace.getY() + 0.2d, (double)this.fishingPlace.getZ() + 1.7d);
			
			level.addFreshEntity(cod);
			
			level.gameEvent(villager, GameEvent.ENTITY_PLACE, this.fishingPlace);
			
			level.playSound((Player)null, (double)this.fishingPlace.getX(), (double)this.fishingPlace.getY(), (double)this.fishingPlace.getZ(), SoundEvents.FISH_SWIM, SoundSource.NEUTRAL, 1.0F, 1.0F);
		};
		
//		for(int i = 0; i < simplecontainer.getContainerSize(); ++i) {
//			ItemStack itemstack = simplecontainer.getItem(i);
//			
//			boolean flag = false;
//			
//			if(!itemstack.isEmpty()) {
//				if(itemstack.is(Items.COCOA_BEANS)) {
//					BlockState blockstate1 = Blocks.COCOA.defaultBlockState();
//					
//					level.setBlockAndUpdate(this.fishingPlace, blockstate1);
//					level.gameEvent(GameEvent.BLOCK_PLACE, this.fishingPlace, GameEvent.Context.of(villager, blockstate1));
//					
//					flag = true;
//				}
//			}
//			
//			if(flag) {
//				level.playSound((Player)null, (double)this.fishingPlace.getX(), (double)this.fishingPlace.getY(), (double)this.fishingPlace.getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
//				
//				itemstack.shrink(1);
//				
//				if(itemstack.isEmpty()) {
//					simplecontainer.setItem(i, ItemStack.EMPTY);
//				}
//				
//				break;
//			}
//		}
	}
	
	private void goToNextJob(ServerLevel level, Villager villager, long time) {
		this.validCoastAroundVillager.remove(this.fishingPlace);
    	
    	this.fishingPlace = this.getValidFishingPlace(level);
    	
    	if(this.fishingPlace != null) {
    		this.nextOkStartTime = time + 20L;
    		
    		villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.fishingPlace), 0.5F, 1));
    		villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.fishingPlace));
    	}
	}
	
	public static boolean hasFishBait(Villager villager) {
		   //return villager.getInventory().hasAnyOf(ImmutableSet.of(Items.COCOA_BEANS));
		return true;
	}
	
	protected boolean canStillUse(ServerLevel p_23204_, Villager p_23205_, long p_23206_) {
		return this.timeWorkedSoFar < 200;
	}
	
	private static boolean isMaxAge(BlockState blockstate) {
		return blockstate.getValue(BlockStateProperties.AGE_2) >= 2;
	}
}
