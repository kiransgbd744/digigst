package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.DocAndSupplyTypeConstants;
import com.ey.advisory.app.docs.dto.B2CLInvoiceData;
import com.ey.advisory.app.docs.dto.B2CLInvoices;
import com.ey.advisory.app.docs.dto.B2CLLineItem;
import com.ey.advisory.app.docs.dto.B2clLineItemDetail;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("rateDataToB2clB2claConverter")
public class RateDataToB2clB2claConverter implements RateDataToGstr1Converter {

	@Autowired
	private GstnApi gstnapi;

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2, int chunkSize) {

		Long id = arr1[12] != null ? (Long) arr1[12] : null;
		Long id2 = arr2[12] != null ? (Long) arr2[12] : null;
		return !id.equals(id2)
				|| isNewPos(arr1, arr2, idsList, totSize, counter2, chunkSize);
	}

	private boolean isNewPos(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2, int chunkSize) {

		// String pos = arr1[4] != null ? String.valueOf(arr1[4]) : null;
		// String pos2 = arr2[4] != null ? String.valueOf(arr2[4]) : null;

		String pos = arr1[4] != null
				? (String.valueOf(arr1[4]).trim().length() == 1
						? "0".concat(String.valueOf(arr1[4]).trim())
						: String.valueOf(arr1[4]))
				: null;
		String pos2 = arr2[4] != null
				? (String.valueOf(arr2[4]).trim().length() == 1
						? "0".concat(String.valueOf(arr2[4]).trim())
						: String.valueOf(arr2[4]))
				: null;

		return pos != null && !pos.equals(pos2) || isNewBatch(arr1, arr2,
				idsList, totSize, counter2, chunkSize);
	}

	private boolean isNewBatch(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2, int chunkSize) {

		/*
		 * String txPriod = arr1[1]!= null ? String.valueOf(arr1[1]) : null;
		 * String sGstin = arr1[0]!= null ? String.valueOf(arr1[0]) : null;
		 * String txPriod2 = arr2[1]!= null ? String.valueOf(arr2[1]) : null;
		 * String sGstin2 = arr2[0]!= null ? String.valueOf(arr2[0]) : null;
		 */
		return /*
				 * (sGstin != null && !sGstin.equals(sGstin2)) || (txPriod !=
				 * null && !txPriod.equals(txPriod2)) ||
				 */ idsList.size() >= chunkSize || counter2 == totSize;
	}

	private B2CLLineItem setItemDetail(Object[] arr1, int counter2) {

		BigDecimal cessAmt = arr1[11] != null
				? new BigDecimal(String.valueOf(arr1[11])) : BigDecimal.ZERO;
		BigDecimal igstAmt = arr1[10] != null
				? new BigDecimal(String.valueOf(arr1[10])) : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[9] != null
				? new BigDecimal(String.valueOf(arr1[9])) : BigDecimal.ZERO;
		BigDecimal taxRate = arr1[8] != null
				? new BigDecimal(String.valueOf(arr1[8])) : BigDecimal.ZERO;

		B2CLLineItem itms = new B2CLLineItem();
		B2clLineItemDetail itemDeta = new B2clLineItemDetail();
		itemDeta.setCessAmount(cessAmt);
		itemDeta.setIgstAmount(igstAmt);
		itemDeta.setTaxableValue(taxableVal);
		itemDeta.setRate(taxRate);
		itms.setLineNumber(counter2);
		itms.setItemDetail(itemDeta);
		return itms;

	}

	private B2CLInvoiceData setInvData(Object[] arr1, String section,
			List<B2CLLineItem> itmsList, String taxDocType) {

		String docType = arr1[16] != null ? String.valueOf(arr1[16]) : null;
		String oInvDate = null;
		if (arr1[15] != null && arr1[15].toString().trim().length() > 0) {
			oInvDate = ((LocalDate) arr1[15])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		String oInvNum = arr1[14] != null ? String.valueOf(arr1[14]) : null;
		String invDate = null;
		if (arr1[13] != null && arr1[13].toString().trim().length() > 0) {
			invDate = ((LocalDate) arr1[13])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		Long id = (Long) arr1[12];
		String sec7 = arr1[7] != null ? String.valueOf(arr1[7])
				: APIConstants.N;
		String diffPercent = arr1[6] != null ? String.valueOf(arr1[6]) : null;
		String eTin = arr1[5] != null ? String.valueOf(arr1[5]) : null;
		BigDecimal invVal = arr1[3] != null ? (BigDecimal) arr1[3]
				: BigDecimal.ZERO;
		String invNum = arr1[2] != null ? String.valueOf(arr1[2]) : null;

		B2CLInvoiceData inv = new B2CLInvoiceData();
		if (taxDocType != null
				&& GSTConstants.CAN.equalsIgnoreCase(taxDocType)) {
			inv.setInvoiceStatus(APIConstants.D); // D-Delete
		}
		if (GSTConstants.L65.equalsIgnoreCase(diffPercent)) {
			inv.setDiffPercent(new BigDecimal("0.65"));
		} else if (GSTConstants.L.equalsIgnoreCase(diffPercent)) {
			// needs to clarify
		} else if (GSTConstants.N.equalsIgnoreCase(diffPercent)) {
			// needs to clarify
		}
		inv.setEcomTin(eTin);
		inv.setInvoiceValue(invVal);
		inv.setInvoiceNum(invNum);
		inv.setInvoiceDate(invDate);
		inv.setInvoiceType(decideInvType(docType, sec7));
		if (section.equals(APIConstants.B2CLA)) {
			inv.setOinum(oInvNum);
			inv.setOidt(oInvDate);
		}
		inv.setDocId(id);
		inv.setLineItems(itmsList);
		return inv;

	}

	private String decideInvType(String docType, String sec7) {

		if (gstnapi.isDelinkingEligible(APIConstants.GSTR1.toUpperCase())) {
			if (DocAndSupplyTypeConstants.INV.equals(docType)) {
				if (sec7.equals(APIConstants.N)) {
					// return APIConstants.INV_TYPE_R;
					return null;
				} else {
					return null;
				}
			}
		} else {
			if (DocAndSupplyTypeConstants.INV.equals(docType)) {
				if (sec7.equals(APIConstants.N)) {
					// return APIConstants.INV_TYPE_R;
					return null;
				} else {
					return APIConstants.INV_TYPE_CBW;
				}
			}
		}
		return null;
	}

	private SaveGstr1 setBatch(Object[] arr1, String section,
			List<B2CLInvoices> b2clList) {

		String txPriod = arr1[1] != null ? String.valueOf(arr1[1]) : null;
		String sGstin = arr1[0] != null ? String.valueOf(arr1[0]) : null;
		LOGGER.info("New {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveGstr1 gstr1 = new SaveGstr1();
		gstr1.setSgstin(sGstin);
		gstr1.setTaxperiod(txPriod);
		// gstr1.setGt(new BigDecimal("3782969.01")); // static
		// gstr1.setCur_gt(new BigDecimal("3782969.01")); // static
		if (section.equals(APIConstants.B2CL)) {
			gstr1.setB2clInvoice(b2clList);
		} else if (section.equals(APIConstants.B2CLA)) {
			gstr1.setB2claInvoice(b2clList);
		}
		return gstr1;
	}

	private B2CLInvoices setInv(Object[] arr1, List<B2CLInvoiceData> invList) {

		// String pos = arr1[4] != null ? String.valueOf(arr1[4]) : null ;
		String pos = arr1[4] != null
				? (String.valueOf(arr1[4]).trim().length() == 1
						? "0".concat(String.valueOf(arr1[4]).trim())
						: String.valueOf(arr1[4]))
				: null;

		B2CLInvoices b2cl = new B2CLInvoices();
		b2cl.setPointOfSupply(pos);
		b2cl.setB2CLInvoiceData(invList);
		return b2cl;
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
				List<B2CLInvoices> b2clList = new ArrayList<>();
				List<B2CLInvoiceData> invList = new ArrayList<>();
				List<B2CLLineItem> itmsList = new ArrayList<>();
				List<Long> idsList = new ArrayList<>();
				int itemNumber = 0;
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
					Long id = arr1[12] != null ? (Long) arr1[12] : null;
					B2CLLineItem itms = setItemDetail(arr1, ++itemNumber);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2,
							chunkSize)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						B2CLInvoiceData inv = setInvData(arr1, section,
								itmsList, taxDocType);
						invList.add(inv);
						itmsList = new ArrayList<>();
						itemNumber = 0;
					}
					if (isNewPos(arr1, arr2, idsList, totSize, counter2,
							chunkSize)) {
						B2CLInvoices b2cl = setInv(arr1, invList);
						b2clList.add(b2cl);
						itmsList = new ArrayList<>();
						invList = new ArrayList<>();
					}
					if (isNewBatch(arr1, arr2, idsList, totSize, counter2,
							chunkSize)) {
						SaveGstr1 gstr1 = setBatch(arr1, section, b2clList);
						batchesList.add(gstr1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						b2clList = new ArrayList<>();
						itmsList = new ArrayList<>();
						invList = new ArrayList<>();
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
			throw new AppException(msg, ex);
		}
		batchDto.setGstr1(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
	}
}
