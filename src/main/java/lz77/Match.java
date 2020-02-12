package lz77;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Catalin
 */
public class Match {
    private final int offset;
    private final int length;
    private final byte newChar;

    public Match(int startIndex, int length, byte newChar) {
        this.offset = startIndex;
        this.length = length;
        this.newChar = newChar;
    }

    public byte getNewChar() {
        return newChar;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

}
