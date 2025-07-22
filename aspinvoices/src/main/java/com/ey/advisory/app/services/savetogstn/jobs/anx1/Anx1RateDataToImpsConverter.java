package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1ImpsDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1ImpsItemDetails;
import com.ey.advisory.app.docs.dto.anx1.SaveAnx1;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("anx1RateDataToImpsConverter")
@Slf4j
public class Anx1RateDataToImpsConverter implements RateDataToAnx1Converter {

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
				//List<Anx1ImpsData> impsList = new ArrayList<>();
				List<Anx1ImpsDocumentData> invList = new ArrayList<>();
				List<Anx1ImpsItemDetails> itmsList = new ArrayList<>();
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
					Anx1ImpsItemDetails itms = setItemDetail(arr1);
					itmsList.add(itms);

					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						Anx1ImpsDocumentData inv = setInvData(arr1, itmsList,
								taxDocType);
						invList.add(inv);
						itmsList = new ArrayList<>();
					} else {
						continue;
					}

					if (isNewBatch(idsList, totSize, counter2)) {

						/*Anx1ImpsData imps = new Anx1ImpsData();
						imps.setDocs(invList);
						impsList.add(imps);*/
						SaveAnx1 anx1 = setBatch(arr1, section, invList);
						LOGGER.debug("New {} Batch is formed {}", section, anx1);
						batchesList.add(anx1);
						batchIdsList.add(idsList);
						/**
						 * reset
						 */
						idsList = new ArrayList<>();
					//	impsList = new ArrayList<>();
						itmsList = new ArrayList<>();
					}
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn "
						+ "with arg {} ";
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

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2) {

		/**
		 * Assuming id IS NOTNULL(primary key)
		 */
		/*Long id = new Long(String.valueOf(arr1[0]));
		Long id2 = new Long(String.valueOf(arr2[0]));*/
		
		String rfndelg = arr1[3] != null ? String.valueOf(arr1[3]) : "";
		String pos = arr1[4] != null ? String.valueOf(arr1[4]) : "";

		String rfndelg2 = arr2[3] != null ? String.valueOf(arr2[3]) : "";
		String pos2 = arr2[4] != null ? String.valueOf(arr2[4]) : "";
		
		return /*!id.equals(id2)*/
				!pos.equals(pos2) || !rfndelg.equals(rfndelg2)
				|| isNewBatch(idsList, totSize, counter2);
	}

	private boolean isNewBatch(List<Long> idsList,
			int totSize, int counter2) {
		
		//Summary section should go as single batch becoz for every summary 
		//section save GSTN will Override the Data at Sandbox.
		return /*idsList.size() >= chunkSizeFetcher.getSize()
				||*/ counter2 == totSize;
	}

	private Anx1ImpsItemDetails setItemDetail(Object[] arr1) {

		BigDecimal cess = arr1[9] != null ? new BigDecimal(String.valueOf(arr1[9])) : BigDecimal.ZERO;
		BigDecimal igst = arr1[8] != null ? new BigDecimal(String.valueOf(arr1[8])) : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[7] != null ? new BigDecimal(String.valueOf(arr1[7])) : null;;
		BigDecimal taxRate = arr1[6] != null ? new BigDecimal(String.valueOf(arr1[6])) : null;
		String hsnsac = arr1[5] != null ? String.valueOf(arr1[5]) : null;

		Anx1ImpsItemDetails itms = new Anx1ImpsItemDetails();
		itms.setHsn(hsnsac);
		itms.setRate(taxRate);
		itms.setTaxableValue(taxableVal);
		itms.setIgstAmount(igst);
		itms.setCessAmount(cess);
		return itms;
	}

	private Anx1ImpsDocumentData setInvData(Object[] arr1,
			List<Anx1ImpsItemDetails> itmsList, String taxDocType) {

		Long id = new Long(String.valueOf(arr1[0]));
		String rfndelg = arr1[3] != null ? String.valueOf(arr1[3]) : APIConstants.N;
		String pos = arr1[4] != null ? String.valueOf(arr1[4]) : null;

		Anx1ImpsDocumentData inv = new Anx1ImpsDocumentData();
		if (taxDocType != null && GSTConstants.CAN.equals(taxDocType)) {
			inv.setFlag(APIConstants.D); // D-Delete
		}
		inv.setPos(pos);
		inv.setRfndelg(rfndelg);
		inv.setItems(itmsList);
		/**
		 * An Extra field used in the process of chunking.
		 */
		inv.setDocId(id);
		return inv;
	}

	private SaveAnx1 setBatch(Object[] arr1, String section,
			List<Anx1ImpsDocumentData> invList) {

		String txPriod = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		String cGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		LOGGER.info("New Anx1 {} Batch with SGSTN {} and TaxPeriod {}", section,
				cGstin, txPriod);
		SaveAnx1 anx1 = new SaveAnx1();
		anx1.setSgstin(cGstin);
		anx1.setTaxperiod(txPriod);
		anx1.setImpsInvoice(invList);

		return anx1;
	}
}
