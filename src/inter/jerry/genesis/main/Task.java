package inter.jerry.genesis.main;

import java.util.concurrent.TimeUnit;

public class Task implements Runnable {
	private String name;
	private Long duration;

	public Task(String name) {
		this.name = name;
		this.duration = (long) (Math.random() * 10+1);
	}

	public String getName() {
		return name;
	}

	public Long getDuration() {
		return duration;
	}

	public void run() {
		try {
			TimeUnit.SECONDS.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
