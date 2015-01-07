package dictionary;

public class Word {
	public String english;
	public String chinese;
	public String color;
	public Word parent;
	public Word lchild;
	public Word rchild;

	public Word() {

	}

	public Word(String newEng, String newChn) {
		english = newEng;
		chinese = newChn;
		color = "R";
	}

	public int isRChild() {
		if (parent != null && parent.rchild == this)
			return 1;
		else
			return 0;
	}

	public int compareTo(Word another) {
		return english.compareTo(another.english);
	}

}
