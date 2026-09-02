package de.tomalbrc.skillcore.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.world.entity.Entity;

import java.lang.reflect.Type;

public record SkillHealthCondition(
        double minValue,
        double maxValue,
        boolean percent
) {
    public boolean isMet(Entity entity) {
        var living = entity.asLivingEntity();
        if (living == null)
            return true;

        double health = living.getHealth();
        double maxHealth = living.getMaxHealth();
        double current = percent ? (health / maxHealth) : health;
        return current >= minValue && current <= maxValue;
    }

    public static SkillHealthCondition fromString(String input) {
        double minValue = 0;
        double maxValue = 1;

        var isPercent = input.contains("%");

        try {
            if (input.startsWith("=")) {
                // "=90%" or "=30%-50%"
                String expr = input.substring(1).trim();

                if (expr.contains("-")) {
                    String[] parts = expr.split("-");
                    minValue = parseValue(parts[0]);
                    maxValue = parseValue(parts[1]);
                    isPercent = expr.contains("%");
                } else {
                    maxValue = parseValue(expr);
                    isPercent = expr.endsWith("%");
                    minValue = 0.0;
                }

            } else if (input.startsWith("<")) {
                // "<50%" or "<2000"
                String expr = input.substring(1).trim();
                maxValue = parseValue(expr);
                minValue = 0.0;
                isPercent = expr.endsWith("%");

            } else if (input.startsWith(">")) {
                // ">500" or ">75%"
                String expr = input.substring(1).trim();
                minValue = parseValue(expr);
                maxValue = isPercent ? 1.0 : Double.MAX_VALUE;
                isPercent = expr.endsWith("%");

            }

            if (isPercent) {
                minValue = toPercent(minValue);
                maxValue = toPercent(maxValue);
            }

        } catch (NumberFormatException ignored) {
        }

        return new SkillHealthCondition(minValue, maxValue, isPercent);

    }

    private static double parseValue(String str) {
        str = str.replace("%", "").trim();
        return Double.parseDouble(str);
    }

    private static double toPercent(double value) {
        return value / 100.0;
    }


    public static class Deserializer implements JsonDeserializer<SkillHealthCondition> {
        @Override
        public SkillHealthCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String input = json.getAsString().trim();
            return SkillHealthCondition.fromString(input);
        }
    }
}
