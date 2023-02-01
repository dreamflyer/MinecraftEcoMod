package com.dreamflyer.menhance.behavior;

import java.util.Set;

import com.dreamflyer.menhance.utils.ContainerUtils;
import com.dreamflyer.menhance.utils.Utils;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class ContainerInteraction extends Behavior<Villager> {
	ContainerInteraction() {
		super(ImmutableMap.of(Utils.CONTAINER_INTERACTION_TARGET.get(), MemoryStatus.VALUE_PRESENT, Utils.NEAREST_CONTAINERS.get(), MemoryStatus.VALUE_PRESENT));
	}
	
	protected boolean checkExtraStartConditions(ServerLevel level, Villager villager) {
		return villager.getBrain().getMemory(Utils.CONTAINER_INTERACTION_TARGET.get()).isPresent();
	}
	
	protected boolean canStillUse(ServerLevel p_24419_, Villager p_24420_, long p_24421_) {
		return this.checkExtraStartConditions(p_24419_, p_24420_);
	}
	
	protected void start(ServerLevel p_24437_, Villager villager, long p_24439_) {
		MemoryModuleType<BlockPos> mem = Utils.CONTAINER_INTERACTION_TARGET.get();
		
		BlockPos chestPos = villager.getBrain().getMemory(mem).get();
		
		villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(chestPos), 0.5F, 1));
	}
	
	private boolean doInteract(ServerLevel level, BlockPos pos, Villager villager) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		
		boolean success = false;
		
		if(blockEntity instanceof ChestBlockEntity) {
			ChestBlockEntity chestBlockEntity = (ChestBlockEntity) blockEntity;
			
			SimpleContainer villagerContainer = villager.getInventory();
			
			while(hasTransfer(villagerContainer, chestBlockEntity)) {
				ItemStack src = ContainerUtils.getVillagerItemStack(villagerContainer, chestBlockEntity);
				
				ItemStack dst = ContainerUtils.findDestinationStack(src.getItem(), chestBlockEntity);
				
				
				if(src == null || dst == null) {
					break;
				}
				
				ContainerUtils.decreaseStack(villagerContainer, src);
				
				dst.grow(1);
				
				success = true;
			}
			
			if(success) {
				chestBlockEntity.setChanged();
				villagerContainer.setChanged();
			}
		}
		
		return success;
	}
	
	private static boolean hasTransfer(Container from, Container to) {
		Set<Item> villagerItems = ContainerUtils.getVillagerItems(from);
		
		return villagerItems.stream().anyMatch(item -> ContainerUtils.canTransferOneTo(item, to));
	}
	
	protected void tick(ServerLevel p_24445_, Villager villager, long p_24447_) {
		MemoryModuleType<BlockPos> mem = Utils.CONTAINER_INTERACTION_TARGET.get();
		
		BlockPos chestPos = villager.getBrain().getMemory(mem).get();
		
		if(chestPos.closerToCenterThan(villager.position(), 1.5D)) {
			if(doInteract(p_24445_, chestPos, villager)) {
				p_24445_.playSound((Player)null, (double)chestPos.getX(), (double)chestPos.getY(), (double)chestPos.getZ(), SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
			};
		}
	}
	
	
	protected void stop(ServerLevel p_24453_, Villager p_24454_, long p_24455_) {
		p_24454_.getBrain().eraseMemory(Utils.CONTAINER_INTERACTION_TARGET.get());
	}
}