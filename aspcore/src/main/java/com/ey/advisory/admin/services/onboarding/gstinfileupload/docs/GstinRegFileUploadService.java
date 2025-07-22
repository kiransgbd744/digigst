package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.util.List;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;

public interface GstinRegFileUploadService {

	public List<GSTNDetailEntity> saveAll(List<GSTNDetailEntity> gstins);

	public List<Long> getByGstin(String gstin, Long entityId);

	public void createGstinEntriesInOrg(Long entityId, String groupCode,
			List<GSTNDetailEntity> gstins);

	public void disableUploadedGstinEntries(Long entityId, String groupCode,
			List<GSTNDetailEntity> gstins);

}
