package com.transcendruins.settings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONObject;

import com.transcendruins.utilities.exceptions.fileexceptions.FileFormatException;
import com.transcendruins.utilities.exceptions.fileexceptions.MissingFileException;
import com.transcendruins.utilities.files.TracedPath;
import com.transcendruins.utilities.json.JSONOperator;

/**
 * <code>GameSettings</code>: A class representing the settings preferences of the user.
 */
public final class GameSettings {

    /**
     * <code>String</code>: An enum constant representing the <code>Video</code> menu.
     */
    public static final String VIDEO = "Video";

    /**
     * <code>String</code>: An enum constant representing the <code>Audio</code> menu.
     */
    public static final String AUDIO = "Audio";

    /**
     * <code>String</code>: An enum constant representing the <code>UI</code> menu.
     */
    public static final String UI = "UI";

    /**
     * <code>TracedPath</code>: The filepath of the settings JSON file.
     */
    public static final TracedPath SETTINGS_DIRECTORY = CacheOperator.getCacheDirectory().extend("settings.json");

    /**
     * <code>HashMap&lt;String, HashMap&lt;String, Object&gt;&gt;</code>: The map of menu types to their user settings as applied by the user or stored in memory.
     */
    private static final HashMap<String, HashMap<String, Object>> SETTINGS = retrieveSettings();

    /**
     * Retrieves a key from the saved settings.
     * @param menu <code>String</code>: The menu to retrieve.
     * @param key <code>String</code>: The setting key to retrieve from the menu. All settings are in lowercase with underscores between words.
     * @return <code>Object</code>: The retrieved setting.
     */
    public static Object getValue(String menu, String key) {

        return SETTINGS.get(menu).get(key);
    }

    /**
     * Applies a key to a saved setting and saves the user's settings.
     * @param menu <code>String</code>: The menu to save to.
     * @param key <code>String</code>: The setting key to apply to the menu. All settings are in lowercase with underscores between words.
     * @param value code>Object</code>: The applied setting.
     */
    public static void putValue(String menu, String key, Object value) {

        SETTINGS.get(menu).put(key, value);

        save(SETTINGS);
    }

    /**
     * Retrieves the set of all menu keys.
     * @return <code>Set&lt;String&gt;</code>: The key set of the <code>SETTINGS</code> field
     */
    public static Set<String> getMenuSet() {

        return SETTINGS.keySet();
    }

    /**
     * Creates the default settings used when previous settings do not exist or could not be found.
     * @return <code>HashMap&lt;String, HashMap&lt;String, Object&gt;&gt;</code>: The generated default settings.
     */
    private static HashMap<String, HashMap<String, Object>> defaultSettings() {

        HashMap<String, HashMap<String, Object>> settingsJson = new HashMap<>();

        // Create the video settings.
        HashMap<String, Object> videoSettings = new HashMap<>();
        videoSettings.put("invertZoom", false);
        videoSettings.put("fovSensitivity", 1.0);

        settingsJson.put(VIDEO, videoSettings);

        // Create the audio settings.
        HashMap<String, Object> audioSettings = new HashMap<>();

        settingsJson.put(AUDIO, audioSettings);

        // Create the UI settings.
        HashMap<String, Object> uiSettings = new HashMap<>();

        settingsJson.put(UI, uiSettings);

        return settingsJson;
    }

    /**
     * Retrieves the settings from the path traced by the <code>SETTINGS_DIRECTORY</code> directory.
     * @return <code>HashMap&lt;String, HashMap&lt;String, Object&gt;&gt;</code>: The retrieved settings map.
     */
    private static HashMap<String, HashMap<String, Object>> retrieveSettings() {

        HashMap<String, HashMap<String, Object>> settings = defaultSettings();

        if (!SETTINGS_DIRECTORY.exists()) {

            save(settings);
            return settings;
        }

        try {

            // Retrieve the previous JSON settings.
            JSONObject settingsJson = JSONOperator.retrieveJSON(SETTINGS_DIRECTORY).getCollection();

            for (String menu : settings.keySet()) {

                JSONObject menuSettingsJson = (JSONObject) settingsJson.get(menu);
                if (menuSettingsJson == null) {
                    
                    continue;
                }

                HashMap<String, Object> menuSettingsMap = new HashMap<>();

                for (String setting : settings.keySet()) {

                    if (menuSettingsJson.containsKey(setting)) {
                        
                        menuSettingsMap.put(setting, menuSettingsJson.get(setting));
                    }
                }

                // Save all previous settings to the new settings.
                settings.get(menu).putAll(menuSettingsMap);
            }

        } catch (FileFormatException | MissingFileException e) {

            save(settings);
        }

        return settings;
    }

    /**
     * Saves a map representing the user's settings to the path represented by the <code>SETTINGS_PATH</code> constant.
     * @param settings <code>HashMap&lt;String, HashMap&lt;String, Object&gt;&gt;</code>: The settings to save.
     * @return <code>boolean</code>: Whether or not the settings were successfully saved.
     */
    private static boolean save(HashMap<String, HashMap<String, Object>> settings) {

        try {

            JSONOperator.writeTo(SETTINGS_DIRECTORY, settings);
            return true;
        } catch (IOException e) {

            return false;
        }
    }

    /**
     * Prevents the <code>GameSettings</code> class from being instantiated.
     */
    private GameSettings() {}
}
