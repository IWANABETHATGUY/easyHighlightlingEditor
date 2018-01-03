package com.example;

import com.example.Util.TextUtil;
import com.example.bean.Token;

import java.util.*;

import com.example.bean.TokenType;
import com.example.bean.StateType;


public class Lex {
    private int corsor = 0;
    private String Line = "";
    private int lineCount = 0;
    private int totalLen = 0;
    private boolean DISPLAY_COMMENT = true;

    public Lex() {

    }


    private TokenType isKeyWrodToken(String s) {
        if (TextUtil.isKeyword(s)) {
            return TokenType.KEYWORD;
        } else {
            return TokenType.ID;

        }
    }


    private void ungetNextChar() {
        corsor--;
    }

    public void setLine(String line, int pos, int len) {
        Line = line;
        corsor = pos;
        totalLen = pos + len;
        lineCount = 0;
    }

    public List<Token> getAllToken() {
        List<Token> tokens = new ArrayList<>();
        if (!Line.equals("")) {

            while (corsor < totalLen) {
                tokens.add(getToken(totalLen));
            }
        }
        return tokens;
    }

    private Token getToken(int len) {
        String result = "";
        boolean save;
        int startIndex = 0;
        Token token = null;
        int commentThroughLine = 0;
        TokenType currentToken = null;
        StateType state = StateType.START;
        while (state != StateType.DONE && corsor < len) {
            char cur = Line.charAt(corsor++);
            save = true;
            switch (state) {
                case START:
                    startIndex = corsor - 1 - lineCount;
                    if ((TextUtil.isDigit(cur))) {
                        state = StateType.INNUM;
                        if (corsor == len) {
                            state = StateType.DONE;
                            currentToken = TokenType.NUM;
                        }
                    }

                    else if (TextUtil.isLetter(cur)) {
                        state = StateType.INID;
                        if (corsor == len) {
                            state = StateType.DONE;
                            currentToken = TokenType.ID;
                        }
                    }

                    else if (cur == '<') {
                        state = StateType.INLESS;
                        if (corsor == len) {
                            state = StateType.DONE;
                            currentToken = TokenType.LE;
                        }
                    }
                    else if (cur == '\r') {
                        lineCount++;
                        save = false;
                    }
                    else if ((cur == ' ') || (cur == '\t') || (cur == '\n')) {
                        save = false;
                        if (corsor == len) {
                            state = StateType.DONE;
                        }
                    } else if (cur == '/') {
                        state = StateType.INDIVIDE;
                        if (corsor == len) {
                            state = StateType.DONE;
                            currentToken = TokenType.TIMES;
                        }
                    } else if (cur == '>') {
                        state = StateType.INGREAT;
                        if (corsor == len) {
                            state = StateType.DONE;
                            currentToken = TokenType.GT;
                        }
                    } else if (cur == '!') {

                        if (corsor ==len) {
                            state = StateType.DONE;
                            currentToken = TokenType.ERROR;
                        } else {
                            state = StateType.INNOTEQUAL;
                        }

                    } else if (cur == '=') {
                        state = StateType.INASSIGN;
                        if (corsor ==len) {
                            state = StateType.DONE;
                            currentToken = TokenType.EQ;
                        }
                    } else {
                        state = StateType.DONE;
                        switch (cur) {
                            case '+':
                                currentToken = TokenType.PLUS;
                                break;
                            case '*':
                                currentToken = TokenType.MULTIPLY;
                                break;
                            case '-':
                                currentToken = TokenType.MINUS;
                                break;
                            case '(':
                                currentToken = TokenType.LPAREN;
                                break;
                            case ')':
                                currentToken = TokenType.RPAREN;
                                break;
                            case ';':
                                currentToken = TokenType.SEMI;
                                break;
                            case ',':
                                currentToken = TokenType.COMMA;
                                break;
                            case '[':
                                currentToken = TokenType.LBRACK;
                                break;
                            case ']':
                                currentToken = TokenType.RBRACK;
                                break;
                            case '{':
                                currentToken = TokenType.LBRACE;
                                break;
                            case '}':
                                currentToken = TokenType.RBRACE;
                                break;
                            default:
                                currentToken = TokenType.ERROR;
                                break;
                        }
                    }
                    break;
                case INDIVIDE:
                    if (cur == '*') {
                        state = StateType.INCOMMENT;
                        if (corsor == len) {
                            state = StateType.DONE;
                            currentToken = TokenType.COMMENT;
                        }
                    } else {
                        state = StateType.DONE;
                        ungetNextChar();
                        save = false;
                        currentToken = TokenType.TIMES;
                    }
                    break;
                case INLESS:
                    state = StateType.DONE;
                    if (cur == '=') {
                        currentToken = TokenType.LE;
                    } else {
                        ungetNextChar();
                        save = false;
                        currentToken = TokenType.LT;
                    }
                    break;
                case INGREAT:
                    state = StateType.DONE;
                    if (cur == '=') {
                        currentToken = TokenType.GE;
                    } else {
                        ungetNextChar();
                        save = false;
                        currentToken = TokenType.GT;
                    }
                    break;
                case INASSIGN:
                    state = StateType.DONE;
                    if (cur == '=') {
                        currentToken = TokenType.EQ;
                    } else { /* backup in the input */
                        ungetNextChar();
                        save = false;
                        currentToken = TokenType.ASSIGN;
                    }
                    break;
                case INNOTEQUAL:
                    state = StateType.DONE;
                    if (cur == '=') {
                        currentToken = TokenType.NE;
                    } else { /* backup in the input */
                        ungetNextChar();
                        save = false;
                        currentToken = TokenType.ERROR;
                    }
                    break;
                case INNUM:
                    if (!TextUtil.isDigit(cur)) { /* backup in the input */
                        ungetNextChar();
                        save = false;
                        state = StateType.DONE;
                        currentToken = TokenType.NUM;
                    } else if (corsor == len) {
                        state = StateType.DONE;
                        currentToken = TokenType.NUM;
                    }
                    break;
                case INID:
                    if (!TextUtil.isLetter(cur)) {/* backup in the input */
                        ungetNextChar();
                        save = false;
                        state = StateType.DONE;
                        currentToken = TokenType.ID;
                    } else if (corsor == len) {
                        state = StateType.DONE;
                        currentToken = TokenType.ID;
                    }
                    break;
                case INCOMMENT:
                    if (cur == '*') {
                        state = StateType.INECOMMENT;
                        if (corsor == len) {
                            state = StateType.DONE;
                            currentToken = TokenType.COMMENT;
                        }
                    } else if (cur == '\r'){
                        commentThroughLine ++;
                    }
                    else if (corsor == len) {
                        state = StateType.DONE;
                        currentToken = TokenType.COMMENT;
                    }
                    break;
                case INECOMMENT:
                    if (cur == '/') {
                        state = StateType.START;
                        if (DISPLAY_COMMENT) {
                            state = StateType.DONE;
                            currentToken = TokenType.COMMENT;
                        }
                    } else if (cur == '*') {
                        state = StateType.INECOMMENT;
                    } else if(corsor == totalLen){
                        state = StateType.DONE;
                        currentToken = TokenType.COMMENT;
                    } else {
                        state = StateType.INCOMMENT;
                    }
                    break;
                case DONE:
                    break;
                default: /* should never happen */
                    state = StateType.DONE;
                    currentToken = TokenType.ERROR;
                    break;

            }
            if (save) {
                result += (char) cur;
            }
            if (state == StateType.DONE) {
//                tokenMap.put(currentToken, result);
//                printToken(currentToken, result);
                if (currentToken == null ) {
                    currentToken = TokenType.ERROR;
                }
                if (currentToken == TokenType.ID) {
                    currentToken = isKeyWrodToken(result);
                }

                int relen = result.length();
                if (currentToken == TokenType.COMMENT) {
                    relen = relen - (commentThroughLine);
                }
                token = new Token(currentToken, result, relen, startIndex);
                result = "";
            }
        }
        return token;
    }
}
