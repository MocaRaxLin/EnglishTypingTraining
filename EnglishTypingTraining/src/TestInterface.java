import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TestInterface implements Runnable {
	int[] userData;
	JLabel timerLabel;
	Stack<String> words;
	Stack<Integer> questionNo;
	int number;
	String[] questionWords;

	JFrame f;
	String cur_input;
	JLabel[] wordsLabel;
	int cur_question;

	Timer timer;
	KeyListener keylistener;
	JTextField inputField;
	public TestInterface(String path, int[] userData) {
		this.userData = userData;
		words = new Stack<String>();
		questionNo = new Stack<Integer>();
		number = 10;
		questionWords = new String[number];
		loadFile(path);
		setQuestionNo();
		setQuestion();

		// GUI
		cur_question = 0;
		gui(path);

	}

	private void gui(String path) {
		f = new JFrame(path);
		f.setSize(600, 400);
		f.setResizable(false);
		f.setVisible(true);

		JPanel panel = new JPanel();

		// test word
		wordsLabel = new JLabel[questionWords.length];
		for (int i = 0; i < questionWords.length; i++) {
			wordsLabel[i] = new JLabel(questionWords[i]);
			wordsLabel[i].setFont(new Font("Serif", Font.BOLD, 40));
			panel.add(wordsLabel[i]);
		}
		setQuestionVisible(wordsLabel, cur_question);

		// input textfield
		inputField = new JTextField(20);
		keylistener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					cur_input = inputField.getText();
					if (cur_input.equals(questionWords[cur_question])) {
						inputField.setText("");
						cur_question++;
						if (cur_question < number) {
							setQuestionVisible(wordsLabel, cur_question);
						} else {
							timer.stop();
							timerLabel.setText("End");
							end();
						}

					} else {
						wordsLabel[cur_question].setForeground(Color.RED);
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		};
		
		panel.add(inputField);

		// show timer
		timerLabel = new JLabel();
		timerLabel.setFont(new Font("Serif", Font.BOLD, 40));
		timer = new Timer(timerLabel, userData);
		panel.add(timerLabel);

		// end listener
		final Thread listener = new Thread(this);

		// start timer
		final JButton startButton = new JButton("開始!");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inputField.addKeyListener(keylistener);
				timer.start();
				listener.start();
				startButton.setVisible(false);
			}
		});
		panel.add(startButton);

		panel.setVisible(true);

		f.add(panel);
	}

	private void end() {
		inputField.addKeyListener(null);
		
		int newScore = timer.getSecond();
		if (newScore == timer.End) {
			JOptionPane.showMessageDialog(null, "挑戰失敗，得加把勁才行！", "失敗",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null,
					"恭喜完成10字測驗\n花費時間為" + timer.getTime(), "完成",
					JOptionPane.INFORMATION_MESSAGE);
		}

		setNewRecord(userData, newScore);
		show(userData);
	}

	private void setNewRecord(int[] userData2, int newScore) {
		for (int i = 0; i < userData2.length - 1; i++) {
			userData2[i] = userData2[i + 1];
		}
		userData2[userData2.length - 1] = newScore;
	}

	private void setQuestionVisible(JLabel[] wordsLabel, int cur_question) {
		for (int i = 0; i < wordsLabel.length; i++) {
			wordsLabel[i].setVisible(false);
		}
		wordsLabel[cur_question].setVisible(true);
	}

	private void show(int[] userData2) {
		for (int i = 0; i < userData2.length; i++) {
			System.out.print(userData2[i] + ", ");
		}
		System.out.println();
	}

	private void setQuestion() {
		for (int i = 0; i < number; i++) {
			questionWords[i] = words.get(questionNo.get(i));
		}
	}

	private void setQuestionNo() {
		int counter = 0;
		while (counter < number) {
			int t = (int) (Math.random() * words.size());
			if (questionNo.search(t) == -1) {
				questionNo.push(t);
				counter++;
			}
		}
	}

	private void loadFile(String path) {
		try {
			Scanner file = new Scanner(new File(path));
			while (file.hasNext()) {
				words.push(file.nextLine());
			}
			file.close();
		} catch (FileNotFoundException e) {
		}
	}

	@Override
	public void run() {
		while (!timerLabel.getText().equals("End")) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		end();
	}

}
