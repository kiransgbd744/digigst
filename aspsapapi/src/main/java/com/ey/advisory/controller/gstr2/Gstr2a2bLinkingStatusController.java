package com.ey.advisory.controller.gstr2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.dashboard.apiCall.APICAllDtls;
import com.ey.advisory.app.dashboard.apiCall.ApiGstinDetailsDto;
import com.ey.advisory.app.dashboard.apiCall.TaxPeriodDetailsDto;
import com.ey.advisory.app.docs.dto.gstr2.GSTR2a2bLinkingStatusReqDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Akhilesh Yadav
 *
 */

@Slf4j
@RestController
public class Gstr2a2bLinkingStatusController {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	DefaultStateCache defaultStateCache;

	@RequestMapping(value = "/ui/getGstr2a2bLinkingStatus", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr2a2bLinkingRecords(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();

		try {
			APICAllDtls apiDtls = new APICAllDtls();
			List<ApiGstinDetailsDto> apiGstinDetails = new ArrayList<>();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Entered API Call for GSTR2A2B Linking Status with input %s",
						jsonString);
				LOGGER.debug(msg);
			}

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			String reqJson = requestObject.get("req").getAsJsonObject()
					.toString();
			GSTR2a2bLinkingStatusReqDto reqDashBoardDto = gson.fromJson(reqJson,
					GSTR2a2bLinkingStatusReqDto.class);

			if (reqDashBoardDto.getEntityId() == null
					|| reqDashBoardDto.getFinancialYear() == null) {

				String msg = "Entity Id, Financial year,"
						+ " should be mandatory";
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}

			String financialPeriod = reqDashBoardDto.getFinancialYear();
			Pair<String, String> fromAndToRetTaxPeriod = null;
			int entityId = reqDashBoardDto.getEntityId().intValue();
			int retPeriodStart;
			int retPeriodEnd;

			fromAndToRetTaxPeriod = GenUtil
					.extractFromAndToRetTaxPeriodsFromFY(financialPeriod);
			retPeriodStart = Integer
					.parseInt(fromAndToRetTaxPeriod.getValue0());
			retPeriodEnd = Integer.parseInt(fromAndToRetTaxPeriod.getValue1());

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_GSTR2A_GSTR2B_TAG_STATUS");

			storedProc.registerStoredProcedureParameter("ENTITY_ID",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("ENTITY_ID", entityId);

			storedProc.registerStoredProcedureParameter("RET_PERIOD_START",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("RET_PERIOD_START", retPeriodStart);

			storedProc.registerStoredProcedureParameter("RET_PERIOD_END",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("RET_PERIOD_END", retPeriodEnd);

			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();

			Map<String, List<Object[]>> gstnLinkMap = null;
			if (list != null && !list.isEmpty()) {

				gstnLinkMap = new LinkedHashMap<>();
				for (Object[] obj : list) {
					gstnLinkMap.computeIfAbsent(obj[0].toString(),
							k -> new ArrayList<>()).add(obj);
				}
			} else {
				String msg = "No records found!";
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(msg));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

			}

			Pair<Map<String, String>, Map<String, String>> gstnRegMap = commonUtility
					.getGstnRegMap();
			Map<String, String> gstinAuthMap = gstnRegMap.getValue0();
			Map<String, String> regTypeMap = gstnRegMap.getValue1();

			gstnLinkMap.entrySet().forEach(entry -> {
				List<TaxPeriodDetailsDto> taxPeriodDetails = new ArrayList<>();
				ApiGstinDetailsDto dto = new ApiGstinDetailsDto();
				List<Object[]> gstnsKey = entry.getValue();
				taxPeriodDetails.addAll(
						gstnsKey.stream().map(o -> converttoDto(o)).collect(
								Collectors.toCollection(ArrayList::new)));
				dto.setGstin(entry.getKey());
				dto.setTaxPeriodDetails(taxPeriodDetails);
				apiGstinDetails.add(dto);

			});

			apiGstinDetails.forEach(o -> {
				o.setAuthStatus(gstinAuthMap.get(o.getGstin()));
				o.setRegistrationType(regTypeMap.get(o.getGstin()));
				/*
				 * o.setStateName(defaultStateCache .getStateName(o.getGstin().substring(0,
				 * 2)));
				 */
				String stateName = defaultStateCache
						.getStateName(o.getGstin().substring(0, 2));
				
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"GSTR2A2B Linking GSTN : %s  and State Name : %s",
							o.getGstin(), stateName);
					LOGGER.debug(msg);
				}

				if (stateName != null) {
					o.setStateName(stateName);
				}
			});

			apiDtls.setApiGstinDetails(apiGstinDetails);

			JsonElement respBody = gson.toJsonTree(apiDtls);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

	}

	public TaxPeriodDetailsDto converttoDto(Object[] arr) {

		TaxPeriodDetailsDto obj = new TaxPeriodDetailsDto();
		obj.setTaxPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setApiStatus(arr[2] != null ? arr[2].toString() : null);
		obj.setInitiatedOn(arr[3] != null
				? EYDateUtil.toISTDateTimeFromUTC((Timestamp) arr[3]) : null);
		obj.setLinkedCount(arr[4] != null ? arr[4].toString() : null);
		obj.setNotLinkedCount(arr[5] != null ? arr[5].toString() : null);
		obj.setGstr1NotFiledCount(arr[6] != null ? arr[6].toString() : null);
		return obj;

	}

}
