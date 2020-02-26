package lz77;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class BitWriter {
    private static byte bufferWriter;
    private static int numberOfBitsWrite;

    private static boolean isBufferFull() {
        return numberOfBitsWrite == 8;
    }

    private static void writeBit(boolean value, OutputStream outputStream) throws IOException {

        if (value) {
            bufferWriter |= 1 << (7 - numberOfBitsWrite);
        }
        numberOfBitsWrite++;
        if (isBufferFull()) {

            outputStream.write(bufferWriter);
            numberOfBitsWrite = 0;
            bufferWriter = 0;
        }
    }

    public static void writerNBits(ArrayList<Boolean> bits, OutputStream outputStream) throws IOException {

        for (Boolean bit : bits) {
            writeBit(bit, outputStream);
        }
    }
    public static void clearBufferWriter(OutputStream outputStream) throws IOException {
        for (int i = 0; i < 7; i++) {
            writeBit(false ,outputStream);
        }
    }


}