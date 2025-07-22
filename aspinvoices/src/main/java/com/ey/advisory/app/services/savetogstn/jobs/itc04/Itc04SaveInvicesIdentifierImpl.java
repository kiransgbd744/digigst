package com.ey.advisory.app.services.savetogstn.jobs.itc04;

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
@Service("Itc04SaveInvicesIdentifierImpl")
@Slf4j
public class Itc04SaveInvicesIdentifierImpl implements Itc04SaveInvicesIdentifier{
	
	@Autowired
	@Qualifier("Itc04SaveDataFetcherImpl")
	private Itc04SaveDataFetcher SaveItc04Data;

	@Autowired
	@Qualifier("Itc04BatchMakerImpl")
	private Itc04BatchMaker Itc04BatchMaker;
	
	@Transactional(value = "clientTransactionManager")
	@Override
	public List<SaveToGstnBatchRefIds> findItc04SaveInvoices(String gstin, String retPeriod, String groupCode,
			String section, List<Long> docIds, SaveToGstnOprtnType operationType, Long userRequestId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} findSaveInvoices method with args {}{}",
					section, gstin, groupCode);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		List<Object[]> docs = null;
		docs = SaveItc04Data.findInvoiceLevelData(gstin, retPeriod, groupCode,
				section, docIds);
		if (docs != null && !docs.isEmpty()) {
			respList = Itc04BatchMaker.saveItc04Data(groupCode, section, docs,
					operationType,userRequestId);
		} else {
			String msg = "Zero Docs found to do {} Save to Gstn with args {}";
			LOGGER.warn(msg, section, gstin);
			LOGGER.error(msg, section, gstin);
		}
		return respList;
	}
	
	@Transactional(value = "clientTransactionManager")
	@Override
	public List<SaveToGstnBatchRefIds> findItc04CanInvoices(String gstin, String retPeriod, String groupCode,
			String section, List<Long> docIds, SaveToGstnOprtnType operationType, Long userRequestId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} findCanInvoices method with args {}{}",
					section, gstin, groupCode);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		List<Object[]> docs = null;
		docs = SaveItc04Data.findCanInvoiceLevelData(gstin, retPeriod, groupCode,
				section, docIds);
		if (docs != null && !docs.isEmpty()) {
			respList = Itc04BatchMaker.saveItc04Data(groupCode, section, docs,
					operationType,userRequestId);
		} else {
			String msg = "Zero Docs found to do {} Save to Gstn for CAN with args {}";
			LOGGER.warn(msg, section, gstin);
			LOGGER.error(msg, section, gstin);
		}
		return respList;
	}

}
