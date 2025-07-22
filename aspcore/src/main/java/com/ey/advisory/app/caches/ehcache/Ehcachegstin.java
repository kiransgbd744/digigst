package com.ey.advisory.app.caches.ehcache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component("Ehcachegstin")
public class Ehcachegstin {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Cacheable(cacheResolver = "cacheResolver", key = "#groupCode + #gstin",
			unless = "#result == null")
	public GSTNDetailEntity getGstinInfo(String groupCode, String gstin) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Not Cached About to get from DB,Gstin = {}, groupCode= {}",
					gstin, groupCode);
		}
		return gstinInfoRepository.findByGstinAndIsDeleteFalse(gstin);
	}

}
