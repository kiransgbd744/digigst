package com.ey.advisory.admin.services.onboarding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.DataSecurityEntity;
import com.ey.advisory.admin.data.entities.client.EntityAtConfigEntity;
import com.ey.advisory.admin.data.entities.client.EntityAtValueEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.EntityUserMapping;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.entities.client.UserPermissionsEntity;
import com.ey.advisory.admin.data.repositories.client.DataSecurityRepository;
import com.ey.advisory.admin.data.repositories.client.EntityAtConfiRepository;
import com.ey.advisory.admin.data.repositories.client.EntityAtValueRepository;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.EntityUserMappingRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.admin.data.repositories.client.UserPermissionsRepository;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.AppPermissionDto;
import com.ey.advisory.core.dto.DataPermDetRespDto;
import com.ey.advisory.core.dto.DataPermItemsDetRespDto;
import com.ey.advisory.core.dto.DataPermissionRespDto;
import com.ey.advisory.core.dto.DataSecuriDto;
import com.ey.advisory.core.dto.DataSecurityAttriUserRespDto;
import com.ey.advisory.core.dto.DataSecurityDto;
import com.ey.advisory.core.dto.DataSecurityReqDto;
import com.ey.advisory.core.dto.DataSecurityRespDto;
import com.ey.advisory.core.dto.DistChannelDto;
import com.ey.advisory.core.dto.DivisionDto;
import com.ey.advisory.core.dto.EachUserAttributes;
import com.ey.advisory.core.dto.GSTINDetailDto;
import com.ey.advisory.core.dto.ItemsDto;
import com.ey.advisory.core.dto.LocationDto;
import com.ey.advisory.core.dto.PlantDto;
import com.ey.advisory.core.dto.ProfitCenter2Dto;
import com.ey.advisory.core.dto.ProfitCenterDto;
import com.ey.advisory.core.dto.PurchOrgDto;
import com.ey.advisory.core.dto.SalesOrgDto;
import com.ey.advisory.core.dto.SourceIdDto;
import com.ey.advisory.core.dto.SubDivisionDto;
import com.ey.advisory.core.dto.UserAccess1Dto;
import com.ey.advisory.core.dto.UserAccess2Dto;
import com.ey.advisory.core.dto.UserAccess3Dto;
import com.ey.advisory.core.dto.UserAccess4Dto;
import com.ey.advisory.core.dto.UserAccess5Dto;
import com.ey.advisory.core.dto.UserAccess6Dto;
import com.google.common.collect.ImmutableList;

/**
 * @author Umesha.M
 *
 */
@Component("dataSecurityService")
public class DataSecurityServiceImpl implements DataSecurityService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataSecurityServiceImpl.class);

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("dataSecurityRepository")
	private DataSecurityRepository dataSecRepository;

	@Autowired
	@Qualifier("entityAtValueRepository")
	private EntityAtValueRepository entityAtValueRepository;

	@Autowired
	@Qualifier("entityAtConfiRepository")
	private EntityAtConfiRepository entityAtConfiRepository;

	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository userCreationRepository;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("EntityUserMappingRepository")
	private EntityUserMappingRepository entityUserMappingRepository;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entConPrmtRepo;

	@Autowired
	@Qualifier("UserPermissionsRepository")
	private UserPermissionsRepository userPermissionsRepository;

	private final List<String> REGISTRATION_TYPES = ImmutableList.of("REGULAR",
			"SEZ", "SEZU", "SEZD");

	/**
	 * 
	 */
	@Override
	public List<DataSecurityRespDto> getDataSecurity(
			final DataSecurityReqDto dataSecurityReqDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl getDataSecurity begin");
		}
		List<EntityInfoEntity> entityInfoEntities = entityInfoDetailsRepository
				.findEntityInfoDetails(dataSecurityReqDto.getGroupCode());
		List<DataSecurityRespDto> dataSecurityRespDtos = new ArrayList<>();

		if (!entityInfoEntities.isEmpty()) {
			entityInfoEntities.forEach(entityInfoEntity -> {
				DataSecurityRespDto dataSecurityRespDto = new DataSecurityRespDto();
				dataSecurityRespDto.setEntityId(entityInfoEntity.getId());
				dataSecurityRespDto
						.setEntityName(entityInfoEntity.getEntityName());

				// find the Attribute values From DB
				List<EntityAtValueEntity> entityAtValueEntities = entityAtValueRepository
						.getAllEntityAtValueEntity(
								entityInfoEntity.getGroupCode(),
								entityInfoEntity.getId());
				if (entityAtValueEntities != null) {
					getAllDataSecurity1(entityAtValueEntities,
							dataSecurityRespDto);
					getAllDataSecurity2(entityAtValueEntities,
							dataSecurityRespDto);
				}

				Pair<ItemsDto, ItemsDto> itemPair = getEntityAtConfigAttributes(
						entityInfoEntity);
				dataSecurityRespDto.setItems(itemPair.getValue0());
				dataSecurityRespDtos.add(dataSecurityRespDto);
			});
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl getDataSecurity End");
		}
		return dataSecurityRespDtos;
	}

	private Pair<ItemsDto, ItemsDto> getEntityAtConfigAttributes(
			EntityInfoEntity entityInfoEntity) {
		List<EntityAtConfigEntity> listEntityAtConfig = entityAtConfiRepository
				.findAllEntityAtConfigEntity(entityInfoEntity.getGroupCode(),
						entityInfoEntity.getId());
		ItemsDto outwardItems = new ItemsDto();
		ItemsDto inwardItems = new ItemsDto();
		listEntityAtConfig.forEach(entityAtConfig -> {
			if (OnboardingConstant.PC
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setProfitCenter(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setProfitCenter(true);
			} else if (OnboardingConstant.PC2
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setProfitCenter2(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setProfitCenter2(true);
			} else if (OnboardingConstant.PLANT
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setPlant(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setPlant(true);
			} else if (OnboardingConstant.DIVISION
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setDivision(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setDivision(true);
			} else if (OnboardingConstant.SUB_DIVISION
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setSubDivision(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setSubDivision(true);
			} else if (OnboardingConstant.SO
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setSalesOrg(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setSalesOrg(true);
			} else if (OnboardingConstant.PO
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setPurchOrg(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setPurchOrg(true);
			} else if (OnboardingConstant.DC
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setDistChannel(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setDistChannel(true);
			} else if (OnboardingConstant.LOCATION
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setLocation(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setLocation(true);
			} else if (OnboardingConstant.UD1
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setUserAccess1(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setUserAccess1(true);
			} else if (OnboardingConstant.UD2
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setUserAccess2(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setUserAccess2(true);
			} else if (OnboardingConstant.UD3
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setUserAccess3(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setUserAccess3(true);
			} else if (OnboardingConstant.UD4
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setUserAccess4(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setUserAccess4(true);
			} else if (OnboardingConstant.UD5
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setUserAccess5(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setUserAccess5(true);
			} else if (OnboardingConstant.UD6
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setUserAccess6(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setUserAccess6(true);
			} else if (OnboardingConstant.SI
					.equalsIgnoreCase(entityAtConfig.getAtCode())) {
				if ("M".equals(entityAtConfig.getAtOutward()))
					outwardItems.setSourceId(true);
				if ("M".equals(entityAtConfig.getAtInward()))
					inwardItems.setSourceId(true);
			}
		});
		return new Pair<>(outwardItems, inwardItems);
	}

	private void getAllDataSecurity1(
			List<EntityAtValueEntity> entityAtValueEntities,
			DataSecurityRespDto dataSecurityRespDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl getAllDataSecurity1 begin");
		}
		List<ProfitCenterDto> profitCenterDtos = new ArrayList<>();
		List<ProfitCenter2Dto> profitCenter2Dtos = new ArrayList<>();
		List<PlantDto> plantDtos = new ArrayList<>();
		List<DivisionDto> divisionDtos = new ArrayList<>();
		List<SubDivisionDto> subDivisionDtos = new ArrayList<>();
		List<GSTINDetailDto> gstiNDetailDtos = new ArrayList<>();
		List<SalesOrgDto> salesOrgDtos = new ArrayList<>();
		List<PurchOrgDto> purchOrgDtos = new ArrayList<>();
		List<DistChannelDto> distChannelDtos = new ArrayList<>();
		List<LocationDto> locationDtos = new ArrayList<>();
		entityAtValueEntities.forEach(entityAtValueEntity -> {

			if (OnboardingConstant.GSTIN
					.equalsIgnoreCase(entityAtValueEntity.getAtCode())) {
				GSTINDetailDto gstINDetailDto = new GSTINDetailDto();
				gstINDetailDto.setId(entityAtValueEntity.getId());
				gstINDetailDto
						.setSupplierGstin(entityAtValueEntity.getAtValue());
				gstiNDetailDtos.add(gstINDetailDto);
			} else if (OnboardingConstant.PC
					.equalsIgnoreCase(entityAtValueEntity.getAtCode())) {
				ProfitCenterDto profitCenterDto = new ProfitCenterDto();
				profitCenterDto.setId(entityAtValueEntity.getId());
				profitCenterDto
						.setProfitCenter(entityAtValueEntity.getAtValue());
				profitCenterDtos.add(profitCenterDto);
			} else if (OnboardingConstant.PC2
					.equalsIgnoreCase(entityAtValueEntity.getAtCode())) {
				ProfitCenter2Dto profitCenter2Dto = new ProfitCenter2Dto();
				profitCenter2Dto.setId(entityAtValueEntity.getId());
				profitCenter2Dto
						.setProfitCenter2(entityAtValueEntity.getAtValue());
				profitCenter2Dtos.add(profitCenter2Dto);
			} else if (OnboardingConstant.PLANT
					.equalsIgnoreCase(entityAtValueEntity.getAtCode())) {
				PlantDto plantDto = new PlantDto();
				plantDto.setId(entityAtValueEntity.getId());
				plantDto.setPlant(entityAtValueEntity.getAtValue());
				plantDtos.add(plantDto);
			} else if (OnboardingConstant.DIVISION
					.equalsIgnoreCase(entityAtValueEntity.getAtCode())) {
				DivisionDto divisionDto = new DivisionDto();
				divisionDto.setId(entityAtValueEntity.getId());
				divisionDto.setDivision(entityAtValueEntity.getAtValue());
				divisionDtos.add(divisionDto);
			} else if (OnboardingConstant.SUB_DIVISION
					.equalsIgnoreCase(entityAtValueEntity.getAtCode())) {
				SubDivisionDto subDivisionDto = new SubDivisionDto();
				subDivisionDto.setId(entityAtValueEntity.getId());
				subDivisionDto.setSubDivision(entityAtValueEntity.getAtValue());
				subDivisionDtos.add(subDivisionDto);
			} else if (OnboardingConstant.SO
					.equalsIgnoreCase(entityAtValueEntity.getAtCode())) {
				SalesOrgDto salesOrgDto = new SalesOrgDto();
				salesOrgDto.setId(entityAtValueEntity.getId());
				salesOrgDto.setSalesOrg(entityAtValueEntity.getAtValue());
				salesOrgDtos.add(salesOrgDto);
			} else if (OnboardingConstant.PO
					.equalsIgnoreCase(entityAtValueEntity.getAtCode())) {
				PurchOrgDto purchOrgDto = new PurchOrgDto();
				purchOrgDto.setId(entityAtValueEntity.getId());
				purchOrgDto.setPurchOrg(entityAtValueEntity.getAtValue());
				purchOrgDtos.add(purchOrgDto);
			} else if (OnboardingConstant.DC
					.equalsIgnoreCase(entityAtValueEntity.getAtCode())) {
				DistChannelDto distChannelDto = new DistChannelDto();
				distChannelDto.setId(entityAtValueEntity.getId());
				distChannelDto.setDistChannel(entityAtValueEntity.getAtValue());
				distChannelDtos.add(distChannelDto);
			} else if (OnboardingConstant.LOCATION
					.equalsIgnoreCase(entityAtValueEntity.getAtCode())) {
				LocationDto locationDto = new LocationDto();
				locationDto.setId(entityAtValueEntity.getId());
				locationDto.setLocation(entityAtValueEntity.getAtValue());
				locationDtos.add(locationDto);
			}
			dataSecurityRespDto.setGstiNDetailDto(gstiNDetailDtos);
			dataSecurityRespDto.setPlant(plantDtos);
			dataSecurityRespDto.setProfitCenter(profitCenterDtos);
			dataSecurityRespDto.setProfitCenter2(profitCenter2Dtos);
			dataSecurityRespDto.setDivision(divisionDtos);
			dataSecurityRespDto.setSubDivision(subDivisionDtos);
			dataSecurityRespDto.setSalesOrg(salesOrgDtos);
			dataSecurityRespDto.setPurchOrg(purchOrgDtos);
			dataSecurityRespDto.setDistChannel(distChannelDtos);
			dataSecurityRespDto.setLocation(locationDtos);
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl getAllDataSecurity1 End");
		}
	}

	private void getAllDataSecurity2(
			List<EntityAtValueEntity> entityAtValueEntities,
			DataSecurityRespDto dataSecurityRespDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl getAllDataSecurity2 begin");
		}
		List<UserAccess1Dto> userAccess1Dtos = new ArrayList<>();
		List<UserAccess2Dto> userAccess2Dtos = new ArrayList<>();
		List<UserAccess3Dto> userAccess3Dtos = new ArrayList<>();
		List<UserAccess4Dto> userAccess4Dtos = new ArrayList<>();
		List<UserAccess5Dto> userAccess5Dtos = new ArrayList<>();
		List<UserAccess6Dto> userAccess6Dtos = new ArrayList<>();
		List<SourceIdDto> sourceIdDtos = new ArrayList<>();
		entityAtValueEntities.forEach(entityAtValueEntity -> {
			if (entityAtValueEntity.getAtCode() != null
					&& OnboardingConstant.UD1.equalsIgnoreCase(
							entityAtValueEntity.getAtCode())) {
				UserAccess1Dto userAccess1Dto = new UserAccess1Dto();
				userAccess1Dto.setId(entityAtValueEntity.getId());
				userAccess1Dto.setUserAccess1(entityAtValueEntity.getAtValue());
				userAccess1Dtos.add(userAccess1Dto);
			} else if (entityAtValueEntity.getAtCode() != null
					&& OnboardingConstant.UD2.equalsIgnoreCase(
							entityAtValueEntity.getAtCode())) {
				UserAccess2Dto userAccess2Dto = new UserAccess2Dto();
				userAccess2Dto.setId(entityAtValueEntity.getId());
				userAccess2Dto.setUserAccess2(entityAtValueEntity.getAtValue());
				userAccess2Dtos.add(userAccess2Dto);
			} else if (entityAtValueEntity.getAtCode() != null
					&& OnboardingConstant.UD3.equalsIgnoreCase(
							entityAtValueEntity.getAtCode())) {
				UserAccess3Dto userAccess3Dto = new UserAccess3Dto();
				userAccess3Dto.setId(entityAtValueEntity.getId());
				userAccess3Dto.setUserAccess3(entityAtValueEntity.getAtValue());
				userAccess3Dtos.add(userAccess3Dto);
			} else if (entityAtValueEntity.getAtCode() != null
					&& OnboardingConstant.UD4.equalsIgnoreCase(
							entityAtValueEntity.getAtCode())) {
				UserAccess4Dto userAccess4Dto = new UserAccess4Dto();
				userAccess4Dto.setId(entityAtValueEntity.getId());
				userAccess4Dto.setUserAccess4(entityAtValueEntity.getAtValue());
				userAccess4Dtos.add(userAccess4Dto);
			} else if (entityAtValueEntity.getAtCode() != null
					&& OnboardingConstant.UD5.equalsIgnoreCase(
							entityAtValueEntity.getAtCode())) {
				UserAccess5Dto userAccess5Dto = new UserAccess5Dto();
				userAccess5Dto.setId(entityAtValueEntity.getId());
				userAccess5Dto.setUserAccess5(entityAtValueEntity.getAtValue());
				userAccess5Dtos.add(userAccess5Dto);
			} else if (entityAtValueEntity.getAtCode() != null
					&& OnboardingConstant.UD6.equalsIgnoreCase(
							entityAtValueEntity.getAtCode())) {
				UserAccess6Dto userAccess6Dto = new UserAccess6Dto();
				userAccess6Dto.setId(entityAtValueEntity.getId());
				userAccess6Dto.setUserAccess6(entityAtValueEntity.getAtValue());
				userAccess6Dtos.add(userAccess6Dto);
			} else if (entityAtValueEntity.getAtCode() != null
					&& OnboardingConstant.SI.equalsIgnoreCase(
							entityAtValueEntity.getAtCode())) {
				SourceIdDto sourceIdDto = new SourceIdDto();
				sourceIdDto.setId(entityAtValueEntity.getId());
				sourceIdDto.setSourceId(entityAtValueEntity.getAtValue());
				sourceIdDtos.add(sourceIdDto);
			}
		});
		dataSecurityRespDto.setUserAccess1(userAccess1Dtos);
		dataSecurityRespDto.setUserAccess2(userAccess2Dtos);
		dataSecurityRespDto.setUserAccess3(userAccess3Dtos);
		dataSecurityRespDto.setUserAccess4(userAccess4Dtos);
		dataSecurityRespDto.setUserAccess5(userAccess5Dtos);
		dataSecurityRespDto.setUserAccess6(userAccess6Dtos);
		dataSecurityRespDto.setSourceId(sourceIdDtos);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl getAllDataSecurity2 End");
		}
	}

	@Override
	public List<DataPermissionRespDto> getDataPermission(
			final DataSecurityReqDto dataSecurityReqDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl getDataPermission begin");
		}
		List<EntityInfoEntity> entityInfoEntities = entityInfoDetailsRepository
				.findEntityInfoDetails(dataSecurityReqDto.getGroupCode());
		List<Long> entities = new ArrayList<>();
		entityInfoEntities.forEach(entityInfoEntity -> {
			entities.add(entityInfoEntity.getId());
		});

		List<Object[]> objs = dataSecRepository
				.getDataSecurDetByUserIdsAndAttValues(entities);

		List<DataPermDetRespDto> permDetRespDtos = new ArrayList<>();

		objs.forEach(obj -> {
			DataPermDetRespDto permDetRespDto = new DataPermDetRespDto();
			permDetRespDto.setEntityId(obj[0] != null ? (Long) obj[0] : null);
			permDetRespDto.setId(obj[1] != null ? (Long) obj[1] : null);
			permDetRespDto.setUserName(obj[2] != null ? (String) obj[2] : null);
			permDetRespDto.setAtCode(obj[3] != null ? (String) obj[3] : null);
			permDetRespDto.setAttId(obj[4] != null ? (Long) obj[4] : null);
			permDetRespDto.setEmail(obj[5] != null ? (String) obj[5] : null);
			permDetRespDtos.add(permDetRespDto);
		});
		Map<Long, List<DataPermDetRespDto>> mapDataPermDetResp = mapDataPermDetRespEntity(
				permDetRespDtos);

		List<DataPermissionRespDto> dataPermissionRespDtos = new ArrayList<>();
		if (entityInfoEntities != null) {
			entityInfoEntities.forEach(entityInfoEntity -> {
				DataPermissionRespDto dataPermissionRespDto = new DataPermissionRespDto();
				dataPermissionRespDto.setEntityId(entityInfoEntity.getId());
				dataPermissionRespDto
						.setEntityName(entityInfoEntity.getEntityName());
				List<DataPermDetRespDto> permDetEntDtos = mapDataPermDetResp
						.get(entityInfoEntity.getId());

				Map<String, List<DataPermDetRespDto>> dataPermUserInfoMap = mapDataPermDetRespUserInfo(
						permDetEntDtos);
				if (dataPermUserInfoMap != null
						&& !dataPermUserInfoMap.isEmpty()) {
					List<DataPermItemsDetRespDto> dataPermItemsDetRespDtos = new ArrayList<>();
					dataPermUserInfoMap.keySet()
							.forEach(dataPermUserInfoKey -> {
								List<DataPermDetRespDto> dataPermDetRespDtos = dataPermUserInfoMap
										.get(dataPermUserInfoKey);
								DataPermItemsDetRespDto respDto = new DataPermItemsDetRespDto();
								if (dataPermDetRespDtos != null
										&& !dataPermDetRespDtos.isEmpty()) {
									dataPermDetRespDtos
											.forEach(dataPermDetRespDto -> {

												respDto.setId(dataPermDetRespDto
														.getId());
												respDto.setUserName(
														dataPermDetRespDto
																.getUserName());
												respDto.setEmail(
														dataPermDetRespDto
																.getEmail());
												Map<String, List<Long>> mapDataPermDetRespEntityAtt = mapDataPermDetRespEntityAttr(
														dataPermDetRespDtos);
												List<Long> gstnIds = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.GSTIN);

												List<Long> profitCenterDtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.PC);
												List<Long> profitCenter2Dtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.PC2);
												List<Long> plantDtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.PLANT);
												List<Long> divisionDtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.DIVISION);
												List<Long> subDivisionDtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.SUB_DIVISION);
												List<Long> salesOrgDtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.SO);
												List<Long> purchOrgDtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.PO);
												List<Long> distChannelDtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.DC);

												List<Long> locations = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.LOCATION);
												List<Long> userAccess1Dtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.UD1);
												List<Long> userAccess2Dtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.UD2);
												List<Long> userAccess3Dtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.UD3);
												List<Long> userAccess4Dtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.UD4);
												List<Long> userAccess5Dtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.UD5);
												List<Long> userAccess6Dtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.UD6);
												List<Long> sourceIdDtos = mapDataPermDetRespEntityAtt
														.get(OnboardingConstant.SI);

												respDto.setProfitCenter(
														profitCenterDtos);
												respDto.setProfitCenter2(
														profitCenter2Dtos);
												respDto.setPlant(plantDtos);
												respDto.setDivision(
														divisionDtos);
												respDto.setSubDivision(
														subDivisionDtos);
												respDto.setSalesOrg(
														salesOrgDtos);
												respDto.setPurchOrg(
														purchOrgDtos);
												respDto.setDistChannel(
														distChannelDtos);

												respDto.setLocation(locations);
												respDto.setUserAccess1(
														userAccess1Dtos);
												respDto.setUserAccess2(
														userAccess2Dtos);
												respDto.setUserAccess3(
														userAccess3Dtos);
												respDto.setUserAccess4(
														userAccess4Dtos);
												respDto.setUserAccess5(
														userAccess5Dtos);
												respDto.setUserAccess6(
														userAccess6Dtos);
												respDto.setSourceId(
														sourceIdDtos);
												respDto.setGstinId(gstnIds);

											});
								}
								dataPermItemsDetRespDtos.add(respDto);
							});
					dataPermissionRespDto.setItems(dataPermItemsDetRespDtos);
				}
				dataPermissionRespDtos.add(dataPermissionRespDto);
			});
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl getDataPermission End");
		}
		return dataPermissionRespDtos;
	}

	private Map<Long, List<DataPermDetRespDto>> mapDataPermDetRespEntity(
			List<DataPermDetRespDto> respDtos) {
		Map<Long, List<DataPermDetRespDto>> mapDataPermDetResp = new HashMap<>();
		respDtos.forEach(respDto -> {
			StringBuilder key = new StringBuilder();
			key.append(respDto.getEntityId());
			Long docKey = new Long(key.toString());
			if (mapDataPermDetResp.containsKey(docKey)) {
				List<DataPermDetRespDto> permDetDtos = mapDataPermDetResp
						.get(docKey);
				permDetDtos.add(respDto);
				mapDataPermDetResp.put(docKey, permDetDtos);
			} else {
				List<DataPermDetRespDto> permDetDtos = new ArrayList<>();
				permDetDtos.add(respDto);
				mapDataPermDetResp.put(docKey, permDetDtos);
			}
		});

		return mapDataPermDetResp;
	}

	private Map<String, List<Long>> mapDataPermDetRespEntityAttr(
			List<DataPermDetRespDto> respDtos) {
		Map<String, List<Long>> mapDataPermDetResp = new HashMap<>();
		respDtos.forEach(respDto -> {
			StringBuilder key = new StringBuilder();
			key.append(respDto.getAtCode());
			String docKey = key.toString();
			if (mapDataPermDetResp.containsKey(docKey)) {
				List<Long> permDetDtos = mapDataPermDetResp.get(docKey);
				permDetDtos.add(respDto.getAttId());
				mapDataPermDetResp.put(docKey, permDetDtos);
			} else {
				List<Long> permDetDtos = new ArrayList<>();
				permDetDtos.add(respDto.getAttId());
				mapDataPermDetResp.put(docKey, permDetDtos);
			}
		});
		return mapDataPermDetResp;
	}

	private Map<String, List<DataPermDetRespDto>> mapDataPermDetRespUserInfo(
			List<DataPermDetRespDto> respDtos) {
		Map<String, List<DataPermDetRespDto>> mapDataPermDetResp = new HashMap<>();
		if (respDtos != null && !respDtos.isEmpty()) {
			respDtos.forEach(respDto -> {
				StringBuilder key = new StringBuilder();
				key.append(respDto.getEntityId());
				key.append("-");
				key.append(respDto.getId());
				String docKey = key.toString();
				if (mapDataPermDetResp.containsKey(docKey)) {
					List<DataPermDetRespDto> permDetDtos = mapDataPermDetResp
							.get(docKey);
					permDetDtos.add(respDto);
					mapDataPermDetResp.put(docKey, permDetDtos);
				} else {
					List<DataPermDetRespDto> permDetDtos = new ArrayList<>();
					permDetDtos.add(respDto);
					mapDataPermDetResp.put(docKey, permDetDtos);
				}
			});
		}
		return mapDataPermDetResp;
	}

	public void updateDataPermission(DataSecurityReqDto attrs,
			String loggedInUser) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl updateDataPermission begin");
		}
		String groupCode = attrs.getGroupCode();
		Long groupId = groupInfoDetailsRepository.findByGroupId(groupCode);
		Long entityId = attrs.getEntityId();
		String entityName = attrs.getEntityName();

		// Get the list of userIds from the modified list of attributes
		// obtained from the UI.
		List<Long> userIds = extractUserNamesFromInput(attrs);

		// Get the list of attribute values assigned to each of the users
		// in the above list.
		Map<Long, List<Long>> secMapFromDb = loadUserDataSecAttrs(userIds,
				entityId);

		// Get the newly modified list of attribute values for each of the
		// users.
		Map<Long, List<Long>> secMapFromUI = extractModifiedDataSecAttrs(attrs);

		// From the above 2 maps, calculate the list of sec attr values to
		// be newly added and the sec attr values to be marked as deleted,
		// for each user. The return map will contain a 'Pair' object with
		// the first element as a List of attr ids to be newly inserted and
		// the second element as the List of attr ids to be marked as deleted.
		Map<Long, Pair<List<Long>, List<Long>>> secChanges = calcualteModifiedAttrs(
				secMapFromDb, secMapFromUI);

		performInsertsAndUpdatesOfDsAttrs(groupId, groupCode, entityId,
				entityName, secChanges, loggedInUser);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl updateDataPermission End");
		}
	}

	/**
	 * This method creates a list of entities to be newly inserted into the Data
	 * security attributes table.
	 * 
	 * @param groupId
	 * @param groupCode
	 * @param entityId
	 * @param entityName
	 * @param userId
	 * @param newAttrs
	 * @param loggedInUser
	 * @return
	 */
	private List<DataSecurityEntity> createNewDSAttributes(Long groupId,
			String groupCode, Long entityId, String entityName, Long userId,
			List<Long> newAttrs, String loggedInUser) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl createNewDSAttributes begin");
		}
		List<DataSecurityEntity> entities = new ArrayList<>();
		newAttrs.forEach(attr -> {
			DataSecurityEntity secEntity = new DataSecurityEntity();
			secEntity.setEntityId(entityId);
			secEntity.setUserId(userId);
			secEntity.setGroupCode(groupCode);
			secEntity.setGroupId(groupId);
			secEntity.setEntityName(entityName);
			secEntity.setEntityAtValueId(attr);
			secEntity.setDelete(false);
			// Select At code value from entity at Value table insert At
			// Code to Data security table.
			if (attr != null) {
				EntityAtValueEntity entityAtValue = entityAtValueRepository
						.getEntityAlValue(attr);
				secEntity.setAtCode(entityAtValue.getAtCode());
			}
			// This needs to be done once the filter is implemented by
			User user = SecurityContext.getUser();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("User Name: {}", user.getUserPrincipalName());
			}
			secEntity.setCreatedBy(user.getUserPrincipalName());
			LocalDateTime localDateTime = EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now());
			secEntity.setCreatedOn(localDateTime);
			entities.add(secEntity);
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl createNewDSAttributes End");
		}
		return entities;
	}

	private void performInsertsAndUpdatesOfDsAttrs(Long groupId,
			String groupCode, Long entityId, String entityName,
			Map<Long, Pair<List<Long>, List<Long>>> map, String loggedInUser) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"DataSecurityServiceImpl performInsertsAndUpdatesOfDsAttrs begin");
		}
		map.forEach((userId, insertUpdateList) -> {
			List<Long> insertList = insertUpdateList.getValue0();
			List<Long> updateList = insertUpdateList.getValue1();

			// These are the new entities to be inserted to the DB.
			List<DataSecurityEntity> newEntities = createNewDSAttributes(
					groupId, groupCode, entityId, entityName, userId,
					insertList, loggedInUser);

			// Insert the new attrs list to the DB.
			dataSecRepository.saveAll(newEntities);

			// Mark the entities in the update list to delete.
			if (userId != null && !updateList.isEmpty()) {
				dataSecRepository.markAttrsAsDeletedForUser(userId, updateList,
						entityId);
			}
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"DataSecurityServiceImpl performInsertsAndUpdatesOfDsAttrs End");
		}
	}

	/**
	 * This method uses the
	 * 
	 * @return
	 */
	private Map<Long, Pair<List<Long>, List<Long>>> calcualteModifiedAttrs(
			Map<Long, List<Long>> secMapFromDb,
			Map<Long, List<Long>> secMapFromUI) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"DataSecurityServiceImpl calcualteModifiedAttrs begin");
		}
		Map<Long, Pair<List<Long>, List<Long>>> retMap = new HashMap<>();

		secMapFromUI.forEach((userId, attrs) -> {
			List<Long> uiAttrsList = attrs;
			List<Long> dbAttrsList = (secMapFromDb.get(userId) != null)
					? secMapFromDb.get(userId)
					: new ArrayList<>();

			// Find the list of new attributes passed from the UI that are
			// to be inserted to the DB.
			List<Long> attrsToBeInserted = ListUtils.subtract(uiAttrsList,
					dbAttrsList);
			// Find the list of attributes to be marked as deleted in the DB.
			List<Long> attrsToBeDeleted = ListUtils.subtract(dbAttrsList,
					uiAttrsList);

			retMap.put(userId, new Pair<List<Long>, List<Long>>(
					attrsToBeInserted, attrsToBeDeleted));
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl calcualteModifiedAttrs End");
		}
		return retMap;
	}

	/**
	 * Get the list of user Ids from input.
	 * 
	 * @param attrs
	 * @return
	 */
	private List<Long> extractUserNamesFromInput(DataSecurityReqDto attrs) {
		// List the user Ids
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"DataSecurityServiceImpl extractUserNamesFromInput begin");
		}
		List<Long> userIds = new ArrayList<>();
		List<EachUserAttributes> userDetails = attrs.getUserDetails();
		userDetails.forEach(userDetail -> {
			userIds.add(userDetail.getUserId());
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"DataSecurityServiceImpl extractUserNamesFromInput End");
		}
		return userIds;
	}

	/**
	 * This method loads the list of data security attributes for the given list
	 * of users and returns a map with the userId as the key and the
	 * corresponding user's allocated Attribute Value Ids.
	 * 
	 * The return map represents the existing data security permissions for the
	 * given user
	 * 
	 * @param userNames
	 * @return
	 */
	private Map<Long, List<Long>> loadUserDataSecAttrs(List<Long> userIds,
			Long entityId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl loadUserDataSecAttrs begin");
		}
		Map<Long, List<Long>> userDataSecAttrs = new HashMap<>();

		List<Object[]> objects = dataSecRepository
				.findAttrValuesForUsers(userIds, entityId);
		objects.forEach(object -> {

			Long userId = (Long) object[0];
			Long attrId = (Long) object[1];

			List<Long> attrsForUser = userDataSecAttrs.get(userId);
			if (attrsForUser == null) {
				attrsForUser = new ArrayList<>();
				userDataSecAttrs.put(userId, attrsForUser);
			}
			attrsForUser.add(attrId);
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityServiceImpl loadUserDataSecAttrs End");
		}
		return userDataSecAttrs;
	}

	/**
	 * This method used for extract Modified Data Sec Attributes
	 * 
	 * @param attrs
	 * @return
	 */
	private Map<Long, List<Long>> extractModifiedDataSecAttrs(
			DataSecurityReqDto attrs) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"DataSecurityServiceImpl extractModifiedDataSecAttrs begin");
		}
		Map<Long, List<Long>> extractModData = new HashMap<>();
		List<EachUserAttributes> userDetails = attrs.getUserDetails();

		if (userDetails != null) {
			userDetails.forEach(user -> {
				List<Long> listOfAllIds = new ArrayList<>();
				if (user.getGstIn() != null && !user.getGstIn().isEmpty()) {
					user.getGstIn()
							.forEach(getGstIns -> listOfAllIds.add(getGstIns));
				}
				if (user.getProfitCenter() != null
						&& !user.getProfitCenter().isEmpty()) {
					user.getProfitCenter()
							.forEach(profitCenterIds -> listOfAllIds
									.add(profitCenterIds));
				}

				if (user.getProfitCenter2() != null
						&& !user.getProfitCenter2().isEmpty()) {
					user.getProfitCenter2()
							.forEach(profitCenter2Ids -> listOfAllIds
									.add(profitCenter2Ids));
				}

				if (user.getPlant() != null && !user.getPlant().isEmpty()) {
					user.getPlant().forEach(planId -> listOfAllIds.add(planId));
				}
				if (user.getDivision() != null
						&& !user.getDivision().isEmpty()) {
					user.getDivision()
							.forEach(divId -> listOfAllIds.add(divId));
				}

				if (user.getSubDivision() != null
						&& !user.getSubDivision().isEmpty()) {
					user.getSubDivision()
							.forEach(divId -> listOfAllIds.add(divId));
				}
				if (user.getLocation() != null
						&& !user.getLocation().isEmpty()) {
					user.getLocation().forEach(
							locationId -> listOfAllIds.add(locationId));
				}
				if (user.getDistChannel() != null
						&& !user.getDistChannel().isEmpty()) {
					user.getDistChannel().forEach(
							distChannelId -> listOfAllIds.add(distChannelId));
				}
				if (user.getPurchOrg() != null
						&& !user.getPurchOrg().isEmpty()) {
					user.getPurchOrg().forEach(
							purchOrdId -> listOfAllIds.add(purchOrdId));
				}
				if (user.getSalesOrg() != null
						&& !user.getSalesOrg().isEmpty()) {
					user.getSalesOrg().forEach(
							salesOrgId -> listOfAllIds.add(salesOrgId));
				}
				if (user.getUserAccess1() != null
						&& !user.getUserAccess1().isEmpty()) {
					user.getUserAccess1().forEach(
							userDef1Ids -> listOfAllIds.add(userDef1Ids));
				}
				if (user.getUserAccess2() != null
						&& !user.getUserAccess2().isEmpty()) {
					user.getUserAccess2().forEach(
							userDef2Ids -> listOfAllIds.add(userDef2Ids));
				}
				if (user.getUserAccess3() != null
						&& !user.getUserAccess3().isEmpty()) {
					user.getUserAccess3().forEach(
							userDef3Ids -> listOfAllIds.add(userDef3Ids));
				}
				if (user.getUserAccess4() != null
						&& !user.getUserAccess4().isEmpty()) {
					user.getUserAccess4().forEach(
							userDef4Ids -> listOfAllIds.add(userDef4Ids));
				}
				if (user.getUserAccess5() != null
						&& !user.getUserAccess5().isEmpty()) {
					user.getUserAccess5().forEach(
							userDef5Ids -> listOfAllIds.add(userDef5Ids));
				}
				if (user.getUserAccess6() != null
						&& !user.getUserAccess6().isEmpty()) {
					user.getUserAccess6().forEach(
							userDef6Ids -> listOfAllIds.add(userDef6Ids));
				}

				if (user.getSourceId() != null
						&& !user.getSourceId().isEmpty()) {
					user.getSourceId().forEach(
							sourceIdIds -> listOfAllIds.add(sourceIdIds));
				}
				extractModData.put(user.getUserId(), listOfAllIds);
			});
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"DataSecurityServiceImpl extractModifiedDataSecAttrs End");
		}
		return extractModData;
	}

	/**
	 * Getting Security Attributes for User in Data Status Screen.
	 */
	@Override
	public List<DataSecurityAttriUserRespDto> getDataPermissionForUser(
			List<Long> entityList, String userName, String regType) {

		List<DataSecurityAttriUserRespDto> dataSecurityRespDtos = new ArrayList<>();
		Long userId = userCreationRepository.findIdByUserName(userName);
		List<Object[]> dataSecurityList = dataSecRepository
				.findDataSecurityWithAttributeValuesWithValue(entityList,
						userId);

		Map<Long, Map<String, List<Pair<Long, String>>>> attributeValueMap = dataSecurityList
				.stream()
				.collect(Collectors.groupingBy(obj -> (Long) obj[0],
						Collectors.groupingBy(obj -> (String) obj[1],
								Collectors.mapping(
										obj -> new Pair<Long, String>(
												(Long) obj[2], (String) obj[3]),
										Collectors.toList()))));
		for (Long eachEntity : entityList) {
			EntityUserMapping userMapping = entityUserMappingRepository
					.getUserBy(eachEntity, userId);
			if (userMapping != null) {
				DataSecurityAttriUserRespDto dataSecurityRespDto = new DataSecurityAttriUserRespDto();
				EntityInfoEntity entityInfo = entityInfoDetailsRepository
						.findEntityByEntityId(eachEntity);

				Map<String, List<Pair<Long, String>>> listAttributeCode = attributeValueMap
						.get(eachEntity);
				dataSecurityRespDto.setEntityId(eachEntity);
				dataSecurityRespDto.setEntityName(entityInfo.getEntityName());
				Set<String> eachAttributeCode = listAttributeCode.keySet();
				DataSecurityDto dataSecurityDto = new DataSecurityDto();
				List<DataSecuriDto> gstiNDetailDtos = new ArrayList<>();
				List<DataSecuriDto> profitCenterDtos = new ArrayList<>();
				List<DataSecuriDto> profitCenter2Dtos = new ArrayList<>();
				List<DataSecuriDto> plantDtos = new ArrayList<>();
				List<DataSecuriDto> divisionDtos = new ArrayList<>();
				List<DataSecuriDto> subDivisionDtos = new ArrayList<>();
				List<DataSecuriDto> salesOrgDtos = new ArrayList<>();
				List<DataSecuriDto> purchOrgDtos = new ArrayList<>();
				List<DataSecuriDto> distChannelDtos = new ArrayList<>();
				List<DataSecuriDto> locationDtos = new ArrayList<>();
				List<DataSecuriDto> userAccess1Dtos = new ArrayList<>();
				List<DataSecuriDto> userAccess2Dtos = new ArrayList<>();
				List<DataSecuriDto> userAccess3Dtos = new ArrayList<>();
				List<DataSecuriDto> userAccess4Dtos = new ArrayList<>();
				List<DataSecuriDto> userAccess5Dtos = new ArrayList<>();
				List<DataSecuriDto> userAccess6Dtos = new ArrayList<>();
				List<DataSecuriDto> sourceIdDtos = new ArrayList<>();
				Pair<ItemsDto, ItemsDto> itemsPair = getEntityAtConfigAttributes(
						entityInfo);
				ItemsDto items = itemsPair.getValue0();
				dataSecurityDto.setItems(items);
				dataSecurityDto.setInwarditems(itemsPair.getValue1());

				for (String attributeCode : eachAttributeCode) {
					List<Pair<Long, String>> eachAttributePair = listAttributeCode
							.get(attributeCode);
					if (OnboardingConstant.GSTIN
							.equalsIgnoreCase(attributeCode)) {

						List<String> eachAttrGsinPairList = eachAttributePair
								.stream().map(attrPair -> attrPair.getValue1())
								.collect(Collectors.toList());
						// List of GSTN's and RegisType - REGULAR
						if ("REGULAR".equalsIgnoreCase(regType)) {
							eachAttrGsinPairList = gstNDetailRepository
									.filterGstinBasedOnRegType(
											eachAttrGsinPairList);
							// List of GSTN's and RegisType - ISD
						} else if ("ISD".equalsIgnoreCase(regType)) {
							eachAttrGsinPairList = gstNDetailRepository
									.getGstinRegTypeISD(eachAttrGsinPairList);
							// List of GSTN's and RegisType - TDS
						} else if ("TDS".equalsIgnoreCase(regType)) {
							eachAttrGsinPairList = gstNDetailRepository
									.getGstinRegTypeTDS(eachAttrGsinPairList);
						}

						eachAttrGsinPairList.forEach(eachPair -> {
							DataSecuriDto gstINDetailDto = new DataSecuriDto();
							gstINDetailDto.setValue(eachPair);
							gstiNDetailDtos.add(gstINDetailDto);
						});
					}
					if (OnboardingConstant.PC.equalsIgnoreCase(attributeCode)
							&& items.isProfitCenter()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto profitCenterDto = new DataSecuriDto();
							profitCenterDto.setValue(eachPair.getValue1());
							profitCenterDtos.add(profitCenterDto);
						});
					}
					if (OnboardingConstant.PC2.equalsIgnoreCase(attributeCode)
							&& items.isProfitCenter2()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto profitCenter2Dto = new DataSecuriDto();
							profitCenter2Dto.setValue(eachPair.getValue1());
							profitCenter2Dtos.add(profitCenter2Dto);
						});
					}
					if (OnboardingConstant.PLANT.equalsIgnoreCase(attributeCode)
							&& items.isPlant()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto plantDto = new DataSecuriDto();
							plantDto.setValue(eachPair.getValue1());
							plantDtos.add(plantDto);
						});
					}
					if (OnboardingConstant.DIVISION.equalsIgnoreCase(
							attributeCode) && items.isDivision()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto divisionDto = new DataSecuriDto();
							divisionDto.setValue(eachPair.getValue1());
							divisionDtos.add(divisionDto);
						});
					}

					if (OnboardingConstant.SUB_DIVISION.equalsIgnoreCase(
							attributeCode) && items.isSubDivision()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto subDivisionDto = new DataSecuriDto();
							subDivisionDto.setValue(eachPair.getValue1());
							subDivisionDtos.add(subDivisionDto);
						});
					}
					if (OnboardingConstant.SO.equalsIgnoreCase(attributeCode)
							&& items.isSalesOrg()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto salesOrgDto = new DataSecuriDto();
							salesOrgDto.setValue(eachPair.getValue1());
							salesOrgDtos.add(salesOrgDto);
						});
					}
					if (OnboardingConstant.PO.equalsIgnoreCase(attributeCode)
							&& items.isPurchOrg()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto purchOrgDto = new DataSecuriDto();
							purchOrgDto.setValue(eachPair.getValue1());
							purchOrgDtos.add(purchOrgDto);
						});
					}
					if (OnboardingConstant.DC.equalsIgnoreCase(attributeCode)
							&& items.isDistChannel()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto distChannelDto = new DataSecuriDto();
							distChannelDto.setValue(eachPair.getValue1());
							distChannelDtos.add(distChannelDto);
						});
					}
					if (OnboardingConstant.LOCATION.equalsIgnoreCase(
							attributeCode) && items.isLocation()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto locationDto = new DataSecuriDto();
							locationDto.setValue(eachPair.getValue1());
							locationDtos.add(locationDto);
						});
					}
					if (OnboardingConstant.UD1.equalsIgnoreCase(attributeCode)
							&& items.isUserAccess1()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto userDef1Dto = new DataSecuriDto();
							userDef1Dto.setValue(eachPair.getValue1());
							userAccess1Dtos.add(userDef1Dto);
						});
					}
					if (OnboardingConstant.UD2.equalsIgnoreCase(attributeCode)
							&& items.isUserAccess2()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto userDef2Dto = new DataSecuriDto();
							userDef2Dto.setValue(eachPair.getValue1());
							userAccess2Dtos.add(userDef2Dto);
						});
					}
					if (OnboardingConstant.UD3.equalsIgnoreCase(attributeCode)
							&& items.isUserAccess3()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto userDef3Dto = new DataSecuriDto();
							userDef3Dto.setValue(eachPair.getValue1());
							userAccess3Dtos.add(userDef3Dto);
						});
					}
					if (OnboardingConstant.UD4.equalsIgnoreCase(attributeCode)
							&& items.isUserAccess4()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto userDef4Dto = new DataSecuriDto();
							userDef4Dto.setValue(eachPair.getValue1());
							userAccess4Dtos.add(userDef4Dto);
						});
					}
					if (OnboardingConstant.UD5.equalsIgnoreCase(attributeCode)
							&& items.isUserAccess5()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto userDef5Dto = new DataSecuriDto();
							userDef5Dto.setValue(eachPair.getValue1());
							userAccess5Dtos.add(userDef5Dto);
						});
					}
					if (OnboardingConstant.UD6.equalsIgnoreCase(attributeCode)
							&& items.isUserAccess6()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto userDef6Dto = new DataSecuriDto();
							userDef6Dto.setValue(eachPair.getValue1());
							userAccess6Dtos.add(userDef6Dto);
						});
					}
					if (OnboardingConstant.SI.equalsIgnoreCase(attributeCode)
							&& items.isSourceId()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto sourceIdDto = new DataSecuriDto();
							sourceIdDto.setValue(eachPair.getValue1());
							sourceIdDtos.add(sourceIdDto);
						});
					}
				}
				dataSecurityDto.setGstin(gstiNDetailDtos);
				dataSecurityDto.setPlant(plantDtos);
				dataSecurityDto.setProfitCenter(profitCenterDtos);
				dataSecurityDto.setProfitCenter2(profitCenter2Dtos);
				dataSecurityDto.setDivision(divisionDtos);
				dataSecurityDto.setSubDivision(subDivisionDtos);
				dataSecurityDto.setSalesOrg(salesOrgDtos);
				dataSecurityDto.setPurchOrg(purchOrgDtos);
				dataSecurityDto.setDistChannel(distChannelDtos);
				dataSecurityDto.setLocation(locationDtos);
				dataSecurityDto.setUserAccess1(userAccess1Dtos);
				dataSecurityDto.setUserAccess2(userAccess2Dtos);
				dataSecurityDto.setUserAccess3(userAccess3Dtos);
				dataSecurityDto.setUserAccess4(userAccess4Dtos);
				dataSecurityDto.setUserAccess5(userAccess5Dtos);
				dataSecurityDto.setUserAccess6(userAccess6Dtos);
				dataSecurityDto.setSourceId(sourceIdDtos);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User Name: {}", userName);
				}

				List<String> permCodes = userPermissionsRepository
						.getPermissionByUser(userId, eachEntity);
				AppPermissionDto appPermdto = new AppPermissionDto();
				if (permCodes != null) {
					appPermdto.setPermissions(permCodes);
				}

				List<String> roles = new ArrayList<>();
				String answer = entConPrmtRepo
						.findMaxAnswerForQuestionCode(eachEntity);
				String eInvoiceAnswer = entConPrmtRepo
						.findByEntityServiceOptionsEIvoice(eachEntity);

				String ewbAnswer = entConPrmtRepo
						.findByEntityServiceOptionsForEwb(eachEntity);

				String qrCodeAnswer = entConPrmtRepo
						.findByEntityQRCode(eachEntity);
				String optedforAP = entConPrmtRepo.findByOptedforAP(eachEntity);

				String optedforRateLevelReportGstr1 = entConPrmtRepo
						.findByOptedRateLevelReport(eachEntity);

				if (answer != null || eInvoiceAnswer != null
						|| ewbAnswer != null || qrCodeAnswer != null) {
					if (answer != null && ("C".equalsIgnoreCase(answer)
							|| "E".equalsIgnoreCase(answer))) {
						roles.add("R1");
					}
					if (eInvoiceAnswer != null
							&& "A".equalsIgnoreCase(eInvoiceAnswer)) {
						roles.add("R2");
					}
					if (ewbAnswer != null && "A".equalsIgnoreCase(ewbAnswer)) {
						roles.add("R3");
					}
					if (qrCodeAnswer != null
							&& ("A".equalsIgnoreCase(qrCodeAnswer)
									|| "B".equalsIgnoreCase(qrCodeAnswer)
									|| "C".equalsIgnoreCase(qrCodeAnswer))) {
						roles.add("R4");
					}
					if (optedforAP != null
							&& "A".equalsIgnoreCase(optedforAP)) {
						roles.add("R5");
					}

					if (optedforRateLevelReportGstr1 != null && "A"
							.equalsIgnoreCase(optedforRateLevelReportGstr1)) {
						roles.add("R6");
					}
					
					if (optedforRateLevelReportGstr1 != null && "A"
							.equalsIgnoreCase(optedforRateLevelReportGstr1)) {
						roles.add("R7");
					}
				} else {
					roles.add(" ");
				}
				appPermdto.setRoles(roles);
				dataSecurityRespDto.setAppPermissionDto(appPermdto);
				dataSecurityRespDto.setDataSecurity(dataSecurityDto);
				dataSecurityRespDtos.add(dataSecurityRespDto);
			}
		}
		return dataSecurityRespDtos;
	}

	@Override
	public List<DataSecurityAttriUserRespDto> getDataPermissionForUser(
			String regType) {

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();
		List<DataSecurityAttriUserRespDto> dataSecurityRespDtos = new ArrayList<>();
		Long userId = userCreationRepository.findIdByUserName(userName);

		Map<Long, Map<String, List<Pair<Long, String>>>> attributeValueMap = user
				.getAttributeMap();
		List<Long> entities = new ArrayList<>(attributeValueMap.keySet());
		List<EntityUserMapping> entityUserList = entityUserMappingRepository
				.findByUserIdAndEntityIdInAndIsFlagFalse(userId, entities);

		Map<String, EntityUserMapping> entityUserMap = entityUserList.stream()
				.collect(Collectors
						.toMap(key -> concatenateKeys(key.getEntityId(),
								key.getUserId()), value -> value));

		List<EntityInfoEntity> entityInfoList = entityInfoDetailsRepository
				.findByIdInAndIsDeleteFalse(entities);

		Map<Long, EntityInfoEntity> entityInfoMap = entityInfoList.stream()
				.collect(Collectors.toMap(key -> key.getId(), value -> value));
		List<GSTNDetailEntity> gstinInfoList = gstNDetailRepository
				.getGstinByEntites(entities);

		Map<String, List<String>> gstinInfoMap = gstinInfoList.stream()
				.collect(Collectors.groupingBy(
						gstin -> REGISTRATION_TYPES.contains(
								gstin.getRegistrationType()) ? "REGULAR"
										: gstin.getRegistrationType(),
						Collectors.mapping(GSTNDetailEntity::getGstin,
								Collectors.toList())));

		List<UserPermissionsEntity> userList = userPermissionsRepository
				.findByEntityIdInAndUserIdAndIsApplicableTrueAndIsDeleteFalse(
						entities, userId);
		Map<String, List<String>> userPermMap = userList.stream()
				.collect(Collectors.groupingBy(
						u -> concatenateKeys(u.getEntityId(), u.getUserId()),
						Collectors.mapping(UserPermissionsEntity::getPermCode,
								Collectors.toList())));

		for (Long eachEntity : attributeValueMap.keySet()) {
			EntityUserMapping userMapping = entityUserMap
					.get(eachEntity + "-" + userId);

			if (userMapping != null) {
				DataSecurityAttriUserRespDto dataSecurityRespDto = new DataSecurityAttriUserRespDto();
				EntityInfoEntity entityInfo = entityInfoMap.get(eachEntity);
				Map<String, List<Pair<Long, String>>> listAttributeCode = attributeValueMap
						.get(eachEntity);
				dataSecurityRespDto.setEntityId(eachEntity);
				dataSecurityRespDto.setEntityName(entityInfo.getEntityName());
				Set<String> eachAttributeCode = listAttributeCode.keySet();
				DataSecurityDto dataSecurityDto = new DataSecurityDto();
				List<DataSecuriDto> gstiNDetailDtos = new ArrayList<>();
				List<DataSecuriDto> profitCenterDtos = new ArrayList<>();
				List<DataSecuriDto> profitCenter2Dtos = new ArrayList<>();
				List<DataSecuriDto> plantDtos = new ArrayList<>();
				List<DataSecuriDto> divisionDtos = new ArrayList<>();
				List<DataSecuriDto> subDivisionDtos = new ArrayList<>();
				List<DataSecuriDto> salesOrgDtos = new ArrayList<>();
				List<DataSecuriDto> purchOrgDtos = new ArrayList<>();
				List<DataSecuriDto> distChannelDtos = new ArrayList<>();
				List<DataSecuriDto> locationDtos = new ArrayList<>();
				List<DataSecuriDto> userAccess1Dtos = new ArrayList<>();
				List<DataSecuriDto> userAccess2Dtos = new ArrayList<>();
				List<DataSecuriDto> userAccess3Dtos = new ArrayList<>();
				List<DataSecuriDto> userAccess4Dtos = new ArrayList<>();
				List<DataSecuriDto> userAccess5Dtos = new ArrayList<>();
				List<DataSecuriDto> userAccess6Dtos = new ArrayList<>();
				List<DataSecuriDto> sourceIdDtos = new ArrayList<>();
				Pair<ItemsDto, ItemsDto> itemsPair = getEntityAtConfigAttributes(
						entityInfo);
				ItemsDto items = itemsPair.getValue0();
				dataSecurityDto.setItems(items);
				dataSecurityDto.setInwarditems(itemsPair.getValue1());

				for (String attributeCode : eachAttributeCode) {
					List<Pair<Long, String>> eachAttributePair = listAttributeCode
							.get(attributeCode);
					if (OnboardingConstant.GSTIN
							.equalsIgnoreCase(attributeCode)) {
						List<String> eachAttrGsinPairList = eachAttributePair
								.stream().map(attrPair -> attrPair.getValue1())
								.collect(Collectors.toList());
						List<String> onboardedGstins = gstinInfoMap
								.get(regType);
						if (onboardedGstins != null
								&& !onboardedGstins.isEmpty()) {
							eachAttrGsinPairList.retainAll(onboardedGstins);
						}

						eachAttrGsinPairList.forEach(eachPair -> {
							DataSecuriDto gstINDetailDto = new DataSecuriDto();
							gstINDetailDto.setValue(eachPair);
							gstiNDetailDtos.add(gstINDetailDto);
						});
					}
					if (OnboardingConstant.PC.equalsIgnoreCase(attributeCode)
							&& items.isProfitCenter()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto profitCenterDto = new DataSecuriDto();
							profitCenterDto.setValue(eachPair.getValue1());
							profitCenterDtos.add(profitCenterDto);
						});
					}
					if (OnboardingConstant.PC2.equalsIgnoreCase(attributeCode)
							&& items.isProfitCenter2()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto profitCenter2Dto = new DataSecuriDto();
							profitCenter2Dto.setValue(eachPair.getValue1());
							profitCenter2Dtos.add(profitCenter2Dto);
						});
					}
					if (OnboardingConstant.PLANT.equalsIgnoreCase(attributeCode)
							&& items.isPlant()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto plantDto = new DataSecuriDto();
							plantDto.setValue(eachPair.getValue1());
							plantDtos.add(plantDto);
						});
					}
					if (OnboardingConstant.DIVISION.equalsIgnoreCase(
							attributeCode) && items.isDivision()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto divisionDto = new DataSecuriDto();
							divisionDto.setValue(eachPair.getValue1());
							divisionDtos.add(divisionDto);
						});
					}

					if (OnboardingConstant.SUB_DIVISION.equalsIgnoreCase(
							attributeCode) && items.isSubDivision()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto subDivisionDto = new DataSecuriDto();
							subDivisionDto.setValue(eachPair.getValue1());
							subDivisionDtos.add(subDivisionDto);
						});
					}
					if (OnboardingConstant.SO.equalsIgnoreCase(attributeCode)
							&& items.isSalesOrg()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto salesOrgDto = new DataSecuriDto();
							salesOrgDto.setValue(eachPair.getValue1());
							salesOrgDtos.add(salesOrgDto);
						});
					}
					if (OnboardingConstant.PO.equalsIgnoreCase(attributeCode)
							&& items.isPurchOrg()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto purchOrgDto = new DataSecuriDto();
							purchOrgDto.setValue(eachPair.getValue1());
							purchOrgDtos.add(purchOrgDto);
						});
					}
					if (OnboardingConstant.DC.equalsIgnoreCase(attributeCode)
							&& items.isDistChannel()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto distChannelDto = new DataSecuriDto();
							distChannelDto.setValue(eachPair.getValue1());
							distChannelDtos.add(distChannelDto);
						});
					}
					if (OnboardingConstant.LOCATION.equalsIgnoreCase(
							attributeCode) && items.isLocation()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto locationDto = new DataSecuriDto();
							locationDto.setValue(eachPair.getValue1());
							locationDtos.add(locationDto);
						});
					}
					if (OnboardingConstant.UD1.equalsIgnoreCase(attributeCode)
							&& items.isUserAccess1()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto userDef1Dto = new DataSecuriDto();
							userDef1Dto.setValue(eachPair.getValue1());
							userAccess1Dtos.add(userDef1Dto);
						});
					}
					if (OnboardingConstant.UD2.equalsIgnoreCase(attributeCode)
							&& items.isUserAccess2()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto userDef2Dto = new DataSecuriDto();
							userDef2Dto.setValue(eachPair.getValue1());
							userAccess2Dtos.add(userDef2Dto);
						});
					}
					if (OnboardingConstant.UD3.equalsIgnoreCase(attributeCode)
							&& items.isUserAccess3()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto userDef3Dto = new DataSecuriDto();
							userDef3Dto.setValue(eachPair.getValue1());
							userAccess3Dtos.add(userDef3Dto);
						});
					}
					if (OnboardingConstant.UD4.equalsIgnoreCase(attributeCode)
							&& items.isUserAccess4()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto userDef4Dto = new DataSecuriDto();
							userDef4Dto.setValue(eachPair.getValue1());
							userAccess4Dtos.add(userDef4Dto);
						});
					}
					if (OnboardingConstant.UD5.equalsIgnoreCase(attributeCode)
							&& items.isUserAccess5()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto userDef5Dto = new DataSecuriDto();
							userDef5Dto.setValue(eachPair.getValue1());
							userAccess5Dtos.add(userDef5Dto);
						});
					}
					if (OnboardingConstant.UD6.equalsIgnoreCase(attributeCode)
							&& items.isUserAccess6()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto userDef6Dto = new DataSecuriDto();
							userDef6Dto.setValue(eachPair.getValue1());
							userAccess6Dtos.add(userDef6Dto);
						});
					}
					if (OnboardingConstant.SI.equalsIgnoreCase(attributeCode)
							&& items.isSourceId()) {
						eachAttributePair.forEach(eachPair -> {
							DataSecuriDto sourceIdDto = new DataSecuriDto();
							sourceIdDto.setValue(eachPair.getValue1());
							sourceIdDtos.add(sourceIdDto);
						});
					}
				}
				dataSecurityDto.setGstin(gstiNDetailDtos);
				dataSecurityDto.setPlant(plantDtos);
				dataSecurityDto.setProfitCenter(profitCenterDtos);
				dataSecurityDto.setProfitCenter2(profitCenter2Dtos);
				dataSecurityDto.setDivision(divisionDtos);
				dataSecurityDto.setSubDivision(subDivisionDtos);
				dataSecurityDto.setSalesOrg(salesOrgDtos);
				dataSecurityDto.setPurchOrg(purchOrgDtos);
				dataSecurityDto.setDistChannel(distChannelDtos);
				dataSecurityDto.setLocation(locationDtos);
				dataSecurityDto.setUserAccess1(userAccess1Dtos);
				dataSecurityDto.setUserAccess2(userAccess2Dtos);
				dataSecurityDto.setUserAccess3(userAccess3Dtos);
				dataSecurityDto.setUserAccess4(userAccess4Dtos);
				dataSecurityDto.setUserAccess5(userAccess5Dtos);
				dataSecurityDto.setUserAccess6(userAccess6Dtos);
				dataSecurityDto.setSourceId(sourceIdDtos);
				List<String> permCodes = userPermMap
						.get(concatenateKeys(eachEntity, userId));
				AppPermissionDto appPermdto = new AppPermissionDto();
				if (permCodes != null) {
					appPermdto.setPermissions(permCodes);
				}

				List<String> roles = new ArrayList<>();
				String answer = entConPrmtRepo
						.findMaxAnswerForQuestionCode(eachEntity);
				String eInvoiceAnswer = entConPrmtRepo
						.findByEntityServiceOptionsEIvoice(eachEntity);

				String ewbAnswer = entConPrmtRepo
						.findByEntityServiceOptionsForEwb(eachEntity);

				String qrCodeAnswer = entConPrmtRepo
						.findByEntityQRCode(eachEntity);
				String optedforAP = entConPrmtRepo.findByOptedforAP(eachEntity);

				String optedforRateLevelReportGstr1 = entConPrmtRepo
						.findByOptedRateLevelReport(eachEntity);
				
				String optedforHsnReportReportGstr1 = entConPrmtRepo
						.findByOptedHsnSummaryReport(eachEntity);
				
				
				String optedforInwardEinvoice = entConPrmtRepo
						.findByInwardEinvoiceOpted(eachEntity);
				
				String optedforDRC01EmailNotification = entConPrmtRepo
						.findByInwardEinvoiceOpted(eachEntity);
				String optedDRC01AutoEmail = entConPrmtRepo.findByDRC01AutoEmailOpted(eachEntity);
				
				String gLReconoptedMainAns = entConPrmtRepo
							.findAnsbyQuestionGLRecon(eachEntity,
									"Do you want to enable GL Recon Functionality?",
									"R");
				
				if (answer != null || eInvoiceAnswer != null
						|| ewbAnswer != null || qrCodeAnswer != null) {
					if (answer != null && ("C".equalsIgnoreCase(answer)
							|| "E".equalsIgnoreCase(answer))) {
						roles.add("R1");
					}
					if (eInvoiceAnswer != null
							&& "A".equalsIgnoreCase(eInvoiceAnswer)) {
						roles.add("R2");
					}
					if (ewbAnswer != null && "A".equalsIgnoreCase(ewbAnswer)) {
						roles.add("R3");
					}
					if (qrCodeAnswer != null
							&& ("A".equalsIgnoreCase(qrCodeAnswer)
									|| "B".equalsIgnoreCase(qrCodeAnswer)
									|| "C".equalsIgnoreCase(qrCodeAnswer))) {
						roles.add("R4");
					}
					if (optedforAP != null
							&& "A".equalsIgnoreCase(optedforAP)) {
						roles.add("R5");
					}

					if (optedforRateLevelReportGstr1 != null && "A"
							.equalsIgnoreCase(optedforRateLevelReportGstr1)) {
						roles.add("R6");
					}
					if (optedforHsnReportReportGstr1 != null && "A"
							.equalsIgnoreCase(optedforHsnReportReportGstr1)) {
						roles.add("R7");
					}
					
					if (optedforInwardEinvoice != null && "A"
							.equalsIgnoreCase(optedforInwardEinvoice)) {
						roles.add("R8");
					}
					
					if (optedDRC01AutoEmail != null && "A"
							.equalsIgnoreCase(optedDRC01AutoEmail)) {
						roles.add("R9");
					}
					
					if (gLReconoptedMainAns != null && "A"
							.equalsIgnoreCase(gLReconoptedMainAns)) {
						roles.add("R10");
					}

				} else {
					roles.add(" ");
				}
				appPermdto.setRoles(roles);
				dataSecurityRespDto.setAppPermissionDto(appPermdto);
				dataSecurityRespDto.setDataSecurity(dataSecurityDto);
				dataSecurityRespDtos.add(dataSecurityRespDto);
			}
		}
		return dataSecurityRespDtos;
	}

	private String concatenateKeys(Long entityId, Long userId) {
		return String.format("%d-%d", entityId, userId);
	}
}
