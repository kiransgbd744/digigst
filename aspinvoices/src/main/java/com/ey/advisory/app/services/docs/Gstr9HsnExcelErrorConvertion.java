package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.Gstr2XVerticalErrorEntity;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr9HsnExcelErrorConvertion")
public class Gstr9HsnExcelErrorConvertion {

	@Autowired
	@Qualifier("Gstr9HsnExcelConvertion")
	private Gstr9HsnExcelConvertion gstr9ExcelConvertion;

	public List<Gstr2XVerticalErrorEntity> appendErroRecordsData(
			List<Object[]> objArray,
			Map<String, List<ProcessingResult>> processingResults,
			Gstr1FileStatusEntity updateFileStatus, Map<Long, String> hsnMap,
			List<Long> asEnterIds, int index) {
		List<Gstr2XVerticalErrorEntity> hsn = new ArrayList<Gstr2XVerticalErrorEntity>();
		int id = 0;
		for (Object[] obj : objArray) {
			Gstr2XVerticalErrorEntity hsnError = new Gstr2XVerticalErrorEntity();
			String hsnKey = gstr9ExcelConvertion.getFileProcessedKey(obj);

			// hsnError.setCommonId(asEnterIds.get(id));

			/*
			 * if (asEnterIds != null && !asEnterIds.isEmpty()) {
			 * asEnterIds.forEach(asIds -> {
			 * hsnError.setCommonId(asIds.get(Long.valueOf(id))); }); }
			 */

			if (asEnterIds != null && !asEnterIds.isEmpty()) {
				for (Long asEnterId : asEnterIds) {
					hsnError.setCommonId(asEnterId);
				}
			}
			hsnError.setInvKey(hsnKey);

			String fileType = null;
			if (updateFileStatus.getFileType() != null
					&& !updateFileStatus.getFileType().trim().isEmpty()) {
				fileType = updateFileStatus.getFileType();

			}
			hsnError.setTableType(fileType);
			if (updateFileStatus != null) {
				hsnError.setFileId(updateFileStatus.getId());
			}
			hsnError.setCreatedBy("System");
			// hsnError.setCreatedOn(LocalDate.now());
			hsnError.setErrorSource("WEBUPLOAD");

			String code = null;
			String desc = null;
			String errorField = null;
			List<ProcessingResult> list = processingResults.get(hsnKey);
			if (CollectionUtils.isNotEmpty(list)) {
				StringBuffer codeBuffer = new StringBuffer();
				StringBuffer descBuffer = new StringBuffer();
				StringBuffer errorFieldBuffer = new StringBuffer();
				list.stream().forEach(key -> {
					codeBuffer.append(key.getCode()).append(",");
					descBuffer.append(key.getDescription()).append(",");

					TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) key
							.getLocation();
					if (loc != null) {
						Object[] identifiers = loc.getFieldIdentifiers();
						for (int i = 0; i < identifiers.length; i++) {
							errorFieldBuffer.append(identifiers[i]).append(",");
						}
					}
				});
				code = codeBuffer.toString();
				desc = descBuffer.toString();
				errorField = errorFieldBuffer.toString();
			}
			hsnError.setErrorField((StringUtils.isEmpty(errorField)) ? ""
					: errorField.substring(0, errorField.length() - 1));
			hsnError.setErrorCode((StringUtils.isEmpty(code)) ? ""
					: code.substring(0, code.length() - 1));
			hsnError.setErrorDesc((StringUtils.isEmpty(desc)) ? ""
					: desc.substring(0, desc.length() - 1));
			hsn.add(hsnError);
			id++;
		}
		return hsn;
	}

	/*
	 * List<ProcessingResult> list = processingResults.get(hsnKey); if
	 * (CollectionUtils.isNotEmpty(list)) { list.forEach(result -> {
	 * hsnError.setErrorCode(result.getCode());
	 * hsnError.setErrorDesc(result.getDescription()); StringBuffer
	 * errorFieldBuffer = new StringBuffer();
	 * 
	 * TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc)
	 * result.getLocation(); if (loc != null) { Object[] identifiers =
	 * loc.getFieldIdentifiers(); for (int i = 0; i < identifiers.length; i++) {
	 * errorFieldBuffer.append(identifiers[i]).append(","); } }
	 * 
	 * hsnError.setErrorField(errorFieldBuffer.toString()); hsn.add(hsnError);
	 * }); } } return hsn; }
	 */

	public Long getKey(Map<Long, String> hsnMap, String hsnKey) {
		return hsnMap.entrySet().stream()
				.filter(e -> e.getValue().equals(hsnKey)).map(Map.Entry::getKey)
				.findFirst().orElse(null);
	}

	public String getHsnValues(Object[] obj) {
		String gstin = (obj[0] != null && !obj[0].toString().trim().isEmpty())
				? String.valueOf(obj[0]) : null;

		String fy = (obj[1] != null && !obj[1].toString().trim().isEmpty())
				? String.valueOf(obj[1]) : null;

		String tableNumber = (obj[2] != null
				&& !obj[2].toString().trim().isEmpty())
						? String.valueOf(obj[2]).trim() : null;

		String hsnL = (obj[3] != null && !obj[3].toString().trim().isEmpty())
				? String.valueOf(obj[3]) : null;

		String rate = (obj[5] != null && !obj[5].toString().trim().isEmpty())
				? String.valueOf(obj[5]) : null;

		String uqc = (obj[6] != null && !obj[6].toString().trim().isEmpty())
				? String.valueOf(obj[6]) : null;

		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(gstin).add(fy)
				.add(tableNumber).add(hsnL).add(rate).add(uqc).toString()
				.trim();
	}

}
