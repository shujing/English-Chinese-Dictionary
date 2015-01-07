package dictionary;

import java.util.ArrayList;

public class BPlusTreeNode {
	// 我们可以把 words 看做是这个node的key值
	ArrayList<Word> words = new ArrayList<Word>();
	final int ORDER = 50;
	// 节点度数
	int t = words.size();

	NonLeaf parent;

	boolean isLeaf;
	boolean isRoot;

	public BPlusTreeNode() {
	}

	// NODE间比较大小
	public int compare(BPlusTreeNode node) {
		return words.get(0).compareTo(node.words.get(0));
	}

	public int search(Word word) {
		int i = 0;
		while (i < words.size() && word.compareTo(words.get(i)) > 0)
			i++;
		if (words.get(i).english.equals(word.english))
			return i;
		else {
			System.out.print("oops, the word not found!!!");
			return -1;
		}
	}

	// words中插入一个新的word的位置
	public ArrayList<Word> insertWord(Word newWord) {
		ArrayList<Word> newWordList = new ArrayList<Word>();

		// find where to insert
		int i = 0;
		while (i < words.size() && newWord.compareTo(words.get(i)) > 0)
			i++;

		// copy to the new array
		for (int j = 0; j < i; j++)
			newWordList.add(words.get(j));

		newWordList.add(newWord);

		if (newWordList.size() < words.size() + 1)
			for (int j = i; j < words.size(); j++)
				newWordList.add(words.get(j));

		return newWordList;
	}

}
