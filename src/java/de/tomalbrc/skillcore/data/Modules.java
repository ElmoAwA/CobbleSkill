package de.tomalbrc.skillcore.data;

import com.google.gson.annotations.SerializedName;

public record Modules(
        @SerializedName("ThreatTable") boolean threatTable,
        @SerializedName("ImmunityTable") boolean immunityTable) {
}
