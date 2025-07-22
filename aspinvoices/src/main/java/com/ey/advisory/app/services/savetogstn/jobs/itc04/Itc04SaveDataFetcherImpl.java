package com.ey.advisory.app.services.savetogstn.jobs.itc04;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Service("Itc04SaveDataFetcherImpl")
@Slf4j
public class Itc04SaveDataFetcherImpl implements Itc04SaveDataFetcher{
	
	private static final String LOG_GROUPCODE_MSG = "groupCode {} is set";

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	
	@Override
	public List<Object[]> findInvoiceLevelData(String gstin, String retPeriod, String groupCode, String docType,
			List<Long> docIds) {
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		return docRepository.findItc04InvLevelData(gstin, retPeriod, docType);
	}

	@Override
	public List<Object[]> findCanInvoiceLevelData(String gstin, String retPeriod, String groupCode, String docType,
			List<Long> docIds) {
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		return docRepository.findItc04CanInvLevelData(gstin, retPeriod, docType);
	}

}
