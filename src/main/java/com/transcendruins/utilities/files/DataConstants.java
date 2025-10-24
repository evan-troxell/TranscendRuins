package com.transcendruins.utilities.files;

import com.transcendruins.assets.AssetType;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.JSONOperator;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class DataConstants {

    public static final InternalPath INTERNAL_DATA_DIRECTORY = InternalPath.INTERNAL_DIRECTORY.extend("data");

    /**
     * <code>InternalPath</code>: The path to the placeholder sound.
     */
    public static final InternalPath MISSING_SOUND = INTERNAL_DATA_DIRECTORY.extend("missingSound.wav");

    /**
     * <code>InternalPath</code>: The path to the placeholder texture.
     */
    public static final InternalPath MISSING_TEXTURE = INTERNAL_DATA_DIRECTORY.extend("missingTexture.png");

    public static final InternalPath FRAME_ICON_PATH = INTERNAL_DATA_DIRECTORY.extend("frameIcon.png");

    public static final AssetPresets PLAYER_IDENTIFIER;

    public static final AssetPresets GLOBAL_MAP_IDENTIFIER;

    public static final AssetPresets LOCATION_DISPLAY_IDENTIFIER;

    public static final AssetPresets INVENTORY_DISPLAY_IDENTIFIER;

    public static final String GLOBAL_MAP_PLAYER_PIN = "global.pin.player";

    public static final String INVENTORY_SLOT_TEXTURE = "interface.inventory.slot";

    public static final String INVENTORY_SLOT_SELECTED_TEXTURE = "interface.inventory.slotSelected";

    static {

        try {

            InternalPath constantsPath = INTERNAL_DATA_DIRECTORY.extend("constants.json");
            TracedDictionary constantsJson = JSONOperator.retrieveJSON(constantsPath);

            TracedEntry<AssetPresets> playerIdentifierEntry = constantsJson.getAsPresets("playerIdentifier", false,
                    AssetType.ENTITY);
            PLAYER_IDENTIFIER = playerIdentifierEntry.getValue();

            TracedEntry<AssetPresets> globalMapIdentifierEntry = constantsJson.getAsPresets("globalMapIdentifier",
                    false, AssetType.INTERFACE);
            GLOBAL_MAP_IDENTIFIER = globalMapIdentifierEntry.getValue();

            TracedEntry<AssetPresets> locationDisplayIdentifierEntry = constantsJson
                    .getAsPresets("locationDisplayIdentifier", false, AssetType.INTERFACE);
            LOCATION_DISPLAY_IDENTIFIER = locationDisplayIdentifierEntry.getValue();

            TracedEntry<AssetPresets> inventoryDisplayIdentifierEntry = constantsJson
                    .getAsPresets("inventoryDisplayIdentifier", false, AssetType.INTERFACE);
            INVENTORY_DISPLAY_IDENTIFIER = inventoryDisplayIdentifierEntry.getValue();
        } catch (LoggedException e) {

            e.printStackTrace();
            throw new Error("The data constants could not be generated. Proceeding...");
        }
    }
}
