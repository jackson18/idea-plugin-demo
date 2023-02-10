package com.example.ideaplugindemo.model;

public class CurWord {

    /**
     * 单词内容
     */
    private String text;
    /**
     * 单词在整个文件中的开始位置
     */
    private int start;
    /**
     * 单词在整个文件中的结束位置
     */
    private int end;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
