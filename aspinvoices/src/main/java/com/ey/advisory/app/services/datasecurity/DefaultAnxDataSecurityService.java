package com.ey.advisory.app.services.datasecurity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.docs.dto.AnxDataSecAttrItemDto;
import com.ey.advisory.app.docs.dto.AnxDataSecurityDto;
import com.ey.advisory.app.docs.dto.DataSecDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.SecurityContext;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Service("DefaultAnxDataSecurityService")
public class DefaultAnxDataSecurityService implements AnxDataSecurityService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultAnxDataSecurityService.class);

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Override
	public List<AnxDataSecurityDto> getAnxDataSecurityAttributes() {
		List<AnxDataSecurityDto> respList = new ArrayList<>();

		User user = SecurityContext.getUser();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("User Id " + user.getUserPrincipalName());
		}
		// List<AnxDataSecurityDto> respList = new ArrayList<>();
		Map<Long, List<Quartet<String, String, String, String>>> entityMap = user.getEntityMap();
		if (entityMap != null && !entityMap.isEmpty()) {
			Set<Long> entityIds = entityMap.keySet();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Entity Ids " + entityIds);
			}

			if (entityIds != null && !entityIds.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Entity Ids not empty " + entityIds);
				}
				List<EntityInfoEntity> entityList = entityInfoRepository
						.findByEntityIds(entityIds);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("entityList " + entityList);
				}
				if (entityList != null && !entityList.isEmpty()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("entityList not empty or not null");
					}
					entityList.forEach(entity -> {
						AnxDataSecurityDto anxDataSecDto = new AnxDataSecurityDto();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Entity Id " + entity.getId());
						}
						anxDataSecDto.setEntityId(entity.getId());
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Entity Name " + entity.getEntityName());
						}
						anxDataSecDto.setEntityName(entity.getEntityName());

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("RespList after adding entities"
									+ respList);
						}
						List<Quartet<String,String,String,String>> applicableAttrs = user
								.getApplicableAttrs(entity.getId());
						if (applicableAttrs != null
								&& !applicableAttrs.isEmpty()) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"applicableAttrs is not null or empty");
							}
							for (Quartet<String,String,String,String> applicableAttr : applicableAttrs) {
								String attrCode = applicableAttr.getValue0();
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug("attrCode " + attrCode);
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.GSTIN)) {
									List<Pair<Long, String>> attrValuesForGstin = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForGstin "
												+ attrValuesForGstin);
									}
									if (attrValuesForGstin != null
											&& !attrValuesForGstin.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug(
													"attrValuesForGstin is "
															+ "not null or empty");
										}
										List<DataSecDto> gstinList = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForGstin) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug(
														"attrValuesForGstin "
																+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											gstinList.add(dataSec);
										}
										anxDataSecDto.setGstin(gstinList);
									}
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.PC)) {
									List<Pair<Long, String>> attrValuesForPC = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForPC "
												+ attrValuesForPC);
									}
									if (attrValuesForPC != null
											&& !attrValuesForPC.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug("attrValuesForPC is "
													+ "not null or empty");
										}
										List<DataSecDto> pcList = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForPC) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("attrValueForPC "
														+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											pcList.add(dataSec);
										}
										anxDataSecDto.setProfitCenter(pcList);
									}
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.PLANT)) {

									List<Pair<Long, String>> attrValuesForPlant = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForPlant "
												+ attrValuesForPlant);
									}
									if (attrValuesForPlant != null
											&& !attrValuesForPlant.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug(
													"attrValuesForPlant is "
															+ "not null or empty");
										}
										List<DataSecDto> plantList = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForPlant) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("attrValueForPC "
														+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											plantList.add(dataSec);
										}
										anxDataSecDto.setPlant(plantList);
									}

								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.DIVISION)) {
									List<Pair<Long, String>> attrValuesForDiv = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForDivision "
												+ attrValuesForDiv);
									}
									if (attrValuesForDiv != null
											&& !attrValuesForDiv.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug(
													"attrValuesForDivision is "
															+ "not null or empty");
										}
										List<DataSecDto> divisionList = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForDiv) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug(
														"attrValueForDivision "
																+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											divisionList.add(dataSec);
										}
										anxDataSecDto.setDivision(divisionList);
									}
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.LOCATION)) {
									List<Pair<Long, String>> attrValuesForLoc = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForLocation "
												+ attrValuesForLoc);
									}
									if (attrValuesForLoc != null
											&& !attrValuesForLoc.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug(
													"attrValuesForLocation is "
															+ "not null or empty");
										}
										List<DataSecDto> locationList = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForLoc) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug(
														"attrValueForLocation "
																+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											locationList.add(dataSec);
										}
										anxDataSecDto.setLocation(locationList);
									}
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.SO)) {
									List<Pair<Long, String>> attrValuesForSO = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForSO "
												+ attrValuesForSO);
									}
									if (attrValuesForSO != null
											&& !attrValuesForSO.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug("attrValuesForSO is "
													+ "not null or empty");
										}
										List<DataSecDto> soList = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForSO) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("attrValueForSO "
														+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											soList.add(dataSec);
										}
										anxDataSecDto.setSalesOrg(soList);
									}
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.PO)) {
									List<Pair<Long, String>> attrValuesForPO = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForPO "
												+ attrValuesForPO);
									}
									if (attrValuesForPO != null
											&& !attrValuesForPO.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug("attrValuesForPO is "
													+ "not null or empty");
										}
										List<DataSecDto> poList = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForPO) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("attrValueForPO "
														+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											poList.add(dataSec);
										}
										anxDataSecDto.setPurchOrg(poList);
									}
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.DC)) {
									List<Pair<Long, String>> attrValuesForDC = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForDC "
												+ attrValuesForDC);
									}
									if (attrValuesForDC != null
											&& !attrValuesForDC.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug("attrValuesForDC is "
													+ "not null or empty");
										}
										List<DataSecDto> dcList = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForDC) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("attrValueForDC "
														+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											dcList.add(dataSec);
										}
										anxDataSecDto.setDistChannel(dcList);
									}
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.UD1)) {
									List<Pair<Long, String>> attrValuesForUD1 = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForUD1 "
												+ attrValuesForUD1);
									}
									if (attrValuesForUD1 != null
											&& !attrValuesForUD1.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug("attrValuesForUD1 is "
													+ "not null or empty");
										}
										List<DataSecDto> ud1List = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForUD1) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("attrValueForUD1 "
														+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											ud1List.add(dataSec);
										}
										anxDataSecDto.setUserAccess1(ud1List);
									}
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.UD2)) {
									List<Pair<Long, String>> attrValuesForUD2 = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForUD2 "
												+ attrValuesForUD2);
									}
									if (attrValuesForUD2 != null
											&& !attrValuesForUD2.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug("attrValuesForUD2 is "
													+ "not null or empty");
										}
										List<DataSecDto> ud2List = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForUD2) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("attrValueForUD2 "
														+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											ud2List.add(dataSec);
										}
										anxDataSecDto.setUserAccess2(ud2List);
									}
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.UD3)) {
									List<Pair<Long, String>> attrValuesForUD3 = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForUD3 "
												+ attrValuesForUD3);
									}
									if (attrValuesForUD3 != null
											&& !attrValuesForUD3.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug("attrValuesForUD3 is "
													+ "not null or empty");
										}
										List<DataSecDto> ud3List = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForUD3) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("attrValueForUD3 "
														+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											ud3List.add(dataSec);
										}
										anxDataSecDto.setUserAccess3(ud3List);
									}
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.UD4)) {
									List<Pair<Long, String>> attrValuesForUD4 = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForUD4 "
												+ attrValuesForUD4);
									}
									if (attrValuesForUD4 != null
											&& !attrValuesForUD4.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug("attrValuesForUD4 is "
													+ "not null or empty");
										}
										List<DataSecDto> ud4List = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForUD4) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("attrValueForUD4 "
														+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											ud4List.add(dataSec);
										}
										anxDataSecDto.setUserAccess4(ud4List);
									}
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.UD5)) {
									List<Pair<Long, String>> attrValuesForUD5 = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForUD5 "
												+ attrValuesForUD5);
									}
									if (attrValuesForUD5 != null
											&& !attrValuesForUD5.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug("attrValuesForUD5 is "
													+ "not null or empty");
										}
										List<DataSecDto> ud5List = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForUD5) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("attrValueForUD5 "
														+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											ud5List.add(dataSec);
										}
										anxDataSecDto.setUserAccess5(ud5List);
									}
								}

								if (attrCode.equalsIgnoreCase(
										OnboardingConstant.UD6)) {
									List<Pair<Long, String>> attrValuesForUD6 = user
											.getAttrValuesForAttrCode(
													entity.getId(), attrCode);
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("attrValuesForUD6 "
												+ attrValuesForUD6);
									}
									if (attrValuesForUD6 != null
											&& !attrValuesForUD6.isEmpty()) {
										if (LOGGER.isDebugEnabled()) {
											LOGGER.debug("attrValuesForUD6 is "
													+ "not null or empty");
										}
										List<DataSecDto> ud6List = new ArrayList<>();
										for (Pair<Long, String> attrValue : attrValuesForUD6) {
											String attrVal = attrValue
													.getValue1();
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("attrValueForUD6 "
														+ attrVal);
											}
											DataSecDto dataSec = new DataSecDto();
											dataSec.setValue(attrVal);
											ud6List.add(dataSec);
										}
										anxDataSecDto.setUserAccess6(ud6List);
									}
								}
							}
						}
						AnxDataSecAttrItemDto item = new AnxDataSecAttrItemDto();
						item.setProfitCenter(true);
						item.setDistChannel(true);
						item.setDivision(true);
						item.setLocation(true);
						item.setPlant(true);
						anxDataSecDto.setItems(item);
						respList.add(anxDataSecDto);
					});
				}
			}
		}
		return respList;
	}

	@Override
	public AnxDataSecurityDto getAnxDataSecurityApplAttributes(Long entityId) {

		AnxDataSecurityDto anxDataSecDto = new AnxDataSecurityDto();
		User user = SecurityContext.getUser();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entered getAnxDataSecurityApplAttributes");
			LOGGER.debug("Applicable Attributes for User Id "
					+ user.getUserPrincipalName() + "Entity Id " + entityId);
		}
		List<Quartet<String, String,String, String>> applicableAttrs = user
				.getApplicableAttrs(entityId);
		if (applicableAttrs != null && !applicableAttrs.isEmpty()) {
			AnxDataSecAttrItemDto item = new AnxDataSecAttrItemDto();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("applicableAttrs is not null or empty");
			}
			for (Quartet<String, String,String, String> applicableAttr : applicableAttrs) {
				String attrCode = applicableAttr.getValue0();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("attrCode " + attrCode);
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					List<Pair<Long, String>> attrValuesForGstin = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"attrValuesForGstin " + attrValuesForGstin);
					}
					if (attrValuesForGstin != null
							&& !attrValuesForGstin.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForGstin is "
									+ "not null or empty");
						}
						List<DataSecDto> gstinList = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForGstin) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValuesForGstin " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							gstinList.add(dataSec);
						}
						// Sort the list in Ascending Order
						Collections.sort(gstinList,
								new Comparator<DataSecDto>() {
									@Override
									public int compare(DataSecDto gstin1,
											DataSecDto gstin2) {
										return gstin1.getValue()
												.compareTo(gstin2.getValue());
									}
								});
						anxDataSecDto.setGstin(gstinList);
					}
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.PC)) {
					List<Pair<Long, String>> attrValuesForPC = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("attrValuesForPC " + attrValuesForPC);
					}
					if (attrValuesForPC != null && !attrValuesForPC.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForPC is "
									+ "not null or empty");
						}
						List<DataSecDto> pcList = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForPC) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForPC " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							pcList.add(dataSec);
						}
						if (!pcList.isEmpty()) {
							item.setProfitCenter(true);
						}
						anxDataSecDto.setProfitCenter(pcList);
					}
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.PLANT)) {
					List<Pair<Long, String>> attrValuesForPlant = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"attrValuesForPlant " + attrValuesForPlant);
					}
					if (attrValuesForPlant != null
							&& !attrValuesForPlant.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForPlant is "
									+ "not null or empty");
						}
						List<DataSecDto> plantList = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForPlant) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForPC " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							plantList.add(dataSec);
						}
						if (!plantList.isEmpty()) {
							item.setPlant(true);
						}
						anxDataSecDto.setPlant(plantList);
					}

				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					List<Pair<Long, String>> attrValuesForDiv = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"attrValuesForDivision " + attrValuesForDiv);
					}
					if (attrValuesForDiv != null
							&& !attrValuesForDiv.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForDivision is "
									+ "not null or empty");
						}
						List<DataSecDto> divisionList = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForDiv) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForDivision " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							divisionList.add(dataSec);
						}
						if (!divisionList.isEmpty()) {
							item.setDivision(true);
						}
						anxDataSecDto.setDivision(divisionList);
					}
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					List<Pair<Long, String>> attrValuesForLoc = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"attrValuesForLocation " + attrValuesForLoc);
					}
					if (attrValuesForLoc != null
							&& !attrValuesForLoc.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForLocation is "
									+ "not null or empty");
						}
						List<DataSecDto> locationList = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForLoc) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForLocation " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							locationList.add(dataSec);
						}
						if (!locationList.isEmpty()) {
							item.setLocation(true);
						}
						anxDataSecDto.setLocation(locationList);
					}
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.SO)) {
					List<Pair<Long, String>> attrValuesForSO = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("attrValuesForSO " + attrValuesForSO);
					}
					if (attrValuesForSO != null && !attrValuesForSO.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForSO is "
									+ "not null or empty");
						}
						List<DataSecDto> soList = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForSO) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForSO " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							soList.add(dataSec);
						}
						if (!soList.isEmpty()) {
							item.setSalesOrg(true);
						}
						anxDataSecDto.setSalesOrg(soList);
					}
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.PO)) {
					List<Pair<Long, String>> attrValuesForPO = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("attrValuesForPO " + attrValuesForPO);
					}
					if (attrValuesForPO != null && !attrValuesForPO.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForPO is "
									+ "not null or empty");
						}
						List<DataSecDto> poList = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForPO) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForPO " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							poList.add(dataSec);
						}
						if (!poList.isEmpty()) {
							item.setPurchOrg(true);
						}
						anxDataSecDto.setPurchOrg(poList);
					}
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.DC)) {
					List<Pair<Long, String>> attrValuesForDC = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("attrValuesForDC " + attrValuesForDC);
					}
					if (attrValuesForDC != null && !attrValuesForDC.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForDC is "
									+ "not null or empty");
						}
						List<DataSecDto> dcList = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForDC) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForDC " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							dcList.add(dataSec);
						}
						if (!dcList.isEmpty()) {
							item.setDistChannel(true);
						}
						anxDataSecDto.setPurchOrg(dcList);
					}
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.UD1)) {
					List<Pair<Long, String>> attrValuesForUD1 = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("attrValuesForUD1 " + attrValuesForUD1);
					}
					if (attrValuesForUD1 != null
							&& !attrValuesForUD1.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForUD1 is "
									+ "not null or empty");
						}
						List<DataSecDto> ud1List = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForUD1) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForUD1 " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							ud1List.add(dataSec);
						}
						if (!ud1List.isEmpty()) {
							item.setUserAccess1(true);
						}
						anxDataSecDto.setUserAccess1(ud1List);
					}
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.UD2)) {
					List<Pair<Long, String>> attrValuesForUD2 = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("attrValuesForUD2 " + attrValuesForUD2);
					}
					if (attrValuesForUD2 != null
							&& !attrValuesForUD2.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForUD2 is "
									+ "not null or empty");
						}
						List<DataSecDto> ud2List = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForUD2) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForUD2 " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							ud2List.add(dataSec);
						}
						if (!ud2List.isEmpty()) {
							item.setUserAccess2(true);
						}
						anxDataSecDto.setUserAccess2(ud2List);
					}
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.UD3)) {
					List<Pair<Long, String>> attrValuesForUD3 = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("attrValuesForUD3 " + attrValuesForUD3);
					}
					if (attrValuesForUD3 != null
							&& !attrValuesForUD3.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForUD3 is "
									+ "not null or empty");
						}
						List<DataSecDto> ud3List = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForUD3) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForUD3 " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							ud3List.add(dataSec);
						}
						if (!ud3List.isEmpty()) {
							item.setUserAccess3(true);
						}
						anxDataSecDto.setUserAccess3(ud3List);
					}
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.UD4)) {
					List<Pair<Long, String>> attrValuesForUD4 = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("attrValuesForUD4 " + attrValuesForUD4);
					}
					if (attrValuesForUD4 != null
							&& !attrValuesForUD4.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForUD4 is "
									+ "not null or empty");
						}
						List<DataSecDto> ud4List = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForUD4) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForUD4 " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							ud4List.add(dataSec);
						}
						if (!ud4List.isEmpty()) {
							item.setUserAccess4(true);
						}
						anxDataSecDto.setUserAccess4(ud4List);
					}
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.UD5)) {
					List<Pair<Long, String>> attrValuesForUD5 = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("attrValuesForUD5 " + attrValuesForUD5);
					}
					if (attrValuesForUD5 != null
							&& !attrValuesForUD5.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForUD5 is "
									+ "not null or empty");
						}
						List<DataSecDto> ud5List = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForUD5) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForUD5 " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							ud5List.add(dataSec);
						}
						if (!ud5List.isEmpty()) {
							item.setUserAccess5(true);
						}
						anxDataSecDto.setUserAccess5(ud5List);
					}
				}

				if (attrCode.equalsIgnoreCase(OnboardingConstant.UD6)) {
					List<Pair<Long, String>> attrValuesForUD6 = user
							.getAttrValuesForAttrCode(entityId, attrCode);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("attrValuesForUD6 " + attrValuesForUD6);
					}
					if (attrValuesForUD6 != null
							&& !attrValuesForUD6.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("attrValuesForUD6 is "
									+ "not null or empty");
						}
						List<DataSecDto> ud6List = new ArrayList<>();
						for (Pair<Long, String> attrValue : attrValuesForUD6) {
							String attrVal = attrValue.getValue1();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("attrValueForUD6 " + attrVal);
							}
							DataSecDto dataSec = new DataSecDto();
							dataSec.setValue(attrVal);
							ud6List.add(dataSec);
						}
						if (!ud6List.isEmpty()) {
							item.setUserAccess6(true);
						}
						anxDataSecDto.setUserAccess6(ud6List);
					}
				}
			}
			anxDataSecDto.setItems(item);
		}
		return anxDataSecDto;
	}

}
