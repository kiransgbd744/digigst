package com.ey.advisory.app.services.docs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.MasterErrorEntity;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.MasterDataToProductConverter;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.ProcessingResult;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("ProductFileTansDocErrConvertion")
public class ProductFileTansDocErrConvertion {
	private final static String WEB_UPLOAD_KEY = "|";

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductFileTansDocErrConvertion.class);

	@Autowired
	@Qualifier("MasterDataToProductConverter")
	private MasterDataToProductConverter masterDataToProductConverter;

	public List<MasterErrorEntity> convertProductFileTransDoc(List<Object[]> errRecords,
			Gstr1FileStatusEntity updateFileStatus, Map<String, List<ProcessingResult>> processingResults,
			Long entityId) {
		LOGGER.info("MasterProductFileConvertion " + "convertProductFileToError Begining");
		List<MasterErrorEntity> product = new ArrayList<MasterErrorEntity>();

		Map<String, List<Object[]>> filteredMap = new HashMap<>();
		errRecords.forEach(obj -> {
			String prodKey = masterDataToProductConverter.getProductValues(obj);

			if (filteredMap.containsKey(prodKey)) {
				List<Object[]> objArray = filteredMap.get(prodKey);
				objArray.add(obj);
				filteredMap.put(prodKey, objArray);
			} else {
				List<Object[]> objArray = new ArrayList<>();
				objArray.add(obj);
				filteredMap.put(prodKey, objArray);
			}
		});

		filteredMap.keySet().forEach(prodKey -> {
			List<Object[]> objArray = filteredMap.get(prodKey);
			if (objArray.size() > 1) {
				List<MasterErrorEntity> duplicateList = appendErroRecordsData(objArray, updateFileStatus,
						processingResults, entityId);
				duplicateList.forEach(dto -> {
					dto.setErrorCode("ERXXXX");
					dto.setErrorDec("Duplicate Records of " + prodKey);
				});
				product.addAll(duplicateList);
			} else {
				List<MasterErrorEntity> errorList = appendErroRecordsData(objArray, updateFileStatus, processingResults,
						entityId);
				product.addAll(errorList);
			}
		});
		return product;
	}

	private List<MasterErrorEntity> appendErroRecordsData(List<Object[]> objArray,
			Gstr1FileStatusEntity updateFileStatus, Map<String, List<ProcessingResult>> processingResults,
			Long entityId) {
		List<MasterErrorEntity> product = new ArrayList<MasterErrorEntity>();
		for (Object[] obj : objArray) {
			MasterErrorEntity prod = new MasterErrorEntity();

			String sgstin = (obj[0] != null) ? String.valueOf(obj[0].toString()) : null;
			String productCode = (obj[1] != null) ? String.valueOf(obj[1].toString()) : null;

			String productDescription = (obj[2] != null) ? String.valueOf(obj[2].toString()) : null;

			String category = (obj[3] != null) ? String.valueOf(obj[3].toString()) : null;

			String hsnSac = (obj[4] != null) ? String.valueOf(obj[4].toString()) : null;

			String uom = (obj[5] != null) ? String.valueOf(obj[5].toString()) : null;
			String reverseCharge = (obj[6] != null) ? String.valueOf(obj[6].toString()) : null;
			String tds = (obj[7] != null) ? String.valueOf(obj[7].toString()) : null;

			String differential = (obj[8] != null) ? String.valueOf(obj[8].toString()) : null;

			String nilNonExmpt = (obj[9] != null) ? String.valueOf(obj[9].toString()) : null;

			String notificationNumber = (obj[10] != null) ? String.valueOf(obj[10].toString()) : null;

			String notificationDate = (obj[11] != null) ? String.valueOf(obj[11]) : null;
			LocalDate notificationDateLocal = DateUtil.parseObjToDate(notificationDate);

			String circularDate = (obj[12] != null) ? String.valueOf(obj[12]) : null;
			LocalDate circularDateLocal = DateUtil.parseObjToDate(circularDate);
			String rate = (obj[13] != null) ? String.valueOf(obj[13].toString()) : null;

			String itcFlag = (obj[14] != null) ? String.valueOf(obj[14].toString()) : null;
			String productKey = getProductValues(obj);

			StringJoiner totalRecord = new StringJoiner(",").add(sgstin).add(productCode).add(productDescription)
					.add(category).add(hsnSac).add(uom).add(reverseCharge).add(tds).add(differential).add(nilNonExmpt)
					.add(notificationNumber).add(notificationDate).add(circularDate).add(rate).add(itcFlag);

			prod.setErrorKey(productKey);
			prod.setErrorRecords(totalRecord.toString());
			if (updateFileStatus != null) {
				prod.setMasterFileId(updateFileStatus.getId());
				prod.setFileName(updateFileStatus.getFileName());
				prod.setFileType(updateFileStatus.getFileType());
				prod.setStatus(updateFileStatus.getFileStatus());
			}
			prod.setCreatedBy("System");
			prod.setCreatedOn(LocalDateTime.now());

			String code = null;
			String desc = null;
			List<ProcessingResult> list = processingResults.get(productKey);
			if (CollectionUtils.isNotEmpty(list)) {
				StringBuffer codeBuffer = new StringBuffer();
				StringBuffer descBuffer = new StringBuffer();
				list.stream().forEach(key -> {
					codeBuffer.append(key.getCode()).append(",");
					descBuffer.append(key.getDescription()).append(",");
				});
				code = codeBuffer.toString();
				desc = descBuffer.toString();
			}

			prod.setErrorCode((StringUtils.isEmpty(code)) ? "" : code.substring(0, code.length() - 1));
			prod.setErrorDec((StringUtils.isEmpty(desc)) ? "" : desc.substring(0, desc.length() - 1));
			prod.setEntityId(entityId);
			product.add(prod);
		}
		return product;
	}

	public String getProductValues(Object[] obj) {
		String sgstin = (obj[0] != null && !obj[0].toString().trim().isEmpty()) ? String.valueOf(obj[0]) : null;

		String hsnOrSac = (obj[4] != null && !obj[4].toString().trim().isEmpty()) ? String.valueOf(obj[4]) : null;
		String circularDate = (obj[12] != null) ? String.valueOf(obj[12]) : null;

		String rate = (obj[13] != null && !obj[13].toString().trim().isEmpty()) ? String.valueOf(obj[13]) : null;
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(hsnOrSac).add(circularDate).add(rate).toString();
	}

}
