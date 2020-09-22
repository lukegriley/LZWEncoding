import java.io.*;
import java.util.*;

public class LZWEncoding {
	public static void main(String[] args) throws IOException {
		LZWEncoding encoder = new LZWEncoding();
		encoder.encode("lzw-text0.txt");
	}

	public void encode(String input) throws IOException {
		File inputFile = new File(input);
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(input.substring(0, input.length() - 4) + " encoded.txt"));

		// create new file to write your encoded codestream and dictionary on
		String p = "";
		String c = "" + (char) br.read();
		String concat = "" + c;

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

		while (br.ready()) {
			// if it is already in the dictionary
			if (encodingTable.containsKey(concat)) {
				p = concat;
			} else // if it's not in the dictionary
			{
				bw.write(encodingTable.get(p) + ",");

				if (encodingTable.get(p) < maximum) {
					reverseTable.put(encodingTable.size(), concat);
					encodingTable.put(concat, encodingTable.size());
				}

				p = c;
			}
			c = "" + (char) br.read();
			concat = "" + p + c; // update c and concat
		}
		// end of while loop case - if the next P+C isn't in the dictionary, add it
		if (!encodingTable.containsKey(concat)) {
			reverseTable.put(encodingTable.size(), "" + p);
			encodingTable.put("" + p, encodingTable.size());
		}
		int lastIndex = encodingTable.get(concat);// get the last index of the dictionary, write that to the file since
													// the while loop ends one turn too early

		bw.write("" + lastIndex + ",");
		bw.write(";");

		for (int i = 256; i < encodingTable.size(); i++) {// output all the unknown codes to the end of the encoded file
			bw.write(reverseTable.get(i).length() + "=" + reverseTable.get(i));
		}
		bw.close();
		br.close();
	}
}