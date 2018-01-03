package com.example.TextEditor;

import com.example.Lex;
import com.example.Main;
import com.example.bean.Token;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;

public class Editor extends JFrame{

    private Lex lex = new Lex();

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
            StyledDocument m_doc = editor.getStyledDocument();
            editor.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == 123) {
                        String line = editor.getText();
                        eeee.lex.setLine(line, 0, line.length());
                        System.out.println(line.length());
                        eeee.Print(eeee.lex.getAllToken());
                    }
                }
            });
            editor.setFont(new Font("Dialog", Font.PLAIN, 20));

            editor.getDocument().addDocumentListener(new SyntaxHighlighter(editor));

            JScrollPane scrollPane = new JScrollPane(editor);
            scrollPane.setVerticalScrollBarPolicy(
                    javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

            frame.getContentPane().add(scrollPane);
            frame.setResizable(true);
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

    private SimpleAttributeSet COMMENT = new SimpleAttributeSet();

    private Lex lex;

    private JEditorPane ed;

    public SyntaxHighlighter(JTextPane editor) {
        // 准备着色使用的样式
        ed = editor;
        Main main = new Main();
        HashMap<String, String> map = main.parseXML("c_minus_config.xml");


        StyleConstants.setForeground(NORMAL, Color.black);

        if (map.get("number") != null) {
            StyleConstants.setForeground(NUMBER, Color.decode(map.get("number")));
        } else {
            StyleConstants.setForeground(NUMBER, Color.green);
        }

        if (map.get("keyword") != null) {
            StyleConstants.setForeground(KEYWORD, Color.decode(map.get("keyword")));
        } else {
            StyleConstants.setForeground(KEYWORD, Color.decode("#ff0000"));
        }
        StyleConstants.setBold(KEYWORD, true);


        if (map.get("id") != null) {
            StyleConstants.setForeground(ID, Color.decode(map.get("id")));
        } else {
            StyleConstants.setForeground(ID, Color.blue);
        }

        if (map.get("comment") != null) {
            StyleConstants.setForeground(COMMENT, Color.decode(map.get("comment")));
        } else {
            StyleConstants.setForeground(COMMENT, Color.decode("#cccccc"));
        }


        lex = new Lex();
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
                case KEYWORD:
                    SwingUtilities.invokeLater(new ColouringTask(m_doc, token.getPos(), token.getLen(), KEYWORD));
                    break;
                case ID:
                    SwingUtilities.invokeLater(new ColouringTask(m_doc, token.getPos(), token.getLen(), ID));
                    break;
                case NUM:
                    SwingUtilities.invokeLater(new ColouringTask(m_doc, token.getPos(), token.getLen(), NUMBER));
                    break;
                case COMMENT:
                    SwingUtilities.invokeLater(new ColouringTask(m_doc, token.getPos(), token.getLen(), COMMENT));
                    break;
                default:
                    SwingUtilities.invokeLater(new ColouringTask(m_doc, token.getPos(), token.getLen(), NORMAL));
                    break;
            }
        }
    }


    @Override
    public void changedUpdate(DocumentEvent e) {
        StyledDocument sd = (StyledDocument) e.getDocument();
        String line = ed.getText();
        lex.setLine(line, 0, line.length());
        EventQueue.invokeLater(() -> {
            Print(lex.getAllToken(), sd);
        });


    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        StyledDocument sd = (StyledDocument) e.getDocument();
        String line = ed.getText();
        lex.setLine(line, 0, line.length());
        EventQueue.invokeLater(() -> {
            Print(lex.getAllToken(), sd);
        });

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        // 因为删除后光标紧接着影响的单词两边, 所以长度就不需要了

        StyledDocument sd = (StyledDocument) e.getDocument();
        String line = ed.getText();
        lex.setLine(line, 0, line.length());
        EventQueue.invokeLater(() -> {
            Print(lex.getAllToken(), sd);
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
                EventQueue.invokeLater(() -> {
                    doc.setCharacterAttributes(pos, len, styleSet, false);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

