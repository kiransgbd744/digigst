package com.ey.advisory.app.services.docs.common;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityAtValueEntity;
import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.repositories.client.EntityAtConfiRepository;
import com.ey.advisory.admin.data.repositories.client.EntityAtValueRepository;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.docs.dto.ValOrgAttributesDto;
import com.ey.advisory.app.util.AspDocumentConstants.TransDocTypes;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AOrgHierarchyMasterDataUpdation")
public class Gstr1AOrgHierarchyMasterDataUpdation {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AOrgHierarchyMasterDataUpdation.class);

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck configParamCheck;

	@Autowired
	@Qualifier("entityAtValueRepository")
	private EntityAtValueRepository entityAtValueRepository;

	@Autowired
	@Qualifier("entityAtConfiRepository")
	private EntityAtConfiRepository entAtConfRepo;

	public void updateOrgHierarchyMasterDataValForOutwardDocs(
			List<Gstr1AOutwardTransDocument> documents) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Begin : updateOrgHierarchyMasterDataValForOutwardDocs "
							+ "for Group Code " + TenantContext.getTenantId());
		}
		try {
			Set<EntityAtValueEntity> entityAtValSet = new HashSet<>();
			for (Gstr1AOutwardTransDocument document : documents) {
				Long entityId = document.getEntityId();
				Map<Long, List<EntityConfigPrmtEntity>> map = document
						.getEntityConfigParamMap();
				Map<String, String> questionAnsMap = configParamCheck
						.getQuestionAndAnswerMap(entityId, map);
				String o5SelectedAnswer = questionAnsMap
						.get(OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O5
								.name());
				String o5AnsA = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name();
				if (o5AnsA.equalsIgnoreCase(o5SelectedAnswer)) {
					ValOrgAttributesDto dto = new ValOrgAttributesDto();
					dto.setTransDocType(TransDocTypes.OUTWARD.getType());
					dto.setEntityId(document.getEntityId());
					dto.setGroupId(document.getGroupId());
					dto.setEntityAtConfMap(document.getEntityAtConfMap());
					dto.setEntityAtValMap(document.getEntityAtValMap());
					dto.setProfitCentre(document.getProfitCentre());
					dto.setPlant(document.getPlantCode());
					dto.setDivision(document.getDivision());
					dto.setLocation(document.getLocation());
					dto.setSalesOrg(document.getSalesOrgnization());
					dto.setDistChnl(document.getDistributionChannel());
					dto.setUserDefined1(document.getUserAccess1());
					dto.setUserDefined2(document.getUserAccess2());
					dto.setUserDefined3(document.getUserAccess3());
					dto.setUserDefined4(document.getUserAccess4());
					dto.setUserDefined5(document.getUserAccess5());
					dto.setUserDefined6(document.getUserAccess6());
					dto.setCreatedBy(document.getCreatedBy());
					validateAndAddOrgAttributes(entityAtValSet, dto);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				int size = entityAtValSet.size();
				LOGGER.debug("entityAtValSet SIZE " + size);
				if (entityAtValSet != null && !entityAtValSet.isEmpty()) {
					entityAtValSet.forEach(entityAtVal -> {
						String atCode = entityAtVal.getAtCode();
						String atValue = entityAtVal.getAtValue();
						Long entityId = entityAtVal.getEntityId();
						Long entityConfId = entityAtVal.getEntityAtConfigId();
						String groupCode = entityAtVal.getGroupCode();
						LOGGER.debug("entityAtValSet: atCode- " + atCode
								+ " ,atValue - " + atValue + " ,entityConfId- "
								+ entityConfId + " ,entityId - " + entityId
								+ " ,groupCode - " + groupCode);
					});
				}
			}
			if (!entityAtValSet.isEmpty()) {
				entityAtValueRepository.saveAll(entityAtValSet);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"End : updateOrgHierarchyMasterDataValForOutwardDocs "
								+ "for Group Code "
								+ TenantContext.getTenantId());
			}
		} catch (RuntimeException e) {
			LOGGER.error("For Group Code " + TenantContext.getTenantId()
					+ "Exception in Update Outward Org Hierarchy Tables " + e);
			throw e;
		}
	}

	public void updateOrgHierarchyMasterDataValForInwardDocs(
			List<InwardTransDocument> documents) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begin : updateOrgHierarchyMasterDataValForInwardDocs "
					+ "for Group Code " + TenantContext.getTenantId());
		}
		try {
			Set<EntityAtValueEntity> entityAtValSet = new HashSet<>();
			for (InwardTransDocument document : documents) {
				Long entityId = document.getEntityId();
				Map<Long, List<EntityConfigPrmtEntity>> map = document
						.getEntityConfigParamMap();
				Map<String, String> questionAnsMap = configParamCheck
						.getQuestionAndAnswerMap(entityId, map);
				String o5SelectedAnswer = questionAnsMap
						.get(OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID.I4
								.name());
				String o5AnsA = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A.name();
				if (o5AnsA.equalsIgnoreCase(o5SelectedAnswer)) {
					ValOrgAttributesDto dto = new ValOrgAttributesDto();
					dto.setTransDocType(TransDocTypes.INWARD.getType());
					dto.setEntityId(document.getEntityId());
					dto.setGroupId(document.getGroupId());
					dto.setEntityAtConfMap(document.getEntityAtConfMap());
					dto.setEntityAtValMap(document.getEntityAtValMap());
					dto.setProfitCentre(document.getProfitCentre());
					dto.setPlant(document.getPlantCode());
					dto.setDivision(document.getDivision());
					dto.setLocation(document.getLocation());
					dto.setPurchaseOrg(document.getPurchaseOrganization());
					dto.setUserDefined1(document.getUserAccess1());
					dto.setUserDefined2(document.getUserAccess2());
					dto.setUserDefined3(document.getUserAccess3());
					dto.setUserDefined4(document.getUserAccess4());
					dto.setUserDefined5(document.getUserAccess5());
					dto.setUserDefined6(document.getUserAccess6());
					dto.setCreatedBy(document.getCreatedBy());
					validateAndAddOrgAttributes(entityAtValSet, dto);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				int size = entityAtValSet.size();
				LOGGER.debug("entityAtValSet SIZE " + size);
				if (entityAtValSet != null && !entityAtValSet.isEmpty()) {
					entityAtValSet.forEach(entityAtVal -> {
						String atCode = entityAtVal.getAtCode();
						String atValue = entityAtVal.getAtValue();
						Long entityId = entityAtVal.getEntityId();
						String groupCode = entityAtVal.getGroupCode();
						LOGGER.debug("entityAtValSet : atCode - " + atCode
								+ " atValue - " + atValue + " entityId - "
								+ entityId + " groupCode - " + groupCode);
					});
				}
			}
			if (!entityAtValSet.isEmpty()) {
				entityAtValueRepository.saveAll(entityAtValSet);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"End : updateOrgHierarchyMasterDataValForInwardDocs "
								+ "for Group Code "
								+ TenantContext.getTenantId());
			}
		} catch (RuntimeException e) {
			LOGGER.error("For Group Code " + TenantContext.getTenantId()
					+ "Exception in Update Outward Org Hierarchy Tables " + e);
			throw e;
		}
	}

	private void validateAndAddOrgAttributes(
			Set<EntityAtValueEntity> entityAtValSet,
			ValOrgAttributesDto valOrgAttrDto) {
		Map<Long, List<Pair<String, String>>> entityAtValMap = valOrgAttrDto
				.getEntityAtValMap();
		List<Pair<String, String>> orgAttrlist = entityAtValMap
				.get(valOrgAttrDto.getEntityId());
		Map<String, Set<String>> attrMap = new HashMap<>();
		if (orgAttrlist != null && !orgAttrlist.isEmpty()) {
			for (Pair<String, String> orgAttr : orgAttrlist) {
				String attrCode = orgAttr.getValue0();
				if (OnboardingConstant.PC.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.PC,
							k -> new HashSet<>()).add(attrValue);
				}
				if (OnboardingConstant.PLANT.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.PLANT,
							k -> new HashSet<>()).add(attrValue);
				}
				if (OnboardingConstant.DIVISION.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.DIVISION,
							k -> new HashSet<>()).add(attrValue);
				}
				if (OnboardingConstant.LOCATION.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.LOCATION,
							k -> new HashSet<>()).add(attrValue);
				}
				if (OnboardingConstant.SO.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.SO,
							k -> new HashSet<>()).add(attrValue);
				}
				if (OnboardingConstant.PO.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.PO,
							k -> new HashSet<>()).add(attrValue);
				}
				if (OnboardingConstant.DC.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.DC,
							k -> new HashSet<>()).add(attrValue);
				}
				if (OnboardingConstant.UD1.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.UD1,
							k -> new HashSet<>()).add(attrValue);
				}
				if (OnboardingConstant.UD2.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.UD2,
							k -> new HashSet<>()).add(attrValue);
				}
				if (OnboardingConstant.UD3.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.UD3,
							k -> new HashSet<>()).add(attrValue);
				}
				if (OnboardingConstant.UD4.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.UD4,
							k -> new HashSet<>()).add(attrValue);
				}
				if (OnboardingConstant.UD5.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.UD5,
							k -> new HashSet<>()).add(attrValue);
				}
				if (OnboardingConstant.UD6.equalsIgnoreCase(attrCode)) {
					String attrValue = orgAttr.getValue1();
					attrMap.computeIfAbsent(OnboardingConstant.UD6,
							k -> new HashSet<>()).add(attrValue);
				}
			}
			Set<String> pcAttrValSet = attrMap.get(OnboardingConstant.PC);
			Set<String> plantAttrValSet = attrMap.get(OnboardingConstant.PLANT);
			Set<String> divisionAttrValSet = attrMap
					.get(OnboardingConstant.DIVISION);
			Set<String> locationAttrValSet = attrMap
					.get(OnboardingConstant.LOCATION);
			Set<String> soAttrValSet = attrMap.get(OnboardingConstant.SO);
			Set<String> distChannelAttrValSet = attrMap
					.get(OnboardingConstant.DC);
			Set<String> poAttrValSet = attrMap.get(OnboardingConstant.PO);
			Set<String> ud1AttrValSet = attrMap.get(OnboardingConstant.UD1);
			Set<String> ud2AttrValSet = attrMap.get(OnboardingConstant.UD2);
			Set<String> ud3AttrValSet = attrMap.get(OnboardingConstant.UD3);
			Set<String> ud4AttrValSet = attrMap.get(OnboardingConstant.UD4);
			Set<String> ud5AttrValSet = attrMap.get(OnboardingConstant.UD5);
			Set<String> ud6AttrValSet = attrMap.get(OnboardingConstant.UD6);
			if (valOrgAttrDto.getProfitCentre() != null
					&& !valOrgAttrDto.getProfitCentre().trim().isEmpty()) {
				if (pcAttrValSet != null && !pcAttrValSet.isEmpty()) {
					if (!pcAttrValSet.stream().anyMatch(valOrgAttrDto
							.getProfitCentre()::equalsIgnoreCase)) {
						addToEntityAtValListToPersist(entityAtValSet,
								valOrgAttrDto, OnboardingConstant.PC,
								valOrgAttrDto.getProfitCentre().trim());
					}
				}
			}
			if (valOrgAttrDto.getPlant() != null
					&& !valOrgAttrDto.getPlant().trim().isEmpty()) {
				if (plantAttrValSet != null && !plantAttrValSet.isEmpty()) {
					if (!plantAttrValSet.stream().anyMatch(
							valOrgAttrDto.getPlant()::equalsIgnoreCase)) {
						if (valOrgAttrDto.getPlant() != null) {
							addToEntityAtValListToPersist(entityAtValSet,
									valOrgAttrDto, OnboardingConstant.PLANT,
									valOrgAttrDto.getPlant().trim());
						}
					}
				}
			}
			if (valOrgAttrDto.getDivision() != null
					&& !valOrgAttrDto.getDivision().trim().isEmpty()) {
				if (divisionAttrValSet != null
						&& !divisionAttrValSet.isEmpty()) {
					if (!divisionAttrValSet.stream().anyMatch(
							valOrgAttrDto.getDivision()::equalsIgnoreCase)) {
						addToEntityAtValListToPersist(entityAtValSet,
								valOrgAttrDto, OnboardingConstant.DIVISION,
								valOrgAttrDto.getDivision().trim());
					}
				}
			}
			if (valOrgAttrDto.getLocation() != null
					&& !valOrgAttrDto.getLocation().isEmpty()) {
				if (locationAttrValSet != null
						&& !locationAttrValSet.isEmpty()) {
					if (!locationAttrValSet.stream().anyMatch(
							valOrgAttrDto.getLocation()::equalsIgnoreCase)) {
						addToEntityAtValListToPersist(entityAtValSet,
								valOrgAttrDto, OnboardingConstant.LOCATION,
								valOrgAttrDto.getLocation().trim());
					}
				}
			}
			if (TransDocTypes.OUTWARD.getType()
					.equals(valOrgAttrDto.getTransDocType())) {
				if (valOrgAttrDto.getSalesOrg() != null
						&& !valOrgAttrDto.getSalesOrg().trim().isEmpty()) {
					if (soAttrValSet != null && !soAttrValSet.isEmpty()) {
						if (!soAttrValSet.stream().anyMatch(valOrgAttrDto
								.getSalesOrg()::equalsIgnoreCase)) {
							addToEntityAtValListToPersist(entityAtValSet,
									valOrgAttrDto, OnboardingConstant.SO,
									valOrgAttrDto.getSalesOrg().trim());
						}
					}
				}
				if (valOrgAttrDto.getDistChnl() != null
						&& !valOrgAttrDto.getDistChnl().trim().isEmpty()) {
					if (distChannelAttrValSet != null
							&& !distChannelAttrValSet.isEmpty()) {
						if (!distChannelAttrValSet.stream()
								.anyMatch(valOrgAttrDto
										.getDistChnl()::equalsIgnoreCase)) {
							addToEntityAtValListToPersist(entityAtValSet,
									valOrgAttrDto, OnboardingConstant.DC,
									valOrgAttrDto.getDistChnl().trim());
						}
					}
				}
			}
			if (TransDocTypes.INWARD.getType()
					.equals(valOrgAttrDto.getTransDocType())) {
				if (valOrgAttrDto.getPurchaseOrg() != null
						&& !valOrgAttrDto.getPurchaseOrg().trim().isEmpty()) {
					if (poAttrValSet != null && !poAttrValSet.isEmpty()) {
						if (!poAttrValSet.stream().anyMatch(valOrgAttrDto
								.getPurchaseOrg()::equalsIgnoreCase)) {
							addToEntityAtValListToPersist(entityAtValSet,
									valOrgAttrDto, OnboardingConstant.PO,
									valOrgAttrDto.getPurchaseOrg().trim());
						}
					}
				}
			}
			if (valOrgAttrDto.getUserDefined1() != null
					&& !valOrgAttrDto.getUserDefined1().trim().isEmpty()) {
				if (ud1AttrValSet != null && !ud1AttrValSet.isEmpty()) {
					if (!ud1AttrValSet.stream().anyMatch(valOrgAttrDto
							.getUserDefined1()::equalsIgnoreCase)) {
						addToEntityAtValListToPersist(entityAtValSet,
								valOrgAttrDto, OnboardingConstant.UD1,
								valOrgAttrDto.getUserDefined1().trim());
					}
				}
			}
			if (valOrgAttrDto.getUserDefined2() != null
					&& !valOrgAttrDto.getUserDefined2().trim().isEmpty()) {
				if (ud2AttrValSet != null && !ud2AttrValSet.isEmpty()) {
					if (!ud2AttrValSet.stream().anyMatch(valOrgAttrDto
							.getUserDefined2()::equalsIgnoreCase)) {
						addToEntityAtValListToPersist(entityAtValSet,
								valOrgAttrDto, OnboardingConstant.UD2,
								valOrgAttrDto.getUserDefined2().trim());
					}
				}
			}
			if (valOrgAttrDto.getUserDefined3() != null
					&& !valOrgAttrDto.getUserDefined3().trim().isEmpty()) {
				if (ud3AttrValSet != null && !ud3AttrValSet.isEmpty()) {
					if (!ud3AttrValSet.stream().anyMatch(valOrgAttrDto
							.getUserDefined3()::equalsIgnoreCase)) {
						addToEntityAtValListToPersist(entityAtValSet,
								valOrgAttrDto, OnboardingConstant.UD3,
								valOrgAttrDto.getUserDefined3().trim());
					}
				}
			}
			if (valOrgAttrDto.getUserDefined4() != null
					&& !valOrgAttrDto.getUserDefined4().trim().isEmpty()) {
				if (ud4AttrValSet != null && !ud4AttrValSet.isEmpty()) {
					if (!ud4AttrValSet.stream().anyMatch(valOrgAttrDto
							.getUserDefined4()::equalsIgnoreCase)) {
						addToEntityAtValListToPersist(entityAtValSet,
								valOrgAttrDto, OnboardingConstant.UD4,
								valOrgAttrDto.getUserDefined4().trim());
					}
				}
			}
			if (valOrgAttrDto.getUserDefined5() != null
					&& !valOrgAttrDto.getUserDefined5().trim().isEmpty()) {
				if (ud5AttrValSet != null && !ud5AttrValSet.isEmpty()) {
					if (!ud5AttrValSet.stream().anyMatch(valOrgAttrDto
							.getUserDefined5()::equalsIgnoreCase)) {
						addToEntityAtValListToPersist(entityAtValSet,
								valOrgAttrDto, OnboardingConstant.UD5,
								valOrgAttrDto.getUserDefined5().trim());
					}
				}
			}
			if (valOrgAttrDto.getUserDefined6() != null
					&& !valOrgAttrDto.getUserDefined6().trim().isEmpty()) {
				if (ud6AttrValSet != null && !ud6AttrValSet.isEmpty()) {
					if (!ud6AttrValSet.stream().anyMatch(valOrgAttrDto
							.getUserDefined6()::equalsIgnoreCase)) {
						addToEntityAtValListToPersist(entityAtValSet,
								valOrgAttrDto, OnboardingConstant.UD6,
								valOrgAttrDto.getUserDefined6().trim());
					}
				}
			}
		}
	}

	private void addToEntityAtValListToPersist(
			Set<EntityAtValueEntity> entityAtValSet,
			ValOrgAttributesDto valOrgAttrDto, String attrCode,
			String attrValue) {
		EntityAtConfigKey entityAtConfigKey = new EntityAtConfigKey(
				valOrgAttrDto.getEntityId(), attrCode);
		Map<EntityAtConfigKey, Map<Long, String>> entAtConfMap = valOrgAttrDto
				.getEntityAtConfMap();
		Map<Long, String> entitAtConfigInnerMap = entAtConfMap
				.get(entityAtConfigKey);
		if (entitAtConfigInnerMap != null) {
			entitAtConfigInnerMap.entrySet().forEach(entitAtConfMap -> {
				Long entityAtConfigId = entitAtConfMap.getKey();// Primary key
				// AT_OUTWARD/AT_INWARD Value
				String atConfigVal = entitAtConfMap.getValue();
				if (entityAtConfigId != null) {
					if (OnboardingConstant.AT_O.equalsIgnoreCase(atConfigVal)) {
						EntityAtValueEntity enAtVal = new EntityAtValueEntity();
						enAtVal.setEntityAtConfigId(entityAtConfigId);
						enAtVal.setGroupId(valOrgAttrDto.getGroupId());
						enAtVal.setAtCode(attrCode);
						enAtVal.setAtValue(attrValue);
						enAtVal.setGroupCode(TenantContext.getTenantId());
						enAtVal.setEntityId(valOrgAttrDto.getEntityId());
						enAtVal.setCreatedBy(valOrgAttrDto.getCreatedBy());
						enAtVal.setCreatedOn(LocalDateTime.now());
						entityAtValSet.add(enAtVal);
					}
				}
			});
		}
	}
}
