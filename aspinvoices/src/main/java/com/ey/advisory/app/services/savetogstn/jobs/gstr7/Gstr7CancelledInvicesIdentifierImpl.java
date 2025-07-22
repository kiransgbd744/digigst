package com.ey.advisory.app.services.savetogstn.jobs.gstr7;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
 * @author SriBhavya
 *
 */

@Slf4j
@Service("Gstr7CancelledInvicesIdentifierImpl")
public class Gstr7CancelledInvicesIdentifierImpl
		implements Gstr7CancelledInvicesIdentifier {

	@Autowired
	@Qualifier("Gstr7SaveInvicesIdentifierImpl")
	private Gstr7SaveInvicesIdentifier saveData;

	@Autowired
	@Qualifier("SaveGstr7DataFetcherImpl")
	private SaveGstr7DataFetcher saveGstr7Data;

	@Autowired
	@Qualifier("Gstr7BatchMakerImpl")
	private Gstr7BatchMaker gstr7BatchMaker;

	@Transactional(value = "clientTransactionManager")
	@Override
	public List<SaveToGstnBatchRefIds> findGstr7CanInvoices(String gstin,
			String retPeriod, String groupCode, String section,
			SaveToGstnOprtnType oprtnType, Long userRequestId,
			ProcessingContext gstr7context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} findSaveInvoices method with args {}{}",
					section, gstin, groupCode);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		List<Object[]> docs = null;

		docs = saveGstr7Data.findGstr7CancelledData(gstin, retPeriod, groupCode,
				section, oprtnType, userRequestId, gstr7context);

		if (docs != null && !docs.isEmpty()) {
			respList = gstr7BatchMaker.saveGstr7Data(groupCode, section, docs,
					oprtnType, userRequestId, gstr7context);
		} else {
			String msg = "Zero Docs found to do {} Save to Gstn with args {}";
			LOGGER.warn(msg, section, gstin);
			LOGGER.error(msg, section, gstin);
		}
		return respList;

	}

	@Override
	public Map<String, Map<Long, Long>> findOrgCanInvoicesMap(String gstin,
			String retPeriod, String groupCode, SaveToGstnOprtnType oprtnType,
			Long userRequestId, ProcessingContext gstr7context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_CAN_INV_START,
				PerfamanceEventConstants.Gstr1CancelledInvicesIdentifierImpl,
				PerfamanceEventConstants.findCanInvoices,
				PerfamanceEventConstants.GSTIN_Ret_Period.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat("}"));

		Map<String, Map<Long, Long>> map = new LinkedHashMap<>();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("findCanInvoices method with groupcode {} and "
					+ "request {}", groupCode, gstin);
		}
		List<Object[]> docs = saveGstr7Data.findGstr7CancelledData(gstin,
				retPeriod, groupCode, groupCode, oprtnType, userRequestId,
				gstr7context);
		if (docs != null && !docs.isEmpty()) {
			String prvSec = null;
			Map<Long, Long> canIdsMap = new LinkedHashMap<>();
			PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
					PerfamanceEventConstants.FIND_CAN_INV_SEC_MAP_CREATE_START,
					PerfamanceEventConstants.Gstr1CancelledInvicesIdentifierImpl,
					PerfamanceEventConstants.findCanInvoices,
					PerfamanceEventConstants.For_loop_size.concat(":{")
							.concat(String.valueOf(docs.size())).concat("}"));
			for (Object[] doc : docs) {
				Long id = new Long(doc[0].toString());
				String sec = doc[1].toString().toLowerCase();
				Long canId = new Long(doc[2].toString());
				if (docs.indexOf(doc) == 0) {
					prvSec = sec;
				}
				if (!isSameSec(sec, prvSec)) {
					map = makeSectionEntryIntoMap(prvSec, canIdsMap, map);
					canIdsMap = new LinkedHashMap<>();
				}
				canIdsMap.put(id, canId);
				prvSec = sec;
				if (docs.indexOf(doc) == docs.size() - 1) {
					map = makeSectionEntryIntoMap(prvSec, canIdsMap, map);
				}
			}
			PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
					PerfamanceEventConstants.FIND_CAN_INV_SEC_MAP_CREATE_END,
					PerfamanceEventConstants.Gstr1CancelledInvicesIdentifierImpl,
					PerfamanceEventConstants.findCanInvoices,
					PerfamanceEventConstants.For_loop_size.concat(":{")
							.concat(String.valueOf(docs.size())).concat("}"));

		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_CAN_INV_END,
				PerfamanceEventConstants.Gstr1CancelledInvicesIdentifierImpl,
				PerfamanceEventConstants.findCanInvoices,
				PerfamanceEventConstants.GSTIN_Ret_Period.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat("}"));
		return map;
	}

	private Map<String, Map<Long, Long>> makeSectionEntryIntoMap(String section,
			Map<Long, Long> canIdsMap, Map<String, Map<Long, Long>> map) {
		map.put(section, canIdsMap);
		return map;
	}

	private Boolean isSameSec(String sec, String prvSec) {
		return sec != null && sec.equals(prvSec);
	}

	@Override
	public List<SaveToGstnBatchRefIds> findGstr7TransCanInvoices(String gstin,
			String retPeriod, String groupCode,
			Map<String, Map<Long, Long>> map, Long userRequestId,
			ProcessingContext gstr7context) {

		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();

		List<SaveToGstnBatchRefIds> tdsResp = saveData.findGstr7SaveInvoices(
				gstin, retPeriod, groupCode, APIConstants.TDS, null,
				SaveToGstnOprtnType.CAN, userRequestId, gstr7context,
				map.get(APIConstants.TDS));
		if (tdsResp != null && !tdsResp.isEmpty()) {
			respList.addAll(tdsResp);
		}
		List<SaveToGstnBatchRefIds> tdsaResp = saveData.findGstr7SaveInvoices(
				gstin, retPeriod, groupCode, APIConstants.TDSA, null,
				SaveToGstnOprtnType.CAN, userRequestId, gstr7context,
				map.get(APIConstants.TDSA));
		if (tdsaResp != null && !tdsaResp.isEmpty()) {
			respList.addAll(tdsaResp);
		}

		return respList;
	}

}
