package com.ey.advisory.app.data.services.savetogstn.gstr8;

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
 * @author Siva.Reddy
 *
 */
@Service("Gstr8SaveInvicesIdentifierImpl")
@Slf4j
public class Gstr8SaveInvicesIdentifierImpl
		implements Gstr8SaveInvicesIdentifier {

	@Autowired
	@Qualifier("Gstr8SaveDataFetcherImpl")
	private Gstr8SaveDataFetcher saveGstr8Data;

	@Autowired
	@Qualifier("Gstr8BatchMakerImpl")
	private Gstr8BatchMaker gstr8BatchMaker;

	@Transactional(value = "clientTransactionManager")
	@Override
	public List<SaveToGstnBatchRefIds> findGstr8SaveInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			List<Long> docIds, SaveToGstnOprtnType operationType,
			Long userRequestId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} findSaveInvoices method with args {}{}",
					section, gstin, groupCode);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		List<Object[]> docs = null;
		docs = saveGstr8Data.findInvoiceLevelData(gstin, retPeriod, groupCode,
				section, userRequestId);

		if (docs != null && !docs.isEmpty()) {
			respList = gstr8BatchMaker.saveGstr8Data(groupCode, section, docs,
					operationType, userRequestId);
		} else {
			String msg = "Zero Docs found to do {} Save to Gstn with args {}";
			LOGGER.warn(msg, section, gstin);
			LOGGER.error(msg, section, gstin);
		}
		return respList;
	}

	@Override
	public List<SaveToGstnBatchRefIds> findGstr8CanInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			SaveToGstnOprtnType oprtnType, Long userRequestId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} findSaveInvoices method with args {}{}",
					section, gstin, groupCode);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		List<Object[]> docs = saveGstr8Data.findGstr8CancelledData(gstin,
				retPeriod, groupCode, section, oprtnType, userRequestId);

		if (docs != null && !docs.isEmpty()) {
			respList = gstr8BatchMaker.saveGstr8Data(groupCode, section, docs,
					oprtnType, userRequestId);
		} else {
			String msg = "Zero Docs found to do {} Save to Gstn with args {}";
			LOGGER.warn(msg, section, gstin);
			LOGGER.error(msg, section, gstin);
		}
		return respList;

	}

}
