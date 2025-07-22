package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.util.List;
import java.util.Map;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.MasterErrorEntity;
import com.ey.advisory.common.ProcessingResult;

public interface DocErrorSaveService {
	public Map<String, List<MasterErrorEntity>> saveVendorErrorRecord(
			Map<String, List<ProcessingResult>> processingResults, Gstr1FileStatusEntity updateFileStatus);

}
