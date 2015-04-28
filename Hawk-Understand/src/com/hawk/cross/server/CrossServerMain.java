package com.hawk.cross.server;

public class CrossServerMain {
	public static void main(String[] args) {
		new BattleManager();
		DBManager dbManager = new DBManager();
		new PackegeHandler();
		dbManager.allStart();
		CSBattle.getInstance().run();
	}
}
