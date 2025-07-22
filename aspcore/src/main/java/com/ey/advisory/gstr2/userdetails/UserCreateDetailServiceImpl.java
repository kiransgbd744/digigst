package com.ey.advisory.gstr2.userdetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.EntityUserMapping;
import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.EntityUserMappingRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.admin.data.repositories.client.UserPermissionsRepository;

/**
 * @author Umesha.M
 *
 */
@Component("UserCreateDetailServiceImpl")
public class UserCreateDetailServiceImpl implements UserCreateDetailService {

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

	/**
	 * This method used for find user details.
	 * 
	 * @return
	 */
	public List<UserCreationDto> findUserDetail(
			final UserCreationReqDto userCreaReqDto) {
		List<EntityInfoEntity> entityInfoEntities = entityInfoDetailsRepository
				.findEntityInfoDetails(userCreaReqDto.getGroupCode());
		List<UserCreationDto> userCreationDtos = new ArrayList<>();
		if (!entityInfoEntities.isEmpty()) {
			entityInfoEntities.forEach(entityInfoEntity -> {
				UserCreationDto userCreationDto = new UserCreationDto();
				userCreationDto.setEntityId(entityInfoEntity.getId());
				userCreationDto.setEntityName(entityInfoEntity.getEntityName());
				List<UserCreationItemsDetailsDto> userCreaItemsDetailsDtos = new ArrayList<>();
				List<EntityUserMapping> entityUserMappingList = entityUserMappingRepository
						.getUserIdsBasedOnEntityIdWithoutFlag(
								entityInfoEntity.getId());
				if (entityUserMappingList != null
						&& !entityUserMappingList.isEmpty()) {
					entityUserMappingList.forEach(entityUserMapping -> {
						Optional<UserCreationEntity> userCreationEntity = userCreationRepository
								.findById(entityUserMapping.getUserId());
						if (userCreationEntity.isPresent()) {
							UserCreationItemsDetailsDto userCreationItemsDetailsDto = new UserCreationItemsDetailsDto();
							userCreationItemsDetailsDto.setFirstName(
									userCreationEntity.get().getFirstName());
							Long userMappingId = entityUserMappingRepository
									.getidBasedOnUserIdAndEntityId(
											userCreationEntity.get().getId(),
											entityInfoEntity.getId());
							userCreationItemsDetailsDto.setId(userMappingId);
							userCreationItemsDetailsDto.setUserName(
									userCreationEntity.get().getUserName());
							userCreationItemsDetailsDto.setGroupCode(
									userCreationEntity.get().getGroupCode());
							userCreationItemsDetailsDto.setLastName(
									userCreationEntity.get().getLastName());
							userCreationItemsDetailsDto.setEmail(
									userCreationEntity.get().getEmail());
							userCreationItemsDetailsDto.setMobile(
									userCreationEntity.get().getMobile());
							userCreationItemsDetailsDto.setUserRole(
									userCreationEntity.get().getUserRole());
							userCreationItemsDetailsDto
									.setIsFlag(entityUserMapping.getIsFlag());
							userCreationItemsDetailsDto.setCreatedBy(
									userCreationEntity.get().getCreatedBy());
							userCreationItemsDetailsDto.setModifiedBy(
									userCreationEntity.get().getModifiedBy());
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

}
