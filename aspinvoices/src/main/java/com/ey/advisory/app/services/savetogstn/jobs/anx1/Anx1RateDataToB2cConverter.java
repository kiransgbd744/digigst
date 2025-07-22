package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1B2cData;
import com.ey.advisory.app.docs.dto.anx1.Anx1B2cItemDetails;
import com.ey.advisory.app.docs.dto.anx1.SaveAnx1;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("anx1RateDataToB2cConverter")
@Slf4j
public class Anx1RateDataToB2cConverter implements RateDataToAnx1Converter {

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
				List<Anx1B2cData> b2cList = new ArrayList<>();
				List<Anx1B2cItemDetails> itmsList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				int maxInvoices = 0 ;

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
					Anx1B2cItemDetails itms = setItemDetail(arr1);
					itmsList.add(itms);
					
					if(isNewInvoice(arr1, arr2)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
					} /*else {
						continue;
					}*/

					if (isNewPos(arr1, arr2, maxInvoices,totSize, counter2)) {
						
						maxInvoices ++;
						Anx1B2cData b2c = setInvData(arr1, itmsList,
								taxDocType);
						b2cList.add(b2c);
						itmsList = new ArrayList<>();
					} /*else {
						continue;
					}*/

					if (isNewBatch(maxInvoices, totSize, counter2)) {

						SaveAnx1 anx1 = setBatch(arr1, section, b2cList);
						LOGGER.debug("New {} Batch is formed {}", section, anx1);
						batchesList.add(anx1);
						batchIdsList.add(idsList);
						/**
						 * reset
						 */
						idsList = new ArrayList<>();
						b2cList = new ArrayList<>();
						itmsList = new ArrayList<>();
					}
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn "
						+ "with arg {} ";
				LOGGER.warn(msg, objects);
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
		}
		batchDto.setAnx1(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
	}
	
	private boolean isNewInvoice(Object[] arr1, Object[] arr2) {
		/**
		 * Assuming id IS NOTNULL(primary key)
		 */
		Long id = new Long(String.valueOf(arr1[0]));
		Long id2 = new Long(String.valueOf(arr2[0]));
		return !id.equals(id2);
	}

	private boolean isNewPos(Object[] arr1, Object[] arr2,
			int maxInvoices, int totSize, int counter2) {

		String pos = arr1[6] !=null ? String.valueOf(arr1[6]) : null;
		String pos2 = arr2[6] != null ? String.valueOf(arr2[6]) : null;
		return pos != null && !pos.equals(pos2)
				|| isNewBatch(maxInvoices, totSize, counter2);
	}

	private boolean isNewBatch(int maxInvoices,
			int totSize, int counter2) {
		
		//Summary section should go as single batch becoz for every summary 
		//section save GSTN will Override the Data at Sandbox.
		return  /*maxInvoices >= chunkSizeFetcher.getSize()
				||*/ counter2 == totSize;
	}

	private Anx1B2cItemDetails setItemDetail(Object[] arr1) {

		BigDecimal cess = arr1[12] != null ? new BigDecimal(String.valueOf(arr1[12])) : BigDecimal.ZERO;
		BigDecimal sgst = arr1[11] != null ? new BigDecimal(String.valueOf(arr1[11])) : BigDecimal.ZERO;
		BigDecimal cgst = arr1[10] != null ? new BigDecimal(String.valueOf(arr1[10])) : BigDecimal.ZERO;
		BigDecimal igst = arr1[9] != null ? new BigDecimal(String.valueOf(arr1[9])) : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[8] != null ? new BigDecimal(String.valueOf(arr1[8])) : null;
		BigDecimal taxRate = arr1[7] != null ? new BigDecimal(String.valueOf(arr1[7])) : null;
		
		Anx1B2cItemDetails itms = new Anx1B2cItemDetails();
		
		String pos = arr1[6] != null ? String.valueOf(arr1[6]) : null;
		String sGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		String sec7 = arr1[4] != null ? String.valueOf(arr1[4]) : APIConstants.N ;
		if (APIConstants.Y.equalsIgnoreCase(sec7)|| (sGstin != null
				&& !sGstin.substring(0, 2).equalsIgnoreCase(pos)
				|| igst.compareTo(BigDecimal.ZERO) > 0)) {
			itms.setIgstAmount(igst);
		} else {
			itms.setCgstAmount(cgst);
			itms.setSgstAmount(sgst);
		}
		itms.setCessAmount(cess);
		itms.setTaxableValue(taxableVal);
		itms.setRate(taxRate);
		return itms;
	}

	private Anx1B2cData setInvData(Object[] arr1,
			List<Anx1B2cItemDetails> itmsList, String taxDocType) {
		
		String diffprcnt = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		String sec7 = arr1[4] != null ? String.valueOf(arr1[4]) : APIConstants.N ;
		String rfndelg = arr1[5] != null ? String.valueOf(arr1[5]) : APIConstants.N;
		String pos = arr1[6] != null ? String.valueOf(arr1[6]) : null;
		
		Anx1B2cData inv = new Anx1B2cData();
		if (taxDocType != null && GSTConstants.CAN.equals(taxDocType)) {
			inv.setFlag(APIConstants.D); // D-Delete
		}
		if (!APIConstants.N.equalsIgnoreCase(diffprcnt)) {
			inv.setDiffprcnt(new BigDecimal("0.65"));
		}
		inv.setPos(pos);
		inv.setRfndelg(rfndelg);
		inv.setSec7act(sec7);
		inv.setItems(itmsList);
		return inv;
	}

	private SaveAnx1 setBatch(Object[] arr1, String section,
			List<Anx1B2cData> b2csList) {

		String txPriod = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		String sGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		LOGGER.info("New Anx1 {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveAnx1 anx1 = new SaveAnx1();
		anx1.setSgstin(sGstin);
		anx1.setTaxperiod(txPriod);
		anx1.setB2cInvoice(b2csList);

		return anx1;
	}

}
