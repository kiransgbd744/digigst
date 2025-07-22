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
 * @author Siva.Reddy
 *
 * 
 */
@Slf4j
@Component("ExtAndPostGetEWBJobsProcessor")
public class ExtAndPostGetEWBJobsProcessor implements TaskProcessor {

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

		JsonParser parser = new JsonParser();
		String jsonString = message.getParamsJson();
		JsonObject json = (JsonObject) parser.parse(jsonString);
		String ewbCallDate = json.get("ewbCallDate").getAsString();
		List<AsyncExecJob> getEwbAsyncJobs = new ArrayList<>();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {

			List<String> distGstin = ewbNICUserRepo.getDistinctGstins();
			List<Triplet<String, String, Boolean>> combin = new ArrayList<>();

			
			for (String suppGstin : distGstin) {
				Optional<GetEwbCntrlEntity> isAvai = getEwbCntrlRepo.isRecAvail(
						suppGstin, LocalDate.parse(ewbCallDate, FORMATTER));
				if (!isAvai.isPresent()) {
					combin.add(new Triplet<String, String, Boolean>(suppGstin,
							ewbCallDate, false));
				} else {
					combin.add(new Triplet<String, String, Boolean>(suppGstin,
							ewbCallDate, true));
				}
			}

			List<GetEwbCntrlEntity> cntrlEntity = getEwbCntrlRepo
					.findByGstinInAndGetStatus(distGstin, APIConstants.FAILED);

			for (GetEwbCntrlEntity getEwbCntrlEntity : cntrlEntity) {
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

					LOGGER.debug("EWB Get Date {}", ewbCallDate);

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
