package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.B2BInvoiceData;
import com.ey.advisory.app.docs.dto.B2BInvoices;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("RateDataToB2bConverter")
public class RateDataToB2bConverter implements RateDataToGstr1Converter {

	private B2BInvoiceData setInvData(Object[] arr1) {

		String invDate = null;
		if (arr1[5] != null && arr1[5].toString().trim().length() > 0) {
			invDate = ((LocalDate) arr1[5])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		String invNum = arr1[4] != null ? String.valueOf(arr1[4]) : null;
		Long id = arr1[0] != null ? (Long) arr1[0] : null;
		
		B2BInvoiceData inv = new B2BInvoiceData();
		inv.setInvoiceStatus(APIConstants.D); // D-Delete					
		inv.setInvoiceNumber(invNum);
		inv.setInvoiceDate(invDate);
		inv.setDocId(id);
		return inv;
	}


	private B2BInvoices setInv(Object[] arr1, List<B2BInvoiceData> invList) {

		String cGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		if (cGstin.equalsIgnoreCase(APIConstants.URP)) {
			cGstin = null;
		}
		B2BInvoices b2b = new B2BInvoices();
		b2b.setCgstin(cGstin);
		b2b.setB2bInvoiceData(invList);
		return b2b;
	}

	private SaveGstr1 setBatch(Object[] arr1, String section,
			List<B2BInvoices> b2bList) {

		String txPriod = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		String sGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		LOGGER.info("New {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveGstr1 gstr1 = new SaveGstr1();
		gstr1.setSgstin(sGstin);
		gstr1.setTaxperiod(txPriod);
		gstr1.setB2bInvoice(b2bList);
		
		return gstr1;
	}

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2, int chunkSize) {
		Long id = arr1[0] != null ? (Long) arr1[0] : null;
		Long id2 = arr2[0] != null ? (Long) arr2[0] : null;
		return !id.equals(id2)
				|| isNewCtin(arr1, arr2, idsList, totSize, counter2, chunkSize);
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2, int chunkSize) {
		String cGstin = arr1[3] != null ? String.valueOf(arr1[3])
				: APIConstants.URP;
		String cGstin2 = arr2[3] != null ? String.valueOf(arr2[3])
				: APIConstants.URP;
		return cGstin != null && !cGstin.equalsIgnoreCase(cGstin2)
				|| isNewBatch(arr1, arr2, idsList, totSize, counter2, chunkSize);
	}

	private boolean isNewBatch(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2, int chunkSize) {

		return idsList.size() >= chunkSize
				|| counter2 == totSize;
	}

	@Override
	public SaveBatchProcessDto convertToGstr1Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType, int chunkSize) {

		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveGstr1> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				List<B2BInvoices> b2bList = new ArrayList<>();
				List<B2BInvoiceData> invList = new ArrayList<>();
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
					Long id = arr1[0] != null ? (Long) arr1[0] : null;

					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2, chunkSize)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						B2BInvoiceData inv = setInvData(arr1);
						invList.add(inv);
					}
					if (isNewCtin(arr1, arr2, idsList, totSize, counter2,chunkSize)) {
						B2BInvoices b2b = setInv(arr1, invList);
						b2bList.add(b2b);
						invList = new ArrayList<>();
					}
					if (isNewBatch(arr1, arr2, idsList, totSize, counter2, chunkSize)) {
						SaveGstr1 gstr1 = setBatch(arr1, section, b2bList);
						batchesList.add(gstr1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						b2bList = new ArrayList<>();
						invList = new ArrayList<>();
					}
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn";
				LOGGER.warn(msg, objects);
			}

		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		batchDto.setGstr1(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
	}
}
