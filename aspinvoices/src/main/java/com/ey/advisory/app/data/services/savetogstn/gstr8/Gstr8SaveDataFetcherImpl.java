package com.ey.advisory.app.data.services.savetogstn.gstr8;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.SaveToGstnOprtnType;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Service("Gstr8SaveDataFetcherImpl")
@Slf4j
public class Gstr8SaveDataFetcherImpl implements Gstr8SaveDataFetcher {

	private static final String LOG_GROUPCODE_MSG = "groupCode {} is set";

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Override
	public List<Object[]> findInvoiceLevelData(String gstin, String retPeriod,
			String groupCode, String section, Long userRequestId) {
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		// procedure call
		docRepository.saveGstr8ProcCall(gstin, retPeriod, section, groupCode,
				userRequestId, GenUtil.convertTaxPeriodToInt(retPeriod));
		// Query Call
		List<Object[]> resultset = docRepository.findGstr8SummaryData(gstin,
				retPeriod, section);

		return resultset;
	}
	
	@Override
	public List<Object[]> findGstr8CancelledData(String gstin, String retPeriod, String groupCode, String section,
			SaveToGstnOprtnType oprtnType, Long userRequestId) {
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		docRepository.canGstr8ProcCall(gstin, retPeriod, section, groupCode,
				userRequestId,GenUtil.convertTaxPeriodToInt(retPeriod));
		List<Object[]> resultset = docRepository.findGstr8CanSummaryData(gstin, retPeriod, section);
		return resultset;
	}

}
