package com.hawk.cross.server;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PackegeHandler extends BaseCSTickable {

	@Override
	public boolean onTick() {
		System.out.println("------ PackegeHandler onTick...   " + new Date());
		try {
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("------ finish PackegeHandler onTick  " + new Date());
		return false;
	}

}
