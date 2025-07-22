/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.ret;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
@Service("RetCancelledInvicesIdentifierImpl")
@Slf4j
public class RetCancelledInvicesIdentifierImpl
		implements RetCancelledInvicesIdentifier {

	@Autowired
	@Qualifier("RetBatchMakerImpl")
	private RetBatchMaker batchMaker;

	@Autowired
	@Qualifier("SaveRetDataFetcherImpl")
	private SaveRetDataFetcher saveRetData;

	@Autowired
	@Qualifier("RetSaveInvicesIdentifierImpl")
	private RetSaveInvicesIdentifier saveData;

	@Override
	public List<SaveToGstnBatchRefIds> findCanInvoices(String gstin,
			String retPeriod, String groupCode, SaveToGstnOprtnType docType) {
		if(LOGGER.isDebugEnabled()) {
		LOGGER.debug("findCanInvoices method with groupcode {} and "
				+ "request {}",groupCode, gstin);
		}
		List<Object[]> docs = saveRetData.findRetCancelledData(gstin,
				retPeriod, groupCode, docType.toString());
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		if (docs != null && !docs.isEmpty()) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} CAN Invoices found",docs.size());
			}
			/*for (List<Object[]> listOfDoc : docs) {
				if (listOfDoc != null && !listOfDoc.isEmpty()) {*/
					String prvSec = null;
					List<Long> docIds = new ArrayList<>();
					Map<String, List<Long>> map = new LinkedHashMap<>();
					for (Object[] doc : docs) {
						Long id = new Long(doc[0].toString());
						String sec = doc[1].toString();
						if (docs.indexOf(doc) == 0) {
							prvSec = sec;
							//docIds.add(id);
						}
						if (!isSameSec(sec, prvSec)) {
							map = makeSectionEntryIntoMap(prvSec, docIds, map);
							docIds = new ArrayList<>();
						}
						docIds.add(id);
						prvSec = sec;
						if (docs.indexOf(doc) == docs.size() - 1) {
							map = makeSectionEntryIntoMap(prvSec, docIds, map);
						}
					}
					respList = doCanInvSave(gstin, retPeriod, groupCode, map, respList);
			/*	}
			}*/
		} else {
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
		map.put(section, docIds);
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

	private List<SaveToGstnBatchRefIds> doCanInvSave(String gstin,
			String retPeriod,
			String groupCode, Map<String, List<Long>> map,
			List<SaveToGstnBatchRefIds> respList) {
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("doCanInvSave method with groupcode {}, request {} and "
					+ "BatchRefIds {} ",groupCode, gstin, respList);
			}
		
		map.keySet().forEach(tableAsKey -> {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Processing {} section {} data.", tableAsKey,
						SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findRetSaveInvoices(
					gstin, retPeriod, groupCode, tableAsKey, map.get(tableAsKey),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}

		});
		
		
		
		
		/*if (map.get(APIConstants.ECOM) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.ECOM, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
					groupCode, APIConstants.ECOM, map.get(APIConstants.ECOM),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.MIS) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.MIS, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
					groupCode, APIConstants.MIS, map.get(APIConstants.MIS),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.IMPGSEZ) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.IMPGSEZ, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
					groupCode, APIConstants.IMPGSEZ,
					map.get(APIConstants.IMPGSEZ), SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.IMPG) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.IMPG, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
					groupCode, APIConstants.IMPG, map.get(APIConstants.IMPG),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.IMPS) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.IMPS, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
					groupCode, APIConstants.IMPS, map.get(APIConstants.IMPS),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.REV) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.REV, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
					groupCode, APIConstants.REV, map.get(APIConstants.REV),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.DE) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.DE, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
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
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
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
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
					groupCode, APIConstants.SEZWP, map.get(APIConstants.SEZWP),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.EXPWOP) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.EXPWOP, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
					groupCode, APIConstants.EXPWOP,
					map.get(APIConstants.EXPWOP), SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.EXPWP) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.EXPWP, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
					groupCode, APIConstants.EXPWP, map.get(APIConstants.EXPWP),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.B2B) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.B2B, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
					groupCode, APIConstants.B2B, map.get(APIConstants.B2B),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}
		if (map.get(APIConstants.B2C) != null) {
			if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Processing {} section {} data.", APIConstants.B2C, SaveToGstnOprtnType.CAN);
			}
			List<SaveToGstnBatchRefIds> resp = saveData.findAnx1SaveInvoices(dto,
					groupCode, APIConstants.B2C, map.get(APIConstants.B2C),
					SaveToGstnOprtnType.CAN);
			if (resp != null && !resp.isEmpty()) {
				respList.addAll(resp);
			}
		}*/
		return respList;
	}

}
