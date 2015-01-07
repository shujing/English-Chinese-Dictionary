package dictionary;

import java.util.ArrayList;

public class NonLeaf extends BPlusTreeNode {

	ArrayList<BPlusTreeNode> children = new ArrayList<BPlusTreeNode>();

	public NonLeaf() {
		isLeaf = false;
		if (isRoot == true)
			parent = null;
	}

	public int searchChildren(BPlusTreeNode node) {
		int index = 0;
		while (index < children.size() && node.compare(children.get(index)) > 0)
			index++;
		return index;
	}

	// **中间节点的插入，或者分裂
	public void insertOrSplit(BPlusTree_dictionary T, Word word) {
		words = insertWord(word);

		// 该节点分裂成左右两个,它本来的孩子平均分给这两个孩子
		NonLeaf left = new NonLeaf();
		NonLeaf right = new NonLeaf();
		left.isRoot = false;
		right.isRoot = false;

		// 左右两个节点关键字长度
		int leftSize = ORDER / 2;
		int rightSize = ORDER / 2;

		// 复制子节点到分裂出来的新节点，并更新关键字
		for (int i = 0; i < leftSize; i++) {
			left.children.add(children.get(i));
			left.words.add(words.get(i));
			children.get(i).parent = left;
		}

		left.children.add(children.get(leftSize));
		children.get(leftSize).parent = left;
		right.children.add(children.get(leftSize + 1));
		children.get(leftSize + 1).parent = right;

		for (int i = 0; i < rightSize; i++) {
			right.children.add(children.get(i + leftSize + 2));
			right.words.add(words.get(i + leftSize + 1));
			children.get(i + leftSize + 2).parent = right;
		}

		// 如果该节点时根节点，则直接把中间的单词插入新的根节点
		if (isRoot == true) {
			NonLeaf root = new NonLeaf();
			root.isRoot = true;
			root.words.add(words.get(ORDER / 2));
			root.children.add(left);
			root.children.add(right);
			root.parent = null;

			T.root = root;
			left.parent = root;
			right.parent = root;
		} else {
			// 如果不是根节点，则把新的右
			int index = parent.searchChildren(this);
			parent.children.remove(index);
			parent.children.add(index, left);
			parent.children.add(index + 1, right);
			left.parent = parent;
			right.parent = parent;
			if (parent.words.size() < ORDER)
				parent.words.add(index, words.get(ORDER / 2));
			else
				parent.insertOrSplit(T, right.words.get(0));
		}

	}

	// ＊＊ 当内部节点的fill factor小于50%时，对内部节点进行调整
	public void update(BPlusTree_dictionary T) {
		int index = 0;
		if (parent != null)
			index = parent.searchChildren(this);

		// 如果是根节点
		if (isRoot) {

			// 如果子节点数大于等于2，不用调整,否则合并根和子节点
			if (words.size() == 0) {
				T.root = children.get(0);
				T.root.isRoot = true;
				T.root.parent = null;
				// children = null;
				words = null;
			}
		} else {
			// 如果有左sibling
			if (index > 0) {
				int lSiblingKeys = parent.children.get(index - 1).words.size();
				// 并且左兄弟关键字的数目超过了一半，就把它的最右边的儿子借过来
				if (lSiblingKeys > ORDER / 2) {
					// 把原来间隔它和sibling的word移到它的words中的第一个
					words.add(0, parent.words.get(index - 1));

					NonLeaf leftSibling = (NonLeaf) parent.children
							.get(index - 1);
					BPlusTreeNode borrowChild = leftSibling.children
							.get(lSiblingKeys);
					children.add(0, borrowChild);
					borrowChild.parent = this;

					// 把leftSibling的最右边的关键字移到父节点上去
					parent.words.add(index - 1,
							leftSibling.words.get(lSiblingKeys - 1));
					parent.children.get(index - 1).words
							.remove(lSiblingKeys - 1);

					// leftSibling删除掉这个儿子
					((NonLeaf) parent.children.get(index - 1)).children
							.remove(lSiblingKeys);
					if (parent.words.size() < ORDER / 2)
						parent.update(T);

				}

			}

			// 如果它有右兄弟,并且左兄弟不够，或者没有左sibling
			if (((index > 0 && parent.children.get(index - 1).words.size() <= ORDER / 2) || index == 0)
					&& index < parent.children.size() - 1) {
				int rSiblingKeys = parent.children.get(index + 1).words.size();
				// 并且右兄弟关键字的数目超过了一半，就把它的最左边的儿子借过来
				if (rSiblingKeys > ORDER / 2) {
					// 把原来间隔它和sibling的word移到它的words中的最后一个
					words.add(parent.words.get(index + 1));

					NonLeaf rightSibling = (NonLeaf) parent.children
							.get(index + 1);
					BPlusTreeNode borrowChild = rightSibling.children.get(0);
					children.add(borrowChild);
					borrowChild.parent = this;

					// 把rightSibling的最右边的关键字移到父节点上去
					parent.words.add(index + 1, rightSibling.words.get(0));
					parent.children.get(index + 1).words.remove(0);

					// rightSibling删除掉这个儿子
					((NonLeaf) parent.children.get(index + 1)).children
							.remove(0);
				}
				if (parent.words.size() < ORDER / 2)
					parent.update(T);

			}
			// 和left sibling 合并
			else {
				if (index > 0
						&& (parent.children.get(index - 1).words.size()) <= ORDER / 2)
					merge((NonLeaf) parent.children.get(index - 1), this);
				else
					merge(this, (NonLeaf) parent.children.get(index + 1));
				if (parent.words.size() < ORDER / 2)
					parent.update(T);

				/*
				 * if (parent.words.size() < ORDER / 2 && !parent.isRoot)
				 * parent.update(T); else if (parent.isRoot &&
				 * parent.children.size() == 1) { parent.children.get(0).parent
				 * = null; T.setRoot(parent.children.get(0)); parent.children =
				 * null; }
				 */

			}
		}
	}

	private void merge(NonLeaf node1, NonLeaf node2) {
		int index = node2.parent.searchChildren(node2);
		node1.words.add(node1.parent.words.get(index - 1));

		// 将node2中的key值全部copy到node1中
		for (int i = 0; i < node2.words.size(); i++) {
			node1.words.add(node2.words.get(i));
		}
		// 合并两节点的子节点
		for (int i = 0; i < node2.children.size(); i++) {
			node1.children.add(node2.children.get(i));
			node2.children.get(i).parent = node1;
		}
		// 删除node1和node2间的分隔单词

		node1.parent.words.remove(index - 1);
		node1.parent.children.remove(index);

	}
}
