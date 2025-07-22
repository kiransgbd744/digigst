package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.CDNRInvoices;
import com.ey.advisory.app.docs.dto.CreditDebitNote;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.common.AppException;
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
@Service("RateDataToCdnrConverter")
public class RateDataToCdnrConverter implements RateDataToGstr1Converter {

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2, int chunkSize) {

		Long id = arr1[0] != null ? (Long) arr1[0] : null;
		Long id2 = arr2[0] != null ? (Long) arr2[0] : null;
		return !id.equals(id2)
				|| isNewCtin(arr1, arr2, idsList, totSize, counter2, chunkSize);
	}

	private boolean isNewCtin(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2, int chunkSize) {
		// URP(UnRegisteredParty) == NULL
		String cGstin = arr1[3] != null ? String.valueOf(arr1[3])
				: APIConstants.URP;
		String cGstin2 = arr2[3] != null ? String.valueOf(arr2[3])
				: APIConstants.URP;
		return cGstin != null && !cGstin.equalsIgnoreCase(cGstin2)
				|| isNewBatch(arr1, arr2, idsList, totSize, counter2,
						chunkSize);
	}

	private boolean isNewBatch(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2, int chunkSize) {

		return idsList.size() >= chunkSize || counter2 == totSize;
	}

	private SaveGstr1 setBatch(Object[] arr1, String section,
			List<CDNRInvoices> cdnrList) {

		String txPriod = arr1[2] != null ? String.valueOf(arr1[2]) : null;
		String sGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		LOGGER.info("New {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveGstr1 gstr1 = new SaveGstr1();
		gstr1.setSgstin(sGstin);
		gstr1.setTaxperiod(txPriod);
		gstr1.setCdnrInvoice(cdnrList);
		return gstr1;
	}

	private CDNRInvoices setInv(Object[] arr1,
			List<CreditDebitNote> creditdebitList) {
		String cGstin = arr1[3] != null ? String.valueOf(arr1[3]) : null;
		if (cGstin != null && cGstin.equalsIgnoreCase(APIConstants.URP)) {
			cGstin = null;
		}
		CDNRInvoices cdnr = new CDNRInvoices();
		cdnr.setCpGstin(cGstin);
		cdnr.setCreditDebitNoteDetails(creditdebitList);
		return cdnr;
	}

	private CreditDebitNote setInvData(Object[] arr1) {

		String docType = arr1[6] != null ? String.valueOf(arr1[6]) : null;
		String invDate = null;
		if (arr1[5] != null && arr1[5].toString().trim().length() > 0) {
			invDate = ((LocalDate) arr1[5])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		String invNum = arr1[4] != null ? String.valueOf(arr1[4]) : null;
		Long id = arr1[0] != null ? (Long) arr1[0] : null;

		CreditDebitNote credeb = new CreditDebitNote();
		credeb.setInvoiceStatus(APIConstants.D);// D-Delete, A-Accept, R-Reject
		credeb.setCredDebRefVoucherNum(invNum);
		credeb.setCredDebRefVoucherDate(invDate);
		if (GSTConstants.CR.concat(",").concat(GSTConstants.RCR)
				.contains(docType)) {
			credeb.setCredDebRefVoucher(APIConstants.C);
		} else if (GSTConstants.DR.concat(",").concat(GSTConstants.RDR)
				.contains(docType)) {
			credeb.setCredDebRefVoucher(APIConstants.D);
		} else if (GSTConstants.RFV.concat(",").concat(GSTConstants.RRFV)
				.contains(docType)) {
			credeb.setCredDebRefVoucher(APIConstants.R);
		}
		credeb.setDocId(id);
		return credeb;
	}

	@Override
	public SaveBatchProcessDto convertToGstr1Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType,
			int chunkSize) {
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveGstr1> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				List<CDNRInvoices> cdnrList = new ArrayList<>();
				List<CreditDebitNote> creditdebitList = new ArrayList<>();
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

					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2,
							chunkSize)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						CreditDebitNote credeb = setInvData(arr1);
						creditdebitList.add(credeb);

					}
					if (isNewCtin(arr1, arr2, idsList, totSize, counter2,
							chunkSize)) {
						CDNRInvoices cdnr = setInv(arr1, creditdebitList);
						cdnrList.add(cdnr);
						creditdebitList = new ArrayList<>();
					}
					if (isNewBatch(arr1, arr2, idsList, totSize, counter2,
							chunkSize)) {
						SaveGstr1 gstr1 = setBatch(arr1, section, cdnrList);
						batchesList.add(gstr1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						cdnrList = new ArrayList<>();
						creditdebitList = new ArrayList<>();
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
