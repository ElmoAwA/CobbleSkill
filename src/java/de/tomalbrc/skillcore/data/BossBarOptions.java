package de.tomalbrc.skillcore.data;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.util.RangedBossBar;
import de.tomalbrc.skillcore.util.TextUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;

public record BossBarOptions(
        @SerializedName("Enabled") boolean enabled,
        @SerializedName("Title") String title,
        @SerializedName("Range") double range,
        @SerializedName("Color") BossEvent.BossBarColor color,
        @SerializedName("Style") String style,
        @SerializedName("CreateFog") boolean createFog,
        @SerializedName("DarkenSky") boolean darkenSky,
        @SerializedName("PlayMusic") boolean playMusic
) {
    public RangedBossBar asServerBossEvent(Entity entity) {
        if (!enabled)
            return null;

        BossEvent.BossBarOverlay overlay = switch (style) {
            case "SOLID" -> BossEvent.BossBarOverlay.PROGRESS;
            case "SEGMENTED_6" -> BossEvent.BossBarOverlay.NOTCHED_6;
            case "SEGMENTED_10" -> BossEvent.BossBarOverlay.NOTCHED_10;
            case "SEGMENTED_12" -> BossEvent.BossBarOverlay.NOTCHED_12;
            case "SEGMENTED_20" -> BossEvent.BossBarOverlay.NOTCHED_20;
            default -> null;
        };

        if (overlay == null) {
            overlay = BossEvent.BossBarOverlay.valueOf(style);
        }

        RangedBossBar event = new RangedBossBar(TextUtil.formatText(title, entity), color, overlay, entity, range <= 0 ? 30 : range);
        event.setCreateWorldFog(createFog);
        event.setDarkenScreen(darkenSky);
        event.setPlayBossMusic(playMusic);
        return event;
    }

    public Component name(Entity entity) {
        return TextUtil.formatText(title, entity);
    }
}
