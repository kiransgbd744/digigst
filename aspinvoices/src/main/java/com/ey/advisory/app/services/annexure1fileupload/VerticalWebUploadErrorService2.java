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
import com.ey.advisory.admin.data.entities.client.Gstr2XExcelTcsTdsEntity;
import com.ey.advisory.app.data.entities.client.CewbExcelEntity;
import com.ey.advisory.app.data.entities.client.CrossItcAsEnteredEntity;
import com.ey.advisory.app.data.entities.client.Gstr9OutwardInwardAsEnteredEntity;
import com.ey.advisory.app.data.entities.client.VerticalWebErrorTable;
import com.ey.advisory.app.data.repositories.client.VerticalWebErrorRepo;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

@Component("VerticalWebUploadErrorService2")
public class VerticalWebUploadErrorService2 {

	@Autowired
	@Qualifier("VerticalWebErrorRepo")
	private VerticalWebErrorRepo ann1VerticalWebErrorRepo;

	public Map<String, List<VerticalWebErrorTable>> convertErrors(
			Map<String, List<ProcessingResult>> results, String valueType,
			Gstr1FileStatusEntity updateFileStatus) {

		Map<String, List<VerticalWebErrorTable>> map = new HashMap<>();
		results.keySet().stream().forEach(key -> {
			List<ProcessingResult> pResults = results.get(key);
			List<VerticalWebErrorTable> errors = new ArrayList<>();
			pResults.forEach(pr -> {
				// Instantiate the ent
				VerticalWebErrorTable error = new VerticalWebErrorTable();
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

	public void storedErrorGstr2xRecords(
			List<Gstr2XExcelTcsTdsEntity> saveStrucAll,
			Map<String, List<VerticalWebErrorTable>> errorMap) {

		saveStrucAll.forEach(errorRecords -> {
			String b2cKey = errorRecords.getDocKey();
			Long commanId = errorRecords.getId();
			String key = b2cKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<VerticalWebErrorTable> errList = errorMap.get(key);
			String invKey = errorRecords.getDocKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<VerticalWebErrorTable> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<VerticalWebErrorTable> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			ann1VerticalWebErrorRepo.saveAll(outError);
		}
	}

	public void storedCewbRecords(List<CewbExcelEntity> saveStrucAll,
			Map<String, List<VerticalWebErrorTable>> errorMap) {

		saveStrucAll.forEach(errorRecords -> {
			String cewbKey = errorRecords.getCewbInvKey();
			Long commanId = errorRecords.getId();
			String key = cewbKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<VerticalWebErrorTable> errList = errorMap.get(key);
			String invKey = errorRecords.getCewbInvKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<VerticalWebErrorTable> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<VerticalWebErrorTable> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			ann1VerticalWebErrorRepo.saveAll(outError);
		}
	}

	public void storedErrorCrossItcRecords(
			List<CrossItcAsEnteredEntity> strErrRecords,
			Map<String, List<VerticalWebErrorTable>> errorMap) {

		strErrRecords.forEach(errorRecords -> {
			String cewbKey = errorRecords.getCrossItcDocKey();
			Long commanId = errorRecords.getId();
			String key = cewbKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<VerticalWebErrorTable> errList = errorMap.get(key);
			String invKey = errorRecords.getCrossItcDocKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<VerticalWebErrorTable> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<VerticalWebErrorTable> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			ann1VerticalWebErrorRepo.saveAll(outError);
		}
	}

	public void storedErrorGstr9InOutwardRecords(
			List<Gstr9OutwardInwardAsEnteredEntity> strErrRecords,
			Map<String, List<VerticalWebErrorTable>> errorMap) {

		strErrRecords.forEach(errorRecords -> {
			String cewbKey = errorRecords.getGst9DocKey();
			Long commanId = errorRecords.getId();
			String key = cewbKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<VerticalWebErrorTable> errList = errorMap.get(key);
			String invKey = errorRecords.getGst9DocKey(); 
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<VerticalWebErrorTable> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<VerticalWebErrorTable> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
		if (!outError.isEmpty()) {
			ann1VerticalWebErrorRepo.saveAll(outError);
		}
	}
}
