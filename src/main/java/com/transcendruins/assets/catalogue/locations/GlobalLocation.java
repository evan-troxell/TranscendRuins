package com.transcendruins.assets.catalogue.locations;

import static com.transcendruins.assets.AssetType.LOCATION;
import com.transcendruins.assets.assets.AssetPresets;
import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class GlobalLocation {

    private final AssetPresets location;

    public final AssetPresets AssetPresets() {

        return location;
    }

    private final LocationTrigger trigger;

    public final LocationTrigger getTrigger() {

        return trigger;
    }

    public GlobalLocation(TracedCollection json) throws LoggedException {

        TracedEntry<AssetPresets> presetsEntry = json.getAsPresets("location", false, LOCATION);
        location = presetsEntry.getValue();

        TracedEntry<TracedDictionary> triggerEntry = json.getAsDict("trigger", true);
        if (triggerEntry.containsValue()) {

            TracedDictionary triggerJson = triggerEntry.getValue();
            trigger = new LocationTrigger(triggerJson);
        } else {

            trigger = null;
        }
    }
}
