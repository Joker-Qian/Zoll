package com.hawk.cross.server;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BattleManager extends BaseCSTickable {

	@Override
	public boolean onTick() {
		System.out.println("------ BattleManager onTick...   " + new Date());
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("------ finish BattleManager onTick" + new Date());
		return true;
	}

}
