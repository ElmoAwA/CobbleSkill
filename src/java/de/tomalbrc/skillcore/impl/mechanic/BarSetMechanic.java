package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.util.TextUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;

public class BarSetMechanic extends AbstractMechanic {
    @SerializedName(value = "name", alternate = {"n"})
    Resolvable<String> name = Resolvable.literal("infobar");
    @SerializedName(value = "display", alternate = {"d", "bartimerdisplay", "bartimertext"})
    Resolvable<String> display;
    @SerializedName(value = "value", alternate = {"v"})
    Resolvable<Double> value;
    @SerializedName(value = "color", alternate = {"c", "bartimercolor"})
    BossEvent.BossBarColor color;
    @SerializedName(value = "style", alternate = {"s", "bartimerstyle"})
    Resolvable<String> style;
    @SerializedName(value = "range", alternate = {"r"})
    Resolvable<Double> range = Resolvable.literal(64.);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                if (!target.isEntity()) continue;
                var o = target.getEntity().overlay();
                if (o == null) continue;
                var bar = o.getBossBar(name.resolve(tree, target));
                if (bar == null) continue;

                var s = style.resolve(tree, target);
                BossEvent.BossBarOverlay overlay = switch (s) {
                    case "SOLID" -> BossEvent.BossBarOverlay.PROGRESS;
                    case "SEGMENTED_6" -> BossEvent.BossBarOverlay.NOTCHED_6;
                    case "SEGMENTED_10" -> BossEvent.BossBarOverlay.NOTCHED_10;
                    case "SEGMENTED_12" -> BossEvent.BossBarOverlay.NOTCHED_12;
                    case "SEGMENTED_20" -> BossEvent.BossBarOverlay.NOTCHED_20;
                    default -> null;
                };

                if (overlay == null) {
                    overlay = BossEvent.BossBarOverlay.valueOf(s);
                }

                bar.setName(TextUtil.formatText(display.resolve(tree, target), tree.caster()));
                bar.setOverlay(overlay);
                bar.setColor(color);
                bar.setRange(range.resolve(tree, target));
                bar.setProgress((float)(double)value.resolve(tree, target));
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.BAR_SET;
    }
}
