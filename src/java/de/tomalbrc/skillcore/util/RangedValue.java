package de.tomalbrc.skillcore.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Random;

public class RangedValue {
    private static final Random random = new Random();

    public double get() {
        return a == b ? a : random.nextDouble(a, b);
    }

    public int getAsInteger() {
        return (int) (a == b ? a : random.nextDouble(a, b));
    }

    public enum Mode {
        EQUAL,
        NOT_EQUAL,
        GREATER,
        GREATER_EQUAL,
        LESS,
        LESS_EQUAL,
        BETWEEN
    }

    private final Mode mode;
    private final double a, b;

    public RangedValue(double val) {
        this(Mode.EQUAL, val, val);
    }

    public RangedValue(Mode mode, double a, double b) {
        this.mode = mode;
        this.a = Math.min(a, b);
        this.b = Math.max(b, a);
    }

    public boolean isWithinRangeFromZero(double v) {
        return v <= b;
    }

    public static RangedValue of(double val) {
        return new RangedValue(Mode.EQUAL, val, val);
    }

    public static RangedValue parse(String s) {
        s = s.trim().toLowerCase();

        try {
            if (s.contains("-") || s.contains("to")) {
                String cleaned = s.replace("to", "-");
                String[] parts = cleaned.split("-");
                if (parts.length == 2) {
                    double v1 = Double.parseDouble(parts[0]);
                    double v2 = Double.parseDouble(parts[1]);
                    double min = Math.min(v1, v2);
                    double max = Math.max(v1, v2);
                    return new RangedValue(Mode.BETWEEN, min, max);
                }
            }

            if (s.startsWith(">=")) {
                double v = Double.parseDouble(s.substring(2));
                return new RangedValue(Mode.GREATER_EQUAL, v, 0);
            }

            if (s.startsWith("<=")) {
                double v = Double.parseDouble(s.substring(2));
                return new RangedValue(Mode.LESS_EQUAL, v, 0);
            }

            if (s.startsWith("!=")) {
                double v = Double.parseDouble(s.substring(2));
                return new RangedValue(Mode.NOT_EQUAL, v, v);
            }

            if (s.startsWith("=")) {
                double v = Double.parseDouble(s.substring(1));
                return new RangedValue(Mode.EQUAL, v, v);
            }

            if (s.startsWith(">")) {
                double v = Double.parseDouble(s.substring(1));
                return new RangedValue(Mode.GREATER, v, 0);
            }

            if (s.startsWith("<")) {
                double v = Double.parseDouble(s.substring(1));
                return new RangedValue(Mode.LESS, v, 0);
            }

            double v = Double.parseDouble(s);
            return new RangedValue(Mode.EQUAL, v, v);

        } catch (Exception ex) {
            throw new JsonParseException("Invalid RangedValue: " + s, ex);
        }
    }

    public boolean isWithin(double value) {
        return switch (mode) {
            case EQUAL -> value == b;
            case NOT_EQUAL -> value != b;
            case GREATER -> value > b;
            case GREATER_EQUAL -> value >= b;
            case LESS -> value < a;
            case LESS_EQUAL -> value <= a;
            case BETWEEN -> value >= a && value <= b;
        };
    }

    @Override
    public String toString() {
        return switch (mode) {
            case EQUAL -> "=" + a;
            case NOT_EQUAL -> "!=" + a;
            case GREATER -> ">" + a;
            case GREATER_EQUAL -> ">=" + a;
            case LESS -> "<" + a;
            case LESS_EQUAL -> "<=" + a;
            case BETWEEN -> a + "-" + b;
        };
    }

    public static class Deserializer implements JsonDeserializer<RangedValue> {
        @Override
        public RangedValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx)
                throws JsonParseException {

            if (!json.isJsonPrimitive())
                throw new JsonParseException("Expected primitive for RangedValue");

            String text = json.getAsString();
            return RangedValue.parse(text);
        }
    }
}
