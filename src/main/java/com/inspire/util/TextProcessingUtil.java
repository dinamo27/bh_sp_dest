package com.inspire.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessingUtil {

    private TextProcessingUtil() {
        // Private constructor to prevent instantiation
    }

    public static String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        
        // Replace specific terms
        String normalized = text.replace("VALVOLE", "VAL VOLE")
                               .replace("DISTINTA", "DIS TINTA");
        
        // Replace special characters with spaces
        normalized = normalized.replaceAll("[^a-zA-Z0-9]", " ");
        
        // Normalize multiple spaces to single space
        normalized = normalized.replaceAll("\\s+", " ");
        
        // Trim leading and trailing spaces
        return normalized.trim();
    }

    public static String extractMaterialCode(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        // Pattern to match letter-number or number-letter combinations
        Pattern pattern = Pattern.compile("([a-zA-Z]+[0-9]+|[0-9]+[a-zA-Z]+)[a-zA-Z0-9]*");
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        return null;
    }

    public static String formatExtractedCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        
        // Limit code length to 40 characters if needed
        String limitedCode = code.length() > 40 ? code.substring(0, 40) : code;
        
        // Add prefix
        return "inSPIRe suggested from superseded notes: " + limitedCode;
    }
}