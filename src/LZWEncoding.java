import java.io.*;
import java.util.*;
import java.nio.file.*;

public class LZWEncoding {
	public static void main(String[] args) throws IOException {
		System.out.println("Enter a file path of the file you want to be encoded: ");
		String inputFilename = null;

		try (Scanner sc = new Scanner(System.in))
		{
			System.out.print("> ");			
		
			// Keep trying to prompt user input.
			do {
				inputFilename = sc.nextLine();

				// Check if user input is an invalid file
				if (Files.notExists(Paths.get(inputFilename))) {
					System.out.println("Cannot find file.");
					inputFilename = null;
				}

			} while (inputFilename == null);
		}

		LZWEncoding encoder = new LZWEncoding();

		try {
			encoder.encode(inputFilename);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		System.out.println("Encoded file at " + Paths.get(inputFilename + ".lzw"));
	}

	public void encode(String input) throws IOException {
		File inputFile = new File(input);
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(
				new FileWriter(new File(input + ".lzw")));

		// create new file to write your encoded codestream and dictionary on
		String previousChar = "";
		String currentChar = "" + (char) reader.read();
		String concatString = "" + currentChar;

		HashMap<String, Integer> encodingTable = new HashMap<String, Integer>();
		HashMap<Integer, String> reverseTable = new HashMap<Integer, String>();

		// maximum number of entries in encoding table
		final int MAXIMUM_ENRIES = 1 << 13;

		for (int i = 0; i < 256; i++) // fill in the dictionary with the first known 256 characters and codes
		{
			String codepoint = "" + (char) i;
			encodingTable.put(codepoint, i);
		}

		while (reader.ready()) {
			// if concatString is already in the dictionary
			if (encodingTable.containsKey(concatString)) {
				previousChar = concatString;
			} else // if concatString not in the dictionary
			{
				writer.write(encodingTable.get(previousChar) + ",");

				if (encodingTable.get(previousChar) < MAXIMUM_ENRIES - 1) {
					reverseTable.put(encodingTable.size(), concatString);
					encodingTable.put(concatString, encodingTable.size());
				}

				previousChar = currentChar;
			}
			currentChar = "" + (char) reader.read();
			concatString = "" + previousChar + currentChar; // update currentChar and concatString
		}
		// end of while loop case - if the next previous+current isn't in the dictionary, add it
		if (!encodingTable.containsKey(concatString)) {
			reverseTable.put(encodingTable.size(), "" + previousChar);
			encodingTable.put("" + previousChar, encodingTable.size());
		}
		int lastIndex = encodingTable.get(concatString);// get the last index of the dictionary, write that to the file
														// since
		// the while loop ends one turn too early

		writer.write("" + lastIndex + ",");
		writer.write(";");

		for (int i = 256; i < encodingTable.size(); i++) {// output all the unknown codes to the end of the encoded file
			writer.write(reverseTable.get(i).length() + "=" + reverseTable.get(i));
		}
		writer.close();
		reader.close();
	}
}