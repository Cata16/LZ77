package main.java;

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
    static ArrayList<Character> decodeWindow = new ArrayList();
    static FileReader reader = null;
    static BufferedWriter writer = null;
    static int windowSize = 16000;
    static int lookAheadSize = 16000;
    static int minimumLeght=7;

    /**
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

    public static void decode(File inputFile, File outputFile) throws IOException {

        reader = new FileReader(inputFile);
        writer = new BufferedWriter(new FileWriter(outputFile));
        Match match;
        int counter=0;
        fillLookAheadBuffer(reader);
        int i;
        while (!lookAheadBuffer.isEmpty()) {
           i=0;
                  if( ((char) (lookAheadBuffer.get(i) & 0xFF) )=='('  ) {
                           match= searchForMatch();
                   if(match==null){
                       writer.write('(');
                       decodeWindow.add(decodeWindow.size(),'(');
                       i=1;
                   }else{
                       outputDecodedChars(match,decodeWindow);
                       i=getNumberOfPositionsToMove(match);


                       }
                       }
               else{
               writer.write(lookAheadBuffer.get(i));
               decodeWindow.add(decodeWindow.size(), ((char) (lookAheadBuffer.get(i) & 0xFF) ));
               i=1;
           }

            moveLookAheadBuffer(i);
            moveWindow(decodeWindow);
        }
        writer.close();
        reader.close();

    }

    private static int getNumberOfPositionsToMove(Match match) {
        int counter=0;
        if(match.getCh()==')'){
            for(int j=0;j<lookAheadBuffer.size();j++){
                if(lookAheadBuffer.get(j)==')'){
                    counter++;
                }
                if(counter==2){
                    return j+1;
                }
            }
        }else{
            return lookAheadBuffer.indexOf((byte)')')+1;
        }
        return -1;
    }

    private static Match searchForMatch() throws IOException {
        int position=1;
        String startIndex="";
        String length="";
        Match match=null;
        while( (0<=((char) (lookAheadBuffer.get(position) & 0xFF) - '0'))&&(((char) (lookAheadBuffer.get(position) & 0xFF) - '0')<=9)){
            startIndex += ((char) (lookAheadBuffer.get(position) & 0xFF) - '0');
            position++;
        }
        if(((char) (lookAheadBuffer.get(position) & 0xFF) )!=','){
            return match;
        }
        position++;
        while( (0<=((char) (lookAheadBuffer.get(position) & 0xFF) - '0'))&&(((char) (lookAheadBuffer.get(position) & 0xFF) - '0')<=9)){
            length += ((char) (lookAheadBuffer.get(position) & 0xFF) - '0');
            position++;
        }
        if( (((char) (lookAheadBuffer.get(position) & 0xFF) )!=',')|| (((char) (lookAheadBuffer.get(position+2) & 0xFF) )!=')')){
           return match;
        }
        match=new Match(Integer.parseInt(startIndex),Integer.parseInt(length),((char) (lookAheadBuffer.get(position+1) & 0xFF) ));
        return match;
        }

    public static void encode(File inputFile, File outputFile) throws IOException {
        try {
            reader = new FileReader(inputFile);
            writer = new BufferedWriter(new FileWriter(outputFile));
            fillLookAheadBuffer(reader);
            while (!lookAheadBuffer.isEmpty()) {
                Match match = findMatch(0);
                outputEncodedChars(match);
                moveLookAheadBuffer(match.getLength() + 1);
                moveWindow(window);
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
            insertCharsInWindow(match);
            return match;
        }
        insertCharsInWindow(match);


        return match;
    }

    private static void outputEncodedChars(Match match) throws IOException {
        if (match.getLength() > minimumLeght) {
            writer.write("(" + match.getOffset() + "," + match.getLength() + "," + match.getCh() + ")");
        } else {

            for (int i = (window.size() - match.getOffset() - match.getLength() - 1); i < (window.size() - match.getOffset() - 1); i++) {
                writer.write((char) ((window.get(i) & 0xFF)));
            }
            writer.write(match.getCh());
        }


    }
    private static void outputDecodedChars(Match match,ArrayList<Character> array) throws IOException {
        int start=array.size() - match.getOffset() ;
        int stop=start+ match.getLength() ;
        for (int i = start; i < stop; i++) {
            writer.write(array.get(i) );
            array.add(array.size(),array.get(i) );

        }
        writer.write(match.getCh());
        array.add(array.size(),match.getCh());

    }

    public static void insertCharsInWindow(Match match) {
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
        System.out.print((inputSize - outputSize) + "  bytes Saved" + "  " + percentageDifference+"%");

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