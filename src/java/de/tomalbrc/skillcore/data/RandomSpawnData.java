package de.tomalbrc.skillcore.data;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.condition.Condition;

import java.util.List;

public record RandomSpawnData(
        String identifier,
        @SerializedName("Action") Action action,
        @SerializedName(value = "Type", alternate = "MobType") String type,
        @SerializedName(value = "Types", alternate = "MobTypes") List<String> types,
        @SerializedName("Level") Integer level,
        @SerializedName("Chance") Double chance,
        @SerializedName("Priority") Integer priority,
        @SerializedName("UseWorldScaling") boolean useWorldScaling,
        @SerializedName("Worlds") List<String> worlds,
        @SerializedName("Biomes") List<String> biomes,
        @SerializedName("Conditions") List<Condition> conditions,
        @SerializedName("Reason") String reason,
        @SerializedName("PositionType") String positionType,
        @SerializedName("Cooldown") Double cooldown,
        @SerializedName("Structures") List<String> structures
) {
    public enum Action {
        REPLACE,
        ADD,
        DENY,
        SCALE, // TODO: "upcoming feature"

        //Cobblemon specific:
        UPGRADE
    }
}
