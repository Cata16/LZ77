package lz77;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Lz {
    private ArrayList<Byte> lookAheadBuffer = new ArrayList<>();
    private ArrayList<Byte> window = new ArrayList<>();
    private ArrayList<Byte> decodeWindow = new ArrayList<>();
    private int windowSize = 16000;
    private int lookAheadSize = 16000;

    public Lz() {
    }

    public void decode(String inputFilename, String outputFilename) throws IOException {
        FileInputStream reader = new FileInputStream(inputFilename);
        FileOutputStream writer = new FileOutputStream(outputFilename);

        fillLookAheadBuffer(reader);
        while (!lookAheadBuffer.isEmpty()) {
            int positionsToMove = 0;
            if (lookAheadBuffer.get(positionsToMove).equals((byte) '(')) {
                Match match = searchForMatch();
                if (match == null) {
                    writer.write('(');
                    decodeWindow.add(decodeWindow.size(), (byte) '(');
                    positionsToMove = 1;
                } else {
                    outputDecodedChars(writer, match, decodeWindow);
                    positionsToMove = getNumberOfPositionsToMove(match);
                }
            } else {
                writer.write(lookAheadBuffer.get(positionsToMove));
                decodeWindow.add(decodeWindow.size(), lookAheadBuffer.get(positionsToMove));
                positionsToMove = 1;
            }

            moveLookAheadBuffer(reader, positionsToMove);
            moveWindow(decodeWindow);
        }
        writer.close();
        reader.close();

    }

    private int getNumberOfPositionsToMove(Match match) {
        int counter = 0;
        if (match.getNewChar() == ')') {
            for (int j = 0; j < lookAheadBuffer.size(); j++) {
                if (lookAheadBuffer.get(j) == ')') {
                    counter++;
                }
                if (counter == 2) {
                    return j + 1;
                }
            }
        } else {
            return lookAheadBuffer.indexOf((byte) ')') + 1;
        }
        return -1;
    }

    // this will also be removed since there will always be only tokens in the encoded file
    private Match searchForMatch() {
        int position = 1;
        String startIndex = "";
        String length = "";
        Match match;
        while ((0 <= lookAheadBuffer.get(position) - '0') && ((lookAheadBuffer.get(position) - '0') <= 9)) {
            startIndex += (lookAheadBuffer.get(position) - '0');
            position++;
        }
        if ((lookAheadBuffer.get(position)) != ',') {
            return null;
        }
        position++;
        while ((0 <= (lookAheadBuffer.get(position) - '0')) && ((lookAheadBuffer.get(position) - '0') <= 9)) {
            length += ((char) (lookAheadBuffer.get(position) & 0xFF) - '0');
            position++;
        }
        if ((lookAheadBuffer.get(position) != ',') || (lookAheadBuffer.get(position + 2) != ')')) {
            return null;
        }
        match = new Match(Integer.parseInt(startIndex), Integer.parseInt(length), lookAheadBuffer.get(position + 1));
        return match;
    }

    public void encode(String inputFilename, String outputFilename) throws IOException {
        FileInputStream reader = new FileInputStream(inputFilename);
        FileOutputStream writer = new FileOutputStream(outputFilename);

        fillLookAheadBuffer(reader);
        while (!lookAheadBuffer.isEmpty()) {
            Match match = findMatch();
            outputEncodedChars(writer, match);
            moveLookAheadBuffer(reader, match.getLength() + 1);
            moveWindow(window);
        }

        writer.close();
        reader.close();
    }

    // this needs to be transformed into findMatches - meaning that it returns all the possible choices for the encoder
    private Match findMatch() {
        int tokenOffset = 0;
        int tokenLength = 0;
        byte tokenNewChar = 0;

        byte chToMatch = lookAheadBuffer.get(0);

        if (!window.isEmpty()) {
            for (int i = window.size() - 1; i >= 0; i--) {
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
                    } while (Objects.equals(lookAheadBuffer.get(lookAheadPosition), window.get(windowPosition)));

                    if ((length > tokenLength)) {
                        tokenLength = length;
                        tokenOffset = offset;
                        if (lookAheadPosition < lookAheadBuffer.size()) {
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

    // this method will be changed completely to output tokens in binary
    // the changes made here are only for compatibility with the old code
    private void outputEncodedChars(OutputStream writer, Match match) throws IOException {
        writer.write((byte) '(');
//      writer.write(match.getOffset() + "," + match.getLength() + "," + match.getNewChar() + ")");
        writer.write((byte) ')');
    }

    private void outputDecodedChars(OutputStream writer, Match match, ArrayList<Byte> source) throws IOException {
        int start = source.size() - match.getOffset();
        int stop = start + match.getLength();
        for (int i = start; i < stop; i++) {
            writer.write(source.get(i));
            source.add(source.size(), source.get(i));
        }
        writer.write(match.getNewChar());
        source.add(source.size(), match.getNewChar());
    }

    private void insertCharsInWindow(Match match) {
        int start = window.size() - match.getOffset();
        int stop = window.size() - match.getOffset() + match.getLength();
        for (int i = start; i < stop; i++) {
            window.add(window.size(), window.get(i));
        }
        window.add(window.size(), match.getNewChar());
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

    private void moveWindow(ArrayList array) {
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

    public String getSpaceSaved(String inputFile, String outputFile) {
        long inputSize = new File(inputFile).length(); // I'm sure there is a better way than this
        long outputSize = new File(outputFile).length();
        long percentageDifference = ((100 * (inputSize - outputSize)) / inputSize);
        return ((inputSize - outputSize) + "  bytes Saved" + "  " + percentageDifference + "%");
    }
}
