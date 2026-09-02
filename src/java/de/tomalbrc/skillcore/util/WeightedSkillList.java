package de.tomalbrc.skillcore.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class WeightedSkillList {
    public static final double DEFAULT_WEIGHT = 1.0;
    private final List<SkillEntry> entries;

    public WeightedSkillList(List<SkillEntry> entries) {
        this.entries = entries == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public List<SkillEntry> getEntries() {
        return entries;
    }

    public String pickName() {
        if (entries.isEmpty()) return null;
        double total = 0.0;
        for (SkillEntry e : entries) total += Math.max(0.0, e.weight);
        if (total <= 0.0) { // uniform fallback
            return entries.get(ThreadLocalRandom.current().nextInt(entries.size())).name;
        }
        double r = ThreadLocalRandom.current().nextDouble(total);
        double acc = 0.0;
        for (SkillEntry e : entries) {
            acc += Math.max(0.0, e.weight);
            if (r < acc) return e.name;
        }
        return entries.get(entries.size() - 1).name; // numeric safety
    }

    public static final class SkillEntry {
        public final String name;
        public final double weight;
        public SkillEntry(String name, double weight) {
            this.name = Objects.requireNonNull(name).trim();
            this.weight = weight;
        }
        @Override public String toString() { return "SkillEntry{name='" + name + "', weight=" + weight + '}'; }
    }

    /**
     * "SkillOne 10, SkillTwo 0.5, SkillThree 0.1, SkillFour"
     * "[- message{m="..."} @trigger, - message{m="..."} @trigger ]"
     */
    public static final class Deserializer implements JsonDeserializer<WeightedSkillList> {

        @Override
        public WeightedSkillList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            if (json == null || json.isJsonNull()) return new WeightedSkillList(Collections.emptyList());
            if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
                throw new JsonParseException("WeightedSkillList deserializer only accepts JSON strings");
            }

            String raw = json.getAsString();
            List<String> tokens = splitTopLevel(raw);
            List<SkillEntry> out = new ArrayList<>(tokens.size());
            for (String token : tokens) {
                parseToken(token, out);
            }
            return new WeightedSkillList(out);
        }

        private static void parseToken(String token, List<SkillEntry> out) {
            if (token == null) return;
            String s = token.trim();
            if (s.isEmpty()) return;

            // strip surrounding brackets if whole token is wrapped like [ ... ]
            if (s.startsWith("[") && s.endsWith("]")) {
                s = s.substring(1, s.length() - 1).trim();
            }

            // remove leading dash used in inline YAML items
            if (s.startsWith("-")) s = s.substring(1).trim();

            // remove trailing comma if present
            if (s.endsWith(",")) s = s.substring(0, s.length() - 1).trim();

            if (s.isEmpty()) return;

            // find last whitespace that is at top-level (not inside quotes/braces/brackets/parens)
            int lastTopLevelWs = findLastTopLevelWhitespaceIndex(s);

            if (lastTopLevelWs >= 0) {
                String candidateWeight = s.substring(lastTopLevelWs + 1).trim();
                String namePart = s.substring(0, lastTopLevelWs).trim();
                double w = DEFAULT_WEIGHT;
                if (!candidateWeight.isEmpty() && isParsableDouble(candidateWeight)) {
                    try {
                        w = Double.parseDouble(candidateWeight);
                    } catch (NumberFormatException ignored) {
                        w = DEFAULT_WEIGHT;
                    }
                    if (!namePart.isEmpty()) {
                        out.add(new SkillEntry(namePart, w));
                        return;
                    }
                }
            }

            // fallback: entire token is the name
            out.add(new SkillEntry(s, DEFAULT_WEIGHT));
        }

        // returns index of last whitespace character which is top-level (not inside quotes/braces/[]/())
        private static int findLastTopLevelWhitespaceIndex(String s) {
            int last = -1;
            int braces = 0, brackets = 0, parens = 0;
            boolean inSingle = false, inDouble = false;
            boolean escape = false;

            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (escape) {
                    escape = false;
                    continue;
                }
                if (c == '\\') {
                    escape = true;
                    continue;
                }
                if (c == '"' && !inSingle) {
                    inDouble = !inDouble;
                    continue;
                }
                if (c == '\'' && !inDouble) {
                    inSingle = !inSingle;
                    continue;
                }
                if (!inSingle && !inDouble) {
                    if (c == '{') { braces++; continue; }
                    if (c == '}') { braces = Math.max(0, braces - 1); continue; }
                    if (c == '[') { brackets++; continue; }
                    if (c == ']') { brackets = Math.max(0, brackets - 1); continue; }
                    if (c == '(') { parens++; continue; }
                    if (c == ')') { parens = Math.max(0, parens - 1); continue; }

                    if (Character.isWhitespace(c) && braces == 0 && brackets == 0 && parens == 0) {
                        last = i;
                    }
                }
            }
            return last;
        }

        private static boolean isParsableDouble(String s) {
            if (s == null || s.isEmpty()) return false;
            // quick check: allow leading +/-, digits, optional decimal point, optional exponent
            int len = s.length();
            int i = 0;
            if (s.charAt(0) == '+' || s.charAt(0) == '-') {
                if (len == 1) return false;
                i = 1;
            }
            boolean hasDot = false, hasDigit = false;
            for (; i < len; i++) {
                char c = s.charAt(i);
                if (c >= '0' && c <= '9') { hasDigit = true; continue; }
                if (c == '.' && !hasDot) { hasDot = true; continue; }
                // accept exponent part 'e' or 'E' only if digits exist and it's not last char
                if ((c == 'e' || c == 'E') && hasDigit && i + 1 < len) {
                    // rest should be a signed/unsigned integer
                    String rest = s.substring(i + 1);
                    if (rest.startsWith("+") || rest.startsWith("-")) rest = rest.substring(1);
                    if (rest.isEmpty()) return false;
                    for (char rc : rest.toCharArray()) if (!Character.isDigit(rc)) return false;
                    return true;
                }
                return false;
            }
            return hasDigit;
        }

        // split by top-level commas (not inside quotes, braces, brackets, parentheses)
        private static List<String> splitTopLevel(String s) {
            List<String> result = new ArrayList<>();
            if (s == null) return result;
            StringBuilder cur = new StringBuilder();
            int braces = 0, brackets = 0, parens = 0;
            boolean inSingle = false, inDouble = false;
            boolean escape = false;

            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (escape) {
                    cur.append(c);
                    escape = false;
                    continue;
                }
                if (c == '\\') {
                    cur.append(c);
                    escape = true;
                    continue;
                }
                if (c == '"' && !inSingle) {
                    inDouble = !inDouble;
                    cur.append(c);
                    continue;
                }
                if (c == '\'' && !inDouble) {
                    inSingle = !inSingle;
                    cur.append(c);
                    continue;
                }
                if (!inSingle && !inDouble) {
                    if (c == '{') { braces++; cur.append(c); continue; }
                    if (c == '}') { braces = Math.max(0, braces - 1); cur.append(c); continue; }
                    if (c == '[') { brackets++; cur.append(c); continue; }
                    if (c == ']') { brackets = Math.max(0, brackets - 1); cur.append(c); continue; }
                    if (c == '(') { parens++; cur.append(c); continue; }
                    if (c == ')') { parens = Math.max(0, parens - 1); cur.append(c); continue; }

                    if (c == ',' && braces == 0 && brackets == 0 && parens == 0) {
                        String token = cur.toString().trim();
                        if (!token.isEmpty()) result.add(token);
                        cur.setLength(0);
                        continue;
                    }
                }
                cur.append(c);
            }
            String last = cur.toString().trim();
            if (!last.isEmpty()) result.add(last);
            return result;
        }
    }
}