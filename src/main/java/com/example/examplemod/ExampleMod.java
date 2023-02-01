package com.example.examplemod;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import com.dreamflyer.menhance.behavior.ExtendedVillagerBrain;
import com.dreamflyer.menhance.entity.crawfish.CrawfishRenderer;
import com.dreamflyer.menhance.utils.Utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.event.EntityRenderersEvent.AddLayers;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = Utils.ITEM_REGISTER.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    
    public ExampleMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        Utils.ITEM_REGISTER.register(modEventBus);
        
        Utils.MEMORY_MODULE_TYPE_REGISTER.register(modEventBus);
        Utils.ENTITY_TYPE_REGISTER.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    	
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        HashSet<Item> wantedItems = new HashSet<>();
        
        wantedItems.addAll(Villager.WANTED_ITEMS);
        
        wantedItems.add(Items.COCOA_BEANS);
        wantedItems.add(Items.EGG);
        wantedItems.add(Items.SUGAR_CANE);
        
        Villager.WANTED_ITEMS = wantedItems;
    }
    
    @SubscribeEvent
	public void onAttributeCreation1(EntityAttributeCreationEvent event) {
		event.put(Utils.CRAWFISH.get(), AbstractFish.createAttributes().build());
	}
    
    @SubscribeEvent
	public static void onAttributeCreation(EntityAttributeCreationEvent event) {
		event.put(Utils.CRAWFISH.get(), AbstractFish.createAttributes().build());
	}
    
    @Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
    public static class ForgeModEvents1 {
    	@SubscribeEvent
    	public static void onAttributeCreation(EntityAttributeCreationEvent event) {
    		event.put(Utils.CRAWFISH.get(), AbstractFish.createAttributes().build());
    	}
    	
    	@SubscribeEvent
    	public static void onAddLayers(AddLayers event) {
    		//event.
    		//event.put(Utils.CRAWFISH.get(), AbstractFish.createAttributes().build());
    	}
    	
    	@SubscribeEvent
    	public static void onRegisterRenderers(RegisterRenderers event) {
    		event.registerEntityRenderer(Utils.CRAWFISH.get(), CrawfishRenderer::new);
    		//EntityRenderers.register(Utils.CRAWFISH.get(), CrawfishRenderer::new);
    		//event.put(Utils.CRAWFISH.get(), AbstractFish.createAttributes().build());
    	}
    }
    
    @Mod.EventBusSubscriber(modid = MODID)
    public static class ForgeModEvents {
    	@SubscribeEvent
    	public static void onAttributeCreation(EntityAttributeCreationEvent event) {
    		event.put(Utils.CRAWFISH.get(), AbstractFish.createAttributes().build());
    	}
    	
    	@SubscribeEvent
    	public void onAttributeCreation1(EntityAttributeCreationEvent event) {
    		event.put(Utils.CRAWFISH.get(), AbstractFish.createAttributes().build());
    	}
    	
    	@SubscribeEvent
        public static void onJoin(EntityConstructing event) {
    		if(event.getEntity() instanceof Villager) {
    			Villager villager = (Villager) event.getEntity();
    			
    			if(villager.level instanceof ServerLevel) {
    				Timer t = new Timer();
        			
        			t.schedule(new TimerTask() {
        				@Override
						public void run() {
        					ServerLevel level = (ServerLevel) villager.level;
        					
        					if(villager.brain == null || (villager.brain instanceof ExtendedVillagerBrain)) {
        						return;
        					}
        					
        					t.cancel();
        					
            				villager.brain = ExtendedVillagerBrain.from((Brain<Villager>) villager.brain);
                			
                			villager.refreshBrain(level);
						}}, 100, 100);
    			}
    		}
        }
    }
}
