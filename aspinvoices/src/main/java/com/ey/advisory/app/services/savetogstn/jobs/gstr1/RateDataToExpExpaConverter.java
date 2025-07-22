package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.EXPInvoiceData;
import com.ey.advisory.app.docs.dto.EXPInvoices;
import com.ey.advisory.app.docs.dto.EXPLineItem;
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
@Service("rateDataToExpExpaConverter")
public class RateDataToExpExpaConverter implements RateDataToGstr1Converter {
	
	@Autowired
	private GstnApi gstnapi;

	private boolean isNewInvoice(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2, int chunkSize) {

		Long id = arr1[9]!= null ? (Long) arr1[9] : null;
		Long id2 = arr2[9]!= null ? (Long) arr2[9] : null;
		return !id.equals(id2)
				|| isNewSupType(arr1, arr2, idsList, totSize, counter2, chunkSize);
	}

	private boolean isNewSupType(Object[] arr1, Object[] arr2,
			List<Long> idsList, int totSize, int counter2, int chunkSize) {

		String supType = arr1[16]!= null ? String.valueOf(arr1[16]) : null;
		String supType2 = arr2[16]!= null ? String.valueOf(arr2[16]) : null;
		return supType != null && !supType.equals(supType2)
				|| isNewBatch(arr1, arr2, idsList, totSize, counter2, chunkSize);
	}

	private boolean isNewBatch(Object[] arr1, Object[] arr2, List<Long> idsList,
			int totSize, int counter2, int chunkSize) {

		/*String txPriod = arr1[1]!= null ? String.valueOf(arr1[1]) : null;
		String sGstin = arr1[0]!= null ? String.valueOf(arr1[0]) : null;
		String txPriod2 = arr2[1]!= null ? String.valueOf(arr2[1]) : null;
		String sGstin2 = arr2[0]!= null ? String.valueOf(arr2[0]) : null;*/
		return /*(sGstin != null && !sGstin.equals(sGstin2))
				|| (txPriod != null && !txPriod.equals(txPriod2))
				||*/ idsList.size() >= chunkSize
				|| counter2 == totSize;
	}

	private SaveGstr1 setBatch(Object[] arr1, String section,
			List<EXPInvoices> expList) {
		String txPriod = arr1[1]!= null ? String.valueOf(arr1[1]) : null;
		String sGstin = arr1[0]!= null ? String.valueOf(arr1[0]) : null;
		LOGGER.info("New {} Batch with SGSTN {} and TaxPeriod {}", section,
				sGstin, txPriod);
		SaveGstr1 gstr1 = new SaveGstr1();
		gstr1.setSgstin(sGstin);
		gstr1.setTaxperiod(txPriod);
		//gstr1.setGt(new BigDecimal("3782969.01")); // static
		//gstr1.setCur_gt(new BigDecimal("3782969.01")); // static

		if (section.equals(APIConstants.EXP)) {
			gstr1.setExpInvoice(expList);
		} else if (section.equals(APIConstants.EXPA)) {
			gstr1.setExpaInvoice(expList);
		}
		return gstr1;
	}

	private EXPInvoiceData setInvData(Object[] arr1, String section,
			List<EXPLineItem> itmsList, String taxDocType) {

		String shippingDate = null;
		if (arr1[15] != null && arr1[15].toString().trim().length() > 0) {
			shippingDate = ((LocalDate) arr1[15])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		String shippingNum = arr1[14]!= null ? String.valueOf(arr1[14]) : null;
		String portCode = arr1[13]!= null ? String.valueOf(arr1[13]) : null;
		String oInvDate = null;
		if (arr1[12] != null && arr1[12].toString().trim().length() > 0) {
			oInvDate = ((LocalDate) arr1[12])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		String oInvNum = arr1[11]!= null ? String.valueOf(arr1[11]) : null;
		String invDate = null;
		if (arr1[10] != null && arr1[10].toString().trim().length() > 0) {
			invDate = ((LocalDate) arr1[10])
					.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		Long id = arr1[9]!= null ? (Long) arr1[9] : null;
		String diffPercent = arr1[4]!= null ? String.valueOf(arr1[4]) : null;
		BigDecimal invVal = arr1[3] != null ? new BigDecimal(String.valueOf(arr1[3])) : BigDecimal.ZERO;
		String invNum = arr1[2]!= null ? String.valueOf(arr1[2]) : null;

		EXPInvoiceData inv = new EXPInvoiceData();
		if(taxDocType != null && GSTConstants.CAN.equalsIgnoreCase(taxDocType)){
		inv.setInvoiceStatus(APIConstants.D); //D-Delete
		}
		inv.setInvoiceNum(invNum);
		inv.setInvoiceDate(invDate);
		inv.setInvoiceValue(invVal);
		inv.setShipBillPortCode(portCode);
		inv.setShipBillNum(shippingNum);
		inv.setShipBillDate(shippingDate);
		if (gstnapi.isDelinkingEligible(APIConstants.GSTR1.toUpperCase())) {
		
		} else {
			if(GSTConstants.L65.equalsIgnoreCase(diffPercent)) {
				inv.setDiffPercent(new BigDecimal("0.65"));
			} else if(GSTConstants.L.equalsIgnoreCase(diffPercent)) {
				// needs to clarify
			} else if(GSTConstants.N.equalsIgnoreCase(diffPercent)) {
				// needs to clarify
			}
		}
		if (section.equals(APIConstants.EXPA)) {
			inv.setOidt(oInvDate);
			inv.setOinum(oInvNum);
		}
		inv.setDocId(id);
		inv.setLineItems(itmsList);
		return inv;
	}

	private EXPLineItem setItemDetail(Object[] arr1) {

		BigDecimal cessAmt = arr1[8] != null ? new BigDecimal(String.valueOf(arr1[8])) : BigDecimal.ZERO;
		BigDecimal igstAmt = arr1[7] != null ? new BigDecimal(String.valueOf(arr1[7])) : BigDecimal.ZERO;
		BigDecimal taxableVal = arr1[6] != null ? new BigDecimal(String.valueOf(arr1[6])) : BigDecimal.ZERO;
		BigDecimal taxRate = arr1[5] != null ? new BigDecimal(String.valueOf(arr1[5])) : BigDecimal.ZERO;

		EXPLineItem itms = new EXPLineItem();
		itms.setRate(taxRate);
		itms.setTaxableValue(taxableVal);
		itms.setIgstAmount(igstAmt);
		itms.setCessAmount(cessAmt);
		return itms;
	}

	private EXPInvoices setInv(Object[] arr1, List<EXPInvoiceData> invList) {

		String supType = arr1[16]!= null ? String.valueOf(arr1[16]) : null;
		EXPInvoices exp = new EXPInvoices();
		if (supType.equals(GSTConstants.EXPWT)) {
			exp.setExportType(APIConstants.EXP_TYPE_WOPAY);
		} else if (supType.equals(GSTConstants.EXPT)) {
			exp.setExportType(APIConstants.EXP_TYPE_WPAY);
		}
		exp.setExpInvoiceData(invList);
		return exp;
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
				List<EXPInvoices> expList = new ArrayList<>();
				List<EXPInvoiceData> invList = new ArrayList<>();
				List<EXPLineItem> itmsList = new ArrayList<>();
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
					Long id = (Long) arr1[9];
					EXPLineItem itms = setItemDetail(arr1);
					itmsList.add(itms);
					if (isNewInvoice(arr1, arr2, idsList, totSize, counter2, chunkSize)) {
						/**
						 * This ids are used to update the Gstr1_doc_header
						 * table as a single/same batch.
						 */
						idsList.add(id);
						EXPInvoiceData inv = setInvData(arr1, section,
								itmsList, taxDocType);
						invList.add(inv);
						itmsList = new ArrayList<>();
					}
					if (isNewSupType(arr1, arr2, idsList, totSize, counter2, chunkSize)) {

						EXPInvoices exp = setInv(arr1, invList);
						expList.add(exp);

						invList = new ArrayList<>();
						itmsList = new ArrayList<>();
					}
					if (isNewBatch(arr1, arr2, idsList, totSize, counter2, chunkSize)) {
						
						SaveGstr1 gstr1 = setBatch(arr1, section, expList);
						batchesList.add(gstr1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						expList = new ArrayList<>();
						invList = new ArrayList<>();
						itmsList = new ArrayList<>();
					}
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn";
				LOGGER.warn(msg, objects);
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
			throw new AppException(msg,ex);
		}
		batchDto.setGstr1(batchesList);
		batchDto.setIdsList(batchIdsList);
		return batchDto;
	}
}
