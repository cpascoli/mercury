package com.blockchain.mercury.util;

import java.util.LinkedHashMap;
import java.util.Map;


public class LRUCache<K, V> extends LinkedHashMap<K, V> {


	private static final long serialVersionUID = -4520438433217885184L;
	private final int cacheSize;
	
	public LRUCache(int cacheSize) {
		super(cacheSize, 0.75f, true);
		this.cacheSize = cacheSize;
	}

	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return  size() >= cacheSize;
	}
}