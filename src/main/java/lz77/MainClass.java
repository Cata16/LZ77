package lz77;

import java.io.IOException;

public class MainClass {
    public static void main(String[] args) throws IOException {
        Lz lz = new Lz(15, 6);
        String inFile = "C:\\Users\\Catalin\\Desktop\\inStream.txt";
        String outFile1 = "C:\\Users\\Catalin\\Desktop\\outStream1.txt";
        String outFile = "C:\\Users\\Catalin\\Desktop\\outStream.txt";
        System.out.println("Number of Bytes " + lz.hideMessage(inFile, outFile, "ceva mesaj "));
        System.out.println(lz.getHiddenMessage(outFile, outFile1));

    }
}
