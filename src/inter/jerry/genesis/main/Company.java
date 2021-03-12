package inter.jerry.genesis.main;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class Company {

	// number of employee
	private final int nums;

	// Internally pool is an array
	private final Thread[] workers;

	// Task queues
	private final Map<Integer, LinkedBlockingQueue<Task>> queues;

	public Company(int nums) {
		this.nums = nums;

		queues = new HashMap<Integer, LinkedBlockingQueue<Task>>();
		queues.put(Priority.EE, new LinkedBlockingQueue<Task>());
		queues.put(Priority.LT, new LinkedBlockingQueue<Task>());
		queues.put(Priority.PM, new LinkedBlockingQueue<Task>());

		workers = new Thread[nums];
		addWorker(Priority.PM, "PM", 0);
		addWorker(Priority.LT, "LT", 1);
		for (int i = 2; i < nums; i++) {
			addWorker(Priority.EE, "EE", i);
		}
	}

	private void addWorker(int Priority, String position, int index) {
		WorkerThread worker = new WorkerThread(Priority);
		workers[index] = new Thread(worker);
		workers[index].setPriority(Priority + 1);
		workers[index].setName(position + "-" + index);
		workers[index].start();
	}

	public void execute(Task task) {
		execute(task, Priority.EE);
	}

	public void execute(Task task, int priority) {
		synchronized (queues) {
			queues.get(priority).add(task);
			queues.notifyAll();
		}
	}

	private class WorkerThread implements Runnable {
		int priority;

		public WorkerThread(int priority) {
			this.priority = priority;
		}

		public void run() {
			Task task;
			while (true) {
				synchronized (queues) {
					while (isQueuesEmpty(priority)) {
						try {
							queues.wait();
						} catch (InterruptedException e) {
							System.out.println("An error occurred while queue is waiting: " + e.getMessage());
						}
					}

					if (this.priority > 0 && queues.get(this.priority).size() != 0) {
						task = (Task) queues.get(this.priority).poll();
					} else {
						task = !isSubordinateFree(this.priority) ? (Task) queues.get(0).poll() : null;
					}
				}
				if (task != null) {
					try {
						System.out.printf("[%s] %s start %s(%ds)\n", LocalTime.now(), Thread.currentThread().getName(),task.getName(),task.getDuration());
						task.run();

						// random exception
						if (this.priority <= 1 && Math.random() < 0.5) {
							throw new RandomException();
						}
						System.out.printf("[%s] %s do %s SUCCESS\n", LocalTime.now(), Thread.currentThread().getName(),task.getName());
					} catch (RandomException e) {
						execute(task, this.priority + 1);
						System.out.printf("[%s] %s do %s FAILED\n", LocalTime.now(), Thread.currentThread().getName(),task.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private boolean isQueuesEmpty(int priority) {
		synchronized (queues) {
			boolean isEmpty = true;
			for (int i = 0; i <= priority; i++) {
				if (queues.get(i).size() != 0) {
					isEmpty = false;
				}
			}
			return isEmpty;
		}
	}

	private boolean isSubordinateFree(int priority) {
		synchronized (workers) {
			for (Thread thread : workers) {
				if (priority >= thread.getPriority() && thread.getState() == Thread.State.BLOCKED) {
					return true;
				}
			}
			return false;
		}
	}

	public void shutdown() {
		System.out.println("Shutting down thread pool");
		for (int i = 0; i < nums; i++) {
			workers[i] = null;
		}
	}
}
