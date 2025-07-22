package com.ey.advisory.app.services.savetogstn.jobs.gstr2x;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Service("Gstr2XSaveInvicesIdentifierImpl")
@Slf4j
public class Gstr2XSaveInvicesIdentifierImpl implements Gstr2XSaveInvicesIdentifier {

	@Autowired
	@Qualifier("Gstr2XSaveDataFetcherImpl")
	private Gstr2XSaveDataFetcher gstr2XSaveDataFetcher;
	
	@Autowired
	@Qualifier("Gstr2XBatchMakerImpl")
	private Gstr2XBatchMaker gstr2XBatchMaker;
	
	@Transactional(value = "clientTransactionManager")
	@Override
	public List<SaveToGstnBatchRefIds> findGstr2XSaveInvoices(String gstin, String retPeriod, String groupCode,
			String section, SaveToGstnOprtnType operationType, Long userRequestId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} findGstr2XSaveInvoices method with args {}{}", section, gstin, groupCode);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		List<Object[]> docs = null;
		docs = gstr2XSaveDataFetcher.findInvoiceLevelData(gstin, retPeriod, groupCode, section);
		if (docs != null && !docs.isEmpty()) {
			respList = gstr2XBatchMaker.saveGstr2XData(groupCode, section, docs, operationType, userRequestId);
		} else {
			String msg = "Zero Docs found to do {} Save to Gstn with args {} in GSTR2X";
			LOGGER.warn(msg, section, gstin);
			LOGGER.error(msg, section, gstin);
		}
		return respList;
	}

}
