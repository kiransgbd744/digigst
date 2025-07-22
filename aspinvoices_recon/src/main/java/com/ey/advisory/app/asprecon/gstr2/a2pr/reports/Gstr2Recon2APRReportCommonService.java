package com.ey.advisory.app.asprecon.gstr2.a2pr.reports;

import java.io.File;

import org.javatuples.Pair;

public interface Gstr2Recon2APRReportCommonService {

	Pair<Integer, Integer> getChunkingSizes();

	void chunkZipFiles(File tempDir, Long reportId, Long configId,
			String reportType, int maxLimitPerZip);

	Long saveReportChunks(Long reportId, String reportType, String filePath,
			boolean isDownloadable);
}
