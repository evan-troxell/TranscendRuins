package com.transcendruins.rendering;

import java.util.List;

import com.jme3.light.Light;
import com.jme3.scene.Geometry;

public final record RenderPacket(List<Geometry> opaque, List<Geometry> transparent, List<Light> lights) {

}
