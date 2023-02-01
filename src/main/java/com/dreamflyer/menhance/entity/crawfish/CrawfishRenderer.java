package com.dreamflyer.menhance.entity.crawfish;

import com.dreamflyer.model.buildable.BuildableModel;
import com.dreamflyer.model.buildable.BuildableModelDescription.MeshNode;
import com.dreamflyer.model.buildable.BuildableModelLoader;
import com.example.examplemod.ExampleMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrawfishRenderer extends MobRenderer<Crawfish, BuildableModel<Crawfish>> {
	private static final ResourceLocation CRAWFISH_LOCATION_TEXTURE = new ResourceLocation(ExampleMod.MODID, "textures/entity/fish/crawfish/crawfish.png");
	private static final ResourceLocation CRAWFISH_LOCATION_MODEL = new ResourceLocation(ExampleMod.MODID, "models/entity/fish/crawfish/crawfish.json");
	
	//private static final LayerDefinition layerDefinition = CrawfishModel.createBodyLayer();
	
	private static final MeshNode MESH_DEFENITION = BuildableModelLoader.load(CRAWFISH_LOCATION_MODEL);
	
	public CrawfishRenderer(EntityRendererProvider.Context p_173954_) {
		super(p_173954_, new BuildableModel<Crawfish>(MESH_DEFENITION), 0.3F);
		
		//super(p_173954_, new BuildableModel<>(layerDefinition.bakeRoot()), 0.3F);
		
		//CrawfishModel.tryLoadModel(p_173954_);
	}
	
	public ResourceLocation getTextureLocation(Crawfish p_114015_) {
		return CRAWFISH_LOCATION_TEXTURE;
	}
	
	protected void setupRotations(Crawfish crawfish, PoseStack p_114018_, float p_114019_, float p_114020_, float p_114021_) {
		if (this.isShaking(crawfish)) {
			p_114020_ += (float)(Math.cos((double)crawfish.tickCount * 3.25D) * Math.PI * (double)0.4F);
	      }

	      if (!crawfish.hasPose(Pose.SLEEPING)) {
	    	  p_114018_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_114020_));
	      }

	      if (crawfish.deathTime > 0) {
	         float f = ((float)crawfish.deathTime + p_114021_ - 1.0F) / 20.0F * 1.6F;
	         f = Mth.sqrt(f);
	         if (f > 1.0F) {
	            f = 1.0F;
	         }

	         p_114018_.mulPose(Vector3f.ZP.rotationDegrees(f * this.getFlipDegrees(crawfish)));
	      } else if (crawfish.isAutoSpinAttack()) {
	    	  p_114018_.mulPose(Vector3f.XP.rotationDegrees(-90.0F - crawfish.getXRot()));
	    	  p_114018_.mulPose(Vector3f.YP.rotationDegrees(((float)crawfish.tickCount + p_114021_) * -75.0F));
	      } else if (crawfish.hasPose(Pose.SLEEPING)) {
	         Direction direction = crawfish.getBedOrientation();
	         float f1 = direction != null ? sleepDirectionToRotation(direction) : p_114020_;
	         p_114018_.mulPose(Vector3f.YP.rotationDegrees(f1));
	         p_114018_.mulPose(Vector3f.ZP.rotationDegrees(this.getFlipDegrees(crawfish)));
	         p_114018_.mulPose(Vector3f.YP.rotationDegrees(270.0F));
	      } else if (isEntityUpsideDown(crawfish)) {
	    	  p_114018_.translate(0.0D, (double)(crawfish.getBbHeight() + 0.1F), 0.0D);
	    	  p_114018_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
	      }
		//super.setupRotations(p_114017_, p_114018_, p_114019_, p_114020_, p_114021_);
		
		float f = 4.3F * Mth.sin(0.6F * p_114019_);
		
		p_114018_.mulPose(Vector3f.YP.rotationDegrees(f));
		
		if(!crawfish.isInWater()) {
			p_114018_.translate((double)0.1F, (double)0.1F, (double)-0.1F);
			
			p_114018_.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
		}
	}
	
	private static float sleepDirectionToRotation(Direction p_115329_) {
	      switch (p_115329_) {
	         case SOUTH:
	            return 90.0F;
	         case WEST:
	            return 0.0F;
	         case NORTH:
	            return 270.0F;
	         case EAST:
	            return 180.0F;
	         default:
	            return 0.0F;
	      }
	   }
}