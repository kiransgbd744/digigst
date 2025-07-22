package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1SezwpAndSezwopData;
import com.ey.advisory.app.docs.dto.anx1.Anx1SezwpAndSezwopDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1SezwpAndSezwopDocumentDetails;
import com.ey.advisory.app.docs.dto.anx1.Anx1SezwpAndSezwopItemDetails;
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
@Service("anx1RateDataToSezwpAndSezwopConverter")
@Slf4j
public class Anx1RateDataToSezwpAndSezwopConverter
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
				List<Anx1SezwpAndSezwopData> sezList = new ArrayList<>();
				List<Anx1SezwpAndSezwopDocumentData> invList = 
						new ArrayList<>();
				List<Anx1SezwpAndSezwopItemDetails> itmsList = 
						new ArrayList<>();
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
					Anx1SezwpAndSezwopItemDetails itms = setItemDetail(arr1,
							section);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						Anx1SezwpAndSezwopDocumentData inv = setInvData(arr1,
								itmsList, taxDocType, section);
						invList.add(inv);
						itmsList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewCtin(arr1, arr2, idsList, totSize, counter2)) {
						Anx1SezwpAndSezwopData sez = setInv(arr1, invList);
						sezList.add(sez);
						itmsList = new ArrayList<>();
						invList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewBatch(idsList, totSize, counter2)) {
						SaveAnx1 anx1 = setBatch(arr1, section, sezList);
						LOGGER.debug("New {} Batch is formed {}", section, anx1);
						batchesList.add(anx1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						sezList = new ArrayList<>();
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

	private Anx1SezwpAndSezwopItemDetails setItemDetail(Object[] arr1,
			String section) {

		BigDecimal taxableVal = arr1[14] != null ? new BigDecimal(String.valueOf(arr1[14])) : null;
		BigDecimal taxRate = arr1[13] != null ? new BigDecimal(String.valueOf(arr1[13])) : null;
		String hsnsac = arr1[12] != null ? String.valueOf(arr1[12]) : null ;
		Anx1SezwpAndSezwopItemDetails item = new 
				Anx1SezwpAndSezwopItemDetails();
		if (APIConstants.SEZWP.equals(section)) {
			BigDecimal cessAmt = arr1[16] != null ? new BigDecimal(String.valueOf(arr1[16])) : BigDecimal.ZERO;
			BigDecimal igstAmt = arr1[15] != null ? new BigDecimal(String.valueOf(arr1[15])) : BigDecimal.ZERO;
			item.setCessAmount(cessAmt);
			item.setIgstAmount(igstAmt);
		}
		item.setTaxableValue(taxableVal);
		item.setRate(taxRate);
		item.setHsn(hsnsac);
		return item;
	}

	private Anx1SezwpAndSezwopDocumentData setInvData(Object[] arr1,
			List<Anx1SezwpAndSezwopItemDetails> itmsList, String taxDocType,
			String section) {

		long id = Long.parseLong(arr1[0].toString());
		//String docDate = String.valueOf(arr1[4]);
		String docDate = null;
		if (arr1[4] != null && arr1[4].toString().trim().length() > 0) {
			docDate = DateUtil.parseObjToDate(arr1[4]).format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		String docNum = arr1[5] != null ? String.valueOf(arr1[5]) : null;
		BigDecimal docAmt = arr1[6] != null ? new BigDecimal(String.valueOf(arr1[6])) : BigDecimal.ZERO ;
		String docType = arr1[7] != null ? String.valueOf(arr1[7]) : null;
		String difPrcnt = arr1[8] != null ? String.valueOf(arr1[8]) : null;
		String rfndel = arr1[9] != null ? String.valueOf(arr1[9]) : APIConstants.N;
		String clmrfnd = arr1[10] != null ? String.valueOf(arr1[10]) : APIConstants.N;
		String pos = arr1[11] != null ? String.valueOf(arr1[11]) : null;

		Anx1SezwpAndSezwopDocumentDetails invDetail = new 
				Anx1SezwpAndSezwopDocumentDetails();
		invDetail.setDocDate(docDate);
		invDetail.setDocNum(docNum);
		invDetail.setDocVal(docAmt);
		Anx1SezwpAndSezwopDocumentData inv = new 
				Anx1SezwpAndSezwopDocumentData();

		if (taxDocType != null && GSTConstants.CAN.equals(taxDocType)) {
			inv.setFlag(APIConstants.D); // D-Delete, A-Accept, R-Reject

		}
		if (APIConstants.SEZWP.equals(section)) {
			inv.setClmrfnd(clmrfnd);
			if (!APIConstants.N.equalsIgnoreCase(difPrcnt)) {
				inv.setDiffprcnt(new BigDecimal("0.65"));
			}
		}

		inv.setPos(pos);
		
		if (docType != null) {
			if (GSTConstants.INV.equalsIgnoreCase(docType))
				inv.setDoctyp(GSTConstants.I);
			if (GSTConstants.CR.equalsIgnoreCase(docType))
				inv.setDoctyp(GSTConstants.C);
			if (GSTConstants.DR.equalsIgnoreCase(docType))
				inv.setDoctyp(GSTConstants.D);
		}
		inv.setRfndelg(rfndel);
		inv.setDoc(invDetail);
		inv.setItems(itmsList);
		/**
		 * An Extra field used in the process of chunking.
		 */
		inv.setDocId(id);
		return inv;
	}

	private Anx1SezwpAndSezwopData setInv(Object[] arr1,
			List<Anx1SezwpAndSezwopDocumentData> invList) {

		String cGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		Anx1SezwpAndSezwopData b2b = new Anx1SezwpAndSezwopData();
		b2b.setCgstin(cGstin);
		b2b.setDocs(invList);
		return b2b;
	}

	private SaveAnx1 setBatch(Object[] arr1, String section,
			List<Anx1SezwpAndSezwopData> sezList) {

		String txPriod = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		String sGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		LOGGER.info("New Anx1 {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveAnx1 anx1 = new SaveAnx1();
		anx1.setSgstin(sGstin);
		anx1.setTaxperiod(txPriod);
		if (APIConstants.SEZWP.equals(section)) {
			anx1.setSezwpInvoice(sezList);
		} else if (APIConstants.SEZWOP.equals(section)) {
			anx1.setSezwopInvoice(sezList);
		}
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
		String cGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		String cGstin2 = arr2[3] != null ? String.valueOf(arr2[3]) : null;
		return cGstin != null && !cGstin.equals(cGstin2)
				|| isNewBatch(idsList, totSize, counter2);
	}

	private boolean isNewBatch(List<Long> idsList,
			int totSize, int counter2) {
		
		return idsList.size() >= chunkSizeFetcher.getSize()
				|| counter2 == totSize;
	}

}
