package com.ey.advisory.app.services.docs.common;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.entities.client.MasterItemEntity;
import com.ey.advisory.admin.data.entities.client.MasterVendorEntity;
import com.ey.advisory.admin.data.repositories.client.MasterItemRepository;
import com.ey.advisory.admin.data.repositories.client.MasterVendorRepository;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Component("InwardAdditionalMasterDataAddition")
public class InwardAdditionalMasterDataAddition {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(InwardAdditionalMasterDataAddition.class);

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck configParamCheck;

	@Autowired
	@Qualifier("masterVendorRepository")
	private MasterVendorRepository masterVendorRepo;

	@Autowired
	@Qualifier("masterItemRepository")
	private MasterItemRepository masterItemRepo;

	public void addAdditionalMasterData(List<InwardTransDocument> documents) {
		// Add Additional Master Vendor Data Q12,13
		addAdditionalMasterVendorData(documents);
		// Add Additional Master Product Data - On Boarding Questions -
		// Q19,20,21
		addAdditionalMasterItemData(documents);
	}

	private void addAdditionalMasterVendorData(
			List<InwardTransDocument> documents) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begin : addAdditionalMasterVendorData for Group Code "
					+ TenantContext.getTenantId());
		}
		try {
			Set<MasterVendorEntity> masterVendorEntities = new HashSet<>();
			documents.forEach(document -> {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("does Supplier GSTIN exists "
							+ "in Master Vendor Table "
							+ document.getIsSgstInMasterVendor()
							+ ", Sgstin in document req " + document.getCgstin()
							+ " of Inward Document Key " + document.getDocKey()
							+ " for Group Code " + TenantContext.getTenantId());
				}
				if (document.getIsProcessed()) {
					if ((document.getSgstin() != null
							&& !document.getSgstin().trim().isEmpty())
							&& !(document.getIsSgstInMasterVendor())) {
						Long entityId = document.getEntityId();
						Map<Long, List<EntityConfigPrmtEntity>> map = document
								.getEntityConfigParamMap();
						Map<String, String> questionAnsMap = configParamCheck
								.getQuestionAndAnswerMap(entityId, map);
						String i5SelectedAnswer = questionAnsMap
								.get(OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID.I5
										.name());
						String i5AnsB = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B
								.name();
						if (i5AnsB.equalsIgnoreCase(i5SelectedAnswer)) {
							String i6SelectedAnswer = questionAnsMap
									.get(OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID.I6
											.name());
							String i6AnsA = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A
									.name();
							if (i6AnsA.equalsIgnoreCase(i6SelectedAnswer)) {
								MasterVendorEntity mvEntity = new MasterVendorEntity();
								mvEntity.setCustGstinPan(document.getCgstin());
								mvEntity.setSupplierGstinPan(
										document.getSgstin());
								mvEntity.setLegalName(
										document.getCustOrSuppName());
								mvEntity.setSupplierCode(
										document.getCustOrSuppCode());
								mvEntity.setSupplierType(
										document.getCustOrSuppType());
								mvEntity.setEntityId(document.getEntityId());
								mvEntity.setVendorKey(document.getSgstin());
								mvEntity.setOutSideIndia(GSTConstants.N);
								masterVendorEntities.add(mvEntity);
							}
						}
					}
				}
			});
			if (LOGGER.isDebugEnabled()) {
				int size = masterVendorEntities.size();
				LOGGER.debug("masterVendorEntities SIZE " + size);
				if (masterVendorEntities != null
						&& !masterVendorEntities.isEmpty()) {
					masterVendorEntities.forEach(mcEntity -> {
						String gstin = mcEntity.getSupplierGstinPan();
						Long entityId = mcEntity.getEntityId();
						LOGGER.debug("MasterVendor Additional Data: gstin - "
								+ gstin + " entityId - " + entityId
								+ " For Group Code - "
								+ TenantContext.getTenantId());
					});
				}
			}
			if (masterVendorEntities != null
					&& !masterVendorEntities.isEmpty()) {
				masterVendorRepo.saveAll(masterVendorEntities);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"End : addAdditionalMasterVendorData for Group Code "
								+ TenantContext.getTenantId());
			}
		} catch (RuntimeException e) {
			LOGGER.error("For Group Code " + TenantContext.getTenantId()
					+ " Exception in Add Additional Master Vendor  Data " + e);
			throw e;
		}
	}

	private void addAdditionalMasterItemData(
			List<InwardTransDocument> documents) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begin : addAdditionalMasterItemData for Group Code "
					+ TenantContext.getTenantId());
		}
		try {
			Set<MasterItemEntity> masterItemEntities = new HashSet<>();
			documents.forEach(document -> {
				if (document.getIsProcessed()) {
					Map<Long, List<EntityConfigPrmtEntity>> map = document
							.getEntityConfigParamMap();
					Map<String, String> questionAnsMap = configParamCheck
							.getQuestionAndAnswerMap(document.getEntityId(),
									map);
					String i9SelectedAnswer = questionAnsMap
							.get(OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID.I9
									.name());
					String i9AnsD = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.D.name();
					if (i9SelectedAnswer != null
							&& !i9AnsD.equalsIgnoreCase(i9SelectedAnswer)) {
						String i10SelectedAnswer = questionAnsMap
								.get(OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID.I10
										.name());
						String i10AnsB = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B
								.name();
						if (i10SelectedAnswer != null && i10AnsB
								.equalsIgnoreCase(i10SelectedAnswer)) {
							String i11SelectedAnswer = questionAnsMap
									.get(OnboardingConstant.CONFIG_PARAM_INWARD_QUE_KEY_ID.I11
											.name());
							String i11AnsA = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A
									.name();
							if (i11SelectedAnswer != null && i11AnsA
									.equalsIgnoreCase(i11SelectedAnswer)) {
								String i9AnsA = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A
										.name();
								String i9AnsB = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B
										.name();
								String i9AnsC = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.C
										.name();
								String cgstin = document.getCgstin();
								Map<String, List<Pair<Integer, BigDecimal>>> iMap = document
										.getMasterItemMap();
								List<String> hsnSacList = new ArrayList<>();
								List<BigDecimal> rateList = new ArrayList<>();
								if (iMap != null && !iMap.isEmpty()) {
									List<Pair<Integer, BigDecimal>> detailList = iMap
											.get(cgstin);
									if (detailList != null
											&& !detailList.isEmpty()) {
										detailList.forEach(detail -> {
											Integer hsnSac = detail.getValue0();
											String hsnSacStr = null;
											if (hsnSac != null) {
												hsnSacStr = Integer
														.toString(hsnSac);
											}
											BigDecimal rate = detail
													.getValue1();
											if (hsnSacStr != null) {
												hsnSacList.add(hsnSacStr);
											}
											if (rate != null) {
												BigDecimal rateRnd = rate
														.setScale(2,
																BigDecimal.ROUND_HALF_EVEN);
												rateList.add(rateRnd);
											}
										});
									}
								}
								// Add only if values of hsnSac,rate from req
								// payload do not exist in MasterItem Table
								document.getLineItems().forEach(lineItem -> {
									MasterItemEntity entity = new MasterItemEntity();
									entity.setGstinPan(cgstin);
									entity.setItmCode(lineItem.getItemCode());
									entity.setItemDesc(
											lineItem.getItemDescription());
									entity.setUom(lineItem.getUom());
									entity.setReverseChargeFlag(
											document.getReverseCharge());
									entity.setDiffPercent(
											document.getDiffPercent());
									entity.setItmCategory(
											lineItem.getItemCategory());
									entity.setReverseChargeFlag(
											document.getReverseCharge());
									entity.setDiffPercent(
											document.getDiffPercent());
									entity.setEntityId(document.getEntityId());
									String nilOrNonOrExmt = null;
									if (GSTConstants.NIL.equalsIgnoreCase(
											document.getSupplyType())
											|| GSTConstants.NON
													.equalsIgnoreCase(
															document.getSupplyType())
											|| GSTConstants.EXMPT_SUPPLY_TYPE
													.equalsIgnoreCase(document
															.getSupplyType())) {
										nilOrNonOrExmt = document
												.getSupplyType();
									}
									entity.setNilOrNonOrExmt(nilOrNonOrExmt);
									entity.setItemKey(cgstin);
									entity.setElgblIndicator(
											lineItem.getEligibilityIndicator());
									entity.setCommonSuppIndicator(lineItem
											.getCommonSupplyIndicator());
									entity.setItcReversalIdentifier(lineItem
											.getItcReversalIdentifier());
									entity.setItcsEntitlement(
											lineItem.getItcEntitlement());
									if (i9AnsA.equalsIgnoreCase(
											i9SelectedAnswer)) {
										if (hsnSacList
												.contains(lineItem.getHsnSac())
												&& iMap.containsKey(cgstin)) {
											return;
										} else {
											try {
												if (lineItem
														.getHsnSac() != null) {
													entity.setHsnOrSac(Integer
															.parseInt(lineItem
																	.getHsnSac()));
												}
											} catch (NumberFormatException e) {
												LOGGER.error("HSN SAC "
														+ lineItem.getHsnSac()
														+ " of Line Item "
														+ lineItem.getLineNo()
														+ " of Document Key "
														+ document.getDocKey()
														+ " is not a valid "
														+ "integer number ");
												throw e;
											}
										}
									}
									if (i9AnsB.equalsIgnoreCase(
											i9SelectedAnswer)) {
										BigDecimal itmRate = lineItem
												.getTaxRate();
										if(itmRate != null){
										BigDecimal itmRateRnd = itmRate
												.setScale(2,
														BigDecimal.ROUND_HALF_EVEN);
										if (rateList.contains(itmRateRnd)
												&& iMap.containsKey(cgstin)) {
											return;
										} else {
											entity.setRate(itmRateRnd);
										}
										}
									}
									if (i9AnsC.equalsIgnoreCase(
											i9SelectedAnswer)) {
										BigDecimal itmRate = lineItem
												.getTaxRate();
										if(itmRate != null){
										BigDecimal itmRateRnd = itmRate
												.setScale(2,
														BigDecimal.ROUND_HALF_EVEN);
										if (hsnSacList
												.contains(lineItem.getHsnSac())
												&& rateList.contains(itmRateRnd)
												&& iMap.containsKey(cgstin)) {
											return;
										} else {
											if (lineItem.getHsnSac() != null) {
												entity.setHsnOrSac(Integer
														.parseInt(lineItem
																.getHsnSac()));
											}
											entity.setRate(itmRateRnd);
										}
									  }
									}
									masterItemEntities.add(entity);
								});
							}
						}
					}
				}
			});
			if (LOGGER.isDebugEnabled()) {
				int size = masterItemEntities.size();
				LOGGER.debug("masterItemEntities SIZE " + size);
				if (masterItemEntities != null
						&& !masterItemEntities.isEmpty()) {
					masterItemEntities.forEach(mpEntity -> {
						String gstin = mpEntity.getGstinPan();
						Long entityId = mpEntity.getEntityId();
						Integer hsnSac = mpEntity.getHsnOrSac();
						BigDecimal rate = mpEntity.getRate();
						LOGGER.debug("MasterItem Additional Data: gstin - "
								+ gstin + " entityId - " + entityId + " hsnsac "
								+ hsnSac + " rate " + rate);
					});
				}
			}
			if (masterItemEntities != null && !masterItemEntities.isEmpty()) {
				masterItemRepo.saveAll(masterItemEntities);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("End : addAdditionalMasterItemData for Group Code "
						+ TenantContext.getTenantId());
			}
		} catch (RuntimeException e) {
			LOGGER.error("For Group Code " + TenantContext.getTenantId()
					+ " Exception in Add Additional Master Item  Data " + e);
			throw e;
		}
	}
}
