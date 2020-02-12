package main.java.com.lz77;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Catalin
 */
public class Match {
    private int offset;
    private int length;
    private char ch;

    public Match(int startIndex, int length, char ch) {
        this.offset = startIndex;
        this.length = length;
        this.ch = ch;
    }
    

    public char getCh() {
        return ch;
    }

    public void setCh(char ch) {
        this.ch = ch;
    }
   

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
    
}
