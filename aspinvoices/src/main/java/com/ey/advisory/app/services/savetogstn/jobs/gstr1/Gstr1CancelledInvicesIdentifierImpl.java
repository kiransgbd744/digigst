package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("gstr1CancelledInvicesIdentifierImpl")
public class Gstr1CancelledInvicesIdentifierImpl
		implements Gstr1CancelledInvicesIdentifier {

	@Autowired
	@Qualifier("gstr1BatchMakerImpl")
	private Gstr1BatchMaker batchMaker;

	@Autowired
	@Qualifier("saveGstr1DataFetcherImpl")
	private SaveGstr1DataFetcher saveGstr1Data;

	@Autowired
	@Qualifier("gstr1SaveInvicesIdentifierImpl")
	private Gstr1SaveInvicesIdentifier saveData;

	@Override
	public Map<String, Map<Long, Long>> findOrgCanInvoicesMap(String gstin,
			String retPeriod, String groupCode, SaveToGstnOprtnType docType,
			List<Long> docIds, ProcessingContext context) {

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
		List<Object[]> docs = saveGstr1Data.findGstr1CancelledData(gstin,
				retPeriod, groupCode, docType.toString(), docIds, context);
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
				if (!Strings.isNullOrEmpty(sec)
						&& sec.equalsIgnoreCase("CDNUR-EXPORTS")) {
					sec = APIConstants.CDNUR;
				}
				Long canId = new Long(doc[2].toString());
				if (docs.indexOf(doc) == 0) {
					prvSec = sec;
					// docIds.add(id);
				}
				if (!isSameSec(sec, prvSec)) {
					map = makeSectionEntryIntoMap(prvSec, canIdsMap, map);
					// docIds = new ArrayList<>();
					canIdsMap = new LinkedHashMap<>();
				}
				// docIds.add(id);
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

			// respList = doCanInvSave(gstin, retPeriod, groupCode, map,
			// respList);
			/*
			 * } }
			 */
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

	/**
	 * Grouping one section invoices as together of One particular Supplier
	 * Gstin and period combination. In order to initiate the section wise Gstn
	 * call by forming the batches.
	 * 
	 * @param section
	 * @param custGstin
	 * @param docIds
	 * @param map
	 * @return
	 */
	private Map<String, Map<Long, Long>> makeSectionEntryIntoMap(String section,
			/* List<Long> docIds */Map<Long, Long> canIdsMap,
			Map<String, Map<Long, Long>> map) {
		// Checking for CR/DR
		/*
		 * if (isCreditDebit(section)) { // Checking for CR/DR
		 * Registered/Unregistered if (isRegisteredCRDR(custGstin)) { //
		 * Registered CR/DR if (section.equals(GSTConstants.CDNA)) {
		 * map.put(APIConstants.CDNRA, docIds); } else {
		 * map.put(APIConstants.CDNR, docIds); } } else { // Unregistered CR/DR
		 * if (section.equals(GSTConstants.CDNA)) { map.put(APIConstants.CDNURA,
		 * docIds); } else { map.put(APIConstants.CDNUR, docIds); } } } else {
		 */
		// Non CR/DR Invoices
		map.put(section, canIdsMap);
		// }
		return map;
	}

	/**
	 * Check for the Same section to group the multiple invoices into one
	 * section.
	 * 
	 * @param sec
	 * @param prvSec
	 * @return
	 */
	private Boolean isSameSec(String sec, String prvSec) {
		return sec != null && sec.equals(prvSec);
	}

	/**
	 * If Customer has Gstin then its a registered invoice(CR/DR) Else it is
	 * UnRegistered Invoice.
	 * 
	 * @param cgstin
	 * @return
	 */
	/*
	 * private Boolean isRegisteredCRDR(String cgstin) { return cgstin != null
	 * && cgstin.trim().length() > 0; }
	 */
	/**
	 * As per Gstr1 Bifurcation rules we are deciding the Hana table data as
	 * CR/DR data or not. This will change if Bifurcation rules gets change.
	 * 
	 * @param sec
	 * @return
	 */
	/*
	 * private Boolean isCreditDebit(String sec) { return sec != null &&
	 * (sec.equals(GSTConstants.CDNA) || sec.equals(GSTConstants.CR) ||
	 * sec.equals(GSTConstants.DR) || sec.equals(GSTConstants.RFV)); }
	 */

	@Override
	public List<SaveToGstnBatchRefIds> findCanInvoices(String gstin,
			String retPeriod, String groupCode,
			Map<String, Map<Long, Long>> map, Long userRequestId,
			String userSelectedSec, List<Long> docIds,
			ProcessingContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("doCanInvSave method with groupcode {}, request {} ",
					groupCode, gstin);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.CDNRA)) {
			if (map.get(GSTConstants.GSTR1_CDNRA.toLowerCase()) != null) {
				List<SaveToGstnBatchRefIds> resp = saveData.findSaveInvoices(
						gstin, retPeriod, groupCode, APIConstants.CDNRA,
						map.get(GSTConstants.GSTR1_CDNRA.toLowerCase()),
						SaveToGstnOprtnType.CAN, userRequestId, docIds,
						context);
				if (resp != null && !resp.isEmpty()) {
					respList.addAll(resp);
				}
			}
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.CDNR)) {
			if (map.get(GSTConstants.CDNR.toLowerCase()) != null) {
				List<SaveToGstnBatchRefIds> resp = saveData.findSaveInvoices(
						gstin, retPeriod, groupCode, APIConstants.CDNR,
						map.get(GSTConstants.CDNR.toLowerCase()),
						SaveToGstnOprtnType.CAN, userRequestId, docIds,
						context);
				if (resp != null && !resp.isEmpty()) {
					respList.addAll(resp);
				}
			}
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.CDNURA)) {
			if (map.get(GSTConstants.GSTR1_CDNURA.toLowerCase()) != null) {
				List<SaveToGstnBatchRefIds> resp = saveData.findSaveInvoices(
						gstin, retPeriod, groupCode, APIConstants.CDNURA,
						map.get(GSTConstants.GSTR1_CDNURA.toLowerCase()),
						SaveToGstnOprtnType.CAN, userRequestId, docIds,
						context);
				if (resp != null && !resp.isEmpty()) {
					respList.addAll(resp);
				}
			}
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.CDNUR)) {
			if (map.get(GSTConstants.CDNUR.toLowerCase()) != null) {
				List<SaveToGstnBatchRefIds> resp = saveData.findSaveInvoices(
						gstin, retPeriod, groupCode, APIConstants.CDNUR,
						map.get(GSTConstants.CDNUR.toLowerCase()),
						SaveToGstnOprtnType.CAN, userRequestId, docIds,
						context);
				if (resp != null && !resp.isEmpty()) {
					respList.addAll(resp);
				}
			}
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.EXPA)) {
			if (map.get(GSTConstants.GSTR1_EXPA.toLowerCase()) != null) {
				List<SaveToGstnBatchRefIds> resp = saveData.findSaveInvoices(
						gstin, retPeriod, groupCode, APIConstants.EXPA,
						map.get(GSTConstants.GSTR1_EXPA.toLowerCase()),
						SaveToGstnOprtnType.CAN, userRequestId, docIds,
						context);
				if (resp != null && !resp.isEmpty()) {
					respList.addAll(resp);
				}
			}
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.EXP)) {
			if (map.get(GSTConstants.GSTR1_EXP.toLowerCase()) != null) {
				List<SaveToGstnBatchRefIds> resp = saveData.findSaveInvoices(
						gstin, retPeriod, groupCode, APIConstants.EXP,
						map.get(GSTConstants.GSTR1_EXP.toLowerCase()),
						SaveToGstnOprtnType.CAN, userRequestId, docIds,
						context);
				if (resp != null && !resp.isEmpty()) {
					respList.addAll(resp);
				}
			}
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.B2CLA)) {
			if (map.get(GSTConstants.GSTR1_B2CLA.toLowerCase()) != null) {
				List<SaveToGstnBatchRefIds> resp = saveData.findSaveInvoices(
						gstin, retPeriod, groupCode, APIConstants.B2CLA,
						map.get(GSTConstants.GSTR1_B2CLA.toLowerCase()),
						SaveToGstnOprtnType.CAN, userRequestId, docIds,
						context);
				if (resp != null && !resp.isEmpty()) {
					respList.addAll(resp);
				}
			}
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.B2CL)) {
			if (map.get(GSTConstants.GSTR1_B2CL.toLowerCase()) != null) {
				List<SaveToGstnBatchRefIds> resp = saveData.findSaveInvoices(
						gstin, retPeriod, groupCode, APIConstants.B2CL,
						map.get(GSTConstants.GSTR1_B2CL.toLowerCase()),
						SaveToGstnOprtnType.CAN, userRequestId, docIds,
						context);
				if (resp != null && !resp.isEmpty()) {
					respList.addAll(resp);
				}
			}
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.B2BA)) {
			if (map.get(GSTConstants.GSTR1_B2BA.toLowerCase()) != null) {
				List<SaveToGstnBatchRefIds> resp = saveData.findSaveInvoices(
						gstin, retPeriod, groupCode, APIConstants.B2BA,
						map.get(GSTConstants.GSTR1_B2BA.toLowerCase()),
						SaveToGstnOprtnType.CAN, userRequestId, docIds,
						context);
				if (resp != null && !resp.isEmpty()) {
					respList.addAll(resp);
				}
			}
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.B2B)) {
			if (map.get(GSTConstants.GSTR1_B2B.toLowerCase()) != null) {
				List<SaveToGstnBatchRefIds> resp = saveData.findSaveInvoices(
						gstin, retPeriod, groupCode, APIConstants.B2B,
						map.get(GSTConstants.GSTR1_B2B.toLowerCase()),
						SaveToGstnOprtnType.CAN, userRequestId, docIds,
						context);
				if (resp != null && !resp.isEmpty()) {
					respList.addAll(resp);
				}
			}
		}
		return respList;
	}

}
