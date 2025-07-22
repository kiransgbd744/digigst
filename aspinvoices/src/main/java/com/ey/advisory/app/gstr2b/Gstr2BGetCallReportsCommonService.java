package com.ey.advisory.app.gstr2b;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr2BGenerateReportTypeEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2BGenerateReportTypeRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.ZipCsvFilesUtil;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Iterables;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2BGetCallReportsCommonService")
public class Gstr2BGetCallReportsCommonService {

	@Autowired
	private Gstr2BGenerateReportTypeRepository reportTypeRepo;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	ZipCsvFilesUtil zipUtil;

	private static final String CONF_KEY = "gstr2b.get.generate.report.size";
	private static final String CONF_CATEG = "GSTR2B_REPORTS";

	public Integer getChunkSize() {
		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY);
		String chunkSize = config.getValue() != null ? config.getValue()
				: "500000";
		return (Integer.valueOf(chunkSize));
	}

	public void chunkZipFiles(File tempDir, Long reportId, Long configId,
			String reportType, int maxLimitPerZip) {
		String zipFileName = null;

		if (tempDir.isDirectory() && tempDir.list().length == 0) {
			LOGGER.info("Directory is empty");
			return;
		}

		Set<File> fileNames = new TreeSet<File>();
		File[] files = tempDir.listFiles();
		LOGGER.debug("Directory file size {} ", files.length);

		String reportName = null;

		for (File file : files) {
			if (file.isFile()) {
				fileNames.add(file);
				reportName = file.getName();
			}
		}
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("file name before index %s " + ": ",
						reportName);
				LOGGER.debug(msg);
			}

			String index = getIndexNumber(reportName);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Index no from file %s " + ": ",
						index);
				LOGGER.debug(msg);
			}
			Iterable<List<File>> lists = Iterables.partition(fileNames,
					maxLimitPerZip);
			List<Pair<Long, List<File>>> reportIds = new ArrayList<>();
			Iterator<List<File>> it = lists.iterator();

			while (it.hasNext()) {

				String rptType = reportType + index;
				Long id = saveReportChunks(reportId, rptType, false);
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
						.uploadFile(zipFile, "Gstr2bGetReport");

				reportTypeRepo.updateDocId(uploadedDocName.getValue0(),
						uploadedDocName.getValue1(), rptChunks.getValue0());

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

	public Long saveReportChunks(Long reportId, String reportType,
			boolean isDownloadable) {
		Gstr2BGenerateReportTypeEntity entity = new Gstr2BGenerateReportTypeEntity();
		entity.setReportDwnldId(reportId);
		entity.setReportType(reportType);
		entity.setDownloadable(isDownloadable);
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
		index = name[3];
		
		return index;

	}

}
