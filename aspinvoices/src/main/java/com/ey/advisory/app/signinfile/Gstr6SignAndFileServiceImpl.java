package com.ey.advisory.app.signinfile;

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

import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.SignAndFileRepository;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BFilingDetailsDTO;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.google.common.base.Strings;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */

@Slf4j
@Component("Gstr6SignAndFileServiceImpl")
public class Gstr6SignAndFileServiceImpl implements Gstr6SignAndFileService {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	private GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	private SignAndFileRepository signAndFileRepo;

	@Override
	public void updateGstr6Tables(String taxperiod, String gstin, String signId,
			String ackNum, LocalDateTime now) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"update isFiled flags as True for the input combination {} {} ",
					taxperiod, gstin);
		}

		try {
			int i = gstrReturnStatusRepository.updateReturnStatusFiling(
					Gstr3BConstants.FILED, ackNum, gstin, taxperiod,
					APIConstants.GSTR6.toUpperCase());

			if (i == 0) {
				GstrReturnStatusEntity returnStatusentity = new GstrReturnStatusEntity();
				returnStatusentity.setGstin(gstin);
				returnStatusentity.setTaxPeriod(taxperiod);
				returnStatusentity
						.setReturnType(APIConstants.GSTR6.toUpperCase());
				returnStatusentity.setStatus(Gstr3BConstants.FILED);
				returnStatusentity.setCreatedOn(now);
				returnStatusentity.setArnNo(ackNum);
				returnStatusentity.setUpdatedOn(now);
				returnStatusentity.setFilingDate(EYDateUtil.toISTDateTimeFromUTC(LocalDate.now()));
				returnStatusentity.setIsCounterPartyGstin(false);
				gstrReturnStatusRepository.save(returnStatusentity);
			}

			signAndFileRepo.updateStatus(ackNum, "SUCCESS", null,
					Long.valueOf(signId));
		} catch (Exception e) {
			String msg = "Exception occured in updateGstr6Tables";
			LOGGER.error(msg, e);

			throw new AppException(msg, e);
		}

	}

	@Override
	public Pair<Boolean, String> getGstr6GstnSummary(
			Gstr6GetInvoicesReqDto gstr6SummaryDto) {
		String gstin = gstr6SummaryDto.getGstin();
		String taxPeriod = gstr6SummaryDto.getReturnPeriod();
		String gstnResponse = null;
		try {
			APIResponse resp = getGstr6Summary(taxPeriod, gstin);
			if (resp.isSuccess()) {
				gstnResponse = resp.getResponse();
				saveOrUpdateGstnUserRequest(gstin, taxPeriod, gstnResponse);
				return new Pair<>(Boolean.TRUE, resp.getResponse());
			} else {
				LOGGER.error(
						"we have received Error Response from GSTN"
								+ " for GETGSTR6Summ GSTIN {}, Taxperiod {}, Response is {} ",
						gstr6SummaryDto.getGstin(),
						gstr6SummaryDto.getReturnPeriod(), resp.getErrors());
				return new Pair<>(Boolean.FALSE,
						resp.getError().getErrorDesc());
			}
		} catch (Exception ex) {
			String msg = "Exception while GetGSTR1 api Call";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private void saveOrUpdateGstnUserRequest(String gstin, String taxPeriod,
			String getGstnData) {
		GstnUserRequestEntity gstnUserRequestEntity = null;
		gstnUserRequestEntity = gstnUserRequestRepo
				.findTop1ByGstinAndTaxPeriodAndReturnTypeAndRequestTypeAndIsDeleteFalseOrderByIdDesc(
						gstin, taxPeriod, APIConstants.GSTR6.toUpperCase(),
						"GET");
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
				gstnUserRequestEntity
						.setReturnType(APIConstants.GSTR6.toUpperCase());
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
						taxPeriod, APIConstants.GSTR6.toUpperCase(),
						LocalDateTime.now());
			}
		} catch (Exception e) {
			String msg = "Exception occured while persisting gstnUserRequest data";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

	public APIResponse getGstr6Summary(String taxPeriod, String gstin) {
		APIResponse resp = null;
		try {
			// Invoke the GSTN API - Get Return Status API and get the JSON.
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);
			APIParam param3 = new APIParam("fnl", GSTConstants.N);
			String groupCode = TenantContext.getTenantId();

			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GSTR6_GETSUM, param1, param2, param3);
			resp = apiExecutor.execute(params, null);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("gstr6 get summery api respons: {}", resp);
			}
			return resp;
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking '%s',"
							+ " GSTIN - '%s' and Taxperiod - '%s'",
					APIIdentifiers.GSTR6_GETSUM, gstin, taxPeriod);
			LOGGER.error(msg, ex);
			throw ex;
		}

	}

	@Override
	public List<Gstr3BFilingDetailsDTO> getGstr6FilingDetails(String gstin,
			String taxPeriod) {
		List<Gstr3BFilingDetailsDTO> gstr3BFilingDetailsDTOList = new ArrayList<>();
		Integer i = 1;
		try {

			List<SignAndFileEntity> entityList = signAndFileRepo
					.findByGstinAndTaxPeriodAndReturnTypeOrderByCreatedOnDesc(
							gstin, taxPeriod, APIConstants.GSTR6.toUpperCase());
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
	public void downloadGstr6ErrorResp(String id,
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
						"GSTR6 Sign & File is not initiated for id %s", id);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
		} catch (Exception e) {
			String msg = "Exception occured while getting gstr1Filing data";
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
			String fileName = "GSTR6FilingErrors_" + entity.getGstin() + "_"
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
			String msg = "Error while writing Gstr6 errors to txt";
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
									+ "directory created for Gstr6 errors: '%s'. This will "
									+ "lead to clogging of disk space.",
							tempDir.getAbsolutePath());
					LOGGER.error(msg, ex);
				}
			}

		}

	}

	public static File createTempDir() throws IOException {
		return Files.createTempDirectory("Gstr6Errors").toFile();
	}

}
