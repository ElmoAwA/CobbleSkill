package de.tomalbrc.skillcore.impl.variable;

import net.minecraft.world.phys.Vec3;

import java.util.*;

/**
 * Immutable typed runtime value for the skill system.
 * - Overloaded Variable.of(...) factories
 * - Strict type-based numeric behavior (no instanceof Number)
 * - Null-safe and avoids unnecessary boxing
 */
public final class Variable {
    public static final Variable EMPTY = new Variable(null, null);

    private final Object val;
    private final Type type;

    private Variable(Object val, Type type) {
        this.val = val;
        this.type = type;
    }

    public static Variable of(String v)  { return new Variable(v, Type.STRING); }
    public static Variable of(Integer v) { return new Variable(v, Type.INTEGER); }
    public static Variable of(Float v)   { return new Variable(v, Type.FLOAT); }
    public static Variable of(Double v)  { return new Variable(v, Type.DOUBLE); }
    public static Variable of(Boolean v) { return new Variable(v, Type.BOOLEAN); }
    public static Variable of(Set<?> v)  { return new Variable(v, Type.SET); }
    public static Variable of(List<?> v) { return new Variable(v, Type.LIST); }
    public static Variable of(Map<?, ?> v) { return new Variable(v, Type.MAP); }
    public static Variable of(Vec3 v)    { return new Variable(v, Type.POSITION); }
    public static Variable of(Object v, Type t) { return new Variable(v, t); }

    public Type type() { return type; }
    public Object getRaw() { return val; }

    public boolean isEmpty() { return val == null || type == null; }
    public boolean isNumber() { return type != null && type.isNumeric(); }

    public double asDoublePrimitive() {
        if (val == null) return 0.0;
        if (val instanceof Number) return ((Number) val).doubleValue();
        if (val instanceof String s) {
            try { return Double.parseDouble(s.trim()); } catch (NumberFormatException ignored) {}
        }
        return 0.0;
    }

    public float asFloatPrimitive() {
        switch (val) {
            case null -> {
                return 0f;
            }
            case Number number -> {
                return number.floatValue();
            }
            case String s -> {
                try {
                    return Float.parseFloat(s.trim());
                } catch (NumberFormatException ignored) {
                }
            }
            default -> {
            }
        }
        return 0f;
    }

    public int asIntPrimitive() {
        switch (val) {
            case null -> {
                return 0;
            }
            case Number number -> {
                return number.intValue();
            }
            case String s -> {
                try {
                    return Integer.parseInt(s.trim());
                } catch (NumberFormatException ignored) {
                }
            }
            default -> {
            }
        }
        return 0;
    }

    /* -------------------------
       Other type accessors
       ------------------------- */

    public String asString() {
        return val == null ? null : String.valueOf(val);
    }

    public Boolean asBoolean() {
        if (val == null || type == null) return null;
        if (type == Type.BOOLEAN) return (Boolean) val;
        if (type == Type.STRING) {
            String s = ((String) val).trim().toLowerCase(Locale.ROOT);
            if ("true".equals(s)) return true;
            if ("false".equals(s)) return false;
        }
        return null;
    }

    public Vec3 asVec3() { return type == Type.POSITION ? (Vec3) val : null; }
    public List<?> asList() { return type == Type.LIST ? (List<?>) val : null; }
    public Set<?> asSet() { return type == Type.SET ? (Set<?>) val : null; }
    public Map<?,?> asMap() { return type == Type.MAP ? (Map<?,?>) val : null; }

    /* -------------------------
       Safe arithmetic helpers
       ------------------------- */

    private static Type promoteNumericType(Type a, Type b) {
        if (a == Type.DOUBLE || b == Type.DOUBLE) return Type.DOUBLE;
        if (a == Type.FLOAT  || b == Type.FLOAT)  return Type.FLOAT;
        return Type.INTEGER;
    }

    public Variable add(Variable other) {
        if (other == null || this.isEmpty() || other.isEmpty()) return EMPTY;

        switch (this.type) {
            case INTEGER:
            case FLOAT:
            case DOUBLE:
                if (!other.isNumber()) return EMPTY;
                Type resultType = promoteNumericType(this.type, other.type);
                if (resultType == Type.DOUBLE)
                    return Variable.of(this.asDoublePrimitive() + other.asDoublePrimitive());
                if (resultType == Type.FLOAT)
                    return Variable.of(this.asFloatPrimitive() + other.asFloatPrimitive());

                return Variable.of(this.asIntPrimitive() + other.asIntPrimitive());
            case STRING:
                return Variable.of(this.asString() + other.asString());
            case LIST: {
                List<Object> list = new ArrayList<>(this.asList());
                if (other.type == Type.LIST) {
                    assert other.asList() != null;
                    list.addAll(other.asList());
                }
                else list.add(other.getRaw());
                return Variable.of(list);
            }
            case SET: {
                Set<Object> set = new HashSet<>(this.asSet());
                if (other.type == Type.SET) {
                    assert other.asSet() != null;
                    set.addAll(other.asSet());
                }
                else set.add(other.getRaw());
                return Variable.of(set);
            }
            case POSITION:
                if (other.type == Type.POSITION)
                    return Variable.of(this.asVec3().add(other.asVec3()));
                break;
            default:
                break;
        }
        return EMPTY;
    }

    public Variable subtract(Variable other) {
        if (other == null || this.isEmpty() || other.isEmpty()) return EMPTY;

        switch (this.type) {
            case INTEGER:
            case FLOAT:
            case DOUBLE:
                if (!other.isNumber()) return EMPTY;
                Type resultType = promoteNumericType(this.type, other.type);
                if (resultType == Type.DOUBLE)
                    return Variable.of(this.asDoublePrimitive() - other.asDoublePrimitive());
                if (resultType == Type.FLOAT)
                    return Variable.of(this.asFloatPrimitive() - other.asFloatPrimitive());
                return Variable.of(this.asIntPrimitive() - other.asIntPrimitive());
            case LIST: {
                List<Object> list = new ArrayList<>(this.asList());
                if (other.type == Type.LIST) {
                    assert other.asList() != null;
                    list.removeAll(other.asList());
                }
                else list.remove(other.getRaw());
                return Variable.of(list);
            }
            case SET: {
                Set<Object> set = new HashSet<>(this.asSet());
                if (other.type == Type.SET) {
                    assert other.asSet() != null;
                    set.removeAll(other.asSet());
                }
                else set.remove(other.getRaw());
                return Variable.of(set);
            }
            case POSITION:
                if (other.type == Type.POSITION)
                    return Variable.of(this.asVec3().subtract(other.asVec3()));
                break;
            default:
                break;
        }
        return EMPTY;
    }

    public Variable append(Variable other) { return add(other); }
    public Variable remove(Variable other) { return subtract(other); }

    /* -------------------------
       Object overrides
       ------------------------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable that)) return false;
        return Objects.equals(val, that.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(val, type);
    }

    @Override
    public String toString() {
        return "Variable{" + "type=" + type + ", val=" + val + '}';
    }

    /* -------------------------
       Enums
       ------------------------- */

    public enum Type {
        INTEGER, FLOAT, DOUBLE, STRING, BOOLEAN, SET, LIST, MAP, POSITION;
        public boolean isNumeric() {
            return this == INTEGER || this == FLOAT || this == DOUBLE;
        }
    }

    public enum Scope {
        SKILL, CASTER, TARGET, WORLD, GLOBAL;
        public static boolean isScope(String scope) {
            for (Scope value : values())
                if (value.name().equalsIgnoreCase(scope))
                    return true;
            return false;
        }
    }
}
