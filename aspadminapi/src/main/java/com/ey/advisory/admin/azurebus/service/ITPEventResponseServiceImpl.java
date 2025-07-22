package com.ey.advisory.admin.azurebus.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GroupLevelPermissionsMasterEntity;
import com.ey.advisory.admin.data.entities.client.GroupLevelUserPermissionsEntity;
import com.ey.advisory.admin.data.entities.client.PermissionsMasterEntity;
import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.entities.client.UserPermissionsEntity;
import com.ey.advisory.admin.data.repositories.client.GroupLevelPermissionsMasterRepository;
import com.ey.advisory.admin.data.repositories.client.GroupLevelUserPermissionsRepository;
import com.ey.advisory.admin.data.repositories.client.PermissionsMasterRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.admin.data.repositories.client.UserPermissionsRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.repositories.master.GroupRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("ITPEventResponseServiceImpl")
public class ITPEventResponseServiceImpl implements ITPEventResponseService {

	private static final List<String> ERROR_CODES = ImmutableList.of("DIGI102",
			"DIGI103", "GEN501");

	ImmutableMap<String, String> immutableMap = ImmutableMap.of("DIGI102",
			"No Record found for the provided Inputs", "DIGI103",
			"Invalid API Request Parameters", "GEN501",
			"Missing Mandatory Params");

	@Autowired
	@Qualifier("GroupRepository")
	private GroupRepository groupRepository;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityRepository;

	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository userRepository;

	@Autowired
	@Qualifier("UserPermissionsRepository")
	private UserPermissionsRepository userPermissionsRepository;

	@Autowired
	@Qualifier("GroupLevelUserPermissionsRepository")
	private GroupLevelUserPermissionsRepository grpUserPermissionsRepository;

	@Autowired
	@Qualifier("PermissionsMasterRepository")
	private PermissionsMasterRepository permissionsMasterRepository;

	@Autowired
	@Qualifier("GroupLevelPermissionsMasterRepository")
	private GroupLevelPermissionsMasterRepository grpPermissionsMasterRepository;
	

	@Override
	public String getListOfGroups() {
		List<Group> activeGroups = groupRepository.findByIsActive(true);
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<ITPEventCommonResponseDto> responseDtoList = new ArrayList<>();
		ITPEventResponseDto eventResponseDto = new ITPEventResponseDto();
		eventResponseDto.setCategory(ITPEventConstants.GROUP);
		eventResponseDto.setAppType(ITPEventConstants.DIGISAP);
		try {
			if (activeGroups.isEmpty()) {
				return noRecordsResp(gson, eventResponseDto);
			}
			for (Group group : activeGroups) {
				ITPEventCommonResponseDto responseDto = new ITPEventCommonResponseDto();
				responseDto.setGroupId(group.getGroupId().intValue());
				responseDto.setGroupCode(group.getGroupCode());
				responseDto.setGroupName(group.getGroupName());
				responseDtoList.add(responseDto);
			}
			eventResponseDto.setData(responseDtoList);
			String eventJson = gson.toJson(eventResponseDto);
			LOGGER.debug("Response {} ", eventJson);
			return eventJson;
		} catch (Exception e) {
			LOGGER.error("Exception while retriving the Group Code Details ",
					e);
			throw new AppException(e);
		}
	}

	@Override
	public String getEntitiesDetails(String groupCode) {
		List<EntityInfoEntity> activeEntities = entityRepository
				.findActiveEntities();
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<ITPEventCommonResponseDto> responseDtoList = new ArrayList<>();
		ITPEventResponseDto eventResponseDto = new ITPEventResponseDto();
		eventResponseDto.setCategory(ITPEventConstants.ENTITY);
		eventResponseDto.setAppType(ITPEventConstants.DIGISAP);
		try {
			if (activeEntities.isEmpty()) {
				return noRecordsResp(gson, eventResponseDto);
			}
			for (EntityInfoEntity entityInfo : activeEntities) {
				ITPEventCommonResponseDto responseDto = new ITPEventCommonResponseDto();
				responseDto.setGroupCode(groupCode);
				Group group = groupRepository
						.findByGroupCodeAndIsActiveTrue(groupCode);
				responseDto.setGroupId(group.getGroupId().intValue());
				responseDto.setGroupName(group.getGroupName());
				responseDto.setEntityId(entityInfo.getId());
				responseDto.setEntityName(entityInfo.getEntityName());
				responseDto.setEntityPan(entityInfo.getPan());
				responseDtoList.add(responseDto);
			}
			eventResponseDto.setData(responseDtoList);
			String eventJson = gson.toJson(eventResponseDto);
			LOGGER.debug("Response {} ", eventJson);
			return eventJson;
		} catch (Exception e) {
			LOGGER.error("Exception while retriving the Entity Details ", e);
			throw new AppException(e);
		}
	}

	@Override
	public String getUserDetails(String groupCode) {
		List<UserCreationEntity> activeUsers = userRepository.findDetails();
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<ITPEventCommonResponseDto> responseDtoList = new ArrayList<>();
		ITPEventResponseDto eventResponseDto = new ITPEventResponseDto();
		eventResponseDto.setCategory(ITPEventConstants.USER);
		eventResponseDto.setAppType(ITPEventConstants.DIGISAP);
		try {
			if (activeUsers.isEmpty()) {
				return noRecordsResp(gson, eventResponseDto);
			}
			for (UserCreationEntity user : activeUsers) {
				ITPEventCommonResponseDto responseDto = new ITPEventCommonResponseDto();
				Group group = groupRepository
						.findByGroupCodeAndIsActiveTrue(groupCode);
				responseDto.setGroupCode(groupCode);
				responseDto.setGroupId(group.getGroupId().intValue());
				responseDto.setGroupName(group.getGroupName());
				responseDto.setUserId(String.valueOf(user.getId()));
				responseDto.setUserName(user.getUserName());
				responseDto.setFirstName(user.getFirstName());
				responseDto.setLastName(user.getLastName());
				responseDto.setEmailId(user.getEmail());
				responseDtoList.add(responseDto);
			}
			eventResponseDto.setData(responseDtoList);
			String eventJson = gson.toJson(eventResponseDto);
			LOGGER.debug("Response {} ", eventJson);
			return eventJson;
		} catch (Exception e) {
			LOGGER.error("Exception while retriving the User Details ", e);
			throw new AppException(e);
		}
	}

	private String noRecordsResp(Gson gson,
			ITPEventResponseDto eventResponseDto) {
		ITPEventDataErrorDto errorDto = new ITPEventDataErrorDto();
		errorDto.setErrorCode(ERROR_CODES.get(0));
		errorDto.setMsg(immutableMap.get(ERROR_CODES.get(0)));
		eventResponseDto.setError(Arrays.asList(errorDto));
		String errorJson = gson.toJson(eventResponseDto);
		LOGGER.error("Response {} ", errorJson);
		return errorJson;
	}

	@Override
	public void updateUserDetails(ITPEventRequestDto requestDto) {

		int rowsAffected = userRepository.updateITPUserName(
				requestDto.getData().getItpUserId(),
				requestDto.getData().getSapUserId());

		if (rowsAffected > 0) {
			LOGGER.error("Rows Affected are for User Updation {} ",
					rowsAffected);
		}
	}

	@Override
	public String getBulkUserPermissionsDetails(
			ITPEventRequestParamsDto requestParams) {
		// List<Group> activeGroups = groupRepository.findByIsActive(true);
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<ITPEventCommonResponseDto> responseDtoList = new ArrayList<>();
		ITPEventResponseDto eventResponseDto = new ITPEventResponseDto();
		eventResponseDto.setCategory(ITPEventConstants.BULK_ONBOARDING_USER);
		eventResponseDto.setAppType(ITPEventConstants.DIGISAP);
		String tenantCode = requestParams.getTenantCode();
		eventResponseDto.setRequestId(requestParams.getRequestId());

		List<ITPEventCommonResponseDto> users = requestParams.getUsers();

		List<PermissionsMasterEntity> masterEntities = permissionsMasterRepository
				.getAllPermissions();

		List<GroupLevelPermissionsMasterEntity> grpMasterEntities = grpPermissionsMasterRepository
				.getAllPermissions();

		List<EntityInfoEntity> entityDetails = entityRepository
				.findEntitiesByGroupCodes(tenantCode);
		try {
			for (ITPEventCommonResponseDto user : users) {
				ITPEventCommonResponseDto responseDto = new ITPEventCommonResponseDto();
				responseDto.setEmailId(user.getEmailId());
				UserCreationEntity userDetails = userRepository
						.findUserEntityByEmail(user.getEmailId());
				
				if (userDetails != null) {
					responseDto.setPId(userDetails.getUserName());
					responseDto.setEntityAccess(setEntityAccess(userDetails,
							entityDetails, masterEntities));
					responseDto.setGroupAccess(setGroupAccess(userDetails,
							entityDetails, grpMasterEntities));

					responseDtoList.add(responseDto);
				}else{
					responseDto.setPId(null);
					responseDto.setEntityAccess(new ArrayList<>());
					responseDto.setGroupAccess(new ITPEventGroupAccessResponseDto());

					responseDtoList.add(responseDto);
				}

			}
			eventResponseDto.setData(responseDtoList);
			String eventJson = gson.toJson(eventResponseDto);
			
	        JsonObject root = JsonParser.parseString(eventJson).getAsJsonObject();
	        JsonArray dataArray = root.getAsJsonArray("data");
 
	        for (JsonElement element : dataArray) {
	            element.getAsJsonObject().remove("groupId");
	        }

	        String updatedJson = gson.toJson(root);
			
			LOGGER.debug("Response {} ", updatedJson);
			return updatedJson;

		} catch (Exception e) {
			LOGGER.error(
					"Exception while retriving the Bulk User Permission Details ",
					e);
			throw new AppException(e);
		}
	}

	private List<ITPEventEntityAccessResponseDto> setEntityAccess(
			UserCreationEntity userDetails,
			List<EntityInfoEntity> entityDetails,
			List<PermissionsMasterEntity> masterEntities) {
		

		List<ITPEventEntityAccessResponseDto> responseDtoList = new ArrayList<>();

		Long userId = userDetails.getId();
		List<Long> entityIds = entityDetails.stream()
				.map(EntityInfoEntity::getId).collect(Collectors.toList());

		List<UserPermissionsEntity> userPermissions = userPermissionsRepository
				.findByEntityIdInAndUserIdAndIsApplicableTrueAndIsDeleteFalse(
						entityIds, userId);

		Map<Long, List<String>> entityPermCodeMap = userPermissions.stream()
				.collect(Collectors
						.groupingBy(UserPermissionsEntity::getEntityId,
								Collectors.mapping(
										userPermission -> userPermission
												.getPermCode().toLowerCase(),
										Collectors.toList())));

		List<String> overallPermCodes = masterEntities.stream()
				.map(masterEntity -> masterEntity.getPermCode().toLowerCase())
				.collect(Collectors.toList());
		
		for (Map.Entry<Long, List<String>> entry : entityPermCodeMap.entrySet()) {
			Long entityId = entry.getKey();
			
			ITPEventEntityAccessResponseDto dto = new ITPEventEntityAccessResponseDto();
			dto.setUserName(userDetails.getEmail());
			
			EntityInfoEntity entity = entityRepository.findById(entityId)
					.orElse(null);
			if (entity != null) {
				dto.setEntityName(entity.getEntityName());
				dto.setPan(entity.getPan());
			}
			List<String> permittedPermCodes = entry.getValue();
			
			for (String permCode : overallPermCodes) {
				
				boolean isPermApplicable = permittedPermCodes.contains(permCode);


				switch (permCode) {
				case "p1":
					dto.setP1(isPermApplicable);
					break;
				case "p2":
					dto.setP2(isPermApplicable);
					break;
				case "p29":
					dto.setP29(isPermApplicable);
					break;
				case "p101":
					dto.setP101(isPermApplicable);
					break;
				case "p8":
					dto.setP8(isPermApplicable);
					break;
				case "p24":
					dto.setP24(isPermApplicable);
					break;
				case "p3":
					dto.setP3(isPermApplicable);
					break;
				case "p12":
					dto.setP12(isPermApplicable);
					break;
				case "p13":
					dto.setP13(isPermApplicable);
					break;
				case "p10":
					dto.setP10(isPermApplicable);
					break;
				case "p11":
					dto.setP11(isPermApplicable);
					break;
				case "p4":
					dto.setP4(isPermApplicable);
					break;
				case "p102":
					dto.setP102(isPermApplicable);
					break;
				case "p103":
					dto.setP103(isPermApplicable);
					break;
				case "p5":
					dto.setP5(isPermApplicable);
					break;
				case "p9":
					dto.setP9(isPermApplicable);
					break;
				case "p22":
					dto.setP22(isPermApplicable);
					break;
				case "p23":
					dto.setP23(isPermApplicable);
					break;
				case "p6":
					dto.setP6(isPermApplicable);
					break;
				case "p25":
					dto.setP25(isPermApplicable);
					break;
				case "p27":
					dto.setP27(isPermApplicable);
					break;
				case "p28":
					dto.setP28(isPermApplicable);
					break;
				case "p7":
					dto.setP7(isPermApplicable);
					break;
				case "p30":
					dto.setP30(isPermApplicable);
					break;
				case "p31":
					dto.setP31(isPermApplicable);
					break;
				case "p32":
					dto.setP32(isPermApplicable);
					break;
				case "p33":
					dto.setP33(isPermApplicable);
					break;
				case "p34":
					dto.setP34(isPermApplicable);
					break;
				case "p35":
					dto.setP35(isPermApplicable);
					break;
				case "p36":
					dto.setP36(isPermApplicable);
					break;
				case "p38":
					dto.setP38(isPermApplicable);
					break;
				case "p39":
					dto.setP39(isPermApplicable);
					break;
				case "p40":
					dto.setP40(isPermApplicable);
					break;
				case "p41":
					dto.setP41(isPermApplicable);
					break;
				case "p42":
					dto.setP42(isPermApplicable);
					break;

				default:
					break;
				}
			}
			responseDtoList.add(dto);

		}

		return responseDtoList;
	}

	private ITPEventGroupAccessResponseDto setGroupAccess(
			UserCreationEntity userDetails,
			List<EntityInfoEntity> entityDetails,
			List<GroupLevelPermissionsMasterEntity> grpMasterEntities) {
		

		ITPEventGroupAccessResponseDto dto = new ITPEventGroupAccessResponseDto();

		Long userId = userDetails.getId();

		List<GroupLevelUserPermissionsEntity> userPermissions = grpUserPermissionsRepository
				.findByUserIdAndIsApplicableTrueAndIsDeleteFalse(userId);

		List<String> permCodes = userPermissions.stream().map(
				userPermission -> userPermission.getPermCode().toLowerCase())
				.collect(Collectors.toList());

		List<String> overallPermCodes = grpMasterEntities.stream()
				.map(masterEntity -> masterEntity.getPermCode().toLowerCase())
				.collect(Collectors.toList());

		dto.setUserName(userDetails.getEmail());
		

		for (String permCode : overallPermCodes) {
			
			boolean isPermApplicable = permCodes.contains(permCode);

			/*boolean isPermApplicable = permCodes.stream()
					.anyMatch(permCodeInList -> permCodeInList
							.equals(permCode.toLowerCase()));*/

			switch (permCode) {
			case "g1":
				dto.setG1(isPermApplicable);
				break;
			case "g2":
				dto.setG2(isPermApplicable);
				break;
			case "g3":
				dto.setG3(isPermApplicable);
				break;
			case "g4":
				dto.setG4(isPermApplicable);
				break;

			default:
				break;
			}
		}

		return dto;
		
	}
	/*public static void main(String[] args) {
		
		ITPEventResponseServiceImpl itp = new ITPEventResponseServiceImpl();
		
		itp.setEntityAccess(userDetails, entityDetails, masterEntities);
		itp.setGroupAccess(userDetails, entityDetails, grpMasterEntities);
		
		System.out.println();
		
	}*/
	public static void main(String[] args) {
		
		
			    
			     
			     
			     
		
		
	}
	

}
