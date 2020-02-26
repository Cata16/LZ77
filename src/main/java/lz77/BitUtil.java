package lz77;

import java.util.ArrayList;
import java.util.List;

public class BitUtil {
    public static ArrayList<Boolean> convertIntToBits(int dataToConvert, int nrOfBits) {
        if (dataToConvert < 0) {
            return getBitArray(Math.abs(256 + dataToConvert), nrOfBits);
        }

        return getBitArray(Math.abs(dataToConvert), nrOfBits);
    }

    private static ArrayList<Boolean> getBitArray(int dataToConvert, int nrOfBits) {
        ArrayList<Boolean> result = new ArrayList<>(nrOfBits);
        while (Math.abs(dataToConvert) > 0) {
            result.add(0, (dataToConvert % 2) == 1);
            dataToConvert = (dataToConvert >> 1);
            nrOfBits--;
        }
        while (nrOfBits > 0) {
            result.add(0, false);
            nrOfBits--;
        }
        return result;
    }
    public static int convertBitsToInt(List<Boolean> bitArray) {
        int result = 0;
        int powerOf2 = 0;
        for (int i = bitArray.size() - 1; i >= 0; i--) {
            if (bitArray.get(i)) {
                result += Math.pow(2, powerOf2);

            }
            powerOf2++;
        }

        return result;

    }
}