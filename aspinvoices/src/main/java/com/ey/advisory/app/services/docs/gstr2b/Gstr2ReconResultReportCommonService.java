package com.ey.advisory.app.services.docs.gstr2b;

import java.io.File;

import org.javatuples.Pair;

public interface Gstr2ReconResultReportCommonService {

	Pair<Integer, Integer> getChunkingSizes();

	void chunkZipFiles(File tempDir, Long reportId, Long configId,
			String reportType, int maxLimitPerZip);

	Long saveReportChunks(Long reportId, String reportType, String filePath,
			boolean isDownloadable);
}
