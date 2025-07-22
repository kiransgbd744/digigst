package com.ey.advisory.app.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EhcacheGstinTaxperiod")
public class EhcacheGstinTaxperiod {

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Cacheable(cacheResolver = "cacheResolverGstr1", key = "#gstin + #taxPeriod + #returnType + #status + #groupCode", unless = "#result == null")
	public GstrReturnStatusEntity isGstinFiled(String gstin, String taxPeriod,
			String returnType, String status, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Not Cached About to get from DB,Gstin = {}, taxPeriod= {}",
					gstin, taxPeriod);
		}

		return gstrReturnStatusRepository
				.findByGstinAndTaxPeriodAndReturnTypeAndStatusIgnoreCaseAndIsCounterPartyGstinFalse(
						gstin, taxPeriod, returnType, status);
	}

}
