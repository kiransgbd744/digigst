package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

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
 * @author Sri Bhavya
 *
 */
@Service("Gstr6CanInvicesIdentifierImpl")
@Slf4j
public class Gstr6CanInvicesIdentifierImpl implements Gstr6CanInvicesIdentifier {

	@Autowired
	@Qualifier("Gstr6CanDataFetcherImpl")
	private Gstr6CanDataFetcher SaveGstr6Data;

	@Autowired
	@Qualifier("Gstr6BatchMakerImpl")
	private Gstr6BatchMaker gstr6BatchMaker;
	
	@Transactional(value = "clientTransactionManager")
	@Override
	public List<SaveToGstnBatchRefIds> findGstr6CanInvoices(String gstin, String retPeriod, String groupCode,
			String section, SaveToGstnOprtnType operationType, Long userRequestId, String isdDocType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} findCanInvoices method with args {}{}", section, gstin, groupCode);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		List<Object[]> docs = null;
		docs = SaveGstr6Data.findInvoiceLevelData(gstin, retPeriod, groupCode, section,isdDocType);
		if (docs != null && !docs.isEmpty()) {
			respList = gstr6BatchMaker.saveGstr6Data(groupCode, section, docs, operationType, userRequestId);
		} else {
			String msg = "Zero Docs found to do {} Can Save to Gstn with args {}";
			LOGGER.warn(msg, section, gstin);
			LOGGER.error(msg, section, gstin);
		}
		return respList;

	}

}
