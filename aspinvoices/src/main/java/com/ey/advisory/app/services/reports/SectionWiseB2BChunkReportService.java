package com.ey.advisory.app.services.reports;

import java.io.File;

import org.javatuples.Pair;

public interface SectionWiseB2BChunkReportService {
	
	Pair<Integer, Integer> getChunkingSizes();

	Long saveReportChunks(Long reportId, String reportType, String filePath,
			boolean isDownloadable);

	void chunkZipFiles(File tempDir, Long reportId, Long configId,
			String reportType, int maxLimitPerZip, int index);

}
