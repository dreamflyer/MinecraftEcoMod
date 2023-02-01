package com.dreamflyer.menhance.entity.crawfish;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrawfishModel<T extends Entity> extends HierarchicalModel<T> {
   private final ModelPart root;
   
   private final ModelPart head;
   
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   
   private final ModelPart rightHindLeg1;
   private final ModelPart leftHindLeg1;
   
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   
   private final ModelPart test0;
   private final ModelPart test1;
   private final ModelPart test2;
   private final ModelPart test3;
   
   private final ModelPart test4;
   private final ModelPart test5;
   private final ModelPart test6;
   private final ModelPart test7;

   public CrawfishModel(ModelPart p_170494_) {
      this.root = p_170494_;
      
      this.head = p_170494_.getChild("head");
      
      this.test0 = p_170494_.getChild("test0");
      this.test1 = p_170494_.getChild("test1");
      this.test2 = p_170494_.getChild("test2");
      this.test3 = p_170494_.getChild("test3");
      this.test4 = p_170494_.getChild("test4");
      this.test5 = p_170494_.getChild("test5");
      this.test6 = p_170494_.getChild("test6");
      this.test7 = p_170494_.getChild("test7");
      
      this.rightHindLeg = p_170494_.getChild("right_hind_leg");
      this.leftHindLeg = p_170494_.getChild("left_hind_leg");
      
      this.rightHindLeg1 = this.rightHindLeg.getChild("right_hind_leg1");
      this.leftHindLeg1 = this.leftHindLeg.getChild("left_hind_leg1");
      
      this.rightFrontLeg = p_170494_.getChild("right_front_leg");
      this.leftFrontLeg = p_170494_.getChild("left_front_leg");
   }
   
   public static void debugCube(PartDefinition partdefinition, String name, float centerX, float centerY, float centerZ) {
	   float u = 3;
	   float v = 6;
	   float w = 9;
	   
	   partdefinition.addOrReplaceChild(name, CubeListBuilder.create().texOffs(0, 0).addBox(centerX - u/2.0f, centerY - v/2.0f, centerZ - w/2.0f, centerX + u/2.0f, centerY + v/2.0f, centerZ + w/2.0f, CubeDeformation.NONE).mirror(), PartPose.ZERO);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      
      
      
//      int i = 22;
//      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 4.0F, 7.0F), PartPose.offset(0.0F, 22.0F, 0.0F));
//      partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(11, 0).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F), PartPose.offset(0.0F, 22.0F, 0.0F));
//      partdefinition.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F), PartPose.offset(0.0F, 22.0F, -3.0F));
//      partdefinition.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(22, 1).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F), PartPose.offsetAndRotation(-1.0F, 23.0F, 0.0F, 0.0F, 0.0F, (-(float)Math.PI / 4F)));
//      partdefinition.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(22, 4).addBox(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F), PartPose.offsetAndRotation(1.0F, 23.0F, 0.0F, 0.0F, 0.0F, ((float)Math.PI / 4F)));
//      partdefinition.addOrReplaceChild("tail_fin", CubeListBuilder.create().texOffs(22, 3).addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 4.0F), PartPose.offset(0.0F, 22.0F, 7.0F));
      //partdefinition.addOrReplaceChild("top_fin", CubeListBuilder.create().texOffs(20, -6).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 6.0F), PartPose.offset(0.0F, 20.0F, 0.0F));
      int param = 6;
      
      //partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, (float)(18 - param), -6.0F));
      
      debugCube(partdefinition, "test0", 5, 5, 5);
      debugCube(partdefinition, "test1", 5, 5, -5);
      debugCube(partdefinition, "test2", -5, 5, -5);
      debugCube(partdefinition, "test3", -5, 5, 5);
      
      debugCube(partdefinition, "test4", 5, -5, 5);
      debugCube(partdefinition, "test5", 5, -5, -5);
      debugCube(partdefinition, "test6", -5, -5, -5);
      debugCube(partdefinition, "test7", -5, -5, 5);
      
      partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE).texOffs(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 12.0F, -6.0F));
      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, (float)(17 - param), 2.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
      
      CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)param, 4.0F, CubeDeformation.NONE);
      
      PartDefinition pdRight = partdefinition.addOrReplaceChild("right_hind_leg", cubelistbuilder, PartPose.offset(-3.0F, (float)(24 - param), 7.0F));
      PartDefinition pdLeft = partdefinition.addOrReplaceChild("left_hind_leg", cubelistbuilder, PartPose.offset(3.0F, (float)(24 - param), 7.0F));
      
      CubeListBuilder bbb = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)param, 4.0F, CubeDeformation.NONE);
      
      pdRight.addOrReplaceChild("right_hind_leg1", bbb, PartPose.offset(0, 30.0f, 0));
      pdLeft.addOrReplaceChild("left_hind_leg1", bbb, PartPose.offset(0, 30.0f, 0));
      
      partdefinition.addOrReplaceChild("right_front_leg", cubelistbuilder, PartPose.offset(-3.0F, (float)(24 - param), -5.0F));
      partdefinition.addOrReplaceChild("left_front_leg", cubelistbuilder, PartPose.offset(3.0F, (float)(24 - param), -5.0F));
      
      return LayerDefinition.create(meshdefinition, 32, 64);
   }
   
   public ModelPart root() {
      return this.root;
   }

//   public void setupAnim(T p_102409_, float p_102410_, float p_102411_, float p_102412_, float p_102413_, float p_102414_) {
//      float f = 1.0F;
//      if (!p_102409_.isInWater()) {
//         f = 1.5F;
//      }
//
//      this.tailFin.yRot = -f * 0.45F * Mth.sin(0.6F * p_102412_);
//   }
//   
   
   //public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   
//   public void setupAnim(T p_233372_, float p_233373_, float p_233374_, float p_233375_, float p_233376_, float p_233377_) {
//	      this.root().getAllParts().forEach(ModelPart::resetPose);
//	      float f = Math.min((float)p_233372_.getDeltaMovement().lengthSqr() * 200.0F, 8.0F);
//	      this.animate(p_233372_.jumpAnimationState, FrogAnimation.FROG_JUMP, p_233375_);
//	      this.animate(p_233372_.croakAnimationState, FrogAnimation.FROG_CROAK, p_233375_);
//	      this.animate(p_233372_.tongueAnimationState, FrogAnimation.FROG_TONGUE, p_233375_);
//	      this.animate(p_233372_.walkAnimationState, FrogAnimation.FROG_WALK, p_233375_, f);
//	      this.animate(p_233372_.swimAnimationState, FrogAnimation.FROG_SWIM, p_233375_);
//	      this.animate(p_233372_.swimIdleAnimationState, FrogAnimation.FROG_IDLE_WATER, p_233375_);
//	      this.croakingBody.visible = p_233372_.croakAnimationState.isStarted();
//	   }
   
   public void setupAnim(T p_103509_, float frame, float ampl, float p_103512_, float headPitch, float headYaw) {
	   this.head.xRot = headPitch * ((float)Math.PI / 180F);
	   this.head.yRot =  headYaw * ((float)Math.PI / 180F);
	   
	   this.rightHindLeg.xRot = Mth.cos(frame * 0.6662F) * 1.4F * 1;
	   this.leftHindLeg.xRot = Mth.cos(frame * 0.6662F + (float)Math.PI) * 1.4F * 1;
	   
	   this.rightHindLeg1.xRot = Mth.cos(1.0f * 0.6662F) * 1.4F * 1;
	   this.leftHindLeg1.xRot = Mth.cos(1.0f * 0.6662F + (float)Math.PI) * 1.4F * 1;
	   
	   this.rightFrontLeg.xRot = Mth.cos(frame * 0.6662F + (float)Math.PI) * 1.4F * 1;
	   this.leftFrontLeg.xRot = Mth.cos(frame * 0.6662F) * 1.4F * 1;
   }
   
   
}
