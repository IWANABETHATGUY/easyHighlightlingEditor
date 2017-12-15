package com.example.Util;

public class TextUtil {
    public static boolean isDigit(char a) {
        return Character.isDigit(a);
    }
    public static boolean isLetter(char a) {
        return Character.isAlphabetic(a);
    }
    public static boolean isID(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!isLetter(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    public static boolean isNum(String num){
        if (num == null || num.length() == 0) {
            return false;
        } else {
            for (int i = 0, len = num.length(); i < len; i++) {
                if (!isDigit(num.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }
    public static boolean isKeyword(String str) {
        if (str.equals("else") || str.equals("if") || str.equals("int") || str.equals("return") || str.equals("void") || str.equals("while")) {
            return true;
        }
        return false;
    }

}
