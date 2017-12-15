package com.example.TextEditor;

import com.example.Dfa;
import com.example.bean.Token;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class Editor extends JFrame{

    private Dfa dfa= new Dfa();

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Editor eeee = new Editor();

            JFrame frame = new JFrame();
            frame.setSize(800, 600);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);


            // 颜色初始化

            JTextPane editor = new JTextPane();
            editor.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == 123) {
                        String line = editor.getText();
                        eeee.dfa.setLine(line, 0, line.length());
                        eeee.Print(eeee.dfa.getAllToken());
                    }
                }
            });
            StyledDocument m_doc = editor.getStyledDocument();

            editor.getDocument().addDocumentListener(new SyntaxHighlighter(editor));

            frame.getContentPane().add(editor);
        });
    }

    public void Print(List<Token> tokesList) {
        for (int i = 0, len = tokesList.size(); i < len; i++) {
            Token token = tokesList.get(i);
            if (token == null) {
                continue;
            }
            System.out.println(String.format("%s %s %d %d", token.getType(), token.getContent(), token.getPos(), token.getLen()));
        }
    }

}



class SyntaxHighlighter implements DocumentListener {
    private SimpleAttributeSet NORMAL = new SimpleAttributeSet();

    private SimpleAttributeSet NUMBER = new SimpleAttributeSet();

    private SimpleAttributeSet ID = new SimpleAttributeSet();

    private SimpleAttributeSet KEYWORD = new SimpleAttributeSet();

    private Dfa dfa;

    private JEditorPane ed;

    public SyntaxHighlighter(JTextPane editor) {
        // 准备着色使用的样式
        ed = editor;
        StyleConstants.setForeground(NORMAL, Color.black);

        StyleConstants.setForeground(NUMBER, Color.green);

        StyleConstants.setForeground(KEYWORD, Color.red);
        StyleConstants.setBold(KEYWORD, true);

        StyleConstants.setForeground(ID, Color.blue);

        dfa = new Dfa();
    }

//    private void colouring(StyledDocument doc, int pos, int len) throws BadLocationException {
//        // 取得插入或者删除后影响到的单词.
//        // 例如"public"在b后插入一个空格, 就变成了:"pub lic", 这时就有两个单词要处理:"pub"和"lic"
//        // 这时要取得的范围是pub中p前面的位置和lic中c后面的位置
//
//    }

    public void Print(List<Token> tokesList, StyledDocument m_doc) {
        for (int i = 0, len = tokesList.size(); i < len; i++) {
            Token token = tokesList.get(i);
            if (token == null) {
                continue;
            }
            switch (token.getType()) {
                case "KEYWORD":
                    SwingUtilities.invokeLater(new ColouringTask(m_doc, token.getPos(), token.getLen(), KEYWORD));
                    break;
                case "ID":
                    SwingUtilities.invokeLater(new ColouringTask(m_doc, token.getPos(), token.getLen(), ID));
                    break;
                case "NUM":
                    SwingUtilities.invokeLater(new ColouringTask(m_doc, token.getPos(), token.getLen(), NUMBER));
                    break;
                default:
                    SwingUtilities.invokeLater(new ColouringTask(m_doc, token.getPos(), token.getLen(), NORMAL));
                    break;
            }
        }
    }


    @Override
    public void changedUpdate(DocumentEvent e) {
//        StyledDocument sd = (StyledDocument) e.getDocument();
//        String line = ed.getText();
//        dfa.setLine(line, 0, line.length());
//        EventQueue.invokeLater(() -> {
//            Print(dfa.getAllToken(), sd);
//        });


    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        StyledDocument sd = (StyledDocument) e.getDocument();
        String line = ed.getText();
        dfa.setLine(line, 0, line.length());
        EventQueue.invokeLater(() -> {
            Print(dfa.getAllToken(), sd);
        });

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        // 因为删除后光标紧接着影响的单词两边, 所以长度就不需要了

        StyledDocument sd = (StyledDocument) e.getDocument();
        String line = ed.getText();
        dfa.setLine(line, 0, line.length());
        EventQueue.invokeLater(() -> {
            Print(dfa.getAllToken(), sd);
        });


    }

    /**
     * 完成着色任务
     *
     * @author Biao
     */
    private class ColouringTask implements Runnable {
        private StyledDocument doc;
        private SimpleAttributeSet styleSet;
        private int pos;
        private int len;

        public ColouringTask(StyledDocument doc, int pos, int len, SimpleAttributeSet style) {
            this.doc = doc;
            this.pos = pos;
            this.len = len;
            this.styleSet = style;
        }

        public void run() {
            try {
                // 这里就是对字符进行着色
                doc.setCharacterAttributes(pos, len, styleSet, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}