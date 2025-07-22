package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1MisData;
import com.ey.advisory.app.docs.dto.anx1.Anx1MisDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1MisDocumentDetails;
import com.ey.advisory.app.docs.dto.anx1.Anx1MisItemDetails;
import com.ey.advisory.app.docs.dto.anx1.SaveAnx1;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("anx1RateDataToMisConverter")
public class Anx1RateDataToMisConverter implements RateDataToAnx1Converter {

	/**
	 * This method is responsible to convert the list of documents as batches 
	 * (Json Objects) with Maximum size is less than 5MB.
	 * Assuming this method will receive only one ReturnPeriod and SupplierGstin 
	 * combination documents at once.
	 */
	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;
	
	@Override
	public SaveBatchProcessDto convertToAnx1Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType) {
		LOGGER.debug("convertToAnx1Object with section {} and type {}", 
				section, taxDocType);
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveAnx1> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				LOGGER.debug("Batch seperation is started with {} docs", totSize);
				List<Anx1MisData> misList = new ArrayList<>();
				List<Anx1MisDocumentData> invList = new ArrayList<>();
				List<Anx1MisItemDetails> itmsList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				for (int counter = 0; counter < totSize; counter++) {
					Object[] arr1 = objects.get(counter);
					// Reading next object[] for the forming the json.
					Object[] arr2 = objects.get(counter);
					int counter2 = counter + 1;
					/**
					 * Reading the next doc if exist.
					 */
					if (counter2 < totSize) {
						arr2 = objects.get(counter2);
					}
					/**
					 * Assuming id IS NOTNULL(primary key)
					 */
					Long id = new Long(String.valueOf(arr1[0]));
					Anx1MisItemDetails itms = setItemDetail(arr1);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						Anx1MisDocumentData inv = setInvData(arr1,
								itmsList, taxDocType);
						invList.add(inv);
						itmsList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewCtin(arr1, arr2, idsList, totSize, counter2)) {
						Anx1MisData b2b = setInv(arr1, invList);
						misList.add(b2b);
						itmsList = new ArrayList<>();
						invList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewBatch(idsList, totSize, counter2)) {
						SaveAnx1 anx1 = setBatch(arr1, section, misList);
						LOGGER.debug("New {} Batch is formed {}", section, anx1);
						batchesList.add(anx1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						misList = new ArrayList<>();
						invList = new ArrayList<>();
						itmsList = new ArrayList<>();
					}
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn";
				LOGGER.debug(msg, objects);
			}

		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
		}
		batchDto.setAnx1(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
	}

	private Anx1MisItemDetails setItemDetail(Object[] arr1) {

		BigDecimal cessAmt = arr1[18] != null ? new BigDecimal(String.valueOf(arr1[18])) : BigDecimal.ZERO;
		BigDecimal sgstAmt = arr1[17] != null ? new BigDecimal(String.valueOf(arr1[17])) : BigDecimal.ZERO;
		BigDecimal cgstAmt = arr1[16] != null ? new BigDecimal(String.valueOf(arr1[16])) : BigDecimal.ZERO;
		BigDecimal igstAmt = arr1[15] != null ? new BigDecimal(String.valueOf(arr1[15])) : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[14] != null ? new BigDecimal(String.valueOf(arr1[14])) : null;
		BigDecimal taxRate = arr1[13] != null ? new BigDecimal(String.valueOf(arr1[13])) : null;
		String hsnsac = arr1[12] != null ? String.valueOf(arr1[12]) : null;
		Anx1MisItemDetails item = new Anx1MisItemDetails();

		/*item.setCessAmount(cessAmt);
		item.setSgstAmount(sgstAmt);
		item.setCgstAmount(cgstAmt);
		item.setIgstAmount(igstAmt);*/
		
		String sGstin = String.valueOf(arr1[3]);
		String sec7 = arr1[9] != null ? String.valueOf(arr1[9]) : APIConstants.N;
		String pos = arr1[11] != null ? String.valueOf(arr1[11]) : null;
		if (APIConstants.Y.equalsIgnoreCase(sec7) || (sGstin != null
				&& !sGstin.substring(0, 2).equalsIgnoreCase(pos)
				|| igstAmt.compareTo(BigDecimal.ZERO) > 0)) {
			item.setIgstAmount(igstAmt);
		} else {
			item.setSgstAmount(sgstAmt);
			item.setCgstAmount(cgstAmt);
		}
		item.setCessAmount(cessAmt);
		item.setTaxableValue(taxableVal);
		item.setRate(taxRate);
		item.setHsn(hsnsac);
		return item;
	}

	private Anx1MisDocumentData setInvData(Object[] arr1,
			List<Anx1MisItemDetails> itmsList, String taxDocType) {

		Long id = new Long(String.valueOf(arr1[0]));
		//String docDate = arr1[4] != null ? String.valueOf(arr1[4]) : null;
		String docDate = null;
		if (arr1[4] != null && arr1[4].toString().trim().length() > 0) {
			docDate = DateUtil.parseObjToDate(arr1[4]).format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		String docNum = arr1[5] != null ? String.valueOf(arr1[5]) : null;
		BigDecimal docAmt = arr1[6] != null ? new BigDecimal(String.valueOf(arr1[6])) : null ;
		String docType = arr1[7] != null ? String.valueOf(arr1[7]) : null;
		String difPrcnt = arr1[8] != null ? String.valueOf(arr1[8]) : null;
		String sec7 = arr1[9] != null ? String.valueOf(arr1[9]) : APIConstants.N;
		String clmrfnd = arr1[10] != null ? String.valueOf(arr1[10]) : APIConstants.N;
		String pos = arr1[11] != null ? String.valueOf(arr1[11]) : null;

		Anx1MisDocumentDetails invDetail = new Anx1MisDocumentDetails();
		invDetail.setDocDate(docDate);
		invDetail.setDocNum(docNum);
		invDetail.setDocVal(docAmt);
		Anx1MisDocumentData inv = new Anx1MisDocumentData();
		
		if(taxDocType != null && GSTConstants.CAN.equals(taxDocType)){
		 //D-Delete, A-Accept, R-Reject
		}
		inv.setPos(pos);
		if (!APIConstants.N.equalsIgnoreCase(difPrcnt)) {
			inv.setDiffprcnt(new BigDecimal("0.65"));
		}
		inv.setDoctyp(docType);
		inv.setClmrfnd(clmrfnd);
		inv.setSec7act(sec7);
		inv.setDoc(invDetail);
		inv.setItems(itmsList);
		/**
		 *  An Extra field used in the process of chunking.
		 */
		inv.setDocId(id);
		return inv;
	}

	private Anx1MisData setInv(Object[] arr1, List<Anx1MisDocumentData> invList) {

		String sGstin = String.valueOf(arr1[3]);
		Anx1MisData mis = new Anx1MisData();
		mis.setCgstin(sGstin);
		mis.setTblref(null);
		mis.setDocs(invList);
		return mis;
	}

	private SaveAnx1 setBatch(Object[] arr1, String section,
			List<Anx1MisData> misList) {

		String txPriod = String.valueOf(arr1[2]);
		String cGstin = String.valueOf(arr1[1]);
		LOGGER.info("New Anx1 {} Batch with SGSTN {} and TaxPeriod {}", section,
				cGstin, txPriod);
		SaveAnx1 anx1 = new SaveAnx1();
		anx1.setSgstin(cGstin);
		anx1.setTaxperiod(txPriod);
		anx1.setMisInvoice(misList);
		return anx1;
	}

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2) {
		/**
		 * Assuming id IS NOTNULL(primary key)
		 */
		Long id = new Long(String.valueOf(arr1[0]));
		Long id2 = new Long(String.valueOf(arr2[0]));
		return !id.equals(id2)
				|| isNewCtin(arr1, arr2, idsList, totSize, counter2);
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {
		String sGstin = String.valueOf(arr1[3]);
		String sGstin2 = String.valueOf(arr2[3]);
		return sGstin != null && !sGstin.equals(sGstin2)
				|| isNewBatch(idsList, totSize, counter2);
	}

	private boolean isNewBatch(List<Long> idsList,
			int totSize, int counter2) {
		
		return idsList.size() >= chunkSizeFetcher.getSize()
				|| counter2 == totSize;
	}

}
