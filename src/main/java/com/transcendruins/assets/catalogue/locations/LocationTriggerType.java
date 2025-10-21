package com.transcendruins.assets.catalogue.locations;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.exceptions.propertyexceptions.referenceexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedCollection;
import com.transcendruins.utilities.json.TracedEntry;

public enum LocationTriggerType {

    AUTOMATIC, EVENT;

    public static final LocationTriggerType createLocationTriggerType(TracedCollection collection, Object key)
            throws LoggedException {

        TracedEntry<String> triggerTypeEntry = collection.getAsString(key, true, "automatic");
        String triggerType = triggerTypeEntry.getValue();

        return switch (triggerType) {

        case "automatic" -> AUTOMATIC;

        case "event" -> EVENT;

        default -> throw new UnexpectedValueException(triggerTypeEntry);
        };
    }
}
