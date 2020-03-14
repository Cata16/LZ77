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
        bufferIndex = 0;


    }

    public SteganographyUtil() {

    }



    public boolean isMessageEmpty() {
        return (0 == message.size()) && isBufferEmpty();
    }

    public int readBit() {
        int bit = -1;
        if (isBufferEmpty()) {
            if (isMessageEmpty()) return bit;
            byteBuffer = message.get(0);
            message.remove(0);
            bufferIndex = 7;
        }
        bit = ((byteBuffer >> (bufferIndex)) & 1);
        bufferIndex--;
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
                byteBuffer |= 1 << (bufferIndex);
            }
            bufferIndex--;
            if (isBufferEmpty()) {
                message.add(byteBuffer);
                byteBuffer = 0;
                bufferIndex = 7;

            }
        }
    }

    private boolean isBufferEmpty() {
        return bufferIndex == -1;
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


    public int getMessageSize() {
        if (isMessageEmpty())
            return 0;
        System.out.println(message.size() + 1);
        return message.size() + 1;
    }
}
