package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.mechanic.Mechanic;

public abstract class AbstractMechanic implements Mechanic {
    @SerializedName(value = "sync", alternate = {"forcesync"})
    boolean sync = false;
    int delay;
    int repeat;
    @SerializedName(value = "repeatinterval", alternate = {"repeat_interval"})
    int repeatInterval;
    @SerializedName(value = "targetinterval", alternate = {"target_interval"})
    int targetInterval;

    public int delay() {
        return delay;
    }

    public boolean sync() {
        return sync;
    }

    public int repeat() {
        return repeat;
    }

    public int repeatInterval() {
        return repeatInterval;
    }

    public int targetInterval() {
        return targetInterval;
    }
}
