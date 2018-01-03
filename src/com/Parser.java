package com;

import com.example.Lex;
import com.example.NormalEntry;
import com.example.bean.Token;
import com.example.bean.TokenType;

import java.util.List;

public class Parser {
    private List<Token> tokenList;
    private Token token = null;
    private int cursor = 0;

    private Token errorToken = null;
    public static void main(String[] args) {
        String text = NormalEntry.readFileByLines("./test.txt");

        Lex lex = new Lex();
        lex.setLine(text, 0, text.length());
        List<Token> beforeParse = lex.getAllToken();
        Parser parser = new Parser();
        for (int i = 0,len = beforeParse.size(); i < len; i++) {
            if (beforeParse.get(i).getType() == TokenType.COMMENT|| beforeParse.get(i).getType() == TokenType.ERROR) {
                beforeParse.remove(i);
                len--;
            }
        }
        parser.setTokenList(beforeParse);
        parser.parse_program();
    }
    public void setTokenList(List<Token> tokenList) {
        this.tokenList = tokenList;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public Parser() {

    }
    private void back_track() {
        cursor--;
    }
    private void error() {
        if (cursor == tokenList.size()) {
            cursor -= 1;
            token = getToken();
            errorToken = token;
            cursor += 1;
            System.out.println("missing token after " + token.getContent() + " at position: " + token.getPos());
        } else {
            errorToken = token;
            System.out.println("unexpected token -> " + token.getContent() + " at position: " + token.getPos());
        }
    }

    private boolean matchType(TokenType expected) {
        if (token == null) {

            return false;
        }
        if (token.getType() == expected) {
            return true;
        } else {
            return false;
        }
    }

    private boolean matchStr(String expected) {
        if (token == null) {
            return false;
        }
        if (token.getContent().equals(expected)) {
            return true;
        } else {
            return false;
        }
    }
    private Token getToken() {
        Token t = null;
        if (cursor < tokenList.size()) {
            t = tokenList.get(cursor++);
        }
        return t;
    }
    // 语法分析所用到的文法
    public void parse_program() {
        parse_declaration_list();
    }

    private void parse_declaration_list() {
        while (cursor < tokenList.size()) {
            boolean flag = parse_declaration();
            if (!flag) {
                break;
            }
        }
    }

    private boolean parse_declaration() {
        if (parse_type_specifier()) {
            token =getToken();
            if (matchType(TokenType.ID)) {
                token = getToken();
                if (matchType(TokenType.LPAREN)) {
                    return parse_fun_declaration();
                }
                back_track();
            }
            back_track();
            return parse_var_dec();
        }
        error();
        return false;
    }

    private boolean parse_fun_declaration() {
        if (parse_params()) {
            token = getToken();
            if (matchType(TokenType.RPAREN)) {
                return parse_compound_stmt();
            }
            error();
            return false;
        }
        return false;
    }

    private boolean parse_type_specifier() {
        token = getToken();
        if (matchStr("int") || matchStr("void")) {
            return true;
        }
        return false;
    }

    private boolean parse_params() {
        token = getToken();
        if (matchStr("void")) {
            return true;
        }
        back_track();
        return parse_param_list();
    }

    private boolean parse_param_list() {
        if (parse_param()) {
            token = getToken();
            if (matchType(TokenType.COMMA)) {
                return parse_param_list();
            }
            back_track();
            return true;
        }
        return false;

    }
    private boolean parse_param() {
        if (parse_type_specifier()) {
            token = getToken();
            if (matchType(TokenType.ID)) {
                token = getToken();
                if (matchType(TokenType.LBRACK)) {
                    token = getToken();
                    if (matchType(TokenType.RBRACK)) {
                        return true;
                    }
                    error();
                    return false;
                }
                back_track();
                return true;
            } else {
                error();
                return false;
            }
        }
        error();
        return false;

    }

    private boolean parse_compound_stmt() {
        token = getToken();
        if (matchType(TokenType.LBRACE)) {
            if (parse_local_declarations()) {
                if (parse_stmt_list()) {
                    token = getToken();
                    return matchType(TokenType.RBRACE);
                }
                return false;
            }
            return false;
        }
        error();
        return false;
    }

    private boolean parse_local_declarations() {

        if (parse_type_specifier()) {
            if (parse_var_dec()) {
                return parse_local_declarations();
            }
        }
        back_track();
        return true;
    }

    private boolean parse_stmt_list() {
        token = getToken();
        if (matchStr("{") || matchStr("if") || matchStr("while")|| matchStr("return") || matchType(TokenType.ID)
                || matchType(TokenType.NUM) || matchStr(";") || matchStr("(")) {
            back_track();
            return parse_stmt();
        }
        back_track();
        return true;
    }

    private boolean parse_stmt() {
        token = getToken();
        boolean f = false;
        if (matchType(TokenType.LBRACE)) {
            back_track();
            f = parse_compound_stmt();
        } else if (matchStr("if")) {
            f = parse_selection_stmt();

        } else if (matchStr("while")) {
            f = parse_iteration_stmt();

        } else if (matchStr("return")) {
            f = parse_return_stmt();
        } else {
            back_track();
            f =  parse_expression_stmt();
        }
        if (f) {
            return parse_stmt_list();
        }
        return false;
    }

    private boolean parse_iteration_stmt() {
        token = getToken();
        if (matchType(TokenType.LPAREN)) {
            if (parse_expression()) {
                token = getToken();
                if (matchType(TokenType.RPAREN)) {
                    if (parse_stmt()) {
                        return true;
                    }
                }
                error();
                return false;
            }
        }
        return false;

    }

    private boolean parse_selection_stmt() {
        token = getToken();
        if (matchType(TokenType.LPAREN)) {
            if (parse_expression()) {
                token = getToken();
                if (parse_stmt()) {
                    token = getToken();
                    if (matchStr("else")) {
                        return parse_stmt();
                    }
                    back_track();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean parse_expression() {
        token =getToken();
        int temCursor = cursor;
        if (matchType(TokenType.ID)) {
            back_track();
            if (parse_var()) {
                token = getToken();
                if (matchType(TokenType.ASSIGN)) {
                    return parse_expression();
                }
            }
            setCursor(temCursor);
        }
        back_track();
        return parse_simple_expression();
    }

    private boolean parse_expression_stmt() {
        token =getToken();
        if (matchType(TokenType.SEMI)) {
            return true;
        }
        back_track();
        if (parse_expression()) {
            token = getToken();
            if (matchType(TokenType.SEMI)) {
                return true;
            }
            error();
            return false;
        }
        return false;
    }
    private boolean parse_return_stmt() {
        token = getToken();
        if (matchType(TokenType.SEMI)) {
            return true;
        }
        back_track();
        if (parse_expression()) {
            token =getToken();
            if (matchType(TokenType.SEMI)) {
                return true;
            }
            error();
        }
        return false;
    }
    private boolean parse_var() {
        token = getToken();
        if (matchType(TokenType.ID)) {
            token = getToken();
            if (matchType(TokenType.LBRACK)) {
                if (parse_expression()) {
                    token =getToken();
                    if (matchType(TokenType.RBRACK)) {
                        return true;
                    }
                }
                return false;
            }
            back_track();
            return true;
        }
        error();
        return false;
    }
    private boolean parse_simple_expression() {
        if (parse_additive_expression()) {

            if (parse_relop()) {
                return parse_additive_expression();
            }
            back_track();
            return true;
        }
        return false;
    }

    private boolean parse_arg_list() {
        token = getToken();
        if (matchType(TokenType.COMMA)) {
            if (parse_expression()) {
                return parse_arg_list();
            }
        }
        back_track();
        return true;
    }
    private boolean parse_call() {
        token = getToken();
        if (matchType(TokenType.RPAREN)) {
            return true;
        }
        back_track();
        if (parse_args()) {
            token = getToken();
            if (matchType(TokenType.RPAREN)) {
                return true;
            }
            error();
            return false;
        }
        return false;

    }
    private boolean parse_additive_expression() {
        if (parse_term()) {
            if (parse_addop()) {
                return parse_additive_expression();
            }
            back_track();
            return true;
        }
        return false;
    }


    private boolean parse_term() {
        if (parse_factor()) {
            if (parse_mulop()) {
                return parse_term();
            }
            back_track();
            return true;
        }
        return false;
    }
    private boolean parse_factor() {
        token = getToken();
        if (matchType(TokenType.NUM)) {
            return true;
        } else if (matchType(TokenType.LPAREN)) {
            if (parse_expression()) {
                token = getToken();
                if (matchType(TokenType.RPAREN)) {
                    return true;
                }
                error();
                return false;
            }
            return false;
        } else if (matchType(TokenType.ID)) {
            token = getToken();
            if (matchType(TokenType.LPAREN)) {
                return parse_call();
            } else {
                back_track();
                back_track();
                return parse_var();
            }
        }
        error();
        return false;
    }
    private boolean parse_args() {
        int tem = cursor;
        if (parse_expression()) {
            return parse_arg_list();
        }
        setCursor(tem);
        return true;
    }
    private boolean parse_addop() {
        token = getToken();
        if (matchType(TokenType.PLUS) || matchType(TokenType.MINUS)) {
            return true;
        }
        return false;
    }
    private boolean parse_mulop() {
        token = getToken();
        if (matchType(TokenType.MULTIPLY) || matchType(TokenType.TIMES)) {
            return true;
        }
        return false;
    }
    private boolean parse_relop() {
        token = getToken();
        if (matchType(TokenType.EQ) || matchType(TokenType.NE)|| matchType(TokenType.GT)|| matchType(TokenType.GE) || matchType(TokenType.LE) || matchType(TokenType.LT)) {
            return true;
        }
        return false;
    }
    private boolean parse_var_dec() {
        token = getToken();
        if (matchType(TokenType.ID)) {
            token = getToken();
            if (matchType(TokenType.SEMI)) {
                return true;
            } else if (matchType(TokenType.LBRACK)) {
                token = getToken();
                if (matchType(TokenType.NUM)) {
                    token = getToken();
                    if (matchType(TokenType.RBRACK)) {
                        token = getToken();
                        if (matchType(TokenType.SEMI)) {
                            return true;
                        }
                        error();
                        return false;
                    }
                    error();
                    return false;
                }
                error();
                return false;
            }
            error();
            return false;
        }
        error();
        return false;
    }
}
