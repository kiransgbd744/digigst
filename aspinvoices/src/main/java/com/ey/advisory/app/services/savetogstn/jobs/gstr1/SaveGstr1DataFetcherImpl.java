package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("saveGstr1DataFetcherImpl")
public class SaveGstr1DataFetcherImpl implements SaveGstr1DataFetcher {

	private static final String LOG_GROUPCODE_MSG = "groupCode {} is set";

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	
	@Autowired
	private GstnUserRequestRepository gstnUserRequestRepo;

	@Override
	public List<Object[]> findGstr1CancelledData(String gstin, String retPeriod,
			String groupCode, String docType, List<Long> docIds, ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_CAN_DATA_START,
				PerfamanceEventConstants.SaveGstr1DataFetcherImpl,
				PerfamanceEventConstants.findGstr1CancelledData,
				PerfamanceEventConstants.GSTIN_Ret_Period.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat("}"));

		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);

		List<Object[]> resultset = docRepository.findGstr1CancelledData(gstin,
				retPeriod, docType, docIds, context);

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_CAN_DATA_END,
				PerfamanceEventConstants.SaveGstr1DataFetcherImpl,
				PerfamanceEventConstants.findGstr1CancelledData,
				PerfamanceEventConstants.GSTIN_Ret_Period.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat("}"));
		return resultset;
	}

	@Override
	public List<Object[]> findGstr1InvoiceLevelData(String gstin,
			String retPeriod, String groupCode, String docType,
			Map<Long, Long> orgCanIdsMap, List<Long> docIds, ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_INV_DATA_START,
				PerfamanceEventConstants.SaveGstr1DataFetcherImpl,
				PerfamanceEventConstants.findGstr1InvoiceLevelData,
				PerfamanceEventConstants.GSTIN_Ret_Period.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat("}"));

		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);
		List<Object[]> resultset = docRepository.findGstr1InvoiceLevelData(
				gstin, retPeriod, docType,
				orgCanIdsMap != null ? orgCanIdsMap.keySet() : null, docIds, context);

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_INV_DATA_END,
				PerfamanceEventConstants.SaveGstr1DataFetcherImpl,
				PerfamanceEventConstants.findGstr1InvoiceLevelData,
				PerfamanceEventConstants.GSTIN_Ret_Period.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat("}"));
		return resultset;

	}

	@Override
	public List<Object[]> findGstr1SummaryData(String gstin, String retPeriod,
			String groupCode, String docType, Long userRequestId, ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_SUMM_DATA_PROC_START,
				PerfamanceEventConstants.SaveGstr1DataFetcherImpl,
				PerfamanceEventConstants.findGstr1SummaryData,
				PerfamanceEventConstants.GSTIN_Ret_Period.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat("}"));

		TenantContext.setTenantId(groupCode);
		LOGGER.info(LOG_GROUPCODE_MSG, groupCode);

		// Assuming this proc will update the selected rows of both
		// vertical and Horizontal tables BATCH_ID to temp value Zero.
		if (!APIConstants.DOCISS.equals(docType)) {
			boolean isNilUserInput = false , isHsnUserInput = false;
			Optional<GstnUserRequestEntity> optionalEntity = 
					gstnUserRequestRepo.findById(userRequestId);
			if (optionalEntity.isPresent()) {
				GstnUserRequestEntity gstnUserRequestEntity = optionalEntity.get();
				if(gstnUserRequestEntity != null) {
				isNilUserInput = gstnUserRequestEntity.isNilUserInput();
				isHsnUserInput = gstnUserRequestEntity.isHsnUserInput();
				}
			}
			
			//to be saved data fetch logic proc call
			docRepository.saveGstr1ProcCall(gstin, retPeriod, docType, groupCode, userRequestId,
					GenUtil.convertTaxPeriodToInt(retPeriod), isNilUserInput, isHsnUserInput, context);
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_SUMM_DATA_PROC_END,
				PerfamanceEventConstants.SaveGstr1DataFetcherImpl,
				PerfamanceEventConstants.findGstr1SummaryData,
				PerfamanceEventConstants.GSTIN_Ret_Period.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat("}"));

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_SUMM_DATA_START,
				PerfamanceEventConstants.SaveGstr1DataFetcherImpl,
				PerfamanceEventConstants.findGstr1SummaryData,
				PerfamanceEventConstants.GSTIN_Ret_Period.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat("}"));
		
		//to be saved data fetch logic 
		List<Object[]> resultset = docRepository
				.findGstr1SummaryLevelData(gstin, retPeriod, docType, context);

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_SUMM_DATA_END,
				PerfamanceEventConstants.SaveGstr1DataFetcherImpl,
				PerfamanceEventConstants.findGstr1SummaryData,
				PerfamanceEventConstants.GSTIN_Ret_Period.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat("}"));
		return resultset;

	}

}
