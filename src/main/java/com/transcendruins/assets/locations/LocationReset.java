package com.transcendruins.assets.locations;

import com.transcendruins.utilities.exceptions.LoggedException;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

/**
 * <code>LocationReset</code>: A class representing the reset settings of a
 * global location.
 */
public final class LocationReset {

    /**
     * <code>LocationReset</code>: The default location reset presets.
     */
    public static final LocationReset DEFAULT = new LocationReset();

    /**
     * Creates a new instance of the <code>LocationReset</code> class.
     */
    public LocationReset() {

        duration = -1.0;

        displayCountdownTimer = false;
    }

    /**
     * <code>double</code>: The duration of this <code>LocationReset</code>
     * instance, in minutes. This value is guaranteed to be greater than or equal to
     * 0.0, except for values of -1 which represent no countdown.
     */
    private final double duration;

    /**
     * Retrieves the duration of this <code>LocationReset</code> instance, in
     * minutes.
     *
     * @return <code>double</code>: The <code>duration</code> field of this
     *         <code>LocationReset</code> instance.
     */
    public double getDuration() {

        return duration;
    }

    /**
     * <code>boolean</code>: Whether or not this <code>LocationReset</code> instance
     * should display the countdown timer.
     */
    private final boolean displayCountdownTimer;

    /**
     * Retrieves whether or not this <code>LocationReset</code> instance should
     * display the countdown timer.
     * 
     * @return <code>boolean</code>: The <code>displayCountdownTimer</code> field of
     *         this <code>LocationReset</code> instance.
     */
    public boolean getDisplayCountdownTimer() {

        return displayCountdownTimer;
    }

    /**
     * Creates a new instance of the <code>LocationReset</code> class.
     * 
     * @param json <code>TracedDictionary</code>: The JSON to parse into this
     *             <code>LocationReset</code> instance.
     * @throws LoggedException Thrown if there are any issues or missing fields when
     *                         parsing the JSON.
     */
    public LocationReset(TracedDictionary json) throws LoggedException {

        TracedEntry<Double> durationEntry = json.getAsDouble("duration", true, 0.0, num -> num >= 0 || num == -1);
        duration = durationEntry.getValue();

        TracedEntry<Boolean> displayCountdownTimerEntry = json.getAsBoolean("displayCountdownTimer", true, true);
        displayCountdownTimer = displayCountdownTimerEntry.getValue() && duration > -1.0;
    }
}