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

	// ������ʼ��b����
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

	// ����һ������
	public void insert(BPlusTree_dictionary T, String english, String chinese) {
		// �����ظ��������������֮ǰ�Ѿ����ڣ���ô�Ͳ��ٲ���
		if (search(T.root, english).equals(chinese)) {
			System.out.println("The word (" + english + " " + chinese
					+ ") has been inserted before!");

			//return;
		}

		Leaf L;
		Word newWord = new Word(english, chinese);

		// ���root����Ҷ�ڵ㣬������Ѱ��Ŀ��Ҷ�ڵ�
		if (T.root.isLeaf == false) {
			L = nonLeafSearch((NonLeaf) T.root, newWord);
			// leaf is not full
			if (L.words.size() < ORDER)
				L.words = L.insertWord(newWord);
			// leaf is full
			else
				L.split(T, newWord);
		} else
		// ���ֻ��ROOTһ���ڵ㣬��������Ŀ�����Ҷ�ڵ�
		{
			root.words = root.insertWord(newWord);

			if (root.words.size() > ORDER) {
				// �ýڵ���ѳ���������,�������ĺ���ƽ���ָ�����������
				Leaf left = new Leaf();
				Leaf right = new Leaf();
				left.isRoot = false;
				right.isRoot = false;

				// ƽ������key�����������ڵ�
				for (int i = 0; i < ORDER / 2; i++) {
					left.words.add(root.words.get(i));
					right.words.add(root.words.get(i + ORDER / 2 + 1));
				}
				right.words.add(0, root.words.get(ORDER / 2));

				// �����µĸ��ڵ㣬������ָ��
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
		// ���ֻ�и��ڵ㣬��ֱ��ɾ��
		if (T.root.isLeaf) {
			int index = T.root.search(deleteWord);
			System.out.println(index + " :(");
			if (index >= 0) {
				T.root.words.remove(index);
			}
		} else {
			// �ҵ�ɾ����������Ҷ�ڵ㣬ɾ����Ȼ�����
			Leaf deleteLeaf = nonLeafSearch((NonLeaf) T.root, deleteWord);

			deleteLeaf.delete(english, T);
		}
	}

	// ����Ѱ�Ҳ����Ҷ�ڵ�
	private Leaf nonLeafSearch(NonLeaf node, Word newWord) {
		int j = 0;

		// ��ǰNode��key�ĸ���
		int size = node.words.size();
		System.out.println(node.isRoot);
		System.out.println(size);
		System.out.println(node.children.size());
		// �ҵ�ÿ���ڵ��в����λ��
		while (j < size && newWord.compareTo(node.words.get(j)) >= 0) {
			// System.out.print(node.words.get(j).english + " ");
			j++;

		}

		// �����ҵ���һ���ڵ�Ϊ��Ҷ�ڵ�ʱ�����õݹ�������²�ѯ
		System.out.println(j);
		if (node.children.get(j).isLeaf == false)
			return nonLeafSearch((NonLeaf) node.children.get(j), newWord);
		else
			return (Leaf) node.children.get(j);
	}

	// ��������һ��Ӣ�ĵ��ʵ�������˼
	public String search(BPlusTreeNode node, String english) {
		// ���node��Ҷ�ڵ�
		if (node.isLeaf) {
			for (int i = 0; i < node.words.size(); i++) {
				if (english.equals(node.words.get(i).english)) {
					System.out.println(english);
					return node.words.get(i).chinese;
				}
			}
			return "not found";

		} else {
			// ���node����Ҷ�ڵ㣬���������Ѱ��
			int i = 0;
			while (i < node.words.size()
					&& english.compareTo(node.words.get(i).english) >= 0)
				i++;

			return search(((NonLeaf) node).children.get(i), english);
		}

	}

	int count = 0;

	/* ��ӡb���� */
	public void print(BPlusTreeNode node, int level) {
		// ����ӡ500������
		if (count < 5000) {

			// wordString�а�����һ���ڵ��е�����Ӣ�ĵ���
			String wordsString = "  / ";
			for (int i = 0; i < node.words.size(); i++) {
				wordsString += node.words.get(i).english;
				wordsString += " / ";
			}

			// child��ʾ����ڵ��������׵ĵڼ����ӽڵ�
			int child = 0;
			if (!node.isRoot)
				child = node.parent.searchChildren(node);

			System.out.println("level = " + level + "  child = " + child
					+ wordsString);

			// ����ýڵ㲻��Ҷ�ڵ㣬��������´�ӡ
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
