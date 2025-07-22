package com.ey.advisory.controllers.anexure1;

import java.lang.reflect.Type;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class GetBulkCreditLedgerDetailsController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	/*@Autowired
	GetRcmOpeningBalanceReportProcessorTest getRcmOpeningBalanceReportProcessorTest;
*/
	@PostMapping(value = "/ui/getBulkCreditAndCashAndCrRevAndReclaimDetails")
	public ResponseEntity<String> ReportProcess(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getBulkCreditLedgerDetails Download CSV Report Controller";
			LOGGER.debug(msg);
		}
		JsonObject requestObject = JsonParser.parseString(jsonParams)
				.getAsJsonObject();
		JsonObject jsonreq = requestObject.get("req").getAsJsonObject();
		JsonArray json = jsonreq.get("ledgerDetails").getAsJsonArray();
		String reportTypeVal = jsonreq.get("reportType").getAsString();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();
		Set<String> gstnlist = new HashSet<String>();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName() != null
				? user.getUserPrincipalName() : "SYSTEM";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download get bulk credit ledger report: %s",
					json.toString());
			LOGGER.debug(msg);
		}
		Type listType = new TypeToken<List<GstnData>>() {
		}.getType();
		List<GstnData> reqDto = gson.fromJson(json, listType);
		try {
			for (GstnData x : reqDto) {
				gstnlist.add(x.getGstin());
			}
			JsonElement respBody = gson.toJsonTree(reqDto);
			jsonreq.add("reqDto", respBody);
			
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setGstins(GenUtil
					.convertStringToClob(convertToQueryFormat(gstnlist)));
			entity.setCreatedBy(userName);
			entity.setReportCateg("Ledger");
			entity.setReportType(reportTypeVal);
			entity.setDataType("Ledger");
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(jsonreq.toString());
			downloadRepository.save(entity);
			Long id = entity.getId();
			/*// for testing
			creditAndCashLedgerReportProcessorTest.executeTest(id);
			//
*/			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			asyncJobsService.createJob(groupCode,
					JobConstants.CREDIT_AND_CASH_AND_REVERSAL_AND_RECLAIM_REPORT, jobParams.toString(),
					userName, 1L, null, null);
			JsonObject resp = new JsonObject();
			if (reportTypeVal.equalsIgnoreCase("Credit Ledger")) {
				jobParams.addProperty("reportType", "Credit Ledger");
			} else if(reportTypeVal.equalsIgnoreCase("Cash Ledger")) {
				jobParams.addProperty("reportType", "Cash Ledger");
			}else if(reportTypeVal.equalsIgnoreCase("Reversal & Reclaim Ledger"))
				jobParams.addProperty("reportType", "Reversal & Reclaim Ledger");
			else
			{
				jobParams.addProperty("reportType", "Invalid ReportType");
			}
			
			jobParams.addProperty("DataType", "Ledger");
			JsonElement resps = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", resps);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in creating Async Report GetBulkCreditLedgerDetailsController"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in GetBulkCreditLedgerDetailsController";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}
	
	@PostMapping(value = "/ui/getOpeningBalanceRcmDetails")
	public ResponseEntity<String> getOpeningBalanceRcmDetails(
			@RequestBody String jsonParams) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside getOpeningBalanceRcmDetails Download CSV Report Controller";
			LOGGER.debug(msg);
		}
		JsonObject requestObject = JsonParser.parseString(jsonParams)
				.getAsJsonObject();
		JsonObject jsonreq = requestObject.get("req").getAsJsonObject();
		JsonArray json = jsonreq.get("ledgerDetails").getAsJsonArray();
		String reportTypeVal = jsonreq.get("reportType").getAsString();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String groupCode = TenantContext.getTenantId();
		Set<String> gstnlist = new HashSet<String>();
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName() != null
				? user.getUserPrincipalName() : "SYSTEM";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Request for Download get Opening Balance Rcm Details ledger report: %s",
					json.toString());
			LOGGER.debug(msg);
		}
		Type listType = new TypeToken<List<GstnData>>() {
		}.getType();
		List<GstnData> reqDto = gson.fromJson(json, listType);
		try {
			for (GstnData x : reqDto) {
				gstnlist.add(x.getGstin());
			}
			JsonElement respBody = gson.toJsonTree(reqDto);
			jsonreq.add("reqDto", respBody);
			
			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
			entity.setGstins(GenUtil
					.convertStringToClob(convertToQueryFormat(gstnlist)));
			entity.setCreatedBy(userName);
			entity.setReportCateg("Ledger");
			entity.setReportType(reportTypeVal);
			entity.setDataType("Ledger");
			entity.setCreatedDate(LocalDateTime.now());
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReqPayload(jsonreq.toString());
			downloadRepository.save(entity);
			Long id = entity.getId();
			/*// for testing
			getRcmOpeningBalanceReportProcessorTest.execute(id);
			//
*/			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			asyncJobsService.createJob(groupCode,
					JobConstants.GET_RCM_OPENING_BALANCE_REPORT, jobParams.toString(),
					userName, 1L, null, null);
			JsonObject resp = new JsonObject();
			if (reportTypeVal.equalsIgnoreCase("ITC RCM Ledger (Opening Balance)")) {
				jobParams.addProperty("reportType", "ITC RCM Ledger (Opening Balance)");
			}else if(reportTypeVal.equalsIgnoreCase("ITC Reversal & Re-Claim Ledger (Opening Balance)")){
				jobParams.addProperty("reportType", "ITC Reversal & Re-Claim Ledger (Opening Balance)");
			}else{
				jobParams.addProperty("reportType", "Invalid ReportType");
			}
			
			jobParams.addProperty("DataType", "Ledger");
			JsonElement resps = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", resps);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Unexpected occured in creating Async Report GetBulkCreditLedgerDetailsController"
							+ e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in GetBulkCreditLedgerDetailsController";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	public static Clob convertStringToClob(String data) {
		Clob responseClob = null;
		try {
			if (!Strings.isNullOrEmpty(data)) {
				responseClob = new javax.sql.rowset.serial.SerialClob(
						data.toCharArray());
			}

		} catch (Exception e) {

			String msg = "Exception occured while converting String to Clob";
			LOGGER.error(msg);
		}
		return responseClob;
	}

	private String convertToQueryFormat(Set<String> gstinset) {

		List<String> list = new ArrayList<String>();
		list.addAll(gstinset);

		String queryData = null;
		if (list == null || list.isEmpty())
			return null;

		queryData = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			queryData += "," + list.get(i);
		}

		return queryData;
	}

	@Data
	public class GstnData {
		private String gstin;
	}

}
