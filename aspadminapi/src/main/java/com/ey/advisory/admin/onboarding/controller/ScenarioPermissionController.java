/**
 * 
 */
package com.ey.advisory.admin.onboarding.controller;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.SftpScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.erp.ERPPermissionService;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.SftpScenarioPermissionRepository;
import com.ey.advisory.admin.services.onboarding.ErpSPIntegrationDaoImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.ErpGstinDetailsDto;
import com.ey.advisory.core.dto.ErpPermissionDeleteDto;
import com.ey.advisory.core.dto.ErpPermissionSaveDto;
import com.ey.advisory.core.dto.ErpSPDetailsDto;
import com.ey.advisory.core.dto.ErpScenarioDetailsDto;
import com.ey.advisory.core.dto.ErpScenarioInfoDto;
import com.ey.advisory.core.dto.ErpScenarioInfoSftpDto;
import com.ey.advisory.core.dto.ErpScenarioItmDetailsDto;
import com.ey.advisory.core.dto.ErpScenarioSftpItmDetailsDto;
import com.ey.advisory.core.dto.EventBasedErpScenarioInfoDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * @author Umesh
 *
 */
@RestController
public class ScenarioPermissionController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ScenarioPermissionController.class);

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Autowired
	@Qualifier("ErpInfoEntityRepository")
	private ErpInfoEntityRepository erpInfoEntityRepository;

	@Autowired
	@Qualifier("ErpSPIntegrationDaoImpl")
	private ErpSPIntegrationDaoImpl erpSPIntegrationDaoImpl;

	@Autowired
	@Qualifier("ErpScenarioPermissionRepository")
	private ErpScenarioPermissionRepository erpScenarioPermistionRepository;

	@Autowired
	@Qualifier("ErpScenarioMasterRepository")
	private ErpScenarioMasterRepository erpScenarioMasterRepository;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("ERPPermissionServiceImpl")
	private ERPPermissionService erpPermissionService;

	@Autowired
	@Qualifier("ErpEventsScenarioPermissionRepository")
	private ErpEventsScenarioPermissionRepository permRepo;

	@Autowired
	@Qualifier("SftpScenarioPermissionRepository")
	private SftpScenarioPermissionRepository sftpScenPermRepo;

	private static final String UPDATE_SUCCESSFULLY = "Updated Successfully";
	private static final String DELETED_SUCCESSFULLY = "Deleted Succefully";
	private static final String RESP = "resp";

	@PostMapping(value = "/getScenarioPermission", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSenario() {
		try {
			String groupCode = TenantContext.getTenantId();
			List<ErpScenarioInfoDto> scenariosList = new ArrayList<>();

			Long entityId = null;
			String entityName = null;
			List<EntityInfoEntity> entityInfo = entityInfoRepository
					.findEntityInfoDetails(groupCode);

			List<Object[]> erpInfo = erpInfoEntityRepository.getErpIdName();
			List<Object[]> sceMasterObjs = erpScenarioMasterRepository
					.getScenarioIdNameBackGroundJob();

			if (entityInfo != null) {
				for (EntityInfoEntity object : entityInfo) {
					entityId = object.getId();
					entityName = object.getEntityName();

					List<ErpGstinDetailsDto> gstinList = new ArrayList<>();
					List<ErpSPDetailsDto> erpList = new ArrayList<>();
					List<ErpScenarioDetailsDto> scenList = new ArrayList<>();
					List<ErpScenarioItmDetailsDto> itemList = new ArrayList<>();

					List<Object[]> gstinDetails = gstnDetailRepository
							.getGsinIdName(entityId);
					if (gstinDetails != null) {
						for (Object[] gstnInfo : gstinDetails) {
							ErpGstinDetailsDto gstndto = new ErpGstinDetailsDto();
							gstndto.setGstinId((Long) gstnInfo[0]);
							gstndto.setGstinName((String) gstnInfo[1]);
							gstinList.add(gstndto);
						}
					}

					if (erpInfo != null) {
						for (Object[] object1 : erpInfo) {
							ErpSPDetailsDto erpDetails = new ErpSPDetailsDto();
							erpDetails.setErpId((Long) object1[0]);
							erpDetails.setErpName((String) object1[1]);
							erpList.add(erpDetails);
						}
					}
					if (sceMasterObjs != null) {
						for (Object[] sceMasterObj : sceMasterObjs) {
							ErpScenarioDetailsDto scenDetails = new ErpScenarioDetailsDto();
							scenDetails.setScenarioId((Long) sceMasterObj[0]);
							scenDetails
									.setScenarioName((String) sceMasterObj[1]);
							scenDetails.setJobType((String) sceMasterObj[2]);
							scenDetails.setDataType((String) sceMasterObj[3]);
							scenList.add(scenDetails);
						}
					}
					List<ErpScenarioItmDetailsDto> scenarioItems = erpSPIntegrationDaoImpl
							.getErpSPItems(entityId);
					if (scenarioItems != null) {
						for (ErpScenarioItmDetailsDto scenarioinfo : scenarioItems) {

							List<String> gstnList = new ArrayList<>();
							ErpScenarioItmDetailsDto itemDetails = new ErpScenarioItmDetailsDto();
							String gstnIds = scenarioinfo.getGstinId();
							String[] tempArray = gstnIds.split(",");
							for (int i = 0; i < tempArray.length; i++) {
								String gstn = tempArray[i];
								gstnList.add(gstn);
							}
							itemDetails.setGstnItemList(gstnList);
							itemDetails.setErpId(scenarioinfo.getErpId());
							itemDetails.setScenarioId(
									scenarioinfo.getScenarioId());
							itemDetails.setDestName(scenarioinfo.getDestName());
							itemDetails.setJobFrequency(
									scenarioinfo.getJobFrequency());
							itemDetails.setStartRootTag(
									scenarioinfo.getStartRootTag());
							itemDetails.setEndRootTag(
									scenarioinfo.getEndRootTag());
							itemDetails.setEndPointURI(
									scenarioinfo.getEndPointURI());
							itemDetails.setCompanyCode(scenarioinfo.getCompanyCode());
							itemList.add(itemDetails);

						}
					}
					ErpScenarioInfoDto scenheaderDetails = new ErpScenarioInfoDto();
					scenheaderDetails.setEntityId(entityId);
					scenheaderDetails.setEntityName(entityName);
					scenheaderDetails.setGstinDetail(gstinList);
					scenheaderDetails.setErpDetails(erpList);
					scenheaderDetails.setScenario(scenList);
					scenheaderDetails.setItems(itemList);
					scenariosList.add(scenheaderDetails);
				}
			}
			Gson gson = new Gson();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(scenariosList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while searching entities";
			LOGGER.error(msg, e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/getSftpScenarioPermission", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSftpScenarioPermission() {
		try {
			TenantContext.getTenantId();
			List<ErpScenarioInfoSftpDto> scenariosList = new ArrayList<>();

			List<Object[]> erpInfo = erpInfoEntityRepository
					.getErpIdNameForSftpSourceType();
			List<Object[]> sceMasterObjs = erpScenarioMasterRepository
					.getScenarioIdNameBackGroundJob();

			List<ErpSPDetailsDto> erpList = new ArrayList<>();
			List<ErpScenarioDetailsDto> scenList = new ArrayList<>();
			List<ErpScenarioSftpItmDetailsDto> itemList = new ArrayList<>();

			if (erpInfo != null) {
				for (Object[] object1 : erpInfo) {
					ErpSPDetailsDto erpDetails = new ErpSPDetailsDto();
					erpDetails.setErpId((Long) object1[0]);
					erpDetails.setErpName((String) object1[1]);
					erpList.add(erpDetails);
				}
			}
			if (sceMasterObjs != null) {
				for (Object[] sceMasterObj : sceMasterObjs) {
					ErpScenarioDetailsDto scenDetails = new ErpScenarioDetailsDto();
					scenDetails.setScenarioId((Long) sceMasterObj[0]);
					scenDetails.setScenarioName((String) sceMasterObj[1]);
					scenDetails.setJobType((String) sceMasterObj[2]);
					scenDetails.setDataType((String) sceMasterObj[3]);
					scenList.add(scenDetails);
				}
			}
			List<SftpScenarioPermissionEntity> scenarioItems = sftpScenPermRepo
					.getSftpScenarioPermissionEntities();
			if (scenarioItems != null) {
				for (SftpScenarioPermissionEntity scenarioinfo : scenarioItems) {

					ErpScenarioSftpItmDetailsDto itemDetails = new ErpScenarioSftpItmDetailsDto();
					itemDetails.setErpId(scenarioinfo.getErpId());
					itemDetails.setScenarioId(scenarioinfo.getScenarioId());
					itemDetails.setJobFrequency(scenarioinfo.getJobFrequency());
					itemDetails.setEndPointURI(scenarioinfo.getEndPointURI());
					itemList.add(itemDetails);
				}
			}
			ErpScenarioInfoSftpDto scenheaderDetails = new ErpScenarioInfoSftpDto();
			scenheaderDetails.setErpDetails(erpList);
			scenheaderDetails.setScenario(scenList);
			scenheaderDetails.setItems(itemList);
			scenariosList.add(scenheaderDetails);
			Gson gson = new Gson();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(scenariosList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while searching entities";
			LOGGER.error(msg, e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/getEventBasedScenarioPermission", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEventBasedScenarioPermission() {
		try {

			String groupCode = TenantContext.getTenantId();
			List<EventBasedErpScenarioInfoDto> scenariosList = new ArrayList<>();
			List<ErpScenarioItmDetailsDto> scenEventsItems = erpSPIntegrationDaoImpl
					.getErpEventsSP();
			List<ErpSPDetailsDto> erpList = new ArrayList<>();
			List<ErpScenarioDetailsDto> scenList = new ArrayList<>();
			List<ErpScenarioItmDetailsDto> itemList = new ArrayList<>();

			List<Object[]> erpInfo = erpInfoEntityRepository.getErpIdName();
			if (erpInfo != null) {
				for (Object[] object1 : erpInfo) {
					ErpSPDetailsDto erpDetails = new ErpSPDetailsDto();
					erpDetails.setErpId((Long) object1[0]);
					erpDetails.setErpName((String) object1[1]);
					erpList.add(erpDetails);
				}
			}
			List<Object[]> sceMasterObjs = erpScenarioMasterRepository
					.getScenarioIdNameEventBasedJob();
			if (sceMasterObjs != null) {
				for (Object[] sceMasterObj : sceMasterObjs) {
					ErpScenarioDetailsDto scenDetails = new ErpScenarioDetailsDto();
					scenDetails.setScenarioId((Long) sceMasterObj[0]);
					scenDetails.setScenarioName((String) sceMasterObj[1]);
					scenDetails.setJobType((String) sceMasterObj[2]);
					scenDetails.setDataType((String) sceMasterObj[3]);
					scenList.add(scenDetails);
				}
			}

			if (scenEventsItems != null) {
				for (ErpScenarioItmDetailsDto scenEventsItem : scenEventsItems) {
					ErpScenarioItmDetailsDto itemDetails = new ErpScenarioItmDetailsDto();
					itemDetails.setErpId(scenEventsItem.getErpId());
					itemDetails.setScenarioId(scenEventsItem.getScenarioId());
					itemDetails.setDestName(scenEventsItem.getDestName());
					itemDetails
							.setStartRootTag(scenEventsItem.getStartRootTag());
					itemDetails.setEndRootTag(scenEventsItem.getEndRootTag());
					itemDetails.setEndPointURI(scenEventsItem.getEndPointURI());
					itemList.add(itemDetails);
				}
			}
			EventBasedErpScenarioInfoDto scenheaderDetails = new EventBasedErpScenarioInfoDto();
			scenheaderDetails.setErpDetails(erpList);
			scenheaderDetails.setScenario(scenList);
			scenheaderDetails.setItems(itemList);
			scenariosList.add(scenheaderDetails);

			Gson gson = new Gson();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(scenariosList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while searching entities";
			LOGGER.error(msg, e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/saveErpScenario", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getErpScenariosave(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		User user = SecurityContext.getUser();

		try {

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Type listType = new TypeToken<List<ErpPermissionSaveDto>>() {
			}.getType();

			JsonArray json = requestObject.get("req").getAsJsonArray();
			Gson gson = GsonUtil.newSAPGsonInstance();

			String groupCode = TenantContext.getTenantId();
			Long groupId = groupInfoDetailsRepository.findByGroupId(groupCode);

			List<ErpPermissionSaveDto> erpPermSaveDtos = gson.fromJson(json,
					listType);

			Long scenarioId = null;
			Long erpId = null;
			Long gstinId = null;
			Long entityId = null;
			String startRootTag = null;
			String endRootTag = null;
			String jobFrequency = null;
			String endPointURI = null;
			String companyCode = null;
			List<Long> ids = new ArrayList<>();
			List<ErpScenarioPermissionEntity> saveErpRegEntity = new ArrayList<>();
			List<ErpPermissionSaveDto> saveErpdtoList = new ArrayList<>();
			List<ErpEventsScenarioPermissionEntity> erpEventsScenPermEntities = new ArrayList<>();
			List<SftpScenarioPermissionEntity> sftpScenPermEntities = new ArrayList<>();
			for (ErpPermissionSaveDto dto : erpPermSaveDtos) {
				String jobType = dto.getJobType();
				if ("B".equalsIgnoreCase(jobType)) {
					List<Long> gstnsId = dto.getGstnsId();
					for (Long id : gstnsId) {
						ErpPermissionSaveDto ent = new ErpPermissionSaveDto();
						ent.setGstnId(id);
						ent.setEntityId(dto.getEntityId());
						ent.setErpId(dto.getErpId());
						ent.setScenarioId(dto.getScenarioId());
						ent.setDestName(dto.getDestName());
						ent.setJobFrequency(dto.getJobFrequency());
						ent.setStartRootTag(dto.getStartRootTag());
						ent.setEndRootTag(dto.getEndRootTag());
						ent.setEndPointURI(dto.getEndPointURI());
						ent.setCompanyCode(dto.getCompanyCode());
						saveErpdtoList.add(ent);
					}
				} else if ("E".equalsIgnoreCase(jobType)) {

					Long id = permRepo.getScenarioPermission(dto.getErpId(),
							dto.getScenarioId());

					ids.add(id);
					ErpEventsScenarioPermissionEntity permEntity = new ErpEventsScenarioPermissionEntity();

					permEntity.setErpId(dto.getErpId());
					permEntity.setScenarioId(dto.getScenarioId());
					permEntity.setDestName(dto.getDestName());
					permEntity.setStartRootTag(dto.getStartRootTag());

					permEntity.setEndRootTag(dto.getEndRootTag());
					permEntity.setCreatedBy(user.getUserPrincipalName());
					permEntity.setCreatedOn(convertNow);
					permEntity.setModifiedBy(user.getUserPrincipalName());
					permEntity.setModifiedOn(convertNow);
					permEntity.setJobstartDate(convertNow);
					permEntity.setJobCompletionDate(convertNow);
					permEntity.setEndPointURI(dto.getEndPointURI());
					String sourceType = erpInfoEntityRepository
							.getSourceTypeByErpId(dto.getErpId());
					permEntity.setSourceType(sourceType);
					erpEventsScenPermEntities.add(permEntity);

				} else {
					Long id = permRepo.getScenarioPermission(dto.getErpId(),
							dto.getScenarioId());

					ids.add(id);
					SftpScenarioPermissionEntity permEntity = new SftpScenarioPermissionEntity();

					permEntity.setErpId(dto.getErpId());
					permEntity.setScenarioId(dto.getScenarioId());
					permEntity.setEndPointURI(dto.getEndPointURI());
					String sourceType = erpInfoEntityRepository
							.getSourceTypeByErpId(dto.getErpId());
					permEntity.setSourceType(sourceType);
					permEntity.setCreatedBy(user.getUserPrincipalName());
					permEntity.setJobFrequency(dto.getJobFrequency());
					permEntity.setCreatedOn(convertNow);
					permEntity.setModifiedBy(user.getUserPrincipalName());
					permEntity.setModifiedOn(convertNow);
					permEntity.setJobstartDate(convertNow);
					permEntity.setJobCompletionDate(convertNow);

					sftpScenPermEntities.add(permEntity);
				}
			}
			for (ErpPermissionSaveDto savedto : saveErpdtoList) {
				gstinId = savedto.getGstnId();
				erpId = savedto.getErpId();
				entityId = savedto.getEntityId();
				scenarioId = savedto.getScenarioId();
				startRootTag = savedto.getStartRootTag();
				endRootTag = savedto.getEndRootTag();
				jobFrequency = savedto.getJobFrequency();
				endPointURI = savedto.getEndPointURI();
				companyCode = savedto.getCompanyCode();

				List<ErpScenarioPermissionEntity> scenarioInfo = erpScenarioPermistionRepository
						.getDuplicateIds(gstinId, erpId, entityId, scenarioId);

				if (!scenarioInfo.isEmpty()) {
					for (ErpScenarioPermissionEntity idInfo : scenarioInfo) {
						idInfo.setDelete(true);
						idInfo.setModifiedOn(LocalDateTime.now());
						idInfo.setModifiedBy(user.getUserPrincipalName());
						
						erpScenarioPermistionRepository.save(idInfo);
						}
				}
					ErpScenarioPermissionEntity en = new ErpScenarioPermissionEntity();
					en.setGstinId(gstinId);
					en.setEntityId(entityId);
					en.setErpId(erpId);
					en.setScenarioId(scenarioId);
					en.setGroupId(groupId);
					en.setGroupcode(groupCode);
					en.setDestName(savedto.getDestName());
					en.setStartRootTag(startRootTag);
					en.setEndRootTag(endRootTag);
					en.setEndPointURI(endPointURI);
					en.setCompanyCode(companyCode);
					en.setJobFrequency(jobFrequency);
					en.setCreatedBy(user.getUserPrincipalName());
					en.setCreatedOn(convertNow);
					en.setModifiedBy(user.getUserPrincipalName());
					en.setModifiedOn(convertNow);
					en.setJobstartDate(convertNow);
					en.setJobCompletionDate(convertNow);
					en.setEndPointURI(savedto.getEndPointURI());
					String sourceType = erpInfoEntityRepository
							.getSourceTypeByErpId(erpId);
					en.setSourceType(sourceType);
					saveErpRegEntity.add(en);
			}
			if (saveErpRegEntity != null && !saveErpRegEntity.isEmpty()) {
				erpScenarioPermistionRepository.saveAll(saveErpRegEntity);
			}

			if (ids != null && !ids.isEmpty()) {
				permRepo.updateScenarioPerm(ids);
			}
			if (erpEventsScenPermEntities != null
					&& !erpEventsScenPermEntities.isEmpty()) {
				permRepo.saveAll(erpEventsScenPermEntities);
			}

			if (sftpScenPermEntities != null
					&& !sftpScenPermEntities.isEmpty()) {
				sftpScenPermRepo.saveAll(sftpScenPermEntities);
			}
			resp.add(RESP, gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), UPDATE_SUCCESSFULLY)));
		} catch (Exception e) {
			String msg = "Unexpected error while saving Erp details";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/deleteScenario.do", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> deleteScenarioPermission(
			@RequestBody String jsonReq) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonArray requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonArray();
			Type listType = new TypeToken<List<ErpPermissionDeleteDto>>() {
			}.getType();
			List<ErpPermissionDeleteDto> erpScenarioDeletedto = gson
					.fromJson(requestObject, listType);

			erpPermissionService
					.deleteErpPermissionDetails(erpScenarioDeletedto);
			resp.add(RESP, gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), DELETED_SUCCESSFULLY)));
		} catch (Exception e) {
			LOGGER.error("Exception Occure:", e);
			resp.add("hrd",
					gson.toJsonTree(new APIRespDto(APIRespDto.getErrorStatus(),
							e.getMessage())));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
	}

}