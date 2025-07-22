package com.ey.advisory.app.gstr3b;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.DongleMappingEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.repositories.client.DongleMappingRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BFilingDetailsDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSignFilePanDetailsDTO;
import com.ey.advisory.app.services.jobs.gstr1.GetBatchPayloadHandler;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.Gstr3bGetInvoicesReqDto;
import com.google.common.base.Strings;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("gstr3BSignAndFileServiceImpl")
public class Gstr3BSignAndFileServiceImpl implements Gstr3BSignAndFileService {

	@Autowired
	private Gstr3BGstinDashboardService dashBoardService;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private GetBatchPayloadHandler batchPayloadHelper;

	@Autowired
	private GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	private SignAndFileRepository signAndFileRepo;

	@Autowired
	private DongleMappingRepository dongleRepo;

	@Override
	public Pair<Boolean, String> getGstr3BGstnSummary(
			Gstr3bGetInvoicesReqDto gstr3BSummaryDto) {

		String gstin = gstr3BSummaryDto.getGstin();
		String taxPeriod = gstr3BSummaryDto.getTaxPeriod();
		String gstnResponse = null;
		try {
			APIResponse resp = dashBoardService.getGSTR3BSummary(taxPeriod,
					gstin);
			if (resp.isSuccess()) {
				gstnResponse = resp.getResponse();
				saveOrUpdateGstnUserRequest(gstin, taxPeriod, gstnResponse);
				return new Pair<>(Boolean.TRUE, resp.getResponse());
			} else {
				LOGGER.error(
						"we have received Error Response from GSTN"
								+ " for GET3BSumm GSTIN {}, Taxperiod {}, Response is {} ",
						gstr3BSummaryDto.getGstin(),
						gstr3BSummaryDto.getTaxPeriod(), resp.getErrors());
				return new Pair<>(Boolean.FALSE,
						resp.getError().getErrorDesc());
			}

		} catch (Exception ex) {
			String msg = "Exception while Get3B api Call";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	public void saveJsonAsRecords(String apiResp, Gstr3bGetInvoicesReqDto dto,
			GetAnx1BatchEntity batch) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr3B Summary GSTN GET call Json is {} ", apiResp);
		}
		if (apiResp != null && !apiResp.trim().isEmpty()) {

			batchPayloadHelper.dumpGetResponsePayload(
					TenantContext.getTenantId(), dto.getGstin(),
					dto.getTaxPeriod(), batch.getId(), apiResp,
					APIConstants.SYSTEM);

			batchUtil.updateById(batch.getId(), APIConstants.SUCCESS, null,
					null, false);

		} else {
			batchUtil.updateById(batch.getId(),
					APIConstants.SUCCESS_WITH_NO_DATA, null, null, false);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("No response from gstn");
			}
		}

	}

	@Override
	public void updateGstr3BTables(String taxperiod, String gstin,
			String signId, String ackNum) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"update isFiled flags as True for the input combination {} {} ",
					taxperiod, gstin);
		}

		try {
			int i = gstrReturnStatusRepository.updateReturnStatusFiling(
					Gstr3BConstants.FILED, ackNum,gstin, taxperiod,
					Gstr3BConstants.GSTR3B);

			if (i == 0) {
				GstrReturnStatusEntity returnStatusentity = new GstrReturnStatusEntity();
				returnStatusentity.setGstin(gstin);
				returnStatusentity.setTaxPeriod(taxperiod);
				returnStatusentity.setReturnType(Gstr3BConstants.GSTR3B);
				returnStatusentity.setStatus(Gstr3BConstants.FILED);
				returnStatusentity.setArnNo(ackNum);
				returnStatusentity.setUpdatedOn(LocalDateTime.now());
				returnStatusentity.setCreatedOn(LocalDateTime.now());
				returnStatusentity.setFilingDate(EYDateUtil.toISTDateTimeFromUTC(LocalDate.now()));
				returnStatusentity.setIsCounterPartyGstin(false);
				gstrReturnStatusRepository.save(returnStatusentity);
			}

			signAndFileRepo.updateStatus(ackNum, "SUCCESS", null,
					Long.valueOf(signId));
		} catch (Exception e) {
			String msg = "Exception occured in updateGstr3BTables";
			LOGGER.error(msg, e);

			throw new AppException(msg, e);
		}

	}

	private void saveOrUpdateGstnUserRequest(String gstin, String taxPeriod,
			String getGstnData) {

		GstnUserRequestEntity gstnUserRequestEntity = null;

		gstnUserRequestEntity = gstnUserRequestRepo
				.findByGstinAndTaxPeriodAndReturnType(gstin, taxPeriod,
						Gstr3BConstants.GSTR3B);

		Clob reqClob;
		try {
			reqClob = new javax.sql.rowset.serial.SerialClob(
					getGstnData.toCharArray());

			if (gstnUserRequestEntity == null) {

				gstnUserRequestEntity = new GstnUserRequestEntity();

				String userName = (SecurityContext.getUser() != null
						&& SecurityContext.getUser()
								.getUserPrincipalName() != null)
										? SecurityContext.getUser()
												.getUserPrincipalName()
										: "SYSTEM";

				if (userName == null || userName.isEmpty()) {
					userName = "SYSTEM";
				}

				gstnUserRequestEntity.setGstin(gstin);
				gstnUserRequestEntity.setTaxPeriod(taxPeriod);
				gstnUserRequestEntity.setReturnType(Gstr3BConstants.GSTR3B);
				gstnUserRequestEntity.setRequestType("GET");
				gstnUserRequestEntity.setRequestStatus(1);
				gstnUserRequestEntity.setCreatedBy(userName);
				gstnUserRequestEntity.setCreatedOn(LocalDateTime.now());
				gstnUserRequestEntity.setDelete(false);
				gstnUserRequestEntity.setGetResponsePayload(reqClob);
				gstnUserRequestEntity.setDerivedRetPeriod(
						Integer.valueOf(taxPeriod.substring(2)
								.concat(taxPeriod.substring(0, 2))));

				gstnUserRequestRepo.save(gstnUserRequestEntity);

			} else {
				gstnUserRequestRepo.updateGstnResponse(reqClob, 1, gstin,
						taxPeriod, Gstr3BConstants.GSTR3B, LocalDateTime.now());

			}
		} catch (Exception e) {
			String msg = "Exception occured while persisting gstnUserRequest data";
			LOGGER.error(msg, e);

			throw new AppException(msg, e);
		}
	}

	@Override
	public List<Gstr3BFilingDetailsDTO> getGstr3bFilingDetails(String gstin,
			String taxPeriod) {
		List<Gstr3BFilingDetailsDTO> gstr3BFilingDetailsDTOList = new ArrayList<>();
		Integer i = 1;
		try {

			List<SignAndFileEntity> entityList = signAndFileRepo
					.findByGstinAndTaxPeriodAndReturnTypeOrderByCreatedOnDesc(
							gstin, taxPeriod, Gstr3BConstants.GSTR3B);
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");

			for (SignAndFileEntity e : entityList) {
				Gstr3BFilingDetailsDTO dto = new Gstr3BFilingDetailsDTO();
				dto.setId(i++);
				dto.setRefId(e.getId());
				dto.setError(e.getErrorMsg());
				dto.setStatus(e.getStatus());
				dto.setAckNo(e.getAckNum());
				LocalDateTime createdTimeist = EYDateUtil
						.toISTDateTimeFromUTC(e.getCreatedOn());
				dto.setCreatedTime(formatter.format(createdTimeist));
				dto.setFilingMode(e.getFilingMode());
				gstr3BFilingDetailsDTOList.add(dto);
			}
		} catch (Exception e) {
			String msg = "Exception occured while getting gstr3bFiling data";
			LOGGER.error(msg, e);

			throw new AppException(msg, e);
		}

		return gstr3BFilingDetailsDTOList;
	}

	@Override
	public void downloadGstr3bErrorResp(String id,
			HttpServletResponse response) {
		String errorMsg = null;
		SignAndFileEntity entity = null;

		try {
			Optional<SignAndFileEntity> opEntity = signAndFileRepo
					.findById(Long.valueOf(id));

			if (opEntity.isPresent()) {
				entity = opEntity.get();
				errorMsg = entity.getErrorMsg();
				if (!Strings.isNullOrEmpty(errorMsg)) {
					writeMsgToTxt(entity, response);
				}
			} else {
				String errMsg = String.format(
						"GSTR3b Sign & File is not initiated for id %s", id);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
		} catch (Exception e) {
			String msg = "Exception occured while getting gstr3bFiling data";
			LOGGER.error(msg, e);

			throw new AppException(msg, e);
		}

	}

	private void writeMsgToTxt(SignAndFileEntity entity,
			HttpServletResponse response) {
		File tempDir = null;
		Writer writer = null;
		try {
			tempDir = createTempDir();
			String fileName = "GSTR3BFilingErrors_" + entity.getGstin() + "_"
					+ entity.getTaxPeriod();
			String fullPath = tempDir.getAbsolutePath() + File.separator
					+ fileName + ".txt";

			writer = new BufferedWriter(new FileWriter(fullPath), 8192);
			writer.write(entity.getErrorMsg());

			if (writer != null) {
				try {
					writer.flush();
					writer.close();
					if (LOGGER.isDebugEnabled()) {
						String msg = "Flushed writer successfully";
						LOGGER.debug(msg);
					}
				} catch (IOException e) {
					String msg = "Exception while closing the file writer";
					LOGGER.error(msg);
					throw new AppException(msg, e);
				}
			}

			File fileToDownload = new File(fullPath);
			InputStream inputStream = new FileInputStream(fileToDownload);

			response.setContentType("text/plain");
			response.setHeader("Content-Disposition", String
					.format("attachment; filename =%s ", fileName + ".txt"));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {
			String msg = "Error while writing Gstr3b errors to txt";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		} finally {

			if (tempDir != null && tempDir.exists()) {
				try {
					FileUtils.deleteDirectory(tempDir);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Deleted the Temp directory/Folder '%s'",
								tempDir.getAbsolutePath()));
					}
				} catch (Exception ex) {
					String msg = String.format(
							"Failed to remove the temp "
									+ "directory created for Gstr3b errors: '%s'. This will "
									+ "lead to clogging of disk space.",
							tempDir.getAbsolutePath());
					LOGGER.error(msg, ex);
				}
			}

		}

	}

	public static File createTempDir() throws IOException {
		return Files.createTempDirectory("Gstr3BErrors").toFile();
	}

	@Override
	public List<Gstr3BSignFilePanDetailsDTO> getPanDetails(String gstin) {
		List<Gstr3BSignFilePanDetailsDTO> panDetails = new ArrayList<>();
		try {
			List<DongleMappingEntity> entityList = dongleRepo
					.findByGstinAndIsActiveTrue(gstin);
			for (DongleMappingEntity e : entityList) {
				Gstr3BSignFilePanDetailsDTO panDto = new Gstr3BSignFilePanDetailsDTO();
				panDto.setPan(
						!Strings.isNullOrEmpty(e.getPan()) ? e.getPan() : null);
				panDto.setName(!Strings.isNullOrEmpty(e.getAuthorisedName())
						? e.getAuthorisedName() : null);
			//	e.setDesignation("manager");
				panDto.setDesignation(!Strings.isNullOrEmpty(e.getDesignation())
						? e.getDesignation() : "");
				panDetails.add(panDto);
			}

		} catch (Exception ex) {
			String msg = String.format(
					"Failed to get pan details for gstin: '%s'. ", gstin);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return panDetails;
	}

}