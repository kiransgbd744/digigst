package com.ey.advisory.app.services.docs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.Itc04HeaderErrorsEntity;
import com.ey.advisory.app.data.entities.client.Itc04ItemErrorsEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("SRFileToItc04TransDocErrConvertion")
@Slf4j
public class SRFileToItc04TransDocErrConvertion {

	/**
	 * 
	 * @param errDocMapObj
	 * @param documentKeyBuilder
	 * @param fileStatus
	 * @return
	 */
	public List<Itc04HeaderErrorsEntity> convertSRFileToItc04TransError(
			Map<String, List<Object[]>> errDocMapObj,
			DocumentKeyBuilder documentKeyBuilder,
			Gstr1FileStatusEntity fileStatus, String userName) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("SRFileToItc04TransDocErrConvertion "
					+ "convertSRFileToItc04TransError Begining");
		List<Itc04HeaderErrorsEntity> errorHeaders = new ArrayList<>();
		try {
			errDocMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<Object[]> objs = entry.getValue();
				Itc04HeaderErrorsEntity errorDocument = new Itc04HeaderErrorsEntity();
				List<Itc04ItemErrorsEntity> lineItems = new ArrayList<>();
				for (Object[] obj : objs) {
					Itc04ItemErrorsEntity lineItem = new Itc04ItemErrorsEntity();
					String tableNumber = getValue(obj[0]);
					String actionType = getValue(obj[1]);
					String fy = getValue(obj[2]);
					String returnPeriod = getValue(obj[3]);
					String sgstin = getValue(obj[4]);
					String deliveryChallanNo = getValue(obj[5]);
					String deliveryChallanDate = getValue(obj[6]);
					LocalDate localDeliverCallanDate = DateUtil
							.parseObjToDate(deliveryChallanDate);
					String jwDeliveryChallanNo = getValue(obj[7]);
					String jwDeliveryChallanDate = getValue(obj[8]);
					LocalDate localjwDeliveryChallanDate = DateUtil
							.parseObjToDate(jwDeliveryChallanDate);
					String goodsRecDate = getValue(obj[9]);
					LocalDate localGoodsRecDate = DateUtil
							.parseObjToDate(goodsRecDate);
					String invNumber = getValue(obj[10]);
					String invDate = getValue(obj[11]);
					LocalDate localInvDate = DateUtil.parseObjToDate(invDate);
					String jobWorkerGstin = getValue(obj[12]);
					String jobWorkerStateCode = getValue(obj[13]);
					String jobWorkerType = getValue(obj[14]);
					String jobWorkerId = getValue(obj[15]);
					String jobWorkerName = getValue(obj[16]);
					String typesOfGoods = getValue(obj[17]);

					String itemSerialNumber = getValue(obj[18]);
					String productDescription = getValue(obj[19]);
					String productCode = getValue(obj[20]);
					String natureOfJw = getValue(obj[21]);
					String hsn = getValue(obj[22]);
					String uqc = getValue(obj[23]);
					String quantity = getValue(obj[24]);
					String lossesUqc = getValue(obj[25]);
					String lossesQnt = getValue(obj[26]);
					String itemAccessableAmt = getValue(obj[27]);
					String igstRate = getValue(obj[28]);
					String igstAmt = getValue(obj[29]);
					String cgstRate = getValue(obj[30]);
					String cgstAmt = getValue(obj[31]);

					String sgstRate = getValue(obj[32]);
					String sgstAmt = getValue(obj[33]);
					String cessAdvRate = getValue(obj[34]);
					String cessAdvAmt = getValue(obj[35]);
					String cessSpcRate = getValue(obj[36]);
					String cessSpcAmt = getValue(obj[37]);
					String StateCeessAdvRate = getValue(obj[38]);
					String StateCeessAdvAmt = getValue(obj[39]);
					String StateCeessSpecRate = getValue(obj[40]);
					String StateCeessSpecAmt = getValue(obj[41]);
					String tatolValue = getValue(obj[42]);
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

					lineItem.setTableNumber(tableNumber);
					lineItem.setActionType(actionType);
					lineItem.setRetPeriod(returnPeriod);
					lineItem.setSupplierGstin(sgstin);
					lineItem.setFy(fy);
					lineItem.setDeliveryChallanaNumber(deliveryChallanNo);
					if (localDeliverCallanDate != null) {
						lineItem.setDeliveryChallanaDate(
								String.valueOf(localDeliverCallanDate));
					} else {
						lineItem.setDeliveryChallanaDate(deliveryChallanDate);
					}
					lineItem.setJwDeliveryChallanaNumber(jwDeliveryChallanNo);
					if (localjwDeliveryChallanDate != null) {
						lineItem.setJwDeliveryChallanaDate(
								String.valueOf(localjwDeliveryChallanDate));
					} else {
						lineItem.setJwDeliveryChallanaDate(
								jwDeliveryChallanDate);
					}
					if (localGoodsRecDate != null) {
						lineItem.setGoodsReceivingDate(
								String.valueOf(localGoodsRecDate));
					} else {
						lineItem.setGoodsReceivingDate(goodsRecDate);
					}
					lineItem.setInvNumber(invNumber);
					if (localInvDate != null) {
						lineItem.setInvDate(String.valueOf(localInvDate));
					} else {
						lineItem.setInvDate(invDate);
					}
					lineItem.setJobWorkerGstin(jobWorkerGstin);
					lineItem.setJobWorkerStateCode(jobWorkerStateCode);
					lineItem.setJobWorkerId(jobWorkerId);
					lineItem.setJobWorkerName(jobWorkerName);
					lineItem.setJobWorkerType(jobWorkerType);
					lineItem.setTypesOfGoods(typesOfGoods);
					lineItem.setItemSerialNumber(itemSerialNumber);
					lineItem.setProductDescription(productDescription);
					lineItem.setProductCode(productCode);
					lineItem.setNatureOfJw(natureOfJw);
					lineItem.setHsn(hsn);
					lineItem.setQnt(quantity);
					lineItem.setUqc(uqc);
					lineItem.setLossesQnt(lossesQnt);
					lineItem.setLossesUqc(lossesUqc);
					lineItem.setItemAccAmt(itemAccessableAmt);
					lineItem.setIgstRate(igstRate);
					lineItem.setIgstAmount(igstAmt);
					lineItem.setCgstRate(cgstRate);
					lineItem.setCgstAmount(cgstAmt);
					lineItem.setSgstRate(sgstRate);
					lineItem.setSgstAmount(sgstAmt);
					lineItem.setCessAdvaloremRate(cessAdvRate);
					lineItem.setCessAdvaloremAmount(cessAdvAmt);
					lineItem.setCessSpecificRate(cessSpcRate);
					lineItem.setCessSpecificAmount(cessSpcAmt);
					lineItem.setStateCessAdvaloremRate(StateCeessAdvRate);
					lineItem.setStateCessAdvaloremAmount(StateCeessAdvAmt);
					lineItem.setStateCessSpecificRate(StateCeessSpecRate);
					lineItem.setStateCessSpecificAmount(StateCeessSpecAmt);
					lineItem.setTotalValue(tatolValue);

					if (localPostingDate != null) {
						lineItem.setPostingDate(
								String.valueOf(localPostingDate));
					} else {
						lineItem.setPostingDate(postingDate);
					}
					lineItem.setUserId(userId);
					lineItem.setCompanyCode(companyCode);
					lineItem.setSourceIdentifier(sourceID);
					lineItem.setSourceFileName(sourceFileName);
					lineItem.setPlantCode(plantCode);
					lineItem.setDivision(division);
					lineItem.setProfitCentre1(profitCenter1);
					lineItem.setProfitCentre2(profitCenter2);
					lineItem.setAccountingVoucherNumber(accVocharNumber);

					if (localAccVocharDate != null) {
						lineItem.setAccountingVoucherDate(
								String.valueOf(localAccVocharDate));
					} else {
						lineItem.setAccountingVoucherDate(accVocharDate);
					}

					if (fileStatus != null) {
						lineItem.setAcceptanceId(fileStatus.getId());
						errorDocument.setAcceptanceId(fileStatus.getId());
					} else {
						errorDocument.setAcceptanceId(Long.valueOf(78340));
						lineItem.setAcceptanceId(Long.valueOf(78340));
					}
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
					lineItems.add(lineItem); // Add Line Items

					errorDocument.setTableNumber(tableNumber);
					errorDocument.setActionType(actionType);
					errorDocument.setRetPeriod(returnPeriod);
					errorDocument.setSupplierGstin(sgstin);
					errorDocument.setFy(fy);
					errorDocument.setDeliveryChallanaNumber(deliveryChallanNo);
					if (localDeliverCallanDate != null) {
						errorDocument.setDeliveryChallanaDate(
								String.valueOf(localDeliverCallanDate));
					} else {
						errorDocument
								.setDeliveryChallanaDate(deliveryChallanDate);
					}
					errorDocument
							.setJwDeliveryChallanaNumber(jwDeliveryChallanNo);
					if (localjwDeliveryChallanDate != null) {
						errorDocument.setJwDeliveryChallanaDate(
								String.valueOf(localjwDeliveryChallanDate));
					} else {
						errorDocument.setJwDeliveryChallanaDate(
								jwDeliveryChallanDate);
					}

					errorDocument.setInvNumber(invNumber);
					if (localInvDate != null) {
						errorDocument.setInvDate(String.valueOf(localInvDate));
					} else {
						errorDocument.setInvDate(invDate);
					}
					errorDocument.setJobWorkerGstin(jobWorkerGstin);
					errorDocument.setJobWorkerStateCode(jobWorkerStateCode);
					errorDocument.setJobWorkerId(jobWorkerId);
					errorDocument.setJobWorkerName(jobWorkerName);
					errorDocument.setJobWorkerType(jobWorkerType);
					if (localPostingDate != null) {
						errorDocument.setPostingDate(
								String.valueOf(localPostingDate));
					} else {
						errorDocument.setPostingDate(postingDate);
					}
					errorDocument.setUserId(userId);
					errorDocument.setCompanyCode(companyCode);
					errorDocument.setSourceIdentifier(sourceID);
					errorDocument.setSourceFileName(sourceFileName);
					errorDocument.setDivision(division);
					errorDocument.setProfitCentre1(profitCenter1);
					errorDocument.setProfitCentre2(profitCenter2);
					errorDocument.setAccountingVoucherNumber(accVocharNumber);
					String source = fileStatus.getSource();
					if (source != null && source.equalsIgnoreCase(
							JobStatusConstants.SFTP_WEB_UPLOAD)) {
						errorDocument.setDataOriginTypeCode(
								GSTConstants.DataOriginTypeCodes.SFTP
										.getDataOriginTypeCode());
					} else if (source != null && source
							.equalsIgnoreCase(JobStatusConstants.ERP)) {
						errorDocument.setDataOriginTypeCode(
								GSTConstants.DataOriginTypeCodes.ERP_API
										.getDataOriginTypeCode());
					} else {
						errorDocument.setDataOriginTypeCode(
								GSTConstants.DataOriginTypeCodes.EXCL_UPLOAD
										.getDataOriginTypeCode());
					}
					if (localAccVocharDate != null) {
						errorDocument.setAccountingVoucherDate(
								String.valueOf(localAccVocharDate));
					} else {
						errorDocument.setAccountingVoucherDate(accVocharDate);
					}

					if (idStr != null) {
						try {
							Long id = Long.parseLong(idStr);
							errorDocument.setId(id);
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
							errorDocument.setAcceptanceId(fileId);
						} catch (RuntimeException e) {
							throw new AppException(
									" Error occuured while converting "
											+ "String file id to Long  file id "
											+ e.getMessage());
						}
					}

					int maxlength = 99;
					if (key.length() > maxlength) {
						errorDocument.setDocKey(key.substring(0, maxlength));
					} else {
						errorDocument.setDocKey(key);
					}
				}
				errorDocument.setLineItems(lineItems);
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				errorDocument.setCreatedOn(convertNow);
				errorHeaders.add(errorDocument);

				lineItems.forEach(item -> {
					item.setDocument(errorDocument);
				});
			});

		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
		}
		LOGGER.error("SRFileToItc04TransDocErrConvertion "
				+ "convertSRFileToItc04TransError Endining");
		return errorHeaders;
	}

	private String getValue(Object obj) {
		return (obj != null && !obj.toString().trim().isEmpty())
				? String.valueOf(obj).trim() : null;
	}
}
