import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GUI implements Runnable {
	JFrame f;
	JPanel loginPanel;
	int[] userData;
	String userId;
	JPanel startPanel;
	JLabel userInfo;
	String wordRoot = "C:/English typing training/word_bank";
	String userInfoRoot = "C:/English typing training/user_info";
	JTextArea userDataGraph;

	
	
	public GUI() {
		f = new JFrame("English Typing Training");
		f.setSize(800, 600);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		loginPanel();
		startPanel();
	}

	private void loginPanel() {
		loginPanel = new JPanel();
		JLabel whoLabel = new JLabel("你是誰?");
		final JTextField inputName = new JTextField(20);
		JButton login = new JButton("登入");
		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userId = inputName.getText();
				userData = loadUserInfo(userId);
				// show(userData);
				userInfo.setText("挑戰者：" + userId);
				loadText(userDataGraph, userData);

				Thread update = new Thread(GUI.this);
				update.start();

				loginPanel.setVisible(false);
				startPanel.setVisible(true);
			}
		});
		loginPanel.add(whoLabel);
		loginPanel.add(inputName);
		loginPanel.add(login);
		loginPanel.setVisible(true);
		f.add(loginPanel);
		f.setVisible(true);
	}

	private int[] loadUserInfo(String userId) {
		int[] output = new int[10];
		File userFile = new File(userInfoRoot);
		String[] files = userFile.list();
		int index = Arrays.binarySearch(files, userId + ".txt");
		if (index >= 0) {
			String path = userInfoRoot + "/" + files[index];
			//System.out.println(path);
			userFile = new File(path);
			try {
				Scanner data = new Scanner(userFile);
				for (int i = 0; i < output.length; i++) {
					String s = data.nextLine();
					//System.out.println(s);
					output[i] = Integer.parseInt(s);
				}
				data.close();
			} catch (FileNotFoundException e) {
			}
		} else {
			try {
				FileWriter file = new FileWriter(userInfoRoot + "/" + userId
						+ ".txt");
				for (int i = 0; i < output.length; i++) {
					output[i] = 0;
					file.write("0\r\n");
				}
				file.close();
			} catch (IOException e) {
			}
		}
		return output;
	}

	private void startPanel() {
		startPanel = new JPanel(new BorderLayout());
		JLabel title = new JLabel("          English Typing Training");
		title.setFont(new Font("Serif", Font.BOLD, 50));
		startPanel.add(title, BorderLayout.NORTH);

		JPanel selectPanel = new JPanel();

		// select test
		final JComboBox<String> test = new JComboBox<String>();
		String[] testString = getChildren(wordRoot);
		for (int i = 0; i < testString.length; i++) {
			test.addItem(testString[i]);
		}
		selectPanel.add(test);

		// select level
		final JComboBox<String> level = new JComboBox<String>();
		selectPanel.add(level);
		// action listener
		test.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = wordRoot + "/" + test.getSelectedItem();
				String levelString[] = getChildren(s);
				level.removeAllItems();
				for (int i = 0; i < levelString.length; i++) {
					level.addItem(levelString[i]);
				}
			}
		});

		JButton assure = new JButton("確認挑戰");
		assure.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (test.getSelectedItem() == null || level.getSelectedItem() == null){
					JOptionPane.showMessageDialog(null,
							"請選擇單字庫",null,JOptionPane.INFORMATION_MESSAGE);
				}else{
					open(wordRoot + "/" + test.getSelectedItem() + "/"
							+ level.getSelectedItem());
				}
				
			}
		});
		selectPanel.add(assure);
		selectPanel.setVisible(true);

		userDataGraph = new JTextArea(10, 5);
		userDataGraph.setEditable(false);
		selectPanel.add(userDataGraph);

		startPanel.add(selectPanel, BorderLayout.CENTER);

		userInfo = new JLabel();
		startPanel.add(userInfo, BorderLayout.SOUTH);

		startPanel.setVisible(false);
		f.add(startPanel);
		f.setVisible(true);
	}

	private void loadText(JTextArea userDataGraph2, int[] userData2) {
		userDataGraph2.setText("過往紀錄(s)：\n");
		for (int i = userData2.length - 1; i >= 0; i--) {
			userDataGraph2.append(userData2[i] + "\n");
		}
	}

	public void open(String path) {
		// System.out.println(path);
		new TestInterface(path, userData);
	}

	public String[] getChildren(String path) {
		File f = new File(path);
		if (f.isDirectory()) {
			return f.list();
		} else {
			return null;
		}

	}
	
	private static boolean hasFile(String path) {
		File f = new File(path);
		if(f.exists()){
			return true;
		}
		return false;
	}

	// updata user graph
	@Override
	public void run() {
		while (true) {
			loadText(userDataGraph, userData);
			saveData(userId, userData);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}

	private void saveData(String userId2, int[] userData2) {
		FileWriter file;
		try {
			file = new FileWriter(userInfoRoot + "/" + userId2 + ".txt");
			for (int i = 0; i < userData2.length; i++) {
				file.write(userData2[i]+"\r\n");
			}
			file.close();
		} catch (IOException e) {}
		
	}

	public static void main(String[] args) {
		String path = "C:/English typing training/user_info";
		
		URL url = GUI.class.getClass().getResource("/user_info");
		System.out.println("url:"+url);
		if(!hasFile(path)){
			copyFile(url,path);
		}
		path = "C:/English typing training/word_bank";
		
		url = GUI.class.getClass().getResource("/word_bank");
		System.out.println("url:"+url);
		if(!hasFile(path)){
			copyFile(url,path);
		}
		new GUI();
	}
	
	private static void copyFile(URL url, String path) {
		File fromFile = new File(url.getFile());
		File toFile = new File(path);
		toFile.mkdirs();
		copyDirectory(fromFile, toFile);

		String[] fileList = fromFile.list();
		for(int i = 0;i<fileList.length;i++){
			System.out.println(fileList[i]);
		}
		
	}
	
	
	 // copy file
    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
    
    // copy dir
    public static void copyDirectory(File source, File target) {
        File[] file = source.listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                File sourceDemo = new File(source.getAbsolutePath() + "/"
                        + file[i].getName());
                File destDemo = new File(target.getAbsolutePath() + "/"
                        + file[i].getName());
                try {
                    copy(sourceDemo, destDemo);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (file[i].isDirectory()) {
                File sourceDemo = new File(source.getAbsolutePath() + "/"
                        + file[i].getName());
                File destDemo = new File(target.getAbsolutePath() + "/"
                        + file[i].getName());
                destDemo.mkdir();
                copyDirectory(sourceDemo, destDemo);
            }
        }
    }
}
