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
import com.ey.advisory.admin.data.entities.client.MasterCustomerEntity;
import com.ey.advisory.admin.data.entities.client.MasterProductEntity;
import com.ey.advisory.admin.data.repositories.client.MasterCustomerRepository;
import com.ey.advisory.admin.data.repositories.client.MasterProductRepository;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.common.AspInvoiceStatus;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AOutwardAdditionalMasterDataAddition")
public class Gstr1AOutwardAdditionalMasterDataAddition {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AOutwardAdditionalMasterDataAddition.class);

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck configParamCheck;

	@Autowired
	@Qualifier("masterCustomerRepository")
	private MasterCustomerRepository masterCustomerRepo;

	@Autowired
	@Qualifier("masterProductRepository")
	private MasterProductRepository masterProductRepo;

	public void addAdditionalMasterData(
			List<Gstr1AOutwardTransDocument> documents) {
		// Add Additional Master Customer Data - On Boarding Questions - Q10,Q11
		addAdditionalMasterCustomerData(documents);
		// Add Additional Master Product Data - On Boarding Questions -
		// Q16,17,18
		addAdditionalMasterProductData(documents);
	}

	private void addAdditionalMasterCustomerData(
			List<Gstr1AOutwardTransDocument> documents) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Begin : addAdditionalMasterCustomerData for Group Code "
							+ TenantContext.getTenantId());
		}
		try {
			Set<MasterCustomerEntity> masterCustEntities = new HashSet<>();
			documents.forEach(document -> {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("does Customer GSTIN exists "
							+ "in Master Customer Table "
							+ document.getIsCgstInMasterCust()
							+ ", Cgstin in document req " + document.getCgstin()
							+ " of Outward Document Key " + document.getDocKey()
							+ " for Group Code " + TenantContext.getTenantId());
				}
				if (document
						.getAspInvoiceStatus() == AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode()) {
					if (document.getCgstin() != null
							&& !document.getCgstin().isEmpty()
							&& document.getCgstin().length() == 15) {
						if ((document.getCgstin() != null
								&& !document.getCgstin().trim().isEmpty())
								&& !(document.getIsCgstInMasterCust())) {
							Long entityId = document.getEntityId();
							Map<Long, List<EntityConfigPrmtEntity>> map = document
									.getEntityConfigParamMap();
							Map<String, String> questionAnsMap = configParamCheck
									.getQuestionAndAnswerMap(entityId, map);
							String o6SelectedAnswer = questionAnsMap
									.get(OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O6
											.name());
							String o6AnsB = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B
									.name();
							if (o6AnsB.equalsIgnoreCase(o6SelectedAnswer)) {
								String o7SelectedAnswer = questionAnsMap
										.get(OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O7
												.name());
								String o7AnsA = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A
										.name();
								if (o7AnsA.equalsIgnoreCase(o7SelectedAnswer)) {
									MasterCustomerEntity mcEntity = new MasterCustomerEntity();
									mcEntity.setRecipientGstnOrPan(
											document.getCgstin());
									mcEntity.setLegalName(
											document.getCustOrSuppName());
									mcEntity.setRecipientType(
											document.getCustOrSuppType());
									mcEntity.setRecipientCode(
											document.getCustOrSuppCode());
									mcEntity.setOutSideIndia(GSTConstants.N);
									mcEntity.setCustKey(document.getCgstin());
									mcEntity.setEntityId(
											document.getEntityId());
									masterCustEntities.add(mcEntity);
								}
							}
						}
					}
				}
			});
			if (LOGGER.isDebugEnabled()) {
				int size = masterCustEntities.size();
				LOGGER.debug("masterCustomerEntities SIZE " + size);
				if (masterCustEntities != null
						&& !masterCustEntities.isEmpty()) {
					masterCustEntities.forEach(mcEntity -> {
						String gstin = mcEntity.getRecipientGstnOrPan();
						Long entityId = mcEntity.getEntityId();
						LOGGER.debug("MasterCustomer Additional Data: gstin - "
								+ gstin + " entityId - " + entityId);
					});
				}
			}
			if (masterCustEntities != null && !masterCustEntities.isEmpty()) {
				masterCustomerRepo.saveAll(masterCustEntities);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"End : addAdditionalMasterCustomerData for Group Code "
								+ TenantContext.getTenantId());
			}
		} catch (RuntimeException e) {
			LOGGER.error("For Group Code " + TenantContext.getTenantId()
					+ " Exception in Add Additional Master Customer  Data "
					+ e);
			throw e;
		}
	}

	private void addAdditionalMasterProductData(
			List<Gstr1AOutwardTransDocument> documents) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Begin : addAdditionalMasterProductData for Group Code "
							+ TenantContext.getTenantId());
		}
		try {
			Set<MasterProductEntity> masterProdEntities = new HashSet<>();
			documents.forEach(document -> {
				if (document
						.getAspInvoiceStatus() == AspInvoiceStatus.ASP_PROCESSED
								.getAspInvoiceStatusCode()) {
					Map<Long, List<EntityConfigPrmtEntity>> map = document
							.getEntityConfigParamMap();
					Map<String, String> questionAnsMap = configParamCheck
							.getQuestionAndAnswerMap(document.getEntityId(),
									map);
					String o8SelectedAnswer = questionAnsMap
							.get(OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O8
									.name());
					String o8AnsD = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.D.name();
					if (o8SelectedAnswer != null
							&& !o8AnsD.equalsIgnoreCase(o8SelectedAnswer)) {
						String o9SelectedAnswer = questionAnsMap
								.get(OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O9
										.name());
						String o9AnsB = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B
								.name();
						if (o9SelectedAnswer != null
								&& o9AnsB.equalsIgnoreCase(o9SelectedAnswer)) {
							String o10SelectedAnswer = questionAnsMap
									.get(OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O10
											.name());
							String o10AnsA = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A
									.name();
							if (o10SelectedAnswer != null && o10AnsA
									.equalsIgnoreCase(o10SelectedAnswer)) {
								String o8AnsA = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.A
										.name();
								String o8AnsB = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B
										.name();
								String o8AnsC = CONFIG_PARAM_OUTWARD_ANS_KEY_ID.C
										.name();
								if (document.getSgstin() != null
										&& !document.getSgstin().isEmpty()
										&& document.getSgstin()
												.length() == 15) {
									String sgstin = document.getSgstin();
									Map<String, List<Pair<Integer, BigDecimal>>> pMap = document
											.getMasterProductMap();
									List<String> hsnSacList = new ArrayList<>();
									List<BigDecimal> rateList = new ArrayList<>();
									if (pMap != null && !pMap.isEmpty()) {
										List<Pair<Integer, BigDecimal>> detailList = pMap
												.get(sgstin);
										if (detailList != null
												&& !detailList.isEmpty()) {
											detailList.forEach(detail -> {
												Integer hsnSac = detail
														.getValue0();
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
									// Add only if values of hsnSac,rate from
									// req
									// payload do not exist in MasterProduct
									// Table
									document.getLineItems()
											.forEach(lineItem -> {
												MasterProductEntity entity = new MasterProductEntity();
												entity.setGstinPan(sgstin);
												entity.setProductCode(
														lineItem.getItemCode());
												entity.setProductDesc(lineItem
														.getItemDescription());
												entity.setProductCategory(
														lineItem.getItemCategory());
												entity.setUom(
														lineItem.getUom());
												entity.setReverseChargeFlag(
														document.getReverseCharge());
												entity.setDiffPercent(document
														.getDiffPercent());
												entity.setEntityId(
														document.getEntityId());
												entity.setProdKey(sgstin);
												String nilOrNonOrExmt = null;
												if (GSTConstants.NIL
														.equalsIgnoreCase(
																document.getSupplyType())
														|| GSTConstants.NON
																.equalsIgnoreCase(
																		document.getSupplyType())
														|| GSTConstants.EXMPT_SUPPLY_TYPE
																.equalsIgnoreCase(
																		document.getSupplyType())) {
													nilOrNonOrExmt = document
															.getSupplyType();
												}
												entity.setNilOrNonOrExmt(
														nilOrNonOrExmt);
												entity.setItcFlag(
														lineItem.getItcFlag());
												if (o8AnsA.equalsIgnoreCase(
														o8SelectedAnswer)) {
													if (hsnSacList.contains(
															lineItem.getHsnSac())
															&& pMap.containsKey(
																	sgstin)) {
														return;
													} else {
														try {
															entity.setHsnOrSac(
																	Integer.parseInt(
																			lineItem.getHsnSac()));
														} catch (NumberFormatException e) {
															LOGGER.error(
																	"HSN SAC "
																			+ lineItem
																					.getHsnSac()
																			+ " of Line Item "
																			+ lineItem
																					.getLineNo()
																			+ " of Document Key "
																			+ document
																					.getDocKey()
																			+ " is not a valid "
																			+ "integer number ");
															throw e;
														}
													}
												}
												if (o8AnsB.equalsIgnoreCase(
														o8SelectedAnswer)) {
													BigDecimal itmRate = lineItem
															.getTaxRate();
													if (itmRate != null) {
														BigDecimal itmRateRnd = itmRate
																.setScale(2,
																		BigDecimal.ROUND_HALF_EVEN);
														if (rateList.contains(
																itmRateRnd)
																&& pMap.containsKey(
																		sgstin)) {
															return;
														} else {
															entity.setRate(
																	itmRateRnd);
														}
													}
												}
												if (o8AnsC.equalsIgnoreCase(
														o8SelectedAnswer)) {
													BigDecimal itmRate = lineItem
															.getTaxRate();
													if (itmRate != null) {
														BigDecimal itmRateRnd = itmRate
																.setScale(2,
																		BigDecimal.ROUND_HALF_EVEN);
														if (hsnSacList.contains(
																lineItem.getHsnSac())
																&& rateList
																		.contains(
																				itmRateRnd)
																&& pMap.containsKey(
																		sgstin)) {
															return;
														} else {
															entity.setHsnOrSac(
																	Integer.parseInt(
																			lineItem.getHsnSac()));
															entity.setRate(
																	itmRateRnd);
														}
													}
												}
												masterProdEntities.add(entity);
											});
								}
							}
						}
					}
				}
			});
			if (LOGGER.isDebugEnabled()) {
				int size = masterProdEntities.size();
				LOGGER.debug("masterProdEntities SIZE " + size);
				if (masterProdEntities != null
						&& !masterProdEntities.isEmpty()) {
					masterProdEntities.forEach(mpEntity -> {
						String gstin = mpEntity.getGstinPan();
						Long entityId = mpEntity.getEntityId();
						Integer hsnSac = mpEntity.getHsnOrSac();
						BigDecimal rate = mpEntity.getRate();
						LOGGER.debug("MasterProduct Additional Data: gstin - "
								+ gstin + " entityId - " + entityId + " hsnsac "
								+ hsnSac + " rate " + rate);
					});
				}
			}
			if (masterProdEntities != null && !masterProdEntities.isEmpty()) {
				masterProductRepo.saveAll(masterProdEntities);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("End : addAdditionalMasterProductData for "
						+ "Group Code " + TenantContext.getTenantId());
			}
		} catch (RuntimeException e) {
			LOGGER.error("For Group Code " + TenantContext.getTenantId()
					+ " Exception in Add Additional Master Product  Data " + e);
			throw e;
		}
	}
}
