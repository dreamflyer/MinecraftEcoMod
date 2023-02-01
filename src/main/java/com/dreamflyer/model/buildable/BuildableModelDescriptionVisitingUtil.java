package com.dreamflyer.model.buildable;

import java.util.function.BiFunction;

import com.dreamflyer.model.buildable.BuildableModelDescription.MeshNode;

public class BuildableModelDescriptionVisitingUtil {
	private BuildableModelDescriptionVisitingUtil() {
		
	}
	
	public static <T> void startVisiting(MeshNode node, BiFunction<MeshNode, T, T> onModelNode, T previousResult) {
		final T result = onModelNode.apply(node, previousResult);
		
		node.children.forEach(item -> startVisiting(item, onModelNode, result));
	}
}
