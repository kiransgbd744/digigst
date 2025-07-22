package com.ey.advisory.inwardEinvoice.initiateRecon;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.asprecon.gstr2.pr2b.reports.Gstr2Recon2BPRReportCommonService;
import com.ey.advisory.app.data.entities.client.asprecon.InwardEinvReconChildReportTypeEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEinvoiceChildRReportTypeRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Iterables;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("InwardEinvoiceReconReportCommonServiceImpl")
public class InwardEinvoiceReconReportCommonServiceImpl
		implements Gstr2Recon2BPRReportCommonService {

	@Autowired
	private InwardEinvoiceChildRReportTypeRepository reportTypeRepo;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	private static final String RECON_REPORT_CHUNK_ROWS = "gstr2.recon.2bpr.report.chunk.rows";
	private static final String RECON_REPORT_CHUNK_FILES = "gstr2.recon.2bpr.report.chunk.files";

	@Override
	public Pair<Integer, Integer> getChunkingSizes() {
		Map<String, Config> configMap = configManager
				.getConfigs("RECON_REPORTS", "gstr2.recon.2bpr.report.chunk");
		String fileSize = configMap.get(RECON_REPORT_CHUNK_ROWS) != null
				? configMap.get(RECON_REPORT_CHUNK_ROWS).getValue()
				: "100000";
		String reportSize = configMap.get(RECON_REPORT_CHUNK_FILES) != null
				? configMap.get(RECON_REPORT_CHUNK_FILES).getValue()
				: "5";
		return new Pair<>(Integer.valueOf(fileSize),
				Integer.valueOf(reportSize));
	}

	@Override
	public void chunkZipFiles(File tempDir, Long reportId, Long configId,
			String reportType, int maxLimitPerZip) {
		String zipFileName = null;

		if (tempDir.isDirectory() && tempDir.list().length == 0) {
			LOGGER.info("2BPR recon Directory is empty");
			return;
		}

		Set<File> fileNames = new TreeSet<File>();
		File[] files = tempDir.listFiles();
		LOGGER.debug("2BPR recon Directory file size {} ", files.length);
		
		String reportName = null;
		
		for (File file : files) {
			if (file.isFile()) {
				fileNames.add(file);
				reportName = file.getName();
			}
		}
		try {
			String index = getIndexNumber(reportName);
			Iterable<List<File>> lists = Iterables.partition(fileNames,
					maxLimitPerZip);
			List<Pair<Long, List<File>>> reportIds = new ArrayList<>();
			Iterator<List<File>> it = lists.iterator();
			
			while (it.hasNext()) {
				
				String rptType = reportType + "_" + index;
				Long id = saveReportChunks(reportId, rptType, null, false,null);
				reportIds.add(new Pair<>(id, it.next()));
			}
			

			for (Pair<Long, List<File>> rptChunks : reportIds) {
				
				zipFileName = combineAndZipCsvFiles.zipfolder(configId, tempDir,
						reportType + index);	

				File zipFile = new File(tempDir, zipFileName);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Before uploading "
									+ "Zip Inner Mandatory files, tempDir "
									+ "Name %s and ZipFileName %s ",
							tempDir, zipFileName);
					LOGGER.debug(msg);
				}

				Pair<String, String> uploadedDocName = DocumentUtility
						.uploadFile(zipFile,"InwardEinvoiceReconReports");
				
				reportTypeRepo.updateFilePathAndDocId(uploadedDocName
						.getValue0(), uploadedDocName.getValue1(),
						rptChunks.getValue0());

				deleteTempFiles(tempDir.listFiles());
			}

		} catch (Exception ee) {
			String msg = "Exception while chunking zip files";
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		} finally {
			deleteTempFiles(tempDir.listFiles());
		}
	}

	private void deleteTempFiles(File[] files) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Before Deleting Temp file size %d :",
					files.length);
			LOGGER.debug(msg);
		}
		for (File file : files) {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside for loop Before Deleting Temp file %s :",
						file.getName());

				LOGGER.debug(msg);
			}

			if (file.isFile()) {
				file.delete();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("File deleted successfully %s :",
							file.getAbsolutePath());

					LOGGER.debug(msg);
				}
			}
		}
	}

	@Override
	public Long saveReportChunks(Long reportId, String reportType,
			String filePath, boolean isDownloadable, String docId) {
		InwardEinvReconChildReportTypeEntity entity = new InwardEinvReconChildReportTypeEntity();
		entity.setReportDwnldId(reportId);
		entity.setReportType(reportType);
		entity.setDownloadable(isDownloadable);
		entity.setFilePath(filePath);
		entity.setDocId(docId);
		reportTypeRepo.save(entity);
		return entity.getId();
	}
	
	
	private String getIndexNumber(String fileName) {
		
		String[] splitedName = fileName.split("\\.");
		String[] name = splitedName[0].split("_");
		
		return name[2];
		
	}

	

}
