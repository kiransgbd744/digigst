package com.ey.advisory.controllers.anexure1;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.ledger.CreditLedgerBulkDetailsDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class GetBulkLiabilityLedgerDetailsController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	/*
	 * @Autowired LiabilityLedgerReportServiceImpl
	 * liabilityLedgerReportServiceImpl;
	 */

	@PostMapping(value = "/ui/getBulkLiabilityLedgerDetails")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getBulkLiabilityLedgerDetails Download CSV Report Controller";
			LOGGER.debug(msg);
		}
		JsonObject resp = new JsonObject();
		JsonObject requestObject = JsonParser.parseString(jsonParams)
				.getAsJsonObject();
		JsonObject jsonreq = requestObject.get("req").getAsJsonObject();

		JsonArray json = jsonreq.get("ledgerDetails").getAsJsonArray();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();
		//Set<String> gstnlist = new HashSet<String>();
		List<String> gstnlist= new ArrayList<>();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName() != null
				? user.getUserPrincipalName() : "SYSTEM";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download get bulk liability ledger report: %s",
					json.toString());
			LOGGER.debug(msg);
		}
		Type listType = new TypeToken<List<CreditLedgerBulkDetailsDto>>() {
		}.getType();
		List<CreditLedgerBulkDetailsDto> reqDto = gson.fromJson(json, listType);

		try {
			for (CreditLedgerBulkDetailsDto x : reqDto) {
				gstnlist.add(x.getGstin());
			}
			List<String> activeGstnList = new ArrayList<>();
			JsonArray getRespBody = getAllActiveGstnList(gstnlist,
					activeGstnList);

			JsonObject jobParams = new JsonObject();
			if (!activeGstnList.isEmpty()) {
				FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
				entity.setCreatedBy(userName);
				entity.setReportCateg("Ledger");
				entity.setReportType("Liability Ledger");
				entity.setDataType("Ledger");
				entity.setCreatedDate(LocalDateTime.now());
				entity.setReportStatus(ReportStatusConstants.INITIATED);
				entity.setReqPayload(jsonreq.toString());
				entity = downloadRepository.save(entity);
				Long id = entity.getId();

				jobParams.addProperty("id", id);
				jobParams.add("gstins",
						gson.toJsonTree(activeGstnList).getAsJsonArray());

				asyncJobsService.createJob(groupCode,
						JobConstants.LIABILITY_LEDGER_REPORT,
						jobParams.toString(),
						userName, 1L, null, null);
				/*
				 * //tetsing have to remove List<String> activeGstns = new
				 * ArrayList<>(); activeGstns.add("29AAAPH9357H1A2");
				 * activeGstns.add("29AAAPH9357H000");
				 * //liabilityLedgerReportServiceImpl.getLiabilityLedgerReport(
				 * id,"102023","112023",activeGstns); //testing
				 */ resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("respBody", gson.toJsonTree(getRespBody));
				jobParams.addProperty("reportType", "Liability Ledger");
				jobParams.addProperty("DataType", "Ledger");
				resp.add("jobParams", gson.toJsonTree(jobParams));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("respBody", gson.toJsonTree(getRespBody));
			resp.add("jobParams", gson.toJsonTree(jobParams));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in creating Async Report GetBulkCreditLedgerDetailsController"
							+ e.getMessage());
			resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in GetBulkCreditLedgerDetailsController";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	private JsonArray getAllActiveGstnList(List<String> gstnlist,
			List<String> activeGstnList) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Getting all the active GSTNs");
			}

			String msg = "";
		//	List<String> gstnListAsList = new ArrayList<>(gstnlist);
			JsonArray respBody = new JsonArray();
			if (!gstnlist.isEmpty()) {
				Map<String, String> authTokenStatusForGstins = authTokenService
						.getAuthTokenStatusForGstins(gstnlist);

				for (Map.Entry<String, String> entry : authTokenStatusForGstins
						.entrySet()) {
					String gstin = entry.getKey();
					String authTokenStatus = entry.getValue();

					// Create a new JsonObject for each iteration
					JsonObject json = new JsonObject();

					if ("A".equalsIgnoreCase(authTokenStatus)) {
						activeGstnList.add(gstin);
						msg = "Auth Token Active, Ledger report Download initiated Successfully";
					} else {
						msg = "Auth Token is Inactive, Please Activate";
					}

					json.addProperty("gstin", gstin);
					json.addProperty("msg", msg);
					respBody.add(json);
				}
			}

			return respBody;

		} catch (Exception ex) {
			String msg = String
					.format("Exception while getAllActiveGstnList method");
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

}
