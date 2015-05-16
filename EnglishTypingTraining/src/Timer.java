import javax.swing.JLabel;

public class Timer implements Runnable {
	// 0~60¬í timer
	JLabel output;
	int[] userData;
	Thread thread;
	int second;
	int miniSecond;
	final int End = 60;
	boolean isRunning;

	public Timer(JLabel output, int[] userData) {
		this.output = output;
		this.userData = userData;
		second = 0;
		miniSecond = 0;
		output.setText(getTime());
		thread = new Thread(this);
	}

	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
			increaseTimer();
			output.setText(getTime());
			if (second == End) {
				stop();
				output.setText("End");
			}
		}
	}

	private void increaseTimer() {
		miniSecond++;
		if (miniSecond == 100) {
			second++;
			miniSecond = 0;
		}
	}

	public int getSecond(){
		return second;
	}
	
	public String getTime() {
		String a = String.format("%02d", second);
		String b = String.format("%02d", miniSecond);
		return a + ":" + b;
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		isRunning = false;
	}
}
