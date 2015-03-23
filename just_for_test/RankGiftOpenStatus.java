package com.hawk.game.module.activity.rankGift;

import java.util.Collections;
import java.util.List;

import org.hawk.db.HawkDBManager;

import com.hawk.game.ServerData;
import com.hawk.game.config.SysBasicCfg;
import com.hawk.game.entity.PlayerEntity;
import com.hawk.game.manager.ArenaManager;
import com.hawk.game.player.Player;
import com.hawk.game.protocol.Activity2.HPRankGiftInfo;
import com.hawk.game.protocol.Activity2.HPRankGiftItem;
import com.hawk.game.protocol.Arena.ArenaItemInfo;
import com.hawk.game.util.GsConst;

public class RankGiftOpenStatus extends BaseRankGiftState{

	@Override
	public void resetArenaRank() {
		RankGiftServerStatus serverStatus = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.RANK_GIFT, RankGiftServerStatus.class);
		arenaRankGiftList.clear();
		serverStatus.clearArenaRank();
		ArenaManager arenaMan = ArenaManager.getInstance();
		// 从竞技场管理器获取前十排名
		List<ArenaItemInfo.Builder> rankingList = arenaMan.getRankingList(SysBasicCfg.getInstance().getRankGiftPlayerNum());
		for (ArenaItemInfo.Builder arenaItem : rankingList) {
			HPRankGiftItem.Builder rankItem = HPRankGiftItem.newBuilder();
			if (arenaItem.getIdentityType() == GsConst.Arena.ROBOT_OPPONENT) {
				setParams(rankItem, arenaItem.getPlayerId(), arenaItem.getRank(), arenaItem.getName(), 0, 0, NPC_RANK);
			} else {
				setParams(rankItem, arenaItem.getPlayerId(), arenaItem.getRank(), arenaItem.getName(), 0, 0, PLAYER_RANK);
			}
			arenaRankGiftList.add(rankItem);
			serverStatus.addArenaRank(rankItem.getPlayerId(), rankItem.getIsNPC());
		}
	}

	@Override
	public void resetExpRank(PlayerEntity entity) {
		int level = entity.getLevel();
		int exp = entity.getExp();
		int index = -1;
		// 查看集合中是否有该玩家的信息
		for (RankGiftPlayerData rankGiftPlayerData : expRankPlayerEntity) {
			if (entity.getId() == rankGiftPlayerData.getPlayerId()) {
					rankGiftPlayerData.setPlayerLevel(level);
					rankGiftPlayerData.setPlayerExp(exp);
					Collections.sort(expRankPlayerEntity);
					setExpRank(expRankPlayerEntity);
				return;
			}
		}
		// 检查玩家的经验是否能够进入排名
		for (int i = 0; i < expRankGiftList.size() - 1; i++) {
			HPRankGiftItem.Builder builder = expRankGiftList.get(i);
			HPRankGiftItem.Builder builder2 = expRankGiftList.get(i + 1);
			if ((builder.getLevel() > level && builder2.getLevel() < level) || (builder.getLevel() == level && builder.getExp() > exp)) {
				index = i + 1;
				break;
			} else if (builder.getLevel() == level && builder.getExp() < exp) {
				index = i;
				break;
			}
		}
		// 更新排名数据
		if (index != -1) {
			expRankPlayerEntity.add(index, new RankGiftPlayerData(entity));
			if (expRankPlayerEntity.size() > RANK_GIFT_PLAYER_NUM) {
				expRankPlayerEntity.subList(RANK_GIFT_PLAYER_NUM, expRankPlayerEntity.size()).clear();
			}
			Collections.sort(expRankPlayerEntity);
			setExpRank(expRankPlayerEntity);
		}
	}

	@Override
	public void loadExpRankList() {
		List<PlayerEntity> playerEntitys = HawkDBManager.getInstance().query("from PlayerEntity where invalid = 0");
		Collections.sort(playerEntitys);
		if (playerEntitys.size() > RANK_GIFT_PLAYER_NUM) {
			playerEntitys.subList(RANK_GIFT_PLAYER_NUM, playerEntitys.size()).clear();
		}
		for (PlayerEntity playerEntity : playerEntitys) {
			expRankPlayerEntity.add(new RankGiftPlayerData(playerEntity));
		}
		setExpRank(expRankPlayerEntity);
	}

	@Override
	public void expRank(HPRankGiftInfo.Builder expRank, Player player) {
		List<HPRankGiftItem.Builder> subList = expRankGiftList;
		if (expRankGiftList.size() > SysBasicCfg.getInstance().getRankGiftPlayerNum()) {
			subList = expRankGiftList.subList(0, SysBasicCfg.getInstance().getRankGiftPlayerNum());
		}
		int rank = -1;
		for (HPRankGiftItem.Builder builder : subList) {
			expRank.addRankList(builder);
			if (builder.getPlayerId() == player.getId()) {
				rank = expRankGiftList.indexOf(builder) + 1;
			}
		}
		expRank.setSelfRank(rank);
	}

	@Override
	public void ArenaRank(HPRankGiftInfo.Builder arenaRank, Player player) {
		ArenaManager arenaMan = ArenaManager.getInstance();
		ArenaItemInfo.Builder self = arenaMan.getSelfArenaInfo(player);
		// 玩家自己的排行
		if (self == null) {
			arenaRank.setSelfRank(-1);
		} else {
			arenaRank.setSelfRank(self.getRank());
		}
		for (HPRankGiftItem.Builder builder : arenaRankGiftList) {
			arenaRank.addRankList(builder);
		}
	}

	@Override
	public void toGetRankExp() {
		loadStatusMap();
		for (RankGiftPlayerData rankData : expRankPlayerEntity) {
			int lastLogoutTime = getLogoutTime(rankData.getPlayerId());
			if (lastLogoutTime == 0)
				continue;
			countOfflineExp(rankData, lastLogoutTime);
		}
		Collections.sort(expRankPlayerEntity);
		setExpRank(expRankPlayerEntity);
	}

	@Override
	public int getActivityStatus() {
		return 1;
	}

}
