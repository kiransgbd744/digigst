/**
 * 
 */
package com.ey.advisory.app.services.validation.sales;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ravindra V S
 *
 */
@Component("cacheResolverGstr1")
@Slf4j
public class CustomCacheResolverGstr1 implements CacheResolver {

	@Autowired
	@Qualifier("jCacheManager")
	private JCacheCacheManager cacheManager;

	@Override
	public Collection<Cache> resolveCaches(
			CacheOperationInvocationContext<?> context) {
		Collection<Cache> caches = new ArrayList<>();
		Object[] args = context.getArgs();
		String groupCode = args[4].toString();
		String cacheName = "GSTIN_RETURN_STATUS_" + groupCode;
		javax.cache.CacheManager jxCacheManager = cacheManager
				.getCacheManager();
		javax.cache.Cache c1 = jxCacheManager.getCache(cacheName, String.class,
				GstrReturnStatusEntity.class);
		if (c1 == null) {
			caches.add(getNewCache(cacheName));
			return caches;
		} else {
			caches.add(new JCacheCache(c1));
			return caches;
		}
	}

	private Cache getNewCache(String cacheName) {
		javax.cache.CacheManager jCacheManager = cacheManager.getCacheManager();

		// New configuration
		CacheConfiguration<String, GstrReturnStatusEntity> cacheConfiguration 
		= CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class,
						GstrReturnStatusEntity.class, ResourcePoolsBuilder.heap(250))
				.withExpiry(ExpiryPolicyBuilder
						.timeToLiveExpiration(java.time.Duration.ofMinutes(60)))
				.build();
		javax.cache.Cache cache = jCacheManager.createCache(cacheName,
				Eh107Configuration
						.fromEhcacheCacheConfiguration(cacheConfiguration));
		return new JCacheCache(cache);

	}

	public void evictAllCaches() {
		javax.cache.CacheManager jxCacheManager = cacheManager
				.getCacheManager();
		Iterable<String> cacheNames = jxCacheManager.getCacheNames();
		List<String> cachenames1 = StreamSupport
				.stream(cacheNames.spliterator(), false)
				.collect(Collectors.toList());
		cachenames1.parallelStream()
				.forEach(o -> cacheManager.getCache(o).clear());
	}

	public void evictCachesByName(String groupCode, String cacheString) {
		String cacheName = cacheString + groupCode;
		cacheManager.getCache(cacheName).clear();
	}
}
