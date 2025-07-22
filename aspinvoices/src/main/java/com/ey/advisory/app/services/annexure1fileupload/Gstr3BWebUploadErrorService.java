package com.ey.advisory.app.services.annexure1fileupload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.Gstr3bExcelEntity;
import com.ey.advisory.app.data.entities.client.Gstr3bVerticalWebError;
import com.ey.advisory.app.data.repositories.client.Gstr3bVerticalWebErrorRepo;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

@Component("Gstr3BWebUploadErrorService")
public class Gstr3BWebUploadErrorService {

	@Autowired
	@Qualifier("Gstr3bVerticalWebErrorRepo")
	private Gstr3bVerticalWebErrorRepo gstr3bVerticalWebErrorRepo; 

	public Map<String, List<Gstr3bVerticalWebError>> convertErrors(
			Map<String, List<ProcessingResult>> results, String valueType,
			Gstr1FileStatusEntity updateFileStatus) {

		Map<String, List<Gstr3bVerticalWebError>> map = new HashMap<>();
		results.keySet().stream().forEach(key -> {
			List<ProcessingResult> pResults = results.get(key); 
			List<Gstr3bVerticalWebError> errors = new ArrayList<>();
			pResults.forEach(pr -> {
				// Instantiate the ent
				Gstr3bVerticalWebError error = new Gstr3bVerticalWebError();
				TransDocProcessingResultLoc loc = (TransDocProcessingResultLoc) pr
						.getLocation();
				if (updateFileStatus != null) {

					if (null != loc) { // In case of bifurcation failure, loc is
										// null
						Object[] arr = loc.getFieldIdentifiers();
						String[] fields = Arrays.copyOf(arr, arr.length,
								String[].class);
						String errField = StringUtils.join(fields, ',');
						error.setErrorField(errField);
					}
					String fileType = null;
					if (updateFileStatus.getFileType() != null
							&& !updateFileStatus.getFileType().trim()
									.isEmpty()) {
						fileType = updateFileStatus.getFileType();

					}
					String userName = null;
					if (updateFileStatus.getUpdatedBy() != null
							&& !updateFileStatus.getUpdatedBy().trim()
									.isEmpty()) {
						userName = updateFileStatus.getUpdatedBy();

					}
					String source = null;
					if (updateFileStatus.getSource() != null
							&& !updateFileStatus.getSource().trim().isEmpty()) {
						source = updateFileStatus.getSource();

					}
					long fileId = 0;
					if (updateFileStatus.getId() != 0) {
						fileId = updateFileStatus.getId();
					}

					error.setErrorSource(source);
					error.setTableType(fileType);
					error.setValueType(valueType);
					error.setErrorCode(pr.getCode());
					error.setErrorDesc(pr.getDescription());
					error.setCreatedBy(userName);
					error.setErrorType(pr.getType().toString());
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					error.setCreatedDate(convertNow);
					error.setFileId(fileId);

					errors.add(error);
				}
			});
			map.put(key, errors);
		});
		return map;
	}

	public void storedGstr3bErrorRecords(
			List<Gstr3bExcelEntity> strucErrorRecords,
			Map<String, List<Gstr3bVerticalWebError>> errorMap) {
		strucErrorRecords.forEach(errorRecords -> {
			String invoiceKey = errorRecords.getInvKey();
			Long commanId = errorRecords.getId();
			String key = invoiceKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr3bVerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getInvKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr3bVerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr3bVerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			gstr3bVerticalWebErrorRepo.saveAll(outError);
		}
	}
	public void storedGstr3bDuplicateRecords(
			List<Gstr3bExcelEntity> duplicateErrorRecords,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr3bVerticalWebError> err = new ArrayList<>();
		Gstr3bVerticalWebError error = null;
		for (Gstr3bExcelEntity dupl : duplicateErrorRecords) {
			error = new Gstr3bVerticalWebError();
			error.setInvKey(dupl.getInvKey());
			error.setCommanId(dupl.getId());
			error.setFileId(updateFileStatus != null
					? Long.valueOf(updateFileStatus.getId()) : null);
			error.setCreatedBy(updateFileStatus != null
					? updateFileStatus.getUpdatedBy() : "SYSTEM");
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			error.setCreatedDate(convertNow);
			error.setErrorCode("ER6111");
			error.setErrorType("ERROR");
			error.setErrorDesc("Duplicate Records");
			error.setErrorSource(updateFileStatus != null
					? updateFileStatus.getSource() : "");
			error.setTableType(updateFileStatus != null
					? updateFileStatus.getFileType() : "");
			error.setValueType("BV");
			err.add(error);
		}
        if(err != null && !err.isEmpty()){
        	gstr3bVerticalWebErrorRepo.saveAll(err);
        }
	}
}