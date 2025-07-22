package com.ey.advisory.admin.services.onboarding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.ELEntitlementRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.MasterFunctionalityRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.ELExtractRegDto;
import com.ey.advisory.core.dto.ELExtractRegItemsDetailsDto;
import com.ey.advisory.core.dto.ElRegistrationReqDto;
import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.repositories.client.GstnAPIGstinConfigRepository;

/**
 * 
 * @author Umesha.M
 *
 */
@Component("eLRegistrationServiceImpl")
public class ELRegistrationServiceImpl implements ELRegistrationService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ELRegistrationServiceImpl.class);

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	@Autowired
	@Qualifier("elEntitlementRepository")
	private ELEntitlementRepository elEntitlementRepository;

	@Autowired
	@Qualifier("masterFunctionalityRepository")
	private MasterFunctionalityRepository masterFunctionalityRepository;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	GroupInfoDetailsRepository groupInfoDetailsRepository;
	
	@Autowired
	@Qualifier("GstnAPIGstinConfigRepository")
	private GstnAPIGstinConfigRepository gstnAPIGstinConfigRepo;

	/*
	 * This method used to Registration details.
	 * 
	 * @see com.ey.advisory.admin.services.onboarding.ELRegistrationService#
	 * getRegistrationDetails(com.ey.advisory.core.dto.ElRegistrationReqDto)
	 */
	@Override
	public List<ELExtractRegDto> getRegistrationDetails(
			ElRegistrationReqDto dto) {

		return parseGstnDetailsEntityToGstnDetailsDto(dto);
	}

	private List<ELExtractRegDto> parseGstnDetailsEntityToGstnDetailsDto(
			ElRegistrationReqDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ELRegistrationServiceImpl parseGstnDetailsEntityToGstnDetailsDto Begin");
		}
		List<EntityInfoEntity> entityInfoEntities = entityInfoDetailsRepository
				.findEntityInfoDetails(dto.getGroupCode());

		List<ELExtractRegDto> elExtractRegDtos = new ArrayList<>();
		if (!entityInfoEntities.isEmpty()) {
			entityInfoEntities.forEach(entityInfoEntity -> {
				ELExtractRegDto regDto = new ELExtractRegDto();
				regDto.setEntityId(entityInfoEntity.getId());
				regDto.setEntityName(entityInfoEntity.getEntityName());
				List<GSTNDetailEntity> gSTNDetailEntities = gstNDetailRepository
						.findByGstnAll(dto.getGroupCode(),
								entityInfoEntity.getId());
				
				List<ELExtractRegItemsDetailsDto> eleRegItemsDeDtos = new ArrayList<>();
				if (!gSTNDetailEntities.isEmpty()) {

					gSTNDetailEntities.forEach(gSTNDetailEntity -> {
						ELExtractRegItemsDetailsDto elexRegItemsDetDto = new ELExtractRegItemsDetailsDto();
						if (gSTNDetailEntity.getId() != null) {
							elexRegItemsDetDto.setId(gSTNDetailEntity.getId());
						}
						if (gSTNDetailEntity.getGstin() != null) {
							elexRegItemsDetDto.setSupplierGstin(
									gSTNDetailEntity.getGstin());
						} else {
							elexRegItemsDetDto.setSupplierGstin("");
						}
						if (gSTNDetailEntity.getRegistrationType() != null) {
							elexRegItemsDetDto.setRegType(
									gSTNDetailEntity.getRegistrationType());
						} else {
							elexRegItemsDetDto.setRegType("");
						}

						if (gSTNDetailEntity.getRegisteredName() != null) {
							elexRegItemsDetDto.setRegisteredName(
									gSTNDetailEntity.getRegisteredName());
						} else {
							elexRegItemsDetDto.setRegisteredName("");
						}
						if (gSTNDetailEntity.getGstnUsername() != null) {
							elexRegItemsDetDto.setGstnUsername(
									gSTNDetailEntity.getGstnUsername());
						} else {
							elexRegItemsDetDto.setGstnUsername("");
						}
						elexRegItemsDetDto.setEffectiveDate(
								gSTNDetailEntity.getRegDate());
						if (gSTNDetailEntity.getRegisteredEmail() != null) {
							elexRegItemsDetDto.setRegEmail(
									gSTNDetailEntity.getRegisteredEmail());
						} else {
							elexRegItemsDetDto.setRegEmail("");
						}

						if (gSTNDetailEntity.getRegisteredMobileNo() != null) {
							elexRegItemsDetDto.setRegMobile(
									gSTNDetailEntity.getRegisteredMobileNo());
						} else {
							elexRegItemsDetDto.setRegMobile("");
						}

						if (gSTNDetailEntity.getPrimaryAuthEmail() != null) {
							elexRegItemsDetDto.setPrimaryAuthEmail(
									gSTNDetailEntity.getPrimaryAuthEmail());
						} else {
							elexRegItemsDetDto.setPrimaryAuthEmail("");
						}

						if (gSTNDetailEntity.getSecondaryAuthEmail() != null) {
							elexRegItemsDetDto.setSecondaryAuthEmail(
									gSTNDetailEntity.getSecondaryAuthEmail());
						} else {
							elexRegItemsDetDto.setSecondaryAuthEmail("");
						}
						if (gSTNDetailEntity.getPrimaryContactEmail() != null) {
							elexRegItemsDetDto.setPrimaryContactEmail(
									gSTNDetailEntity.getPrimaryContactEmail());
						} else {
							elexRegItemsDetDto.setPrimaryContactEmail("");
						}
						if (gSTNDetailEntity
								.getSecondaryContactEmail() != null) {
							elexRegItemsDetDto
									.setSecondaryContactEmail(gSTNDetailEntity
											.getSecondaryContactEmail());
						} else {
							elexRegItemsDetDto.setSecondaryContactEmail("");
						}

						if (gSTNDetailEntity.getBankAccNum() != null) {
							elexRegItemsDetDto.setBankAccNo(
									gSTNDetailEntity.getBankAccNum());
						} else {
							elexRegItemsDetDto.setBankAccNo("");
						}

						if (gSTNDetailEntity.getAddress1() != null) {
							elexRegItemsDetDto.setAddress1(
									gSTNDetailEntity.getAddress1());
						} else {
							elexRegItemsDetDto.setAddress1("");
						}

						if (gSTNDetailEntity.getAddress2() != null) {
							elexRegItemsDetDto.setAddress2(
									gSTNDetailEntity.getAddress2());
						} else {
							elexRegItemsDetDto.setAddress2("");
						}
						if (gSTNDetailEntity.getAddress3() != null) {
							elexRegItemsDetDto.setAddress3(
									gSTNDetailEntity.getAddress3());
						} else {
							elexRegItemsDetDto.setAddress3("");
						}
						elexRegItemsDetDto
								.setTurnover(gSTNDetailEntity.getTurnover());
						eleRegItemsDeDtos.add(elexRegItemsDetDto);
					});
					regDto.setList(eleRegItemsDeDtos);
				}
				elExtractRegDtos.add(regDto);
			});
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ELRegistrationServiceImpl parseGstnDetailsEntityToGstnDetailsDto End");
		}
		return elExtractRegDtos;
	}

	/*
	 * This method used for Update a Gstin info table
	 * 
	 * @see com.ey.advisory.admin.services.onboarding.ELRegistrationService#
	 * updateGstnInfoDetails(java.util.List)
	 */
	@Override
	public String updateGstnInfoDetails(
			List<ElRegistrationReqDto> elRegReqDtos) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ELRegistrationServiceImpl updateGstnInfoDetails Begin");
		}
		String finalMsg = null;
		StringBuilder errorMsg = new StringBuilder();
		List<GSTNDetailEntity> list = new ArrayList<>();
		List<GstnAPIGstinConfig> gstnApiList = new ArrayList<>();
	
	
		elRegReqDtos.forEach(updateGstinInfo -> {

			GSTNDetailEntity gstnDetailEntity = gstNDetailRepository
					.findById(updateGstinInfo.getId()).get();
			
			GstnAPIGstinConfig gstnAPIGstinConfigEntity = gstnAPIGstinConfigRepo
					.findById(updateGstinInfo.getId()).get();

			Long groupId = groupInfoDetailsRepository
					.findByGroupId(updateGstinInfo.getGroupCode());
			gstnDetailEntity.setEntityId(updateGstinInfo.getEntityId());
			gstnDetailEntity.setGroupId(groupId);
			gstnDetailEntity.setGroupCode(updateGstinInfo.getGroupCode());
			gstnDetailEntity.setGstin(updateGstinInfo.getSupplierGstin());
			// Convert Upper case for reg type
			if (null == updateGstinInfo.getRegType()
					&& StringUtils.isEmpty(updateGstinInfo.getRegType())) {
				errorMsg.append("Registration type is empty");
				errorMsg.append(",");
			} else {
				gstnDetailEntity.setRegistrationType(
						updateGstinInfo.getRegType().toUpperCase());
			}

			if (null == updateGstinInfo.getRegisteredName() && StringUtils
					.isEmpty(updateGstinInfo.getRegisteredName())) {
				errorMsg.append("Registered Name is empty");
				errorMsg.append(",");
			} else {
				gstnDetailEntity
						.setRegisteredName(updateGstinInfo.getRegisteredName());
			}
			if (null == updateGstinInfo.getGstnUsername()
					&& StringUtils.isEmpty(updateGstinInfo.getGstnUsername())) {
				errorMsg.append("Gstn User name is empty");
				errorMsg.append(",");
			} else {
				gstnDetailEntity
						.setGstnUsername(updateGstinInfo.getGstnUsername());
			}
			if (null == updateGstinInfo.getEffectiveDate() && StringUtils
					.isEmpty(updateGstinInfo.getEffectiveDate())) {
				errorMsg.append("Effective date is empty");
				errorMsg.append(",");
			} else {
				gstnDetailEntity.setRegDate(updateGstinInfo.getEffectiveDate());
			}
			if (null == updateGstinInfo.getRegEmail()
					&& StringUtils.isEmpty(updateGstinInfo.getRegEmail())) {
				errorMsg.append("Registration Email is empty");
				errorMsg.append(",");
			} else {
				gstnDetailEntity
						.setRegisteredEmail(updateGstinInfo.getRegEmail());
			}
			gstnDetailEntity
					.setRegisteredMobileNo(updateGstinInfo.getRegMobile());

			if (null == updateGstinInfo.getPrimaryAuthEmail() && StringUtils
					.isEmpty(updateGstinInfo.getPrimaryAuthEmail())) {
				errorMsg.append("Primary Auth email is empty");
				errorMsg.append(",");
			} else {
				gstnDetailEntity.setPrimaryAuthEmail(
						updateGstinInfo.getPrimaryAuthEmail());
			}

			if (null != updateGstinInfo.getSecondaryAuthEmail()
					&& updateGstinInfo.getSecondaryAuthEmail()
							.equals(updateGstinInfo.getPrimaryAuthEmail())) {
				errorMsg.append(
						"Secondary Auth email is same as Primary Auth email");
				errorMsg.append(",");
			} else {
				gstnDetailEntity.setSecondaryAuthEmail(
						updateGstinInfo.getSecondaryAuthEmail());
			}

			if (null == updateGstinInfo.getPrimaryContactEmail() && StringUtils
					.isEmpty(updateGstinInfo.getPrimaryContactEmail())) {
				errorMsg.append("Primary Contact email is empty");
				errorMsg.append(",");
			} else {
				gstnDetailEntity.setPrimaryContactEmail(
						updateGstinInfo.getPrimaryContactEmail());
			}
			if (null != updateGstinInfo.getSecondaryContactEmail()
					&& updateGstinInfo.getSecondaryContactEmail()
							.equals(updateGstinInfo.getPrimaryContactEmail())) {
				errorMsg.append(
						"Secondary Contact email should not be same Primary contact email");
			} else {
				gstnDetailEntity.setSecondaryContactEmail(
						updateGstinInfo.getSecondaryContactEmail());
			}

			gstnDetailEntity.setBankAccNum(updateGstinInfo.getBankAccNo());
			gstnDetailEntity.setTurnover(updateGstinInfo.getTurnover());
			gstnDetailEntity.setAddress1(updateGstinInfo.getAddress1());

			gstnDetailEntity.setAddress2(updateGstinInfo.getAddress2());

			gstnDetailEntity.setAddress3(updateGstinInfo.getAddress3());
			gstnDetailEntity.setIsDelete(false);

			// To do Once priyank done we have to use modified by name.
			User user = SecurityContext.getUser();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("User Name: {}", user.getUserPrincipalName());
			}
			gstnDetailEntity.setModifiedBy(user.getUserPrincipalName());
			gstnDetailEntity.setModifiedOn(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			list.add(gstnDetailEntity);
			
			GstnAPIGstinConfig gstnAPIEntityPresent = gstnAPIGstinConfigRepo
					.findByGstin(updateGstinInfo.getSupplierGstin());
			if(gstnAPIEntityPresent!=null){
				
				gstnAPIEntityPresent.setGstinUserName(updateGstinInfo.getGstnUsername());
				gstnAPIEntityPresent.setMobileNo(updateGstinInfo.getRegMobile());
				gstnAPIEntityPresent.setUpdatedDate(
						EYDateUtil.toDate(LocalDateTime.now()));
				gstnAPIEntityPresent.setEmailId(updateGstinInfo.getRegEmail());
				
				gstnApiList.add(gstnAPIEntityPresent);

				
			}else{
				GstnAPIGstinConfig gstnAPIGstinConfgEntity = new GstnAPIGstinConfig();
				
				gstnAPIGstinConfgEntity.setGstin(updateGstinInfo.getSupplierGstin());
				gstnAPIGstinConfgEntity.setGroupCode(updateGstinInfo.getGroupCode());
				gstnAPIGstinConfgEntity.setGstinUserName(updateGstinInfo.getGstnUsername());
				gstnAPIGstinConfgEntity.setMobileNo(updateGstinInfo.getRegMobile());
				gstnAPIGstinConfgEntity.setCreatedDate(
						EYDateUtil.toDate(LocalDateTime.now()));
				gstnAPIGstinConfgEntity.setUpdatedDate(
						EYDateUtil.toDate(LocalDateTime.now()));
				gstnAPIGstinConfgEntity.setEmailId(updateGstinInfo.getRegEmail());
				
				gstnApiList.add(gstnAPIGstinConfgEntity);
			}
			
		});

		if (errorMsg.toString().trim().isEmpty()) {
			gstNDetailRepository.saveAll(list);
		} else {
			finalMsg = errorMsg.toString();
		}
		gstnAPIGstinConfigRepo.saveAll(gstnApiList);

		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ELRegistrationServiceImpl updateGstnInfoDetails End");
		}
		return finalMsg;
	}

	@Override
	public void deleteGstnInfoDetails(
			final List<ElRegistrationReqDto> elRegistrationReqDtos) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ELRegistrationServiceImpl deleteGstnInfoDetails Begin");
		}
		if (!elRegistrationReqDtos.isEmpty()) {
			elRegistrationReqDtos.forEach(elRegistrationReqDto -> {
				if (elRegistrationReqDto.getId() != null
						&& elRegistrationReqDto.getId() > 0) {
					gstNDetailRepository
							.deleterecord(elRegistrationReqDto.getId());
				}
			});

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ELRegistrationServiceImpl deleteGstnInfoDetails End");
		}
	}
}
