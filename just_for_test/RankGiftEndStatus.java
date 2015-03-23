package com.hawk.game.module.activity.rankGift;

import java.util.List;

import com.hawk.game.ServerData;
import com.hawk.game.entity.PlayerEntity;
import com.hawk.game.manager.SnapShotManager;
import com.hawk.game.player.Player;
import com.hawk.game.protocol.Activity2.HPRankGiftItem;
import com.hawk.game.protocol.Activity2.HPRankGiftInfo.Builder;
import com.hawk.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.hawk.game.util.GsConst;

public class RankGiftEndStatus extends BaseRankGiftState {

	@Override
	public void resetArenaRank() {
		
	}

	@Override
	public void resetExpRank(PlayerEntity entity) {
		
	}

	@Override
	public void loadExpRankList() {
		
	}

	@Override
	public void expRank(Builder expRank, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void ArenaRank(Builder arenaRank, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toGetRankExp() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 生成经验排行榜（活动截至但未关闭时）
	 */
	protected void setExpRank() {
		RankGiftServerStatus serverStatus = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.RANK_GIFT, RankGiftServerStatus.class);
		List<Integer> expRankGift = serverStatus.getExpRankGift();
		for (int i = 0; i < expRankGift.size(); i++) {
			int playerId = expRankGift.get(i);
			PlayerSnapshotInfo.Builder playerSnapShot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			HPRankGiftItem.Builder rankItem = HPRankGiftItem.newBuilder();
			// 有经验的对象都不是NPC
			setParams(rankItem, playerId, i + 1, playerSnapShot.getMainRoleInfo().getName(), playerSnapShot.getMainRoleInfo().getLevel(), 
							playerSnapShot.getMainRoleInfo().getExp(), PLAYER_RANK);
			expRankGiftList.add(rankItem);
		}
	}

	@Override
	public int getActivityStatus() {
		// TODO Auto-generated method stub
		return 0;
	}
	


}
