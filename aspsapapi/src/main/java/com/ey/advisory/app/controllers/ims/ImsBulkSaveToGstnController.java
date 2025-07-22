/**
 * 
 */
package com.ey.advisory.app.controllers.ims;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsProcessedInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsSaveJobQueueRepository;
import com.ey.advisory.app.service.ims.ImsSaveJobQueueEntity;
import com.ey.advisory.app.service.ims.ImsSaveToGstnReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran s
 *
 */
@Slf4j
@RestController
public class ImsBulkSaveToGstnController {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchRepo;

	@Autowired
	private ImsProcessedInvoiceRepository psdRepo;

	@Autowired
	private ImsSaveJobQueueRepository queueRepo;

	private static final List<String> statusList = ImmutableList.of("In Queue",
			"InProgress", "RefId Generated");

	@PostMapping(value = "/ui/imsBulkSaveToGstn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> createGstr1BulkSaveToGstnJob(
			@RequestBody String jsonParam) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"ImsBulkSaveToGstnController Request received from UI as {} ",
						jsonParam);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject resp = new JsonObject();

			JsonObject requestObject = JsonParser.parseString(jsonParam)
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			ImsSaveToGstnReqDto dto = gson.fromJson(reqObject,
					ImsSaveToGstnReqDto.class);

			String groupCode = TenantContext.getTenantId();
			TenantContext.setTenantId(groupCode);
			LOGGER.debug("groupCode {} is set", groupCode);

			JsonArray respObj = saveEntityLevelData(dto.getGstins(), dto);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respObj));

			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving Job";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public JsonArray saveEntityLevelData(List<String> gstinsList,
			ImsSaveToGstnReqDto dto) {
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		JsonArray respBody = new JsonArray();
		String section = "";
		String sectionReset = "";
		Long count = 0L;
		Long countReset = 0L;
		List<String> tableTypeList = dto.getTableType();

		Map<String, String> authMap = authTokenService
				.getAuthTokenStatusForGstins(gstinsList);

		for (String gstin : gstinsList) {
			String authStatus = authMap.get(gstin);
			JsonObject json = new JsonObject();

			if ("A".equals(authStatus)) {
				boolean isTableTypePresentSave = false;
				boolean isAlreadyNotInQueueSave = false;
				boolean isTableTypePresentReset = false;
				boolean isAlreadyNotInQueueReset = false;

				List<Object[]> objectList = psdRepo
						.findActiveAPRCountRecipientGstin(gstin, tableTypeList);
				Map<String, Long> map = new HashMap<>();
				for (Object[] object : objectList) {
					section = object[0] != null
							&& !object[0].toString().trim().isEmpty()
									? object[0].toString().trim() : null;
					count = (Long) object[1];
					map.put(section, count);
				}

				List<Object[]> objectResetList = psdRepo
						.findActiveNCountRecipientGstin(gstin, tableTypeList);
				Map<String, Long> mapReset = new HashMap<>();
				for (Object[] object : objectResetList) {
					sectionReset = object[0] != null
							&& !object[0].toString().trim().isEmpty()
									? String.valueOf(
											object[0].toString().trim())
									: null;

					countReset = (Long) object[1];
					mapReset.put(sectionReset, countReset);

				}

				List<ImsSaveJobQueueEntity> queueEntitiesSave = new ArrayList<>();
				List<ImsSaveJobQueueEntity> queueEntitiesReset = new ArrayList<>();
				List<String> sectionList = queueRepo.findInprogressData(gstin,
						statusList, "SAVE");

				for (String tableType : tableTypeList) {
					if (map.containsKey(tableType) && map.get(tableType) > 0) {
						isTableTypePresentSave = true;
						if (sectionList.isEmpty() || (!sectionList.isEmpty()
								&& !sectionList.contains(tableType))) {
							isAlreadyNotInQueueSave = true;
							ImsSaveJobQueueEntity entity = new ImsSaveJobQueueEntity();
							entity.setSection(tableType);
							entity.setIsActive(true);
							entity.setGstin(gstin);
							entity.setCreatedBy(userName);
							entity.setCreatedOn(LocalDateTime.now());
							entity.setStatus("In Queue");
							entity.setAction("SAVE");
							queueEntitiesSave.add(entity);
						}
					}
				}
				// Reset
				List<String> sectionListReset = queueRepo
						.findInprogressData(gstin, statusList, "RESET");
				for (String tableType : tableTypeList) {
					if (mapReset.containsKey(tableType)
							&& mapReset.get(tableType) > 0) {
						isTableTypePresentReset = true;
						if (sectionListReset.isEmpty() || (!sectionListReset
								.isEmpty()
								&& !sectionListReset.contains(tableType))) {
							isAlreadyNotInQueueReset = true;
							ImsSaveJobQueueEntity entity = new ImsSaveJobQueueEntity();
							entity.setSection(tableType);
							entity.setIsActive(true);
							entity.setGstin(gstin);
							entity.setCreatedBy(userName);
							entity.setCreatedOn(LocalDateTime.now());
							entity.setStatus("In Queue");
							entity.setAction("RESET");
							queueEntitiesReset.add(entity);
						}
					}
				}

				String msg = null;
				if (isTableTypePresentSave) {
					if (isAlreadyNotInQueueSave) {
						msg = "Save to GSTN in Queue";
						queueRepo.saveAll(queueEntitiesSave);
					} else {
						json.addProperty("gstin", gstin);
						msg = "Save to GSTN Already in Queue";
					}
				} else {
					msg = "No Data available to Save";
				}
				json.addProperty("msg", msg);
				respBody.add(json);
				json = new JsonObject();
				if (isTableTypePresentReset) {
					if (isAlreadyNotInQueueReset) {
						msg = "Reset GSTN in Queue";
						queueRepo.saveAll(queueEntitiesReset);
					} else {
						json.addProperty("gstin", gstin);
						msg = "Reset GSTN Already in Queue";
					}
				} else {
					msg = "No Data available to Reset";
				}

				json.addProperty("msg", msg);
				respBody.add(json);

			} else {
				String msg = "Auth Token is Inactive, Please Activate";
				json.addProperty("msg", msg);
				respBody.add(json);

			}

		}

		return respBody;
	}
}
