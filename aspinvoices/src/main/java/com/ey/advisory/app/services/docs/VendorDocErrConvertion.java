package com.ey.advisory.app.services.docs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.MasterErrorEntity;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.admin.services.onboarding.common.MasterVendorDocumentKeyBuilder;

/**
 * 
 * @author Sasidhar Reddy
 *
 */
@Service("VendorDocErrConvertion")
public class VendorDocErrConvertion {
	@Autowired
	@Qualifier("FileStatusRepository")
	private FileStatusRepository fileStatusRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorDocErrConvertion.class);

	/**
	 * 
	 * @param errDocMapObj
	 * @param documentKeyBuilder
	 * @param fileStatus
	 * @return
	 */
	public List<MasterErrorEntity> convertVendorFileTransError(
			Map<String, List<Object[]>> errDocMapObj,
			MasterVendorDocumentKeyBuilder vendorDocumentKeyBuilder,
			Gstr1FileStatusEntity fileStatus) {
		LOGGER.error(
				"VendorDocErrConvertion " + "convertVendorDocError Begining");
		List<MasterErrorEntity> errorHeaders = new ArrayList<>();
		try {
			errDocMapObj.entrySet().forEach(entry -> {
				String key = entry.getKey();
				List<Object[]> objs = entry.getValue();
				for (Object[] obj : objs) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Key - " + key + ", Value - "
								+ Arrays.toString(obj));
					}
					MasterErrorEntity errorDocument = new MasterErrorEntity();
					errorDocument.setErrorRecords(
							obj[0] != null ? obj[0].toString() : null);
					errorDocument.setMasterFileId((Long) obj[1]);
					errorDocument.setErrorCode(
							obj[2] != null ? obj[2].toString() : null);
					errorDocument.setErrorDec(
							obj[3] != null ? obj[3].toString() : null);
					errorDocument.setStatus(
							obj[4] != null ? obj[4].toString() : null);
					errorDocument.setCreatedBy(System.getProperty("user.name"));
					errorDocument.setCreatedOn(LocalDateTime.now());
					errorDocument
							.setModifiedBy(System.getProperty("user.name"));
					errorDocument.setModifiedOn(LocalDateTime.now());
					errorHeaders.add(errorDocument);
				}

			});

		} catch (Exception e) {
			LOGGER.error("Error Occured:{} ", e);
		}
		LOGGER.error("VendorFileTransDocErrConvertion "
				+ "convertVendorFileTransError Endining");
		return errorHeaders;
	}
}
