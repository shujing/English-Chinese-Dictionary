package dictionary;

import java.util.ArrayList;

public class Leaf extends BPlusTreeNode {
	Leaf previous;
	Leaf next;

	public Leaf() {
		isLeaf = true;
	}

	// ** ��Ҷ�ڵ�ֿ�
	void split(BPlusTree_dictionary T, Word newWord) {
		ArrayList<Word> newWords = insertWord(newWord);

		// �ҵ��ýڵ��������׵ĵڼ�������
		int index = parent.searchChildren(this);

		// l���ѳ���������Ҷ�ڵ�
		Leaf lleaf = new Leaf();
		Leaf rleaf = new Leaf();

		// ƽ������key�����������ڵ�
		for (int i = 0; i < ORDER / 2; i++) {
			lleaf.words.add(newWords.get(i));
			rleaf.words.add(newWords.get(i + ORDER / 2 + 1));
		}
		rleaf.words.add(0, newWords.get(ORDER / 2));

		// ��������Ҷ�ڵ��ǰ��ָ��
		if (previous != null) {
			previous.next = lleaf;
			lleaf.previous = previous;
		} else
			T.setHead(lleaf);
		if (next != null) {
			rleaf.next = next;
			next.previous = rleaf;
		}
		lleaf.next = rleaf;
		rleaf.previous = lleaf;
		previous = null;
		next = null;

		// �������ýڵ��ĸ��ӹ�ϵ
		lleaf.parent = parent;
		rleaf.parent = parent;
		parent.children.remove(index);
		parent.children.add(index, lleaf);
		parent.children.add(index + 1, rleaf);

		if (parent.words.size() < ORDER)
			parent.words.add(index, rleaf.words.get(0));
		else
			parent.insertOrSplit(T, rleaf.words.get(0));
	}

	// ** ɾ��Ҷ�ڵ��е�key�������ϵ���
	void delete(String english, BPlusTree_dictionary T) {
		int i = 0;
		// Word deleteWord;

		while (i < words.size() && !english.equals(words.get(i).english))
			i++;
		System.out.println(i + english);
		if (i >= words.size())
			System.out
					.println("there is no such word, you don't have to delete");
		else {
			// deleteWord = words.get(i);
			words.remove(i);

			// ���ýڵ�����и����ڵ��У�deleteWord�г��ֹ��ĵط�������ɾ����Ľڵ����С����
			BPlusTreeNode midNode = parent;
			while (midNode != null) {
				for (int j = 0; j < midNode.words.size(); j++)
					if (english.equals(midNode.words.get(j).english))
						midNode.words.set(j, words.get(0));

				midNode = midNode.parent;
			}

			if (words.size() < ORDER / 2) {
				// �������next�ڵ��еĵ�������������һ��࣬���next��һ������
				if (next != null && next.words.size() > ORDER / 2
						&& next.parent == parent) {
					Word borrowWord = next.words.get(0);
					words.add(borrowWord);
					next.words.remove(0);

					// ��������ߵ�word��֮ǰ�Ľڵ����г��֣�����������һ��word���
					BPlusTreeNode temp = next.parent;
					while (temp != null) {
						for (int j = 0; j < temp.words.size(); j++)
							if (borrowWord == temp.words.get(j))
								temp.words.set(j, next.words.get(0));

						temp = temp.parent;
					}
				} else if (previous != null
						&& previous.words.size() > ORDER / 2
						&& previous.parent == parent) {
					// �������previous�ڵ���key�ĸ��������������������previous��һ��
					int lastIndex = previous.words.size() - 1;
					Word borrowWord = previous.words.get(lastIndex);
					words.add(0, borrowWord);
					previous.words.remove(lastIndex);

					// ��previous֮ǰ�Ľڵ��У�borrowWord�г��ֹ��ĵط��������µ���ڵ�����һ��
					BPlusTreeNode temp = previous.parent;
					while (temp != null) {
						for (int j = 0; j < temp.words.size(); j++)
							if (borrowWord == temp.words.get(j))
								temp.words.set(j,
										previous.words.get(lastIndex - 1));

						temp = temp.parent;
					}
				}
				// �ϲ�Ҷ�ڵ�
				else {
					if (previous != null
							&& (previous.words.size() + words.size()) <= ORDER
							&& previous.parent == parent)
						merge(previous, this, T);
					else if (next != null
							&& (next.words.size() + words.size()) <= ORDER
							&& next.parent == parent)
						merge(this, next, T);
				}
			}
		}
	}

	// �ϲ�����Ҷ��
	private void merge(Leaf leaf1, Leaf leaf2, BPlusTree_dictionary T) {
		// ��leaf2�е�keyֵȫ��copy��leaf1��
		for (int i = 0; i < leaf2.words.size(); i++)
			leaf1.words.add(leaf2.words.get(i));

		// ����ָ���ϵ
		int indexOfLeaf2 = leaf1.parent.searchChildren(leaf2);
		leaf2.parent.children.remove(indexOfLeaf2);
		leaf1.next = leaf2.next;
		if (leaf2.next != null)
			leaf2.next.previous = leaf1;
		leaf2.previous = null;
		leaf2.next = null;

		//ɾ��leaf1��leaf2��Ľڵ�
		leaf1.parent.words.remove(indexOfLeaf2 - 1);
		
		if (leaf1.parent.words.size() < ORDER / 2 && !leaf1.parent.isRoot)
			leaf1.parent.update(T);
		else if (leaf1.parent.isRoot && leaf1.parent.children.size() < 2) {
			leaf1.parent = null;
			T.setRoot(leaf1);
		}
	}
}
