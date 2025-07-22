package com.ey.advisory.admin.services.onboarding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.MakerCheckerWorkFlowEntity;
import com.ey.advisory.admin.data.entities.client.MakerCheckerWorkflowEntityMappingEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.MakerCheckerWorkFlowRepository;
import com.ey.advisory.admin.data.repositories.client.MakerCheckerWorkflowEntityMappingRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.MakerCheWorkFlowFinalRespDto;
import com.ey.advisory.core.dto.MakerCheWorkFlowRespDto;
import com.ey.advisory.core.dto.MakerCheckerReqDto;
import com.ey.advisory.core.dto.MakerCheckerWorkflowRespDto;
import com.ey.advisory.gstnapi.domain.master.MakerCheckerWorkflowMaster;
import com.ey.advisory.gstnapi.repositories.client.MakerCheckerWorkflowMasterRepository;

import lombok.extern.slf4j.Slf4j;

@Service("MakerCheckerWorkflowServiceImpl")
@Slf4j
public class MakerCheckerWorkflowServiceImpl
		implements MakerCheckerWorkflowService {

	@Autowired
	@Qualifier("MakerCheckerWorkflowEntityMappingRepository")
	private MakerCheckerWorkflowEntityMappingRepository mkrCkrWrkflowEntRepo;

	@Autowired
	@Qualifier("MakerCheckerWorkflowMasterRepository")
	private MakerCheckerWorkflowMasterRepository makerCheckerWorkflowMasterRepo;

	@Autowired
	@Qualifier("MakerCheckerWorkFlowRepository")
	private MakerCheckerWorkFlowRepository mkrCkrWorkflowRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepo;

	public List<MakerCheckerWorkflowRespDto> getMakerCheckerWorkflowDetails(
			Long entityId) {

		List<MakerCheckerWorkflowRespDto> respDtos = new ArrayList<>();
		List<MakerCheckerWorkflowMaster> mkrckrWrkfMasterList = makerCheckerWorkflowMasterRepo
				.findByIsDeleteFalse();
		respDtos = convertMkrCkrToResp(mkrckrWrkfMasterList, entityId);

		return respDtos;

	}

	private List<MakerCheckerWorkflowRespDto> convertMkrCkrToResp(
			List<MakerCheckerWorkflowMaster> checkWorkflowMasters,
			Long entityId) {
		List<MakerCheckerWorkflowRespDto> respDtos = new ArrayList<>();
		checkWorkflowMasters.forEach(mkrckrWrkfMaster -> {
			MakerCheckerWorkflowRespDto respDto = new MakerCheckerWorkflowRespDto();
			MakerCheckerWorkflowEntityMappingEntity mkrCkrWrkflowEnti = mkrCkrWrkflowEntRepo
					.findByEntityIdAndWorkflowTypeAndIsDeleteFalse(entityId,
							mkrckrWrkfMaster.getWorkflowType());
			if (mkrCkrWrkflowEnti != null) {
				respDto.setId(mkrCkrWrkflowEnti.getId());
				respDto.setAction(mkrCkrWrkflowEnti.isAction());
			} else {
				respDto.setAction(false);
			}
			respDto.setEntityId(entityId);
			respDto.setWorkFlowType(mkrckrWrkfMaster.getWorkflowType());
			respDtos.add(respDto);
		});
		return respDtos;
	}

	public void updateMkrCkrMapEntity(List<MakerCheckerReqDto> reqDtos) {
		List<MakerCheckerWorkflowEntityMappingEntity> mkrCkrWrkFlwEntMapEntities = new ArrayList<>();
		reqDtos.forEach(reqDto -> {
			MakerCheckerWorkflowEntityMappingEntity mkrCkrWrkFlwEntMapEntity = new MakerCheckerWorkflowEntityMappingEntity();
			mkrCkrWrkFlwEntMapEntity.setId(reqDto.getId());
			mkrCkrWrkFlwEntMapEntity.setAction(reqDto.isAction());
			mkrCkrWrkFlwEntMapEntity.setEntityId(reqDto.getEntityId());
			mkrCkrWrkFlwEntMapEntity.setWorkflowType(reqDto.getWorkFlowType());
			mkrCkrWrkFlwEntMapEntities.add(mkrCkrWrkFlwEntMapEntity);
		});
		if (mkrCkrWrkFlwEntMapEntities != null
				&& !mkrCkrWrkFlwEntMapEntities.isEmpty()) {
			mkrCkrWrkflowEntRepo.saveAll(mkrCkrWrkFlwEntMapEntities);
		}
	}

	public MakerCheWorkFlowFinalRespDto getMkrCkrWorkFlow(Long entityId) {

		MakerCheWorkFlowFinalRespDto mkrCkrWrkFlowFinRespDto = new MakerCheWorkFlowFinalRespDto();

		List<String> gstins = gstnDetailRepo.findgstinByEntityId(entityId);
		mkrCkrWrkFlowFinRespDto.setGstins(gstins);

		List<MakerCheWorkFlowRespDto> mkrCkrWorkFlowDtos = new ArrayList<>();
		List<MakerCheckerWorkFlowEntity> mkrCkrWorkflowEntities = mkrCkrWorkflowRepo
				.findByEntityIdAndIsDeleteFalse(entityId);

		mkrCkrWorkflowEntities.forEach(mkrCkrWorkflowEntity -> {
			MakerCheWorkFlowRespDto mkrCkrWrkFlowRespDto = new MakerCheWorkFlowRespDto();
			mkrCkrWrkFlowRespDto.setGstin(mkrCkrWorkflowEntity.getGstin());
			if (mkrCkrWorkflowEntity.getMakerCheckerType() != null
					&& "C".equalsIgnoreCase(
							mkrCkrWorkflowEntity.getMakerCheckerType())) {
				mkrCkrWrkFlowRespDto
						.setCkrUsrId(mkrCkrWorkflowEntity.getMakerCheckerId());
				mkrCkrWrkFlowRespDto.setCheckNotfTo(
						mkrCkrWorkflowEntity.getCheckerNotificationTo());
			} else if (mkrCkrWorkflowEntity.getMakerCheckerType() != null
					&& "M".equalsIgnoreCase(
							mkrCkrWorkflowEntity.getMakerCheckerType())) {
				mkrCkrWrkFlowRespDto
						.setMkrUsrId(mkrCkrWorkflowEntity.getMakerCheckerId());
			}
			mkrCkrWrkFlowRespDto
					.setRetType(mkrCkrWorkflowEntity.getReturnType());
			mkrCkrWrkFlowRespDto
					.setWorkFlowType(mkrCkrWorkflowEntity.getWorkFlowType());
			mkrCkrWorkFlowDtos.add(mkrCkrWrkFlowRespDto);
		});
		mkrCkrWrkFlowFinRespDto.setMkrCkrRespDtos(mkrCkrWorkFlowDtos);
		return mkrCkrWrkFlowFinRespDto;
	}

	public void updateMkrCkrWorkFlow(List<MakerCheckerReqDto> reqDtos) {
		List<MakerCheckerWorkFlowEntity> mkrckrWrkFlowEntities = new ArrayList<>();
		User user = SecurityContext.getUser();
		reqDtos.forEach(reqDto -> {
			if (reqDto.getMkrUsrId() != null) {
				MakerCheckerWorkFlowEntity mkrckrWrkFlowEntity = new MakerCheckerWorkFlowEntity();
				mkrckrWrkFlowEntity.setEntityId(reqDto.getEntityId());
				mkrckrWrkFlowEntity.setGstin(reqDto.getGstin());
				mkrckrWrkFlowEntity.setWorkFlowType(reqDto.getWorkFlowType());
				mkrckrWrkFlowEntity.setMakerCheckerId(reqDto.getMkrUsrId());
				mkrckrWrkFlowEntity.setMakerCheckerType("M");
				mkrckrWrkFlowEntity.setReturnType(reqDto.getRetType());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User Name: {}", user.getUserPrincipalName());
				}
				mkrckrWrkFlowEntity.setCreatedBy(user.getUserPrincipalName());
				mkrckrWrkFlowEntity.setCreatedOn(
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				mkrckrWrkFlowEntities.add(mkrckrWrkFlowEntity);
				mkrCkrWorkflowRepo.updateCheckersByGstinAndWorkFlowType(
						reqDto.getEntityId(), reqDto.getGstin(),
						reqDto.getWorkFlowType(), reqDto.getMkrUsrId(), "M");
			}
			if (reqDto.getCkrUsrId() != null) {
				MakerCheckerWorkFlowEntity mkrckrWrkFlowEntity = new MakerCheckerWorkFlowEntity();
				mkrckrWrkFlowEntity
						.setCheckerNotificationTo(reqDto.getCheckNotfTo());
				mkrckrWrkFlowEntity.setEntityId(reqDto.getEntityId());
				mkrckrWrkFlowEntity.setGstin(reqDto.getGstin());
				mkrckrWrkFlowEntity.setWorkFlowType(reqDto.getWorkFlowType());
				mkrckrWrkFlowEntity.setMakerCheckerId(reqDto.getCkrUsrId());
				mkrckrWrkFlowEntity.setMakerCheckerType("C");
				mkrckrWrkFlowEntity.setReturnType(reqDto.getRetType());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User Name: {}", user.getUserPrincipalName());
				}
				mkrckrWrkFlowEntity.setCreatedBy(user.getUserPrincipalName());
				mkrckrWrkFlowEntity.setCreatedOn(
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				
				mkrckrWrkFlowEntities.add(mkrckrWrkFlowEntity);
				mkrCkrWorkflowRepo.updateCheckersByGstinAndWorkFlowType(
						reqDto.getEntityId(), reqDto.getGstin(),
						reqDto.getWorkFlowType(), reqDto.getCkrUsrId(), "C");
			}
		});
		if (mkrckrWrkFlowEntities != null) {
			mkrCkrWorkflowRepo.saveAll(mkrckrWrkFlowEntities);
		}
	}
}
