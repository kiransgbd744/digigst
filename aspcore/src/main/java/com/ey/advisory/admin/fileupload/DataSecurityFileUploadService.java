package com.ey.advisory.admin.fileupload;

import java.util.List;

public interface DataSecurityFileUploadService {

	public List<String> uploadDataSecurity(List<Object[]> orgConfigObjects,
			Object[] header, String groupCode, Long entityId);

}
