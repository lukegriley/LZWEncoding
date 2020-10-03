/*
* Praise be to Ms. Kaufman and Computer Science A teachers.
* They spoke the truth when they spoke of handwritten code and BlueJ.
*/
public class LZWTester {
	/**
	 * CLI tool for LZW encoder.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Enter the path of the file you want to be encoded");
		String inputFileName = null;

		try (Scanner keyboardScanner = new Scanner(System.in)) {
			/**
			 * Keep prompting the user until they input a valid file path.
			 */
			do {
				System.out.print("> ");

				inputFileName = keyBoardScanner.nextLine();

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

		 /**
     * CLI tool for LZW decoder.
     * 
     * @param args
     * @throws IOException
     */

		System.out.println("Enter the path of the file you want to be decoded");
        String inputFileName = null;

        try (Scanner keyboardScanner = new Scanner(System.in)) {
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

        long startTime = System.nanoTime();

        LZWDecoding decoder = new LZWDecoding();
        decoder.decode(inputFileName);

        long endTime = System.nanoTime();

        final double ns_to_ms = 1000000;

        System.out.println("total runtime (ms): " + (double) (endTime - startTime) / ns_to_ms);
	}
}
/*
                        .="=.
                      _/.-.-.\_     _
                     ( ( o o ) )    ))
                      |/  "  \|    //
      .-------.        \'---'/    //
     _|~~ ~~  |_       /`"""`\\  ((
   =(_|_______|_)=    / /_,_\ \\  \\
     |:::::::::|      \_\\_'__/ \  ))
     |:::::::[]|       /`  /`~\  |//
     |o=======.|      /   /    \  /
     `"""""""""`  ,--`,--'\/\    /
                   '-- "--'  '--'
*/