package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.ReturnFilingConfigEntity;
import com.ey.advisory.app.data.repositories.client.ReturnFilingConfigRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.FinancialYearDto;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@RestController
@Slf4j
public class ReturnFilingMyFileStatusController {

	@Autowired
	GstnReturnFilingStatus gstnReturnFiling;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("ReturnFilingConfigRepository")
	ReturnFilingConfigRepository returnFilingConfigRepo;

	@PostMapping(value = "/ui/getMyFileStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getMyFileStatus(@RequestBody String jsonReq) {
		Gson gson = new Gson();
		JsonObject resp = new JsonObject();

		try {
			JsonObject requestObj = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			JsonArray gstin = requestObj.getAsJsonArray("gstins");
			JsonArray rtTyps = requestObj.getAsJsonArray("returnTypes");
			String returnPeriod = requestObj.has("returnPeriod")
					? requestObj.get("returnPeriod").getAsString() : "";
			String finYear = requestObj.has("finYear")
					? requestObj.get("finYear").getAsString() : "";
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			List<String> gstins = gson.fromJson(gstin, listType);
			List<String> returnTypes = gson.fromJson(rtTyps, listType);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"About to get the Filling status for FY {} and GSTINS {}",
						finYear, gstins);

			PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
					PublicApiConstants.RET_FILING_UI_SEARCH);

			List<ReturnFilingGstnResponseDto> retFilingList = gstnReturnFiling
					.callGstnApi(gstins, finYear, false);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Before Filtering the Filling status Response {}",
						retFilingList);
			gstnReturnFiling.persistReturnFillingStatus(retFilingList, false);
			if (returnTypes != null && !returnTypes.isEmpty()) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"Filtering the Filling status on return Types {}",
							returnTypes);
				retFilingList = retFilingList.stream()
						.filter(item -> returnTypes.contains(item.getRetType()))
						.collect(Collectors.toCollection(ArrayList::new));
			}
			if (returnPeriod != null && !returnPeriod.isEmpty()) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"Filtering the Filling status on returnPeriod {}",
							returnPeriod);
				retFilingList = retFilingList.stream().filter(
						item -> returnPeriod.contains(item.getRetPeriod()))
						.collect(Collectors.toCollection(ArrayList::new));
			}
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("After Filtering the Filling status Response  {}",
						retFilingList);
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(retFilingList);
			gstinResp.add("filingStatus", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Fetching ReturnFilling Status ", ex);
			resp.add("resp", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("hdr", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/api/getMyFileStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getApiMyFileStatus(
			@RequestBody String jsonReq) {
		Gson gson = new Gson();
		JsonObject resp = new JsonObject();

		ReturnFilingConfigEntity entity = new ReturnFilingConfigEntity();
		try {
			Long requestID = generateCustomId(entityManager);
			entity.setDateOfUpload(LocalDateTime.now());
			entity.setRequestId(requestID);
			entity.setCreatedBy("API");

			JsonObject requestObj = null;
			try {
				requestObj = (new JsonParser()).parse(jsonReq)
						.getAsJsonObject();
			} catch (JsonParseException ex) {
				throw new AppException("Invalid request payload");
			}

			// json check
			if (!requestObj.has("gstins") || !requestObj.has("finYear"))
				throw new AppException(
						"GSTINS and FINANCIAL YEAR is mandatory");
			JsonArray gstin = requestObj.getAsJsonArray("gstins");
			if (gstin.size() == 0)
				throw new AppException("Provide GSTIN");

			String finYear = requestObj.has("finYear")
					? requestObj.get("finYear").getAsString() : "";
			Type listType = new TypeToken<ArrayList<String>>() {
			}.getType();
			List<String> gstins = gson.fromJson(gstin, listType);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"About to get the Filling status for FY {} and GSTINS {}",
						finYear, gstins);

			List<JsonObject> responseList = new ArrayList<>();
			List<FinancialYearDto> dtoList = CommonUtility.getValidFinYear();
			List<String> fy = dtoList.stream().map(FinancialYearDto::getFy)
					.collect(Collectors.toList());

			// financial year check
			if (fy.contains(finYear)) {

				for (String gstn : gstins) {
					if (gstn == null || gstn.isEmpty())
						throw new AppException("GSTIN cannot be empty");
					
					// gstin check
					if (gstn.toUpperCase().length() != 15
							|| !gstn.toUpperCase().matches("[A-Za-z0-9]+")
							|| gstn.toUpperCase().matches("[A-Za-z]+")
							|| gstn.toUpperCase().matches("[0-9]+")) {
						JsonObject response = new JsonObject();
						response.addProperty("gstin", gstn);
						response.addProperty("msg", "GSTIN is not valid");
						responseList.add(response);
					} else {
						PublicApiContext.setContextMap(
								PublicApiConstants.SOURCE,
								PublicApiConstants.RET_FILING_API);

						List<ReturnFilingGstnResponseDto> retFilingList = gstnReturnFiling
								.callGstnApi(Arrays.asList(gstn.toUpperCase()),
										finYear, false);
						for (ReturnFilingGstnResponseDto respApi : retFilingList) {
							JsonObject response = new JsonObject();
							if (respApi.getStatus() != null) {
								response.addProperty("arnNo",
										respApi.getArnNo());
								response.addProperty("retPeriod",
										respApi.getRetPeriod());
								response.addProperty("filingDate",
										respApi.getFilingDate());
								response.addProperty("gstin",
										respApi.getGstin());
								response.addProperty("retType",
										respApi.getRetType());
								response.addProperty("status",
										respApi.getStatus());
							} else {
								response.addProperty("gstin", gstn);
								response.addProperty("msg",
										respApi.getErrMsg());
							}
							responseList.add(response);
						}

					}
					
				}
			} else {
				throw new AppException("Invalid Financial Year");
			}

			entity.setNoOfGstins((long) gstins.size());
			entity.setStatus("COMPLETED");
			returnFilingConfigRepo.save(entity);

			JsonElement respBody = gson.toJsonTree(responseList);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception while Fetching ReturnFilling Status ", ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			entity.setStatus("FAILED");
			entity.setNoOfGstins(0L);
			returnFilingConfigRepo.save(entity);
			resp.add("errMsg", gson.toJsonTree(ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RETURN_FILING_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}

	private static Long generateCustomId(EntityManager entityManager) {
		String reportId = "";
		String digits = "";
		Long nextSequencevalue = getNextSequencevalue(entityManager);

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		String currentDate = currentYear + (currentMonth < 10
				? ("0" + currentMonth) : String.valueOf(currentMonth));
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}
}
