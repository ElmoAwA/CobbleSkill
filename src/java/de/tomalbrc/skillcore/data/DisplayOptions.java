package de.tomalbrc.skillcore.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Display;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record DisplayOptions(
        @SerializedName("ViewRange") double viewRange,
        @SerializedName("Width") float width,
        @SerializedName("Height") float height,
        @SerializedName("ShadowRadius") float shadowRadius,
        @SerializedName("ShadowStrength") float shadowStrength,
        @SerializedName("Billboard") Display.BillboardConstraints billboard,
        @SerializedName("TeleportDuration") int teleportDuration,
        @SerializedName("InterpolationDelay") int interpolationDelay,
        @SerializedName("InterpolationDuration") int interpolationDuration,
        @SerializedName("ColorOverride") int colorOverride,
        @SerializedName("Brightness") int brightness,

        @SerializedName("Translation") Vector3f translation,
        @SerializedName("Scale") Vector3f scale,
        @SerializedName("LeftRotation") Quaternionf leftRotation,
        @SerializedName("RightRotation") Quaternionf rightRotation,

        @SerializedName("Block") ResourceLocation block,

        @SerializedName("Item") ResourceLocation item,
        @SerializedName("Transform") ResourceLocation transform,

        @SerializedName("Text") String text,
        @SerializedName("Opacity") float opacity,
        @SerializedName("DefaultBackground") int defaultBackground,
        @SerializedName("BackgroundColor") int backgroundColor,
        @SerializedName("Alignment") Display.TextDisplay.Align alignment,
        @SerializedName("LineWidth") int lineWidth,
        @SerializedName("Shadowed") boolean shadowed,
        @SerializedName("SeeThrough") boolean seeThrough

) {
}
