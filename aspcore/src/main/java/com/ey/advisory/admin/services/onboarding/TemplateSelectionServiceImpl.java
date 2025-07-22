package com.ey.advisory.admin.services.onboarding;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.BankDetailsEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.TemplateMappingEntity;
import com.ey.advisory.admin.data.entities.client.TemplateMasterEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.BankDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.TemplateMappingRepository;
import com.ey.advisory.admin.data.repositories.client.TemplateMasterRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.BankDetailsItemRespDto;
import com.ey.advisory.core.dto.BankDetailsReqDto;
import com.ey.advisory.core.dto.BankDetailsRespDto;
import com.ey.advisory.core.dto.GstinTemplateDto;
import com.ey.advisory.core.dto.TemplateDto;
import com.ey.advisory.core.dto.TemplateSelectionItemRespDto;
import com.ey.advisory.core.dto.TemplateSelectionReqDto;
import com.ey.advisory.core.dto.TemplateSelectionRespDto;

@Service("TemplateSelectionServiceImpl")
public class TemplateSelectionServiceImpl implements TemplateSelectionService {

	@Autowired
	@Qualifier("TemplateMasterRepository")
	private TemplateMasterRepository tempMasRepo;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("TemplateMappingRepository")
	private TemplateMappingRepository tempMapRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	@Autowired
	@Qualifier("TemplateMappingRepository")
	private TemplateMappingRepository templateMappingRepo;

	@Autowired
	@Qualifier("BankDetailsRepository")
	private BankDetailsRepository respository;

	public List<TemplateSelectionRespDto> getTemplateSelection(
			final TemplateSelectionReqDto tempSelcReqDto) {

		List<TemplateSelectionRespDto> tempSeclRespDtos = new ArrayList<>();

		List<EntityInfoEntity> entitInfoEntities = entityInfoDetailsRepository
				.findEntityInfoDetails(tempSelcReqDto.getGroupCode());
		List<Long> entities = new ArrayList<>();
		entitInfoEntities.forEach(entitInfoEntity -> {
			entities.add(entitInfoEntity.getId());
		});
		List<Object[]> tempMappingList = new ArrayList<>();

		if (!entities.isEmpty()) {
			tempMappingList = tempMapRepo.getTemplateMapping(entities);
		}
		Map<Long, Map<String, List<Long>>> templateMapping = new HashMap<>();
		if (tempMappingList != null && !tempMappingList.isEmpty()) {
			templateMapping = tempMappingList.stream()
					.collect(Collectors.groupingBy(obj -> (Long) obj[0],
							Collectors.groupingBy(obj -> (String) obj[1],
									Collectors.mapping(obj -> (Long) obj[2],
											Collectors.toList()))));
		}
		List<TemplateMasterEntity> tempateMasterList = tempMasRepo
				.getTemplateMasters();
		if (!entitInfoEntities.isEmpty()) {
			for (EntityInfoEntity entitInfoEntity : entitInfoEntities) {

				TemplateSelectionRespDto tempSeleRespDto = new TemplateSelectionRespDto();
				tempSeleRespDto.setEntityId(entitInfoEntity.getId());
				tempSeleRespDto.setEntityName(entitInfoEntity.getEntityName());
				tempSeleRespDto.setGroupI(entitInfoEntity.getGroupId());

				if (tempateMasterList != null && !tempateMasterList.isEmpty()) {
					Map<String, List<TemplateMasterEntity>> templateCodeMap = mapTemplateMaster(
							tempateMasterList);
					List<TemplateMasterEntity> goodsInvTaxList = templateCodeMap
							.get("G01");
					List<TemplateMasterEntity> goodsCDNoteList = templateCodeMap
							.get("G02");
					List<TemplateMasterEntity> serviceInvTaxList = templateCodeMap
							.get("S01");
					List<TemplateMasterEntity> serviceCDNoteList = templateCodeMap
							.get("S02");
					
					List<TemplateMasterEntity> isdDistributionList = templateCodeMap
							.get("G601");
					List<TemplateMasterEntity> crDistributionList = templateCodeMap
							.get("G602");
					List<TemplateMasterEntity> isdRedistributionList = templateCodeMap
							.get("G603");
					List<TemplateMasterEntity> crRedistributionList = templateCodeMap
							.get("G604");

					List<TemplateDto> tempGoodsTaxInvs = new ArrayList<>();
					goodsInvTaxList.forEach(goodsInvTax -> {
						TemplateDto templateDto = new TemplateDto();
						templateDto.setId(goodsInvTax.getId());
						templateDto.setTempType(goodsInvTax.getTemplateType());
						tempGoodsTaxInvs.add(templateDto);
					});

					List<TemplateDto> goodsCDNotes = new ArrayList<>();
					goodsCDNoteList.forEach(goodsCDNote -> {
						TemplateDto templateDto = new TemplateDto();
						templateDto.setId(goodsCDNote.getId());
						templateDto.setTempType(goodsCDNote.getTemplateType());
						goodsCDNotes.add(templateDto);
					});

					List<TemplateDto> serviceTaxInvs = new ArrayList<>();
					serviceInvTaxList.forEach(serviceInvTax -> {
						TemplateDto templateDto = new TemplateDto();
						templateDto.setId(serviceInvTax.getId());
						templateDto
								.setTempType(serviceInvTax.getTemplateType());
						serviceTaxInvs.add(templateDto);
					});
					List<TemplateDto> serviceCDNotes = new ArrayList<>();

					serviceCDNoteList.forEach(serviceCDNote -> {
						TemplateDto templateDto = new TemplateDto();
						templateDto.setId(serviceCDNote.getId());
						templateDto
								.setTempType(serviceCDNote.getTemplateType());
						serviceCDNotes.add(templateDto);
					});
					
					List<TemplateDto> isdDistribution = new ArrayList<>();
					isdDistributionList.forEach(isdResp -> {
						TemplateDto templateDto = new TemplateDto();
						templateDto.setId(isdResp.getId());
						templateDto.setTempType(isdResp.getTemplateType());
						isdDistribution.add(templateDto);
					});
					List<TemplateDto> crDistribution = new ArrayList<>();
					crDistributionList.forEach(crResp -> {
						TemplateDto templateDto = new TemplateDto();
						templateDto.setId(crResp.getId());
						templateDto.setTempType(crResp.getTemplateType());
						crDistribution.add(templateDto);
					});
					List<TemplateDto> isdReDistribution = new ArrayList<>();
					isdRedistributionList.forEach(isdResp -> {
						TemplateDto templateDto = new TemplateDto();
						templateDto.setId(isdResp.getId());
						templateDto.setTempType(isdResp.getTemplateType());
						isdReDistribution.add(templateDto);
					});
					List<TemplateDto> crReDistribution = new ArrayList<>();
					crRedistributionList.forEach(crResp -> {
						TemplateDto templateDto = new TemplateDto();
						templateDto.setId(crResp.getId());
						templateDto.setTempType(crResp.getTemplateType());
						crReDistribution.add(templateDto);
					});
					
						tempSeleRespDto.setGoodsTaxInv(tempGoodsTaxInvs);
					tempSeleRespDto.setGoodsCDNotes(goodsCDNotes);
					tempSeleRespDto.setServiceTaxInv(serviceTaxInvs);
					tempSeleRespDto.setServiceCDNotes(serviceCDNotes);
					tempSeleRespDto.setGstr6IsdDistribution(isdDistribution);
					tempSeleRespDto.setGstr6CRDistribution(crDistribution);
					tempSeleRespDto.setGstr6IsdReDistribution(isdReDistribution);
					tempSeleRespDto.setGstr6CRReDistribution(crReDistribution);
					
				}
				TemplateSelectionItemRespDto itemRespDto = new TemplateSelectionItemRespDto();
				Map<String, List<Long>> eachTempCode = templateMapping
						.get(entitInfoEntity.getId());
				if (eachTempCode != null && !eachTempCode.isEmpty()) {
					List<Long> goodTaxInvs = eachTempCode.get("G01");
					if (goodTaxInvs != null && !goodTaxInvs.isEmpty()) {
						goodTaxInvs.forEach(goodTaxInv -> {
							itemRespDto.setGoodsTaxInv(goodTaxInv);
						});
					}

					List<Long> goodCrDNotes = eachTempCode.get("G02");

					if (goodCrDNotes != null && !goodCrDNotes.isEmpty()) {
						goodCrDNotes.forEach(goodCrDNote -> {
							itemRespDto.setGoodsCDNotes(goodCrDNote);
						});
					}

					List<Long> serviceTaxInvs = eachTempCode.get("S01");

					if (serviceTaxInvs != null && !serviceTaxInvs.isEmpty()) {
						serviceTaxInvs.forEach(serviceTaxInv -> {
							itemRespDto.setServiceTaxInv(serviceTaxInv);
						});
					}
					List<Long> serviceCrDNotes = eachTempCode.get("S02");
					if (serviceCrDNotes != null && !serviceCrDNotes.isEmpty()) {
						serviceCrDNotes.forEach(serviceCrDNote -> {
							itemRespDto.setServiceCDNotes(serviceCrDNote);
						});
					}
					List<Long> isdDistribution = eachTempCode.get("G601");
					if (isdDistribution != null && !isdDistribution.isEmpty()) {
						isdDistribution.forEach(goodTaxInv -> {
							itemRespDto.setGstr6IsdDistribution(goodTaxInv);
						});
					}
					
					List<Long> crdistribution = eachTempCode.get("G602");
					if (crdistribution != null && !crdistribution.isEmpty()) {
						crdistribution.forEach(goodTaxInv -> {
							itemRespDto.setGstr6CRDistribution(goodTaxInv);
						});
					}
					
					List<Long> isdReDistribution = eachTempCode.get("G603");
					if (isdReDistribution != null && !isdReDistribution.isEmpty()) {
						isdReDistribution.forEach(goodTaxInv -> {
							itemRespDto.setGstr6IsdReDistribution(goodTaxInv);
						});
					}
					
					List<Long> crRedistribution = eachTempCode.get("G604");
					if (crRedistribution != null && !crRedistribution.isEmpty()) {
						crRedistribution.forEach(goodTaxInv -> {
							itemRespDto.setGstr6CRReDistribution(goodTaxInv);
						});
					}
					
				}
				tempSeleRespDto.setItem(itemRespDto);
				tempSeclRespDtos.add(tempSeleRespDto);
			}
		}
		return tempSeclRespDtos;
	}

	private Map<String, List<TemplateMasterEntity>> mapTemplateMaster(
			List<TemplateMasterEntity> tempMastEntities) {
		Map<String, List<TemplateMasterEntity>> mapTempMasterList = new HashMap<>();
		tempMastEntities.forEach(tempMastEntity -> {
			StringBuilder key = new StringBuilder();
			key.append(tempMastEntity.getTempateCode());

			String tempKey = key.toString();
			if (mapTempMasterList.containsKey(tempKey)) {
				List<TemplateMasterEntity> temMastars = mapTempMasterList
						.get(tempKey);
				temMastars.add(tempMastEntity);
				mapTempMasterList.put(tempKey, temMastars);
			} else {
				List<TemplateMasterEntity> temMastars = new ArrayList<>();
				temMastars.add(tempMastEntity);
				mapTempMasterList.put(tempKey, temMastars);
			}
		});

		return mapTempMasterList;
	}

	public void saveTemplateSelection(final TemplateSelectionReqDto reqDto) {
		List<Long> gstinIds = gstNDetailRepository
				.findgstinIdByEntityId(reqDto.getEntityId());
		List<TemplateMappingEntity> tempMapEntitis = new ArrayList<>();
		User user = SecurityContext.getUser();

		if (gstinIds != null && !gstinIds.isEmpty()) {
			gstinIds.forEach(gstinId -> {
				Long tempMapId = templateMappingRepo.getTemplateMappringId(
						reqDto.getEntityId(), gstinId, reqDto.getGoodsTaxInv());
				if (tempMapId == null) {
					TemplateMappingEntity tempMapEntity = new TemplateMappingEntity();

					tempMapEntity.setGroupId(reqDto.getGroupId());
					tempMapEntity.setEntityId(reqDto.getEntityId());
					tempMapEntity.setGstinId(gstinId);
					tempMapEntity.setTemplateId(reqDto.getGoodsTaxInv());
					TemplateMasterEntity tempMastEntity = tempMasRepo
							.getTempTypeAndCode(reqDto.getGoodsTaxInv());
					tempMapEntity
							.setTemplateCode(tempMastEntity.getTempateCode());
					tempMapEntity
							.setTemplateType(tempMastEntity.getTemplateType());
					tempMapEntity.setCreatedBy(user.getUserPrincipalName());
					tempMapEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromIST(LocalDateTime.now()));
					tempMapEntitis.add(tempMapEntity);
				}
			});

			gstinIds.forEach(gstinId -> {
				Long tempMapId = templateMappingRepo.getTemplateMappringId(
						reqDto.getEntityId(), gstinId, reqDto.getGoodsTaxInv());
				if (tempMapId == null) {
					TemplateMappingEntity tempMapEntity = new TemplateMappingEntity();
					tempMapEntity.setEntityId(reqDto.getEntityId());
					tempMapEntity.setGstinId(gstinId);
					tempMapEntity.setTemplateId(reqDto.getGoodsCDNotes());
					TemplateMasterEntity tempMastEntity = tempMasRepo
							.getTempTypeAndCode(reqDto.getGoodsCDNotes());
					tempMapEntity
							.setTemplateCode(tempMastEntity.getTempateCode());
					tempMapEntity
							.setTemplateType(tempMastEntity.getTemplateType());
					tempMapEntity.setCreatedBy(user.getUserPrincipalName());
					tempMapEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromIST(LocalDateTime.now()));
					tempMapEntitis.add(tempMapEntity);
				}
			});
			gstinIds.forEach(gstinId -> {
				Long tempMapId = templateMappingRepo.getTemplateMappringId(
						reqDto.getEntityId(), gstinId, reqDto.getGoodsTaxInv());
				if (tempMapId == null) {
					TemplateMappingEntity tempMapEntity = new TemplateMappingEntity();
					tempMapEntity.setEntityId(reqDto.getEntityId());
					tempMapEntity.setGstinId(gstinId);
					tempMapEntity.setTemplateId(reqDto.getServiceTaxInv());
					TemplateMasterEntity tempMastEntity = tempMasRepo
							.getTempTypeAndCode(reqDto.getServiceTaxInv());
					tempMapEntity
							.setTemplateCode(tempMastEntity.getTempateCode());
					tempMapEntity
							.setTemplateType(tempMastEntity.getTemplateType());
					tempMapEntity.setCreatedBy(user.getUserPrincipalName());
					tempMapEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromIST(LocalDateTime.now()));
					tempMapEntitis.add(tempMapEntity);
				}
			});
			gstinIds.forEach(gstinId -> {
				Long tempMapId = templateMappingRepo.getTemplateMappringId(
						reqDto.getEntityId(), gstinId, reqDto.getGoodsTaxInv());
				if (tempMapId == null) {
					TemplateMappingEntity tempMapEntity = new TemplateMappingEntity();
					tempMapEntity.setEntityId(reqDto.getEntityId());
					tempMapEntity.setGstinId(gstinId);
					tempMapEntity.setTemplateId(reqDto.getServiceCDNotes());
					TemplateMasterEntity tempMastEntity = tempMasRepo
							.getTempTypeAndCode(reqDto.getServiceCDNotes());
					tempMapEntity
							.setTemplateCode(tempMastEntity.getTempateCode());
					tempMapEntity
							.setTemplateType(tempMastEntity.getTemplateType());
					tempMapEntity.setCreatedBy(user.getUserPrincipalName());
					tempMapEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromIST(LocalDateTime.now()));
					tempMapEntitis.add(tempMapEntity);
				}
			});
			
			gstinIds.forEach(gstinId -> {
				Long tempMapId = templateMappingRepo.getTemplateMappringId(
						reqDto.getEntityId(), gstinId, reqDto.getGstr6IsdDistribution());
				if (tempMapId == null) {
					TemplateMappingEntity tempMapEntity = new TemplateMappingEntity();
					tempMapEntity.setEntityId(reqDto.getEntityId());
					tempMapEntity.setGstinId(gstinId);
					tempMapEntity.setTemplateId(reqDto.getGstr6IsdDistribution());
					TemplateMasterEntity tempMastEntity = tempMasRepo
							.getTempTypeAndCode(reqDto.getGstr6IsdDistribution());
					tempMapEntity
							.setTemplateCode(tempMastEntity.getTempateCode());
					tempMapEntity
							.setTemplateType(tempMastEntity.getTemplateType());
					tempMapEntity.setCreatedBy(user.getUserPrincipalName());
					tempMapEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromIST(LocalDateTime.now()));
					tempMapEntitis.add(tempMapEntity);
				}
			});
			
			gstinIds.forEach(gstinId -> {
				Long tempMapId = templateMappingRepo.getTemplateMappringId(
						reqDto.getEntityId(), gstinId, reqDto.getGstr6CRDistribution());
				if (tempMapId == null) {
					TemplateMappingEntity tempMapEntity = new TemplateMappingEntity();
					tempMapEntity.setEntityId(reqDto.getEntityId());
					tempMapEntity.setGstinId(gstinId);
					tempMapEntity.setTemplateId(reqDto.getGstr6CRDistribution());
					TemplateMasterEntity tempMastEntity = tempMasRepo
							.getTempTypeAndCode(reqDto.getGstr6CRDistribution());
					tempMapEntity
							.setTemplateCode(tempMastEntity.getTempateCode());
					tempMapEntity
							.setTemplateType(tempMastEntity.getTemplateType());
					tempMapEntity.setCreatedBy(user.getUserPrincipalName());
					tempMapEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromIST(LocalDateTime.now()));
					tempMapEntitis.add(tempMapEntity);
				}
			});
			
			gstinIds.forEach(gstinId -> {
				Long tempMapId = templateMappingRepo.getTemplateMappringId(
						reqDto.getEntityId(), gstinId, reqDto.getGstr6IsdReDistribution());
				if (tempMapId == null) {
					TemplateMappingEntity tempMapEntity = new TemplateMappingEntity();
					tempMapEntity.setEntityId(reqDto.getEntityId());
					tempMapEntity.setGstinId(gstinId);
					tempMapEntity.setTemplateId(reqDto.getGstr6IsdReDistribution());
					TemplateMasterEntity tempMastEntity = tempMasRepo
							.getTempTypeAndCode(reqDto.getGstr6IsdReDistribution());
					tempMapEntity
							.setTemplateCode(tempMastEntity.getTempateCode());
					tempMapEntity
							.setTemplateType(tempMastEntity.getTemplateType());
					tempMapEntity.setCreatedBy(user.getUserPrincipalName());
					tempMapEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromIST(LocalDateTime.now()));
					tempMapEntitis.add(tempMapEntity);
				}
			});
			
			gstinIds.forEach(gstinId -> {
				Long tempMapId = templateMappingRepo.getTemplateMappringId(
						reqDto.getEntityId(), gstinId, reqDto.getGstrCRReDistribution());
				if (tempMapId == null) {
					TemplateMappingEntity tempMapEntity = new TemplateMappingEntity();
					tempMapEntity.setEntityId(reqDto.getEntityId());
					tempMapEntity.setGstinId(gstinId);
					tempMapEntity.setTemplateId(reqDto.getGstrCRReDistribution());
					TemplateMasterEntity tempMastEntity = tempMasRepo
							.getTempTypeAndCode(reqDto.getGstrCRReDistribution());
					tempMapEntity
							.setTemplateCode(tempMastEntity.getTempateCode());
					tempMapEntity
							.setTemplateType(tempMastEntity.getTemplateType());
					tempMapEntity.setCreatedBy(user.getUserPrincipalName());
					tempMapEntity.setCreatedOn(EYDateUtil
							.toUTCDateTimeFromIST(LocalDateTime.now()));
					tempMapEntitis.add(tempMapEntity);
				}
			});
		}
		if (!tempMapEntitis.isEmpty()) {
			templateMappingRepo.saveAll(tempMapEntitis);
		}
	}

	@Override
	public List<BankDetailsRespDto> getBankDetails(BankDetailsReqDto reqDto) {
		List<BankDetailsRespDto> bankDetailsRespDtos = new ArrayList<>();
		List<EntityInfoEntity> entitInfoEntities = entityInfoDetailsRepository
				.findEntityInfoDetails(reqDto.getGroupCode());
		List<Long> entities = new ArrayList<>();
		entitInfoEntities.forEach(entitInfoEntity -> {
			entities.add(entitInfoEntity.getId());
		});

		List<Object[]> bankDetailsObjs = respository.getBankDetails(entities);

		List<BankDetailsItemRespDto> bankDetailsItems = new ArrayList<>();
		if (bankDetailsObjs != null && !bankDetailsObjs.isEmpty()) {
			bankDetailsObjs.forEach(bankDetailsObj -> {
				BankDetailsItemRespDto bankDetailsItem = new BankDetailsItemRespDto();
				bankDetailsItem.setEntityId(bankDetailsObj[0] != null
						? new Long(String.valueOf(bankDetailsObj[0])) : null);

				bankDetailsItem.setBankAcc(bankDetailsObj[1] != null
						? String.valueOf(bankDetailsObj[1]) : null);

				bankDetailsItem.setIfscCode(bankDetailsObj[2] != null
						? String.valueOf(bankDetailsObj[2]) : null);
				bankDetailsItem.setBeneficiary(bankDetailsObj[3] != null
						? String.valueOf(bankDetailsObj[3]) : null);
				LocalDate bankDueDate = bankDetailsObj[4] != null
						? (LocalDate) bankDetailsObj[4] : null;
				if (bankDueDate != null) {
					bankDetailsItem.setPaymentDueDate(
							EYDateUtil.toISTDateTimeFromUTC(bankDueDate));
				}
				bankDetailsItem.setPaymentTerms(bankDetailsObj[5] != null
						? String.valueOf(bankDetailsObj[5]) : null);
				bankDetailsItem.setPaymentInstruction(bankDetailsObj[6] != null
						? String.valueOf(bankDetailsObj[6]) : null);
				bankDetailsItem.setBankName(bankDetailsObj[7] != null
						? String.valueOf(bankDetailsObj[7]) : null);
				bankDetailsItem.setBankAddrs(bankDetailsObj[8] != null
						? String.valueOf(bankDetailsObj[8]) : null);
				
				bankDetailsItems.add(bankDetailsItem);
			});
		}

		Map<String, List<BankDetailsItemRespDto>> mapBankDetails = mapBankDetails(
				bankDetailsItems);
		entitInfoEntities.forEach(entitInfoEntity -> {
			BankDetailsRespDto bankDetailsRespDto = new BankDetailsRespDto();
			bankDetailsRespDto.setEntityId(entitInfoEntity.getId());
			bankDetailsRespDto.setEntityName(entitInfoEntity.getEntityName());
			List<Object[]> objs = gstNDetailRepository
					.getGsinIdName(entitInfoEntity.getId());
			List<GstinTemplateDto> gstins = new ArrayList<>();
			if (objs != null && !objs.toString().isEmpty()) {
				objs.forEach(obj -> {
					GstinTemplateDto gstin = new GstinTemplateDto();
					gstin.setId(obj[0] != null
							? new Long(String.valueOf(obj[0])) : null);
					gstin.setGstin(
							obj[1] != null ? String.valueOf(obj[1]) : null);
					gstins.add(gstin);
				});
			}
			bankDetailsRespDto.setGstins(gstins);
			List<BankDetailsItemRespDto> bankDetaiItemRespDtos = mapBankDetails
					.get(String.valueOf(entitInfoEntity.getId()));
			List<BankDetailsItemRespDto> items = new ArrayList<>();
			if (bankDetaiItemRespDtos != null
					&& !bankDetaiItemRespDtos.isEmpty()) {
				bankDetaiItemRespDtos.forEach(bankDetaiItemRespDto -> {
					BankDetailsItemRespDto itemRespDto = new BankDetailsItemRespDto();
					itemRespDto.setBankAcc(bankDetaiItemRespDto.getBankAcc());
					itemRespDto.setBeneficiary(
							bankDetaiItemRespDto.getBeneficiary());
					List<Long> gstinIds = respository.getGstinIdsByBankAccnt(
							entitInfoEntity.getId(),
							bankDetaiItemRespDto.getBankAcc());
					itemRespDto.setGstinIds(gstinIds);
					itemRespDto.setIfscCode(bankDetaiItemRespDto.getIfscCode());
					itemRespDto.setPaymentDueDate(
							bankDetaiItemRespDto.getPaymentDueDate());
					itemRespDto.setPaymentInstruction(
							bankDetaiItemRespDto.getPaymentInstruction());
					itemRespDto.setPaymentTerms(
							bankDetaiItemRespDto.getPaymentTerms());
					itemRespDto.setBankName(bankDetaiItemRespDto.getBankName());
					itemRespDto.setBankAddrs(bankDetaiItemRespDto.getBankAddrs());
					
					items.add(itemRespDto);
				});
			}
			bankDetailsRespDto.setItems(items);
			bankDetailsRespDtos.add(bankDetailsRespDto);
		});
		return bankDetailsRespDtos;
	}

	private Map<String, List<BankDetailsItemRespDto>> mapBankDetails(
			List<BankDetailsItemRespDto> respDtos) {
		Map<String, List<BankDetailsItemRespDto>> mapBankDetailsRespDto = new HashMap<>();
		respDtos.forEach(respDto -> {
			StringBuilder key = new StringBuilder();
			key.append(respDto.getEntityId());
			String entityIdKey = key.toString();
			if (mapBankDetailsRespDto.containsKey(entityIdKey)) {
				List<BankDetailsItemRespDto> bankDetailsEntities = mapBankDetailsRespDto
						.get(entityIdKey);
				bankDetailsEntities.add(respDto);
				mapBankDetailsRespDto.put(entityIdKey, bankDetailsEntities);
			} else {
				List<BankDetailsItemRespDto> bankDetailsEntities = new ArrayList<>();
				bankDetailsEntities.add(respDto);
				mapBankDetailsRespDto.put(entityIdKey, bankDetailsEntities);
			}
		});
		return mapBankDetailsRespDto;
	}

	@Override
	public void saveBankDetails(final List<BankDetailsReqDto> reqDtos) {
		List<BankDetailsEntity> bankDetailsEntities = new ArrayList<>();
		reqDtos.forEach(reqDto -> {
			List<Long> gstinIds = reqDto.getGstinId();
			gstinIds.forEach(gstinId -> {
				BankDetailsEntity bankDetailsEntity = new BankDetailsEntity();
				respository.updateIsDeleteByEntityIdAndGstinId(
						reqDto.getEntityId(), gstinId);
				bankDetailsEntity.setEntityId(reqDto.getEntityId());
				bankDetailsEntity.setGstinId(gstinId);
				bankDetailsEntity.setGroupId(reqDto.getGroupId());
				bankDetailsEntity.setIfscCode(reqDto.getIfscCode());
				bankDetailsEntity.setBankAcct(reqDto.getBankAcc());
				bankDetailsEntity.setBeneficiary(reqDto.getBeneficiary());
				bankDetailsEntity.setPaymentDueDate(EYDateUtil
						.toUTCDateTimeFromLocal(reqDto.getPaymentDueDate()));
				bankDetailsEntity
						.setPaymentInstruction(reqDto.getPaymentInstruction());
				bankDetailsEntity.setPaymentTerm(reqDto.getPaymentTerms());
				bankDetailsEntity.setBankName(reqDto.getBankName() != null
						? StringUtils.truncate(reqDto.getBankName(), 50)
						: null);
				bankDetailsEntity.setBankAdd(reqDto.getBankAddress() != null
						? StringUtils.truncate(reqDto.getBankAddress(), 100)
						: null);
				bankDetailsEntities.add(bankDetailsEntity);
			});
		});
		if (!bankDetailsEntities.isEmpty()) {
			respository.saveAll(bankDetailsEntities);
		}
	}
}
