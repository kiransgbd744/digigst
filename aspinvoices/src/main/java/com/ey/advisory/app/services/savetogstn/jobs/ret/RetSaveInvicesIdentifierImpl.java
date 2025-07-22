/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.ret;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("RetSaveInvicesIdentifierImpl")
@Slf4j
public class RetSaveInvicesIdentifierImpl implements RetSaveInvicesIdentifier {

	@Autowired
	@Qualifier("SaveRetDataFetcherImpl")
	private SaveRetDataFetcher saveRetData;

	@Autowired
	@Qualifier("RetBatchMakerImpl")
	private RetBatchMaker batchMaker;

	@Override
	public List<SaveToGstnBatchRefIds> findRetSaveInvoices(String gstin,
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
		/*if (isHorizontal(section)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("section {} is Harizontal Data ", section);
			}
			docs = saveRetData.findRetInvoiceLevelData(gstin, retPeriod, groupCode,
					section, docIds);
		} else {*/
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("section {} is Vertical Data ", section);
			}
			docs = saveRetData.findRetSummaryData(gstin, retPeriod, groupCode, section);
		/*}*/

		if (docs != null && !docs.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} docs found ", docs.size());
			}
			respList = batchMaker.saveRetData(groupCode, section, docs,
					operationType);
		} else {
			if (LOGGER.isDebugEnabled()) {
				String msg = "No Docs found to do {} Ret SaveToGstn with args {}";
				LOGGER.debug(msg, section, gstin);
			}
		}
		return respList;
	}

	/*private Boolean isHorizontal(String section) {

		return !(APIConstants.B2C.equals(section)
				|| APIConstants.REV.equals(section)
				|| APIConstants.IMPS.equals(section)
				|| APIConstants.ECOM.equals(section));
	}*/
}
