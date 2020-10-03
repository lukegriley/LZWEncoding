/*
* Praise be to Ms. Kaufman and Computer Science A teachers.
* They spoke the truth when they spoke of handwritten code and BlueJ.
*/
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class LZWDecoding {

    /**
     * Applies LZW decoding to given file. Places decoded file in same directory as
     * input file without ".lzw" extension.
     * 
     * @param inputFileName Path of file to be decoded
     * @throws IOException
     */
    public void decode(String inputFileName) throws IOException {
        String outputFileName = inputFileName.replaceAll("\\.lzw$", "");

        try (BufferedReader inputReader = new BufferedReader(new FileReader(inputFileName))) {
            /**
             * decodingTable represents a mapping from a substitute index to original text.
             * 
             * Substitute values in encoded text will be replaced with original text as part
             * of the LZW decoding algorithm.
             * 
             * This table is populated with the extended ASCII character set.
             * 
             * Example mappings:
             * 
             * 0x41 => "A" | 0x42 => "B" | 0x0A => "\n"
             */
            HashMap<Integer, String> decodingTable = new HashMap<Integer, String>();

            for (int i = 0; i < 256; i++) {
                decodingTable.put(i, "" + (char) i);
            }

            /**
             * Read entire file into a StringBuilder (faster than using Strings which are
             * immutable). Whole file needs to be read first, since the end of the file is
             * read before the beginning. StringBuilder is converted into String at the end
             * for access to String methods.
             */
            StringBuilder inputBuffer = new StringBuilder();
            int fileCursor;

            while ((fileCursor = inputReader.read()) != -1) {
                inputBuffer.append((char) fileCursor);
            }

            String inputtext = inputBuffer.toString();

            /**
             * Initialize variables used in the LZW decoding algorithm.
             * 
             * ciphertext: Encoded text of substitute values.
             * 
             * legend: Partially serialized form of encodingTable (see LZWEncoding.java).
             */

            String ciphertext = inputBuffer.substring(0, inputtext.indexOf(';'));
            String legend = inputBuffer.substring(ciphertext.length() + 1);

            /**
             * Deserialize legend into decodingTable.
             * 
             * Example:
             * 
             * legend: 2=ab3=abc4=bd
             * decodingTable: 256 => "ab" | 257 => "abc" | 258 => "bd"
             */
            int i = 0;
            while (i < legend.length()) {
                /**
                 * Index of next '=' (delimeter) starting from index i.
                 */
                int nextEqualsIndex = legend.indexOf('=', i);

                /**
                 * Number of characters to read to form the next entry of the decoding table.
                 */
                String nextLen = legend.substring(i, nextEqualsIndex);

                /**
                 * Text of next entry in the decodingTable.
                 */
                String nextEntry = legend.substring(nextEqualsIndex + 1,
                        nextEqualsIndex + 1 + Integer.parseInt(nextLen));

                decodingTable.put(decodingTable.size(), nextEntry);

                /**
                 * Increment i to move past characters already read.
                 */
                i = nextEqualsIndex + 1 + nextEntry.length();
            }

            /**
             * Decode ciphertext back into original plaintext.
             * 
             * Tokenizes ciphertext based on delimeter (',') and replaces each
             * subsitute value with its original plaintext equivalent.
             */
            i = 0;
            try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFileName))) {
                while (i < ciphertext.length()) {
                    int nextCommaIndex = ciphertext.indexOf(',', i);

                    String nextSubstitute = ciphertext.substring(i, nextCommaIndex);
                    outputWriter.write(decodingTable.get(Integer.parseInt(nextSubstitute)));

                    i = nextCommaIndex + 1;
                }
            }
        }
    }
}
/*
            __,__
   .--.  .-"     "-.  .--.
  / .. \/  .-. .-.  \/ .. \
 | |  '|  /   Y   \  |'  | |
 | \   \  \ 0 | 0 /  /   / |
  \ '- ,\.-"`` ``"-./, -' /
   `'-' /_   ^ ^   _\ '-'`
       |  \._   _./  |
       \   \ `~` /   /
        '._ '-=-' _.'
           '~---~'
*/
