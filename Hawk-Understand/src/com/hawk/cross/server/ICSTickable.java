package com.hawk.cross.server;

/**
 * 帧更新接口;
 * 
 * @author dell
 *
 */
public interface ICSTickable {
	/**
	 * 帧更新;
	 * 
	 * @return
	 */
	public boolean onTick();
}
