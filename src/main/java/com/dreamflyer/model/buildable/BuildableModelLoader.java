package com.dreamflyer.model.buildable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.dreamflyer.model.buildable.BuildableModelDescription.Animation;
import com.dreamflyer.model.buildable.BuildableModelDescription.Frame;
import com.dreamflyer.model.buildable.BuildableModelDescription.MeshItem;
import com.dreamflyer.model.buildable.BuildableModelDescription.MeshNode;
import com.dreamflyer.model.buildable.BuildableModelDescription.MeshTextureMapping;
import com.dreamflyer.model.buildable.BuildableModelDescription.Position;
import com.dreamflyer.model.buildable.BuildableModelDescription.Rotation;
import com.dreamflyer.model.buildable.BuildableModelDescription.Size;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class BuildableModelLoader {
	private BuildableModelLoader() {

	}

	public static MeshNode load(ResourceLocation location, float textureSize) {
		try {
			Resource r = Minecraft.getInstance().getResourceManager().getResource(location).get();
			
			InputStream in = r.open();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			Gson gson = new Gson();

			return deserializeMeshNode(gson.fromJson(reader, JsonElement.class), 1024);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return null;
	}
	
	private static Position deserializePosition(JsonElement jsonElement, float multiplyer) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		return new Position(jsonObject.get("x").getAsFloat() * multiplyer, jsonObject.get("y").getAsFloat() * multiplyer, jsonObject.get("z").getAsFloat() * multiplyer);
	}
	
	private static MeshTextureMapping deserializeUvMap(JsonElement jsonElement, float textureSize) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		JsonObject offsetObject = jsonObject.get("offset").getAsJsonObject();
		JsonObject sizeObject = jsonObject.get("size").getAsJsonObject();
		
		return new MeshTextureMapping(offsetObject.get("x").getAsFloat() * textureSize, offsetObject.get("y").getAsFloat() * textureSize, sizeObject.get("x").getAsFloat() * textureSize, sizeObject.get("y").getAsFloat() * textureSize, sizeObject.get("z").getAsFloat() * textureSize);
	}
	
	private static MeshItem deserializeMeshItem(JsonElement jsonElement, float textureSize) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		Position start = deserializePosition(jsonObject.get("start"), 16.0f);
		Position end = deserializePosition(jsonObject.get("end"), 16.0f);
		
		MeshTextureMapping uvMap = deserializeUvMap(jsonObject.get("uvMap"), textureSize);
		
		return new MeshItem(start, end, uvMap);
	}
	
	private static Rotation deserializeRotation(JsonElement element) {
		JsonObject jsonObject = element.getAsJsonObject();
		
		return new Rotation(jsonObject.get("x").getAsFloat(), jsonObject.get("y").getAsFloat(), jsonObject.get("z").getAsFloat());
	}
	
	private static Frame deserializeFrame(JsonElement element) {
		JsonObject jsonObject = element.getAsJsonObject();
		
		return new Frame(jsonObject.get("position").getAsFloat(), deserializeRotation(jsonObject.get("rotation")));
	}
	
	private static void deserializeFrames(JsonElement element, Consumer<Frame> consumer) {
		JsonArray jsonArray = element.getAsJsonArray();
		
		jsonArray.forEach(item -> consumer.accept(deserializeFrame(item)));
	}
	
	private static Animation deserializeAnimation(JsonElement element) {
		JsonObject object = element.getAsJsonObject();
		
		Animation result = new Animation();
		
		deserializeFrames(object.get("frames"), item -> result.frames.add(item));
		
		return result;
	}
	
	private static void deserializeAnimations(JsonElement element, BiConsumer<String, Animation> consumer) {
		JsonObject object = element.getAsJsonObject();
		
		object.keySet().forEach(item -> consumer.accept(item, deserializeAnimation(object.get(item))));
	}
	
	private static MeshNode deserializeMeshNode(JsonElement jsonElement, float textureSize) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
				
		MeshNode result = new MeshNode(jsonObject.get("name").getAsString(), deserializePosition(jsonObject.get("mountPoint"), 16.0f));
		
		jsonObject.get("mesh").getAsJsonArray().forEach(item -> result.mesh.add(deserializeMeshItem(item, textureSize)));
		
		jsonObject.get("children").getAsJsonArray().forEach(item -> result.children.add(deserializeMeshNode(item, textureSize)));
		
		deserializeAnimations(jsonObject.get("animations"), (name, item) -> result.animations.put(name, item));
		
		return result;
	}
}
