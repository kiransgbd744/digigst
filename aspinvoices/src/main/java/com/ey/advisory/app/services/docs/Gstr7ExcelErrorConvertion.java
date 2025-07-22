package com.ey.advisory.app.services.docs;

import java.time.LocalDate;
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
import com.ey.advisory.app.data.entities.client.Gstr7DocErrorEntity;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Anand3.M
 *
 */

@Service("Gstr7ExcelErrorConvertion")
public class Gstr7ExcelErrorConvertion {

	@Autowired
	@Qualifier("Gstr7ExcelConvertion")
	private Gstr7ExcelConvertion gstr7ExcelConvertion;

	public List<Gstr7DocErrorEntity> appendErroRecordsData(List<Object[]> objArray,
			Map<String, List<ProcessingResult>> processingResults, Gstr1FileStatusEntity updateFileStatus,
			Map<Long, String> tdsMap, List<Long> asEnterIds, int index) {
		List<Gstr7DocErrorEntity> tds = new ArrayList<Gstr7DocErrorEntity>();
		for (Object[] obj : objArray) {
			Gstr7DocErrorEntity tdsError = new Gstr7DocErrorEntity();
			String tdsKey = gstr7ExcelConvertion.getFileProcessedKey(obj);
			tdsError.setDocHeaderId(asEnterIds.get(index));
			tdsError.setInvKey(tdsKey);
			if (updateFileStatus != null) {
				tdsError.setFileId(updateFileStatus.getId());
			}
			tdsError.setCreatedBy("System");
			tdsError.setCreatedOn(LocalDate.now());
			tdsError.setErrorSource("WEBUPLOAD");

			String code = null;
			String desc = null;
			String errorField = null;
			List<ProcessingResult> list = processingResults.get(tdsKey);
			if (CollectionUtils.isNotEmpty(list)) {
				StringBuffer codeBuffer = new StringBuffer();
				StringBuffer descBuffer = new StringBuffer();
				StringBuffer errorFieldBuffer = new StringBuffer();
				list.stream().forEach(key -> {
					codeBuffer.append(key.getCode()).append(",");
					descBuffer.append(key.getDescription()).append(",");

					TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) key.getLocation();
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
			tdsError.setErrorField(
					(StringUtils.isEmpty(errorField)) ? "" : errorField.substring(0, errorField.length() - 1));
			tdsError.setErrorCode((StringUtils.isEmpty(code)) ? "" : code.substring(0, code.length() - 1));
			tdsError.setErrorDescription((StringUtils.isEmpty(desc)) ? "" : desc.substring(0, desc.length() - 1));
			tds.add(tdsError);
		}
		return tds;
	}

	public Long getKey(Map<Long, String> tdsMap, String tdsKey) {
		return tdsMap.entrySet().stream().filter(e -> e.getValue().equals(tdsKey)).map(Map.Entry::getKey).findFirst()
				.orElse(null);
	}

	

}
