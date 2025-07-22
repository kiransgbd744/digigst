package com.ey.advisory.controller;

import java.text.Collator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.docs.dto.TaxPeriodDto;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.FinancialYearDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@RestController
@Slf4j
public class CommonController {

	@Autowired
	private GstrReturnStatusRepository gstnRetStatRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfPrmRepository;

	@GetMapping(value = "/ui/getAllFy", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllFy() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			List<FinancialYearDto> finYearList = CommonUtility
					.getValidFinYear();

			String jsonEINV = gson.toJson(finYearList);
			JsonElement financialYearElement = new JsonParser().parse(jsonEINV);
			resp.add("finYears", financialYearElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching all financial years";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@GetMapping(value = "/ui/getAllFyFor2B", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllFyFor2B() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			int currYear = LocalDate.now().getYear();
			int month = LocalDate.now().getMonthValue();
			int startYear = 2020;
			List<FinancialYearDto> finYearList = new ArrayList<>();
			for (int year = startYear; year < currYear; year++) {
				FinancialYearDto dto = new FinancialYearDto();
				String finYear = null;
				finYear = year + "-" + String.valueOf(year + 1).substring(2);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Financial year : {} ", finYear);
				dto.setFy(finYear);
				finYearList.add(dto);
			}
			if (month > 3) {
				FinancialYearDto dto = new FinancialYearDto();
				String currentFy = GenUtil.getCurrentFinancialYear();
				dto.setFy(currentFy);
				finYearList.add(dto);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("CurrentFinancial year : {} ", currentFy);
			}
			Collator collator = Collator.getInstance();
			finYearList
					.sort((o1, o2) -> collator.compare(o2.getFy(), o1.getFy()));

			String jsonEINV = gson.toJson(finYearList);
			JsonElement financialYearElement = new JsonParser().parse(jsonEINV);
			resp.add("finYears", financialYearElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while fetching all financial years"
					+ " for 2B";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getITC04taxPeriods", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getITC04taxPeriods(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String fy = requestObject.get("fy").getAsString();
			if (StringUtils.isEmpty(fy)) {
				throw new AppException("Invalid FY");
			}
			int finYear = Integer.parseInt(fy.substring(0, 4));
			List<TaxPeriodDto> taxPeriodList = new ArrayList<>();
			if (finYear <= 2021) {
				taxPeriodList.add(new TaxPeriodDto("Q1 (Apr-Jun)", "A", "Q1"));
				taxPeriodList.add(new TaxPeriodDto("Q2 (Jul-Sep)", "B", "Q2"));
				if (finYear < 2021) {
					taxPeriodList
							.add(new TaxPeriodDto("Q3 (Oct-Dec)", "C", "Q3"));
					taxPeriodList
							.add(new TaxPeriodDto("Q4 (Jan-Mar)", "D", "Q4"));
				} else {
					taxPeriodList
							.add(new TaxPeriodDto("H2 (Oct-Mar)", "C", "H2"));
				}
			} else {
				taxPeriodList.add(new TaxPeriodDto("H1 (Apr-Sep)", "A", "H1"));
				taxPeriodList.add(new TaxPeriodDto("H2 (Oct-Mar)", "B", "H2"));
			}
			Collections.sort(taxPeriodList,
					(a, b) -> a.getOrder().compareToIgnoreCase(b.getOrder()));

			String jsonEINV = gson.toJson(taxPeriodList);
			JsonElement taxPeriodElement = new JsonParser().parse(jsonEINV);
			resp.add("taxPeriodList", taxPeriodElement);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error while fetching taxPeriods";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/gstinIsFiled", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstinIsFiled(@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		JsonObject json = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE getGstr3BgstnDashboard");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");
			String taxPeriod = requestObject.has("taxPeriod")
					? requestObject.get("taxPeriod").getAsString() : "";
			if (Strings.isNullOrEmpty(taxPeriod))
				throw new AppException("tax period cannot be empty");

			String gstin = requestObject.has("gstin")
					? requestObject.get("gstin").getAsString() : "";
			if (Strings.isNullOrEmpty(gstin))
				throw new AppException("gstin cannot be empty");

			String returnType = requestObject.has("returnType")
					? requestObject.get("returnType").getAsString() : "";
			if (Strings.isNullOrEmpty(returnType))
				throw new AppException("returnType cannot be empty");
			if ("GSTR1".equalsIgnoreCase(returnType)) {
				String answer = entityConfPrmRepository.findOnboardingAnswer(
						TenantContext.getTenantId(), gstin,
						CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O36.name());
				if (answer == null || GSTConstants.B.equalsIgnoreCase(answer)) {
					json.addProperty("dataEditable", true);
					JsonObject resps = new JsonObject();
					JsonElement respBody = gson.toJsonTree(json);
					resps.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					resps.add("resp", respBody);
					return new ResponseEntity<>(resps.toString(),
							HttpStatus.OK);
				}
			}
			List<GstrReturnStatusEntity> resultSet = gstnRetStatRepo
					.findByGstinInAndTaxPeriodAndReturnTypeInAndStatusIgnoreCase(
							Arrays.asList(gstin), taxPeriod,
							Arrays.asList(returnType), "FILED");

			if (!resultSet.isEmpty()) {
				if ("FILED".equalsIgnoreCase(resultSet.get(0).getStatus()))
					json.addProperty("dataEditable", false);
				else
					json.addProperty("dataEditable", true);
			} else {
				json.addProperty("dataEditable", true);
			}

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(json);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (AppException ex) {
			String msg = ex.getMessage();
			LOGGER.error(ex.getMessage(), ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while checking gstin isFiled ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

}
