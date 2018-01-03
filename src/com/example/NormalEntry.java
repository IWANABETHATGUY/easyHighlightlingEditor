package com.example;

import com.example.bean.Token;

import java.io.*;
import java.util.List;

public class NormalEntry {
    public static void main(String[] args) {
        Lex lex = new Lex();
        String text = readFileByLines("./test.txt");
        lex.setLine(text, 0, text.length());
        List<Token> tokenList = lex.getAllToken();
        for (Token t: tokenList) {
            System.out.println(t);
        }
    }

    public static String readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;

        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));

            String tempString;
            while ((tempString = reader.readLine()) != null) {
                builder.append(tempString);
                builder.append("\n");
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return builder.toString();
    }
}
