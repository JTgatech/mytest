package cs6300.team59;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AvgSentenceLength {

	private String fileName;
	private String sentenceDelimiters = ".?!:";
	private int    minWordLength = 3;
	
	// Options
	private static final String MIN_WORD_LENGTH_OPT = "-l";
	private static final String DELIMITERS_OPT = "-d";
	
	// Error and message strings
	private static final String NO_FILENAME_ERROR = "No file name specified";
	private static final String URECOGNIZED_OPTION_ERROR = "Unrecognized option: ";
	private static final String NO_ARG_VAL_ERROR = "No value specified for option: ";
	private static final String INVALID_OPTION_VALUE_ERROR = "Invalid value for option: ";
	private static final String INVALID_FILE_ERROR = "Invalid file name specified: ";
	private static final String ERROR_READING_FILE = "Error reading file: ";
	private static final String USAGE_MSG = "\nusage: java AvgSentenceLength path_to_input_file [-d delimiter_list] [-l minimum_word_length]\n\t-d delimiter_list: list of sentence delimiters\n\t-l minimum_word_length: only count words that are > minimum_word_length\n";
	private static final String AVG_SENTENCE_LEN_MSG = "Average words per sentence: ";
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSentenceDelimiters() {
		return sentenceDelimiters;
	}

	public void setSentenceDelimiters(String sentenceDelimiters) {
		this.sentenceDelimiters = sentenceDelimiters;
	}

	public int getMinWordLength() {
		return minWordLength;
	}

	public void setMinWordLength(int minWordLength) {
		this.minWordLength = minWordLength;
	}
	
	public int avgSentenceLength(){
		
		BufferedReader reader = null;
		
		int currentChar = 0;
		
		// Total number of sentences in the document
		int sentenceCount = 0;
		
		// Number of words in the document
		int wordCount = 0;
		
		// Number of letters in the current word
		int currentWordLetterCount = 0;
		
		char letter = 0;

		
		// If we're only counting sentences that have > 0 words which have length > minWordLength
		// then we'll need another variable to track the number of words in the current sentence
		// int currentSentenceWordCount = 0;
		
		try{
			reader = new BufferedReader(new FileReader(getFileName()));
			currentChar = reader.read();
			
			// If the last sentence in the document ends without a delimiter, should it be counted? 
			// If so, then a check needs to be done after reaching EOF to see whether the current word count
			// is >0. If so, update sentenceCount and wordCount
			while(currentChar != -1){

				letter = (char) currentChar;
				
				// Check for end of sentence
				if(getSentenceDelimiters().indexOf(letter) != -1){
					// End the current word
					if(currentWordLetterCount > getMinWordLength()){
						wordCount += 1;
					}
					
					currentWordLetterCount = 0;

					// If we're only counting sentences that have > 0 words which have length > minWordLength
					// then we need to check that the current sentence has > 0 words before incrementing
					// the sentence count
					sentenceCount += 1;
				}
				
				
				// Check for end of word. i.e. Check for whitespace
				else if(Character.isWhitespace(letter)){
					if(currentWordLetterCount > getMinWordLength()){
						wordCount += 1;
					}
					
					currentWordLetterCount = 0;
				}
				
				else{
					// Only count "valid" letters
					// Need to confirm what counts as a "valid" letter in a word
					if(Character.isAlphabetic(letter)){
						currentWordLetterCount +=1;
					}
				}
				
				currentChar = reader.read();
			}
		}
		catch (FileNotFoundException e) {
			printError(INVALID_FILE_ERROR + getFileName());
		}
		catch(IOException eofe){
			printError(ERROR_READING_FILE + getFileName());
		}
		finally{
			try{
				if(reader != null){
					reader.close();
				}
			}
			catch(IOException ioe){
				// Hey we tried...
			}
			
		}

		// Need to check that sentenceCount is >0? Should we count a document with no delimiters
		// as one big sentence?
		if(sentenceCount > 0)
			return wordCount/sentenceCount;
		else
			return 0;
	}
	
	private void printAvgWordsPerSentence(){
		System.out.println(AVG_SENTENCE_LEN_MSG + avgSentenceLength());
	}
	private void printUsage(){
		System.out.println(USAGE_MSG);
	}
	
	private void printError(String error){
		System.out.println("ERROR: " + error);
		printUsage();
		System.exit(1);
	}
	
	// Parse the options and do some validation
	private void init(String[] args){
		
		if(args.length > 0){
			setFileName(args[0]);
		}
		else{
			printError(NO_FILENAME_ERROR);
		}
		
		
		// After the filename argument, if any options are specified, 
		// they should be of the form: [-l|-d] <value>
		for(int i = 1; i < args.length; i+=2){
			
			switch(args[i]){
			
				case DELIMITERS_OPT: 
					// Assuming that any character can be a delimiter. 
					// If only some characters can be delimiters, then a 
					// validation check should be done here
					if(i+1 < args.length){
						setSentenceDelimiters(args[i+1]);
					}
					else{
						printError(NO_ARG_VAL_ERROR);
					}
					break;
					
				case MIN_WORD_LENGTH_OPT: 
					
					if(i+1 < args.length){
						try{
							setMinWordLength(Integer.parseInt(args[i+1]));
						}
						catch(NumberFormatException nfe){
							printError(INVALID_OPTION_VALUE_ERROR + args[i]);
							break;
						}
					}
					else{
						printError(NO_ARG_VAL_ERROR);
					}
					break;
					
				default: printError(URECOGNIZED_OPTION_ERROR + args[i]);
						 break;
			}	
		}
	}
			
	public static void main(String[] args) {
		AvgSentenceLength sl = new AvgSentenceLength();
		sl.init(args);
		sl.printAvgWordsPerSentence();
	}

}

