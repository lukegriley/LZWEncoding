import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LZWDecoding {
    public static void main(String[] args) throws IOException {
        System.out.println("Enter the path of the file you want to be decoded");
        String inputFilename = null;

        try (Scanner sc = new Scanner(System.in)) {
            // Keep trying to prompt user input.
            do {
                System.out.print("> ");
                inputFilename = sc.nextLine();

                // Check if user input is an invalid file
                if (Files.notExists(Paths.get(inputFilename))) {
                    System.out.println("Cannot find file.");
                    inputFilename = null;
                }

            } while (inputFilename == null);
        }

        long startTime = System.nanoTime();
        LZWDecoding decoder = new LZWDecoding();
        decoder.decode(inputFilename);
        long endTime = System.nanoTime();

        final double ns_to_ms = 1000000;

        System.out.println("total runtime (ms): " + (double) (endTime - startTime) / ns_to_ms);
    }

    public void decode(String inputFileName) throws IOException {
        String outputFileName = inputFileName.replaceAll("\\.lzw$", "");// get the original file name

        try (FileReader inputReader = new FileReader(inputFileName)) {
            HashMap<Integer, String> decodingTable = new HashMap<Integer, String>();

            for (int i = 0; i < 256; i++) {
                decodingTable.put(i, "" + (char) i);
            }

            StringBuilder inputBuffer = new StringBuilder();

            int fileCursor;

            while ((fileCursor = inputReader.read()) != -1) {
                inputBuffer.append((char) fileCursor);
            }

            String[] splittext = inputBuffer.toString().split(";", 2);

            String legend = splittext[1];
            String ciphertext = splittext[0];

            while (legend.length() > 0) {
                String[] tokens = legend.split("=", 2);

                int nextLen = Integer.parseInt(tokens[0]);
                String nextEntry = tokens[1].substring(0, nextLen);

                decodingTable.put(decodingTable.size(), nextEntry);

                legend = tokens[1].substring(nextLen);
            }

            try (FileWriter outputWriter = new FileWriter(outputFileName)) {
                for (String token : ciphertext.split(",")) {
                    int substitute = Integer.parseInt(token);
                    outputWriter.write(decodingTable.get(substitute));
                }
            }
        }
    }
}
