/**
 * 
 */
package com.ey.advisory.processors.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.EwbUploadProcessedHeaderRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbUploadProcessedItemRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetEwbCntrlRepo;
import com.ey.advisory.app.service.upload.way3recon.Ewb3WayReconCommUtility;
import com.ey.advisory.app.service.upload.way3recon.GetEwbCntrlEntity;
import com.ey.advisory.app.service.upload.way3recon.PostManualEwbStatusInputDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.ewb.api.GetEWBDetailsByDate;
import com.ey.advisory.ewb.app.api.APIError;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.GetEwbByDateResponseDto;
import com.ey.advisory.gstnapi.repositories.client.EWBNICUserRepository;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 * 
 */
@Slf4j
@Component("ExtAndPostManualGetEWBJobsProcessor")
public class ExtAndPostManualGetEWBJobsProcessor implements TaskProcessor {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	@Autowired
	EWBNICUserRepository ewbNICUserRepo;

	@Autowired
	Ewb3WayReconCommUtility ewbCommUtility;

	@Autowired
	private GetEwbCntrlRepo getEwbCntrlRepo;

	@Autowired
	@Qualifier("GetEWBDetailsByDateImpl")
	private GetEWBDetailsByDate getEWBDetailsByDate;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("EwbUploadProcessedHeaderRepository")
	private EwbUploadProcessedHeaderRepository psdHeaderRepo;

	@Autowired
	@Qualifier("EwbUploadProcessedItemRepository")
	private EwbUploadProcessedItemRepository psdItemRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		String jsonString = message.getParamsJson();
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("JSON", json);
		}
		PostManualEwbStatusInputDto criteria = gson.fromJson(json,
				PostManualEwbStatusInputDto.class);
		List<String> gstinList = criteria.getGstins();
		LocalDate fromDate = LocalDate.parse(criteria.getFromdate());
		LocalDate toDate = LocalDate.parse(criteria.getToDate());
		String initiatedFor = criteria.getInitiatedFor();

		List<AsyncExecJob> getEwbAsyncJobs = new ArrayList<>();

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Before Triplet Execution");
			}
			List<Triplet<String, String, Boolean>> combin = new ArrayList<>();

			List<LocalDate> dates = new ArrayList<>();
			if (fromDate.isBefore(toDate)) {
				while (fromDate.isBefore(toDate)) {
					dates.add(fromDate);
					fromDate = fromDate.plusDays(1);
				}
				dates.add(fromDate);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("After Triplet Execution Dates : ", dates);
			}
			List<GetEwbCntrlEntity> cntrlEntity = new ArrayList<>();
			List<GetEwbCntrlEntity> successFailedCntrlEntity = new ArrayList<>();
			// List<GetEwbCntrlEntity> availableEntities = getEwbCntrlRepo
			// .getEwableData(gstin, fromDate, toDate);
			for (LocalDate date : dates) {
				for (String gstin : gstinList) {
					GetEwbCntrlEntity entity = new GetEwbCntrlEntity();
					entity.setGstin(gstin);
					entity.setGetCallDate(date);
					Optional<GetEwbCntrlEntity> isAvai = getEwbCntrlRepo
							.isRecAvail(gstin, date);
					if (initiatedFor.equalsIgnoreCase("N")) {
						if (!isAvai.isPresent()) {
							cntrlEntity.add(entity);
						}
					} else if (initiatedFor.equalsIgnoreCase("S")) {
						if (isAvai.get().getGetStatus()
								.equalsIgnoreCase("SUCCESS")) {
							successFailedCntrlEntity.add(isAvai.get());
						}
					}
					// else if (initiatedFor.equalsIgnoreCase("F")) {
					// if (isAvai.get().getGetStatus()
					// .equalsIgnoreCase("FAILED")) {
					// successFailedCntrlEntity.add(isAvai.get());
					// }
					// }
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Control Entity", cntrlEntity);
			}
			for (GetEwbCntrlEntity getEwbCntrlEntity : cntrlEntity) {
				combin.add(
						new Triplet<String, String, Boolean>(
								getEwbCntrlEntity.getGstin(), getEwbCntrlEntity
										.getGetCallDate().format(FORMATTER),
								false));
			}
			for (GetEwbCntrlEntity getEwbCntrlEntity : successFailedCntrlEntity) {
				combin.add(
						new Triplet<String, String, Boolean>(
								getEwbCntrlEntity.getGstin(), getEwbCntrlEntity
										.getGetCallDate().format(FORMATTER),
								true));
			}

			LOGGER.debug("GSTIN and Date Combinations are {}", combin);

			for (Triplet<String, String, Boolean> gstnDateComb : combin) {
				String suppGstin = gstnDateComb.getValue0();
				String ewbDate = gstnDateComb.getValue1();
				Boolean isRetry = gstnDateComb.getValue2();
				GetEwbCntrlEntity cntrlEnt = null;
				try {
					cntrlEnt = ewbCommUtility.logEwbByDateInitiation(suppGstin,
							ewbDate, isRetry);

					APIResponse response = getEWBDetailsByDate
							.getEWBDetailsByDate(ewbDate, suppGstin);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.info("End  GetEwbDetailsByDate ,response = {} ",
								response);
					}
					String errMsg = null;
					String errCode = null;
					String status = null;

					if (response.isSuccess()) {

						status = APIConstants.SUCCESS;
						String jsonResp = response.getResponse();

						TypeToken<List<GetEwbByDateResponseDto>> token = new TypeToken<List<GetEwbByDateResponseDto>>() {
						};
						List<GetEwbByDateResponseDto> getEwbByDateResponseList = gson
								.fromJson(jsonResp, token.getType());

						List<List<GetEwbByDateResponseDto>> ewbChunks = Lists
								.partition(getEwbByDateResponseList, 50);

						for (List<GetEwbByDateResponseDto> ewbList : ewbChunks) {

							getEWBByDateAsync(ewbList, suppGstin,
									getEwbAsyncJobs);
						}
					} else {
						String msg = String.format(
								"No EwayBill's Generated for the date %s and GSTIN %s",
								ewbDate, suppGstin);
						LOGGER.error(msg);
						APIError errDtls = response.getErrors().get(0);
						errMsg = errDtls.getErrorDesc();
						errCode = errDtls.getErrorCode();
						if (errCode.equals("418")) {
							status = APIConstants.SUCCESS_WITH_NO_DATA;
						} else {
							status = APIConstants.FAILED;
						}
					}

					LOGGER.debug("GSTIN {}", suppGstin);

					// LOGGER.debug("EWB Get Date {}", ewbCallDate);

					cntrlEnt.setGetStatus(status);
					cntrlEnt.setErrMsg(Strings.isNullOrEmpty(errCode) ? null
							: errCode + "-" + errMsg);
					cntrlEnt.setUpdatedOn(LocalDateTime.now());
					getEwbCntrlRepo.save(cntrlEnt);
				} catch (Exception e) {
					String msg = String.format(
							"Error occured while invoking GET Ewb by Date API for GSTIN %s Hence skipping the GSTIN",
							suppGstin);
					LOGGER.error(msg, e);
					cntrlEnt = new GetEwbCntrlEntity();
					cntrlEnt.setGetStatus(APIConstants.FAILED);
					cntrlEnt.setErrMsg(e.getMessage());
					cntrlEnt.setUpdatedOn(LocalDateTime.now());
					getEwbCntrlRepo.save(cntrlEnt);
				}
			}
			if (!getEwbAsyncJobs.isEmpty()) {
				asyncJobsService.createJobs(getEwbAsyncJobs);
			}

		} catch (Exception ex) {
			String errMsg = "Error occured while invoking GET eway bill call";
			LOGGER.error(errMsg, ex);

			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

	public void getEWBByDateAsync(
			List<GetEwbByDateResponseDto> getEwbByDateResponseDto,
			String suppGstin, List<AsyncExecJob> getEwbAsyncJobs) {

		String groupCode = TenantContext.getTenantId();

		String ewbNoList = getEwbByDateResponseDto.stream()
				.map(GetEwbByDateResponseDto::getEwbNo)
				.collect(Collectors.joining(","));
		JsonObject jobParamsObj = new JsonObject();

		jobParamsObj.addProperty("ewbNos", ewbNoList);
		jobParamsObj.addProperty("gstin", suppGstin);

		getEwbAsyncJobs.add(asyncJobsService.createJobAndReturn(groupCode,
				JobConstants.Initiate_GET_WAY, jobParamsObj.toString(),
				"SYSTEM", 1L, null, null));

	}
}
