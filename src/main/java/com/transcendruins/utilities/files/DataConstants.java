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

    public static final AssetPresets INVENTORY_DISPLAY_IDENTIFIER;

    public static final String INVENTORY_SLOT_TEXTURE = "interface.inventoryDisplay.slot";

    public static final String INVENTORY_SLOT_SELECTED_TEXTURE = "interface.inventoryDisplay.slotSelected";

    static {

        try {

            InternalPath constantsPath = INTERNAL_DATA_DIRECTORY.extend("constants.json");
            TracedDictionary constantsJson = JSONOperator.retrieveJSON(constantsPath);

            TracedEntry<AssetPresets> playerIdentifierEntry = constantsJson.getAsPresets("playerIdentifier", false,
                    AssetType.ENTITY);
            PLAYER_IDENTIFIER = playerIdentifierEntry.getValue();

            TracedEntry<AssetPresets> inventoryDisplayIdentifierEntry = constantsJson
                    .getAsPresets("inventoryDisplayIdentifier", false, AssetType.INTERFACE);
            INVENTORY_DISPLAY_IDENTIFIER = inventoryDisplayIdentifierEntry.getValue();
        } catch (LoggedException e) {

            e.printStackTrace();
            throw new Error("The data constants could not be generated. Proceeding...");
        }
    }
}
