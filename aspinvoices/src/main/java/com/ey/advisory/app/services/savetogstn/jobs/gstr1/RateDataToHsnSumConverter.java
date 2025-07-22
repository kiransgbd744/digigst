package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.master.HsnOrSacMasterEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.HsnOrSacRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.docs.dto.HSNSummaryInvData;
import com.ey.advisory.app.docs.dto.HSNSummaryInvoices;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("rateDataToHsnSumConverter")
public class RateDataToHsnSumConverter implements RateDataToGstr1Converter {

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	@Qualifier("HsnOrSacRepositoryMaster")
	private HsnOrSacRepository hsnMasterRepo;

	@Autowired
	private GstnApi gstnApi;

	@Autowired
	private EntityConfigPrmtRepository entityConfigPrmtRepo;

	@Autowired
	private GSTNDetailRepository gstinRepo;

	private static final List<String> REGYPE_IMPORTS = ImmutableList
			.of(GSTConstants.SEZD, GSTConstants.SEZU);

	private boolean isSEZSupplier(String groupCode, String supplierGstin) {

		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);
		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				supplierGstin);
		if (gstin != null) {
			if (gstin.getRegistrationType() != null) {
				String regType = gstin.getRegistrationType();
				if (REGYPE_IMPORTS.contains(regType.toUpperCase())) {
					return true;
				}
			}
		}
		return false;
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
				List<HSNSummaryInvData> hsninvListB2b = new ArrayList<>();
				List<HSNSummaryInvData> hsninvListB2c = new ArrayList<>();
				List<HSNSummaryInvData> hsninvList = new ArrayList<>();
				
				List<Long> idsList = new ArrayList<>();
				// Extra new logic to find the supplierGstin is SEZ status
				String supplierGstin = objects.get(0)[2] != null
						? String.valueOf(objects.get(0)[2])
						: null;
				boolean isSEZSupplier = isSEZSupplier(groupCode, supplierGstin);
				// Extra new logic to find the supplierGstin is SEZ status
				String TaxPeriod = objects.get(0)[1] != null
						? String.valueOf(objects.get(0)[1])
						: null;
				Boolean isRateIncludedInHsn = gstnApi
						.isRateIncludedInHsn(TaxPeriod);
				GSTNDetailEntity findByGstin = gstinRepo
						.findByGstinAndIsDeleteFalse(supplierGstin);
				String hsnDescAnswer = entityConfigPrmtRepo
						.findParamValByGroupCodeAndEntityIdAndIsDeleteFalse(
								groupCode, findByGstin.getEntityId(), "O25");
				for (int counter = 0; counter < totSize; counter++) {
					
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug("SaveBatchProcessDto objects {}", objects);
					}
					
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

					// first Object[]
					Long id = (Long) arr1[0];
					String taxPeriod = arr1[1] != null ? (String) arr1[1]
							: null;
					String sGstin = arr1[2] != null ? (String) arr1[2] : null;
					String itmHsnsac = arr1[3] != null ? (String) arr1[3]
							: null;
					String itmDec = arr1[4] != null ? (String) arr1[4] : null;
					
					String itmUniq = null;
					if (arr1[5] != null) {
						String stItemUnique = String.valueOf(arr1[5]);
						itmUniq = stItemUnique.contains("-")
								? stItemUnique.substring(0,
										stItemUnique.indexOf("-"))
								: stItemUnique;
					} else {
						itmUniq = "OTH";
					}

					// String itmUniq = arr1[5] != null ?
					// String.valueOf(arr1[5]).substring(0,
					// String.valueOf(arr1[5]).indexOf("-")) : "OTH";
					BigDecimal itmQty = arr1[6] != null ? (BigDecimal) arr1[6]
							: BigDecimal.ZERO;
					BigDecimal txableVal = arr1[7] != null
							? (BigDecimal) arr1[7]
							: BigDecimal.ZERO;
					BigDecimal igst = arr1[8] != null ? (BigDecimal) arr1[8]
							: null;
					BigDecimal cgst = arr1[9] != null ? (BigDecimal) arr1[9]
							: null;
					BigDecimal sgst = arr1[10] != null ? (BigDecimal) arr1[10]
							: null;
					BigDecimal cess = arr1[11] != null ? (BigDecimal) arr1[11]
							: null;
					BigDecimal totalVal = arr1[12] != null
							? (BigDecimal) arr1[12]
							: BigDecimal.ZERO;
					BigDecimal taxRate = arr1[13] != null
							? (BigDecimal) arr1[13]
							: BigDecimal.ZERO;
					String recordType = arr1[14] != null ? (String) arr1[14]
							: null;

					// object creation
					HSNSummaryInvData hsninv = new HSNSummaryInvData();
					// set HSNInvoiceData
					hsninv.setSerialNumber(counter2);
					hsninv.setHsnGoodsOrService(itmHsnsac);
					if ("A".equalsIgnoreCase(hsnDescAnswer)) {
						if (itmDec == null || Strings.isNullOrEmpty(itmDec)) {
							// with out like Operator
							HsnOrSacMasterEntity hsnMaster = hsnMasterRepo
									.findByHsnSac(itmHsnsac);
							itmDec = hsnMaster != null ? hsnMaster.getDescription()
									: null;
							// with Like operator
							if (itmDec == null) {
								List<String> itmDecs = hsnMasterRepo
										.findByHsnOrSacDesc(itmHsnsac);
								if(!itmDecs.isEmpty()){
									itmDec = itmDecs.get(0);
								} 
							}
						}
						
						if (itmDec != null && itmDec.length() > 30) {
							itmDec = itmDec.substring(0, 30);
						}
						hsninv.setUserDescOfGoodsSold(itmDec);
					}
					hsninv.setUnitOfMeasureOfGoodsSold(itmUniq);
					hsninv.setQtyOfGoodsSold(itmQty);
					hsninv.setTaxValOfGoodsOrService(txableVal);
					// Tax Rate and total value on conditon basis
					if (isRateIncludedInHsn) {
						hsninv.setTaxRate(taxRate);
					} else {
						hsninv.setTotalValue(totalVal);
					}
					if (isSEZSupplier) {
						hsninv.setIgstAmount(igst);
					} else {
						hsninv.setIgstAmount(igst);
						hsninv.setCgstAmount(cgst);
						hsninv.setSgstAmount(sgst);
					}
					hsninv.setCessAmount(cess);
					if (recordType != null) {
						if ("B2B".equalsIgnoreCase(recordType)
								|| "HSN_B2B".equalsIgnoreCase(recordType)) {
							hsninvListB2b.add(hsninv);
						} else {
							hsninvListB2c.add(hsninv);
						}
					}else {
						hsninvList.add(hsninv);
					}
					/**
					 * This ids are used to update the Gstr1_doc_header table as
					 * a single/same batch.
					 */
					idsList.add(id);

					if (/*
						 * (sGstin != null && !sGstin.equals(sGstin2)) ||
						 * (taxPeriod != null && !taxPeriod.equals(taxPeriod2))
						 * ||
						 */ counter2 == totSize) {

						HSNSummaryInvoices hsn = new HSNSummaryInvoices();
						if (taxDocType != null
								&& GSTConstants.CAN.equals(taxDocType)) {
							hsn.setFlag(APIConstants.D);// D-Delete
						}
						// hsn.setHsnSummaryInvData(hsninvList);
						if(recordType != null) {
						hsn.setHsnB2b(hsninvListB2b);
						hsn.setHsnB2c(hsninvListB2c);
						}else {
							 hsn.setHsnSummaryInvData(hsninvList);
							}
						LOGGER.info("New Batch with SGSTN {} and TaxPeriod {}",
								sGstin, taxPeriod);
						SaveGstr1 gstr1 = new SaveGstr1();
						/**
						 * set Save GSTR1 data
						 */
						gstr1.setSgstin(sGstin);
						gstr1.setTaxperiod(taxPeriod);
						gstr1.setHsnSummaryInvoices(hsn);
						batchesList.add(gstr1);
						batchIdsList.add(idsList);

						idsList = new ArrayList<>();
						hsninvListB2b = new ArrayList<>();
						hsninvListB2c = new ArrayList<>();

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
