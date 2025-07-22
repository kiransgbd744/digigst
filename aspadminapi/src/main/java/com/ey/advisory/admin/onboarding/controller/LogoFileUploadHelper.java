package com.ey.advisory.admin.onboarding.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.LogoConfigEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.LogoConfigRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.LogoResponseDto;
import com.ey.advisory.core.dto.TemplateSelectionReqDto;

@Service("LogoFileUploadHelper")
public class LogoFileUploadHelper {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(LogoFileUploadHelper.class);

	@Autowired
	@Qualifier("LogoConfigRepository")
	private LogoConfigRepository logoConfigRepo;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	public void logFileUpload(MultipartFile[] files, String groupCode,
			Long entityId) {

		MultipartFile file = files[0];
		Long groupId = groupInfoDetailsRepository.findByGroupId(groupCode);

		List<Long> gstinIds = gstnDetailRepository.findidByEntityId(entityId);
		List<LogoConfigEntity> entites = new ArrayList<>();

		String fileName = file.getOriginalFilename();
		User user = SecurityContext.getUser();
		if (gstinIds != null && !gstinIds.isEmpty()) {
			gstinIds.forEach(gstinId -> {
				try {
					LogoConfigEntity entity = new LogoConfigEntity();
					byte[] logoFile = file.getBytes();
					logoConfigRepo.updateLogoConfigFile(entityId, gstinId);
					entity.setLogofile(logoFile);
					entity.setEntityId(entityId);
					entity.setGroupId(groupId);
					entity.setGstinId(gstinId);
					entity.setLogoType(fileName);
					entity.setCreatedBy(user.getUserPrincipalName());
					entity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now()));
					entites.add(entity);
				} catch (Exception e) {
					LOGGER.error("Exception Occured: ", e);
				}
			});
		}
		if (!entites.isEmpty()) {
			logoConfigRepo.saveAll(entites);
		}
	}

	public List<LogoResponseDto> getLogoFile(
			final TemplateSelectionReqDto reqDto) {

		List<LogoResponseDto> respDtos = new ArrayList<>();
		List<EntityInfoEntity> entitInfoEntities = entityInfoDetailsRepository
				.findEntityInfoDetails(reqDto.getGroupCode());
		List<Long> entities = new ArrayList<>();
		entitInfoEntities.forEach(entitInfoEntity -> {
			entities.add(entitInfoEntity.getId());
		});
		List<Object[]> objs = logoConfigRepo.getLogoConfigDetails(entities);
		List<LogoResponseDto> logoRespDtos = new ArrayList<>();
		if (objs != null && !objs.isEmpty()) {
			objs.forEach(obj -> {
				LogoResponseDto logoRespDto = new LogoResponseDto();
				logoRespDto.setEntityId(obj[0] != null
						? new Long(String.valueOf(obj[0])) : null);
				String logoFile = obj[1] != null ? String.valueOf(obj[1])
						: null;
				String logoFile1 = null;
				if (logoFile != null) {
					byte[] bytes = logoFile.getBytes();
					logoFile1 = Base64.getEncoder().encodeToString(bytes);
				}
				logoRespDto.setLogoFile(logoFile1);
				logoRespDto.setLogoType(
						obj[2] != null ? String.valueOf(obj[2]) : null);
				logoRespDtos.add(logoRespDto);
			});

		}
		if (!logoRespDtos.isEmpty()) {
			Map<Long, List<LogoResponseDto>> mapLogoResponseDto = mapLogoResponseDto(
					logoRespDtos);

			entitInfoEntities.forEach(entitInfoEntity -> {
				List<LogoResponseDto> responseDtos = mapLogoResponseDto
						.get(entitInfoEntity.getId());
				LogoResponseDto responseDto = new LogoResponseDto();
				String logoFile = null;
				String logoType = null;
				if (responseDtos != null && !responseDtos.isEmpty()) {
					LogoResponseDto respDto = responseDtos.get(0);
					logoType = respDto.getLogoType();
					logoFile = respDto.getLogoFile();
				}
				responseDto.setEntityId(entitInfoEntity.getId());
				responseDto.setEntityName(entitInfoEntity.getEntityName());
				responseDto.setLogoFile(logoFile);
				responseDto.setLogoType(logoType);
				respDtos.add(responseDto);
			});
		}
		return respDtos;
	}

	private Map<Long, List<LogoResponseDto>> mapLogoResponseDto(
			List<LogoResponseDto> respDtos) {

		Map<Long, List<LogoResponseDto>> mapLogoRespDto = new HashMap<>();

		respDtos.forEach(respDto -> {
			if (mapLogoRespDto.containsKey(respDto.getEntityId())) {
				List<LogoResponseDto> responseDtos = mapLogoRespDto
						.get(respDto.getEntityId());
				responseDtos.add(respDto);
				mapLogoRespDto.put(respDto.getEntityId(), responseDtos);
			} else {
				List<LogoResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(respDto);
				mapLogoRespDto.put(respDto.getEntityId(), responseDtos);
			}
		});
		return mapLogoRespDto;
	}
}
