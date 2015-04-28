package com.hawk.cross.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CSBattle {
	private static final long THREE_SECOND = 3000;
	
	private List<ICSTickable> tickables;
	private static CSBattle instance = new CSBattle();
	public volatile boolean running = true;
	private long tickTime = 0;
	
	private CSBattle() {
		tickables = new ArrayList<ICSTickable>();
	}
	
	public static CSBattle getInstance() {
		return instance;
	}
	
	public void registeTick(ICSTickable tickable) {
		if (tickables.contains(tickable)) {
			return;
		}
		tickables.add(tickable);
	}
	
	public void run() {
		while(running) {
			long time = new Date().getTime();
			if (time - tickTime > THREE_SECOND) {
				tickTime = time;
				onTick();
			}
		}
	}

	private void onTick() {
		for (ICSTickable icsTickable : tickables) {
			icsTickable.onTick();
		}
	}
}
