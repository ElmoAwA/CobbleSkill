package de.tomalbrc.skillcore.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class DisguiseSyntaxParser {
    public record DisguiseCommand(String type, Map<String, String> options) {
            public DisguiseCommand(String type, Map<String, String> options) {
                this.type = type;
                this.options = Collections.unmodifiableMap(new LinkedHashMap<>(options));
            }

            @Override
            public @NotNull String toString() {
                return "DisguiseCommand{type=" + type + ", options=" + options + "}";
            }

            public boolean getBoolean(String key, boolean defaultValue) {
                String v = options.get(key);

                if (v == null)
                    return defaultValue;

                v = v.toLowerCase(Locale.ROOT);
                if ("true".equals(v) || "yes".equals(v) || "on".equals(v)) return true;
                if ("false".equals(v) || "no".equals(v) || "off".equals(v)) return false;

                return false;
            }

            public OptionalInt getInt(String key) {
                String v = options.get(key);
                if (v == null) return OptionalInt.empty();
                try {
                    return OptionalInt.of(Integer.parseInt(v));
                } catch (NumberFormatException ex) {
                    return OptionalInt.empty();
                }
            }

            public Optional<String> getString(String key) {
                return Optional.ofNullable(options.get(key));
            }
        }

    public static DisguiseCommand parse(String input) {
        if (input == null)
            return null;

        List<String> tokens = tokenize(input);

        if (tokens.isEmpty())
            return null;

        String type = tokens.getFirst();
        Map<String, String> options = new LinkedHashMap<>();

        int i = 1;
        while (i < tokens.size()) {
            String token = tokens.get(i);
            if (!startsWithSet(token)) {
                //throw new IllegalArgumentException("Expected option starting with 'set', got: " + token);
                i++;
                continue;
            }
            String key = token.substring(3).toLowerCase(Locale.ROOT);
            if (key.isEmpty()) throw new IllegalArgumentException("Empty option name in token: " + token);

            if (i + 1 >= tokens.size() || startsWithSet(tokens.get(i + 1))) {
                options.put(key, "true");
                i += 1;
            } else {
                String value = tokens.get(i + 1);
                options.put(key, value);
                i += 2;
            }
        }

        return new DisguiseCommand(type, options);
    }

    private static boolean startsWithSet(String s) {
        if (s.length() < "set".length()) return false;
        return s.regionMatches(true, 0, "set", 0, "set".length());
    }

    private static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>(8);
        StringBuilder cur = new StringBuilder();
        boolean inQuote = false;
        char quoteChar = 0;

        for (int idx = 0; idx < input.length(); idx++) {
            char c = input.charAt(idx);

            if (!inQuote && (c == '"' || c == '\'')) {
                inQuote = true;
                quoteChar = c;
                continue;
            }

            if (inQuote && c == quoteChar) {
                inQuote = false;
                quoteChar = 0;
                continue;
            }

            if (!inQuote && Character.isWhitespace(c)) {
                if (!cur.isEmpty()) {
                    tokens.add(cur.toString());
                    cur.setLength(0);
                }
                continue;
            }

            cur.append(c);
        }

        if (inQuote) {
            throw new IllegalArgumentException("Unterminated quoted string");
        }

        if (!cur.isEmpty())
            tokens.add(cur.toString());

        return tokens;
    }
}
