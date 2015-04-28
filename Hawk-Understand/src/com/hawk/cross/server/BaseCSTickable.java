package com.hawk.cross.server;

public abstract class BaseCSTickable implements ICSTickable{
	
	public BaseCSTickable() {
		rigestSelf();
	}
	
	private void rigestSelf() {
		CSBattle.getInstance().registeTick(this);
	}
	
}
