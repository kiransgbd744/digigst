package com.ey.advisory.app.inward.einvoice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.AsyncFileStatusRptTypeEntity;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.AsyncFileStatusReportTypeRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombineAndZipTxtFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Harsh
 *
 */
@Component("AsyncJsonDownloadServiceImpl")
@Slf4j
public class AsyncJsonDownloadServiceImpl
		implements AsyncJsonReportDownloadService {

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;


	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository repo;


	@Autowired
	CombineAndZipTxtFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("InwardEinvoiceJsonDaoImpl")
	private InwardEinvoiceJsonDao inwardEinvoiceJsonDao;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	private AsyncFileStatusReportTypeRepository reportTypeRepo;

	public static final String EINVOICE_CONF_KEY = "inward.einvoice.json.report.chunk.size";

	@Transactional(value = "clientTransactionManager")
	@Override
	public String fileDownloads(Long id) {

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"inwardEinvoiceJsonDownload");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		try {
			Optional<FileStatusDownloadReportEntity> opt = downloadRepository
					.findById(id);
			FileStatusDownloadReportEntity entity = opt.get();

			List<Object> resp = inwardEinvoiceJsonDao
					.getInwardEinvoiceJsonData(entity);

			if (Collections.isEmpty(resp)) {
				return null;
			}
			List<String> strObj = resp.stream().map(obj -> obj.toString())
					.collect(Collectors.toList());

			List<JsonObject> jsonList = new ArrayList<>();

			for (String jsonStrg : strObj) {
				JsonObject jsonObject = JsonParser.parseString(jsonStrg)
						.getAsJsonObject();
				jsonList.add(jsonObject);
			}

			respToJsonFile(jsonList, id);

		} catch (Exception ex) {
			String msg = "Error occured while downloading the Zip File ";

			LOGGER.error(msg, ex);

			throw new AppException(ex);

		}
		return "Success";
	}

	private static File createTempDir(String uploadFolder) throws IOException {
		return Files.createTempDirectory(uploadFolder).toFile();
	}

	private void respToJsonFile(List<JsonObject> jsonList, Long id) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("inside json file creation");
			LOGGER.debug(msg);
		}
		Map<String, Config> configMap = configManager.getConfigs("EWB",
				EINVOICE_CONF_KEY, "DEFAULT");
		Integer chunkSize = configMap.get(EINVOICE_CONF_KEY) == null ? 1000
				: Integer.valueOf(configMap.get(EINVOICE_CONF_KEY).getValue());

		List<List<JsonObject>> chunks = Lists.partition(jsonList, chunkSize);
		int fileIndex = 0;
		for (List<JsonObject> chunk : chunks) {
			File tempDir = null;
			try {
				tempDir = createTempDir("JsonReportDownload");
				
				Gson gson = new GsonBuilder().setPrettyPrinting()
						.disableHtmlEscaping().create();

				fileIndex++;
				String jsonPath = tempDir.getAbsolutePath() + File.separator
						+ "Inward E-invoice_JSONs_" + id + "_" + fileIndex + ".json";

				BufferedWriter writer = Files
						.newBufferedWriter(Paths.get(jsonPath));

				JsonObject resps = new JsonObject();
				resps.add("irnJson", gson.toJsonTree(chunk));
				String jsonString = resps.toString();
				writer.write(jsonString);

				writer.flush();
				writer.close();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("json file created for id {}",
							id);
					LOGGER.debug(msg);
				}
				String reportType = "JSON Data ";

				String zipFileName = combineAndZipCsvFiles.zipfolder(
						Long.parseLong(String.valueOf(fileIndex)), tempDir,
						String.valueOf(id), "Inward E-invoice_JSONs", "");

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("zipFileName : %s", zipFileName);
					LOGGER.debug(msg);
				}
				Pair<String,String> uploadedZipName = null;


				File zipFile = new File(tempDir, zipFileName);
				uploadedZipName = DocumentUtility.uploadFile(zipFile,
						"JsonReportDownload");
				
				String uploadedDocName = uploadedZipName.getValue0();
				String docId = uploadedZipName.getValue1();
				
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Before uploading "
									+ "Zip Inner Mandatory files, tempDir "
									+ "Name %s and ZipFileName %s ",
							tempDir, uploadedDocName);
					LOGGER.debug(msg);
				}

				saveReportChunks(id, reportType + fileIndex, uploadedDocName,
						true,docId);

			} catch (Exception ex) {
				String msg = "Exception occured while saving json file";
				LOGGER.error(msg);
				throw new AppException();
			} finally {
				GenUtil.deleteTempDir(tempDir);
			}

		}
	}

	public Long saveReportChunks(Long reportId, String reportType,
			String filePath, boolean isDownloadable, String docId) {
		AsyncFileStatusRptTypeEntity entity = new AsyncFileStatusRptTypeEntity();
		entity.setReportDwnldId(reportId);
		entity.setReportType(reportType);
		entity.setDownloadable(isDownloadable);
		entity.setFilePath(filePath);
		entity.setId(generateCustomId(entityManager));
		entity.setDocId(docId);
		reportTypeRepo.save(entity);
		return entity.getId();
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

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RECON_RESULT_REPORT_SEQ.nextval "
				+ "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}

}
