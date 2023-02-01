package com.dreamflyer.menhance.utils;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.Container;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ContainerUtils {
	public static Set<Item> getVillagerItems(Container container) {
		Set<Item> items = new HashSet<>();
		
		int size = container.getContainerSize();
		
		for(int i = 0; i < size; i++) {
			ItemStack stack = container.getItem(i);
			
			if(stack.isEmpty()) {
				continue;
			}
			
			if(Villager.FOOD_POINTS.keySet().contains(stack.getItem())) {
				continue;
			}
			
			if(container.countItem(stack.getItem()) < 5) {
				continue;
			}
			
			items.add(stack.getItem());
		}
		
		return items;
	}
	
	public static boolean canTransferOneTo(Item item, Container to) {
		if(to.hasAnyMatching(itemStack -> itemStack.is(item) &&  (itemStack.getCount() < itemStack.getMaxStackSize()))) {
			return true;
		}
		
		return to.hasAnyMatching(itemStack -> itemStack.isEmpty());
	}
	
	public static ItemStack getVillagerItemStack(Container from, Container to) {
		Set<Item> villagerItems = getVillagerItems(from);
		
		for(Item item: villagerItems) {
			ItemStack found = findSmallestStack(item, from);
			
			if(found == null) {
				continue;
			}
			
			if(!canTransferOneTo(found.getItem(), to)) {
				continue;
			}
			
			return found;
		}
		
		return null;
	}
	
	
	public static ItemStack findSmallestStack(Item item, Container container) {
		ItemStack stack = null;
		
		for(int i = 0; i < container.getContainerSize(); i++) {
			if(!container.getItem(i).is(item)) {
				continue;
			}
			
			if(stack == null) {
				stack = container.getItem(i);
				
				continue;
			}
			
			if(stack.getCount() > container.getItem(i).getCount()) {
				stack = container.getItem(i);
			}
		}
		
		return stack;
	}
	
	public static ItemStack findDestinationStack(Item item, Container container) {
		ItemStack stack = null;
		
		Integer empty = null;
		
		for(int i = 0; i < container.getContainerSize(); i++) {
			if(empty == null && container.getItem(i).isEmpty()) {
				empty = i;
			}
			
			if(!container.getItem(i).is(item)) {
				continue;
			}
			
			if(container.getItem(i).getCount() >= container.getItem(i).getMaxStackSize()) {
				continue;
			}
			
			if(stack == null) {
				stack = container.getItem(i);
				
				continue;
			}
			
			if(stack.getCount() < container.getItem(i).getCount()) {
				stack = container.getItem(i);
			}
		}
		
		if(stack ==null && empty != null) {
			stack = new ItemStack(item);
			
			container.setItem(empty, stack);
		}
		
		return stack;
	}
	
	public static void decreaseStack(Container container, ItemStack stack) {
		stack.shrink(1);
		
		if(stack.isEmpty()) {
			for(int i = 0; i < container.getContainerSize(); i++) {
				if(container.getItem(i) == stack) {
					container.setItem(i, ItemStack.EMPTY);
				}
			}
		}
	}
}
