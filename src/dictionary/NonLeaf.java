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

	// **�м�ڵ�Ĳ��룬���߷���
	public void insertOrSplit(BPlusTree_dictionary T, Word word) {
		words = insertWord(word);

		// �ýڵ���ѳ���������,�������ĺ���ƽ���ָ�����������
		NonLeaf left = new NonLeaf();
		NonLeaf right = new NonLeaf();
		left.isRoot = false;
		right.isRoot = false;

		// ���������ڵ�ؼ��ֳ���
		int leftSize = ORDER / 2;
		int rightSize = ORDER / 2;

		// �����ӽڵ㵽���ѳ������½ڵ㣬�����¹ؼ���
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

		// ����ýڵ�ʱ���ڵ㣬��ֱ�Ӱ��м�ĵ��ʲ����µĸ��ڵ�
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
			// ������Ǹ��ڵ㣬����µ���
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

	// ���� ���ڲ��ڵ��fill factorС��50%ʱ�����ڲ��ڵ���е���
	public void update(BPlusTree_dictionary T) {
		int index = 0;
		if (parent != null)
			index = parent.searchChildren(this);

		// ����Ǹ��ڵ�
		if (isRoot) {

			// ����ӽڵ������ڵ���2�����õ���,����ϲ������ӽڵ�
			if (words.size() == 0) {
				T.root = children.get(0);
				T.root.isRoot = true;
				T.root.parent = null;
				// children = null;
				words = null;
			}
		} else {
			// �������sibling
			if (index > 0) {
				int lSiblingKeys = parent.children.get(index - 1).words.size();
				// �������ֵܹؼ��ֵ���Ŀ������һ�룬�Ͱ��������ұߵĶ��ӽ����
				if (lSiblingKeys > ORDER / 2) {
					// ��ԭ���������sibling��word�Ƶ�����words�еĵ�һ��
					words.add(0, parent.words.get(index - 1));

					NonLeaf leftSibling = (NonLeaf) parent.children
							.get(index - 1);
					BPlusTreeNode borrowChild = leftSibling.children
							.get(lSiblingKeys);
					children.add(0, borrowChild);
					borrowChild.parent = this;

					// ��leftSibling�����ұߵĹؼ����Ƶ����ڵ���ȥ
					parent.words.add(index - 1,
							leftSibling.words.get(lSiblingKeys - 1));
					parent.children.get(index - 1).words
							.remove(lSiblingKeys - 1);

					// leftSiblingɾ�����������
					((NonLeaf) parent.children.get(index - 1)).children
							.remove(lSiblingKeys);
					if (parent.words.size() < ORDER / 2)
						parent.update(T);

				}

			}

			// ����������ֵ�,�������ֵܲ���������û����sibling
			if (((index > 0 && parent.children.get(index - 1).words.size() <= ORDER / 2) || index == 0)
					&& index < parent.children.size() - 1) {
				int rSiblingKeys = parent.children.get(index + 1).words.size();
				// �������ֵܹؼ��ֵ���Ŀ������һ�룬�Ͱ���������ߵĶ��ӽ����
				if (rSiblingKeys > ORDER / 2) {
					// ��ԭ���������sibling��word�Ƶ�����words�е����һ��
					words.add(parent.words.get(index + 1));

					NonLeaf rightSibling = (NonLeaf) parent.children
							.get(index + 1);
					BPlusTreeNode borrowChild = rightSibling.children.get(0);
					children.add(borrowChild);
					borrowChild.parent = this;

					// ��rightSibling�����ұߵĹؼ����Ƶ����ڵ���ȥ
					parent.words.add(index + 1, rightSibling.words.get(0));
					parent.children.get(index + 1).words.remove(0);

					// rightSiblingɾ�����������
					((NonLeaf) parent.children.get(index + 1)).children
							.remove(0);
				}
				if (parent.words.size() < ORDER / 2)
					parent.update(T);

			}
			// ��left sibling �ϲ�
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

		// ��node2�е�keyֵȫ��copy��node1��
		for (int i = 0; i < node2.words.size(); i++) {
			node1.words.add(node2.words.get(i));
		}
		// �ϲ����ڵ���ӽڵ�
		for (int i = 0; i < node2.children.size(); i++) {
			node1.children.add(node2.children.get(i));
			node2.children.get(i).parent = node1;
		}
		// ɾ��node1��node2��ķָ�����

		node1.parent.words.remove(index - 1);
		node1.parent.children.remove(index);

	}
}
