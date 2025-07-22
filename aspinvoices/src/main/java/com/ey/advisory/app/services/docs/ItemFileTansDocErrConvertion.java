package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
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
import com.ey.advisory.admin.services.onboarding.gstinfileupload.MasterDataToItemConverter;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.ProcessingResult;

/**
 * 
 * @author Anand3.M
 *
 */

@Service("ItemFileTansDocErrConvertion")
public class ItemFileTansDocErrConvertion {
	private final static String WEB_UPLOAD_KEY = "|";

	private static final Logger LOGGER = LoggerFactory.getLogger(ItemFileTansDocErrConvertion.class);

	@Autowired
	@Qualifier("MasterDataToItemConverter")
	private MasterDataToItemConverter masterDataToItemConverter;

	public List<MasterErrorEntity> convertItemFileTransDoc(List<Object[]> errRecords,
			Gstr1FileStatusEntity updateFileStatus, Map<String, List<ProcessingResult>> processingResults,
			Long entityId) {
		LOGGER.info("MasterItemFileConvertion " + "convertItemFileToError Begining");
		List<MasterErrorEntity> item = new ArrayList<MasterErrorEntity>();

		Map<String, List<Object[]>> filteredMap = new HashMap<>();
		errRecords.forEach(obj -> {
			String itemKey = masterDataToItemConverter.getItemValues(obj);

			if (filteredMap.containsKey(itemKey)) {
				List<Object[]> objArray = filteredMap.get(itemKey);
				objArray.add(obj);
				filteredMap.put(itemKey, objArray);
			} else {
				List<Object[]> objArray = new ArrayList<>();
				objArray.add(obj);
				filteredMap.put(itemKey, objArray);
			}
		});

		filteredMap.keySet().forEach(itemKey -> {
			List<Object[]> objArray = filteredMap.get(itemKey);
			if (objArray.size() > 1) {
				List<MasterErrorEntity> duplicateList = appendErroRecordsData(objArray, updateFileStatus,
						processingResults, entityId);
				duplicateList.forEach(dto -> {
					dto.setErrorCode("ERXXXX");
					dto.setErrorDec("Duplicate Records of " + itemKey);
				});
				item.addAll(duplicateList);
			} else {
				List<MasterErrorEntity> errorList = appendErroRecordsData(objArray, updateFileStatus, processingResults,
						entityId);
				item.addAll(errorList);
			}
		});
		return item;
	}

	private List<MasterErrorEntity> appendErroRecordsData(List<Object[]> objArray,
			Gstr1FileStatusEntity updateFileStatus, Map<String, List<ProcessingResult>> processingResults,
			Long entityId) {
		List<MasterErrorEntity> item = new ArrayList<MasterErrorEntity>();
		for (Object[] obj : objArray) {
			MasterErrorEntity ite = new MasterErrorEntity();

			String sgstin = (obj[0] != null) ? String.valueOf(obj[0].toString()) : null;
			String itemCode = (obj[1] != null) ? String.valueOf(obj[1].toString()) : null;

			String itemDescription = (obj[2] != null) ? String.valueOf(obj[2].toString()) : null;

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

			String eligIndicator = (obj[14] != null) ? String.valueOf(obj[14].toString()) : null;

			String perOfEligi = (obj[15] != null) ? String.valueOf(obj[15].toString()) : null;
			BigDecimal perOfElig = BigDecimal.ZERO;
			if (perOfEligi != null) {
				perOfElig = new BigDecimal(perOfEligi);
			}

			String comSuppIndi = (obj[16] != null) ? String.valueOf(obj[16].toString()) : null;
			String itcRevIden = (obj[17] != null) ? String.valueOf(obj[17].toString()) : null;

			String itcEntitlement = (obj[18] != null) ? String.valueOf(obj[18].toString()) : null;
			String itemKey = getItemValues(obj);

			StringJoiner totalRecord = new StringJoiner(",").add(sgstin).add(itemCode).add(itemDescription)
					.add(category).add(hsnSac).add(uom).add(reverseCharge).add(tds).add(differential).add(nilNonExmpt)
					.add(notificationNumber).add(notificationDate).add(circularDate).add(rate).add(eligIndicator)
					.add(perOfEligi).add(comSuppIndi).add(itcRevIden).add(itcEntitlement);

			ite.setErrorKey(itemKey);
			ite.setErrorRecords(totalRecord.toString());
			if (updateFileStatus != null) {
				ite.setMasterFileId(updateFileStatus.getId());
				ite.setFileName(updateFileStatus.getFileName());
				ite.setFileType(updateFileStatus.getFileType());
				ite.setStatus(updateFileStatus.getFileStatus());
			}
			ite.setCreatedBy("System");
			ite.setCreatedOn(LocalDateTime.now());
			
			
			String code = null;
			String desc = null;
			List<ProcessingResult> list = processingResults.get(itemKey);
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

			ite.setErrorCode((StringUtils.isEmpty(code)) ? "" : code.substring(0, code.length() - 1));
			ite.setErrorDec((StringUtils.isEmpty(desc)) ? "" : desc.substring(0, desc.length() - 1));
			ite.setEntityId(entityId);
			item.add(ite);
		}
		return item;
	}

	private String getItemValues(Object[] obj) {
		String sgstin = (obj[0] != null && !obj[0].toString().trim().isEmpty()) ? String.valueOf(obj[0]) : null;

		String hsnOrSac = (obj[4] != null && !obj[4].toString().trim().isEmpty()) ? String.valueOf(obj[4]) : null;
		String circularDate = (obj[12] != null) ? String.valueOf(obj[12]) : null;

		String rate = (obj[13] != null && !obj[13].toString().trim().isEmpty()) ? String.valueOf(obj[13]) : null;
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(hsnOrSac).add(circularDate).add(rate).toString();
	}
}
