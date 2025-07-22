package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("anx1SaveInvicesIdentifierImpl")
@Slf4j
public class Anx1SaveInvicesIdentifierImpl
		implements Anx1SaveInvicesIdentifier {

	@Autowired
	@Qualifier("saveAnx1DataFetcherImpl")
	private SaveAnx1DataFetcher saveAnx1Data;

	@Autowired
	@Qualifier("anx1BatchMakerImpl")
	private Anx1BatchMaker batchMaker;

	@Override
	public List<SaveToGstnBatchRefIds> findAnx1SaveInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			List<Long> docIds, SaveToGstnOprtnType operationType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Executing {} findSaveInvoices method with args {} and {}",
					section, gstin, groupCode);
		}
		List<SaveToGstnBatchRefIds> respList = null;
		/**
		 * Get all the newly processed plus not sent to gstn documents that are
		 * present in the user selected dates.
		 */
		List<Object[]> docs = null;
		if (isHorizontal(section)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("section {} is Harizontal Data ", section);
			}
			docs = saveAnx1Data.findAnx1InvoiceLevelData(gstin, retPeriod,
					groupCode, section, docIds);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("section {} is Vertical Data ", section);
			}
			docs = saveAnx1Data.findAnx1SummaryData(gstin, retPeriod, groupCode,
					section);
		}
		/*docs = saveAnx1Data.findAnx1Data(jsonReq, groupCode,
				section, docIds);*/

		if (docs != null && !docs.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} docs found ", docs.size());
			}
			respList = batchMaker.saveAnx1Data(groupCode, section, docs,
					operationType);
		} else {
			if (LOGGER.isDebugEnabled()) {
				String msg = "No Docs found to do {} Anx1 SaveToGstn with args {}";
				LOGGER.debug(msg, section, gstin);
			}
		}
		return respList;
	}

	private Boolean isHorizontal(String section) {

		return !(APIConstants.B2C.equals(section)
				|| APIConstants.REV.equals(section)
				|| APIConstants.IMPS.equals(section)
				|| APIConstants.ECOM.equals(section));
	}
}
