/**
 * 
 */
package com.ey.advisory.app.caches.ehcache;

import javax.cache.Caching;

import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * 
 * @author Siva.Nandam
 *
 */
@Configuration
@EnableCaching
public class CachingConfig  {
	
	 	@Bean
	    public javax.cache.CacheManager cacheManager() {
	 		org.ehcache.jsr107.EhcacheCachingProvider provider = 
	 				(EhcacheCachingProvider) Caching.getCachingProvider();
		    return provider.getCacheManager(); 
	    }
	 	
	 	@Bean(name = "jCacheManager")
	 	public JCacheCacheManager  jCacheManager() {
	 		return new JCacheCacheManager(cacheManager());
	 	}
	 	
	 	
	 	
	 	
}
