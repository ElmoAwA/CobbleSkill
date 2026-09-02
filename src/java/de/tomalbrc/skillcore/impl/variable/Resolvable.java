package de.tomalbrc.skillcore.impl.variable;

import com.google.gson.*;
import de.tomalbrc.skillcore.api.GlobalStates;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("unused")
// variable format: "<scope>.var.<name>"
// ex: "skill.var.mything"
public class Resolvable<T> {
    private final boolean isReference;
    private final Variable.Scope scope;
    private final String variable;
    private final T literalValue;

    private Resolvable(boolean isReference, Variable.Scope scope, String variable, T literalValue) {
        this.isReference = isReference;
        this.scope = scope;
        this.variable = variable;
        this.literalValue = literalValue;
    }

    public static <T> Resolvable<T> reference(Variable.Scope scope, String variable) {
        return new Resolvable<>(true, scope, variable, null);
    }

    public static <T> Resolvable<T> literal(T value) {
        return new Resolvable<>(false, null, null, value);
    }

    public static <T> Resolvable<T> nullable() {
        return new Resolvable<>(false, null, null, null);
    }

    public boolean isReference() {
        return isReference;
    }

    public Variable.Scope getScope() {
        return scope;
    }

    public String getVariable() {
        return variable;
    }

    public T getLiteralValue() {
        return literalValue;
    }

    @SuppressWarnings("unchecked")
    public T resolve(SkillTree tree, @Nullable Target target) {
        if (!isReference) return literalValue;
        Map<String, Variable> scopedMap = switch (scope) {
            case SKILL -> tree.vars();
            case CASTER -> tree.caster().getVariables();
            case TARGET -> {
                if (!target.isEntity()) yield null;
                yield target.getEntity().getVariables();
            }
            case WORLD -> GlobalStates.getWorldVariables(tree.level().dimension());
            case GLOBAL -> GlobalStates.getGlobalVariables();
        };

        if (scopedMap == null)
            return null;

        Variable val = scopedMap.get(variable);
        if (val == null)
            return null;
        try {
            return (T) val.getRaw();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Type mismatch for variable '" + variable + "'", e);
        }
    }

    public T resolve(SkillTree tree) {
        return resolve(tree, null);
    }

    @Override
    public String toString() {
        if (isReference) return "<" + scope.name().toLowerCase() + ".var." + variable + ">";
        return String.valueOf(literalValue);
    }

    public static class Deserializer<T> implements JsonDeserializer<Resolvable<T>> {
        @Override
        public Resolvable<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Type paramType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];

            if (json.isJsonPrimitive()) {
                JsonPrimitive primitive = json.getAsJsonPrimitive();

                if (primitive.isString()) {
                    String s = primitive.getAsString();
                    if (s.startsWith("<") && s.endsWith(">")) {
                        String inner = s.substring(1, s.length() - 1);
                        String[] parts = inner.split("\\.");
                        if (parts.length >= 3 && "var".equalsIgnoreCase(parts[1])) {
                            Variable.Scope scope = Variable.Scope.valueOf(parts[0].toUpperCase(Locale.ROOT));
                            return Resolvable.reference(scope, parts[2]);
                        }
                    }
                }
            }

            T val = context.deserialize(json, paramType);
            return Resolvable.literal(val);
        }
    }
}
