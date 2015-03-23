package com.hawk.game.module.activity.rankGift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hawk.config.HawkConfigManager;
import org.hawk.db.HawkDBManager;
import org.hawk.os.HawkTime;

import com.hawk.game.ServerData;
import com.hawk.game.config.MapCfg;
import com.hawk.game.config.SysBasicCfg;
import com.hawk.game.entity.MapStatisticsEntity;
import com.hawk.game.entity.StateEntity;
import com.hawk.game.manager.SnapShotManager;
import com.hawk.game.protocol.Activity2.HPRankGiftItem;
import com.hawk.game.protocol.Snapshot.PlayerSnapshotInfo;
import com.hawk.game.util.GsConst;
import com.hawk.game.util.PlayerUtil;

public abstract class BaseRankGiftState implements IRankGiftState {
	/** 为NPC*/
	protected static final int NPC_RANK = 0;
	/** 为玩家*/
	protected static final int PLAYER_RANK = 1;
	
	/** 管理两倍的经验排名数*/
	protected static int RANK_GIFT_PLAYER_NUM = SysBasicCfg.getInstance().getRankGiftPlayerNum() * 2;
	/** 经验排行列表*/
	protected List<HPRankGiftItem.Builder> expRankGiftList;
	/** 根据经验排名的playerEntity列表*/
	protected List<RankGiftPlayerData> expRankPlayerEntity;
	/** 竞技场排行列表*/
	protected List<HPRankGiftItem.Builder> arenaRankGiftList;
	/** 玩家所在地图信息*/
	private Map<Integer, MapCfg> playerMapCfg;

	public BaseRankGiftState() {
		this.expRankGiftList = new ArrayList<HPRankGiftItem.Builder>();
		this.arenaRankGiftList = new ArrayList<HPRankGiftItem.Builder>();
		this.expRankPlayerEntity = new ArrayList<RankGiftPlayerData>();
		this.playerMapCfg = new HashMap<Integer, MapCfg>();
	}
	
	/**
	 * 生成经验排行榜（活动开放时）
	 * @param playerEntitys
	 */
	protected void setExpRank(List<RankGiftPlayerData> rankGiftPlayerData) {
		expRankGiftList.clear();
		RankGiftServerStatus serverStatus = ServerData.getInstance().getServerStatus(GsConst.ServerStatusId.RANK_GIFT, RankGiftServerStatus.class);
		serverStatus.clearExpRank();
		for (int i = 0; i < rankGiftPlayerData.size(); i++) {
			RankGiftPlayerData playerData = rankGiftPlayerData.get(i);
			HPRankGiftItem.Builder builder = HPRankGiftItem.newBuilder();
			setParams(builder,playerData.getPlayerId(), i + 1, playerData.getPlayerName(), playerData.getPlayerLevel(), playerData.getPlayerExp(), PLAYER_RANK);
			expRankGiftList.add(builder);
			serverStatus.addExpRank(playerData.getPlayerId());
		}
		// TODO 没必要落地，以后修改
		ServerData.getInstance().updateServerData(GsConst.ServerStatusId.RANK_GIFT);
	}
	
	/**
	 * 构造排行协议对象
	 * @param builder
	 * @param playerId
	 * @param rank
	 * @param playerName
	 * @param playerLevel
	 * @param playerExp
	 * @param isNPC
	 */
	protected void setParams(HPRankGiftItem.Builder builder, int playerId, int rank, String playerName, int playerLevel, int playerExp, int isNPC) {
		builder.setPlayerId(playerId);
		builder.setRank(rank);
		builder.setName(playerName);
		builder.setLevel(playerLevel);
		builder.setExp(playerExp);
		builder.setIsNPC(isNPC);
	}
	
	/**
	 * 查询出玩家的进行到的地图信息;
	 */
	protected void loadStatusMap() {
		StringBuffer ids = new StringBuffer();
		for (RankGiftPlayerData rankData : expRankPlayerEntity) {
			ids.append(rankData.getPlayerId() + ";");
		}
		String idStr = ids.substring(0, ids.length() - 1);
		List<StateEntity> stateEntitys = HawkDBManager.getInstance().query("from StateEntity where playerId in (" + idStr + ")");
		for (StateEntity stateEntity : stateEntitys) {
			int curMapId = stateEntity.getCurBattleMap();
			if (curMapId <= 0) {
				curMapId = MapCfg.getMinMapId();
			}
			MapCfg mapCfg = HawkConfigManager.getInstance().getConfigByKey(MapCfg.class, curMapId);
			playerMapCfg.put(stateEntity.getPlayerId(), mapCfg);
		}
	}
	
	
	/**
	 * 计算玩家离线经验
	 * @param rankData
	 * @param lastLogoutTime
	 */
	protected void countOfflineExp(RankGiftPlayerData rankData, int lastLogoutTime) {
		int offlineTime = HawkTime.getSeconds() - lastLogoutTime;
		List<MapStatisticsEntity> mapStatisticsEntities = HawkDBManager.getInstance().query("from MapStatisticsEntity where playerId = ? and invalid = 0", rankData.getPlayerId());
		MapStatisticsEntity mapStatisticsEntity = null;
		if (mapStatisticsEntities != null && mapStatisticsEntities.size() > 0) {
			mapStatisticsEntity = mapStatisticsEntities.get(0);
		}
		MapCfg mapCfg = playerMapCfg.get(rankData.getPlayerId());
		if (mapCfg == null) {
			return;
		}
		int fightTimesRate = Math.min(mapStatisticsEntity.getFightTimes(), mapCfg.getMaxFightTimes());
		int canFightTimes = (int) Math.rint((offlineTime / 3600.0f) * fightTimesRate);
		int winTimes = (int) Math.rint(canFightTimes * mapStatisticsEntity.getWinRate() * 0.01f);
		int expAward = (int) Math.rint((offlineTime / 3600.0f) * mapStatisticsEntity.getExpRate());
		if (expAward <= winTimes * mapCfg.getExpDrop()) {
			expAward = winTimes * mapCfg.getExpDrop();
		}
		int exp = rankData.getPlayerExp();
		rankData.setPlayerExp(exp + expAward);
	}
	
	/**
	 * 获得玩家上次离线时间,如果当前在线则返回0;
	 * @param playerId
	 * @return
	 */
	protected int getLogoutTime(int playerId) {
		if (PlayerUtil.queryPlayer(playerId) == null || !PlayerUtil.queryPlayer(playerId).isOnline()) {
			PlayerSnapshotInfo.Builder playerSnapShot = SnapShotManager.getInstance().getPlayerSnapShot(playerId);
			return playerSnapShot.getLastLogoutTime();
		} else {
			return 0;
		}
	}

}
