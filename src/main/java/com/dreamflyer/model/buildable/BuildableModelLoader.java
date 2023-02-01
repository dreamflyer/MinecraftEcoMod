package com.dreamflyer.model.buildable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.dreamflyer.model.buildable.BuildableModelDescription.MeshItem;
import com.dreamflyer.model.buildable.BuildableModelDescription.MeshNode;
import com.dreamflyer.model.buildable.BuildableModelDescription.MeshTextureMapping;
import com.dreamflyer.model.buildable.BuildableModelDescription.Position;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class BuildableModelLoader {
	private BuildableModelLoader() {

	}

	public static MeshNode load(ResourceLocation location) {
		try {
			Resource r = Minecraft.getInstance().getResourceManager().getResource(location).get();
			
			InputStream in = r.open();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			Gson gson = new Gson();

			return deserializeMeshNode(gson.fromJson(reader, JsonElement.class));
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return null;
	}
	
	private static Position deserializePosition(JsonElement jsonElement, float multiplyer) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		return new Position(jsonObject.get("x").getAsFloat() * multiplyer, jsonObject.get("y").getAsFloat() * multiplyer, jsonObject.get("z").getAsFloat() * multiplyer);
	}
	
	private static MeshTextureMapping deserializeUvMap(JsonElement jsonElement) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		JsonObject offsetObject = jsonObject.get("offset").getAsJsonObject();
		JsonObject sizeObject = jsonObject.get("size").getAsJsonObject();
		
		return new MeshTextureMapping(offsetObject.get("x").getAsFloat(), offsetObject.get("y").getAsFloat(), 100, 100, 100);
	}
	
	private static MeshItem deserializeMeshItem(JsonElement jsonElement) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		Position start = deserializePosition(jsonObject.get("start"), 16.0f);
		Position end = deserializePosition(jsonObject.get("end"), 16.0f);
		
		MeshTextureMapping uvMap = deserializeUvMap(jsonObject.get("uvMap"));
		
		return new MeshItem(start, end, uvMap);
	}
	
	private static MeshNode deserializeMeshNode(JsonElement jsonElement) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
				
		MeshNode result = new MeshNode(jsonObject.get("name").getAsString(), deserializePosition(jsonObject.get("mountPoint"), 16.0f));
		
		jsonObject.get("mesh").getAsJsonArray().forEach(item -> result.mesh.add(deserializeMeshItem(item)));
		
		jsonObject.get("children").getAsJsonArray().forEach(item -> result.children.add(deserializeMeshNode(item)));
		
		return result;
	}
}
