package com.dreamflyer.model.buildable;

import java.util.HashMap;
import java.util.function.BiFunction;

import com.dreamflyer.model.buildable.BuildableModelDescription.Animation;
import com.dreamflyer.model.buildable.BuildableModelDescription.MeshItem;
import com.dreamflyer.model.buildable.BuildableModelDescription.MeshNode;
import com.dreamflyer.model.buildable.BuildableModelDescription.Position;
import com.mojang.math.Vector3f;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;

public class BuildableModel<E extends Entity> extends HierarchicalModel<E> {
	private final MeshNode modelDescription;
	
	private final ModelPart root;
	
	private final HashMap<String, AnimationDefinition> animations = new HashMap<>();
	
	private final BiFunction<Entity, String, AnimationState> getAnimationState;
	
	public BuildableModel(MeshNode modelDescription, BiFunction<Entity, String, AnimationState> getAnimationState) {
		this.modelDescription = modelDescription;
		
		this.root = createBodyLayer().bakeRoot();
		
		this.getAnimationState = getAnimationState;
		
		initModel();
	}
	
	private void initModel() {
		BuildableModelDescriptionVisitingUtil.<ModelPart>startVisiting(this.modelDescription, (item, parent) -> parent.getChild(item.name), this.root);
		
		this.root.offsetPos(new Vector3f(0, 16, 0));
		
		HashMap<String, AnimationDefinition.Builder> builders = new HashMap<>();
		
		AnimationChannel.Target setRotationTarget = new AnimationChannel.Target() {
			@Override
			public void apply(ModelPart p_232248_, Vector3f p_232249_) {
				p_232248_.setRotation(p_232249_.x(), p_232249_.y(), p_232249_.z());
			}
		};
		
		BuildableModelDescriptionVisitingUtil.<HashMap<String, AnimationDefinition.Builder>>startVisiting(this.modelDescription, (item, parent) -> {
			item.animations.keySet().forEach(name -> {
				if(!builders.containsKey(name)) {
					Animation animation = item.animations.get(name);
					
					float length = (animation.frames.get(animation.frames.size() - 1).position - animation.frames.get(0).position) / 25.0f;
					
					AnimationDefinition.Builder builder = AnimationDefinition.Builder.withLength(length).looping();
					
					builders.put(name, builder);
				}
				
				AnimationDefinition.Builder builder = builders.get(name);
				
				
				Keyframe[] keyframes = item.animations.get(name).frames.stream().map(frame -> {
					return new Keyframe(frame.position / 25.0f, new Vector3f(frame.rotation.x, frame.rotation.y, frame.rotation.z), AnimationChannel.Interpolations.LINEAR);
				}).toArray(Keyframe[]::new);
				
				builder.addAnimation(item.name, new AnimationChannel(setRotationTarget, keyframes));
			});
			
			return builders;
		}, builders);
		
		builders.keySet().forEach(item -> this.animations.put(item, builders.get(item).build()));
	}

	private LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		BuildableModelDescriptionVisitingUtil.<PartDefinition>startVisiting(this.modelDescription, (item, parent) -> {
			CubeListBuilder builder = CubeListBuilder.create();
			
			for(MeshItem meshItem: item.mesh) {
				boolean mirror = meshItem.pos1.x > meshItem.pos2.x;
				
				builder = builder.mirror(mirror);
				builder = builder.texOffs((int)meshItem.textureMapping.offset.u, (int)meshItem.textureMapping.offset.v);
				
				Position pos1 = mirror ? meshItem.pos2 : meshItem.pos1;
				Position pos2 = mirror ? meshItem.pos1 : meshItem.pos2;
				
				float deltaX = originDelta(pos1.x, pos2.x, meshItem.textureMapping.mapping.x);
				float deltaY = originDelta(pos1.y, pos2.y, meshItem.textureMapping.mapping.y);
				float deltaZ = originDelta(pos1.z, pos2.z, meshItem.textureMapping.mapping.z);
				
				CubeDeformation deformation = new CubeDeformation(deltaX, deltaY, deltaZ);
				
				builder = builder.addBox(pos1.x + deltaX, pos1.y + deltaY, pos1.z + deltaZ, meshItem.textureMapping.mapping.x, meshItem.textureMapping.mapping.y, meshItem.textureMapping.mapping.z, deformation);
			}
			
			return parent.addOrReplaceChild(item.name, builder, PartPose.offset(item.mountPoint.x, item.mountPoint.y, item.mountPoint.z));
		}, partdefinition);
		
		return LayerDefinition.create(meshdefinition, 1024, 1024);
	}
	
	private static float originDelta(float x1, float x2, float dx) {
		return ((x2 - x1) - dx) / 2.0f;
	}
	
	@Override
	public ModelPart root() {
		return root;
	}

	@Override
	public void setupAnim(E entity, float hz, float ampl, float frame, float headPitch, float headYaw) {
		this.animations.keySet().forEach(item -> {
			AnimationState state = this.getAnimationState.apply(entity, item);
			
			this.animate(state, this.animations.get(item), frame);
		});
	}
}
