package com.dreamflyer.menhance.behavior;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class HarvestShugarCane extends Behavior<Villager> {
	private static final int HARVEST_DURATION = 200;
	
	public static final float SPEED_MODIFIER = 0.5F;
	
	@Nullable
	private BlockPos nearJungleLogPos;
	private long nextOkStartTime;
	private int timeWorkedSoFar;
	private final List<BlockPos> validJungleLogAroundVillager = Lists.newArrayList();
	
	public HarvestShugarCane() {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryStatus.VALUE_PRESENT));
	}
	
	protected boolean checkExtraStartConditions(ServerLevel p_23174_, Villager p_23175_) {
		if(!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(p_23174_, p_23175_)) {
			return false;
		} else if (p_23175_.getVillagerData().getProfession() != VillagerProfession.FARMER) {
			return false;
		} else {
			BlockPos.MutableBlockPos blockpos$mutableblockpos = p_23175_.blockPosition().mutable();
			
			this.validJungleLogAroundVillager.clear();
			
			for(int i = -1; i <= 1; ++i) {
				for(int j = -1; j <= 1; ++j) {
					for(int k = -1; k <= 1; ++k) {
						blockpos$mutableblockpos.set(p_23175_.getX() + (double)i, p_23175_.getY() + (double)j, p_23175_.getZ() + (double)k);
						
						if(this.validPos(blockpos$mutableblockpos, p_23174_)) {
							this.validJungleLogAroundVillager.add(new BlockPos(blockpos$mutableblockpos));
						}
					}
	            }
			}
			
			this.nearJungleLogPos = this.getValidJungleLog(p_23174_);
			
			return this.nearJungleLogPos != null;
		}
	}
	
	@Nullable
	private BlockPos getValidJungleLog(ServerLevel p_23165_) {
		return this.validJungleLogAroundVillager.isEmpty() ? null : this.validJungleLogAroundVillager.get(p_23165_.getRandom().nextInt(this.validJungleLogAroundVillager.size()));
	}
	
	
	private boolean validPos(BlockPos p_23181_, ServerLevel p_23182_) {
		BlockState blockstate = p_23182_.getBlockState(p_23181_);
		
		Block block = blockstate.getBlock();
		
		return (block == Blocks.SUGAR_CANE) && canHarvest(blockstate, p_23181_, p_23182_) || blockstate.isAir() && isUnderSoilNearWater(p_23181_, p_23182_);
	}
	
	
	private static boolean isUnderSoilNearWater(BlockPos pos, ServerLevel level) {
		if(!isSoil(level.getBlockState(pos.below()).getBlock())) {
			return false;
		}
		
		if(isNearWater(level, pos.below())) {
			return true;
		}
		
//		if(isWater(level.getBlockState(pos.below().north()).getBlock())) {
//			return true;
//		}
//		
//		if(isWater(level.getBlockState(pos.below().south()).getBlock())) {
//			return true;
//		}
//		
//		if(isWater(level.getBlockState(pos.below().west()).getBlock())) {
//			return true;
//		}
//		
//		if(isWater(level.getBlockState(pos.below().east()).getBlock())) {
//			return true;
//		}
		
		return false;
	}
	
	private static boolean isWater(Block block) {
		if(block == Blocks.WATER) {
			return true;
		}
		
		return false;
	}
	
	private static boolean isSoil(Block block) {
		if(block == Blocks.GRASS_BLOCK) {
			return true;
		}
		
		if(block == Blocks.DIRT) {
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
	
	protected void start(ServerLevel p_23177_, Villager villager, long p_23179_) {
		if(p_23179_ > this.nextOkStartTime && this.nearJungleLogPos != null) {
			villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.nearJungleLogPos));
			villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.nearJungleLogPos), 0.5F, 1));
		}
	}
	
	protected void stop(ServerLevel p_23188_, Villager p_23189_, long p_23190_) {
		p_23189_.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
		p_23189_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
		
		this.timeWorkedSoFar = 0;
		this.nextOkStartTime = p_23190_ + 40L;
	}
	
	protected void tick(ServerLevel level, Villager villager, long time) {
		if(this.nearJungleLogPos == null || this.nearJungleLogPos.closerToCenterThan(villager.position(), 1.5D)) {
			if(this.nearJungleLogPos != null && time > this.nextOkStartTime) {
				BlockState blockstate = level.getBlockState(this.nearJungleLogPos);
	            Block block = blockstate.getBlock();
	            
	            if(block instanceof SugarCaneBlock && canHarvest(blockstate, nearJungleLogPos, level)) {
	            	level.destroyBlock(this.nearJungleLogPos, true, villager);
	            }

	            if(blockstate.isAir() && isUnderSoilNearWater(this.nearJungleLogPos, level) && hasFarmSeeds(villager)) {
	            	SimpleContainer simplecontainer = villager.getInventory();
	            	
	            	for(int i = 0; i < simplecontainer.getContainerSize(); ++i) {
	            		ItemStack itemstack = simplecontainer.getItem(i);
	            		
	            		boolean flag = false;
	            		
	            		if(!itemstack.isEmpty()) {
	            			if(itemstack.is(Items.SUGAR_CANE)) {
	            				BlockState blockstate1 = Blocks.SUGAR_CANE.defaultBlockState();
	            				
	            				level.setBlockAndUpdate(this.nearJungleLogPos, blockstate1);
	            				
	            				level.gameEvent(GameEvent.BLOCK_PLACE, this.nearJungleLogPos, GameEvent.Context.of(villager, blockstate1));
	            				
	            				flag = true;
	            			}
	            		}
	            		
	            		if(flag) {
	            			level.playSound((Player)null, (double)this.nearJungleLogPos.getX(), (double)this.nearJungleLogPos.getY(), (double)this.nearJungleLogPos.getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
	            			itemstack.shrink(1);
	            			
	            			if(itemstack.isEmpty()) {
	            				simplecontainer.setItem(i, ItemStack.EMPTY);
	            			}
	            			
	            			break;
	            		}
	            	}
	            }
	            
	            if(block == Blocks.SUGAR_CANE && !canHarvest(blockstate, this.nearJungleLogPos, level)) {
	            	this.validJungleLogAroundVillager.remove(this.nearJungleLogPos);
	            	
	            	this.nearJungleLogPos = this.getValidJungleLog(level);
	            	
	            	if(this.nearJungleLogPos != null) {
	            		this.nextOkStartTime = time + 20L;
	            		
	            		villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.nearJungleLogPos), 0.5F, 1));
	            		villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.nearJungleLogPos));
	            	}
	            }
			}

	         ++this.timeWorkedSoFar;
	      }
	   }
	   
	   public static boolean hasFarmSeeds(Villager villager) {
		   return villager.getInventory().hasAnyOf(ImmutableSet.of(Items.SUGAR_CANE));
	   }

	   protected boolean canStillUse(ServerLevel p_23204_, Villager p_23205_, long p_23206_) {
	      return this.timeWorkedSoFar < 200;
	   }
	   
	   private static boolean canHarvest(BlockState blockstate, BlockPos blockPosition, ServerLevel level) {
		   boolean isPreviousMaxAge = isMaxAge(blockPosition, level);
				   
		   BlockPos currentPosition = blockPosition.above();
		   BlockState currentState = level.getBlockState(currentPosition);
		   
		   if(currentState.getBlock() != Blocks.SUGAR_CANE) {
			   return !currentState.isAir() && isPreviousMaxAge;
		   };
		   
		   isPreviousMaxAge = isMaxAge(currentPosition, level);
		   
		   currentPosition = currentPosition.above();
		   currentState = level.getBlockState(currentPosition);
		   
		   if(currentState.getBlock() != Blocks.SUGAR_CANE) {
			   return !currentState.isAir() && isPreviousMaxAge;
		   };
		   
		   return isMaxAge(currentPosition, level);
	   }
	   
	   private static boolean isMaxAge(BlockPos blockPosition, ServerLevel level) {
		   Map result = level.getBlockState(blockPosition).getValues();
		   
		   return true;
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
}
