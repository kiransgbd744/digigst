package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("gstr1SaveInvicesIdentifierImpl")
public class Gstr1SaveInvicesIdentifierImpl
		implements Gstr1SaveInvicesIdentifier {

	@Autowired
	@Qualifier("saveGstr1DataFetcherImpl")
	private SaveGstr1DataFetcher saveGstr1Data;

	@Autowired
	@Qualifier("gstr1BatchMakerImpl")
	private Gstr1BatchMaker gstr1BatchMaker;

	@Transactional(value = "clientTransactionManager")
	@Override
	public List<SaveToGstnBatchRefIds> findSaveInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			Map<Long, Long> orgCanIdsMap, SaveToGstnOprtnType operationType,
			Long userRequestId, List<Long> docIds, ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_SAV_INV_START,
				PerfamanceEventConstants.Gstr1SaveInvicesIdentifierImpl,
				PerfamanceEventConstants.findSaveInvoices,
				PerfamanceEventConstants.GSTIN_Ret_Period_section.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat(section).concat("}"));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} findSaveInvoices method with args {}{}",
					section, gstin, groupCode);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		/**
		 * Get all the newly processed plus not sent to gstn documents that are
		 * present in the user selected dates.
		 */
		List<Object[]> docs = null;
		String origin = APIConstants.GSTR1.toUpperCase();
		if (docIds != null && !docIds.isEmpty()) {
			origin = APIConstants.INVMGMT;
		}

		if (isHorizontal(section)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} Invoice level Data Identifier", section);
			}
			docs = saveGstr1Data.findGstr1InvoiceLevelData(gstin, retPeriod,
					groupCode, section, orgCanIdsMap,
					(operationType != null && operationType.name()
							.equalsIgnoreCase(SaveToGstnOprtnType.CAN.name()))
									? null : docIds, context);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} Summary level Data Identifier", section);
			}
			docs = saveGstr1Data.findGstr1SummaryData(gstin, retPeriod,
					groupCode, section, userRequestId, context);
		}
		// This does the actual Gstr1 SaveToGstn operation for a given section
		// by forming the Json structure as per government published API.
		if (docs != null && !docs.isEmpty()) {
			respList = gstr1BatchMaker.saveGstr1Data(groupCode, section, docs,
					operationType, orgCanIdsMap, 0l, userRequestId, origin, context);
		} else {
			String msg = "Zero Docs found to do {} Save to Gstn with args {}";
			LOGGER.warn(msg, section, gstin);
			LOGGER.error(msg, section, gstin);
		}

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_SAV_INV_END,
				PerfamanceEventConstants.Gstr1SaveInvicesIdentifierImpl,
				PerfamanceEventConstants.findSaveInvoices,
				PerfamanceEventConstants.GSTIN_Ret_Period_section.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat(section).concat("}"));
		return respList;
	}

	private Boolean isHorizontal(String section) {

		return (APIConstants.B2B.equals(section)
				|| APIConstants.B2BA.equals(section)
				|| APIConstants.B2CL.equals(section)
				|| APIConstants.B2CLA.equals(section)
				|| APIConstants.EXP.equals(section)
				|| APIConstants.EXPA.equals(section)
				|| APIConstants.CDNR.equals(section)
				|| APIConstants.CDNRA.equals(section)
				|| APIConstants.CDNUR.equals(section)
				|| APIConstants.CDNURA.equals(section)
				|| APIConstants.ECOMSUP.equals(section));
	}

}
