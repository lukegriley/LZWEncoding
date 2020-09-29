import java.io.*;
import java.util.*;
import java.nio.file.*;

public class LZWEncoding {
	/**
	 * CLI tool for LZW encoder.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Enter the path of the file you want to be encoded");
		String inputFileName = null;

		try (Scanner sc = new Scanner(System.in)) {
			/**
			 * Keep prompting the user until they input a valid file path.
			 */
			do {
				System.out.print("> ");

				inputFileName = sc.nextLine();

				// Check if user input is an invalid file
				if (inputFileName.equals("") || Files.notExists(Paths.get(inputFileName))) {
					System.out.println("Cannot find file.");
					inputFileName = null;
				}

			} while (inputFileName == null);
		}

		long startTime = System.nanoTime(); // record start time

		LZWEncoding encoder = new LZWEncoding();
		encoder.encode(inputFileName);

		long endTime = System.nanoTime(); // record end time

		final double ns_to_ms = 1000000; // number of nanoseconds in a millisecond

		System.out.println("total runtime (ms): " + (double) (endTime - startTime) / ns_to_ms);
	}

	/**
	 * Applies LZW encoding to given file. Places encoded file in same 
	 * directory as input file with extension ".lzw".
	 * 
	 * @param inputFileName Path of file to be encoded
	 * @throws IOException
	 */
	public void encode(String inputFileName) throws IOException {
		String outputFileName = inputFileName + ".lzw";

		try (BufferedReader inputReader = new BufferedReader(new FileReader(inputFileName))) {

			/**
			 * Initialize variables used in the LZW algorithm implementation.
			 * 
			 * previousString: represents the previously-read characters in the stream.
			 * currentChar: represents the current character being read in the stream.
			 * concatString: an intermediate value for previousString + currentChar
			 */
			String previousString = "";
			String currentChar = "" + (char) inputReader.read();
			String concatString = "" + currentChar;

			/**
			 * encodingTable represents a mapping from a string to a substitute value.
			 * 
			 * These substitute values will be placed into the output value as part of the
			 * LZW encoding algorithm.
			 * 
			 * This table is first populated with the entirety of the extended ASCII codeset
			 * (256 characters).
			 * 
			 * Example mappings:
			 * 
			 * "A" => 0x41 | "B" => 0x42 | "\n" => 0x0A
			 */
			HashMap<String, Integer> encodingTable = new HashMap<String, Integer>();

			/**
			 * reverseTable represents a mapping from a substitute value to a string.
			 * 
			 * This table is used to place the values of the encodingTable in sequential
			 * order after the ciphertext (encoded text) and makes the LZW decoding
			 * algorithm much simpler.
			 */
			HashMap<Integer, String> reverseTable = new HashMap<Integer, String>();

			/**
			 * Maximum number of entries in the encoding table.
			 * 
			 * Currently, the maximum entries value is fixed to a 13-bit limit.
			 * 
			 * 13-bit integers can only represent up to 8192 (the largest index is 8191).
			 */
			final int MAXIMUM_ENRIES = 1 << 13;

			/**
			 * Fill the encoding table with the extended ASCII character set.
			 */
			for (int i = 0; i < 256; i++) {
				String codepoint = "" + (char) i;
				encodingTable.put(codepoint, i);
			}

			try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFileName))) {
				/**
				 * Algorithm description
				 * 				 
				 * - if concatString is in encodingTable
				 * 	- previousString = concatString
				 * - else
				 * 	- output substitute for previousString into codestream
				 * 	- put concatString into encodingTable
				 * 	- previousString = currentChar
				 * - currentChar = next character in input stream				 
				 * - concatString = previousString + currentChar
				 */
				while (inputReader.ready()) {
					// if concatString is already in the dictionary
					if (encodingTable.containsKey(concatString)) {
						previousString = concatString;
					} else { // if concatString not in the dictionary					
						outputWriter.write(encodingTable.get(previousString) + ",");

						if (encodingTable.size() < MAXIMUM_ENRIES) {
							reverseTable.put(encodingTable.size(), concatString);
							encodingTable.put(concatString, encodingTable.size());
						}

						previousString = currentChar;
					}
					currentChar = "" + (char) inputReader.read();
					concatString = "" + previousString + currentChar; // update currentChar and concatString
				}
				
				/**
				 * Handle edge case where the last few characters of the input stream have no substitute.
				 */
				if (!encodingTable.containsKey(concatString)) {
					reverseTable.put(encodingTable.size(), "" + previousString);
					encodingTable.put("" + previousString, encodingTable.size());
				}

				/**
				 * Output last substitute value into file.
				 */
				int lastSubstitute = encodingTable.get(concatString);				

				outputWriter.write("" + lastSubstitute + ",");
				outputWriter.write(";");

				/**
				 * Output encodingTable information into the output file.
				 * 
				 * Format (where substring is the original string put into the encodingTable):
				 * `${substring.length()}=${substring}`
				 */
				for (int i = 256; i < encodingTable.size(); i++) {
					outputWriter.write(reverseTable.get(i).length() + "=" + reverseTable.get(i));
				}
			}
		}
	}
}