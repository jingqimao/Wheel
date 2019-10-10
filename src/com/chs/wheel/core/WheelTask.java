package com.chs.wheel.core;

public class WheelTask {
	public long time;
	public Runnable task;
	public int poolSize;
	public WheelTask(long time, Runnable task,int poolSize) {
		this.time=time;
		this.task=task;
		this.poolSize=poolSize;
	}
}
