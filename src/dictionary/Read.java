package dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Read {
	
	/**
	 * Read lines from the text;
	 * 
	 * @param inRB
	 * @return
	 * @throws IOException
	 */

	public static ArrayList<Word> getInserteWordList(String address)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(address));
		String english = null;
		String chinese = null;

		ArrayList<Word> words = new ArrayList<Word>();
		// BPlusTreeNode[] nodes = new BPlusTreeNode[5000];
		String title = reader.readLine();
		System.out.println(title);
		while ((english = reader.readLine()) != null) {
			chinese = reader.readLine();
			words.add(new Word(english, chinese));

		}
		return words;
	}

	public static ArrayList<String> getDeleteWordList(String address)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(address));
		String english;
		ArrayList<String> englishList = new ArrayList<String>();
		String title = reader.readLine();
		System.out.println(title);

		while ((english = reader.readLine()) != null) {

			englishList.add(english);
		}
		return englishList;
	}

	public static void main(String[] args) throws IOException {
		ArrayList<String> a = new ArrayList<String>();
		a.add("1");
		a.add("3");
		a.set(0, "2");
		for(int i = 0 ; i < a.size();i++)
			System.out.print(a.get(i));
	}

}
