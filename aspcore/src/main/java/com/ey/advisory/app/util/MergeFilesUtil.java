package com.ey.advisory.app.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;

import com.ey.advisory.app.dashboard.mergefiles.FileInfo;
import com.ey.advisory.common.DashboardCommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MergeFilesUtil {
	
	public static File createOutputdDir(File tempDir) throws IOException {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Creating output directory to merge data into csv file");
		}
		
		File outputDir = new File(tempDir.getAbsolutePath()+ File.separator + "outputDir"); 
		outputDir.mkdirs();
		
		return outputDir;
	}

	public static File createDownloadDir(File tempDir) throws IOException {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Creating downloaded directory to download generated csv file for merging");
		}
		
		File downloadDir = new File(tempDir.getAbsolutePath()+ File.separator + "DownloadDir"); 
		downloadDir.mkdirs();
		
		return downloadDir;
	}

	public static File createTempDir() throws IOException {
		
		return Files.createTempDirectory("TempDirectory").toFile();
	}
	
	public static File downloadFile(FileInfo fileInfo, File downloadDir, String returnType) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Downloading csv file");
		}

		String folderName = DashboardCommonUtility.getDashboardFolderName(
				returnType, JobStatusConstants.CSV_FILE);
		
		String fileName = returnType + "_" + fileInfo.getSection() + "_"
				+ fileInfo.getTaxPeriod() + "_" + fileInfo.getGstin() + ".csv";
		
		File destFile = new File(downloadDir.getAbsolutePath() + File.separator + fileName);
		
		try {
			Document document = DocumentUtility.downloadDocument(fileName, folderName);
			
			if(document == null)
				return null;
			
			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), destFile);
		} catch (Exception e) {
			
			String msg = "Error occured while downloading file";
			LOGGER.error(msg, e);
		}
		
		return destFile;
	}
	
	public static  File createOutputFile(String outputFileName, File outputDir) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Creating output file");
		}
		File outputFile = new File(outputDir.getAbsolutePath()+ File.separator +outputFileName); 
		
		return outputFile;
	}


}
