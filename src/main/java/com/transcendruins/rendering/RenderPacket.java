package com.transcendruins.rendering;

import java.util.LinkedHashMap;

import com.jme3.light.Light;
import com.jme3.scene.Geometry;
import com.transcendruins.assets.modelassets.ModelAssetInstance;

public final record RenderPacket(LinkedHashMap<ModelAssetInstance, Geometry> opaque,
        LinkedHashMap<ModelAssetInstance, Geometry> transparent, LinkedHashMap<ModelAssetInstance, Light> lights) {

}
