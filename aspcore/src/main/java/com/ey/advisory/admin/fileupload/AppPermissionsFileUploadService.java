package com.ey.advisory.admin.fileupload;

import java.util.List;

public interface AppPermissionsFileUploadService {

	public String appPermissionFileUpload(
			List<Object[]> appPermissionObjs, Object[] header, String groupCode,
			Long entityId);
}
