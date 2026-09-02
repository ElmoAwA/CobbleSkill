package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.Scoreboard;

public class ScoreCondition extends AbstractCondition {
    @SerializedName(value = "objective", alternate = {"obj", "o"})
    Resolvable<String> objective = Resolvable.literal("");

    @SerializedName(value = "entry", alternate = {"ent", "e"})
    Resolvable<String> entry = Resolvable.literal("");

    @SerializedName(value = "value", alternate = {"val", "v"})
    Resolvable<RangedValue> value = Resolvable.literal(RangedValue.of(0));

    @Override
    public boolean test(SkillTree tree, Target target) {
        if (!target.isEntity())
            return false;

        Entity entity = target.getEntity();
        if (entity == null) return false;

        Level level = entity.level();

        String objName = objective.resolve(tree);
        String entryName = entry.resolve(tree);
        var expectedValue = value.resolve(tree);

        if (objName == null || objName.isEmpty() || entryName == null || entryName.isEmpty()) {
            return false;
        }

        Scoreboard scoreboard = level.getScoreboard();
        Objective obj = scoreboard.getObjective(objName);
        if (obj == null) return false;

        ScoreAccess score = scoreboard.getOrCreatePlayerScore(entity, obj);
        return expectedValue.isWithin(score.get());
    }
}
