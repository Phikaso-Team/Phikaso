package com.android.phikaso.util;

public class StringUtil {
    public static String unescapeUnicode(String escaped) {
        if (!escaped.contains("\\u")) return escaped;
        String processed = "";
        int position = escaped.indexOf("\\u");
        while (position != -1) {
            if (position != 0) {
                processed += escaped.substring(0, position);
            }
            String token = escaped.substring(position + 2, position + 6);
            escaped = escaped.substring(position + 6);
            processed += (char) Integer.parseInt(token, 16);
            position = escaped.indexOf("\\u");
        }
        processed += escaped;
        return processed;
    }

    public static String removeHtmlTags(final String input) {
        return input.replaceAll("\\<[^>]*>","");
    }
}
