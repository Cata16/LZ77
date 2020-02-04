import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LZ {
 
    static ArrayList<Byte> lookAheadBuffer = new ArrayList();
    static ArrayList<Byte> window = new ArrayList();
    static FileReader reader = null;
    static BufferedWriter writer = null;
    static int windowSize = 2048;
    static int lookAheadSize = 2048;
 
    /**
     *
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        File inputFile = new File("C:\\Users\\Catalin\\Desktop\\inStream.txt");
        File outputFileEncoded = new File("C:\\Users\\Catalin\\Desktop\\outStream.txt");
        File outputFileDecoded = new File("C:\\Users\\Catalin\\Desktop\\outStream1.txt");
        encode(inputFile, outputFileEncoded);
        decode(outputFileEncoded, outputFileDecoded);
 
    }
 
    public static void decode(File inputFile, File outputFile) throws FileNotFoundException, IOException {
        int offset = 0;
        int length = 0;
        int digitCount = 1;
        ArrayList<Character> decodeWindow = new ArrayList();
        reader = new FileReader(inputFile);
        writer = new BufferedWriter(new FileWriter(outputFile));
 
        fillLookAheadBuffer(reader);
        int i;
        while (!lookAheadBuffer.isEmpty()) {
            i = 0;
            if (((char) (lookAheadBuffer.get(i) & 0xFF)) == '(') {
                i++;
                while (('0' <= lookAheadBuffer.get(i) && (lookAheadBuffer.get(i) <= '9'))) {
                    offset = offset * digitCount + ((char) (lookAheadBuffer.get(i) & 0xFF) - '0');
                    digitCount = 10;
 
                    i++;
                }
                digitCount = 1;
                i++;
                while (('0' <= lookAheadBuffer.get(i) && (lookAheadBuffer.get(i) <= '9'))) {
                    length = length * digitCount + ((char) (lookAheadBuffer.get(i) & 0xFF) - '0');
                    digitCount = 10;
                    i++;
                }
                if (offset != 0) {
 
                    offset = decodeWindow.size() - offset;
                    for (int j = offset; j < offset + length; j++) {
                        writer.write(decodeWindow.get(j));
                        decodeWindow.add(decodeWindow.size(), decodeWindow.get(j));
 
                    }
 
                }
                i++;
                writer.write((char) ((lookAheadBuffer.get(i) & 0xFF)));
                decodeWindow.add(decodeWindow.size(), (char) ((lookAheadBuffer.get(i) & 0xFF)));
                i += 2;
 
                digitCount = 1;
                offset = 0;
                length = 0;
            } else {
                writer.write((char) ((lookAheadBuffer.get(i) & 0xFF)));
                decodeWindow.add(decodeWindow.size(), (char) ((lookAheadBuffer.get(i) & 0xFF)));
                moveWindow(decodeWindow);
 
            }
            moveLookAheadBuffer(i);
        }
        writer.close();
        reader.close();
 
    }
 
    public static void encode(File inputFile, File outputFile) throws IOException {
        try {
            reader = new FileReader(inputFile);
            writer = new BufferedWriter(new FileWriter(outputFile));
            fillLookAheadBuffer(reader);
            while (!lookAheadBuffer.isEmpty()) {
                Match match = findMatch(0);
                moveLookAheadBuffer(match.getLength() + 1);
                writer.write("(" + match.getOffset() + "," + match.getLength() + "," + match.getCh() + ")");
            }
            writer.close();
            reader.close();
            displaySpaceSaved(inputFile, outputFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LZ.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }
 
    public static Match findMatch(int chPosition) {
        int offset = 0;
        int length = 0;
        int windowPosition;
        int lookAheadPosition;
        byte chToMatch = lookAheadBuffer.get(chPosition);
        Match match = new Match(offset, length, (char) (chToMatch & 0xFF));
 
        if (!window.isEmpty()) {
            for (int i = window.size() - 1; i >= 0; i--) {
 
                if (window.get(i) == chToMatch) {
                    lookAheadPosition = chPosition;
                    offset = window.size() - i;
                    windowPosition = i;
                    do {
                        length++;
                        lookAheadPosition++;
                        windowPosition++;
 
                        if (lookAheadPosition >= lookAheadBuffer.size() || windowPosition >= window.size()) {
                            break;
                        }
 
                    } while (Objects.equals(lookAheadBuffer.get(lookAheadPosition), window.get(windowPosition)));
 
                    if ((length > match.getLength())) {
                        match.setLength(length);
                        match.setOffset(offset);
                        if (lookAheadPosition < lookAheadBuffer.size()) {
 
                            match.setCh((char) (lookAheadBuffer.get(lookAheadPosition) & 0xFF));
                        } else {
                            match.setLength(length - 1);
                            match.setCh((char) (lookAheadBuffer.get(lookAheadPosition - 1) & 0xFF));
                        }
                    }
                    length = 0;
                }
            }
        } else {
            insertCharsInWindow(match, chToMatch);
            return match;
        }
        insertCharsInWindow(match, chToMatch);
        moveWindow(window);
 
        return match;
    }
 
    public static void insertCharsInWindow(Match match, byte ch) {
        // Insert new chars in window
        int start = window.size() - match.getOffset();
        int stop = window.size() - match.getOffset() + match.getLength();
        for (int i = start; i < stop; i++) {
            window.add(window.size(), window.get(i));
        }
        window.add(window.size(), (byte) match.getCh());
 
    }
 
    public static void fillLookAheadBuffer(FileReader inputReader) {
 
        try {
            byte charValue;
            while (lookAheadBuffer.size() < lookAheadSize) {
                if ((charValue = (byte) inputReader.read()) != (-1)) {
 
                    lookAheadBuffer.add(charValue);
                } else {
                    break;
                }
 
            }
        } catch (IOException ex) {
            Logger.getLogger(LZ.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }
 
    public static void moveWindow(ArrayList array) {
        if (array.size() > windowSize) {
            int numberOfPositions = array.size() - windowSize;
 
            for (int i = 0; i < array.size() - numberOfPositions; i++) {
                Collections.swap(array, i, i + numberOfPositions);
            }
            for (int i = 0; i < numberOfPositions; i++) {
                array.remove(array.size() - 1);
            }
        }
    }
 
    public static void displaySpaceSaved(File inputFile, File outputFile) {
        long inputSize = inputFile.length();
        long outputSize = outputFile.length();
        long percentageDifference = ((100 * (inputSize - outputSize)) / inputSize);
        System.out.print((inputSize - outputSize) + "  bytes Saved" + "  " + percentageDifference);
 
    }
 
    private static void moveLookAheadBuffer(int numberOfPositions) {
 
        for (int i = 0; i < lookAheadBuffer.size() - numberOfPositions; i++) {
 
            Collections.swap(lookAheadBuffer, i, i + numberOfPositions);
        }
        for (int i = 0; i < numberOfPositions; i++) {
            lookAheadBuffer.remove(lookAheadBuffer.size() - 1);
        }
        fillLookAheadBuffer(reader);
 
    }
 
}