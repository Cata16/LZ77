package lz77;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenUtil {
    static final Pattern tokenPattern = Pattern.compile("\\((\\d+),(\\d+),(-?\\d+)\\)");
    static final byte[] byteBuffer = new byte[32];

    public static byte[] encodeMatch(Match match) {
        String token = "(" + match.getOffset() + "," + match.getLength() + "," + match.getNewChar() + ")";
        return token.getBytes();
    }

    public static Match decodeMatch(byte[] data) {
        String tokenString = new String(data);

        Matcher matcher = tokenPattern.matcher(tokenString);
        if (matcher.find()) {
            int offset = Integer.parseInt(matcher.group(1));
            int length = Integer.parseInt(matcher.group(2));
            byte newChar = Byte.parseByte(matcher.group(3));
            return new Match(offset, length, newChar);
        }
        return null;
    }

    public static Match readToken(InputStream inputStream) {
        try {
            int bufferIndex = 0;
            // we read a token from the stream
            do {
                int newChar = inputStream.read();
                if (newChar == -1) {
                    break;
                }
                byte c = (byte) newChar;
                byteBuffer[bufferIndex++] = c;
                if (c == (byte) ')') {
                    break;
                }
            } while (true);
            return decodeMatch(Arrays.copyOf(byteBuffer, bufferIndex));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // this only serves as a test
    public static void main(String[] args) {
        Match match = new Match(1, 2, (byte) 3);
        byte[] encodedMatch = encodeMatch(match);
        Match decodedMatch1 = decodeMatch(encodedMatch);
        System.out.println(decodedMatch1);

        String token2 = "(1,2,3)";
        Match decodedMatch2 = decodeMatch(token2.getBytes());
        System.out.println(decodedMatch2);

        String token3 = "(2,3)";
        Match decodedMatch3 = decodeMatch(token3.getBytes());
        System.out.println(decodedMatch3);

        String token4 = "(2,3,-4)";
        Match decodedMatch4 = decodeMatch(token4.getBytes());
        System.out.println(decodedMatch4);
    }
}
