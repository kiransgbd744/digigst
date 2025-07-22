package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.LedgerSummaryBalanceRepository;
import com.ey.advisory.app.docs.dto.ledger.LedgerBalanceJobTriggerDto;
import com.ey.advisory.app.services.ledger.LedgerTotalBalanceSummServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
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

/**
 * @author Arun.KA
 *
 */

@RestController
@Slf4j
public class LedgerRefreshController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;
	/*//for testing
	@Autowired
	LedgerTotalBalanceSummServiceImplTest ledgerTotalBalanceSummServiceImpl;
	//for testing
*/	
	@Autowired
	@Qualifier(value = "LedgerSummaryBalanceRepository")
	private LedgerSummaryBalanceRepository ledgerSummaryBalance;

	@PostMapping(value = "/ui/LedgerRefresh", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getLedgerRefresh(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LocalDateTime dateAndTime = LocalDateTime.now();
		List<String> notInprogressGstinsList = new ArrayList<>();
		
		try {
			
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			gson = GsonUtil.newSAPGsonInstance();
			Long entityId = reqObject.get("entityId").getAsLong();
			LocalDate currentDate = LocalDate.now();
			LocalDate startOfMonth = currentDate.withDayOfMonth(1);
			String taxPeriod = startOfMonth.format(DateTimeFormatter.ofPattern("MMyyyy"));
			
			//String taxPeriod = reqObject.get("taxPeriod").getAsString();
			List<String> ledgerGstinList = new ArrayList<>();
			if (reqObject.has("gstins")) {
				JsonArray gstinArray = reqObject
						.getAsJsonArray("gstins");
				Type listType = new TypeToken<ArrayList<String>>() {
				}.getType();

				ledgerGstinList = gson.fromJson(gstinArray, listType);
			}
			List<String> activeGstnList = new ArrayList<>();
			//activeGstnList.add("33GSPTN0481G1ZA");
			JsonArray getRespBody=null;
			if (!ledgerGstinList.isEmpty()) 
			{
				 getRespBody = getAllActiveGstnList(ledgerGstinList, taxPeriod,
						activeGstnList);
			}
			else
			{
				List<String> gstinList = gSTNDetailRepository
						.filterGstinBasedOnRegTypeforACD(entityId);
				 getRespBody = getAllActiveGstnList(gstinList, taxPeriod,
						activeGstnList);
			}
			
			if (!activeGstnList.isEmpty()) {
				//186759 us//trying to get the gstins where get 
				//call status not in progress and Initiated
				
				 // Step 1: Get GSTINs already present and eligible
		        List<String> existing = ledgerSummaryBalance.findActiveSupplierGstin(activeGstnList);

		        // Step 2: Calculate GSTINs not present at all
		        List<String> missing = new ArrayList<>(activeGstnList);
		        missing.removeAll(existing);

		        // Step 3: Merge missing and eligible GSTINs
		        notInprogressGstinsList.addAll(existing);
		        notInprogressGstinsList.addAll(missing);

		        // Step 4: Update and initiate
		        ledgerSummaryBalance.updateStatusAndLastUpdatedDate("Initiated", dateAndTime, notInprogressGstinsList);
		        initiateLedgerGetcall(notInprogressGstinsList, taxPeriod);
				/*notInprogressGstinsList = ledgerSummaryBalance
						.findActiveSupplierGstin(activeGstnList);
				
				ledgerSummaryBalance.updateStatusAndLastUpdatedDate(
						"Initiated", dateAndTime, notInprogressGstinsList);
				initiateLedgerGetcall(notInprogressGstinsList, taxPeriod);*/
			}
			if (getRespBody != null) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("InActive GSTNs found ");
				}
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(getRespBody));

				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {

			ledgerSummaryBalance.updateStatusAndLastUpdatedDate("Failed",
					dateAndTime, notInprogressGstinsList);
			String msg = String.format(
					"Error Occured while Ledger Refresh '%s'", e.getMessage());
			APIRespDto dto = new APIRespDto("Failed", msg);
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	/**
	 * @param entityId
	 * @param taxPeriod
	 * @param getRespBody
	 */
	private void initiateLedgerGetcall(List<String> gstins, String taxPeriod) {
		LocalDateTime dateAndTime = LocalDateTime.now();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"About to Initiate Ledger Refresh for gstin '%s', Taxperiod '%s'",
						gstins, taxPeriod);
				LOGGER.debug(msg);
			}
			Gson gson = new Gson();
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			String groupCode = TenantContext.getTenantId();
			

			JsonObject jsonParams = new JsonObject();
			jsonParams.add("gstins", gson.toJsonTree(gstins).getAsJsonArray());
			jsonParams.addProperty("taxPeriod", taxPeriod);
			/*//for testing
			LedgerBalanceJobTriggerDto ledgerBalanceJobTriggerDto = new LedgerBalanceJobTriggerDto();
			ledgerBalanceJobTriggerDto.setGstins(gstins);
			ledgerBalanceJobTriggerDto.setTaxPeriod(taxPeriod);
			ledgerTotalBalanceSummServiceImpl.getLedgerTotalBalance(ledgerBalanceJobTriggerDto);
			//for testing
*/			asyncJobsService.createJob(groupCode, JobConstants.LEDGER_REFRESH,
					jsonParams.toString(), userName, 1L, null, null);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Initiated Ledger Refresh for Entity '%s', Taxperiod '%s'",
						gstins, taxPeriod);
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			
			ledgerSummaryBalance.updateStatusAndLastUpdatedDate("Failed",
					dateAndTime, gstins);
			String msg = String.format("Exception while Ledger Refresh");
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private JsonArray getAllActiveGstnList(List<String> gstins,
			String taxPeriod, List<String> activeGstnList) {
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Getting all the active GSTNs for GSTR1 GET-EInvoices");
			}

			String msg = "";

			JsonArray respBody = new JsonArray();
			if (gstins != null && !gstins.isEmpty()) {

				for (String gstin : gstins) {
					JsonObject json = new JsonObject();
					String authStatus = authTokenService
							.getAuthTokenStatusForGstin(gstin);
					if ("A".equalsIgnoreCase(authStatus)) {
						activeGstnList.add(gstin);
						msg = "Auth Token Active, Ledger get call successfully initiated";

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
