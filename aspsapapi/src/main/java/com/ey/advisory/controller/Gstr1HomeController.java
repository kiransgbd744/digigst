package com.ey.advisory.controller;

import static com.ey.advisory.common.GSTConstants.FYYEAR;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.ApiSummaryReqDto;
import com.ey.advisory.app.docs.dto.ApiSummaryResDto;
import com.ey.advisory.app.docs.dto.Gstr1FinancialYearDto;
import com.ey.advisory.app.services.search.apisummarysearch.ApiSummarySearchService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
/**
 * 
 * @author Mahesh.Golla
 * Home Page API(Financial Year)
 *
 */
/**
 * This class responsible for getting the financial years from starting FY is
 * 01/04/2017 then it will calculate the from start date 
 *
 *
 */

@RestController
public class Gstr1HomeController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1HomeController.class);
	
	@Autowired
	@Qualifier("apiSummarySearchService")
	private ApiSummarySearchService apiSummarySearchService;
	/**
	 * @param 
	 * @return ResponseEntity<String>
	 */

	@PostMapping(value = "/ui/financialYear", produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> eyGstr1Summary() {

		String groupCode = TenantContext.getTenantId();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		}
		// It will take the present Date of System
		LocalDate today = LocalDate.now();
		// It is the start date of GST Financial Year
		LocalDate startingFI = LocalDate.of(2017, Month.APRIL, 01);
		// Different between the Two dates of GST 
		Period intervalPeriod = Period.between(startingFI, today);
		int diffYear = intervalPeriod.getYears();
         
		//getting the present Year and starting Year of GST FY
		int startYear = startingFI.getYear();

		JsonObject resps = new JsonObject();

		List<Gstr1FinancialYearDto> totalFY = new ArrayList<>();

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			for (int i = 0; i <= diffYear; i++) {

		            Gstr1FinancialYearDto financialDto =
							                    new Gstr1FinancialYearDto();
					financialDto.setFinancialYear(
							startYear + FYYEAR + (startYear + 1));
					startYear++;
					totalFY.add(financialDto);
			}
			Collections.reverse(totalFY);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", gson.toJsonTree(totalFY));
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing financial " + "Year ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	
	/**
	 * API summary 
	 * @param jsonString
	 * @return
	 */
	
	@PostMapping(value = "/ui/apiSummary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> apiSummaryByReceivedDate(
			                             @RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		}
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			ApiSummaryReqDto apiSumRequest = gson.fromJson(reqJson.toString(),
					ApiSummaryReqDto.class);
			@SuppressWarnings("unused")
			List<ApiSummaryResDto> resp = apiSummarySearchService
					                    .<ApiSummaryResDto>find(apiSumRequest);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		}

		catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Gstr1 "
					+ "Summary Documents ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
}