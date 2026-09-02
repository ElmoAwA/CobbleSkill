package de.tomalbrc.skillcore.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record DropTable(
        String identifier,
        @SerializedName(value = "Type", alternate = "MobType") String type,
        @SerializedName("Drops") List<String> drops

) {
}
