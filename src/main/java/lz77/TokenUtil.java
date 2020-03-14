package lz77;


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class TokenUtil {
    public static int nrBitsOffset;
    public static int nrBitsLength;
    public static final int nrBitsChar = 8;

    public static void writeHeader(OutputStream outputStream, int nrBitsOffsetValue, int nrBitsLengthValue) throws IOException {
        nrBitsLength = nrBitsLengthValue;
        nrBitsOffset = nrBitsOffsetValue;
        BitWriter.writerNBits(BitUtil.convertIntToBits(nrBitsOffset, 4), outputStream);
        BitWriter.writerNBits(BitUtil.convertIntToBits(nrBitsLength, 3), outputStream);

    }

    public static void readHeader(BitReader bitReader) throws IOException {

        nrBitsOffset = BitUtil.convertBitsToInt(bitReader.readNBits(4));
        nrBitsLength = BitUtil.convertBitsToInt(bitReader.readNBits(3));
    }

    public static Match readToken(BitReader bitReader) throws IOException {
        ArrayList<Boolean> bitBuffer;
        bitBuffer = bitReader.readNBits(nrBitsLength + nrBitsOffset + nrBitsChar);
        if (bitBuffer == null) {
            return null;
        }
        return decodeMatch(bitBuffer);
    }

    public static void writeToken(OutputStream writer, Match match) throws IOException {

        BitWriter.writerNBits(encodeMatch(match), writer);
        System.out.println(match);

    }

    public static Match decodeMatch(ArrayList<Boolean> data) {
        Match result;
        int offset = BitUtil.convertBitsToInt(data.subList(0, nrBitsOffset));
        int length = BitUtil.convertBitsToInt(data.subList(nrBitsOffset, nrBitsOffset + nrBitsLength));
        byte newChar = (byte) BitUtil.convertBitsToInt(data.subList(nrBitsLength + nrBitsOffset, data.size()));
        result = new Match(offset, length, newChar);

        return result;
    }

    private static  ArrayList<Boolean> encodeMatch(Match match) {
        ArrayList<Boolean> result = new ArrayList<>();
        result.addAll(BitUtil.convertIntToBits(match.getOffset(), nrBitsOffset));
        result.addAll(BitUtil.convertIntToBits(match.getLength(), nrBitsLength));
        result.addAll((BitUtil.convertIntToBits(match.getNewChar(), nrBitsChar)));

        return result;
    }
}