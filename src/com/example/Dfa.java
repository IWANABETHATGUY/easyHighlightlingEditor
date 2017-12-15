package com.example;

import com.example.Util.TextUtil;
import com.example.bean.Token;

import java.util.*;
import com.example.Dfa.*;

import static com.example.Dfa.TokenType.*;
import static com.example.Dfa.StateType.*;


public class Dfa {
    private int cursor = 0;
    private String Line = "";
    private int lineCount = 0;
    private int totalLen = 0;
    private boolean DISPLAY_COMMENT = true;

    public enum TokenType {
        ENDFILE, ERROR,
        /* reserved words */
        IF, ELSE, INT, RETURN, VOID, WRITE,
        /* multicharacter tokens */
        ID, NUM, KEYWORD,
        /* special symbols */
        PLUS, MINUS, MULTIPLY, TIMES, LT, LE, GT, GE, EQ, NE, SEMI, COMMA, LPAREN, RPAREN, LBRACK, RBRACK, LBRACE, RBRACE, COMMENT, ASSIGN
    }

    public enum StateType {
        START, INDIVIDE, INMULTPLY, INNUM, INID, DONE, INLESS, INGREAT, INASSIGN, INNOTEQUEAL, INCOMMENT, INECOMMENT
    }


    public Dfa() {

    }


    private TokenType isKeyWrodToken(String s) {
        if (TextUtil.isKeyword(s)) {
            return KEYWORD;
        } else {
            return ID;

        }
    }

    //    private static boolean test1() {
//        Set<Integer> set = new HashSet<>();
//    }
    private void ungetNextChar() {
        cursor--;
    }

    public void setLine(String line, int pos, int len) {
        Line = line;
        cursor = pos;
        totalLen = pos + len;
        lineCount = 0;
    }

    public List<Token> getAllToken() {
        List<Token> tokens = new ArrayList<>();
        if (!Line.equals("")) {

            while (cursor < totalLen) {
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
        TokenType currentToken = null;
        StateType state = START;
        while (state != DONE && cursor < len) {
            char cur = Line.charAt(cursor++);
            save = true;
            switch (state) {
                case START:
                    startIndex = cursor - 1 - lineCount;
                    if ((TextUtil.isDigit(cur)))
                        state = INNUM;
                    else if (TextUtil.isLetter(cur))
                        state = INID;
                    else if (cur == '<')
                        state = INLESS;
                    else if (cur == '\r') {
                        lineCount++;
                        save = false;
                    }
                    else if ((cur == ' ') || (cur == '\t') || (cur == '\n')) {
                        save = false;
                        if (cursor == len) {
                            state = DONE;
                        }
                    } else if (cur == '/') {
                        state = INDIVIDE;
                        if (cursor == totalLen) {
                            state = DONE;
                            currentToken = TIMES;
                        }
                    } else if (cur == '>') {
                        state = INGREAT;
                    } else if (cur == '!') {
                        state = INNOTEQUEAL;
                    } else if (cur == '=') {
                        state = INASSIGN;
                    } else {
                        state = DONE;
                        switch (cur) {
                            case '+':
                                currentToken = PLUS;
                                break;
                            case '*':
                                currentToken = MULTIPLY;
                                break;
                            case '-':
                                currentToken = MINUS;
                                break;
                            case '(':
                                currentToken = LPAREN;
                                break;
                            case ')':
                                currentToken = RPAREN;
                                break;
                            case ';':
                                currentToken = SEMI;
                                break;
                            case ',':
                                currentToken = COMMA;
                                break;
                            case '[':
                                currentToken = LBRACK;
                                break;
                            case ']':
                                currentToken = RBRACK;
                                break;
                            case '{':
                                currentToken = LBRACE;
                                break;
                            case '}':
                                currentToken = RBRACE;
                                break;
                            default:
                                currentToken = ERROR;
                                break;
                        }
                    }
                    break;
                case INDIVIDE:
                    state = DONE;
                    if (cur == '*') {
                        state = INCOMMENT;
                    } else {
                        ungetNextChar();
                        save = false;
                        currentToken = TIMES;
                    }
                    break;
                case INLESS:
                    state = DONE;
                    if (cur == '=') {
                        currentToken = LE;
                    } else {
                        ungetNextChar();
                        save = false;
                        currentToken = LT;
                    }
                    break;
                case INGREAT:
                    state = DONE;
                    if (cur == '=') {
                        currentToken = GE;
                    } else {
                        ungetNextChar();
                        save = false;
                        currentToken = GT;
                    }
                    break;
                case INASSIGN:
                    state = DONE;
                    if (cur == '=') {
                        currentToken = EQ;
                    } else { /* backup in the input */
                        ungetNextChar();
                        save = false;
                        currentToken = ASSIGN;
                    }
                    break;
                case INNOTEQUEAL:
                    state = DONE;
                    if (cur == '=') {
                        currentToken = NE;
                    } else { /* backup in the input */
                        ungetNextChar();
                        save = false;
                        currentToken = ERROR;
                    }
                    break;
                case INNUM:
                    if (!TextUtil.isDigit(cur)) { /* backup in the input */

                        ungetNextChar();
                        save = false;
                        state = DONE;
                        currentToken = NUM;
                    }
                    break;
                case INID:

                    if (!TextUtil.isLetter(cur)) {/* backup in the input */

                        ungetNextChar();
                        save = false;
                        state = DONE;
                        currentToken = ID;
                    }
                    break;
                case INCOMMENT:
                    if (cur == '*') {
                        state = INECOMMENT;
                    }
                    break;
                case INECOMMENT:
                    if (cur == '/') {
                        state = START;
                        save = false;
                        if (DISPLAY_COMMENT) {
//                            printToken(COMMENT, result + '/');
                        }
                        result = "";
                    } else if (cur == '*') {
                        state = INECOMMENT;
                    } else {
                        state = INCOMMENT;
                    }
                    break;
                case DONE:
                    break;
                default: /* should never happen */
                    state = DONE;
                    currentToken = ERROR;
                    break;

            }
            if (save) {
                result += (char) cur;
            }
            if (state == DONE) {
//                tokenMap.put(currentToken, result);
//                printToken(currentToken, result);
                if (currentToken == null) {
                    currentToken = ERROR;
                }
                if (currentToken == ID) {
                    currentToken = isKeyWrodToken(result);
                }
                int relen = result.length();
                token = new Token(currentToken.toString(), result, relen, startIndex);
                result = "";
            }
        }
        return token;
    }
}
