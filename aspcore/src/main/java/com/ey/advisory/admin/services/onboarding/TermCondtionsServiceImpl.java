package com.ey.advisory.admin.services.onboarding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.TermConditionsEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.TermConditionsRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.TermCondGstinDto;
import com.ey.advisory.core.dto.TermCondItemRespDto;
import com.ey.advisory.core.dto.TermCondRespDto;
import com.ey.advisory.core.dto.TermConditionsReqDto;

@Service("TermCondtionsServiceImpl")
public class TermCondtionsServiceImpl implements TermCondtionsService {

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("TermConditionsRepository")
	private TermConditionsRepository termCondiRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	public List<TermCondRespDto> getTermCondi(
			final TermConditionsReqDto reqDto) {

		List<TermCondRespDto> termCndRespDtos = new ArrayList<>();

		List<EntityInfoEntity> entitInfoEntities = entityInfoDetailsRepository
				.findEntityInfoDetails(reqDto.getGroupCode());
		List<Long> entities = new ArrayList<>();
		entitInfoEntities.forEach(entitInfoEntity -> {
			entities.add(entitInfoEntity.getId());
		});

		List<TermConditionsEntity> termCondEntities = termCondiRepo
				.getTermConditions(entities);
		List<GSTNDetailEntity> gstinDetEntities = gstNDetailRepository
				.getGstinByEntites(entities);
		Map<Long, List<GSTNDetailEntity>> mapGstinDetbyEntity = mapGstinDetEntity(
				gstinDetEntities);
		Map<Long, List<TermConditionsEntity>> mapTermConditionEntity = mapTermCondtions(
				termCondEntities);
		entitInfoEntities.forEach(entitInfoEntity -> {
			TermCondRespDto termCondRespDto = new TermCondRespDto();
			termCondRespDto.setEntityId(entitInfoEntity.getId());
			termCondRespDto.setEntityName(entitInfoEntity.getEntityName());
			termCondRespDto.setGroupId(entitInfoEntity.getGroupId());
			List<GSTNDetailEntity> gstinDetailEntities = mapGstinDetbyEntity
					.get(entitInfoEntity.getId());
			List<TermCondGstinDto> gstinDtos = new ArrayList<>();

			if (gstinDetailEntities != null && !gstinDetailEntities.isEmpty()) {
				gstinDetailEntities.forEach(gstinDetailEntity -> {
					TermCondGstinDto gstinDto = new TermCondGstinDto();
					gstinDto.setId(gstinDetailEntity.getId());
					gstinDto.setGstin(gstinDetailEntity.getGstin());
					gstinDtos.add(gstinDto);
				});
			}
			termCondRespDto.setGstinDtos(gstinDtos);
			List<TermCondItemRespDto> termCondItemRespDtos = new ArrayList<>();
			List<TermConditionsEntity> termConditionEntities = mapTermConditionEntity
					.get(entitInfoEntity.getId());

			if (termConditionEntities != null
					&& !termConditionEntities.isEmpty()) {
				termConditionEntities.forEach(termConditionEntiy -> {
					TermCondItemRespDto termCondItemRespDto = new TermCondItemRespDto();
					termCondItemRespDto.setId(termConditionEntiy.getId());
					termCondItemRespDto
							.setGstinId(termConditionEntiy.getGstinId());
					String gstin = gstNDetailRepository
							.getGstinById(termConditionEntiy.getGstinId());
					termCondItemRespDto.setGstin(gstin);
					termCondItemRespDto.setTermsCond(
							termConditionEntiy.getConitionsText());
					termCondItemRespDtos.add(termCondItemRespDto);
				});
			}
			termCondRespDto.setTermCondItemRespDtos(termCondItemRespDtos);
			termCndRespDtos.add(termCondRespDto);
		});
		return termCndRespDtos;
	}

	private Map<Long, List<GSTNDetailEntity>> mapGstinDetEntity(
			List<GSTNDetailEntity> gstinDetEntities) {
		Map<Long, List<GSTNDetailEntity>> mapGstinDetiEntities = new HashMap<>();
		gstinDetEntities.forEach(gstinDetEntity -> {
			Long entityId = gstinDetEntity.getEntityId();
			if (mapGstinDetiEntities.containsKey(entityId)) {
				List<GSTNDetailEntity> gstinDetails = mapGstinDetiEntities
						.get(entityId);
				gstinDetails.add(gstinDetEntity);
				mapGstinDetiEntities.put(entityId, gstinDetails);
			} else {
				List<GSTNDetailEntity> gstinDetails = new ArrayList<>();
				gstinDetails.add(gstinDetEntity);
				mapGstinDetiEntities.put(entityId, gstinDetails);
			}
		});
		return mapGstinDetiEntities;
	}

	private Map<Long, List<TermConditionsEntity>> mapTermCondtions(
			List<TermConditionsEntity> termCondiEntities) {
		Map<Long, List<TermConditionsEntity>> mapTermConditionEntity = new HashMap<>();
		termCondiEntities.forEach(termCondiEntity -> {
			Long entityId = termCondiEntity.getEntityId();
			if (mapTermConditionEntity.containsKey(entityId)) {
				List<TermConditionsEntity> termConditionEntities = mapTermConditionEntity
						.get(entityId);
				termConditionEntities.add(termCondiEntity);
				mapTermConditionEntity.put(entityId, termConditionEntities);
			} else {
				List<TermConditionsEntity> termConditionEntities = new ArrayList<>();
				termConditionEntities.add(termCondiEntity);
				mapTermConditionEntity.put(entityId, termConditionEntities);
			}
		});
		return mapTermConditionEntity;
	}

	@Override
	public void saveTermCondi(List<TermConditionsReqDto> reqDtos) {
		List<TermConditionsEntity> termCondiEntities = new ArrayList<>();

		reqDtos.forEach(reqDto -> {
			List<Long> gstinIds = reqDto.getGstinId();
			if (gstinIds != null && !gstinIds.isEmpty()) {
				gstinIds.forEach(gstinId -> {
					TermConditionsEntity termCondiEntity = new TermConditionsEntity();
					termCondiEntity.setGroupId(reqDto.getGroupId());
					termCondiEntity.setEntityId(reqDto.getEntityId());
					termCondiEntity.setGstinId(gstinId);
					termCondiEntity.setConitionsText(reqDto.getTermsCond());
					User user = SecurityContext.getUser();
					termCondiEntity.setCreatedBy(user.getUserPrincipalName());
					termCondiEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromIST(LocalDateTime.now()));
					termCondiEntities.add(termCondiEntity);
				});
			}
		});
		if (!termCondiEntities.isEmpty()) {
			termCondiRepo.saveAll(termCondiEntities);
		}
	}

	@Override
	public void deleteTermCondtions(TermConditionsReqDto reqDto) {
			termCondiRepo.deleteTermCondtions(reqDto.getId(),
					reqDto.getEntityId());
		
	}
}
