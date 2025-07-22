package com.ey.advisory.app.serviceoption;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.ey.advisory.admin.data.entities.client.EntityAtValueEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityAtValueRepository;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.entities.client.SourceInfoEntity;
import com.ey.advisory.app.data.repositories.client.SourceInfoRepository;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.GstinDto;
import com.ey.advisory.core.dto.ServiceOptItemDto;
import com.ey.advisory.core.dto.ServiceOptionDto;
import com.ey.advisory.core.dto.ServiceOptionReqDto;
import com.ey.advisory.gstnapi.domain.master.SourceMasterEntity;
import com.ey.advisory.gstnapi.repositories.master.SourceMasterRepository;

@Service("ServiceOptionServiceImpl")
public class ServiceOptionServiceImpl implements ServiceOptionService {

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("entityAtValueRepository")
	private EntityAtValueRepository entityAtValueRepository;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository confPrmtRepo;

	@Autowired
	@Qualifier("SourceMasterRepository")
	private SourceMasterRepository sourcMasterRepo;

	@Autowired
	@Qualifier("SourceInfoRepository")
	private SourceInfoRepository infoRep;

	public List<ServiceOptionDto> getServiceOption(
			final ServiceOptionReqDto servOptReqDto) {
		List<ServiceOptionDto> optDtos = new ArrayList<>();

		List<EntityInfoEntity> entityInfoEntities = entityInfoDetailsRepository
				.findEntityInfoDetails(servOptReqDto.getGroupCode());

		for (EntityInfoEntity entityInfoEntity : entityInfoEntities) {
			ServiceOptionDto opDto = new ServiceOptionDto();
			opDto.setEntityName(entityInfoEntity.getEntityName());
			opDto.setEntityId(entityInfoEntity.getId());
			String eInvoiceAnswer = confPrmtRepo
					.findByEntityServiceOptionsEIvoice(
							entityInfoEntity.getId());
			if ("A".equalsIgnoreCase(eInvoiceAnswer)) {
				opDto.setEinvFlag(true);
			} else if ("B".equalsIgnoreCase(eInvoiceAnswer)) {
				opDto.setEinvFlag(false);
			}

			String ewbAnswer = confPrmtRepo
					.findByEntityServiceOptionsForEwb(entityInfoEntity.getId());

			if ("A".equalsIgnoreCase(ewbAnswer)) {
				opDto.setEwbFlag(true);
			} else if ("B".equalsIgnoreCase(ewbAnswer)) {
				opDto.setEwbFlag(false);
			}

			List<EntityAtValueEntity> entityAtValueEntities = entityAtValueRepository
					.getAllEntityAtValueEntity(entityInfoEntity.getGroupCode(),
							entityInfoEntity.getId());
			List<GstinDto> gstinDtos = new ArrayList<>();
			List<GstinDto> plantDtos = new ArrayList<>();
			List<GstinDto> ewbDtos = new ArrayList<>();
			List<GstinDto> einvDtos = new ArrayList<>();

			if (entityAtValueEntities != null
					&& !entityAtValueEntities.isEmpty()) {
				entityAtValueEntities.forEach(entityAtValueEntity -> {
					if (OnboardingConstant.GSTIN.equalsIgnoreCase(
							entityAtValueEntity.getAtCode())) {
						GstinDto gstinDto = new GstinDto();
						gstinDto.setKey(entityAtValueEntity.getId());
						gstinDto.setValue(entityAtValueEntity.getAtValue());
						gstinDtos.add(gstinDto);
					} else if (OnboardingConstant.PLANT.equalsIgnoreCase(
							entityAtValueEntity.getAtCode())) {
						GstinDto palntDto = new GstinDto();
						palntDto.setKey(entityAtValueEntity.getId());
						palntDto.setValue(entityAtValueEntity.getAtValue());
						plantDtos.add(palntDto);
					}
				});
			}
			opDto.setGstinDto(gstinDtos);
			opDto.setPlant(plantDtos);

			List<SourceMasterEntity> sourceMasterEntities = sourcMasterRepo
					.getSourceMaster();
			if (sourceMasterEntities != null
					&& !sourceMasterEntities.isEmpty()) {
				sourceMasterEntities.forEach(sourceMasterEntity -> {
					if ("EWB".equalsIgnoreCase(
							sourceMasterEntity.getServiceType())) {
						GstinDto ewbDto = new GstinDto();
						ewbDto.setKey(sourceMasterEntity.getId());
						ewbDto.setValue(sourceMasterEntity.getJobType());
						ewbDtos.add(ewbDto);
					}
					if ("E_INVOICE".equalsIgnoreCase(
							sourceMasterEntity.getServiceType())) {
						GstinDto einvDto = new GstinDto();
						einvDto.setKey(sourceMasterEntity.getId());
						einvDto.setValue(sourceMasterEntity.getJobType());
						einvDtos.add(einvDto);
					}
				});
			}
			opDto.setEwb(ewbDtos);
			opDto.setEinv(einvDtos);
			List<SourceInfoEntity> sourceInfoEntities = infoRep
					.findGstnBasedOnEntityId(entityInfoEntity.getId());
			List<ServiceOptItemDto> itemDtos = new ArrayList<>();
			if (sourceInfoEntities != null && !sourceInfoEntities.isEmpty()) {
				sourceInfoEntities.forEach(sourceInfoEntity -> {
					ServiceOptItemDto itemDto = new ServiceOptItemDto();
					itemDto.setId(sourceInfoEntity.getId());
					itemDto.setEwb(sourceInfoEntity.getEwbJob());
					itemDto.setEinv(sourceInfoEntity.getEInvJob());
					itemDto.setGstin(sourceInfoEntity.getGstin());
					itemDto.setPlant(sourceInfoEntity.getPlant());
					itemDtos.add(itemDto);
				});
			}
			opDto.setItems(itemDtos);
			optDtos.add(opDto);
		}
		return optDtos;
	}

	@Override
	public void saveServiceOption(List<ServiceOptionReqDto> servOptReqDtos) {
		List<SourceInfoEntity> sourceInfoEntities = new ArrayList<>();
		if (servOptReqDtos != null && !servOptReqDtos.isEmpty()) {
			servOptReqDtos.forEach(servOptReqDto -> {
				SourceInfoEntity sourceInfoEntity = new SourceInfoEntity();
				sourceInfoEntity.setId(servOptReqDto.getId());
				//sourceInfoEntity.setGstin(servOptReqDto.getGstin());
				//sourceInfoEntity.setPlant(servOptReqDto.getPlant());
				String gstin = servOptReqDto.getGstin() != null
                        && !servOptReqDto.getGstin().trim().isEmpty()
                                ? servOptReqDto.getGstin() : null;
                sourceInfoEntity.setGstin(gstin);
                String plant = servOptReqDto.getPlant() != null
                        && !servOptReqDto.getPlant().trim().isEmpty()
                                ? servOptReqDto.getPlant() : null;
                sourceInfoEntity.setPlant(plant);
				sourceInfoEntity.setEntityId(servOptReqDto.getEntityId());
				Integer einv = servOptReqDto.getEinv() != null
						&& !servOptReqDto.getEinv().trim().isEmpty()
								? new Integer(
										String.valueOf(servOptReqDto.getEinv()))
								: null;
				Integer ewb = servOptReqDto.getEwb() != null
						&& !servOptReqDto.getEwb().trim().isEmpty()
								? new Integer(
										String.valueOf(servOptReqDto.getEwb()))
								: null;
				sourceInfoEntity.setEInvJob(einv);
				sourceInfoEntity.setEwbJob(ewb);
				User user = SecurityContext.getUser();
				sourceInfoEntity.setCreatedBy(user.getUserPrincipalName());
				sourceInfoEntity.setCreatedOn(
						EYDateUtil.toLocalDateTimeFromUTC(LocalDateTime.now()));
				sourceInfoEntities.add(sourceInfoEntity);

			});
			if (!sourceInfoEntities.isEmpty()) {
				infoRep.saveAll(sourceInfoEntities);
			}
		}
	}
}
