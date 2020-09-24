import java.io.*;
import java.util.*;

public class LZWEncoding {
	public static void main(String[] args) throws IOException {
		LZWEncoding encoder = new LZWEncoding();
		encoder.encode("lzw-text0.txt");
	}

	public void encode(String input) throws IOException {
		File inputFile = new File(input);
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(input.substring(0, input.length() - 4) + " encoded.txt"));

		// create new file to write your encoded codestream and dictionary on
		String previousChar = "";
		String currentChar = "" + (char) reader.read();
		String concatString = "" + currentChar;

		// ArrayList<String> dictionary = new ArrayList<String>();
		HashMap<String, Integer> encodingTable = new HashMap<String, Integer>();
		HashMap<Integer, String> reverseTable = new HashMap<Integer, String>();

		// maximum number of characters
		final int maximum = 1 << 13;

		for (int i = 0; i < 256; i++) // fill in the dictionary with the first known 255 characters and codes
		{
			String codepoint = "" + (char) i;
			encodingTable.put(codepoint, i);
		}

		while (reader.ready()) {
			// if it is already in the dictionary
			if (encodingTable.containsKey(concatString)) {
				previousChar = concatString;
			} else // if it's not in the dictionary
			{
				writer.write(encodingTable.get(previousChar) + ",");

				if (encodingTable.get(previousChar) < maximum) {
					reverseTable.put(encodingTable.size(), concatString);
					encodingTable.put(concatString, encodingTable.size());
				}

				previousChar = currentChar;
			}
			currentChar = "" + (char) reader.read();
			concatString = "" + previousChar + currentChar; // update c and concat
		}
		// end of while loop case - if the next P+C isn't in the dictionary, add it
		if (!encodingTable.containsKey(concatString)) {
			reverseTable.put(encodingTable.size(), "" + previousChar);
			encodingTable.put("" + previousChar, encodingTable.size());
		}
		int lastIndex = encodingTable.get(concatString);// get the last index of the dictionary, write that to the file since
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