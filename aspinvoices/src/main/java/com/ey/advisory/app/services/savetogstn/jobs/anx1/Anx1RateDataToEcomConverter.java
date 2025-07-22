package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1EcomData;
import com.ey.advisory.app.docs.dto.anx1.SaveAnx1;
import com.ey.advisory.common.GSTConstants;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("anx1RateDataToEcomConverter")
public class Anx1RateDataToEcomConverter implements RateDataToAnx1Converter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1RateDataToEcomConverter.class);

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
				List<Anx1EcomData> ecomList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				for (int counter = 0; counter < totSize; counter++) {
					Object[] arr1 = objects.get(counter);
					// Reading next object[] for the forming the json.
					//Object[] arr2 = objects.get(counter);
					int counter2 = counter + 1;
					/**
					 * Reading the next doc if exist.
					 */
					/*if (counter2 < totSize) {
						arr2 = objects.get(counter2);
					}*/
					/**
					 * Assuming id IS NOTNULL(primary key)
					 */
					Long id = new Long(String.valueOf(arr1[0]));
					/**
					 * This ids are used to update the Gstr1_doc_header
					 * table as a single/same batch.
					 */
					idsList.add(id);
					Anx1EcomData ecom = setInvData(arr1, taxDocType);
					ecomList.add(ecom);
					if (isNewBatch(idsList, totSize, counter2)) {
						
						SaveAnx1 anx1 = setBatch(arr1, section, ecomList);
						LOGGER.debug("New {} Batch is formed {}", section, anx1);
						batchesList.add(anx1);
						batchIdsList.add(idsList);
						
						idsList = new ArrayList<>();
						ecomList = new ArrayList<>();
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

	/**
	 * Assuming the invoices are coming at eTin level
	 * @param arr1
	 * @param taxDocType
	 * @return
	 */
	private Anx1EcomData setInvData(Object[] arr1, String taxDocType) {

		Long id = new Long(String.valueOf(arr1[0]));
		String eTin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		BigDecimal madeSup = arr1[4] != null ? new BigDecimal(String.valueOf(arr1[4])) : null;
		BigDecimal retSup = arr1[5] != null ? new BigDecimal(String.valueOf(arr1[5])) : null;
		BigDecimal netSup = arr1[6] != null ? new BigDecimal(String.valueOf(arr1[6])) : null;
		BigDecimal igst = arr1[7] != null ? new BigDecimal(String.valueOf(arr1[7])) : null;
		BigDecimal cgst = arr1[8] != null ? new BigDecimal(String.valueOf(arr1[8])) : null;
		BigDecimal sgst = arr1[9] != null ? new BigDecimal(String.valueOf(arr1[9])) : null;
		BigDecimal cess = arr1[10] != null ? new BigDecimal(String.valueOf(arr1[10])) : null;
		
		
		Anx1EcomData inv = new Anx1EcomData();
		if (taxDocType != null && GSTConstants.CAN.equals(taxDocType)) {
			// D-Delete, A-Accept, R-Reject
		}
		inv.setEtin(eTin);
		inv.setCessAmount(cess);
		inv.setCgstAmount(cgst);
		inv.setIgstAmount(igst);
		inv.setSgstAmount(sgst);
		inv.setMadeSupVal(madeSup);
		inv.setRetSupVal(retSup);
		inv.setNetSupval(netSup);

		/**
		 *  An Extra field used in the process of chunking.
		 */
		inv.setDocId(id);
		return inv;
	}

	/**
	 * Assuming the invoices are coming at Gstin and TaxPeriod level.
	 * @param arr1
	 * @param section
	 * @param misList
	 * @return
	 */
	private SaveAnx1 setBatch(Object[] arr1, String section,
			List<Anx1EcomData> misList) {

		String txPriod = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		String sGstin = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		LOGGER.info("New Anx1 {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveAnx1 anx1 = new SaveAnx1();
		anx1.setSgstin(sGstin);
		anx1.setTaxperiod(txPriod);
		anx1.setEcomInvoice(misList);
		return anx1;
	}

	private boolean isNewBatch(List<Long> idsList,
			int totSize, int counter2) {
		
		//Summary section should go as single batch becoz for every summary 
		//section save GSTN will Override the Data at Sandbox.
		return  /*idsList.size() >= chunkSizeFetcher.getSize()
				||*/ counter2 == totSize;
	}
}
