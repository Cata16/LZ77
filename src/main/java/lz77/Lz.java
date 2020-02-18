package lz77;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lz {
    private final List<Byte> lookAheadBuffer = new ArrayList<>();
    private final List<Byte> window = new ArrayList<>();
    private final List<Byte> decodeWindow = new ArrayList<>();
    private final int windowSize = 16000;
    private final int lookAheadSize = 16000;

    public Lz() {
    }

    public void encode(String inputFilename, String outputFilename) throws IOException {
        FileInputStream reader = new FileInputStream(inputFilename);
        FileOutputStream writer = new FileOutputStream(outputFilename);

        fillLookAheadBuffer(reader);
        while (!lookAheadBuffer.isEmpty()) {
            Match match = findMatch();
            TokenUtil.writeToken(writer, match);
            moveLookAheadBuffer(reader, match.getLength() + 1);
            limitWindow(window);
        }

        writer.close();
        reader.close();
    }

    public void decode(String inputFilename, String outputFilename) throws IOException {
        FileInputStream reader = new FileInputStream(inputFilename);
        FileOutputStream writer = new FileOutputStream(outputFilename);

        do {
            Match match = TokenUtil.readToken(reader);
            if (match == null) {
                break;
            }
            outputDecodedChars(writer, match, decodeWindow);
            limitWindow(decodeWindow);
        }
        while (true);

        writer.close();
        reader.close();
    }

    // this needs to be transformed into findMatches - meaning that it returns all the possible choices for the encoder
    private Match findMatch() {
        int tokenOffset = 0;
        int tokenLength = 0;
        byte tokenNewChar = lookAheadBuffer.get(0);

        if (!window.isEmpty()) {
            for (int i = window.size() - 1; i >= 0; i--) {
                byte chToMatch = lookAheadBuffer.get(0);
                if (window.get(i) == chToMatch) {
                    int length = 0;
                    int offset = window.size() - i;
                    int lookAheadPosition = 0;
                    int windowPosition = i;
                    do {
                        length++;
                        lookAheadPosition++;
                        windowPosition++;

                        if (lookAheadPosition >= lookAheadBuffer.size() || windowPosition >= window.size()) {
                            break;
                        }
                    } while (lookAheadBuffer.get(lookAheadPosition).equals(window.get(windowPosition)));

                    if (length > tokenLength) {
                        tokenOffset = offset;
                        if (lookAheadPosition < lookAheadBuffer.size()) {
                            tokenLength = length;
                            tokenNewChar = lookAheadBuffer.get(lookAheadPosition);
                        } else {
                            tokenLength = length - 1;
                            tokenNewChar = lookAheadBuffer.get(lookAheadPosition - 1);
                        }
                    }
                }
            }
        }
        Match match = new Match(tokenOffset, tokenLength, tokenNewChar);
        insertCharsInWindow(match);
        return match;
    }

    private void outputDecodedChars(OutputStream writer, Match match, List<Byte> array) throws IOException {
        int start = array.size() - match.getOffset();
        int stop = start + match.getLength();
        for (int i = start; i < stop; i++) {
            writer.write(array.get(i));
            array.add(array.get(i));
        }
        writer.write(match.getNewChar());
        array.add(match.getNewChar());
    }

    private void insertCharsInWindow(Match match) {
        int start = window.size() - match.getOffset();
        int stop = start + match.getLength();
        for (int i = start; i < stop; i++) {
            window.add(window.get(i));
        }
        window.add(match.getNewChar());
    }

    private void limitWindow(List<Byte> array) {
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

    private void moveLookAheadBuffer(InputStream reader, int numberOfPositions) throws IOException {
        for (int i = 0; i < lookAheadBuffer.size() - numberOfPositions; i++) {
            Collections.swap(lookAheadBuffer, i, i + numberOfPositions);
        }
        for (int i = 0; i < numberOfPositions; i++) {
            lookAheadBuffer.remove(lookAheadBuffer.size() - 1);
        }
        fillLookAheadBuffer(reader);
    }

    private void fillLookAheadBuffer(InputStream inputStream) throws IOException {
        while (lookAheadBuffer.size() < lookAheadSize) {
            int read = inputStream.read();
            if (read != (-1)) {
                lookAheadBuffer.add((byte) read);
            } else {
                break;
            }
        }
    }

    public String getSpaceSaved(String inputFile, String outputFile) {
        long inputSize = new File(inputFile).length(); // I'm sure there is a better way than this
        long outputSize = new File(outputFile).length();
        long percentageDifference = ((100 * (inputSize - outputSize)) / inputSize);
        return ((inputSize - outputSize) + "  bytes Saved" + "  " + percentageDifference + "%");
    }
}
