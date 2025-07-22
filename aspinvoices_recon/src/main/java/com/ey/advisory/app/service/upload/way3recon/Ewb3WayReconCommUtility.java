package com.ey.advisory.app.service.upload.way3recon;

import static com.ey.advisory.common.GSTConstants.WEB_UPLOAD_KEY;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.GetEwbCntrlRepo;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.ewb.api.GetEWBDetailsByDate;
import com.ey.advisory.ewb.app.api.APIError;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.GetEwbByDateResponseDto;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Component
@Slf4j
public class Ewb3WayReconCommUtility {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	@Autowired
	private GetEwbCntrlRepo getEwbCntrlRepo;

	@Autowired
	@Qualifier("GetEWBDetailsByDateImpl")
	private GetEWBDetailsByDate getEWBDetailsByDate;

	@Autowired
	private AsyncJobsService asyncJobsService;

	private static Map<String, String> Sub_SupplyMapping = null;

	public static String generateDocKey(LocalDate docDate, String sGstin,
			String docType, String docNum) {
		try {
			String fy = GenUtil.getFinYear(docDate);
			sGstin = (sGstin != null) ? sGstin.trim() : "";
			fy = (fy != null) ? fy.trim() : "";
			docType = (docType != null) ? docType.trim() : "";
			docNum = (docNum != null) ? docNum.trim() : "";
			return new StringJoiner(WEB_UPLOAD_KEY).add(fy).add(sGstin)
					.add(docType).add(docNum).toString();
		} catch (Exception e) {

			String msg = "Unable to generate the DocKey";
			LOGGER.error(msg);
			return null;
		}
	}

	static {
		Map<String, String> map = new HashMap<>();

		map.put("1", "Supply");
		map.put("2", "Import");
		map.put("3", "Export");
		map.put("4", "Job Work");
		map.put("5", "For Own Use");
		map.put("6", "Job work Returns");
		map.put("7", "Sales Return");
		map.put("8", "Others");
		map.put("9", "SKD/CKD/Lots");
		map.put("10", "Line Sales");
		map.put("11", "Recipient Not Known");
		map.put("12", "Exhibition or Fairs");

		Sub_SupplyMapping = Collections.unmodifiableMap(map);
	}

	public static String getSubSupplyDesc(String subSupplyType) {
		if (Strings.isNullOrEmpty(subSupplyType))
			return null;

		return Sub_SupplyMapping.get(subSupplyType);
	}

	public GetEwbCntrlEntity logEwbByDateInitiation(String gstin,
			String ewbDate, Boolean isRetry) {

		if (isRetry) {
			LOGGER.debug("gstin {} ", gstin);
			LOGGER.debug("ewbDate {} ", ewbDate);
			Optional<GetEwbCntrlEntity> isAvai = getEwbCntrlRepo
					.isRecAvail(gstin, LocalDate.parse(ewbDate, FORMATTER));

			if (isAvai.isPresent()) {
				return isAvai.get();
			} else {
				return new GetEwbCntrlEntity();
			}
		} else {
			GetEwbCntrlEntity ewbCntrlEnt = new GetEwbCntrlEntity();
			ewbCntrlEnt.setGstin(gstin);
			ewbCntrlEnt.setCreatedBy("SYSTEM");
			ewbCntrlEnt.setCreatedOn(LocalDateTime.now());
			ewbCntrlEnt.setGetStatus(APIConstants.INPROGRESS);
			ewbCntrlEnt.setUpdatedBy("SYSTEM");
			ewbCntrlEnt.setGetCallDate(LocalDate.parse(ewbDate, FORMATTER));
			ewbCntrlEnt.setUpdatedOn(LocalDateTime.now());
			return getEwbCntrlRepo.save(ewbCntrlEnt);
		}
	}

	public void postEwbJobsbasedOnStatus(List<String> distGstin,
			LocalDate fromDate, LocalDate toDate, boolean isNonIniReq) {

		List<Triplet<String, String, Boolean>> combin = new ArrayList<>();
		List<AsyncExecJob> getEwbAsyncJobs = new ArrayList<>();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		List<String> ewbDates = formateDate(fromDate, toDate);
		if (isNonIniReq) {
			for (String suppGstin : distGstin) {
				Optional<GetEwbCntrlEntity> isAvai = getEwbCntrlRepo.isRecAvail(
						suppGstin, LocalDate.parse(ewbDates.get(0), FORMATTER));
				if (!isAvai.isPresent()) {
					combin.add(new Triplet<String, String, Boolean>(suppGstin,
							ewbDates.get(0), false));
				}
			}
		}

		// change required
		List<GetEwbCntrlEntity> cntrlEntity = getEwbCntrlRepo
				.getEwableFailedData(distGstin, fromDate, toDate,
						APIConstants.FAILED);

		for (GetEwbCntrlEntity getEwbCntrlEntity : cntrlEntity) {
			combin.add(new Triplet<String, String, Boolean>(
					getEwbCntrlEntity.getGstin(),
					getEwbCntrlEntity.getGetCallDate().format(FORMATTER),
					true));
		}

		LOGGER.debug("GSTIN and Date Combinations are {}", combin);

		for (Triplet<String, String, Boolean> gstnDateComb : combin) {
			String suppGstin = gstnDateComb.getValue0();
			String ewbDate = gstnDateComb.getValue1();
			Boolean isRetry = gstnDateComb.getValue2();
			GetEwbCntrlEntity cntrlEnt = null;
			try {
				cntrlEnt = logEwbByDateInitiation(suppGstin, ewbDate, isRetry);

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

						getEWBByDateAsync(ewbList, suppGstin, getEwbAsyncJobs);
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

	private static List<String> formateDate(LocalDate fromDate,
			LocalDate toDate) {
		List<String> dates = new ArrayList<>();
		DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		dates.add(f.format((LocalDate) fromDate));
		/*while (!fromDate.plusDays(1).isAfter(toDate)) {
			fromDate = fromDate.plusDays(1);
			dates.add(f.format((LocalDate) fromDate));
		}*/
		while (!fromDate.plusDays(1).isAfter(toDate)) { fromDate =
									  fromDate.plusDays(1); dates.add(f.format((LocalDate) fromDate)); }
				 	
		return dates;
	}
}
