package dictionary;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class UI extends JFrame {

	int option = 0;
	final int RBT = 1;
	final int BPT = 2;
	String fileName;
	JTextArea output = new JTextArea("Here show the result");

	RBTree_dictionary T1;
	BPlusTree_dictionary T2;

	public UI() throws IOException {

		JPanel rPanel = new JPanel(new BorderLayout());

		rPanel.add(select(), BorderLayout.NORTH);
		rPanel.add(lookUpPanel());

		setLayout(new BorderLayout(5, 10));
		add(managePanel(), BorderLayout.WEST);
		add(rPanel, BorderLayout.EAST);
	}

	public JPanel select() {
		JPanel selectPanel = new JPanel(new FlowLayout());

		JRadioButton rbButton = new JRadioButton("red-black tree");
		JRadioButton bpButton = new JRadioButton("b＋ tree");

		// 用group限制rbButton和bpButton只能选其一
		ButtonGroup group = new ButtonGroup();
		group.add(rbButton);
		group.add(bpButton);

		// 选择红黑树
		rbButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					T1 = new RBTree_dictionary();
				} catch (IOException e1) {

					e1.printStackTrace();
				}
				option = RBT;
			}

		});

		// 选择b+树
		bpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				option = BPT;

				try {
					T2 = new BPlusTree_dictionary();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		selectPanel.add(rbButton);
		selectPanel.add(bpButton);

		return selectPanel;
	}

	public JPanel managePanel() {
		JPanel managePanel = new JPanel();
		managePanel.setBorder(new TitledBorder("MANAGEMENT"));
		managePanel.setLayout(new GridLayout(2, 1, 0, 5));
		managePanel.add(managePanel1());
		managePanel.add(managePanel2());
		return managePanel;
	}

	public JPanel managePanel1() {
		// User chooses a file and execute the corresponding operations here.
		JPanel panel1 = new JPanel(new GridLayout(2, 1));

		final String[] fileNameList = { "1_initial.txt", "2_delete.txt",
				"3_insert.txt" };
		final JList browserList = new JList();
		browserList.setVisibleRowCount(1);
		JButton browser = new JButton("Browser");
		JButton submit = new JButton("Submit");

		browserList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				fileName = fileNameList[browserList.getSelectedIndex()];
			}

		});

		browser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				browserList.setListData(fileNameList);
			}

		});

		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 红黑树初始化
				if (fileName.equals("1_initial.txt") && option == RBT) {
					try {
						T1 = new RBTree_dictionary();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// b＋树初始化
				else if (fileName.equals("1_initial.txt") && option == BPT) {
					try {
						T2 = new BPlusTree_dictionary();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// 红黑树的删除
				else if (fileName.equals("2_delete.txt") && option == RBT) {
					ArrayList<String> deleteWordList;
					try {
						deleteWordList = Read.getDeleteWordList("2_delete.txt");
						for (int i = 0; i < deleteWordList.size(); i++) {
							T1.delete(
									T1,
									T1.RBTreeSearch(T1.root,
											deleteWordList.get(i)));
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				// B+树的文档删除
				else if (fileName.equals("2_delete.txt") && option == BPT) {
					ArrayList<String> deleteWordList;
					try {
						deleteWordList = Read.getDeleteWordList("2_delete.txt");

						for (int i = 0; i < deleteWordList.size(); i++)
							T2.delete(T2, deleteWordList.get(i), "");

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// 红黑树的插入
				else if (fileName.equals("3_insert.txt") && option == RBT) {
					ArrayList<Word> insertWordList;
					try {
						insertWordList = Read
								.getInserteWordList("3_insert.txt");
						for (int i = 0; i < insertWordList.size(); i++) {

							T1.insert(T1, insertWordList.get(i).english,
									insertWordList.get(i).chinese);
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// b+树的插入
				else if (fileName.equals("3_insert.txt") && option == BPT) {
					ArrayList<Word> insertWordList;
					try {
						insertWordList = Read
								.getInserteWordList("3_insert.txt");
						for (int i = 0; i < insertWordList.size(); i++) {
							T2.insert(T2, insertWordList.get(i).english,
									insertWordList.get(i).chinese);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else output.setText("请选择一种查询方式，并打开一个文档！");
			}
		});

		JPanel bsPanel = new JPanel(new FlowLayout());
		bsPanel.add(browser);
		bsPanel.add(submit);

		panel1.add(browserList);
		panel1.add(bsPanel);
		panel1.setBorder(new TitledBorder(""));
		return panel1;
	}

	public JPanel managePanel2() {
		// User inserts or deletes a single word here
		JPanel panel2 = new JPanel(new GridLayout(2, 1));
		final JTextField ejtf = new JTextField(15);
		final JTextField cjtf = new JTextField(15);
		JButton addButton = new JButton("Add");
		JButton deleteButton = new JButton("Delete");

		JPanel newWordPanel = new JPanel(new FlowLayout());
		newWordPanel.add(new JLabel("English: "));
		newWordPanel.add(ejtf);
		newWordPanel.add(new JLabel("Chinese: "));
		newWordPanel.add(cjtf);

		JPanel adPanel = new JPanel(new FlowLayout());
		adPanel.add(addButton);
		adPanel.add(deleteButton);

		// 给add按钮加监听器
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (option == RBT) {
					T1.insert(T1, ejtf.getText(), cjtf.getText());
				} else if (option == BPT)
					T2.insert(T2, ejtf.getText(), cjtf.getText());
				else
					output.setText("请从上面选择一种查询方式！");
			}
		});

		// 给deleteButton加监听器
		deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (option == RBT) {

					T1.delete(T1, T1.RBTreeSearch(T1.root, ejtf.getText()));
				} else if (option == BPT)
					T2.delete(T2, ejtf.getText(), cjtf.getText());
				else
					output.setText("请从上面选择一种查询方式！");
			}
		});

		panel2.add(newWordPanel);
		panel2.add(adPanel);
		panel2.setBorder(new TitledBorder(""));
		return panel2;
	}

	// look up 部分的大panel
	public JPanel lookUpPanel() {
		JPanel lookUpPanel = new JPanel();
		lookUpPanel.setLayout(new GridLayout(3, 1));
		lookUpPanel.setBorder(new TitledBorder("LOOK-UP"));
		lookUpPanel.add(lookUpPanel1());
		lookUpPanel.add(lookUpPanel2());
		lookUpPanel.add(output);
		return lookUpPanel;
	}

	// 在这个panel上输入要查询的单词
	public JPanel lookUpPanel1() {
		final JTextField inputField = new JTextField(20);
		JButton transButton = new JButton("Translate");

		transButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String english = inputField.getText();
				if (option == RBT)
					output.setText(T1.RBTreeSearch(T1.root, english).chinese);
				else if (option == BPT)
					output.setText(T2.search(T2.root, english));

				else
					output.setText("请从上面选择一种查询方式！");
			}

		});

		JPanel panel1 = new JPanel(new FlowLayout());
		panel1.add(inputField);
		panel1.add(transButton);
		return panel1;
	}

	// 在这个panel上输入查询范围
	public JPanel lookUpPanel2() {
		final JTextField start = new JTextField(8);
		final JTextField end = new JTextField(8);
		JButton submitButton = new JButton("Submit");

		submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					// 得到输入范围的开始和结束在初始化的字典中位置
					ArrayList<Word> insertWordList = Read
							.getInserteWordList("1_initial.txt");
					int startIndex = 0;
					while (!start.getText().equals(
							insertWordList.get(startIndex).english))
						startIndex++;
					int endIndex = 0;
					while (!end.getText().equals(
							insertWordList.get(endIndex).english))
						endIndex++;

					// 红黑树查询一段单词
					if (option == RBT) {
						String chineseList = " ";
						for (int i = startIndex; i <= endIndex; i++) {
							chineseList = chineseList
									+ T1.RBTreeSearch(T1.root,
											insertWordList.get(i).english).chinese
									+ T1.RBTreeSearch(T1.root,
											insertWordList.get(i).english).english
									+ " \n";
						}
						output.setText(chineseList);
					} else if (option == BPT) {
						String chineseList = " ";
						for (int i = startIndex; i <= endIndex; i++) {
							chineseList = chineseList
									+ T2.search(T2.root,
											insertWordList.get(i).english)
									+ insertWordList.get(i).english + " \n";
							System.out.println("oopa");
						}
						output.setText(chineseList);
					} else
						output.setText("请从上面选择一种查询方式！");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

		JPanel panel2 = new JPanel(new FlowLayout());
		panel2.add(new JLabel("Search from "));
		panel2.add(start);
		panel2.add(new JLabel("to"));
		panel2.add(end);
		panel2.add(submitButton);
		return panel2;
	}

	public static void main(String[] args) throws IOException {
		UI frame = new UI();
		frame.setSize(1000, 300);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
	}
}