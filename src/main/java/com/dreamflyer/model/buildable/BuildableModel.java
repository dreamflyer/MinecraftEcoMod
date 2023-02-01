package com.dreamflyer.model.buildable;

import com.dreamflyer.model.buildable.BuildableModelDescription.MeshItem;
import com.dreamflyer.model.buildable.BuildableModelDescription.MeshNode;
import com.dreamflyer.model.buildable.BuildableModelDescription.Position;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;

public class BuildableModel<E extends Entity> extends HierarchicalModel<E> {
	private final MeshNode modelDescription;
	
	private final ModelPart root;
	
	public BuildableModel(MeshNode modelDescription) {
		this.modelDescription = modelDescription;
		
		this.root = createBodyLayer().bakeRoot();
		
		initModel();
	}
	
	private void initModel() {
		BuildableModelDescriptionVisitingUtil.<ModelPart>startVisiting(this.modelDescription, (item, parent) -> {
			return parent.getChild(item.name);
		}, this.root);
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
		
		return LayerDefinition.create(meshdefinition, 256, 256);
	}
	
	private static float originDelta(float x1, float x2, float dx) {
		return ((x2 - x1) - dx) / 2.0f;
	}
	
	@Override
	public ModelPart root() {
		return root;
	}

	@Override
	public void setupAnim(E p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {
		
	}
}
