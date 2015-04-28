package com.hawk.cross.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DBThread implements Runnable {
	private static int counte = 0;
	private static final int ID= counte ++ ;
	private static final int QUEUE_SIZE = 5000;
	private static final long WAIT_TIME = 20;
	
	private int taskIndex = 0;
	
	private volatile boolean running = true;
	private volatile boolean isFull = false;
	
	private ArrayBlockingQueue<DBTask> tasks;
	
	public DBThread() {
		tasks = new ArrayBlockingQueue<DBTask>(QUEUE_SIZE);
	}
	
	@Override
	public void run() {
		while(running) {
			try {
				DBTask task = tasks.poll();
				if (task == null) {
					TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
					continue;
				}
				
				task.setTaskId(taskIndex++);
				task.execute();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getID() {
		return ID;
	}
	
	public boolean addTask(DBTask task) {
		if (tasks.size() == QUEUE_SIZE) {
			full();
			return false;
		}
		notFull();
		return tasks.add(task);
	}

	public void shutDown() {
		this.running = false;
	}
	
	public boolean isTaskQueueFull() {
		return this.isFull;
	}
	
	private void full() {
		this.isFull = true;
	}
	
	private void notFull() {
		this.isFull = false;
	}
}
