package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.anx1.Anx1RevData;
import com.ey.advisory.app.docs.dto.anx1.Anx1RevDocumentData;
import com.ey.advisory.app.docs.dto.anx1.Anx1RevItemDetails;
import com.ey.advisory.app.docs.dto.anx1.SaveAnx1;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("anx1RateDataToRevConverter")
@Slf4j
public class Anx1RateDataToRevConverter implements RateDataToAnx1Converter {

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
				List<Anx1RevData> revList = new ArrayList<>();
				List<Anx1RevDocumentData> invList = new ArrayList<>();
				List<Anx1RevItemDetails> itmsList = new ArrayList<>();
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
					Anx1RevItemDetails itms = setItemDetail(arr1);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						Anx1RevDocumentData inv = setInvData(arr1,
								itmsList, taxDocType);
						invList.add(inv);
						itmsList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewCtin(arr1, arr2, idsList, totSize, counter2)) {
						Anx1RevData rev = setInv(arr1, invList);
						revList.add(rev);
						itmsList = new ArrayList<>();
						invList = new ArrayList<>();
					} else {
						continue;
					}
					if (isNewBatch(idsList, totSize, counter2)) {
						SaveAnx1 anx1 = setBatch(arr1, section, revList);
						LOGGER.debug("New {} Batch is formed {}", section, anx1);
						batchesList.add(anx1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						revList = new ArrayList<>();
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

	private Anx1RevItemDetails setItemDetail(Object[] arr1) {

		BigDecimal cessAmt = arr1[14] != null ? new BigDecimal(String.valueOf(arr1[14])) : BigDecimal.ZERO;
		BigDecimal sgstAmt = arr1[13] != null ? new BigDecimal(String.valueOf(arr1[13])) : BigDecimal.ZERO;
		BigDecimal cgstAmt = arr1[12] != null ? new BigDecimal(String.valueOf(arr1[12])) : BigDecimal.ZERO;
		BigDecimal igstAmt = arr1[11] != null ? new BigDecimal(String.valueOf(arr1[11])) : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[10] != null ? new BigDecimal(String.valueOf(arr1[10])) : null;
		BigDecimal taxRate = arr1[9] != null ? new BigDecimal(String.valueOf(arr1[9])) : null;
		String hsnsac = arr1[8] != null ? String.valueOf(arr1[8]) : null;
		Anx1RevItemDetails item = new Anx1RevItemDetails();
		
		String pos = arr1[7] != null ? String.valueOf(arr1[7]) : null;
		String sGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		String sec7 = arr1[5] != null ? String.valueOf(arr1[5]) : APIConstants.N;
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

	private Anx1RevDocumentData setInvData(Object[] arr1,
			List<Anx1RevItemDetails> itmsList, String taxDocType) {

		Long id = new Long(String.valueOf(arr1[0]));
		String difPrcnt = arr1[4] != null ? String.valueOf(arr1[4]) : null;
		String sec7 = arr1[5] != null ? String.valueOf(arr1[5]) : APIConstants.N;
		String rfndel = arr1[6] != null ? String.valueOf(arr1[6]) : APIConstants.N;
		String pos = arr1[7] != null ? String.valueOf(arr1[7]) : null;
		
		String cGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		BigDecimal igstAmt = arr1[11] != null ? new BigDecimal(String.valueOf(arr1[11])) : BigDecimal.ZERO;

		String sGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		String supTyp = null;
		if (sGstin != null && sGstin.length() != 15) {
			//cGstin is PAN
			if (!cGstin.equalsIgnoreCase(pos)
							|| igstAmt.compareTo(BigDecimal.ZERO) > 0) {
				supTyp = APIConstants.SUP_TYPE_INTER.toLowerCase();
			} else {
				supTyp = APIConstants.SUP_TYPE_INTRA.toLowerCase();
			}
		}

		Anx1RevDocumentData inv = new Anx1RevDocumentData();
		if (taxDocType != null && GSTConstants.CAN.equals(taxDocType)) {
			inv.setFlag(APIConstants.D); // D-Delete, A-Accept, R-Reject
		}
		inv.setPos(pos);
		//Comments are for testing purpose (Chinna)
		/*if (!APIConstants.N.equalsIgnoreCase(difPrcnt)) {
			inv.setDiffprcntSaveToGstn(new BigDecimal("0.65"));
		}*/
		inv.setRfndelg(rfndel);
		inv.setSec7act(sec7);
		inv.setSuptyp(supTyp);
		inv.setItems(itmsList);
		/**
		 *  An Extra field used in the process of chunking.
		 */
		inv.setDocId(id);
		return inv;
	}

	private Anx1RevData setInv(Object[] arr1,
			List<Anx1RevDocumentData> invList) {

		String sGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		String type = null ;
		if(sGstin != null) {
			if(sGstin.length() == 15) {
				type = APIConstants.G;
			} else {
				type = APIConstants.P;
			}
			
		}
		Anx1RevData rev = new Anx1RevData();
		rev.setCgstin(sGstin);
		rev.setType(type);
		rev.setDocs(invList);
		return rev;
	}

	private SaveAnx1 setBatch(Object[] arr1, String section,
			List<Anx1RevData> revList) {

		String txPriod = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		String cGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		LOGGER.info("New Anx1 {} Batch with SGSTN {} and TaxPeriod {}", section,
				cGstin, txPriod);
		SaveAnx1 anx1 = new SaveAnx1();
		anx1.setSgstin(cGstin);
		anx1.setTaxperiod(txPriod);
		anx1.setRevInvoice(revList);
		return anx1;
	}

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2) {
		/*Long id = new Long(String.valueOf(arr1[0]));
		Long id2 = new Long(String.valueOf(arr2[0]));*/
		
		String difPrcnt = arr1[4] != null ? String.valueOf(arr1[4]) : "";
		String sec7 = arr1[5] != null ? String.valueOf(arr1[5]) : "";
		String rfndel = arr1[6] != null ? String.valueOf(arr1[6]) : "";
		String pos = arr1[7] != null ? String.valueOf(arr1[7]) : "";
		
		String difPrcnt2 = arr2[4] != null ? String.valueOf(arr2[4]) : "";
		String sec72 = arr2[5] != null ? String.valueOf(arr2[5]) : "";
		String rfndel2 = arr2[6] != null ? String.valueOf(arr2[6]) : "";
		String pos2 = arr2[7] != null ? String.valueOf(arr2[7]) : "";
		
		return /*!id.equals(id2)*/
				!difPrcnt.equals(difPrcnt2) || !sec7.equals(sec72) || !rfndel.equals(rfndel2) || !pos.equals(pos2)
				|| isNewCtin(arr1, arr2, idsList, totSize, counter2);
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2) {
		String sGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		String sGstin2 = arr2[3] != null ? String.valueOf(arr2[3]) : null;
		return sGstin != null && !sGstin.equals(sGstin2)
				|| isNewBatch(idsList, totSize, counter2);
	}

	private boolean isNewBatch(List<Long> idsList,
			int totSize, int counter2) {
		
		//Summary section should go as single batch becoz for every summary 
		//section save GSTN will Override the Data at Sandbox.
		return /*idsList.size() >= chunkSizeFetcher.getSize()
				||*/ counter2 == totSize;
	}

}
