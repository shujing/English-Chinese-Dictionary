package dictionary;

import java.util.ArrayList;

public class Leaf extends BPlusTreeNode {
	Leaf previous;
	Leaf next;

	public Leaf() {
		isLeaf = true;
	}

	// ** 将叶节点分开
	void split(BPlusTree_dictionary T, Word newWord) {
		ArrayList<Word> newWords = insertWord(newWord);

		// 找到该节点是它父亲的第几个孩子
		int index = parent.searchChildren(this);

		// l分裂成左右两个叶节点
		Leaf lleaf = new Leaf();
		Leaf rleaf = new Leaf();

		// 平均分配key到左右两个节点
		for (int i = 0; i < ORDER / 2; i++) {
			lleaf.words.add(newWords.get(i));
			rleaf.words.add(newWords.get(i + ORDER / 2 + 1));
		}
		rleaf.words.add(0, newWords.get(ORDER / 2));

		// 重新设置叶节点的前后指针
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

		// 重新设置节点间的父子关系
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

	// ** 删除叶节点中的key，并向上调整
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

			// 将该节点的所有父辈节点中，deleteWord有出现过的地方都换成删掉后的节点的最小单词
			BPlusTreeNode midNode = parent;
			while (midNode != null) {
				for (int j = 0; j < midNode.words.size(); j++)
					if (english.equals(midNode.words.get(j).english))
						midNode.words.set(j, words.get(0));

				midNode = midNode.parent;
			}

			if (words.size() < ORDER / 2) {
				// 如果它的next节点中的单词数比容量的一半多，则从next借一个单词
				if (next != null && next.words.size() > ORDER / 2
						&& next.parent == parent) {
					Word borrowWord = next.words.get(0);
					words.add(borrowWord);
					next.words.remove(0);

					// 如果被借走的word在之前的节点中有出现，则用它的下一个word替代
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
					// 如果它的previous节点中key的个数超过容量半数，则从previous借一个
					int lastIndex = previous.words.size() - 1;
					Word borrowWord = previous.words.get(lastIndex);
					words.add(0, borrowWord);
					previous.words.remove(lastIndex);

					// 将previous之前的节点中，borrowWord有出现过的地方都换成新的左节点的最后一个
					BPlusTreeNode temp = previous.parent;
					while (temp != null) {
						for (int j = 0; j < temp.words.size(); j++)
							if (borrowWord == temp.words.get(j))
								temp.words.set(j,
										previous.words.get(lastIndex - 1));

						temp = temp.parent;
					}
				}
				// 合并叶节点
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

	// 合并两个叶子
	private void merge(Leaf leaf1, Leaf leaf2, BPlusTree_dictionary T) {
		// 将leaf2中的key值全部copy到leaf1中
		for (int i = 0; i < leaf2.words.size(); i++)
			leaf1.words.add(leaf2.words.get(i));

		// 调整指针关系
		int indexOfLeaf2 = leaf1.parent.searchChildren(leaf2);
		leaf2.parent.children.remove(indexOfLeaf2);
		leaf1.next = leaf2.next;
		if (leaf2.next != null)
			leaf2.next.previous = leaf1;
		leaf2.previous = null;
		leaf2.next = null;

		//删除leaf1和leaf2间的节点
		leaf1.parent.words.remove(indexOfLeaf2 - 1);
		
		if (leaf1.parent.words.size() < ORDER / 2 && !leaf1.parent.isRoot)
			leaf1.parent.update(T);
		else if (leaf1.parent.isRoot && leaf1.parent.children.size() < 2) {
			leaf1.parent = null;
			T.setRoot(leaf1);
		}
	}
}
