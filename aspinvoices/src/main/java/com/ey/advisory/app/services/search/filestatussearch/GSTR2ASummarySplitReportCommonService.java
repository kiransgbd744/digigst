package com.ey.advisory.app.services.search.filestatussearch;

/**
 * vishal.verma
 */

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.AsyncFileStatusRptTypeEntity;
import com.ey.advisory.app.data.repositories.client.AsyncFileStatusReportTypeRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombinAndZipReportFiles;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.ZipCsvFilesUtil;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Iterables;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GSTR2ASummarySplitReportCommonService")
public class GSTR2ASummarySplitReportCommonService
		 {

	@Autowired
	private AsyncFileStatusReportTypeRepository reportTypeRepo;

	@Autowired
	CombinAndZipReportFiles combineAndZipCsvFiles;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	ZipCsvFilesUtil zipUtil;

	private static final String GSTR2A_CHUNK_ROWS = "gstr2a.complete.report.row.size";
	private static final String CONF_CATEG = "GSTR2A";

	public Integer getChunkingSizes() {
		Config config = configManager.getConfig(CONF_CATEG, GSTR2A_CHUNK_ROWS);
		String chunkSize = config.getValue();
		chunkSize = chunkSize != null ? chunkSize : "200000" ;
		return (Integer.valueOf(chunkSize));
	}
	
	public void chunkZipFiles(File tempDir, Long reportId, Long configId,
			String reportType, int maxLimitPerZip) {
		String zipFileName = null;

		if (tempDir.isDirectory() && tempDir.list().length == 0) {
			LOGGER.info("GSTR2ASummarySplitReportCommonService Directory is empty");
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

				String rptType = reportType+ " " + index;
				Long id = saveReportChunks(reportId, rptType, null, false);
				reportIds.add(new Pair<>(id, it.next()));
			}

			for (Pair<Long, List<File>> rptChunks : reportIds) {

				zipFileName = combineAndZipCsvFiles.zipfolder(tempDir,
						reportType, configId);

				File zipFile = new File(tempDir, zipFileName);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Before uploading "
									+ "Zip Inner Mandatory files, tempDir "
									+ "Name %s and ZipFileName %s ",
							tempDir, zipFileName);
					LOGGER.debug(msg);
				}

				String uploadedDocName = DocumentUtility.uploadZipFile(zipFile,
						"Anx1FileStatusReport");
				reportTypeRepo.updateFilePath(uploadedDocName,
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

	
	public Long saveReportChunks(Long reportId, String reportType,
			String filePath, boolean isDownloadable) {
		AsyncFileStatusRptTypeEntity entity = 
				new AsyncFileStatusRptTypeEntity();
		entity.setReportDwnldId(reportId);
		entity.setReportType(reportType);
		entity.setDownloadable(isDownloadable);
		entity.setFilePath(filePath);
		entity.setId(generateCustomId(entityManager));
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
		
		index = name[1];
		
		if(fileName.startsWith("GSTR-2A_Get_Records")) {
			index = name[3];
		}
		return index;

	}
	
	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RECON_RESULT_REPORT_SEQ.nextval FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}

	private static Long generateCustomId(EntityManager entityManager) {
		String reportId = "";
		String digits = "";
		Long nextSequencevalue = getNextSequencevalue(entityManager);

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		String currentDate = String.valueOf(currentYear);
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}
	
	

}
