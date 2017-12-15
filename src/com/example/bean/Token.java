package com.example.bean;

import com.example.Dfa;

public class Token {
    private String Type;
    private String Content;
    private int len;
    private int pos;

    @Override
    public String toString() {
        return "Token{" +
                "Type='" + Type + '\'' +
                ", Content='" + Content + '\'' +
                ", len=" + len +
                ", pos=" + pos +
                '}';
    }

    public Token(String type, String content, int len, int pos) {
        Type = type;
        Content = content;
        this.len = len;
        this.pos = pos;
    }

    public String getType() {

        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
