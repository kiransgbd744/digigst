package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sri Bhavya
 *
 */
@Service("Gstr6CanDataFetcherImpl")
@Slf4j
public class Gstr6CanDataFetcherImpl implements Gstr6CanDataFetcher{
	
	private static final String LOG_GROUPCODE_MSG = "groupCode {} is set";

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Override
	public List<Object[]> findInvoiceLevelData(String gstin, String retPeriod, String groupCode, String section,String isdDocType) {
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		return docRepository.findGstr6CanInvLevelData(gstin, retPeriod, section,isdDocType);
	}

}
