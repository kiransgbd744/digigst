package com.ey.advisory.app.services.savetogstn.jobs.gstr7;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sri Bhavya
 *
 */
@Service("Gstr7SaveDataFetcherImpl")
@Slf4j
public class Gstr7SaveDataFetcherImpl implements Gstr7SaveDataFetcher {

	private static final String LOG_GROUPCODE_MSG = "groupCode {} is set";

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Override
	public List<Object[]> findInvoiceLevelData(String gstin, String retPeriod,
			String groupCode, String docType, Long userRequestId,
			ProcessingContext gstr7context) {
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);

		boolean isTransactional = (boolean) gstr7context
				.getAttribute(APIConstants.TRANSACTIONAL);
		List<Object[]> resultset = null;
		// procedure call
		if (isTransactional) {

			resultset = docRepository.findGstr7InvLevelData(gstin, retPeriod,
					docType);
		} else {
			docRepository.saveGstr7ProcCall(gstin, retPeriod, docType,
					groupCode, userRequestId,
					GenUtil.convertTaxPeriodToInt(retPeriod));
			resultset = docRepository.findGstr7InvLevelData(gstin, retPeriod,
					docType);
		}
		return resultset;
	}

	@Override
	public List<Object[]> findGstr7TransInvoiceLevelData(String gstin,
			String retPeriod, String groupCode, Map<Long, Long> orgCanIdsMap,
			String docType, ProcessingContext context) {

		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		List<Object[]> resultset = docRepository.findGstr7TransInvLevelData(
				gstin, retPeriod, docType,
				orgCanIdsMap != null ? orgCanIdsMap.keySet() : null, context);

		return resultset;

	}

}
