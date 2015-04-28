package com.hawk.cross.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DBManager extends BaseCSTickable {
	private static final int THREAD_POOL_SIZE = 4;
	private static DBManager instance;
	private List<DBThread> dbThreads;
	private List<DBTask> notDealTaskList;
	
	public DBManager() {
		if (instance == null) {
			instance = this;
			init();
		}
	}
	
	public static DBManager getInstance() {
		return instance;
	}
	
	@Override
	public boolean onTick() {
		System.out.println("------ DBManager onTick...   " + new Date());
		sendTask();
		return false;
	}

	private void sendTask() {
		for (int i = 0; i < 5000; i++) {
			final int index = i % 4;
			DBTask task = new DBTask() {
				
				@Override
				public void execute() {
					System.out.println("--DBThread : " + index + "  --TaskId : " + this.getTaskId() + "  -------");
					try {
						TimeUnit.MILLISECONDS.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("		" + "  --TaskId : " + this.getTaskId() + " is finished -------");
				}
			};
			if (!dbThreads.get(index).addTask(task)) {
				notDealTaskList.add(task);
			}
		}
		Iterator<DBTask> iterator = notDealTaskList.iterator();
		while (iterator.hasNext()) {
			for (DBThread dbThread : dbThreads) {
				if (!dbThread.isTaskQueueFull()) {
					System.out.println("--DBThread : " + dbThread.getID() + " is not full ---");
					dbThread.addTask(iterator.next());
					iterator.remove();
					break;
				}
			}
		}
		
	}
	
	public void init() {
		dbThreads = new ArrayList<DBThread>();
		notDealTaskList = new ArrayList<DBTask>();
		for (int i = 0; i < THREAD_POOL_SIZE; i++) {
			dbThreads.add(new DBThread());
		}
	}
	
	public void allStart() {
		for (DBThread dbThread : dbThreads) {
			new Thread(dbThread).start();
		}
	}
	
}
