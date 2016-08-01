package com.mbv.mca.checkout.service;

import net.spy.memcached.MemcachedClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("memcachedService")
public class MemcachedService {

	// Memcached
	@Autowired
	@Qualifier("memcachedClient")
	MemcachedClient cache;

	@Value("${memcached_expiration}")
	int exp;

	/*
	 * 
	 */
	public void set(String key, Object o) {
		cache.set(key, exp, o);
	}

	/*
	 * 
	 */
	public void set(String key, int exp, Object o){
		cache.set(key, exp, o);
	}
	
	/*
	 * 
	 */
	public Object get(String key) {
		return cache.get(key);
	}
}
