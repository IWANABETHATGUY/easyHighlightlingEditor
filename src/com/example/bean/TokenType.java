package com.example.bean;

public enum TokenType {
    ENDFILE, ERROR,
    /* reserved words */
    IF, ELSE, INT, RETURN, VOID, WRITE,
    /* multicharacter tokens */
    ID, NUM, KEYWORD,
    /* special symbols */
    PLUS, MINUS, MULTIPLY, TIMES, LT, LE, GT, GE, EQ, NE, SEMI, COMMA, LPAREN, RPAREN, LBRACK, RBRACK, LBRACE, RBRACE, COMMENT, ASSIGN
}
