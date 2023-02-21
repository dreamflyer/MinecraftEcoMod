package com.dreamflyer.model.buildable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dreamflyer.model.buildable.BuildableModelDescription.Frame;

public class BuildableModelDescription {
	private BuildableModelDescription() {
		
	}
	
	public static class MeshNode {
		public final Position mountPoint;
		
		public final String name;
		
		public final List<MeshNode> children = new ArrayList<MeshNode>();
		
		public final List<MeshItem> mesh = new ArrayList<MeshItem>();
		
		public final Map<String, Animation> animations = new HashMap<String, Animation>();
		
		public MeshNode(String name, Position mountPoint) {
			this.name = name;
			this.mountPoint = mountPoint;
		}
	}
	
	public static class MeshItem {
		public final Position pos1;
		public final Position pos2;
		
		public final MeshTextureMapping textureMapping;
		
		public MeshItem(Position pos1, Position pos2, MeshTextureMapping textureMapping) {
			this.pos1 = pos1;
			this.pos2 = pos2;
			
			this.textureMapping = textureMapping;
		}
	}
	
	public static class MeshTextureMapping {
		public final UVPosition offset;
		
		public final Size mapping;
		
		public MeshTextureMapping(float offsetU, float offsetV, float mappingX, float mappingY, float mappingZ) {
			this.offset = new UVPosition(offsetU, offsetV);
			
			this.mapping = new Size(mappingX, mappingY, mappingZ);
		}
	}
	
	public static class Position {
		public final float x;
		public final float y;
		public final float z;
		
		public Position(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	public static class Size {
		public final float x;
		public final float y;
		public final float z;
		
		public Size(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public Size(float x, float y) {
			this(x, y, 0);
		}
		
		public Size(float size) {
			this(size, size, size);
		}
	}
	
	public static class UVPosition {
		public final float u;
		public final float v;
		
		public UVPosition(float u, float v) {
			this.u = u;
			this.v = v;
		}
	}
	
	public static class Rotation {
		public final float x;
		public final float y;
		public final float z;
		
		public Rotation(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	public static class Frame {
		public final float position;
		
		public final Rotation rotation;
		
		public Frame(float position, Rotation rotation) {
			this.position = position;
			
			this.rotation = rotation;
		}
	}
	
	public static class Animation {
		public final List<Frame> frames = new ArrayList<Frame>();
	}
}
