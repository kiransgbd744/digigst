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
import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.app.data.entities.client.Gstr6VerticalWebError;
import com.ey.advisory.app.data.repositories.client.Gstr6VerticalWebErrorRepo;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr6VerticalWebUploadErrorService")
@Slf4j
public class Gstr6VerticalWebUploadErrorService {

	@Autowired
	@Qualifier("Gstr6VerticalWebErrorRepo")
	private Gstr6VerticalWebErrorRepo gstr6VerticalWebErrorRepo;
	
	public Map<String, List<Gstr6VerticalWebError>> convertErrors(
			Map<String, List<ProcessingResult>> results, String valueType,
			Gstr1FileStatusEntity updateFileStatus) {

		Map<String, List<Gstr6VerticalWebError>> map = new HashMap<>();
		results.keySet().stream().forEach(key -> {
			List<ProcessingResult> pResults = results.get(key);
			List<Gstr6VerticalWebError> errors = new ArrayList<>();
			pResults.forEach(pr -> {
				// Instantiate the ent
				Gstr6VerticalWebError error = new Gstr6VerticalWebError();
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
				//	error.setTableType(fileType);
					error.setValueType(valueType);
					error.setErrorCode(pr.getCode());
					error.setErrorDesc(pr.getDescription());
					error.setCreatedBy(userName);
					error.setErrorType(pr.getType().toString());
				/*	LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());*/
					
					error.setCreatedDate(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					error.setFileId(fileId);

					errors.add(error);
				}
			});
			map.put(key, errors);
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("After Freeze CAN in Gstr6VerticalWebUploadErrorService");
			LOGGER.debug(map.toString());
	}
		return map;
	}
	public void storedErrorRecords(List<Gstr6DistributionExcelEntity> saveStrucAll,
			Map<String, List<Gstr6VerticalWebError>> errorMap) {

		saveStrucAll.forEach(errorRecords -> {
			String b2cKey = errorRecords.getProcessKey();
			Long commanId = errorRecords.getId();
			String key = b2cKey.concat(GSTConstants.SLASH)
					.concat(commanId.toString());
			List<Gstr6VerticalWebError> errList = errorMap.get(key);
			String invKey = errorRecords.getProcessKey();
			if (errList != null && !errList.isEmpty()) {
				errList.forEach(err -> {
					err.setCommanId(commanId);
					err.setInvKey(invKey);
				});
			}
		});

		// Add all the errors into a single list to save to the DB.
		List<Gstr6VerticalWebError> outError = new ArrayList<>();
		errorMap.entrySet().forEach(e -> {
			List<Gstr6VerticalWebError> errorList = e.getValue();
			errorList.forEach(error -> {
				if (error.getCommanId() != null) {
					outError.add(error);
				}
			});
		});
	if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("After Freeze CAN in Error List Saving to table");
			for(Gstr6VerticalWebError err: outError){
				LOGGER.debug(err.toString());
			}
			
	}
		if (!outError.isEmpty()) {
			gstr6VerticalWebErrorRepo.saveAll(outError);
		}
	}

	
}
