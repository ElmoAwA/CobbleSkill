package de.tomalbrc.skillcore.io;

import java.util.*;

public class ParseUtil {
    public static Map<String, Object> buildTypeMap(String token) {
        return buildTypeMap(token, "type");
    }

    public static Map<String, Object> buildTypeMap(String token, String typeFieldName) {
        Map<String, Object> m = new HashMap<>();
        int braceIdx = indexOfTopLevelChar(token, '{');
        if (braceIdx == -1) {
            m.put(typeFieldName.toLowerCase(Locale.ROOT), token.trim());
            return m;
        }
        String type = token.substring(0, braceIdx).trim();
        int end = findMatchingBrace(token, braceIdx);
        if (end == -1) end = token.length() - 1;
        String inner = token.substring(braceIdx + 1, end).trim();

        m.put(typeFieldName.toLowerCase(Locale.ROOT), type);
        m.putAll(parseArgsString(inner));
        return m;
    }

    public static List<String> tokenizeTopLevel(String s) {
        List<String> tokens = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        Deque<Character> stack = new ArrayDeque<>();
        boolean inSingle = false, inDouble = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\'' && !inDouble) { inSingle = !inSingle; cur.append(c); continue; }
            if (c == '"' && !inSingle) { inDouble = !inDouble; cur.append(c); continue; }

            if (!inSingle && !inDouble) {
                if (c == '{' || c == '[' || c == '(') { stack.push(c); cur.append(c); continue; }
                if (c == '}' || c == ']' || c == ')') { if (!stack.isEmpty()) stack.pop(); cur.append(c); continue; }
                if ((tokens.isEmpty() && Character.isWhitespace(c) && s.charAt(i+1)!='{' && stack.isEmpty()) || (Character.isWhitespace(c) && stack.isEmpty())) {
                    if (!cur.isEmpty()) { tokens.add(cur.toString()); cur.setLength(0); }
                    continue;
                }
            }
            cur.append(c);
        }
        if (!cur.isEmpty()) tokens.add(cur.toString());
        return tokens;
    }

    public static List<String> splitTopLevel(String s, char sep) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        Deque<Character> stack = new ArrayDeque<>();
        boolean inSingle = false, inDouble = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\'' && !inDouble) { inSingle = !inSingle; cur.append(c); continue; }
            if (c == '"' && !inSingle) { inDouble = !inDouble; cur.append(c); continue; }
            if (!inSingle && !inDouble) {
                if (c == '{' || c == '[' || c == '(') { stack.push(c); cur.append(c); continue; }
                if (c == '}' || c == ']' || c == ')') { if (!stack.isEmpty()) stack.pop(); cur.append(c); continue; }
                if (c == sep && stack.isEmpty()) { out.add(cur.toString()); cur.setLength(0); continue; }
            }
            cur.append(c);
        }
        if (!cur.isEmpty()) out.add(cur.toString());
        return out;
    }

    public static Map<String, Object> parseArgsString(String s) {
        Map<String, Object> args = new HashMap<>();
        if (s == null || s.isEmpty()) return args;
        List<String> parts = splitTopLevel(s, ';');
        for (String part : parts) {
            if (part == null)
                continue;

            String e = part.trim();
            if (e.isEmpty())
                continue;

            int eq = indexOfTopLevelChar(e, '=');
            if (eq == -1) {
                // handle args without value as bool flag
                args.put(e, Boolean.TRUE);
                continue;
            }
            String key = e.substring(0, eq).trim().toLowerCase(Locale.ROOT);
            String valRaw = e.substring(eq + 1).trim();

            if ((valRaw.startsWith("\"") && valRaw.endsWith("\"")) || (valRaw.startsWith("'") && valRaw.endsWith("'"))) {
                valRaw = valRaw.substring(1, valRaw.length() - 1);
            }

            if ("type".equals(key)) key = "subtype"; // ugly hack to support the MM 'type' field in some mechanics
            args.put(key, tryParseValue(valRaw));
        }

        return args;
    }

    public static Object tryParseValue(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return "";

        // array syntax
        if (t.startsWith("[") && t.endsWith("]")) {
            String inner = t.substring(1, t.length() - 1).trim();

            if (inner.startsWith("- ") || inner.startsWith("-\t") || inner.startsWith("-\n") || inner.startsWith("-\r")) {
                List<Object> list = new ArrayList<>();
                StringBuilder cur = new StringBuilder();
                Deque<Character> stack = new ArrayDeque<>();
                boolean inSingle = false, inDouble = false;

                for (int i = 0; i < inner.length(); i++) {
                    char c = inner.charAt(i);

                    if (c == '\'' && !inDouble) { inSingle = !inSingle; cur.append(c); continue; }
                    if (c == '"' && !inSingle) { inDouble = !inDouble; cur.append(c); continue; }

                    if (!inSingle && !inDouble) {
                        if (c == '{' || c == '[' || c == '(') { stack.push(c); cur.append(c); continue; }
                        if (c == '}' || c == ']' || c == ')') { if (!stack.isEmpty()) stack.pop(); cur.append(c); continue; }

                        if (c == '-' && stack.isEmpty()) {
                            boolean isNextWhitespace = (i + 1 < inner.length() && Character.isWhitespace(inner.charAt(i + 1)));
                            boolean isPrevWhitespace = (i == 0 || Character.isWhitespace(inner.charAt(i - 1)));

                            if (isNextWhitespace && isPrevWhitespace) {
                                String part = cur.toString().trim();
                                if (!part.isEmpty()) {
                                    list.add(tryParseValue(part));
                                }
                                cur.setLength(0);
                                continue;
                            }
                        }
                    }
                    cur.append(c);
                }

                String part = cur.toString().trim();
                if (!part.isEmpty()) {
                    list.add(tryParseValue(part));
                }
                return list;

            } else {
                List<String> parts = splitTopLevel(inner, ',');
                List<Object> list = new ArrayList<>();
                for (String p : parts) list.add(tryParseValue(p.trim()));
                return list;
            }
        }

        if (t.equalsIgnoreCase("true")) return Boolean.TRUE;
        if (t.equalsIgnoreCase("false")) return Boolean.FALSE;

        try {
            long lv = Long.parseLong(t);
            if (lv >= Integer.MIN_VALUE && lv <= Integer.MAX_VALUE) return (int) lv;
            return lv;
        } catch (NumberFormatException ignored) {}

        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException ignored) {}

        return t;
    }

    public static int indexOfTopLevelChar(String s, char ch) {
        Deque<Character> stack = new ArrayDeque<>();
        boolean inSingle = false, inDouble = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\'' && !inDouble) {
                inSingle = !inSingle;
                continue;
            }
            if (c == '"' && !inSingle) {
                inDouble = !inDouble;
                continue;
            }

            if (inSingle || inDouble) {
                continue;
            }

            if (c == ch && stack.isEmpty()) {
                return i;
            }

            if (c == '{' || c == '[' || c == '(') {
                stack.push(c);
                continue;
            }

            if (c == '}' || c == ']' || c == ')') {
                if (!stack.isEmpty()) {
                    char top = stack.peek();
                    if ((top == '{' && c == '}') ||
                            (top == '[' && c == ']') ||
                            (top == '(' && c == ')')) {
                        stack.pop();
                    } else {
                        stack.pop();
                    }
                }
            }
        }
        return -1;
    }

    public static int findMatchingBrace(String s, int start) {
        if (start < 0 || start >= s.length() || s.charAt(start) != '{') return -1;
        Deque<Character> stack = new ArrayDeque<>();
        boolean inSingle = false, inDouble = false;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\'' && !inDouble) inSingle = !inSingle;
            else if (c == '"' && !inSingle) inDouble = !inDouble;
            else if (!inSingle && !inDouble) {
                if (c == '{') stack.push('{');
                else if (c == '}') {
                    stack.pop();
                    if (stack.isEmpty()) return i;
                }
            }
        }
        return -1;
    }

    public static String toUpperUnderscore(String s) {
        if (s == null || s.isEmpty()) return "";
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                if (Character.isUpperCase(c) && i > 0 && Character.isLowerCase(s.charAt(i - 1))) out.append('_');
                out.append(Character.toUpperCase(c));
            } else if (c == '-' || c == ' ' || c == '_') {
                if (!out.isEmpty() && out.charAt(out.length() - 1) != '_') out.append('_');
            }
        }
        // collapse multiple underscores
        String res = out.toString().replaceAll("_+", "_");
        // trim underscores
        if (res.startsWith("_")) res = res.substring(1);
        if (res.endsWith("_")) res = res.substring(0, res.length() - 1);
        return res;
    }
}
