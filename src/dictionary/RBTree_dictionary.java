package dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class RBTree_dictionary {
	Word nil = new Word();
	Word root = nil;// 初始化一个空节点

	public RBTree_dictionary() throws IOException {

		this.nil.color = "B";
		this.nil.english = "";
		this.nil.chinese = "";
		this.nil.lchild = nil;
		this.nil.rchild = nil;
		this.nil.parent = nil;

		ArrayList<Word> insertWordList = Read
				.getInserteWordList("1_initial.txt");
		for (int i = 0; i < insertWordList.size(); i++) {

			insert(this, insertWordList.get(i).english,
					insertWordList.get(i).chinese);
		}

	}

	// 插入一个节点
	public void insert(RBTree_dictionary T, String english, String chinese) {
		Word x = T.root;
		Word y = nil;
		Word z = new Word(english, chinese);
		z.parent = null;
		z.lchild = null;
		z.rchild = null;
		z.color = null;

		while (x != nil) {
			y = x;
			if (z.english.compareTo(x.english) < 0)
				x = x.lchild;
			else
				x = x.rchild;
		}
		z.parent = y;
		if (y == nil)
			T.root = z;

		else if (z.english.compareTo(y.english) < 0)
			y.lchild = z;
		else
			y.rchild = z;
		z.color = "R";
		z.lchild = nil;
		z.rchild = nil;
		RBInsertFixup(T, z);
		System.out
				.println("RB-Tree successfully insert : " + english + chinese);
	}

	private void RBInsertFixup(RBTree_dictionary T, Word z) {
		Word y = new Word();
		while (z.parent.color == "R") {
			if (z.parent == z.parent.parent.lchild) {
				y = z.parent.parent.rchild;
				if (y.color == "R") {
					z.parent.color = "B";
					y.color = "B";
					z.parent.parent.color = "R";
					z = z.parent.parent;
				} else {
					if (z == z.parent.rchild) {
						z = z.parent;
						LeftRotate(T, z);
					}
					z.parent.color = "B";
					z.parent.parent.color = "R";
					RightRotate(T, z.parent.parent);
				}
			} else {
				y = z.parent.parent.lchild;
				if (y.color == "R") {
					z.parent.color = "B";
					y.color = "B";
					z.parent.parent.color = "R";
					z = z.parent.parent;
				} else {
					if (z == z.parent.lchild) {
						z = z.parent;
						RightRotate(T, z);
					}
					z.parent.color = "B";
					z.parent.parent.color = "R";
					LeftRotate(T, z.parent.parent);
				}
			}
		}
		T.root.color = "B";
	}

	private void RightRotate(RBTree_dictionary t, Word word) {
		Word y;
		y = word.lchild;
		word.lchild = y.rchild;
		if (y.rchild != nil)
			y.rchild.parent = word;

		y.parent = word.parent;

		if (word.parent == nil)
			t.root = y;
		else if (word == word.parent.lchild)
			word.parent.lchild = y;
		else
			word.parent.rchild = y;

		y.rchild = word;
		word.parent = y;
	}

	private void LeftRotate(RBTree_dictionary t, Word x) {

		Word y = x.rchild;
		x.rchild = y.lchild;
		if (y.lchild != nil)
			y.lchild.parent = x;
		y.parent = x.parent;

		if (x.parent == nil) {
			t.root = y;
		} else if (x == x.parent.lchild) {
			x.parent.lchild = y;
		} else {
			x.parent.rchild = y;
		}

		y.lchild = x;
		x.parent = y;
	}

	public Word RBTreeSearch(Word t, String english) {

		if (t == nil) {
			System.out.println("没有找到");
			return nil;
		} else if (t.english.equals(english))
			// System.out.println(y.data);
			return t;

		else if (t.english.compareTo(english) > 0)
			return RBTreeSearch(t.lchild, english);

		else
			return RBTreeSearch(t.rchild, english);
	}

	// 删除节点
	public Word delete(RBTree_dictionary T, Word node) {
		Word y, x;

		if (node.lchild == nil || node.rchild == nil)
			y = node;
		else
			y = TreeSuccessor(node);

		if (y.lchild != nil)
			x = y.lchild;
		else
			x = y.rchild;

		x.parent = y.parent;
		if (y.parent == nil)
			T.root = x;

		else if (y == y.parent.lchild)
			y.parent.lchild = x;
		else
			y.parent.rchild = x;

		if (y != node) {
			node.english = y.english;
			node.chinese = y.chinese;
		}

		if (y.color == "B")
			RBDeleteFixup(T, x);

		return y;
	}

	// 删除调整
	private void RBDeleteFixup(RBTree_dictionary t, Word x) {
		Word w;
		while (x != t.root && x.color == "B") {
			if (x == x.parent.lchild) {
				w = x.parent.rchild;
				if (w.color == "R") {
					w.color = "B";
					w.parent.color = "R";
					LeftRotate(t, x.parent);
					w = x.parent.rchild;
				}

				if (w.lchild.color == "B" && w.rchild.color == "B") {
					w.color = "R";
					x = x.parent;
				} else {
					if (w.rchild.color == "B") {
						w.lchild.color = "R";
						w.color = "B";
						RightRotate(t, x);
						w = x.parent.rchild;
					}

					w.color = x.parent.color;
					x.parent.color = "B";
					w.rchild.color = "B";
					LeftRotate(t, x.parent);
					x = t.root;
				}
			} else {
				w = x.parent.lchild;
				if (w.color == "R") {
					w.color = "B";
					w.parent.color = "R";
					LeftRotate(t, x.parent);
					w = x.parent.rchild;
				}

				if (w.lchild.color == "B" && w.rchild.color == "B") {
					w.color = "R";
					x = x.parent;
				} else {
					if (w.rchild.color == "B") {
						w.lchild.color = "R";
						w.color = "B";
						LeftRotate(t, x);
						w = x.parent.rchild;
					}

					w.color = x.parent.color;
					x.parent.color = "B";
					w.rchild.color = "B";
					RightRotate(t, x.parent);
					x = t.root;
				}
			}
		}
		x.color = "B";
	}

	private Word TreeSuccessor(Word node) {
		Word y;
		if (node.rchild != nil)
			return TreeMinium(node.rchild);
		y = node.parent;
		while (y != nil && node == y.rchild) {
			node = y;
			y = y.parent;
		}
		return y;
	}

	private Word TreeMinium(Word node) {
		while (node.lchild != nil)
			node = node.lchild;
		return node;
	}

	// 打印红黑树,元素个数在500之内
	int count = 0;

	public void preOrderPrint(Word word, int level) {
		if (count <= 5000) {
			if (word == nil) {
				System.out.println("level = " + level + "  child = "
						+ word.isRChild() + "  NIL  ");
			} else {
				System.out.println("level = " + level + "  child = "
						+ word.isRChild() + " (" + word.english + " "
						+ word.chinese + " " + word.color + ")  ");

				level++;
				preOrderPrint(word.lchild, level);

				preOrderPrint(word.rchild, level);

			}
		}
		count++;
	}

	public static void main(String[] args) throws IOException {
		RBTree_dictionary tree = new RBTree_dictionary();
		// tree.insert(tree, "good", "好");
		tree.insert(tree, "hello", "你好");
		tree.preOrderPrint(tree.root, 0);
		tree.delete(tree, tree.RBTreeSearch(tree.root, "hello"));
		System.out
				.println(tree.RBTreeSearch(tree.root, "hello").lchild == tree.nil);

		tree.preOrderPrint(tree.root, 0);
	}

}
