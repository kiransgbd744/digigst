package com.ey.advisory.app.services.ims;

import java.io.File;

import org.javatuples.Pair;

public interface EinvoiceSummaryChunkReportService {

	Pair<Integer, Integer> getChunkingSizes();

	Long saveReportChunks(Long reportId, String reportType, String filePath,
			boolean isDownloadable);

	void chunkZipFiles(File tempDir, Long reportId, Long configId,
			String reportType, int maxLimitPerZip, int index);

}
