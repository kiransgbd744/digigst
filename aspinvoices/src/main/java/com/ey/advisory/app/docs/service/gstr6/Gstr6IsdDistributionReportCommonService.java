package com.ey.advisory.app.docs.service.gstr6;

import java.io.File;

public interface Gstr6IsdDistributionReportCommonService {

	Integer getChunkingSizes();

	void chunkZipFiles(File tempDir, Long reportId, Long configId,
			String reportType, int maxLimitPerZip);

}
