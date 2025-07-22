package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.app.docs.dto.SupEcom;
import com.ey.advisory.app.docs.dto.SupEcomInvoices;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Service("RateDataToSupEcomConverter")
public class RateDataToSupEcomConverter implements RateDataToGstr1Converter {

	public static void main(String[] args) {
		List<Object[]> objects = new ArrayList<>();
		objects.add(new Object[] { 1L, "GSTIN123", "082023", null,
				GSTConstants.GSTR1_14I, "ECOMGSTN1", "10000", "1000", "2000",
				"3000", "4000" });
		objects.add(new Object[] { 2L, "GSTIN123", "082023", null,
				GSTConstants.GSTR1_14II, "ECOMGSTN1", "20000", "3000", "4000",
				"5000", "6000" });
		objects.add(new Object[] { 3L, "GSTIN123", "082023", null,
				GSTConstants.GSTR1_14I, "ECOMGSTN2", "30000", "4000", "6000",
				"7000", "8000" });
		objects.add(new Object[] { 4L, "GSTIN123", "082023", null,
				GSTConstants.GSTR1_14I, "ECOMGSTN3", "40000", "6000", "6000",
				"7000", "8000" });
		objects.add(new Object[] { 5L, "GSTIN123", "082023", null,
				GSTConstants.GSTR1_14I, "ECOMGSTN4", "50000", "8000", "6000",
				"7000", "8000" });
		objects.add(new Object[] { 6L, "GSTIN123", "082023", null,
				GSTConstants.GSTR1_14II, "ECOMGSTN4", "50000", "8000", "6000",
				"7000", "8000" });

		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveGstr1> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		SupEcom supEcomDto = new SupEcom();
		int totSize = objects.size();
		List<Long> idsList = new ArrayList<>();
		SaveGstr1 gstr1 = null;

		List<SupEcomInvoices> clxList = new ArrayList<>();
		List<SupEcomInvoices> paytxList = new ArrayList<>();

		for (int counter = 0; counter < totSize; counter++) {
			Object[] arr1 = objects.get(counter);
			List<SupEcomInvoices> supEcomInvList = new ArrayList<>();
			SupEcomInvoices supEcomInvoices = new SupEcomInvoices();
			// first object[]
			Long id = arr1[0] != null ? (Long) arr1[0] : null;
			String sGstin = arr1[1] != null ? String.valueOf(arr1[1]) : null;
			String txPriod = arr1[2] != null ? String.valueOf(arr1[2]) : null;
			String tableSection = arr1[4] != null ? String.valueOf(arr1[4])
					: null;
			String ecomGstin = arr1[5] != null ? String.valueOf(arr1[5]) : null;
			BigDecimal taxable = arr1[6] != null
					? new BigDecimal(String.valueOf(arr1[6])) : BigDecimal.ZERO;
			BigDecimal igst = arr1[7] != null
					? new BigDecimal(String.valueOf(arr1[7])) : BigDecimal.ZERO;
			BigDecimal cgst = arr1[8] != null
					? new BigDecimal(String.valueOf(arr1[8])) : BigDecimal.ZERO;
			BigDecimal sgst = arr1[9] != null
					? new BigDecimal(String.valueOf(arr1[9])) : BigDecimal.ZERO;
			BigDecimal cess = arr1[10] != null
					? new BigDecimal(String.valueOf(arr1[10]))
					: BigDecimal.ZERO;
			supEcomInvoices.setEtin(ecomGstin);
			supEcomInvoices.setSuppval(taxable);
			supEcomInvoices.setIgst(igst);
			supEcomInvoices.setCgst(cgst);
			supEcomInvoices.setSgst(sgst);
			supEcomInvoices.setCess(cess);
			supEcomInvoices.setFlag(APIConstants.N);
			idsList.add(id);
			supEcomInvList.add(supEcomInvoices);
			if (GSTConstants.GSTR1_14I.equalsIgnoreCase(tableSection)) {
				clxList.addAll(supEcomInvList);
			} else if (GSTConstants.GSTR1_14II.equalsIgnoreCase(tableSection)) {
				paytxList.addAll(supEcomInvList);
			}

			if (gstr1 == null) {
				gstr1 = new SaveGstr1();
				gstr1.setSgstin(sGstin);
				gstr1.setTaxperiod(txPriod);
			}
		}
		System.out.println(clxList);
		supEcomDto.setClttx(clxList);
		supEcomDto.setPaytx(paytxList);
		gstr1.setSupEco(supEcomDto);
		batchesList.add(gstr1);
		batchIdsList.add(idsList);
		idsList = new ArrayList<>();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("SaveGstr1 Dto {} ", gstr1);
		}
		batchDto.setGstr1(batchesList);
		batchDto.setIdsList(batchIdsList);
		System.out.println(new Gson().toJson(batchDto));

	}

	@Override
	public SaveBatchProcessDto convertToGstr1Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType, int chunkSize) {
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveGstr1> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		SupEcom supEcomDto = new SupEcom();
		List<SupEcomInvoices> clxList = new ArrayList<>();
		List<SupEcomInvoices> paytxList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				List<Long> idsList = new ArrayList<>();
				SaveGstr1 gstr1 = null;
				for (int counter = 0; counter < totSize; counter++) {
					List<SupEcomInvoices> supEcomInvList = new ArrayList<>();
					SupEcomInvoices supEcomInvoices = new SupEcomInvoices();
					Object[] resultSet = objects.get(counter);
					// first object[]
					Long id = resultSet[0] != null ? (Long) resultSet[0] : null;
					String sGstin = resultSet[1] != null
							? String.valueOf(resultSet[1]) : null;
					String txPriod = resultSet[2] != null
							? String.valueOf(resultSet[2]) : null;
					String tableSection = resultSet[4] != null
							? String.valueOf(resultSet[4]) : null;
					String ecomGstin = resultSet[5] != null
							? String.valueOf(resultSet[5]) : null;
					BigDecimal taxable = resultSet[6] != null
							? new BigDecimal(String.valueOf(resultSet[6]))
							: BigDecimal.ZERO;
					BigDecimal igst = resultSet[7] != null
							? new BigDecimal(String.valueOf(resultSet[7]))
							: BigDecimal.ZERO;
					BigDecimal cgst = resultSet[8] != null
							? new BigDecimal(String.valueOf(resultSet[8]))
							: BigDecimal.ZERO;
					BigDecimal sgst = resultSet[9] != null
							? new BigDecimal(String.valueOf(resultSet[9]))
							: BigDecimal.ZERO;
					BigDecimal cess = resultSet[10] != null
							? new BigDecimal(String.valueOf(resultSet[10]))
							: BigDecimal.ZERO;
					supEcomInvoices.setEtin(ecomGstin);
					supEcomInvoices.setSuppval(taxable);
					supEcomInvoices.setIgst(igst);
					supEcomInvoices.setCgst(cgst);
					supEcomInvoices.setSgst(sgst);
					supEcomInvoices.setCess(cess);
					supEcomInvoices.setFlag(APIConstants.N);
					idsList.add(id);
					supEcomInvList.add(supEcomInvoices);
					if (GSTConstants.GSTR1_14I.equalsIgnoreCase(tableSection)) {
						clxList.addAll(supEcomInvList);
					} else if (GSTConstants.GSTR1_14II
							.equalsIgnoreCase(tableSection)) {
						paytxList.addAll(supEcomInvList);
					}

					if (gstr1 == null) {
						gstr1 = new SaveGstr1();
						gstr1.setSgstin(sGstin);
						gstr1.setTaxperiod(txPriod);
					}
				}
				supEcomDto.setClttx(clxList);
				supEcomDto.setPaytx(paytxList);
				gstr1.setSupEco(supEcomDto);
				batchesList.add(gstr1);
				batchIdsList.add(idsList);
				idsList = new ArrayList<>();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("SaveGstr1 Dto {} ", gstr1);
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
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("batch Dto {} ", batchDto);
		}
		return batchDto;
	}
}
