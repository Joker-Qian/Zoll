package com.hawk.cross.server;

public abstract class DBTask {
	private int taskId;
	
	public void setTaskId(int id) {
		this.taskId = id;
	}
	
	public int getTaskId() {
		return this.taskId;
	}
	
	public abstract void execute();
}
