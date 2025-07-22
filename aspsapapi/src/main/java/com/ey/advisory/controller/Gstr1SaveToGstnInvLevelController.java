/**
 * 
 */
package com.ey.advisory.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1SummarySaveStatusRespDto;
import com.ey.advisory.app.processors.handler.Gstr1SaveToGstnResetHandler;
import com.ey.advisory.app.services.daos.get2a.GetGstr1SummarySaveStatusService;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@RestController
public class Gstr1SaveToGstnInvLevelController {

	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Autowired
	private Gstr1SaveToGstnResetHandler resetHandler;

	@Autowired
	@Qualifier("DocRepository")
	DocRepository docRepository;

	@Autowired
	@Qualifier("GetGstr1SummarySaveStatusService")
	private GetGstr1SummarySaveStatusService gstr1SummarySaveStatusService;

	@PostMapping(value = "/ui/gstr1SaveToGstnInvLvl", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr1SaveToGstnJob(
			@RequestBody String request) {
		JsonObject resp = new JsonObject();
		String msg = "";
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respBody = new JsonArray();
		List<Gstr1GetInvoicesReqDto> updatedDto = new ArrayList<Gstr1GetInvoicesReqDto>();
		Map<Long, Pair<String, Boolean>> canOrigSavedToGstnMap = new HashMap<Long, Pair<String, Boolean>>();
		Map<String, StringBuilder> messagesMap = new HashMap<>();
		try {
			JsonObject requestObject = JsonParser.parseString(request)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {
			}.getType();
			List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);
			List<Gstr1GetInvoicesReqDto> onlyCanDocs = dtos.stream().filter(
					dto -> "CAN".equalsIgnoreCase(dto.getSupplyTypeInvMgmt()))
					.collect(Collectors.toList());
			updatedDto.addAll(onlyCanDocs);
			List<String> docKeys = onlyCanDocs.stream()
					.map(Gstr1GetInvoicesReqDto::getDocKey)
					.collect(Collectors.toList());

			if (docKeys != null && !docKeys.isEmpty()) {
				List<Object[]> originalSavedToGstnCount = docRepository
						.findByOrgDocSaveStatus(docKeys);
				if (originalSavedToGstnCount == null
						|| originalSavedToGstnCount.isEmpty()) {
					for (Gstr1GetInvoicesReqDto reqDto : onlyCanDocs) {
						canOrigSavedToGstnMap.put(reqDto.getDocId(),
								new Pair<>(reqDto.getDocKey(), false));
					}
				} else {
					for (Object[] object : originalSavedToGstnCount) {
						String docKey = object[0] != null ? object[0].toString()
								: null;
						int isSavedToGstn = object[1] != null
								? Integer.parseInt(object[1].toString()) : 0;
						Optional<Gstr1GetInvoicesReqDto> matchingDto = updatedDto
								.stream()
								.filter(dto -> docKey.equals(dto.getDocKey()))
								.findFirst();
						matchingDto.ifPresent(dto -> {
							canOrigSavedToGstnMap.put(dto.getDocId(),
									new Pair<>(docKey,
											isSavedToGstn == 0 ? false : true));
						});

					}
				}
			}

			List<Gstr1GetInvoicesReqDto> onlySaveDocs = dtos.stream().filter(
					dto -> !"CAN".equalsIgnoreCase(dto.getSupplyTypeInvMgmt()))
					.collect(Collectors.toList());

			updatedDto.addAll(onlySaveDocs);

			Map<String, List<Long>> groupedData = updatedDto.stream()
					.collect(Collectors.groupingBy(
							requestDTO -> requestDTO.getGstin() + "%"
									+ requestDTO.getReturnPeriod() + "%"
									+ requestDTO.getSection(),
							Collectors.mapping(Gstr1GetInvoicesReqDto::getDocId,
									Collectors.toList())));

			if (groupedData != null && !groupedData.isEmpty()) {
				List<String> gstnList = groupedData.keySet().stream()
						.map(key -> key.split("%")[0])
						.collect(Collectors.toList());
				Map<String, String> gstinAuthMap = authTokenService
						.getAuthTokenStatusForGstins(gstnList);
				boolean isSuccessMsgAddedInResp = false;
				Set<String> processedGstinSet = new HashSet<>();
				Set<String> inProgressGstnSet = new HashSet<>();
				Set<String> notSavedGstinSet = new HashSet<>();
				for (Map.Entry<String, List<Long>> entry : groupedData
						.entrySet()) {
					String[] keyParts = entry.getKey().split("%");
					String gstin = keyParts[0];
					String retPeriod = keyParts[1];
					String section = keyParts[2];

					if (section.equalsIgnoreCase("exports")) {
						section = APIConstants.EXP;
					}
					if (section.equalsIgnoreCase("Ecomsup")) {
						section = APIConstants.ECOMSUP;
					}
					if (section.equalsIgnoreCase("EXPORTS-A")) {
						section = APIConstants.EXPA;
					}
					if (section.equalsIgnoreCase("CDNUR-EXPORTS")
							|| section.equalsIgnoreCase("CDNUR-B2CL")) {
						section = APIConstants.CDNUR;
					}

					String authStatus = gstinAuthMap.get(gstin);

					if (processedGstinSet.contains(gstin)) {
						continue;
					}
					if (inProgressGstnSet.contains(gstin)) {
						continue;
					}
					List<Long> docIds = entry.getValue();
					List<Long> docIdsCan = new ArrayList<Long>(docIds);

					if (!"A".equalsIgnoreCase(authStatus)) {
						JsonObject json = new JsonObject();
						msg = "Auth Token is Inactive, Please Activate";
						json.addProperty("gstin", gstin);
						json.addProperty("msg", msg);
						respBody.add(json);
						processedGstinSet.add(gstin);
						continue;
					}
					for (Long docId : docIdsCan) {
						if (canOrigSavedToGstnMap.containsKey(docId)
								&& !canOrigSavedToGstnMap.get(docId)
										.getValue1()) {
							String docNum = canOrigSavedToGstnMap.get(docId)
									.getValue0().split("\\|")[3];
							String docNumStr = docNum;
							if (messagesMap.containsKey(gstin)) {
								messagesMap.get(gstin).append(", ")
										.append(docNumStr);
							} else {
								StringBuilder newMessage = new StringBuilder(
										docNumStr);
								messagesMap.put(gstin, newMessage);
							}
							notSavedGstinSet.add(gstin + "%" + docNum);
							docIds.remove(docId);
						}
					}
					if (docIds != null && !docIds.isEmpty()) {
						if (!gstnUserRequestUtil.isNextLvlSaveRequestEligible(
								gstin, retPeriod, APIConstants.SAVE,
								APIConstants.GSTR1.toUpperCase(), section)) {
							JsonObject json = new JsonObject();
							json.addProperty("gstin", gstin);
							json.addProperty("msg",
									"GSTR1 SAVE is Inprogress, New request Cannot be taken.");
							respBody.add(json);
							inProgressGstnSet.add(gstin);
							continue;
						}

						String sections = section;
						if (Strings.isNullOrEmpty(sections)) {
							JsonObject json = new JsonObject();
							json.addProperty("gstin", gstin);
							json.addProperty("msg",
									"No Section is selected to GSTR1 SAVE");
							respBody.add(json);
							continue;
						}
						resetHandler.createSectionWiseJobs(gstin, retPeriod,
								section, docIds, TenantContext.getTenantId());

						if (!isSuccessMsgAddedInResp) {
							JsonObject json = new JsonObject();
							// json.addProperty("gstin", gstin);
							json.addProperty("msg",
									"GSTR1 Save for selected(active) GSTINs is initiated. Please check the save status");
							respBody.add(json);
							isSuccessMsgAddedInResp = true;
						}
					}

				}
			}

			for (Map.Entry<String, StringBuilder> entry : messagesMap
					.entrySet()) {
				JsonObject json = new JsonObject();
				json.addProperty("gstin", entry.getKey());
				String msgStr = String.format(
						"Invoices %s is Not Saved to GSTN from DigiGST, Please Save & then initiate Delete, if required.",
						entry.getValue().toString());
				json.addProperty("msg", msgStr);
				respBody.add(json);
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			msg = "Unexpected Error While Initiating Gstr1 Save.";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/gstr1DeleteInvLvl", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1DeleteInvLvl(
			@RequestBody String request) {
		JsonObject resp = new JsonObject();
		String msg = "";
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respBody = new JsonArray();
		try {
			JsonObject requestObject = JsonParser.parseString(request)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {
			}.getType();
			List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);
			List<Gstr1GetInvoicesReqDto> updatedDto = new ArrayList<Gstr1GetInvoicesReqDto>();

			List<Gstr1GetInvoicesReqDto> onlyCanDocs = dtos.stream()
					.filter(dto -> "CAN".equalsIgnoreCase(dto.getSection()))
					.collect(Collectors.toList());

			for (Gstr1GetInvoicesReqDto dto : onlyCanDocs) {
				Optional<OutwardTransDocument> outwardDocument = docRepository
						.findLatestSavedDoc(dto.getDocKey());
				Gstr1GetInvoicesReqDto reqDto = new Gstr1GetInvoicesReqDto();
				if (outwardDocument.isPresent()) {
					reqDto.setSection(
							outwardDocument.get().getGstnBifurcation());
				} else {
					reqDto.setSection("NOT_SAVED");
				}
				reqDto.setGstin(dto.getGstin());
				reqDto.setReturnPeriod(dto.getReturnPeriod());
				reqDto.setDocId(dto.getDocId());
				reqDto.setDocKey(dto.getDocKey());
				updatedDto.add(reqDto);
			}

			Map<String, List<Long>> groupedData = updatedDto.stream()
					.collect(Collectors.groupingBy(
							requestDTO -> requestDTO.getGstin() + "%"
									+ requestDTO.getReturnPeriod() + "%"
									+ requestDTO.getSection(),
							Collectors.mapping(Gstr1GetInvoicesReqDto::getDocId,
									Collectors.toList())));

			if (groupedData != null && !groupedData.isEmpty()) {
				List<String> gstnList = groupedData.keySet().stream()
						.map(key -> key.split("%")[0])
						.collect(Collectors.toList());
				Map<String, String> gstinAuthMap = authTokenService
						.getAuthTokenStatusForGstins(gstnList);
				boolean isSuccessMsgAddedInResp = false;
				Set<String> processedGstinSet = new HashSet<>();
				Set<String> notSavedGstinSet = new HashSet<>();
				Set<String> inProgressGstnSet = new HashSet<>();
				for (Map.Entry<String, List<Long>> entry : groupedData
						.entrySet()) {
					String[] keyParts = entry.getKey().split("%");
					String gstin = keyParts[0];
					String retPeriod = keyParts[1];
					String section = keyParts[2];
					if (section.equalsIgnoreCase("exports")) {
						section = "exp";
					}
					if (section.equalsIgnoreCase("Ecomsup")) {
						section = APIConstants.ECOMSUP;
					}
					String authStatus = gstinAuthMap.get(gstin);

					if (processedGstinSet.contains(gstin)) {
						continue;
					}
					if (inProgressGstnSet.contains(gstin)) {
						continue;
					}
					if (notSavedGstinSet.contains(gstin)) {
						continue;
					}
					List<Long> docIds = entry.getValue();
					if (!"A".equalsIgnoreCase(authStatus)) {
						JsonObject json = new JsonObject();
						msg = "Auth Token is Inactive, Please Activate";
						json.addProperty("gstin", gstin);
						json.addProperty("msg", msg);
						respBody.add(json);
						processedGstinSet.add(gstin);
						continue;
					}
					if ("NOT_SAVED".equalsIgnoreCase(section)) {
						JsonObject json = new JsonObject();
						msg = "This Invoice is Not Saved to GSTN from DigiGST, Please Save & then initiate Delete, if required.";
						json.addProperty("gstin", gstin);
						json.addProperty("msg", msg);
						respBody.add(json);
						notSavedGstinSet.add(gstin);
						continue;
					}
					if (!gstnUserRequestUtil.isNextLvlSaveRequestEligible(gstin,
							retPeriod, APIConstants.SAVE,
							APIConstants.GSTR1.toUpperCase(), section)) {
						JsonObject json = new JsonObject();
						json.addProperty("gstin", gstin);
						json.addProperty("msg",
								"GSTR1 SAVE is Inprogress, New request Cannot be taken.");
						respBody.add(json);
						inProgressGstnSet.add(gstin);
						continue;
					}
					// if
					// (!gstnUserRequestUtil.isNextLvlDeleteRequestEligible(docKey))
					// {
					// JsonObject json = new JsonObject();
					// json.addProperty("gstin", gstin);
					// json.addProperty("msg",
					// "GSTR1 SAVE is Inprogress, New request Cannot be
					// taken.");
					// respBody.add(json);
					// inProgressGstnSet.add(gstin);
					// continue;
					// }

					String sections = section;
					if (Strings.isNullOrEmpty(sections)) {
						JsonObject json = new JsonObject();
						json.addProperty("gstin", gstin);
						json.addProperty("msg",
								"No Section is selected to GSTR1 Delete");
						respBody.add(json);
						continue;
					}

					resetHandler.createSectionWiseJobs(gstin, retPeriod,
							section, docIds, TenantContext.getTenantId());

					if (!isSuccessMsgAddedInResp) {
						JsonObject json = new JsonObject();
						// json.addProperty("gstin", gstin);
						json.addProperty("msg",
								"GSTR1 Save for selected(active) GSTINs is initiated. Please check the save status");
						respBody.add(json);
						isSuccessMsgAddedInResp = true;
					}
				}
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			msg = "Unexpected Error While Initiating Gstr1 Save.";
			LOGGER.error(msg, e.getMessage());
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/api/gstr1DeleteInvLvl", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> apigstr1DeleteInvLvl(
			@RequestBody String request) {
		JsonObject resp = new JsonObject();
		String msg = "";
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respBody = new JsonArray();
		try {
			JsonObject requestObject = JsonParser.parseString(request)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {
			}.getType();
			List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);

			List<String> docKeys = dtos.stream()
					.map(Gstr1GetInvoicesReqDto::getDocKey)
					.collect(Collectors.toList());

			List<OutwardTransDocument> outwardDocs = docRepository
					.findDocsByDocKeyIn(docKeys);

			for (OutwardTransDocument outwardTransDocument : outwardDocs) {
				for (Gstr1GetInvoicesReqDto updatedReqDto : dtos) {
					updatedReqDto.setSection(
							outwardTransDocument.getGstnBifurcation());
					updatedReqDto.setGstin(outwardTransDocument.getSgstin());
					updatedReqDto.setReturnPeriod(
							outwardTransDocument.getTaxperiod());
					updatedReqDto.setDocId(outwardTransDocument.getId());
					updatedReqDto.setDocKey(outwardTransDocument.getDocKey());
					updatedReqDto.setSavedtoGstn(outwardTransDocument.isSent());
					updatedReqDto.setSupplyType(Arrays
							.asList(outwardTransDocument.getSupplyType()));
				}
			}

			LOGGER.debug("Req Dto {} ", dtos);
			Map<String, List<Long>> groupedData = dtos.stream()
					.collect(Collectors.groupingBy(
							requestDTO -> requestDTO.getGstin() + "%"
									+ requestDTO.getReturnPeriod() + "%"
									+ requestDTO.getSection(),
							Collectors.mapping(Gstr1GetInvoicesReqDto::getDocId,
									Collectors.toList())));

			if (groupedData != null && !groupedData.isEmpty()) {
				List<String> gstnList = groupedData.keySet().stream()
						.map(key -> key.split("%")[0])
						.collect(Collectors.toList());
				Map<String, String> gstinAuthMap = authTokenService
						.getAuthTokenStatusForGstins(gstnList);
				boolean isSuccessMsgAddedInResp = false;
				Set<String> processedGstinSet = new HashSet<>();
				Set<String> inProgressGstnSet = new HashSet<>();
				for (Map.Entry<String, List<Long>> entry : groupedData
						.entrySet()) {
					String[] keyParts = entry.getKey().split("%");
					String gstin = keyParts[0];
					String retPeriod = keyParts[1];
					String section = keyParts[2];
					if (section.equalsIgnoreCase("exports")) {
						section = "exp";
					}
					if (section.equalsIgnoreCase("Ecomsup")) {
						section = APIConstants.ECOMSUP;
					}
					String authStatus = gstinAuthMap.get(gstin);

					if (processedGstinSet.contains(gstin)) {
						continue;
					}
					if (inProgressGstnSet.contains(gstin)) {
						continue;
					}
					List<Long> docIds = entry.getValue();
					if (!"A".equalsIgnoreCase(authStatus)) {
						JsonObject json = new JsonObject();
						msg = "Auth Token is Inactive, Please Activate";
						json.addProperty("gstin", gstin);
						json.addProperty("msg", msg);
						respBody.add(json);
						processedGstinSet.add(gstin);
						continue;
					}
					if (!gstnUserRequestUtil.isNextLvlSaveRequestEligible(gstin,
							retPeriod, APIConstants.SAVE,
							APIConstants.GSTR1.toUpperCase(), section)) {
						JsonObject json = new JsonObject();
						json.addProperty("gstin", gstin);
						json.addProperty("msg",
								"GSTR1 SAVE is Inprogress, New request Cannot be taken.");
						respBody.add(json);
						inProgressGstnSet.add(gstin);
						continue;
					}
					// if
					// (!gstnUserRequestUtil.isNextLvlDeleteRequestEligible(docKey))
					// {
					// JsonObject json = new JsonObject();
					// json.addProperty("gstin", gstin);
					// json.addProperty("msg",
					// "GSTR1 SAVE is Inprogress, New request Cannot be
					// taken.");
					// respBody.add(json);
					// inProgressGstnSet.add(gstin);
					// continue;
					// }

					String sections = section;
					if (Strings.isNullOrEmpty(sections)) {
						JsonObject json = new JsonObject();
						json.addProperty("gstin", gstin);
						json.addProperty("msg",
								"No Section is selected to GSTR1 Delete");
						respBody.add(json);
						continue;
					}

					resetHandler.createSectionWiseJobs(gstin, retPeriod,
							section, docIds, TenantContext.getTenantId());

					if (!isSuccessMsgAddedInResp) {
						JsonObject json = new JsonObject();
						// json.addProperty("gstin", gstin);
						json.addProperty("msg",
								"GSTR1 Save for selected(active) GSTINs is initiated. Please check the save status");
						respBody.add(json);
						isSuccessMsgAddedInResp = true;
					}
				}
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			msg = "Unexpected Error While Initiating Gstr1 Save.";
			LOGGER.error(msg, e.getMessage());
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/gstr1InvLvlSaveStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1SummarySaveStatus(
			@RequestBody String jsonString) throws JsonParseException {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
		Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {
		}.getType();
		List<Gstr1GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
				listType);
		try {
			List<Gstr1SummarySaveStatusRespDto> summaryList = gstr1SummarySaveStatusService
					.findByCriteria(dtos);
			JsonObject resps = new JsonObject();
			if (!summaryList.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(summaryList);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
				return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
			} else {
				return createGstinNodataSuccessResp();
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Detail Status. ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<String> createGstinNodataSuccessResp() {
		JsonObject resp = new JsonObject();
		resp.add("hdr", new Gson().toJsonTree(
				new APIRespDto("S", " No Data for the selected gstins. ")));
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}
}
