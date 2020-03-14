package lz77;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lz {
    private final List<Byte> lookAheadBuffer = new ArrayList<>();
    private final List<Byte> window = new ArrayList<>();
    private int nrBitsOffset;
    private int nrBitsLength;
    private int windowSize;

    public Lz(int nrBitsOffset, int nrBitsLength) {
        this.nrBitsOffset = nrBitsOffset;
        this.nrBitsLength = nrBitsLength;
        windowSize = (int) Math.pow(2, nrBitsOffset) - 1;
    }

    public Lz() {

    }

    public void encode(String inputFilename, String outputFilename) throws IOException {
        FileInputStream reader = new FileInputStream(inputFilename);
        FileOutputStream writer = new FileOutputStream(outputFilename);
        Match match;

        TokenUtil.writeHeader(writer, nrBitsOffset, nrBitsLength);
        fillLookAheadBuffer(reader);
        while (!lookAheadBuffer.isEmpty()) {
            ArrayList<Match> matches = findMatches(lookAheadBuffer.get(0));
            match = getMatchWitMaxLength(matches);
            TokenUtil.writeToken(writer, match);
            moveLookAheadBuffer(reader, match.getLength() + 1);
            moveWindow(match);
        }
        BitWriter.clearBufferWriter(writer);
        writer.close();
        reader.close();
    }


    public void decode(String inputFilename, String outputFilename) throws IOException {
        FileInputStream reader = new FileInputStream(inputFilename);
        BitReader bitReader = new BitReader(reader);
        FileOutputStream writer = new FileOutputStream(outputFilename);
        Match match;
        TokenUtil.readHeader(bitReader);
        windowSize = (int) Math.pow(2, TokenUtil.nrBitsOffset) - 1;
        do {
            match = TokenUtil.readToken(bitReader);
            if (match == null) break;
            outputDecodedBytes(writer, match);
            moveWindow(match);
        }
        while (true);
        writer.close();
        reader.close();
    }

    public int hideMessage(String inputFilename, String outputFilename, String message) throws IOException {
        FileInputStream reader = new FileInputStream(inputFilename);
        FileOutputStream writer = new FileOutputStream(outputFilename);
        SteganographyUtil steganographyUtil = new SteganographyUtil(message.getBytes());
        Match match;
        window.clear();
        lookAheadBuffer.clear();
        fillLookAheadBuffer(reader);
        TokenUtil.writeHeader(writer, nrBitsOffset, nrBitsLength);
        while (!lookAheadBuffer.isEmpty()) {
            ArrayList<Match> matches = findMatches(lookAheadBuffer.get(0));
            match = encodeMessage(matches, steganographyUtil);
            TokenUtil.writeToken(writer, match);
            moveLookAheadBuffer(reader, match.getLength() + 1);
            moveWindow(match);

        }
        BitWriter.clearBufferWriter(writer);
        writer.close();
        reader.close();
        return steganographyUtil.getMessageSize();
    }

    public String getHiddenMessage(String inputFilename, String outputFilename) throws IOException {
        FileOutputStream writer = new FileOutputStream(outputFilename);
        SteganographyUtil steganographyUtil = new SteganographyUtil();

        BitReader matchReader = new BitReader(new FileInputStream(inputFilename));
        TokenUtil.readHeader(matchReader);

        BitReader matchsReader = new BitReader(new FileInputStream(inputFilename));
        TokenUtil.readHeader(matchsReader);

        window.clear();
        lookAheadBuffer.clear();
        fillLookAheadBuffer(matchsReader);
        Match match;
        windowSize = (int) Math.pow(2, TokenUtil.nrBitsOffset) - 1;
        do {
            match = TokenUtil.readToken(matchReader);
            if (match == null) break;
            writeHiddenBitsToMessage(match, steganographyUtil);
            outputDecodedBytes(writer, match);
            moveLookAheadBuffer(matchsReader, match.getLength() + 1);
            moveWindow(match);
        }
        while (true);

        writer.close();
        matchReader.close();
        matchsReader.close();
        return steganographyUtil.getMessage();

    }

    public Match encodeMessage(List<Match> matches, SteganographyUtil steganographyUtil) {
        if (!steganographyUtil.isMessageEmpty()) {
            int numberOfBits = (int) Math.floor((Math.log(matches.size()) / Math.log(2)));
            int index;
            if (numberOfBits > 0) {
                index = steganographyUtil.readNBits(numberOfBits);
                if (index != -1)
                    return matches.get(index);
            }
        }
        return getMatchWitMaxLength(matches);

    }

    private ArrayList<Match> findMatches(byte byteToMatch) {
        ArrayList<Match> matches = new ArrayList<>();
        if (!window.isEmpty()) {
            for (int i = window.size() - 1; i >= 0; i--) {
                if (window.get(i) == byteToMatch) {

                    matches.add(generateMatch(i));

                }
            }
        }
        if (matches.isEmpty()) {
            matches.add(new Match(0, 0, byteToMatch));
        }
        return matches;
    }

    private Match generateMatch(int matchPosition) {
        int tokenLength = 0;
        int tokenOffset = window.size() - matchPosition;
        byte tokenNewChar;
        int lookAheadPosition = 0;
        do {
            tokenLength++;
            lookAheadPosition++;
            matchPosition++;

            if (lookAheadPosition >= lookAheadBuffer.size() || matchPosition >= window.size()) {
                break;
            }
        } while (lookAheadBuffer.get(lookAheadPosition).equals(window.get(matchPosition)));
            if (lookAheadPosition < lookAheadBuffer.size()) {

                tokenNewChar = lookAheadBuffer.get(lookAheadPosition);
            } else {

                // match on the last char
                tokenLength = tokenLength - 1;
                tokenNewChar = lookAheadBuffer.get(lookAheadPosition - 1);
            }

        return new Match(tokenOffset, tokenLength, tokenNewChar);
    }

    private Match getMatchWitMaxLength(List<Match> matches) {
        int maxLength = 0;
        int topvalueForLength = (int) Math.pow(2, TokenUtil.nrBitsLength) - 1;
        Match matchWitMaxLength = null;
        for (Match match : matches) {
            if ((match.getLength() >= maxLength) && (match.getLength() < topvalueForLength)) {
                maxLength = match.getLength();
                matchWitMaxLength = match;
            }
        }

        if (matchWitMaxLength == null) {
            return new Match(0, 0, matches.get(0).getNewChar());
        }
        return matchWitMaxLength;
    }

    private void moveWindow(Match match) {
        int start = window.size() - match.getOffset();
        int stop = start + match.getLength();
        for (int i = start; i < stop; i++) {
            window.add(window.get(i));
        }
        window.add(match.getNewChar());
        limitWindow();

    }

    private void limitWindow() {
        if (window.size() > windowSize) {
            int numberOfPositions = window.size() - windowSize;

            for (int i = 0; i < window.size() - numberOfPositions; i++) {
                Collections.swap(window, i, i + numberOfPositions);
            }
            for (int i = 0; i < numberOfPositions; i++) {
                window.remove(window.size() - 1);
            }
        }
    }

    public int getNumberOfBitsForSteganography(List<Match> matches) {
        return (int) Math.floor((Math.log(matches.size()) / Math.log(2)));
    }

    public ArrayList<Boolean> getHiddenBits(Match matchFound, List<Match> matches) {
        int numberOfBits;
        int hiddenValue = 0;
        numberOfBits = getNumberOfBitsForSteganography(matches);
        for (Match match : matches) {
            if (matchFound.equals(match)) {
                hiddenValue = matches.indexOf(match);
            }
        }
        return BitUtil.convertIntToBits(hiddenValue, numberOfBits);


    }

    private void writeHiddenBitsToMessage(Match matchFound, SteganographyUtil steganographyUtil) {
        byte byteToMatch;
        int tokenToMatchPosition = window.size() - matchFound.getOffset();
        if (matchFound.getOffset() != 0) {
            byteToMatch = window.get(tokenToMatchPosition);
            ArrayList<Match> matches = findMatches(byteToMatch);
            if (matches.size() > 1) {
                steganographyUtil.writeBitsToMessage(getHiddenBits(matchFound, matches));
            }

        }
    }

    private void outputDecodedBytes(OutputStream writer, Match match) throws IOException {
        int start = window.size() - match.getOffset();
        int stop = start + match.getLength();
        for (int i = start; i < stop; i++) {
            writer.write(window.get(i));
        }
        writer.write(match.getNewChar());

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

    private void moveLookAheadBuffer(BitReader bitReader, int numberOfPositions) throws IOException {
        insertMatchInLookAhead(bitReader);
        for (int i = 0; i < lookAheadBuffer.size() - numberOfPositions; i++) {
            Collections.swap(lookAheadBuffer, i, i + numberOfPositions);
        }
        for (int i = 0; i < numberOfPositions; i++) {
            lookAheadBuffer.remove(lookAheadBuffer.size() - 1);
        }

    }

    private void insertMatchInLookAhead(BitReader bitReader) throws IOException {

        Match match = TokenUtil.readToken(bitReader);
        if (match == null) return;
        if (match.getOffset() == 0) {
            lookAheadBuffer.add(lookAheadBuffer.size(), match.getNewChar());
        } else {
            insertBytesInLookAheadBuffer(match);
        }


    }

    public void fillLookAheadBuffer(BitReader bitReader) throws IOException {
        Match match;

        while (lookAheadBuffer.size() < windowSize) {
            match = TokenUtil.readToken(bitReader);
            if (match == null) break;
            if (match.getOffset() == 0) {
                lookAheadBuffer.add(lookAheadBuffer.size(), match.getNewChar());
            } else {
                insertBytesInLookAheadBuffer(match);
            }

        }
    }

    private void insertBytesInLookAheadBuffer(Match match) {
        int start = lookAheadBuffer.size() - match.getOffset();
        int stop = start + match.getLength();
        for (int i = start; i < stop; i++) {
            lookAheadBuffer.add(lookAheadBuffer.get(i));
        }
        lookAheadBuffer.add(match.getNewChar());
    }

    private void fillLookAheadBuffer(InputStream inputStream) throws IOException {
        while (lookAheadBuffer.size() < windowSize) {
            int read = inputStream.read();
            if (read != (-1)) {
                lookAheadBuffer.add((byte) read);
            } else {
                break;
            }
        }
    }

}