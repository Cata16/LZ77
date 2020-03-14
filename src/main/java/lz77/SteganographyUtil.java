package lz77;

import java.util.ArrayList;
import java.util.List;

public class SteganographyUtil {
    private byte byteBuffer;
    private List<Byte> message = new ArrayList<>();
    private int bufferIndex = 0;


    public SteganographyUtil(byte[] message) {
        insertBytesInMessage(message);
        appendEndChar();
        bufferIndex = 8;

    }

    public SteganographyUtil() {

    }

    private boolean isBufferFull() {
        return bufferIndex == 8;
    }

    public boolean isMessageEmpty() {
        return (0 == message.size()) && isBufferFull();
    }

    public int readBit() {
        int bit = -1;
        if (isBufferFull()) {
            if (isMessageEmpty()) return bit;
            byteBuffer = message.get(0);
            System.out.println(byteBuffer);
            message.remove(0);
            bufferIndex = 0;
        }
        bit = ((byteBuffer >> (7 - bufferIndex)) & 1);
        bufferIndex++;
        return bit;


    }

    public int readNBits(int numberOfBits) {
        List<Boolean> result = new ArrayList<>();
        int bit;
        while (numberOfBits > 0) {
            bit = readBit();
            if (bit == -1) return -1;
            result.add(bit == 1);
            numberOfBits--;
        }
        return BitUtil.convertBitsToInt(result);
    }

    public void writeBitsToMessage(ArrayList<Boolean> bits) {
        if (endOfMessage()) return;
        for (Boolean bit : bits) {
            if (bit) {
                byteBuffer |= 1 << (7 - bufferIndex);
            }
            bufferIndex++;
            if (isBufferFull()) {
                System.out.println("write" + byteBuffer);
                message.add(byteBuffer);
                byteBuffer = 0;
                bufferIndex = 0;

            }
        }
    }

    public boolean endOfMessage() {
        if (message.size() != 0)
            return message.get(message.size() - 1) == -1;
        return false;
    }

    public void appendEndChar() {
        message.add(message.size(), (byte) 255);
    }

    private void insertBytesInMessage(byte[] bytes) {
        for (byte b : bytes) {
            message.add(b);
        }
    }

    public int getMessageSize() {
        return message.size();
    }

    public String getMessage() {
        if (message.size() == 0) return "";
        StringBuilder messageAsString = new StringBuilder();
        message.remove(message.get(message.size() - 1));
        for (Byte b : message) {
            messageAsString.append(converByteToChar(b));
        }
        return messageAsString.toString();
    }

    private char converByteToChar(byte b) {
        return (char) (b & 0xFF);
    }


}
