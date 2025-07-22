package com.ey.advisory.app.data.returns.compliance.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cell;
import com.aspose.cells.CellArea;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.FileStatusRepository;
import com.ey.advisory.app.data.entities.client.asprecon.VendorComplianceRatingAsyncApiResponseEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorMasterApiEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendoComplianceAsyncApiRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterApiEntityRepository;
import com.ey.advisory.app.data.services.compliancerating.VendorAsyncApiGstinDto;
import com.ey.advisory.app.data.services.compliancerating.VendorAsyncApiRequestDto;
import com.ey.advisory.app.sftp.service.SFTPFileTransferService;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Service("VendorComplianceRatingSFTPHandler")
@Slf4j
public class VendorComplianceRatingSFTPHandler {

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("VendoComplianceAsyncApiRepository")
	private VendoComplianceAsyncApiRepository repo;

	@Autowired
	@Qualifier("VendorMasterApiEntityRepository")
	private VendorMasterApiEntityRepository vendorMaserApiRepo;

	@Autowired
	private EntityInfoRepository entityInfoRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	@Qualifier("FileStatusRepository")
	private FileStatusRepository gstr1FileStatusRepository;

	private static final String VENDOR_COMPLIANCE_SFTP_SOURCE = "ey.internal.vendor.compliance.sftp.source";

	private static final String VENDOR_COMPLIANCE_SFTP_DESTINATION = "ey.internal.vendor.compliance.sftp.destination";

	private static final List<String> FILE_EXT = ImmutableList
			.of(JobStatusConstants.XLSX_TYPE, JobStatusConstants.CSV_TYPE);

	public void sftpFilesDownload() {
		File tempDir = null;
		try {
			Map<String, Config> configMap = configManager.getConfigs("SFTP",
					"ey.internal.vendor.compliance.sftp");

			tempDir = createTempDir();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("temp directory name : %s", tempDir);
				LOGGER.debug(msg);
			}
			sftpService.downloadFiles(tempDir.getAbsolutePath(),
					configMap.get(VENDOR_COMPLIANCE_SFTP_SOURCE).getValue(),
					FILE_EXT);
			if (tempDir.list().length == 0) {
				String errMsg = String.format("Temp Directory  %s is empty",
						tempDir);// add groupcode
				LOGGER.error(errMsg);
				GenUtil.deleteTempDir(tempDir);
				return;
			}
			File[] downloadedFiles = tempDir.listFiles();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Total No of files downloaded to remote directory is {}",
						downloadedFiles.length);// groupcode
			List<String> uploadedFiles = new ArrayList<>();
			for (File file : downloadedFiles) {

				FileInputStream fis = new FileInputStream(file);

				// Load the Excel file using the FileInputStream
				Workbook workbook = new Workbook(fis);

				Worksheet worksheet = workbook.getWorksheets().get(0);

				// Define the range of cells (from A1 to the last cell)
				CellArea area = new CellArea();
				area.StartRow = 4;
				area.StartColumn = 0;
				area.EndRow = worksheet.getCells().getMaxDataRow();
				area.EndColumn = worksheet.getCells().getMaxDataColumn();

				Cell fyCell = worksheet.getCells().get(0, 1);
				String fy = fyCell.getStringValue();
				Cell panCell = worksheet.getCells().get(1, 1);
				String pan = panCell.getStringValue();
				// Iterate through cells within the defined range
				List<VendorAsyncApiGstinDto> gstinDtoList = new ArrayList<>();
				for (int row = area.StartRow; row <= area.EndRow; row++) {
					Cell gsinCell = worksheet.getCells().get(row, 0);
					Cell vendorCell = worksheet.getCells().get(row, 1);
					VendorAsyncApiGstinDto gstinDto = new VendorAsyncApiGstinDto();
					gstinDto.setVendorGstin(gsinCell.getStringValue());
					gstinDto.setVendorName(vendorCell.getStringValue());
					gstinDtoList.add(gstinDto);
				}

				VendorAsyncApiRequestDto dto = new VendorAsyncApiRequestDto();
				dto.setFy(fy);
				dto.setPan(pan);
				dto.setGstins(gstinDtoList);
				Gson gson = GsonUtil.newSAPGsonInstance();
				try {
					User user = SecurityContext.getUser();
					String userPrincipalName = user.getUserPrincipalName();
					String requestPayload = gson.toJson(dto);
					JsonObject resps = new JsonObject();
					if (Strings.isNullOrEmpty(dto.getFy())
							|| Strings.isNullOrEmpty(dto.getPan())) {
						resps.addProperty("errorMessage",
								"Financial Year or Pan is Empty");
					}
					List<VendorAsyncApiGstinDto> vendorDto = dto.getGstins();
					for (VendorAsyncApiGstinDto vendor : vendorDto) {
						if (Strings.isNullOrEmpty(vendor.getVendorGstin())
								|| Strings.isNullOrEmpty(
										vendor.getVendorGstin())) {
							resps.addProperty("errorMessage",
									"Vendor Gstin is Null or Empty");
						}
						if (Strings.isNullOrEmpty(vendor.getVendorName())
								|| Strings.isNullOrEmpty(
										vendor.getVendorName())) {
							resps.addProperty("errorMessage",
									"Vendor Name is Null or Empty");
						}
					}
					String fileName = file.getName();
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"insithe the method getVendorsReturnFileStatus "
										+ " of controller VendorComplianceRatingAsyncApiController ");

					Long entityId = entityInfoRepo
							.findActiveIdByPan(dto.getPan());
					if (entityId == null) {
						resps.addProperty("errorMessage",
								"Vendor Pan is Invalid");
					} else {
						String userName = SecurityContext.getUser()
								.getUserPrincipalName();
						UUID uuid = UUID.randomUUID();
						VendorComplianceRatingAsyncApiResponseEntity entity = new VendorComplianceRatingAsyncApiResponseEntity();
						entity.setReferenceId(uuid.toString());
						entity.setEntityId(entityId);
						entity.setCreatedBy(userPrincipalName);
						entity.setCreatedOn(LocalDateTime.now());
						entity.setRequestPayload(
								GenUtil.convertStringToClob(requestPayload));
						entity.setGstinCount(dto.getGstins().size());
						entity.setReportCategory("GSTR1,GSTR3B");
						entity.setDataType("All");
						entity.setTableType("VENDOR");
						entity.setFy(dto.getFy());
						entity.setStatus("Initiated");
						entity.setUploadMode("SFTP");
						entity.setFileName(fileName);
						entity = repo.save(entity);
						Long id = entity.getId();
						List<VendorMasterApiEntity> apiEntities = new ArrayList<>();
						List<String> availableGstins = new ArrayList<>();
						for (VendorAsyncApiGstinDto apiDto : vendorDto) {
							if (apiDto.getVendorGstin() != null)
								availableGstins.add(apiDto.getVendorGstin());
						}
						vendorMaserApiRepo
								.updateisDeleteBeforePersist(availableGstins);
						for (VendorAsyncApiGstinDto apiDto : vendorDto) {
							VendorMasterApiEntity apiEntity = new VendorMasterApiEntity();
							apiEntity.setEntityId(entityId);
							apiEntity.setRecipientPAN(dto.getPan());
							apiEntity.setVendorGstin(apiDto.getVendorGstin());
							apiEntity.setCreatedBy(userPrincipalName);
							apiEntity.setVendorName(apiDto.getVendorName());
							if (apiDto.getVendorGstin().length() > 12) {
								apiEntity.setVendorPAN(apiDto.getVendorGstin()
										.substring(2, 12));
							}
							apiEntity.setIsActive(true);
							apiEntity.setIsDelete(false);
							apiEntity.setCreatedOn(LocalDateTime.now());
							apiEntities.add(apiEntity);
						}
						vendorMaserApiRepo.saveAll(apiEntities);
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Successfully saved to DB with Report Id : %d",
									id);
							LOGGER.debug(msg);
						}
						JsonObject jobParams = new JsonObject();
						jobParams.addProperty("id", id);

						asyncJobsService.createJob(TenantContext.getTenantId(),
								JobConstants.InitiateGetVendorFilingApi,
								jobParams.toString(), userName, 5L, 0L, 0L);

						uploadedFiles.add(file.getName());
					}
				} catch (Exception e) {
					LOGGER.error("exception while intiating vendor api: ", e);
				}
			}
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"VENDOR_COMPLIANCE_SFTP_SOURCE is {} and VENDOR_COMPLIANCE_SFTP_DESTINATION is {}",
						VENDOR_COMPLIANCE_SFTP_SOURCE,
						VENDOR_COMPLIANCE_SFTP_DESTINATION);
			boolean archivedFiles = sftpService.moveFiles(uploadedFiles,
					configMap.get(VENDOR_COMPLIANCE_SFTP_SOURCE).getValue(),
					configMap.get(VENDOR_COMPLIANCE_SFTP_DESTINATION)
							.getValue());
			if (!archivedFiles) {
				LOGGER.error("Error while moving the files.");

			}
		} catch (Exception ex) {
			String msg = " Unexpected error occured ";
			LOGGER.error(msg, ex);

		} finally {
			if (tempDir != null) {
				GenUtil.deleteTempDir(tempDir);
			}
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("VendorComplianceSftpFiles").toFile();
	}

}