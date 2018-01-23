/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.settings.refreshstrategy.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a simple HashMap based timeout cache for settings caching. Settings caching was not converted
 * to use Ehcache along with the other SimpleTimeout caches because of the significant complexity to do so.
 *
 * An item is removed from cache upon a call to <tt>get()</tt> if the item has
 * reached the timeout set.  <tt>null</tt> cannot sensibly be cached.
 *
 * NOTE: Settings caching was not converted to use the Ehcache based SimpleTimeoutCache
 * because of dependency issues with Cortex bundles.
 * 
 * @param <K> the type used as a key into the cache.
 * @param <V> the type to actually cache.
 */
@SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
public class IntervalTimeoutCache<K, V> {

	private final Map<K, ExpiringCacheObject<V>> cache = new HashMap<>();
	private long timeout;
	
	/**
	 * Creates an instance of <tt>TimeLimitedCache</tt> with the
	 * given <tt>timeout</tt>.
	 *
	 * @param timeout - The time in milliseconds that an item in cache will become stale.
	 */
	public IntervalTimeoutCache(final long timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * Returns the value associated with the specified <tt>key</tt>. Returns
	 * <tt>null</tt> if the cache contains no association for this key or if
	 * the cached item associated with the key has timed out.
	 *
	 * @param key - Key whose associated value will be returned.
	 * @return The value associated with the key if found, or
	 *         <tt>null</tt> if there is no associated value or it is timed out.
	 */
	public synchronized V get(final K key) {
		
		ExpiringCacheObject<V> cacheObject = cache.get(key);
		
		if (cacheObject == null) {
			return null;
		}
		
		if (cacheObject.getValue() == null || needsRefresh(cacheObject)) {
			cache.remove(key);
			return null;
		}
		
		return cacheObject.getValue();
	}
	
	/**
	 * Stores the <tt>key</tt>/<tt>value</tt> association into the cache.
	 *
	 * @param key - The identifier to associate the value with in the cache.
	 * @param value - The value to be stored in the cache.
	 */
	public synchronized void put(final K key, final V value) {
		cache.put(key, new ExpiringCacheObject<>(value, getCurrentTimeMillis() + timeout));
	}
	
	/**
	 * Returns true if the cached item associated with the <tt>key</tt>
	 * has timed out and needs to be removed from the cache.
	 *
	 * @param cacheObject - The key associated with the item to check whether it needs to be refreshed.
	 * @return <tt>true</tt> if the cached item associated with the <tt>key</tt> needs to be removed from cache, or
	 *         <tt>false</tt> otherwise.
	 */
	private boolean needsRefresh(final ExpiringCacheObject<V> cacheObject) {
		return getCurrentTimeMillis() >= cacheObject.getExpiryTime();
	}
	
	/**
	 * Returns the current time in milliseconds. This method exists to make testing easier.
	 *
	 * @return Current time in milliseconds.
	 */
	long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * Set the expiry milliseconds for cache entries added after this call - current 
	 * entry's expiry timeout will be unaffected (it was determined when the entry was
	 * added).
	 *
	 * @param cacheTimeoutMillis the number of milliseconds after which new cache entries
	 *        will be expired from the cache.
	 */
	public synchronized void setTimeout(final long cacheTimeoutMillis) {
		this.timeout = cacheTimeoutMillis;
	}
	
	/**
	 * Clears the cache.
	 */
	public synchronized void clear() {
		this.cache.clear();
	}
	
	/**
	 * Simple object to put in cache that groups a timeout and the object 
	 * to actual cache.
	 *
	 * @param <T> the type of the object to cache.
	 */
	private static class ExpiringCacheObject<T> {
		
		private final long expiryTime;
		private final T value;

		/**
		 * 
		 * @param value the actual value to cache
		 * @param expiryTime the time when the value should be considered 'expired'.
		 */
		ExpiringCacheObject(final T value, final long expiryTime) {
			this.value = value;
			this.expiryTime = expiryTime;
		}
		
		public T getValue() {
			return value;
		}
		
		public long getExpiryTime() {
			return expiryTime;
		}
	}
}
