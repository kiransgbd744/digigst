package com.ey.advisory.admin.fileupload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.DataSecurityEntity;
import com.ey.advisory.admin.data.entities.client.EntityAtValueEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.DataSecurityRepository;
import com.ey.advisory.admin.data.repositories.client.EntityAtConfiRepository;
import com.ey.advisory.admin.data.repositories.client.EntityAtValueRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.app.util.HeaderCheckerUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.GroupService;

@Service("DataSecurityFileUploadServiceImpl")
public class DataSecurityFileUploadServiceImpl implements DataSecurityFileUploadService {

	private static final String[] EXPECTED_HEADERS = { "UserName", "GSTIN", "Plant Code", "Division", "Sub Division",
			"Location", "Sales Organization", "Distribution Channel", "Purchase Organization", "Profit Centre 1",
			"Profit Centre 2", "Profit Centre 3", "Profit Centre 4", "Profit Centre 5", "Profit Centre 6",
			"Profit Centre 7", "Profit Centre 8","Source ID" };

	@Autowired
	@Qualifier("HeaderCheckerUtil")
	private HeaderCheckerUtil headerCheckerUtil;

	@Autowired
	@Qualifier("entityAtConfiRepository")
	private EntityAtConfiRepository entityAtConfiRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository userCreationRepository;

	@Autowired
	@Qualifier("groupService")
	private GroupService groupService;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("entityAtValueRepository")
	private EntityAtValueRepository entityAtValueRepository;

	@Autowired
	@Qualifier("dataSecurityRepository")
	private DataSecurityRepository dataSecRepository;

	public List<String> uploadDataSecurity(List<Object[]> dataSecurityObj, Object[] header, String groupCode,
			Long entityId) {
		List<String> errorMsgs = new ArrayList<>();
		Long groupId = groupInfoDetailsRepository.findByGroupId(groupCode);
		Pair<Boolean, String> pair = headerCheckerUtil.validateHeaders(EXPECTED_HEADERS, header);

		// Convert the object array to domain objects.
		if (pair.getValue0()) {
			List<DataSecurityEntity> dataSecurityEntities = new ArrayList<>();
			EntityInfoEntity entityInfo = entityInfoDetailsRepository.findEntityByEntityId(entityId);
			User user = SecurityContext.getUser();
			String entityName = entityInfo.getEntityName();
			List<Long> entityAtValueIds = new ArrayList<>();
			Long userId = null;
			for (Object[] dataSecurityObject : dataSecurityObj) {
				String userName = dataSecurityObject[0] != null ? String.valueOf(dataSecurityObject[0]) : null;
				if (userName != null && !userName.trim().isEmpty()) {
					userId = userCreationRepository.findIdByUserName(userName);

					if (userId != null) {
						String gstnObj = dataSecurityObject[1] != null ? String.valueOf(dataSecurityObject[1]) : null;
						if (gstnObj != null && !gstnObj.trim().isEmpty()) {
							if (gstnObj.contains(",")) {
								String[] gstns = gstnObj.split(",");
								List<String> gstinList = Arrays.asList(gstns);
								for (String gstin : gstinList) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository.getEntityAtValue(gstin,
											entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository.getEntityAtValue(gstnObj,
										entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}
						String plantCodeObj = dataSecurityObject[2] != null ? String.valueOf(dataSecurityObject[2])
								: null;
						if (plantCodeObj != null && !plantCodeObj.trim().isEmpty()) {
							if (plantCodeObj.contains(",")) {
								String[] plantCodes = plantCodeObj.split(",");
								List<String> plantCodeList = Arrays.asList(plantCodes);
								for (String plantCode : plantCodeList) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(plantCode, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(plantCodeObj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}
						String divisionObj = dataSecurityObject[3] != null ? String.valueOf(dataSecurityObject[3])
								: null;
						if (divisionObj != null && !divisionObj.trim().isEmpty()) {
							if (divisionObj.contains(",")) {
								String[] divisions = divisionObj.split(",");
								List<String> divisionList = Arrays.asList(divisions);
								for (String division : divisionList) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(division, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(divisionObj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}
						String subDivisionObj = dataSecurityObject[4] != null ? String.valueOf(dataSecurityObject[4])
								: null;
						if (subDivisionObj != null && !subDivisionObj.trim().isEmpty()) {
							if (subDivisionObj.contains(",")) {
								String[] subDivisions = subDivisionObj.split(",");
								List<String> subDivisionList = Arrays.asList(subDivisions);
								for (String subDivision : subDivisionList) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(subDivision, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(subDivisionObj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}

						String locationObj = dataSecurityObject[5] != null ? String.valueOf(dataSecurityObject[5])
								: null;
						if (locationObj != null) {
							if (locationObj.contains(",")) {
								String[] locations = locationObj.split(",");
								List<String> locationList = Arrays.asList(locations);
								for (String location : locationList) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(location, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(locationObj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}
						String salesOrgObj = dataSecurityObject[6] != null ? String.valueOf(dataSecurityObject[6])
								: null;
						if (salesOrgObj != null && !salesOrgObj.trim().isEmpty()) {
							if (salesOrgObj.contains(",")) {
								String[] salesOrgs = salesOrgObj.split(",");
								List<String> salesOrgList = Arrays.asList(salesOrgs);
								for (String salesOrg : salesOrgList) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(salesOrg, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(salesOrgObj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}

						String disChannelObj = dataSecurityObject[7] != null ? String.valueOf(dataSecurityObject[7])
								: null;
						if (disChannelObj != null && !disChannelObj.trim().isEmpty()) {
							if (disChannelObj.contains(",")) {
								String[] disChannels = disChannelObj.split(",");
								List<String> disChannelList = Arrays.asList(disChannels);
								for (String disChannel : disChannelList) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(disChannel, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(disChannelObj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}

						String purchOrgObj = dataSecurityObject[8] != null ? String.valueOf(dataSecurityObject[8])
								: null;
						if (purchOrgObj != null) {
							if (purchOrgObj.contains(",")) {
								String[] purchOrgs = purchOrgObj.split(",");
								List<String> purchOrgList = Arrays.asList(purchOrgs);
								for (String purchOrg : purchOrgList) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(purchOrg, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(purchOrgObj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}

						String profCentre1Obj = dataSecurityObject[9] != null ? String.valueOf(dataSecurityObject[9])
								: null;
						if (profCentre1Obj != null && !profCentre1Obj.trim().isEmpty()) {
							if (profCentre1Obj.contains(",")) {
								String[] profCentre1s = profCentre1Obj.split(",");
								List<String> profCentre1List = Arrays.asList(profCentre1s);
								for (String profCentre1 : profCentre1List) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(profCentre1, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(profCentre1Obj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}

						String profCentre2Obj = dataSecurityObject[10] != null ? String.valueOf(dataSecurityObject[10])
								: null;
						if (profCentre2Obj != null && !profCentre2Obj.trim().isEmpty()) {
							if (profCentre2Obj.contains(",")) {
								String[] profCentre2s = profCentre2Obj.split(",");
								List<String> profCentre2List = Arrays.asList(profCentre2s);
								for (String profCentre2 : profCentre2List) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(profCentre2, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(profCentre2Obj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}
						String profCentre3Obj = dataSecurityObject[11] != null ? String.valueOf(dataSecurityObject[11])
								: null;
						if (profCentre3Obj != null && !profCentre3Obj.trim().isEmpty()) {
							if (profCentre3Obj.contains(",")) {
								String[] profCentre3s = profCentre3Obj.split(",");
								List<String> profCentre3List = Arrays.asList(profCentre3s);
								for (String profCentre3 : profCentre3List) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(profCentre3, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(profCentre3Obj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}
						String profCentre4Obj = dataSecurityObject[12] != null ? String.valueOf(dataSecurityObject[12])
								: null;
						if (profCentre4Obj != null && !profCentre4Obj.trim().isEmpty()) {
							if (profCentre4Obj.contains(",")) {
								String[] profCentre4s = profCentre4Obj.split(",");
								List<String> profCentre4List = Arrays.asList(profCentre4s);
								for (String profCentre4 : profCentre4List) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(profCentre4, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(profCentre4Obj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}
						String profCentre5Obj = dataSecurityObject[13] != null ? String.valueOf(dataSecurityObject[13])
								: null;
						if (profCentre5Obj != null && !profCentre5Obj.trim().isEmpty()) {
							if (profCentre5Obj.contains(",")) {
								String[] profCentre5s = profCentre5Obj.split(",");
								List<String> profCentre5List = Arrays.asList(profCentre5s);
								for (String profCentre5 : profCentre5List) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(profCentre5, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(profCentre5Obj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}
						String profCentre6Obj = dataSecurityObject[14] != null ? String.valueOf(dataSecurityObject[14])
								: null;
						if (profCentre6Obj != null && !profCentre6Obj.trim().isEmpty()) {
							if (profCentre6Obj.contains(",")) {
								String[] profCentre6s = profCentre6Obj.split(",");
								List<String> profCentre6List = Arrays.asList(profCentre6s);
								for (String profCentre6 : profCentre6List) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(profCentre6, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(profCentre6Obj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}
						String profCentre7Obj = dataSecurityObject[15] != null ? String.valueOf(dataSecurityObject[15])
								: null;
						if (profCentre7Obj != null && !profCentre7Obj.trim().isEmpty()) {
							if (profCentre7Obj.contains(",")) {
								String[] profCentre7s = profCentre7Obj.split(",");
								List<String> profCentre7List = Arrays.asList(profCentre7s);
								for (String profCentre7 : profCentre7List) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(profCentre7, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(profCentre7Obj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}

						String profCentre8Obj = dataSecurityObject[16] != null ? String.valueOf(dataSecurityObject[16])
								: null;
						if (profCentre8Obj != null && !profCentre8Obj.trim().isEmpty()) {
							if (profCentre8Obj.contains(",")) {
								String[] profCentre8s = profCentre8Obj.split(",");
								List<String> profCentre8List = Arrays.asList(profCentre8s);
								for (String profCentre8 : profCentre8List) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(profCentre8, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(profCentre8Obj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}
						
						String sourceIdObj = dataSecurityObject[17] != null ? String.valueOf(dataSecurityObject[17])
								: null;
						if (sourceIdObj != null && !sourceIdObj.trim().isEmpty()) {
							if (sourceIdObj.contains(",")) {
								String[] sourceIdObjs = sourceIdObj.split(",");
								List<String> sourceIdObjList = Arrays.asList(sourceIdObjs);
								for (String sourceId : sourceIdObjList) {
									EntityAtValueEntity entityAtValue = entityAtValueRepository
											.getEntityAtValue(sourceId, entityId);
									if (entityAtValue != null) {
										setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
												userId, user, entityAtValue, entityAtValueIds);
									}
								}
							} else {
								EntityAtValueEntity entityAtValue = entityAtValueRepository
										.getEntityAtValue(sourceIdObj, entityId);
								if (entityAtValue != null) {
									setDataSecurity(groupCode, groupId, entityId, dataSecurityEntities, entityName,
											userId, user, entityAtValue, entityAtValueIds);
								}
							}
						}
					}
				} else {
					errorMsgs.add("User name should be Mandatory");
				}
			}
			if (entityAtValueIds != null && !entityAtValueIds.isEmpty() && userId != null && entityId != null) {
				dataSecRepository.markAttrsAsDeletedForUser(userId, entityAtValueIds, entityId);
			}
			if (dataSecurityEntities !=null && !dataSecurityEntities.isEmpty()) {
				dataSecRepository.saveAll(dataSecurityEntities);
			}
		} else {
			errorMsgs.add("Header Length should be match");
		}
		return errorMsgs;
	}

	private void setDataSecurity(String groupCode, Long groupId, Long entityId,
			List<DataSecurityEntity> dataSecurityEntities, String entityName, Long userId, User user,
			EntityAtValueEntity entityAtValue, List<Long> entityIds) {
		DataSecurityEntity dataSecuEntity = new DataSecurityEntity();
		dataSecuEntity.setUserId(userId);
		entityIds.add(entityAtValue.getId());
		dataSecuEntity.setEntityAtValueId(entityAtValue.getId());
		dataSecuEntity.setAtCode(entityAtValue.getAtCode());
		dataSecuEntity.setEntityId(entityId);
		dataSecuEntity.setEntityName(entityName);
		dataSecuEntity.setGroupId(groupId);
		dataSecuEntity.setGroupCode(groupCode);
		dataSecuEntity.setCreatedBy(user.getUserPrincipalName());
		dataSecuEntity.setCreatedOn(EYDateUtil.toLocalDateTimeFromUTC(LocalDateTime.now()));
		dataSecurityEntities.add(dataSecuEntity);
	}
}
