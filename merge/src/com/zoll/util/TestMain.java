package com.zoll.util;

public class TestMain {

	private static final int MAX_RANK = 6;

	public static void main(String[] args) {
		ASRank<Integer> map = new ASRank<Integer>(MAX_RANK);
		map.put(11);
		map.put(44);
		map.put(33);
		map.put(55);
		map.put(22);
		map.put(11);
		map.put(66);
		map.put(44);
		map.put(88);
		System.out.println(map);
		
		ASRank<RankObj> map1 = new ASRank<RankObj>(12);
		map1.put(new RankObj(123));
		map1.put(new RankObj(33));
		map1.put(new RankObj(12));
		map1.put(new RankObj(523));
		map1.put(new RankObj(313));
		map1.put(new RankObj(55));
		map1.put(new RankObj(5));
		map1.put(new RankObj(515));
		map1.put(new RankObj(41));
		map1.put(new RankObj(15));
		map1.put(new RankObj(221));
		map1.put(new RankObj(1));
		map1.put(new RankObj(612));
		System.out.println(map1);
		
		map1.remove(10);
		
		System.out.println(map1);
	}
}

class RankObj implements Comparable<RankObj>{
	int id;
	
	public RankObj(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "[id:" + id + "]";
	}

	@Override
	public int compareTo(RankObj o) {
		return o.getId() - this.id;
	}
	
}