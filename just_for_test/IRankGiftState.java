package com.hawk.game.module.activity.rankGift;

import com.hawk.game.entity.PlayerEntity;
import com.hawk.game.player.Player;
import com.hawk.game.protocol.Activity2.HPRankGiftInfo;

/**
 * 活动状态接口,活动进行到不同阶段都有部分相同对外接口
 * @author qianhang
 *
 */
public interface IRankGiftState {
	
	/**
	 * 刷新竞技场排行榜
	 */
	public void resetArenaRank();
	
	/**
	 * 刷新经验排行榜
	 * @param entity
	 */
	public void resetExpRank(PlayerEntity entity);
	
	/**
	 * 启服时,从数据库加载经验排名
	 */
	public void loadExpRankList();
	
	/**
	 * 构造经验排名协议
	 * @param expRank
	 * @param player
	 */
	public void expRank(HPRankGiftInfo.Builder expRank, Player player);
	
	/**
	 * 构造竞技场排名协议
	 * @param arenaRank
	 * @param player
	 */
	public void ArenaRank(HPRankGiftInfo.Builder arenaRank, Player player);

	/**
	 * 重新进行经验排行
	 */
	public void toGetRankExp();
	
	/**
	 * 获取排名献礼活动的进行阶段
	 * @return
	 */
	public int getActivityStatus();
}
