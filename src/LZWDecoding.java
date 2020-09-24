import java.io.*;
import java.util.*;

public class LZWDecoding {
    public static void main(String[] args) throws IOException {
		LZWDecoding decoder = new LZWDecoding();
		decoder.decode("lzw-text1 encoded.txt");
    }
    
    public void decode(String input) throws IOException {
        String outputFile = input.substring(0, input.length() - 12) + ".txt";// get the original file name
        BufferedWriter decodeWriter = new BufferedWriter(new FileWriter(outputFile));
        BufferedReader reader = new BufferedReader(new FileReader(input));
        ArrayList<String> encodingTable = new ArrayList<String>();// create a new dictionary arraylist that will be filled
                                                                // in from the dictionary on the encoded file
        for (int i = 0; i < 256; i++) {
            encodingTable.add("" + (char) i);
        }
        char temp = (char) reader.read();
        String value = "";
        while (temp != ';') {// the first time you read the file, you have to go through until you find ';',
                             // signifying the start of the dictionary, then you read it into an array list
            temp = (char) reader.read();
        }

        while (reader.ready()) {// setting up dictionary to decode file
            temp = (char) reader.read();
            while (temp != '=') {
                value += temp;
                temp = (char) reader.read();
            }
            int len = Integer.parseInt("" + value);// convert string value to int
            String dictVal = "";
            for (int i = 0; i < len; i++) {// finds full dictionary value
                dictVal += (char) reader.read();
            }
            encodingTable.add(dictVal);
            value = "";
        }

        reader.reset();
        String codeString = "";
        while (temp != ';') {// while you're still reading the codestream part of the file
            temp = (char) reader.read();
            if (temp == ';') {
                break;
            }
            while (temp != ',') {// read each word individually
                codeString += "" + temp;
                temp = (char) reader.read();
            }
            int codeValue = Integer.parseInt(codeString);
            decodeWriter.write(encodingTable.get(codeValue));// access the code's word in the dictionary, write it
            codeString = "";
        }
        reader.close();
        decodeWriter.close();
    }
}
