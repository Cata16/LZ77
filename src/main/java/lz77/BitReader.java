package lz77;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class BitReader {
    private final InputStream inputStream;
    private byte bufferReader;
    private int numberOfReadBits;

    public BitReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private boolean isBufferEmpty() {
        return numberOfReadBits == 0;
    }

    public void close() throws IOException {
        this.inputStream.close();
    }

    public int readBit() throws IOException {
        int b;
        if (isBufferEmpty()) {
            numberOfReadBits = 8;
            if ((b = inputStream.read()) == -1) return -1;
            bufferReader = (byte) b;


        }
        numberOfReadBits--;
        return ((bufferReader >> (numberOfReadBits)) & 1);

    }

    public ArrayList<Boolean> readNBits(int numberOfBits) throws IOException {
        ArrayList<Boolean> result = new ArrayList<>(numberOfBits);
        int readedBit;
        int bitPosition = 0;
        while (bitPosition < numberOfBits) {
            readedBit = readBit();
            if (readedBit == -1) return null;
            result.add(bitPosition, (readedBit == 1));
            bitPosition++;
        }
        return result;

    }

}