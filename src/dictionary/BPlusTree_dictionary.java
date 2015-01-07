package dictionary;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class BPlusTree_dictionary {
	final int ORDER = 50;
	BPlusTreeNode root;
	Leaf head;

	public BPlusTree_dictionary() throws IOException {
		root = new Leaf();
		root.isLeaf = true;
		root.isRoot = true;

		ArrayList<Word> insertWordList = Read
				.getInserteWordList("1_initial.txt");
		for (int i = 0; i < insertWordList.size(); i++) {
			insert(this, insertWordList.get(i).english,
					insertWordList.get(i).chinese);
		}

	}

	public void setRoot(BPlusTreeNode node) {
		root = node;
		root.isRoot = true;
	}

	public void setHead(Leaf lleaf) {
		head = lleaf;
	}

	// ＊＊初始化b＋树
	/*public void BPTreeCreate(BPlusTree_dictionary T) throws IOException {

		T.root = new Leaf();
		T.root.isLeaf = true;
		T.root.isRoot = true;

		ArrayList<Word> insertWordList = Read
				.getInserteWordList("1_initial.txt");
		for (int i = 0; i < insertWordList.size(); i++) {
			T.insert(T, insertWordList.get(i).english,
					insertWordList.get(i).chinese);
		}
	}*/

	// 插入一个单词
	public void insert(BPlusTree_dictionary T, String english, String chinese) {
		// 避免重复插入的情况，如果之前已经存在，那么就不再插入
		if (search(T.root, english).equals(chinese)) {
			System.out.println("The word (" + english + " " + chinese
					+ ") has been inserted before!");

			//return;
		}

		Leaf L;
		Word newWord = new Word(english, chinese);

		// 如果root不是叶节点，则向下寻找目标叶节点
		if (T.root.isLeaf == false) {
			L = nonLeafSearch((NonLeaf) T.root, newWord);
			// leaf is not full
			if (L.words.size() < ORDER)
				L.words = L.insertWord(newWord);
			// leaf is full
			else
				L.split(T, newWord);
		} else
		// 如果只有ROOT一个节点，则它既是目标插入叶节点
		{
			root.words = root.insertWord(newWord);

			if (root.words.size() > ORDER) {
				// 该节点分裂成左右两个,它本来的孩子平均分给这两个孩子
				Leaf left = new Leaf();
				Leaf right = new Leaf();
				left.isRoot = false;
				right.isRoot = false;

				// 平均分配key到左右两个节点
				for (int i = 0; i < ORDER / 2; i++) {
					left.words.add(root.words.get(i));
					right.words.add(root.words.get(i + ORDER / 2 + 1));
				}
				right.words.add(0, root.words.get(ORDER / 2));

				// 产生新的根节点，并设置指针
				NonLeaf newRoot = new NonLeaf();
				newRoot.isRoot = true;
				newRoot.isLeaf = false;
				newRoot.words.add(root.words.get(ORDER / 2));
				newRoot.children.add(left);
				newRoot.children.add(right);
				left.parent = newRoot;
				right.parent = newRoot;

				left.next = right;
				right.previous = left;

				T.setHead(left);
				T.setRoot(newRoot);
			}
		}
	}

	public void delete(BPlusTree_dictionary T, String english, String chinese) {
		Word deleteWord = new Word(english, chinese);
		// 如果只有根节点，则直接删除
		if (T.root.isLeaf) {
			int index = T.root.search(deleteWord);
			System.out.println(index + " :(");
			if (index >= 0) {
				T.root.words.remove(index);
			}
		} else {
			// 找到删除单词所在叶节点，删除，然后调整
			Leaf deleteLeaf = nonLeafSearch((NonLeaf) T.root, deleteWord);

			deleteLeaf.delete(english, T);
		}
	}

	// 向下寻找插入的叶节点
	private Leaf nonLeafSearch(NonLeaf node, Word newWord) {
		int j = 0;

		// 当前Node中key的个数
		int size = node.words.size();
		System.out.println(node.isRoot);
		System.out.println(size);
		System.out.println(node.children.size());
		// 找到每个节点中插入的位置
		while (j < size && newWord.compareTo(node.words.get(j)) >= 0) {
			// System.out.print(node.words.get(j).english + " ");
			j++;

		}

		// 当查找的下一个节点为非叶节点时，则用递归继续向下查询
		System.out.println(j);
		if (node.children.get(j).isLeaf == false)
			return nonLeafSearch((NonLeaf) node.children.get(j), newWord);
		else
			return (Leaf) node.children.get(j);
	}

	// ＊＊查找一个英文单词的中文意思
	public String search(BPlusTreeNode node, String english) {
		// 如果node是叶节点
		if (node.isLeaf) {
			for (int i = 0; i < node.words.size(); i++) {
				if (english.equals(node.words.get(i).english)) {
					System.out.println(english);
					return node.words.get(i).chinese;
				}
			}
			return "not found";

		} else {
			// 如果node不是叶节点，则继续向下寻找
			int i = 0;
			while (i < node.words.size()
					&& english.compareTo(node.words.get(i).english) >= 0)
				i++;

			return search(((NonLeaf) node).children.get(i), english);
		}

	}

	int count = 0;

	/* 打印b＋树 */
	public void print(BPlusTreeNode node, int level) {
		// 最多打印500个单词
		if (count < 5000) {

			// wordString中包含了一个节点中的所有英文单词
			String wordsString = "  / ";
			for (int i = 0; i < node.words.size(); i++) {
				wordsString += node.words.get(i).english;
				wordsString += " / ";
			}

			// child表示这个节点是它父亲的第几个子节点
			int child = 0;
			if (!node.isRoot)
				child = node.parent.searchChildren(node);

			System.out.println("level = " + level + "  child = " + child
					+ wordsString);

			// 如果该节点不是叶节点，则继续向下打印
			if (!node.isLeaf) {
				level++;
				for (int i = 0; i < ((NonLeaf) node).children.size(); i++)
					print(((NonLeaf) node).children.get(i), level);
			}
			count++;
		}
	}

	public static void main(String[] args) throws IOException {
		BPlusTree_dictionary tree = new BPlusTree_dictionary();
	//	tree.BPTreeCreate(tree);

		tree.print(tree.root, 0);
		ArrayList<Word> insertWordList = Read
				.getInserteWordList("3_insert.txt");
		for (int i = 0; i < insertWordList.size(); i++) {
			tree.insert(tree, insertWordList.get(i).english,
					insertWordList.get(i).chinese);
		}

		tree.print(tree.root, 0);

		ArrayList<String> deleteWordList = Read
				.getDeleteWordList("2_delete.txt");
		for (int i = 0; i < deleteWordList.size(); i++) {
			tree.delete(tree, deleteWordList.get(i), "");

		}

		for (int i = 0; i < deleteWordList.size(); i++) {
			System.out.println(tree.search(tree.root, deleteWordList.get(i)));
		}

	}

}
