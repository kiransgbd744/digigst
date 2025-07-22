/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.anx2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx2SaveInvicesIdentifierImpl")
@Slf4j
public class Anx2SaveInvicesIdentifierImpl
		implements Anx2SaveInvicesIdentifier {

	
	@Autowired
	@Qualifier("SaveAnx2DataFetcherImpl")
	private SaveAnx2DataFetcher saveAnx2Data;

	@Autowired
	@Qualifier("Anx2BatchMakerImpl")
	private Anx2BatchMaker anx2BatchMaker;

	@Override
	public List<SaveToGstnBatchRefIds> findAnx2SaveInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			List<Long> docIds, SaveToGstnOprtnType operationType) {
		if(LOGGER.isDebugEnabled()) {
		LOGGER.debug("Executing {} findSaveInvoices method with args {}{}",
				section, gstin, groupCode);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		/**
		 * Get all the newly processed plus not sent to gstn documents that are
		 * present in the user selected dates.
		 */
		List<Object[]> docs = null;
		docs = saveAnx2Data.findInvoiceLevelData(gstin, retPeriod, groupCode,
				section, docIds);
		
		//This does the actual Gstr1 SaveToGstn operation for a given section 
		//by forming the Json structure as per government published API.
		if (docs != null && !docs.isEmpty()) {
			respList = anx2BatchMaker.saveAnx2Data(groupCode, section, docs,
					operationType);
		} else {
			String msg = "Zero Docs found to do {} Save to Gstn with args {}";
			LOGGER.warn(msg, section, gstin);
			LOGGER.error(msg, section, gstin);
		}
		return respList;
	}

}
