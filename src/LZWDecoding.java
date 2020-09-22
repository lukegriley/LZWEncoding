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
        BufferedReader br2 = new BufferedReader(new FileReader(input));
        ArrayList<String> dictionary2 = new ArrayList<String>();// create a new dictionary arraylist that will be filled
                                                                // in from the dictionary on the encoded file
        for (int i = 0; i < 256; i++) {
            dictionary2.add("" + (char) i);
        }
        char temp = (char) br2.read();
        String value = "";
        while (temp != ';') {// the first time you read the file, you have to go through until you find ';',
                             // signifying the start of the dictionary, then you read it into an array list
            temp = (char) br2.read();
        }

        while (br2.ready()) {// setting up dictionary to decode file
            temp = (char) br2.read();
            while (temp != '=') {
                value += temp;
                temp = (char) br2.read();
            }
            int len = Integer.parseInt("" + value);// convert string value to int
            String dictVal = "";
            for (int i = 0; i < len; i++) {// finds full dictionary value
                dictVal += (char) br2.read();
            }
            dictionary2.add(dictVal);
            value = "";
        }

        br2.close();
        BufferedReader br3 = new BufferedReader(new FileReader(input));
        String code = "";
        while (temp != ';') {// while you're still reading the codestream part of the file
            temp = (char) br3.read();
            if (temp == ';') {
                break;
            }
            while (temp != ',') {// read each word individually
                code += "" + temp;
                temp = (char) br3.read();
            }
            int codeValue = Integer.parseInt(code);
            decodeWriter.write(dictionary2.get(codeValue));// access the code's word in the dictionary, write it
            code = "";

        }
        br3.close();
        decodeWriter.close();
    }
}
