package aftergames.engine.utils;

import java.util.ArrayList;

/**
 *
 * @author KiQDominaN
 */
public class StringUtils {

    public static String make_string() {
        return new String().intern();
    }

    public static String make_string(String s) {
        return s.intern();
    }

    public static String replace(String s, String pattern, String replace) {
        while (s.contains(pattern))
            s = s.replace(pattern, replace);

        return s;
    }

    public static String[] splitString(String str, String separator) {
        ArrayList<String> strings = new ArrayList<>();

        int start = 0, end, skip = separator.length();

        str = concat(str, separator);

        while (start < str.length()) {
            end = str.indexOf(separator, start);
            strings.add(str.substring(start, end));
            start = end + skip;
        }

        return strings.toArray(new String[strings.size()]);
    }

    private static final StringBuilder sb = new StringBuilder(64);

    public static String concat(String... strings) {
        sb.delete(0, sb.length());

        for (String s : strings) sb.append(s);

        return sb.toString();
    }

    public static String concat(Object... strings) {
        sb.delete(0, sb.length());

        for (Object s : strings) sb.append(String.valueOf(s));

        return sb.toString();
    }

    public static final String arrayToString(String[] strings) {
        sb.delete(0, sb.length());

        for (String s : strings) sb.append(s);

        return sb.toString();
    }

    public static final String arrayToString(String[] strings, String splitter) {
        sb.delete(0, sb.length());

        for (String s : strings) {
            sb.append(s);
            sb.append(splitter);
        }

        return sb.toString();
    }
}
