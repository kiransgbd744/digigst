package com.ey.advisory.admin.services.onboarding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityAtConfigEntity;
import com.ey.advisory.admin.data.entities.client.EntityAtValueEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityAtConfiRepository;
import com.ey.advisory.admin.data.repositories.client.EntityAtValueRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.app.util.AspDocumentConstants;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.OrgaItemDetailsResDto;
import com.ey.advisory.core.dto.OrganizationDataResDto;
import com.ey.advisory.core.dto.OrganizationReqDto;
import com.ey.advisory.core.dto.OrganizationResDto;

/**
 * @author Umesha.M
 *
 */
@Component("OrganizationServiceImpl")
public class OrganizationServiceImpl implements OrganizationService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OrganizationServiceImpl.class);

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("entityAtConfiRepository")
	private EntityAtConfiRepository entityAtConfiRepository;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("entityAtValueRepository")
	private EntityAtValueRepository entityAtValueRepository;

	private static final String GSTIN = "GSTIN";

	private static final String N = "N";

	@Override
	public List<OrganizationResDto> getOrganization(
			final OrganizationReqDto organizationReqDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("OrganizationServiceImpl getOrganization begin");
		}
		List<EntityInfoEntity> entities = entityInfoDetailsRepository
				.findEntityInfoDetails(organizationReqDto.getGroupCode());
		List<OrganizationResDto> organizationResDtos = new ArrayList<>();
		if (!entities.isEmpty()) {
			entities.forEach(entity -> {
				OrganizationResDto organizationResDto = new OrganizationResDto();
				organizationResDto.setEntityId(entity.getId());
				organizationResDto.setEntityName(entity.getEntityName());
				List<EntityAtConfigEntity> entityAtConfigEntities = entityAtConfiRepository
						.findAllEntityAtConfigEntity(
								organizationReqDto.getGroupCode(),
								entity.getId());
				List<OrgaItemDetailsResDto> orgaItemDetailsResDtos = new ArrayList<>();
				if (!entityAtConfigEntities.isEmpty()) {
					entityAtConfigEntities.forEach(entityAtConfigEntity -> {
						OrgaItemDetailsResDto orgaItemDetailsResDto = new OrgaItemDetailsResDto();
						if (!GSTIN.equals(entityAtConfigEntity.getAtCode())) {
							if (N.equals(entityAtConfigEntity.getAtInward())
									&& N.equals(entityAtConfigEntity
											.getAtOutward())) {
								orgaItemDetailsResDto.setApplicable(false);
							} else {
								orgaItemDetailsResDto.setApplicable(true);
							}
							List<EntityAtValueEntity> atValue = entityAtValueRepository
									.extingAttributeValue(entity.getId(),
											entityAtConfigEntity.getAtCode());
							if (atValue != null) {
								orgaItemDetailsResDto.setActive(true);
							} else {
								orgaItemDetailsResDto.setActive(false);
							}
							orgaItemDetailsResDto
									.setId(entityAtConfigEntity.getId());
							orgaItemDetailsResDto.setAttCode(
									entityAtConfigEntity.getAtCode());
							orgaItemDetailsResDto.setAttName(
									entityAtConfigEntity.getAtName());
							orgaItemDetailsResDto.setInword(
									entityAtConfigEntity.getAtInward());
							orgaItemDetailsResDto.setOutword(
									entityAtConfigEntity.getAtOutward());
							orgaItemDetailsResDtos.add(orgaItemDetailsResDto);
						}
					});

					organizationResDto
							.setOrgaResItemDetailsDto(orgaItemDetailsResDtos);
				}
				organizationResDtos.add(organizationResDto);
			});
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("OrganizationServiceImpl getOrganization End");
		}
		return organizationResDtos;
	}

	@Override
	public void updateOrganization(
			final List<OrganizationReqDto> organizationReqDtos) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("OrganizationServiceImpl updateOrganization begin");
		}
		List<EntityAtConfigEntity> entityAtConfigEntities = new ArrayList<>();
		organizationReqDtos.forEach(organizationReqDto -> {
			EntityAtConfigEntity entityAtConfigEntity = new EntityAtConfigEntity();
			entityAtConfigEntity.setId(organizationReqDto.getId());
			Long groupId = groupInfoDetailsRepository
					.findByGroupId(organizationReqDto.getGroupCode());
			entityAtConfigEntity
					.setGroupcode(organizationReqDto.getGroupCode());
			entityAtConfigEntity.setEntityId(organizationReqDto.getEntityId());
			entityAtConfigEntity.setGroupId(groupId);
			entityAtConfigEntity.setAtCode(organizationReqDto.getAttCode());
			entityAtConfigEntity.setAtName(organizationReqDto.getAttName());
			entityAtConfigEntity.setAtInward(organizationReqDto.getInword());
			entityAtConfigEntity.setAtOutward(organizationReqDto.getOutword());
			User user = SecurityContext.getUser();
			entityAtConfigEntity.setModifiedBy(user.getUserPrincipalName());
			entityAtConfigEntity.setModifiedOn(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			entityAtConfigEntities.add(entityAtConfigEntity);
		});

		if (!entityAtConfigEntities.isEmpty()) {
			entityAtConfigEntities.forEach(entityAtConfigEntity -> {
				if (entityAtConfigEntity.getId() != null
						&& entityAtConfigEntity.getId() > 0) {
					Optional<EntityAtConfigEntity> ids = entityAtConfiRepository
							.findById(entityAtConfigEntity.getId());
					if (ids.isPresent()) {
						entityAtConfigEntity.setId(ids.get().getId());
						entityAtConfigEntity.setGroupId(ids.get().getGroupId());
						entityAtConfiRepository.save(entityAtConfigEntity);
					}
				}
			});
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("OrganizationServiceImpl updateOrganization End");
		}
	}

	@Override
	public List<OrganizationDataResDto> getOrganizationData(
			OrganizationReqDto organizationReqDto) {
		List<EntityAtValueEntity> entityAtValueentities = entityAtValueRepository
				.getAllAttributes(organizationReqDto.getGroupCode(),
						organizationReqDto.getEntityId(),
						organizationReqDto.getAttCode());
		List<OrganizationDataResDto> organizationDataResDtos = new ArrayList<>();
		if (entityAtValueentities != null) {
			entityAtValueentities.forEach(entityAtValueentity -> {
				OrganizationDataResDto orgDataResDto = new OrganizationDataResDto();
				orgDataResDto.setId(entityAtValueentity.getId());
				orgDataResDto
						.setAttributeName(entityAtValueentity.getAtValue());
				organizationDataResDtos.add(orgDataResDto);
			});
		}
		return organizationDataResDtos;
	}

	@Override
	public String addOrganizationData(
			List<OrganizationReqDto> organizationReqDtos) {
		String msg = null;
		for (OrganizationReqDto organizationReqDto : organizationReqDtos) {
			Long groupId = groupInfoDetailsRepository
					.findByGroupId(organizationReqDto.getGroupCode());
			EntityAtValueEntity exitingEntityAtValue = entityAtValueRepository
					.exitingEntityAtValue(organizationReqDto.getGroupCode(),
							organizationReqDto.getEntityId(),
							organizationReqDto.getAttCode(),
							organizationReqDto.getAttributeName());
			if (exitingEntityAtValue == null) {
				EntityAtValueEntity entityAtValueEntity = new EntityAtValueEntity();
				entityAtValueEntity.setId(organizationReqDto.getId());
				EntityAtConfigEntity entityAtConfig = entityAtConfiRepository
						.entityAtConfig(organizationReqDto.getEntityId(),
								organizationReqDto.getAttCode());
				if (entityAtConfig != null) {
					entityAtValueEntity
							.setEntityAtConfigId(entityAtConfig.getId());
				}
				entityAtValueEntity.setAtCode(organizationReqDto.getAttCode());
				entityAtValueEntity.setAtValue(
						organizationReqDto.getAttributeName().trim());
				entityAtValueEntity
						.setEntityId(organizationReqDto.getEntityId());
				entityAtValueEntity.setGroupId(groupId);
				entityAtValueEntity
						.setGroupCode(organizationReqDto.getGroupCode());
				User user = SecurityContext.getUser();
				entityAtValueEntity.setCreatedBy(user.getUserPrincipalName());
				entityAtValueEntity.setCreatedOn(
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				entityAtValueRepository.save(entityAtValueEntity);
				msg = "Uploaded Successfully";
			} else {
				msg = "Attributes Already Exits";
			}

		}
		return msg;
	}

	@Override
	public String deleteOrganizationData(
			List<OrganizationReqDto> organizationReqDtos) {
		String msg = "";
		List<Long> ids = new ArrayList<>();
		for (OrganizationReqDto dto : organizationReqDtos) {
			ids.add(dto.getId());
		}
		entityAtValueRepository.deleteAttributes(ids);
		msg = "Deleted Succefully";
		return msg;
	}

	@Override
	public Map<Long, List<Pair<String, String>>> getAllEntityAtValMap() {
		String groupCode = TenantContext.getTenantId();
		List<EntityAtValueEntity> allEntityAtValues = entityAtValueRepository
				.getAllEntityAtValues(groupCode);
		return allEntityAtValues.stream()
				.collect(Collectors.groupingBy(EntityAtValueEntity::getEntityId,
						Collectors.mapping(
								obj -> new Pair<String, String>(obj.getAtCode(),
										obj.getAtValue()),
								Collectors.toList())));
	}

	@Override
	public Map<EntityAtConfigKey, Map<Long, String>> getEntityAtConfMap(
			String transDocType) {
		String groupCode = TenantContext.getTenantId();
		List<EntityAtConfigEntity> allEntityAtConf = entityAtConfiRepository
				.findAllAtConfigEntity(groupCode);
		Map<EntityAtConfigKey, Map<Long, String>> map = new HashMap<>();
		if (AspDocumentConstants.TransDocTypes.OUTWARD.getType()
				.equals(transDocType)) {
			map = allEntityAtConf.stream()
					.collect(Collectors.groupingBy(
							entityAtConfigKey -> new EntityAtConfigKey(
									entityAtConfigKey.getEntityId(),
									entityAtConfigKey.getAtCode()),
							Collectors.toMap(EntityAtConfigEntity::getId,
									EntityAtConfigEntity::getAtOutward)));
		}
		if (AspDocumentConstants.TransDocTypes.INWARD.getType()
				.equals(transDocType)) {
			map = allEntityAtConf.stream()
					.collect(Collectors.groupingBy(
							entityAtConfigKey -> new EntityAtConfigKey(
									entityAtConfigKey.getEntityId(),
									entityAtConfigKey.getAtCode()),
							Collectors.toMap(EntityAtConfigEntity::getId,
									EntityAtConfigEntity::getAtInward)));
		}
		return map;
	}
}
