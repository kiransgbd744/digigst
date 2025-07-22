package com.ey.advisory.app.asprecon.gstr2.a2pr.reports;

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

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2APRReportTypeEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2APRReportTypeRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.ZipCsvFilesUtil;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Iterables;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2Recon2APRReportCommonServiceImpl")
public class Gstr2Recon2APRReportCommonServiceImpl
		implements Gstr2Recon2APRReportCommonService {

	@Autowired
	private Gstr2Recon2APRReportTypeRepository reportTypeRepo;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	ZipCsvFilesUtil zipUtil;

	private static final String RECON_REPORT_CHUNK_ROWS = 
			"gstr2.recon.2apr.report.chunk.rows";
	private static final String RECON_REPORT_CHUNK_FILES = 
			"gstr2.recon.2apr.report.chunk.files";

	@Override
	public Pair<Integer, Integer> getChunkingSizes() {
		Map<String, Config> configMap = configManager
				.getConfigs("RECON_REPORTS", "gstr2.recon.2apr.report.chunk");
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
			LOGGER.info("2APR recon Directory is empty");
			return;
		}

		Set<File> fileNames = new TreeSet<File>();
		File[] files = tempDir.listFiles();
		LOGGER.debug("2APR recon Directory file size {} ", files.length);

		String reportName = null;

		for (File file : files) {
			if (file.isFile()) {
				fileNames.add(file);
				reportName = file.getName();
			}
		}
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("file name before index %s "
						+ ": ", reportName);
				LOGGER.debug(msg);
			}
			
			String index = getIndexNumber(reportName);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Index no from file %s "
						+ ": ", index);
				LOGGER.debug(msg);
			}
			Iterable<List<File>> lists = Iterables.partition(fileNames,
					maxLimitPerZip);
			List<Pair<Long, List<File>>> reportIds = new ArrayList<>();
			Iterator<List<File>> it = lists.iterator();

			while (it.hasNext()) {

				String rptType = reportType + "_" + index;
				Long id = saveReportChunks(reportId, rptType, null, false);
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

				/*String uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
						"Gstr2ReconReports");*/
				
				Pair<String, String> uploadedDocName = DocumentUtility
						.uploadFile(zipFile,"Gstr2ReconReports");
				
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

	@Override
	public Long saveReportChunks(Long reportId, String reportType,
			String filePath, boolean isDownloadable) {
		Gstr2Recon2APRReportTypeEntity entity = 
				new Gstr2Recon2APRReportTypeEntity();
		entity.setReportDwnldId(reportId);
		entity.setReportType(reportType);
		entity.setDownloadable(isDownloadable);
		entity.setFilePath(filePath);
		reportTypeRepo.save(entity);
		return entity.getId();
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

	private String getIndexNumber(String fileName) {

		String index = "1";

		String[] splitedName = fileName.split(".csv");
		String[] splitedName1 = splitedName[0].split(".CSV");
		String[] name = splitedName1[0].split("_");

		if (fileName.startsWith("Addition in 2A_6A") 
				|| fileName.startsWith("Consolidated PR 2A_6A Report")
				|| fileName.startsWith("Dropped 2A_6A Records Report")) {

			index = name[3];

		} else if (fileName.startsWith("ERP_Report")) {
			index = name[4];
		} else {
			index = name[2];
		}
		return index;

	}

}
