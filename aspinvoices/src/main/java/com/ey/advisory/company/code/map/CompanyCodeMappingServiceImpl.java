package com.ey.advisory.company.code.map;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.app.data.entities.client.ErpCompanyCodeMappingEntity;
import com.ey.advisory.app.data.repositories.client.ErpCompanyCodeMappingRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.ErpCompanyCodeDto;

@Service("CompanyCodeMappingServiceImpl")
public class CompanyCodeMappingServiceImpl
		implements CompanyCodeMappingService {

	@Autowired
	@Qualifier("ErpCompanyCodeMappingRepository")
	private ErpCompanyCodeMappingRepository erpCompCodeMappRepo;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoRepository;

	@Autowired
	@Qualifier("ErpInfoEntityRepository")
	private ErpInfoEntityRepository erpInfoEntityRepository;

	public List<CompanyCodeMappingRespDto> getCompanyCodeMapping(
			final CompanyCodeMappingReqDto reqDto) {
		List<CompanyCodeMappingRespDto> respDtos = new ArrayList<>();
		List<EntityInfoEntity> entityInfoEntities = entityInfoRepository
				.findEntityInfoDetails(reqDto.getGroupCode());
		List<Long> entities = new ArrayList<>();
		entityInfoEntities.forEach(entityInfoEntity -> {
			entities.add(entityInfoEntity.getId());
		});
		List<ErpCompanyCodeMappingEntity> compCodeMappList = erpCompCodeMappRepo
				.getCompanyCodeMapping(entities);
		Map<Long, List<ErpCompanyCodeMappingEntity>> mapCompCodeMapping = mapCompanyCodeMapping(
				compCodeMappList);
		List<Object[]> erpInfo = erpInfoEntityRepository.getErpIdName();
		List<ErpCompanyCodeDto> erpCompCodeDtos = new ArrayList<>();
		if (erpInfo != null) {
			for (Object[] object1 : erpInfo) {
				ErpCompanyCodeDto erpDetails = new ErpCompanyCodeDto();
				erpDetails.setId((Long) object1[0]);
				erpDetails.setSourceId((String) object1[1]);
				erpCompCodeDtos.add(erpDetails);
			}
		}
		entityInfoEntities.forEach(entityInfoEntity -> {
			CompanyCodeMappingRespDto respDto = new CompanyCodeMappingRespDto();
			respDto.setEntityId(entityInfoEntity.getId());
			respDto.setEntityName(entityInfoEntity.getEntityName());

			respDto.setSourceId(erpCompCodeDtos);

			List<CompanyCodeDto> companyCodeDtos = new ArrayList<>();
			CompanyCodeDto companyCodeDto = new CompanyCodeDto();
			companyCodeDto.setId(entityInfoEntity.getId());
			companyCodeDto.setCompanyCode(entityInfoEntity.getCompanyHq());
			companyCodeDtos.add(companyCodeDto);
			respDto.setCompanyCode(companyCodeDtos);

			List<ErpCompanyCodeMappingEntity> erpCompanyCodeEntityMap = mapCompCodeMapping
					.get(entityInfoEntity.getId());
			List<CompanyCodeMapItemRespDto> companyCodeMapItems = new ArrayList<>();
			if (erpCompanyCodeEntityMap != null
					&& !erpCompanyCodeEntityMap.isEmpty()) {
				erpCompanyCodeEntityMap.forEach(erpCompanyCodeEntity -> {
					CompanyCodeMapItemRespDto compCodeMapItemDto = new CompanyCodeMapItemRespDto();
					compCodeMapItemDto.setId(erpCompanyCodeEntity.getId());
					compCodeMapItemDto.setCompanyCode(
							erpCompanyCodeEntity.getCompanyCode());
					compCodeMapItemDto
							.setErpId(erpCompanyCodeEntity.getErpId());
					compCodeMapItemDto
							.setSourceId(erpCompanyCodeEntity.getErpSystemId());
					compCodeMapItemDto
							.setStatus(erpCompanyCodeEntity.isDeleted());
					companyCodeMapItems.add(compCodeMapItemDto);
				});
			}
			respDto.setCompCodeMapItemDto(companyCodeMapItems);
			respDtos.add(respDto);
		});
		return respDtos;
	}

	private Map<Long, List<ErpCompanyCodeMappingEntity>> mapCompanyCodeMapping(
			List<ErpCompanyCodeMappingEntity> erpCompMapEntities) {

		Map<Long, List<ErpCompanyCodeMappingEntity>> mapComCodeMapp = new HashMap<>();
		erpCompMapEntities.forEach(erpCompMapEntity -> {
			Long entityId = erpCompMapEntity.getEntityId();
			if (mapComCodeMapp.containsKey(entityId)) {
				List<ErpCompanyCodeMappingEntity> erpComCodeMapEntityList = mapComCodeMapp
						.get(entityId);
				erpComCodeMapEntityList.add(erpCompMapEntity);
				mapComCodeMapp.put(entityId, erpComCodeMapEntityList);
			} else {
				List<ErpCompanyCodeMappingEntity> erpComCodeMapEntityList = new ArrayList<>();
				erpComCodeMapEntityList.add(erpCompMapEntity);
				mapComCodeMapp.put(entityId, erpComCodeMapEntityList);
			}
		});
		return mapComCodeMapp;
	}

	@Override
	public void saveCompanyCodeMapping(List<CompanyCodeMappingReqDto> reqDtos) {
		List<ErpCompanyCodeMappingEntity> codeMappEntities = new ArrayList<>();
		reqDtos.forEach(reqDto -> {
			ErpCompanyCodeMappingEntity codeMappEntity = new ErpCompanyCodeMappingEntity();
			erpCompCodeMappRepo.updIsStaByCompCodeAndEntIdComp(
						reqDto.getCompanyCode());
			codeMappEntity.setErpId(reqDto.getErpId());
			codeMappEntity.setErpSystemId(reqDto.getSourceId());
			codeMappEntity.setCompanyCode(reqDto.getCompanyCode());
			codeMappEntity.setEntityId(reqDto.getEntityId());
			codeMappEntity.setDeleted(reqDto.isStatus());
			User user = SecurityContext.getUser();
			codeMappEntity.setCreatedBy(user.getUserPrincipalName());
			codeMappEntity.setCreatedDate(
					EYDateUtil.toUTCDateTimeFromIST(LocalDateTime.now()));
			codeMappEntities.add(codeMappEntity);
		});
		if (!codeMappEntities.isEmpty()) {
			erpCompCodeMappRepo.saveAll(codeMappEntities);
		}
	}
}
