package com.ey.advisory.admin.services.onboarding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.PermissionsMasterEntity;
import com.ey.advisory.admin.data.entities.client.ProfilePermissionsMasterEntity;
import com.ey.advisory.admin.data.entities.client.ProfilesMasterEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.entities.client.UserPermissionsEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.PermissionsMasterRepository;
import com.ey.advisory.admin.data.repositories.client.ProfilePermissionsMasterRepository;
import com.ey.advisory.admin.data.repositories.client.ProfilesMasterRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.admin.data.repositories.client.UserPermissionsRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.AppPermissionReqDto;
import com.ey.advisory.core.dto.AppPermissionRespDto;
import com.ey.advisory.core.dto.AppProfilePermissionResp;
import com.ey.advisory.core.dto.AppProfileResp;

/**
 * 
 * @author Umesha.M
 *
 */
@Component("appPermissionService")
public class AppPermissionServiceImpl implements AppPermissionService {

	@Autowired
	@Qualifier("PermissionsMasterRepository")
	private PermissionsMasterRepository permissionsMasterRepository;

	@Autowired
	@Qualifier("ProfilesMasterRepository")
	private ProfilesMasterRepository profilesMasterRepository;

	@Autowired
	@Qualifier("ProfilePermissionsMasterRepository")
	private ProfilePermissionsMasterRepository profPermMasterRepo;
	@Autowired
	@Qualifier("UserPermissionsRepository")
	private UserPermissionsRepository userPermissionsRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository userCreationRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AppPermissionServiceImpl.class);

	/**
	 * This method list of App Permission Response from Get App Permission Table
	 */
	@Override
	public List<AppPermissionRespDto> getPermissions(
			AppPermissionReqDto apPermReqDto) {
		List<AppPermissionRespDto> appPermRespDtos = new ArrayList<>();

		List<PermissionsMasterEntity> masterEntities = permissionsMasterRepository
				.getAllPermissions();

		masterEntities.forEach(masterEntity -> {
			AppPermissionRespDto appPermRespDto = new AppPermissionRespDto();
			appPermRespDto.setPermCode(masterEntity.getPermCode());
			appPermRespDto.setPermDesc(masterEntity.getPermDesc());
			appPermRespDto.setCategory(masterEntity.getCategory());

			// Getting user Id and email from user
			UserCreationEntity userInfo = userCreationRepository
					.findUserEntityByUserName(apPermReqDto.getUserName());

			UserPermissionsEntity userApp = new UserPermissionsEntity();

			if (userInfo != null) {
				userApp = userPermissionsRepository.getUserPermissions(
						apPermReqDto.getEntityId(), userInfo.getId(),
						masterEntity.getPermCode());
				// appPermRespDto.setEmail(userInfo.getEmail());
			}
			if (userApp != null) {
				appPermRespDto.setApplicaple(userApp.isApplicable());
				appPermRespDto.setId(userApp.getId());
			} else {
				appPermRespDto.setApplicaple(false);
				appPermRespDto.setId(null);
			}
			appPermRespDtos.add(appPermRespDto);
		});
		return appPermRespDtos;
	}

	/**
	 * This method update all User Permission entities to app Permission
	 */
	@Override
	public void updatePermissions(List<AppPermissionReqDto> appPermReqDtos) {
		List<UserPermissionsEntity> upentities = new ArrayList<>();
		appPermReqDtos.forEach(appPermReqDto -> {
			Long userId = userCreationRepository
					.findIdByUserName(appPermReqDto.getUserName());
			UserPermissionsEntity uapentity = new UserPermissionsEntity();
			uapentity.setId(appPermReqDto.getId());
			uapentity.setEntityId(appPermReqDto.getEntityId());
			uapentity.setUserId(userId);
			uapentity.setPermCode(appPermReqDto.getPermCode());
			uapentity.setApplicable(appPermReqDto.isApplicaple());
			uapentity.setCreatedOn(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			User user = SecurityContext.getUser();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("User Name: {}", user.getUserPrincipalName());
			}
			uapentity.setCreatedBy(user.getUserPrincipalName());
			upentities.add(uapentity);
		});
		userPermissionsRepository.saveAll(upentities);
	}

	@Override
	public List<AppProfileResp> getProfilePermission() {
		List<ProfilesMasterEntity> profileMasters = profilesMasterRepository
				.getProfiles();
		List<AppProfileResp> respDtos = new ArrayList<>();
		if (profileMasters != null) {
			profileMasters.forEach(profileMaster -> {
				AppProfileResp dto = new AppProfileResp();
				dto.setId(profileMaster.getId());
				dto.setProfileName(profileMaster.getProfileName());
				if (profileMaster.getProfileName() != null) {
					List<ProfilePermissionsMasterEntity> profPerMasterEntities = profPermMasterRepo
							.getAllProfilePerm(profileMaster.getProfileName());
					List<AppProfilePermissionResp> resps = new ArrayList<>();
					if (profPerMasterEntities != null) {
						profPerMasterEntities.forEach(profPerMasterEntity -> {
							AppProfilePermissionResp resp = new AppProfilePermissionResp();
							resp.setProfileName(
									profPerMasterEntity.getProfileName());
							resp.setPromCode(profPerMasterEntity.getPermCode());
							resps.add(resp);
						});
						dto.setAppProfilePermissionResps(resps);
					}
				}
				respDtos.add(dto);
			});
		}
		return respDtos;
	}
}
