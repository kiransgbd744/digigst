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
import com.ey.advisory.core.api.SaveToGstnOprtnType;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@Service("SaveGstr7DataFetcherImpl")
public class SaveGstr7DataFetcherImpl implements SaveGstr7DataFetcher {

	private static final String LOG_GROUPCODE_MSG = "groupCode {} is set";

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Override
	public List<Object[]> findGstr7CancelledData(String gstin, String retPeriod,
			String groupCode, String section, SaveToGstnOprtnType oprtnType,
			Long userRequestId, ProcessingContext gstr7context) {
		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		List<Object[]> resultset = null;
		boolean isTransactional = (boolean) gstr7context
				.getAttribute(APIConstants.TRANSACTIONAL);

		if (isTransactional) {

			resultset = docRepository.findGstr7TransCancelledData(gstin, retPeriod,
					section, gstr7context);
			
		} else {
			docRepository.canGstr7ProcCall(gstin, retPeriod, section, groupCode,
					userRequestId, GenUtil.convertTaxPeriodToInt(retPeriod));

			resultset = docRepository.findGstr7CancelledData(gstin, retPeriod,
					section);
		}
		return resultset;
	}

	@Override
	public List<Object[]> findGstr7TransCancelledData(String gstin,
			String retPeriod, String groupCode, String section,
			SaveToGstnOprtnType oprtnType, Long userRequestId,
			ProcessingContext gstr7context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object[]> findGstr7InvoiceLevelData(String gstin,
			String retPeriod, String groupCode, String docType,
			Map<Long, Long> orgCanIdsMap, List<Long> docIds,
			ProcessingContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
