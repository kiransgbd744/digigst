package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.repositories.client.Gstr1GSTINDeleteDataRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1vsEinvReconRespProcessedRepository;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Gstr1DeleteInvicesIdentifier")
public class Gstr1DeleteInvicesIdentifier {

	@Autowired
	@Qualifier("gstr1BatchMakerImpl")
	private Gstr1BatchMaker gstr1BatchMaker;
	
	@Autowired
	private Gstr1GSTINDeleteDataRepository autoDraftRepo;

	@Autowired
	private Gstr1vsEinvReconRespProcessedRepository deleteRespRepo;
	
	@Transactional(value = "clientTransactionManager")
	public List<SaveToGstnBatchRefIds> findSaveInvoices(String gstin, String retPeriod,
			String groupCode, String section, Map<Long, Long> orgCanIdsMap,
			SaveToGstnOprtnType operationType, Long userRequestId, ProcessingContext context) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} findSaveInvoices method with args {}{}",
					section, gstin, groupCode);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		/**
		 * Get all the newly processed plus not sent to gstn documents that are
		 * present in the user selected dates.
		 */
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("{} Invoice level Data Identifier", section);
			}
			List<Object[]> docs = findGstr1InvoiceLevelData(gstin, retPeriod,
					 section, orgCanIdsMap != null ? orgCanIdsMap.keySet() : null,
							 operationType.toString());
		
		//This does the actual Gstr1 SaveToGstn operation for a given section 
		//by forming the Json structure as per government published API.
		if (docs != null && !docs.isEmpty()) {
			respList = gstr1BatchMaker.deleteGstr1Data(groupCode, section, docs,
					operationType, orgCanIdsMap, 0l, userRequestId, context);
		} else {
			String msg = "Zero Docs found to do {} Save to Gstn with args {}";
			LOGGER.warn(msg, section, gstin);
			LOGGER.error(msg, section, gstin);
		}
		
		
		return respList;
	}

	public List<Object[]> findGstr1InvoiceLevelData(String gstin,
			String retPeriod, String section, Set<Long> orgDocIds, String opType) {

		List<String> tableType = new ArrayList<>();
		if (APIConstants.B2B.equalsIgnoreCase(section)) {
			tableType.add(section.toUpperCase());
		} else if (APIConstants.EXP.equalsIgnoreCase(section)) {
			tableType.add(GSTConstants.EXPT.toUpperCase());
			tableType.add(GSTConstants.EXPWT.toUpperCase());
			//tableType.add(GSTConstants.GSTR1_EXP.toUpperCase());
		} else if (APIConstants.CDNR.equalsIgnoreCase(section)) {
			tableType.add(section.toUpperCase());
		} else if (APIConstants.CDNUR.equalsIgnoreCase(section)) {
			tableType.add(GSTConstants.CDNUR_EXPT.toUpperCase());
			tableType.add(GSTConstants.CDNUR_EXPWT.toUpperCase());
			//tableType.add(GSTConstants.CDNUR_EXPORTS.toUpperCase());
		}

		if(APIConstants.DELETE.equalsIgnoreCase(opType)) {
			// DELETE FILE UPLOAD
		return autoDraftRepo.findDocsByGstinAndRetPeriodAndTableTypes(gstin, retPeriod,
				tableType);
		} else {
			// DELETE RESPONSE DATA
			return deleteRespRepo.findDocsByGstinAndRetPeriodAndTableTypes(gstin, retPeriod,
				tableType);
		}
	}


}
