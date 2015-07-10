package com.zoll.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Automatic sorting Map; <br>
 * a proxy of HashMap<Integer, V>;
 * 
 * @author qianhang
 * 
 * @param <V>
 */
public class AutoSortRank<V extends Comparable<V>> {
	private final HashMap<Integer, V> host = new HashMap<Integer, V>();
	private int MAX_RANK = 0;

	/**
	 * must set the max size of rank;
	 * 
	 * @param maxRank
	 */
	public AutoSortRank(int maxRank) {
		this.MAX_RANK = maxRank;
	}

	/**
	 * hide the put(K, V);
	 * 
	 * @param rank
	 * @param value
	 * @return
	 */
	private V put(Integer rank, V value) {
		V v = host.put(rank, value);
		// �����г��ȴ�������ʱ, �Ƴ����һ��
		if ((host.size() > MAX_RANK) && MAX_RANK != 0) {
			host.remove(host.size());
		}
		return v;
	}

	/**
	 * just put V into rank, it will be sorted;
	 * 
	 * @param value
	 * @return
	 */
	public V put(V value) {
		Collection<V> allVs = host.values();
		for (V eachV : allVs) {
			if (eachV.compareTo(value) > 0) {
				Integer key = getKey(this, eachV);
				return insertV(key, value);
			}
		}
		return put(host.size() + 1, value);
	}

	/**
	 * insert a V on one position;
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	private V insertV(Integer key, V value) {
		Set<Integer> o = host.keySet();
		for (Integer i = o.size() + 1; i > key; i--) {
			put(i, host.get(i - 1));
		}
		return put(key, value);
	}

	/**
	 * get the K by the V;
	 * 
	 * @param rank
	 * @param v
	 * @return
	 */
	private Integer getKey(AutoSortRank<V> rank, V v) {
		Iterator<Integer> it = rank.keySet().iterator();
		while (it.hasNext()) {
			Integer key = (Integer) it.next();
			if (rank.get(key).equals(v)) {
				return key;
			}
		}
		return null;
	}

	public V get(Integer key) {
		return host.get(key);
	}

	private Set<Integer> keySet() {
		return host.keySet();
	}

	/**
	 * remove one rankObj on this position;
	 * 
	 * @param key
	 */
	public void remove(Integer key) {
		if (key > host.size() || key <= 0) {
			throw new RuntimeException("out of rank size:" + host.size() + " key=" + key);
		}
		for (int i = key; i < host.size(); i++) {
			host.put(i, host.get(i + 1));
		}
		host.remove(host.size());
	}

	@Override
	public String toString() {
		return host.toString();
	}
}