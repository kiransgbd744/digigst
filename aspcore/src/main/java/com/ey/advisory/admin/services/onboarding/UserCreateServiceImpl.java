package com.ey.advisory.admin.services.onboarding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.EntityUserMapping;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.entities.client.UserPermissionsEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.EntityUserMappingRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.admin.data.repositories.client.UserPermissionsRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.UserCreationDto;
import com.ey.advisory.core.dto.UserCreationItemsDetailsDto;
import com.ey.advisory.core.dto.UserCreationReqDto;

/**
 * @author Umesha.M
 *
 */
@Component("userCreateService")
public class UserCreateServiceImpl implements UserCreateService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserCreateServiceImpl.class);

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("EntityUserMappingRepository")
	private EntityUserMappingRepository entityUserMappingRepository;

	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository userCreationRepository;

	@Autowired
	@Qualifier("UserPermissionsRepository")
	private UserPermissionsRepository userPermissionsRepository;

	private static final String UPDATE_SUCCESSFULLY = " Updated Successfully ";

	private static final String EMAIL_EXITS = "Email Already Exits";

	/**
	 * This method used for find user details.
	 * 
	 * @return
	 */
	public List<UserCreationDto> findUserDetails(
			final UserCreationReqDto userCreaReqDto) {
		List<EntityInfoEntity> entityInfoEntities = entityInfoDetailsRepository
				.findEntityInfoDetails(userCreaReqDto.getGroupCode());
		List<UserCreationDto> userCreationDtos = new ArrayList<>();
		if (!entityInfoEntities.isEmpty()) {
			List<UserCreationEntity> userCreationEntityList = userCreationRepository
					.findAll();

			Map<Long, UserCreationEntity> userCreationEntityMap = userCreationEntityList
					.stream().collect(Collectors.toMap(
							UserCreationEntity::getId, Function.identity()));

			List<EntityUserMapping> entityAllUserMappingList = entityUserMappingRepository
					.findAll();

			Map<Long, List<EntityUserMapping>> entityUserMap = entityAllUserMappingList
					.stream().collect(Collectors
							.groupingBy(EntityUserMapping::getEntityId));
			
			entityInfoEntities.forEach(entityInfoEntity -> {
				UserCreationDto userCreationDto = new UserCreationDto();
				userCreationDto.setEntityId(entityInfoEntity.getId());
				userCreationDto.setEntityName(entityInfoEntity.getEntityName());
				List<UserCreationItemsDetailsDto> userCreaItemsDetailsDtos = new ArrayList<>();
				List<EntityUserMapping> entityUserMappingList = entityUserMap
						.get(entityInfoEntity.getId());
				if (entityUserMappingList != null
						&& !entityUserMappingList.isEmpty()) {
					entityUserMappingList.forEach(entityUserMapping -> {
						UserCreationEntity userCreationEntity = userCreationEntityMap
								.get(entityUserMapping.getUserId());
						if (userCreationEntity != null) {
							UserCreationItemsDetailsDto userCreationItemsDetailsDto = new UserCreationItemsDetailsDto();
							userCreationItemsDetailsDto.setFirstName(
									userCreationEntity.getFirstName());
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("EntityUserMapping: {}",
										entityUserMapping);
							}
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("userId: {}",
										userCreationEntity.getId());
							}
							Long userMappingId = entityUserMapping.getId();

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("userMappingId: {}",
										userMappingId);
							}
							userCreationItemsDetailsDto.setId(userMappingId);
							userCreationItemsDetailsDto.setUserName(
									userCreationEntity.getUserName());
							userCreationItemsDetailsDto.setGroupCode(
									userCreationEntity.getGroupCode());
							userCreationItemsDetailsDto.setLastName(
									userCreationEntity.getLastName());
							userCreationItemsDetailsDto.setEmail(
									userCreationEntity.getEmail());
							userCreationItemsDetailsDto.setMobile(
									userCreationEntity.getMobile());
							userCreationItemsDetailsDto.setUserRole(
									userCreationEntity.getUserRole());
							userCreationItemsDetailsDto
									.setIsFlag(entityUserMapping.getIsFlag());
							userCreationItemsDetailsDto.setCreatedBy(
									userCreationEntity.getCreatedBy());
							userCreationItemsDetailsDto.setModifiedBy(
									userCreationEntity.getModifiedBy());
							userCreaItemsDetailsDtos
									.add(userCreationItemsDetailsDto);
						}
					});
				}
				userCreationDto.setUserCreationItemsDetailsDtos(
						userCreaItemsDetailsDtos);
				userCreationDtos.add(userCreationDto);
			});
		}
		return userCreationDtos;
	}

	/*
	 * This method used for Update a record into User Info table.
	 * 
	 * @see com.ey.advisory.admin.services.onboarding.UserCreateService#
	 * updateUserCreation(java.util.List)
	 */
	@Override
	public String updateUserCreation(
			List<UserCreationReqDto> userCreationReqDtos) {
		String msg = "";
		User princUser = SecurityContext.getUser();
		
		String groupCode = userCreationReqDtos.get(0).getGroupCode();
		Long groupId = groupInfoDetailsRepository.findByGroupId(groupCode);
		
		for (UserCreationReqDto userCreationReqDto : userCreationReqDtos) {

			if (userCreationReqDto.getUserName() != null
					&& !userCreationReqDto.getUserName().isEmpty()
					&& userCreationReqDto.getEmail() != null
					&& !userCreationReqDto.getEmail().isEmpty()) {
				if (userCreationReqDto.getId() != null
						&& userCreationReqDto.getId() > 0) {
					EntityUserMapping entityUserMapping = entityUserMappingRepository
							.getEntityUserMapping(userCreationReqDto.getId());
					UserCreationEntity exitingUser = userCreationRepository
							.findUserEntityByUserNameAndEmail(
									userCreationReqDto.getUserName(),
									userCreationReqDto.getEmail());
					if (exitingUser != null) {
						exitingUser.setFirstName(
								userCreationReqDto.getFirstName());
						exitingUser
								.setLastName(userCreationReqDto.getLastName());
						exitingUser.setMobile(userCreationReqDto.getMobile());
						exitingUser
								.setUserRole(userCreationReqDto.getUserRole());
						exitingUser.setGroupCode(groupCode);
						exitingUser.setGroupId(groupId);
						
						userCreationRepository.save(exitingUser);
						entityUserMapping
								.setIsFlag(userCreationReqDto.isFlag());
						entityUserMappingRepository.save(entityUserMapping);
						msg = UPDATE_SUCCESSFULLY;
					} else {
						UserCreationEntity exitsEmail = userCreationRepository
								.findUserEntityByEmail(
										userCreationReqDto.getEmail());
						if (exitsEmail == null) {
							UserCreationEntity entityUser = userCreationRepository
									.findUserEntityByUserName(
											userCreationReqDto.getUserName());
							entityUser.setEmail(userCreationReqDto.getEmail());
							entityUser.setGroupCode(groupCode);
							entityUser.setGroupId(groupId);
							entityUser.setFirstName(
									userCreationReqDto.getFirstName());
							entityUser.setLastName(
									userCreationReqDto.getLastName());
							entityUser
									.setMobile(userCreationReqDto.getMobile());
							entityUser.setUserRole(
									userCreationReqDto.getUserRole());
							userCreationRepository.save(entityUser);
							entityUserMapping
									.setIsFlag(userCreationReqDto.isFlag());
							entityUserMappingRepository.save(entityUserMapping);
							msg = UPDATE_SUCCESSFULLY;
						} else {
							msg = EMAIL_EXITS;
						}
					}
				} else {
					UserCreationEntity userEntity = userCreationRepository
							.findUserEntityByUserName(
									userCreationReqDto.getUserName());
					if (userEntity != null) {
						EntityUserMapping userMapping = entityUserMappingRepository
								.getUserBy(userCreationReqDto.getEntityId(),
										userEntity.getId());
						if (userMapping == null) {
							UserCreationEntity exitingUser = userCreationRepository
									.findUserEntityByUserNameAndEmail(
											userCreationReqDto.getUserName(),
											userCreationReqDto.getEmail());
							if (exitingUser != null) {
								exitingUser.setFirstName(
										userCreationReqDto.getFirstName());
								exitingUser.setLastName(
										userCreationReqDto.getLastName());
								exitingUser.setMobile(
										userCreationReqDto.getMobile());
								exitingUser.setUserRole(
										userCreationReqDto.getUserRole());
								exitingUser.setGroupCode(groupCode);
								exitingUser.setGroupId(groupId);
								
								userCreationRepository.save(exitingUser);
								EntityUserMapping entityUserMapping = new EntityUserMapping();
								entityUserMapping.setEntityId(
										userCreationReqDto.getEntityId());
								entityUserMapping
										.setUserId(exitingUser.getId());
								entityUserMapping.setGroupCode(
										exitingUser.getGroupCode());
								entityUserMapping
										.setGroupId(exitingUser.getGroupId());
								entityUserMapping
										.setIsFlag(userCreationReqDto.isFlag());
								entityUserMapping.setUpdatedBy(
										princUser.getUserPrincipalName());
								entityUserMapping.setUpdateOn(
										EYDateUtil.toUTCDateTimeFromLocal(
												LocalDateTime.now()));
								EntityUserMapping entityUserMapp = entityUserMappingRepository
										.save(entityUserMapping);

								if (entityUserMapp != null) {
									UserPermissionsEntity entity = new UserPermissionsEntity();
									entity.setPermCode("P1");
									entity.setEntityId(
											entityUserMapp.getEntityId());
									entity.setUserId(
											entityUserMapp.getUserId());
									entity.setApplicable(true);
									entity.setDelete(false);
									entity.setCreatedBy(
											princUser.getUserPrincipalName());
									entity.setCreatedOn(
											EYDateUtil.toUTCDateTimeFromIST(
													LocalDateTime.now()));
									userPermissionsRepository.save(entity);
								}
								msg = UPDATE_SUCCESSFULLY;
							} else {
								UserCreationEntity exitsEmail = userCreationRepository
										.findUserEntityByEmail(
												userCreationReqDto.getEmail());
								if (exitsEmail == null) {
									UserCreationEntity entityUser = userCreationRepository
											.findUserEntityByUserName(
													userCreationReqDto
															.getUserName());
									entityUser.setEmail(
											userCreationReqDto.getEmail());
									entityUser.setFirstName(
											userCreationReqDto.getFirstName());
									entityUser.setLastName(
											userCreationReqDto.getLastName());
									entityUser.setMobile(
											userCreationReqDto.getMobile());
									entityUser.setUserRole(
											userCreationReqDto.getUserRole());
									entityUser.setGroupCode(groupCode);
									entityUser.setGroupId(groupId);
									
									userCreationRepository.save(entityUser);
									EntityUserMapping entityUserMapping = new EntityUserMapping();
									entityUserMapping.setEntityId(
											userCreationReqDto.getEntityId());
									entityUserMapping
											.setUserId(entityUser.getId());
									entityUserMapping.setGroupCode(
											entityUser.getGroupCode());
									entityUserMapping.setGroupId(
											entityUser.getGroupId());
									entityUserMapping.setIsFlag(
											userCreationReqDto.isFlag());
									entityUserMapping.setUpdatedBy(
											princUser.getUserPrincipalName());
									entityUserMapping.setUpdateOn(
											EYDateUtil.toISTDateTimeFromUTC(
													LocalDateTime.now()));
									EntityUserMapping entityUserMapp = entityUserMappingRepository
											.save(entityUserMapping);
									if (entityUserMapp != null) {
										UserPermissionsEntity entity = new UserPermissionsEntity();
										entity.setPermCode("P1");
										entity.setEntityId(
												entityUserMapp.getEntityId());
										entity.setUserId(
												entityUserMapp.getUserId());
										entity.setApplicable(true);
										entity.setDelete(false);
										entity.setCreatedBy(princUser
												.getUserPrincipalName());
										entity.setCreatedOn(
												EYDateUtil.toUTCDateTimeFromIST(
														LocalDateTime.now()));
										userPermissionsRepository.save(entity);
									}
									msg = UPDATE_SUCCESSFULLY;
								} else {
									msg = EMAIL_EXITS;
								}
							}
						} else {
							msg = "User Already Exits";
						}
					} else {
						UserCreationEntity exitsEmail = userCreationRepository
								.findUserEntityByEmail(
										userCreationReqDto.getEmail());
						if (exitsEmail == null) {
							UserCreationEntity user = new UserCreationEntity();
							user.setUserName(userCreationReqDto.getUserName());
							user.setEmail(userCreationReqDto.getEmail());
							user.setFirstName(
									userCreationReqDto.getFirstName());
							user.setLastName(userCreationReqDto.getLastName());
							user.setMobile(userCreationReqDto.getMobile());
							user.setUserRole(userCreationReqDto.getUserRole());
							user.setIsFlag(false);
							UserCreationEntity userCreationEntity = userCreationRepository
									.save(user);
							EntityUserMapping entityUserMapping = new EntityUserMapping();
							entityUserMapping.setEntityId(
									userCreationReqDto.getEntityId());
							entityUserMapping
									.setUserId(userCreationEntity.getId());
							entityUserMapping.setGroupCode(
									userCreationEntity.getGroupCode());
							entityUserMapping.setGroupId(
									userCreationEntity.getGroupId());
							entityUserMapping
									.setIsFlag(userCreationReqDto.isFlag());
							entityUserMapping.setUpdatedBy(
									princUser.getUserPrincipalName());
							entityUserMapping.setUpdateOn(
									EYDateUtil.toUTCDateTimeFromLocal(
											LocalDateTime.now()));
							EntityUserMapping entityUserMapp = entityUserMappingRepository
									.save(entityUserMapping);
							if (entityUserMapp != null) {
								UserPermissionsEntity entity = new UserPermissionsEntity();
								entity.setPermCode("P1");
								entity.setEntityId(
										entityUserMapp.getEntityId());
								entity.setUserId(
										entityUserMapp.getUserId());
								entity.setApplicable(true);

								entity.setCreatedBy(princUser
										.getUserPrincipalName());
								entity.setCreatedOn(
										EYDateUtil.toUTCDateTimeFromIST(
												LocalDateTime.now()));
								entity.setDelete(false);
								userPermissionsRepository.save(entity);
							}
							msg = UPDATE_SUCCESSFULLY;
						} else {
							msg = EMAIL_EXITS;
						}
					}
				}
			} else {
				msg = "Please enter the Email id";
			}
		}
		return msg;
	}

	/*
	 * Delete User Creation Details
	 * 
	 * @see com.ey.advisory.admin.services.onboarding.UserCreateService#
	 * deleteUserCreation(java.util.List)
	 */
	@Override
	public void deleteUserCreation(
			List<UserCreationReqDto> userCreationReqDtos) {
		List<EntityUserMapping> liCreationEntities = new ArrayList<>();
		userCreationReqDtos.forEach(userCreationReqDto -> {
			EntityUserMapping entityUserMapping = new EntityUserMapping();
			entityUserMapping.setEntityId(userCreationReqDto.getEntityId());
			entityUserMapping.setUserId(userCreationReqDto.getId());
			liCreationEntities.add(entityUserMapping);
		});
		if (!liCreationEntities.isEmpty()) {
			liCreationEntities.forEach(liCreationEntity -> {
				if (liCreationEntity.getEntityId() != null
						&& liCreationEntity.getEntityId() > 0
						&& liCreationEntity.getUserId() != null
						&& liCreationEntity.getUserId() > 0) {
					/*
					 * entityUserMappingRepository.deleteRecord(
					 * liCreationEntity.getEntityId(),
					 * liCreationEntity.getUserId());
					 */
				}
			});

		}
	}
}
