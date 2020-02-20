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
    private final short newChar;

    public Match(int startIndex, int length, short newChar) {
        this.offset = startIndex;
        this.length = length;
        this.newChar = newChar;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public short getNewChar() {
        return newChar;
    }

    @Override
    public String toString() {
        return "Match{" +
                "offset=" + offset +
                ", length=" + length +
                ", newChar=" + newChar +
                '}';
    }
}
