/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.anx2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
@Slf4j
@Service("Anx2CancelledInvicesIdentifierImpl")
public class Anx2CancelledInvicesIdentifierImpl
		implements Anx2CancelledInvicesIdentifier {

	@Autowired
	@Qualifier("SaveAnx2DataFetcherImpl")
	private SaveAnx2DataFetcher saveAnx2Data;

	@Autowired
	@Qualifier("Anx2BatchMakerImpl")
	private Anx2BatchMaker anx2BatchMaker;

	@Autowired
	@Qualifier("Anx2SaveInvicesIdentifierImpl")
	private Anx2SaveInvicesIdentifier saveData;

	// This needs to be modified for
	@Override
	public List<SaveToGstnBatchRefIds> findCanInvoices(String gstin, String retPeriod,
			String groupCode, SaveToGstnOprtnType docType) {
		LOGGER.debug("inside findCanInvoices method");
		List<Object[]> docs = saveAnx2Data.findCancelledData(gstin, retPeriod,
				groupCode, docType.toString());
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		/*if (docs != null && !docs.isEmpty()) {
			for (List<Object[]> listOfDoc : docs) {*/
				if (docs != null && !docs.isEmpty()) {
					String prvSec = null;
					List<Long> docIds = new ArrayList<>();
					Map<String, List<Long>> map = new LinkedHashMap<>();
					for (Object[] doc : docs) {
						Long id = new Long(doc[0].toString());
						String sec = doc[1].toString().toLowerCase();
						if (docs.indexOf(doc) == 0) {
							prvSec = sec;
							//docIds.add(id);
						}
						if (!isSameSec(sec, prvSec)) {
							map = makeSectionEntryIntoMap(prvSec, docIds, map);
							//docIds = new ArrayList<>();
						}
						docIds.add(id);
						prvSec = sec;
						if (docs.indexOf(doc) == docs.size() - 1) {
							map = makeSectionEntryIntoMap(prvSec, docIds, map);
						}
					}
			/*		respList = doCanInvSave(jsonReq, groupCode, map, respList);
				}
			}*/
		}  else {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("No CAN Invoices found");
			}
		}
		return respList;
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
	private Map<String, List<Long>> makeSectionEntryIntoMap(String section,
			List<Long> docIds, Map<String, List<Long>> map) {
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
		map.put(section, docIds);
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

	private List<SaveToGstnBatchRefIds> doCanInvSave(String gstin,
			String retPeriod, String groupCode, Map<String, List<Long>> map,
			List<SaveToGstnBatchRefIds> respList) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"doCanInvSave method with groupcode {}, request {} and "
							+ "BatchRefIds {} ",
					groupCode, gstin, respList);
		}

		map.keySet().forEach(tableAsKey -> {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Processing {} section {} data.", tableAsKey,
						SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx2SaveInvoices(
					gstin, retPeriod, groupCode, tableAsKey,
					map.get(tableAsKey), SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}

		});
		
		
		
		
		/*if (map.get(APIConstants.DEA) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.DEA, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx2SaveInvoices(jsonReq,
					groupCode, APIConstants.DEA, map.get(APIConstants.DEA),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.SEZWOPA) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.SEZWOPA, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx2SaveInvoices(jsonReq,
					groupCode, APIConstants.SEZWOPA,
					map.get(APIConstants.SEZWOPA), SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.SEZWPA) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.SEZWPA, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx2SaveInvoices(jsonReq,
					groupCode, APIConstants.SEZWPA, map.get(APIConstants.SEZWPA),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		
		if (map.get(APIConstants.B2BA) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.B2BA, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx2SaveInvoices(jsonReq,
					groupCode, APIConstants.B2BA, map.get(APIConstants.B2BA),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		
		
		if (map.get(APIConstants.DE) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.DE, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx2SaveInvoices(jsonReq,
					groupCode, APIConstants.DE, map.get(APIConstants.DE),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.SEZWOP) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.SEZWOP, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx2SaveInvoices(jsonReq,
					groupCode, APIConstants.SEZWOP,
					map.get(APIConstants.SEZWOP), SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.SEZWP) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.SEZWP, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx2SaveInvoices(jsonReq,
					groupCode, APIConstants.SEZWP, map.get(APIConstants.SEZWP),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		
		if (map.get(APIConstants.B2B) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.B2B, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx2SaveInvoices(jsonReq,
					groupCode, APIConstants.B2B, map.get(APIConstants.B2B),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}*/
		
		return respList;
	}

}
