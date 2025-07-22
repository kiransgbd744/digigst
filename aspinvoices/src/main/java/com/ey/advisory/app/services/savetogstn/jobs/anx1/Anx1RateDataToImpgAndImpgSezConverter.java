package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1ImpgAndImpgSezBillEntryDetails;
import com.ey.advisory.app.docs.dto.anx1.Anx1ImpgAndImpgSezData;
import com.ey.advisory.app.docs.dto.anx1.Anx1ImpgAndImpgSezDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1ImpgAndImpgSezItemDetails;
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
@Service("anx1RateDataToImpgAndImpgSezConverter")
@Slf4j
public class Anx1RateDataToImpgAndImpgSezConverter
		implements RateDataToAnx1Converter {

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
				List<Anx1ImpgAndImpgSezData> expList = new ArrayList<>();
				List<Anx1ImpgAndImpgSezDocumentData> invList = new ArrayList<>();
				List<Anx1ImpgAndImpgSezItemDetails> itmsList = new ArrayList<>();
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
					Anx1ImpgAndImpgSezItemDetails itms = setItemDetail(arr1,
							section);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2,
							section)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						Anx1ImpgAndImpgSezDocumentData inv = setInvData(arr1,
								itmsList, taxDocType, section);
						invList.add(inv);
						itmsList = new ArrayList<>();
					} else {
						continue;
					}
					if (APIConstants.IMPGSEZ.equals(section) && isNewCtin(arr1,
							arr2, idsList, totSize, counter2)) {
						Anx1ImpgAndImpgSezData exp = setInv(arr1, section, invList);
						expList.add(exp);
						itmsList = new ArrayList<>();
						invList = new ArrayList<>();
					}
					if (isNewBatch(idsList, totSize, counter2)) {

						if (APIConstants.IMPG.equals(section)) {
							Anx1ImpgAndImpgSezData exp = setInv(arr1, section, invList);
							expList.add(exp);
						}

						SaveAnx1 anx1 = setBatch(arr1, section, expList);
						LOGGER.debug("New {} Batch is formed {}", section, anx1);
						batchesList.add(anx1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						expList = new ArrayList<>();
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

	private Anx1ImpgAndImpgSezItemDetails setItemDetail(Object[] arr1,
			String section) {

		BigDecimal cessAmt = arr1[15] != null ? new BigDecimal(String.valueOf(arr1[15])) : BigDecimal.ZERO;
		BigDecimal igstAmt = arr1[14] != null ? new BigDecimal(String.valueOf(arr1[14])) : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[13] != null ? new BigDecimal(String.valueOf(arr1[13])) : null;
		BigDecimal taxRate = arr1[12] != null ? new BigDecimal(String.valueOf(arr1[12])) : null;
		String hsnsac = arr1[11] != null ? String.valueOf(arr1[11]) : null;
		Anx1ImpgAndImpgSezItemDetails item = new Anx1ImpgAndImpgSezItemDetails();

		item.setCessAmount(cessAmt);
		item.setIgstAmount(igstAmt);
		item.setTaxableValue(taxableVal);
		item.setRate(taxRate);
		item.setHsn(hsnsac);
		return item;
	}

	private Anx1ImpgAndImpgSezDocumentData setInvData(Object[] arr1,
			List<Anx1ImpgAndImpgSezItemDetails> itmsList, String taxDocType, String section) {

		/**
		 * Assuming id IS NOTNULL(primary key)
		 */
		Long id = new Long(String.valueOf(arr1[0]));
		//String docDate = String.valueOf(arr1[4]);
		String docDate = null;
		if (arr1[4] != null && arr1[4].toString().trim().length() > 0) {
			docDate = DateUtil.parseObjToDate(arr1[4]).format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		String portCode = arr1[5] != null ? String.valueOf(arr1[5]) : null;
		BigDecimal docAmt = arr1[6] != null ? new BigDecimal(String.valueOf(arr1[6])) : null;
		String docType = arr1[7] != null ? String.valueOf(arr1[7]) : null;
		String billNum = arr1[8] != null ? String.valueOf(arr1[8]) : null;	
		String rfndel = arr1[9] != null ? String.valueOf(arr1[9]) : APIConstants.N;
		String pos = arr1[10] != null ? String.valueOf(arr1[10]) : null;

		Anx1ImpgAndImpgSezBillEntryDetails billOfEntry = 
				new Anx1ImpgAndImpgSezBillEntryDetails();

		billOfEntry.setPortCode(portCode);
		billOfEntry.setBillDate(docDate);
		billOfEntry.setBillNum(billNum);
		billOfEntry.setBillValue(docAmt);
		Anx1ImpgAndImpgSezDocumentData inv = 
				new Anx1ImpgAndImpgSezDocumentData();

		if (taxDocType != null && GSTConstants.CAN.equals(taxDocType)) {
			inv.setFlag(APIConstants.D); // D-Delete, A-Accept, R-Reject
		}

		//Comments are for testing purpose (Chinna)
		if (docType != null) {
			inv.setDoctyp(GSTConstants.B);
			/*if (GSTConstants.INV.equalsIgnoreCase(docType)) {
				 inv.setDoctyp(GSTConstants.I);
			}
			if (GSTConstants.CR.equalsIgnoreCase(docType)) {
				inv.setDoctyp(GSTConstants.C);
			}
			if (GSTConstants.DR.equalsIgnoreCase(docType)) {
				inv.setDoctyp(GSTConstants.D);
			}*/
		}
		if (APIConstants.IMPGSEZ.equals(section)) {
			inv.setImpgSezPos(pos);
			inv.setRfndelg(rfndel);
		}
		
		inv.setBoe(billOfEntry);
		inv.setItems(itmsList);
		
		/**
		 *  An Extra field used in the process of chunking.
		 */
		inv.setDocId(id);
		return inv;
	}

	private Anx1ImpgAndImpgSezData setInv(Object[] arr1, String section,
			List<Anx1ImpgAndImpgSezDocumentData> invList) {

		String pos = arr1[10] != null ? String.valueOf(arr1[10]) : null;
		String sGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		Anx1ImpgAndImpgSezData b2b = new Anx1ImpgAndImpgSezData();
		if (APIConstants.IMPGSEZ.equals(section)) {
			b2b.setCgstin(sGstin);
		} else {
			b2b.setImpgPos(pos);
		}
		b2b.setDocs(invList);
		return b2b;
	}

	private SaveAnx1 setBatch(Object[] arr1, String section,
			List<Anx1ImpgAndImpgSezData> expList) {

		String txPriod = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		String cGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		LOGGER.info("New Anx1 {} Batch with SGSTN {} and TaxPeriod {}", section,
				cGstin, txPriod);
		SaveAnx1 anx1 = new SaveAnx1();
		anx1.setSgstin(cGstin);
		anx1.setTaxperiod(txPriod);
		if (APIConstants.IMPG.equals(section)) {
			anx1.setImpgInvoice(expList);
		} else if (APIConstants.IMPGSEZ.equals(section)) {
			anx1.setImpgsezInvoice(expList);
		}
		return anx1;
	}

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2, String section) {
		/**
		 * Assuming id IS NOTNULL(primary key)
		 */
		Long id = new Long(String.valueOf(arr1[0]));
		Long id2 = new Long(String.valueOf(arr2[0]));
		if (APIConstants.IMPGSEZ.equals(section)) {
			return !id.equals(id2)
					|| isNewCtin(arr1, arr2, idsList, totSize, counter2);
		} else {
			return !id.equals(id2)
					|| isNewBatch(idsList, totSize, counter2);
		}
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {
		String sGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		String sGstin2 =  arr2[3] != null ? String.valueOf(arr2[3]) : null;
		return sGstin != null && !sGstin.equals(sGstin2)
				|| isNewBatch(idsList, totSize, counter2);
	}

	private boolean isNewBatch(List<Long> idsList,
			int totSize, int counter2) {
		return idsList.size() >= chunkSizeFetcher.getSize()
				|| counter2 == totSize;
	}
}
