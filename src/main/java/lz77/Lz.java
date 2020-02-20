package lz77;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lz {
    private final List<Short> lookAheadBuffer = new ArrayList<>();
    private final List<Short> window = new ArrayList<>();
    private  int windowSize;




    public void encode(String inputFilename, String outputFilename,int nrBitsOffset,int nrBitsLength) throws IOException {
        FileInputStream reader = new FileInputStream(inputFilename);
        FileOutputStream writer = new FileOutputStream(outputFilename);
        fillLookAheadBuffer(reader);
        TokenUtil.writeHeader(writer,nrBitsOffset,nrBitsLength);
        windowSize=(int)Math.pow(2,nrBitsOffset)-1;
        while (!lookAheadBuffer.isEmpty()) {
            Match match = getMatchWitMaxLength(findMatches());
            TokenUtil.writeToken(writer, match);
            moveLookAheadBuffer(reader, match.getLength() + 1);
            insertCharsInWindow(match);
            limitWindow(window);
        }
        BitWriter.clearBufferWriter(writer);
        writer.close();
        reader.close();
    }

    public void decode(String inputFilename, String outputFilename) throws IOException {
        FileInputStream reader = new FileInputStream(inputFilename);
        FileOutputStream writer = new FileOutputStream(outputFilename);
        TokenUtil.readHeader(reader);
        windowSize=(int)Math.pow(2,TokenUtil.nrBitsOffset)-1;
        do {
            Match match = TokenUtil.readToken(reader);
            if (match == null) {
                break;
            }
            outputDecodedChars(writer, match);
            insertCharsInWindow(match);
            limitWindow(window);
        }
        while (true);

        writer.close();
        reader.close();
    }

    private List<Match> findMatches() {
        ArrayList<Match> matches = new ArrayList<>();
        short chToMatch = lookAheadBuffer.get(0);
        if (!window.isEmpty()) {
            for (int i = window.size() - 1; i >= 0; i--) {
                if (window.get(i) == chToMatch) {

                    matches.add(generateMatch(i));

                }
            }
        }
        if (matches.isEmpty()) {
            matches.add(new Match(0, 0, lookAheadBuffer.get(0)));
        }
        return matches;
    }

    private Match generateMatch(int matchPosition) {
        int tokenLength = 0;
        int tokenOffset = window.size() - matchPosition;
        short tokenNewChar;
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
        int topvalueForLength=(int)Math.pow(2,TokenUtil.nrBitsLength)-1;
        Match matchWitMaxLength=null;
        for (Match match : matches) {
            if ( (match.getLength() >=maxLength)&&(match.getLength()<topvalueForLength) ) {
                maxLength = match.getLength();
                matchWitMaxLength = match;
            }
        }
        return matchWitMaxLength;

    }

    private void insertCharsInWindow(Match match) {
        int start = window.size() - match.getOffset();
        int stop = start + match.getLength();
        for (int i = start; i < stop; i++) {
            window.add(window.get(i));
        }
        window.add(match.getNewChar());
    }


    private void limitWindow(List<Short> array) {
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

    private void outputDecodedChars(OutputStream writer, Match match) throws IOException {
        System.out.println(match);
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

    private void fillLookAheadBuffer(InputStream inputStream) throws IOException {
        int lookAheadSize = 16000;
        while (lookAheadBuffer.size() < lookAheadSize) {
            int read = inputStream.read();
            if (read != (-1)) {
                lookAheadBuffer.add((short) read);
            } else {
                break;
            }
        }
    }


}
