package com.ey.advisory.app.services.savetogstn.jobs.gstr7;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sri Bhavya
 *
 */
@Service("Gstr7SaveInvicesIdentifierImpl")
@Slf4j
public class Gstr7SaveInvicesIdentifierImpl
		implements Gstr7SaveInvicesIdentifier {

	@Autowired
	@Qualifier("Gstr7SaveDataFetcherImpl")
	private Gstr7SaveDataFetcher saveGstr7Data;

	@Autowired
	@Qualifier("Gstr7BatchMakerImpl")
	private Gstr7BatchMaker gstr7BatchMaker;

	@Transactional(value = "clientTransactionManager")
	@Override
	public List<SaveToGstnBatchRefIds> findGstr7SaveInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			List<Long> docIds, SaveToGstnOprtnType operationType,
			Long userRequestId, ProcessingContext gstr7context,
			Map<Long, Long> orgCanIdsMap) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} findSaveInvoices method with args {}{}",
					section, gstin, groupCode);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		List<Object[]> docs = null;

		boolean isTransactional = (boolean) gstr7context
				.getAttribute(APIConstants.TRANSACTIONAL);
		if (isTransactional) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} Invoice level Data Identifier", section);
			}
			docs = saveGstr7Data.findGstr7TransInvoiceLevelData(gstin,
					retPeriod, groupCode, orgCanIdsMap, section, gstr7context);
		} else {
			docs = saveGstr7Data.findInvoiceLevelData(gstin, retPeriod,
					groupCode, section, userRequestId, gstr7context);
		}

		if (docs != null && !docs.isEmpty()) {
			respList = gstr7BatchMaker.saveGstr7Data(groupCode, section, docs,
					operationType, userRequestId, gstr7context);
		} else {
			String msg = "Zero Docs found to do {} Save to Gstn with args {}";
			LOGGER.warn(msg, section, gstin);
			LOGGER.error(msg, section, gstin);
		}
		return respList;
	}

}
