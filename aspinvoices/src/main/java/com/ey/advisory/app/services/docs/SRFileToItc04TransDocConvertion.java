package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.entities.client.Itc04ItemEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("SRFileToItc04TransDocConvertion")
@Slf4j
public class SRFileToItc04TransDocConvertion {

	/**
	 * 
	 * @param errDocMapObj
	 * @param documentKeyBuilder
	 * @param fileStatus
	 * @return
	 */
	public List<Itc04HeaderEntity> convertSRFileToItc04TransDocs(
			Map<String, List<Object[]>> errDocMapObj,
			DocumentKeyBuilder documentKeyBuilder,
			Gstr1FileStatusEntity fileStatus, String userName) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("SRFileToItc04TransDocConvertion "
					+ "convertSRFileToItc04TransDocs Begining");
		List<Itc04HeaderEntity> errorHeaders = new ArrayList<>();
		try {
			errDocMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<Object[]> objs = entry.getValue();
				Itc04HeaderEntity documents = new Itc04HeaderEntity();
				List<Itc04ItemEntity> lineItems = new ArrayList<>();
				for (Object[] obj : objs) {
					Itc04ItemEntity lineItem = new Itc04ItemEntity();
					String tableNumber = getValue(obj[0]);
					String actionType = getValue(obj[1]);
					String fy = getValue(obj[2]);
					String returnPeriod = getValue(obj[3]);
					String sgstin = getValue(obj[4]);
					String deliveryChallanNo = getValue(obj[5]);
					String deliveryChallanDate = getValue(obj[6]);
					LocalDate localDeliverCallanDate = DateUtil
							.parseObjToDate(deliveryChallanDate);
					String finYearOfDCD = GenUtil
							.getFinYear(localDeliverCallanDate);
					String jwDeliveryChallanNo = getValue(obj[7]);
					String jwDeliveryChallanDate = getValue(obj[8]);
					LocalDate localjwDeliveryChallanDate = DateUtil
							.parseObjToDate(jwDeliveryChallanDate);
					String finYearOfJWDCD = GenUtil
							.getFinYear(localjwDeliveryChallanDate);
					String goodsRecDate = getValue(obj[9]);
					LocalDate localGoodsRecDate = DateUtil
							.parseObjToDate(goodsRecDate);
					String invNumber = getValue(obj[10]);
					String invDate = getValue(obj[11]);
					LocalDate localInvDate = DateUtil.parseObjToDate(invDate);
					String finYearOfInvD = GenUtil.getFinYear(localInvDate);
					String jobWorkerGstin = getValue(obj[12]);
					String jobWorkerStateCode = getValue(obj[13]);
					String jobWorkerType = getValue(obj[14]);
					String jobWorkerId = getValue(obj[15]);
					String jobWorkerName = getValue(obj[16]);
					String typesOfGoods = getValue(obj[17]);

					String itemSerialNumber = getValue(obj[18]);
					/*
					 * Integer intItemSerialNumber = null; if (itemSerialNumber
					 * != null && !itemSerialNumber.trim().isEmpty()) {
					 * intItemSerialNumber = Integer
					 * .parseInt(itemSerialNumber); }
					 */

					String productDescription = getValue(obj[19]);
					String productCode = getValue(obj[20]);
					String natureOfJw = getValue(obj[21]);
					String hsn = getValue(obj[22]);
					// Integer hsn = Integer.parseInt(getValue(obj[22]));
					String uqc = getValue(obj[23]);
					String quantity = getValue(obj[24]);
					BigDecimal qty = null;
					if (quantity != null && !quantity.trim().isEmpty()) {
						qty = new BigDecimal(quantity.trim());
						lineItem.setQnt(qty);
					}

					String lossesUqc = getValue(obj[25]);
					String lossesQnt = getValue(obj[26]);
					BigDecimal dLossesQnt = null;
					if (lossesQnt != null && !lossesQnt.trim().isEmpty()) {
						dLossesQnt = new BigDecimal(lossesQnt.trim());
						lineItem.setLossesQnt(dLossesQnt);
					}
					String itemAccessableAmt = getValue(obj[27]);
					BigDecimal dItemAccessableAmt = null;
					if (itemAccessableAmt != null
							&& !itemAccessableAmt.trim().isEmpty()) {
						dItemAccessableAmt = new BigDecimal(
								itemAccessableAmt.trim());
						lineItem.setTaxableValue(dItemAccessableAmt);
					}
					String igstRate = getValue(obj[28]);
					BigDecimal dIgstRate = null;
					if (igstRate != null && !igstRate.trim().isEmpty()) {
						dIgstRate = new BigDecimal(igstRate.trim());
						lineItem.setIgstRate(dIgstRate);
					}
					String igstAmt = getValue(obj[29]);
					BigDecimal dIgstAmt = null;
					if (igstAmt != null && !igstAmt.trim().isEmpty()) {
						dIgstAmt = new BigDecimal(igstAmt.trim());
						lineItem.setIgstAmount(dIgstAmt);
					}
					String cgstRate = getValue(obj[30]);
					BigDecimal dCgstRate = null;
					if (cgstRate != null && !cgstRate.trim().isEmpty()) {
						dCgstRate = new BigDecimal(cgstRate.trim());
						lineItem.setCgstRate(dCgstRate);
					}
					String cgstAmt = getValue(obj[31]);
					BigDecimal dCgstAmt = null;
					if (cgstAmt != null && !cgstAmt.trim().isEmpty()) {
						dCgstAmt = new BigDecimal(cgstAmt.trim());
						lineItem.setCgstAmount(dCgstAmt);
					}

					String sgstRate = getValue(obj[32]);
					BigDecimal dSgstRate = null;
					if (sgstRate != null && !sgstRate.trim().isEmpty()) {
						dSgstRate = new BigDecimal(sgstRate.trim());
						lineItem.setSgstRate(dSgstRate);
					}
					String sgstAmt = getValue(obj[33]);
					BigDecimal dSgstAmt = null;
					if (sgstAmt != null && !sgstAmt.trim().isEmpty()) {
						dSgstAmt = new BigDecimal(sgstAmt.trim());
						lineItem.setSgstAmount(dSgstAmt);
					}
					String cessAdvRate = getValue(obj[34]);
					BigDecimal dCessRate = null;
					if (cessAdvRate != null && !cessAdvRate.trim().isEmpty()) {
						dCessRate = new BigDecimal(cessAdvRate.trim());
						lineItem.setCessAdvaloremRate(dCessRate);
					}
					String cessAdvAmt = getValue(obj[35]);
					BigDecimal dCessAdvAmt = null;
					if (cessAdvAmt != null && !cessAdvAmt.trim().isEmpty()) {
						dCessAdvAmt = new BigDecimal(cessAdvAmt.trim());
						lineItem.setCessAdvaloremAmount(dCessAdvAmt);
					}
					String cessSpcRate = getValue(obj[36]);
					BigDecimal dCessSpcRate = null;
					if (cessSpcRate != null && !cessSpcRate.trim().isEmpty()) {
						dCessSpcRate = new BigDecimal(cessSpcRate.trim());
						lineItem.setCessSpecificRate(dCessSpcRate);
					}
					String cessSpcAmt = getValue(obj[37]);
					BigDecimal dCessSpcAmt = null;
					if (cessSpcAmt != null && !cessSpcAmt.trim().isEmpty()) {
						dCessSpcAmt = new BigDecimal(cessSpcAmt.trim());
						lineItem.setCessSpecificAmount(dCessSpcAmt);
					}
					String stateCeessAdvRate = getValue(obj[38]);
					BigDecimal dStateCeessAdvRate = null;
					if (stateCeessAdvRate != null
							&& !stateCeessAdvRate.trim().isEmpty()) {
						dStateCeessAdvRate = new BigDecimal(
								stateCeessAdvRate.trim());
						lineItem.setStateCessAdvaloremRate(dStateCeessAdvRate);
					}
					String stateCeessAdvAmt = getValue(obj[39]);
					BigDecimal dStateCeessAdvAmt = null;
					if (stateCeessAdvAmt != null
							&& !stateCeessAdvAmt.trim().isEmpty()) {
						dStateCeessAdvAmt = new BigDecimal(
								stateCeessAdvAmt.trim());
						lineItem.setStateCessAdvaloremAmount(dStateCeessAdvAmt);
					}
					String stateCeessSpecRate = getValue(obj[40]);
					BigDecimal dStateCeessSpecRate = null;
					if (stateCeessSpecRate != null
							&& !stateCeessSpecRate.trim().isEmpty()) {
						dStateCeessSpecRate = new BigDecimal(
								stateCeessSpecRate.trim());
						lineItem.setStateCessSpecificRate(dStateCeessSpecRate);
					}
					String stateCeessSpecAmt = getValue(obj[41]);
					BigDecimal dStateCeessSpecAmt = null;
					if (stateCeessSpecAmt != null
							&& !stateCeessSpecAmt.trim().isEmpty()) {
						dStateCeessSpecAmt = new BigDecimal(
								stateCeessSpecAmt.trim());
						lineItem.setStateCessSpecificAmount(dStateCeessSpecAmt);
					}
					String tatolValue = getValue(obj[42]);
					BigDecimal dTatolValue = null;
					if (tatolValue != null && !tatolValue.trim().isEmpty()) {
						dTatolValue = new BigDecimal(tatolValue.trim());
						lineItem.setTotalValue(dTatolValue);
					}

					String postingDate = getValue(obj[43]);
					LocalDate localPostingDate = DateUtil
							.parseObjToDate(postingDate);
					String userId = getValue(obj[44]);
					String companyCode = getValue(obj[45]);

					String sourceID = getValue(obj[46]);
					String sourceFileName = getValue(obj[47]);
					String plantCode = getValue(obj[48]);
					String division = getValue(obj[49]);
					String profitCenter1 = getValue(obj[50]);
					String profitCenter2 = getValue(obj[51]);
					String accVocharNumber = getValue(obj[52]);
					String accVocharDate = getValue(obj[53]);
					LocalDate localAccVocharDate = DateUtil
							.parseObjToDate(accVocharDate);
					String userDef1 = getValue(obj[54]);
					String userDef2 = getValue(obj[55]);
					String userDef3 = getValue(obj[56]);
					String userDef4 = getValue(obj[57]);
					String userDef5 = getValue(obj[58]);
					String userDef6 = getValue(obj[59]);

					String userDef7 = getValue(obj[60]);
					String userDef8 = getValue(obj[61]);
					String userDef9 = getValue(obj[62]);
					String userDef10 = getValue(obj[63]);

					/**
					 * Start - Outward Document Structural Validation Error
					 * Correction Implementation
					 */
					// File ID and Id are not null only in case of Outward Doc
					// Structural Validation Error Correction
					String idStr = null;
					String fileIdStr = null;
					if (obj.length > 64) {
						idStr = (obj[64] != null
								&& !obj[64].toString().isEmpty())
										? String.valueOf(obj[64]) : null;
						fileIdStr = (obj[65] != null
								&& !obj[65].toString().isEmpty())
										? String.valueOf(obj[65]) : null;
					}
					/**
					 * End - Outward Document Structural Validation Error
					 * Correction Implementation
					 */

					lineItem.setRetPeriod(returnPeriod);
					lineItem.setGoodsReceivingDate(localGoodsRecDate);
					lineItem.setTypeOfGoods(typesOfGoods);
					lineItem.setItemSerialNumber(itemSerialNumber);
					lineItem.setProductDescription(productDescription);
					lineItem.setProductCode(productCode);
					lineItem.setNatureOfJw(natureOfJw);
					lineItem.setHsn(hsn);
					lineItem.setUqc(uqc);
					lineItem.setLossesUqc(lossesUqc);
					lineItem.setPlantCode(plantCode);
					lineItem.setUserdefinedfield1(userDef1);
					lineItem.setUserdefinedfield2(userDef2);
					lineItem.setUserdefinedfield3(userDef3);
					lineItem.setUserDefinedField4(userDef4);
					lineItem.setUserdefinedfield5(userDef5);
					lineItem.setUserdefinedfield6(userDef6);
					lineItem.setUserdefinedfield7(userDef7);
					lineItem.setUserDefinedField8(userDef8);
					lineItem.setUserdefinedfield9(userDef9);
					lineItem.setUserdefinedfield10(userDef10);
					if (fileStatus != null) {
						documents.setFileId(fileStatus.getId());
						lineItem.setFileId(fileStatus.getId());
					}
					lineItems.add(lineItem); // Add Line Items

					documents.setTableNumber(tableNumber);
					documents.setActionType(actionType);
					documents.setRetPeriod(returnPeriod);
					documents.setSupplierGstin(sgstin);
					documents.setFinYear(fy);
					documents.setFyDcDate(finYearOfDCD);
					documents.setFyjwDcDate(finYearOfJWDCD);
					documents.setFyInvDate(finYearOfInvD);
					documents.setDeliveryChallanaNumber(deliveryChallanNo);
					documents.setDeliveryChallanaDate(localDeliverCallanDate);
					documents.setJwDeliveryChallanaNumber(jwDeliveryChallanNo);
					documents.setJwDeliveryChallanaDate(
							localjwDeliveryChallanDate);
					documents.setInvNumber(invNumber);
					documents.setInvDate(localInvDate);
					documents.setJobWorkerGstin(jobWorkerGstin);
					documents.setJobWorkerStateCode(jobWorkerStateCode);
					documents.setJobWorkerId(jobWorkerId);
					documents.setJobWorkerName(jobWorkerName);
					documents.setJobWorkerType(jobWorkerType);
					documents.setPostingDate(localPostingDate);
					documents.setUserId(userId);
					documents.setCompanyCode(companyCode);
					documents.setSourceIdentifier(sourceID);
					documents.setSourceFileName(sourceFileName);
					documents.setDivision(division);
					documents.setProfitCentre1(profitCenter1);
					documents.setProfitCentre2(profitCenter2);
					documents.setAccountingVoucherNumber(accVocharNumber);
					documents.setAccountingVoucherDate(localAccVocharDate);
					String source = fileStatus.getSource();
					if (source != null && source.equalsIgnoreCase(
							JobStatusConstants.SFTP_WEB_UPLOAD)) {
						documents.setDataOriginTypeCode(
								GSTConstants.DataOriginTypeCodes.SFTP
										.getDataOriginTypeCode());
					} else if (source != null && source
							.equalsIgnoreCase(JobStatusConstants.ERP)) {
						documents.setDataOriginTypeCode(
								GSTConstants.DataOriginTypeCodes.ERP_API
										.getDataOriginTypeCode());
					} else {
						documents.setDataOriginTypeCode(
								GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
										.getDataOriginTypeCode());
					}
					if (idStr != null) {
						try {
							Long id = Long.parseLong(idStr);
							documents.setId(id);
						} catch (RuntimeException e) {
							throw new AppException(
									" Error occured while converting "
											+ "String id to Long id "
											+ e.getMessage());
						}
					}

					if (fileIdStr != null) {
						try {
							Long fileId = Long.parseLong(fileIdStr);
							documents.setFileId(fileId);
						} catch (RuntimeException e) {
							throw new AppException(
									" Error occuured while converting "
											+ "String file id to Long  file id "
											+ e.getMessage());
						}
					}

					int maxlength = 99;
					if (key.length() > maxlength) {
						documents.setDocKey(key.substring(0, maxlength));
					} else {
						documents.setDocKey(key);
					}
				}
				documents.setLineItems(lineItems);
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				documents.setCreatedDate(convertNow);
				errorHeaders.add(documents);

				lineItems.forEach(item -> {
					item.setDocument(documents);
				});
			});

		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
		}
		LOGGER.error("SRFileToItc04TransDocConvertion "
				+ "convertSRFileToItc04TransDocs Endining");
		return errorHeaders;
	}

	private String getValue(Object obj) {
		return (obj != null && !obj.toString().trim().isEmpty())
				? String.valueOf(obj).trim() : null;
	}
}
