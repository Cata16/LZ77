package lz77;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class BitReader {
    private static byte bufferReader;
    private static int numberOfReadBits;

    private static boolean isBufferEmpty() {
        return numberOfReadBits == 0;
    }

    public static int readBit(InputStream inputStream) throws IOException {
        int b;
        if (isBufferEmpty()) {
            numberOfReadBits = 8;
            if ((b = inputStream.read()) == -1) return -1;
            bufferReader = (byte) b;


        }
        numberOfReadBits--;
        return ((bufferReader >> (numberOfReadBits)) & 1);

    }

    public static ArrayList<Boolean> readNBits(int numberOfBits, InputStream inputStream) throws IOException {
        ArrayList<Boolean> result = new ArrayList<>(numberOfBits);
        int readedBit;
        int bitPosition = 0;
        while (bitPosition < numberOfBits) {
            readedBit = readBit(inputStream);
            if (readedBit == -1) return null;
            result.add(bitPosition, (readedBit == 1));
            bitPosition++;
        }
        return result;

    }


}