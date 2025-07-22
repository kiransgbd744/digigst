package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.MasterErrorEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ProcessingResult;

@Service("VendorDocErrorService")
public class VendorDocErrorService implements DocErrorSaveService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorDocErrorService.class);

	@Override
	public Map<String, List<MasterErrorEntity>> saveVendorErrorRecord(
			Map<String, List<ProcessingResult>> results, Gstr1FileStatusEntity updateFileStatus) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DefaultDocErrorSaveService saveErrorRecord begining");
		}
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"SRFileToOutwardTransDocErrConvertion convertErrors Begining");
			}
			Map<String, List<MasterErrorEntity>> map = new HashMap<>();

			results.keySet().stream().forEach(key -> {
				List<ProcessingResult> pResults = results.get(key);
				List<MasterErrorEntity> errors = new ArrayList<>();
				pResults.forEach(pr -> {
					// Instantiate the ent
					MasterErrorEntity error = new MasterErrorEntity();
					error.setErrorCode(pr.getCode());
					error.setErrorDec(pr.getDescription());
					error.setErrorKey(key);
					error.setMasterFileId(updateFileStatus.getId());
					error.setStatus(pr.getType().name());
					error.setCreatedBy("System");
					error.setCreatedOn(LocalDateTime.now());
					errors.add(error);
				});
				map.put(key, errors);
			});
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"SRFileToOutwardTransDocErrConvertion convertErrors End");
			}
			return map;

		} catch (Exception e) {
			LOGGER.error("ExceptionOccured:{} ", e);
			throw new AppException("Exception while saving the records");
		}
	}

}
