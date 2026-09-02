package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.Scoreboard;

public class ModifyMobScoreMechanic extends AbstractMechanic {
    @SerializedName(value = "objective", alternate = {"o"})
    Resolvable<String> objective = Resolvable.literal("");

    /**
     * action: set | add | multiply | divide | reset
     * TODO: enum
     */
    @SerializedName(value = "action", alternate = {"a"})
    Resolvable<String> action = Resolvable.literal("set");

    @SerializedName(value = "v", alternate = {"value"})
    Resolvable<Double> value = Resolvable.literal(1.0);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        Entity caster = tree.caster();

        Level level = tree.level();
        if (level == null || level.isClientSide) return ExecutionResult.NULL; // server-side only

        String objName = objective.resolve(tree);
        if (objName == null || objName.isBlank()) return ExecutionResult.NULL;

        Scoreboard scoreboard = level.getScoreboard();
        Objective obj = scoreboard.getObjective(objName);
        if (obj == null) {
            return ExecutionResult.NULL;
        }

        ScoreAccess score = scoreboard.getOrCreatePlayerScore(caster, obj);

        String act = action.resolve(tree);
        if (act == null) act = "set";
        act = act.trim().toLowerCase();

        int current = score.get();
        double v = value.resolve(tree);

        switch (act) {
            case "set", "assign" -> {
                int newVal = (int) Math.floor(v);
                score.set(newVal);
            }
            case "add", "+" -> {
                int add = (int) Math.floor(v);
                // prefer add() if present
                try {
                    score.add(add);
                } catch (NoSuchMethodError ignored) {
                    score.set(current + add);
                }
            }
            case "multiply", "mul", "*" -> {
                int newVal = (int) Math.floor(current * v);
                score.set(newVal);
            }
            case "divide", "div", "/" -> {
                if (v == 0) {
                    score.set(0);
                } else {
                    int newVal = (int) Math.floor(current / v);
                    score.set(newVal);
                }
            }
            case "reset", "remove", "clear" -> {
                score.reset();
            }
            default -> {
                if (act.startsWith("+") || act.startsWith("-")) {
                    try {
                        int delta = Integer.parseInt(act);
                        score.set(current + delta);
                    } catch (NumberFormatException ignored) {
                        return ExecutionResult.NULL;
                    }
                } else {
                    return ExecutionResult.NULL;
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.MODIFYMOBSCORE;
    }
}
