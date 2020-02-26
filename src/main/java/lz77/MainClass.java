package lz77;

import java.io.IOException;

public class MainClass {
    public static void main(String[] args) throws IOException {
        Lz lz = new Lz();
        String inputFile = "C:\\Users\\Catalin\\Desktop\\inStream.txt";
        String outputFile1 = "C:\\Users\\Catalin\\Desktop\\outStream.txt";
        String outputFile2 = "C:\\Users\\Catalin\\Desktop\\outStream1.txt";

        lz.encode(inputFile, outputFile1, 15, 6);
        lz.decode(outputFile1, outputFile2);

    }
}