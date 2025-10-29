package com.transcendruins.rendering.renderBuffer;

import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.transcendruins.geometry.Vector;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final record LightData(int index, ColorRGBA color, float range, Vector3f direction, float innerAngle,
        float outerAngle) {

    public static final LightData createLightData(TracedCollection collection, Object key, int vertices)
            throws LoggedException {

        TracedEntry<TracedDictionary> jsonEntry = collection.getAsDict(key, false);
        TracedDictionary json = jsonEntry.getValue();

        TracedEntry<Integer> indexEntry = json.getAsInteger("index", false, null, num -> 0 <= num && num < vertices);
        Integer index = indexEntry.getValue();

        TracedEntry<ColorRGBA> colorEntry = json.getAsColorRGBA("color", false, null);
        ColorRGBA color = colorEntry.getValue();

        TracedEntry<Float> rangeEntry = json.getAsFloat("range", false, null, num -> 0 < num);
        float range = rangeEntry.getValue();

        Vector3f dir = null;
        float innerAngle = 0f;
        float outerAngle = 0f;

        TracedEntry<Vector> directionEntry = json.getAsVector("direction", true, 3);
        if (directionEntry.containsValue()) {

            Vector direction = directionEntry.getValue();

            float x = (float) direction.getX();
            float y = (float) direction.getY();
            float z = (float) direction.getZ();

            float d = (float) Math.sqrt(x * x + y * y + z * z);
            if (Math.abs(d) > 10e-6) {

                dir = new Vector3f(x / d, y / d, z / d);

                TracedEntry<Float> innerAngleEntry = json.getAsFloat("innerAngle", false, null,
                        num -> 0 < num && num < 180);
                float innerAngleDegrees = innerAngleEntry.getValue();
                innerAngle = (float) Math.toRadians(innerAngleDegrees);

                TracedEntry<Float> outerAngleEntry = json.getAsFloat("outerAngle", false, null,
                        num -> innerAngleDegrees <= num && num < 180);
                outerAngle = (float) Math.toRadians(outerAngleEntry.getValue());
            }
        }

        return new LightData(index, color, range, dir, innerAngle, outerAngle);
    }

    public final Light createLight(Vector3f[] vertices) {

        Vector3f position = vertices[index];
        if (direction == null) {

            PointLight point = new PointLight();
            point.setPosition(position);
            point.setColor(color);
            point.setRadius(range);
            return point;
        }

        SpotLight spotlight = new SpotLight();
        spotlight.setPosition(position);
        spotlight.setDirection(direction);
        spotlight.setColor(color);
        spotlight.setSpotRange(range);
        spotlight.setSpotInnerAngle(innerAngle);
        spotlight.setSpotOuterAngle(outerAngle);

        return spotlight;
    }
}
