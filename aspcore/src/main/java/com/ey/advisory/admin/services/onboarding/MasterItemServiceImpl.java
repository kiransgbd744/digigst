package com.ey.advisory.admin.services.onboarding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.MasterItemEntity;
import com.ey.advisory.admin.data.repositories.client.HsnOrSacRepository;
import com.ey.advisory.admin.data.repositories.client.MasterItemRepository;
import com.ey.advisory.core.dto.MasterItemReqDto;
import com.ey.advisory.core.dto.MasterItemRespDto;
import com.ey.advisory.core.dto.Messages;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Umesha.M
 *
 */
@Slf4j
@Component("masterItemService")
public class MasterItemServiceImpl implements MasterItemService {

	@Autowired
	@Qualifier("masterItemRepository")
	private MasterItemRepository masterItemRepository;

	@Autowired
	@Qualifier("hsnOrSacRepository")
	private HsnOrSacRepository hsnOrSacRepository;

	@Override
	public List<MasterItemRespDto> getMasterItem(final Long entityId) {
		LOGGER.debug("MasterItemServiceImpl getMasterItem begin");
		List<MasterItemEntity> masterItemEntities = masterItemRepository
		        .getAllMasterItem(entityId);
		List<MasterItemRespDto> dtos = new ArrayList<>();
		if (!masterItemEntities.isEmpty()) {
			masterItemEntities.forEach(masterItemEntity -> {
				MasterItemRespDto dto = new MasterItemRespDto();
				dto.setId(masterItemEntity.getId());
				dto.setGstinPan(masterItemEntity.getGstinPan());
				dto.setItmCode(masterItemEntity.getItmCode());
				dto.setItemDesc(masterItemEntity.getItemDesc());
				dto.setItmCategory(masterItemEntity.getItmCategory());
				dto.setHsnOrSac(masterItemEntity.getHsnOrSac());
				dto.setUom(masterItemEntity.getUom());
				dto.setReverseChargeFlag(
				        masterItemEntity.getReverseChargeFlag());
				dto.setTdsFlag(masterItemEntity.getTdsFlag());
				dto.setDiffPercent(masterItemEntity.getDiffPercent());
				dto.setNilOrNonOrExmt(masterItemEntity.getNilOrNonOrExmt());
				dto.setIfYCirularNotificationNum(
				        masterItemEntity.getIfYCirularNotificationNum());
				dto.setIfYCirularNotificationDate(
				        masterItemEntity.getIfYCirularNotificationDate());
				dto.setEfCircularDate(masterItemEntity.getEfCircularDate());
				dto.setRate(masterItemEntity.getRate());
				dto.setElgblIndicator(masterItemEntity.getElgblIndicator());
				dto.setPerOfElgbl(masterItemEntity.getPerOfElgbl());
				dto.setCommonSuppIndicator(
				        masterItemEntity.getCommonSuppIndicator());
				dto.setItcReversalIdentifier(
				        masterItemEntity.getItcReversalIdentifier());
				dto.setItcsEntitlement(masterItemEntity.getItcsEntitlement());
				dtos.add(dto);
			});
		}
		LOGGER.error("MasterItemServiceImpl getMasterItem end");
		return dtos;
	}

	@Override
	public Messages updateMasterItem(List<MasterItemReqDto> dtos) {
		LOGGER.error("MasterItemServiceImpl updateMasterItem begin");
		Pair<List<String>, List<MasterItemEntity>> pairOfMasterItem = convertMasterItem(
		        dtos);
		List<String> errorMsg = pairOfMasterItem.getValue0();
		Messages msg = new Messages();

		List<MasterItemEntity> entities = pairOfMasterItem.getValue1();
		if (errorMsg != null && !errorMsg.isEmpty()) {
			msg.setMessages(errorMsg);
		} else if (entities != null && !entities.isEmpty()) {
			masterItemRepository.saveAll(entities);
		}
		LOGGER.error("MasterItemServiceImpl updateMasterItem end");
		return msg;
	}

	private Pair<List<String>, List<MasterItemEntity>> convertMasterItem(
	        List<MasterItemReqDto> dtos) {
		List<String> errorMsg = new ArrayList<>();
		List<MasterItemEntity> entities = new ArrayList<>();
		dtos.forEach(dto -> {

			allStructuralValidation(errorMsg, dto);

			if (dto.getGstinPan() != null && dto.getHsnOrSac() != null
			        && dto.getRate() != null
			        && dto.getElgblIndicator() != null) {
				MasterItemEntity entity = new MasterItemEntity();
				entity.setId(dto.getId());
				entity.setGstinPan(dto.getGstinPan());
				entity.setItmCode(dto.getItmCode());
				entity.setItemDesc(dto.getItemDesc());
				entity.setItmCategory(dto.getItmCategory());
				entity.setHsnOrSac(dto.getHsnOrSac());
				entity.setUom(dto.getUom());
				entity.setReverseChargeFlag(dto.getReverseChargeFlag());
				entity.setTdsFlag(dto.getTdsFlag());
				entity.setDiffPercent(dto.getDiffPercent());
				entity.setNilOrNonOrExmt(dto.getNilOrNonOrExmt());
				entity.setIfYCirularNotificationNum(
				        dto.getIfYCirularNotificationNum());
				entity.setIfYCirularNotificationDate(
				        dto.getIfYCirularNotificationDate());
				entity.setEfCircularDate(dto.getEfCircularDate());
				entity.setRate(dto.getRate());
				entity.setElgblIndicator(dto.getElgblIndicator());
				entity.setPerOfElgbl(dto.getPerOfElgbl());
				entity.setCommonSuppIndicator(dto.getCommonSuppIndicator());
				entity.setItcReversalIdentifier(dto.getItcReversalIdentifier());
				entity.setItcsEntitlement(dto.getItcsEntitlement());
				entity.setDelete(false);
				entities.add(entity);
			}
		});
		return new Pair<>(errorMsg, entities);
	}

	private void allStructuralValidation(List<String> errorMsg,
	        MasterItemReqDto dto) {
		supplierStructaralValidation(errorMsg, dto);
		if (dto.getHsnOrSac() == null) {
			errorMsg.add("HSN or SAC is empty");
		}
		if (dto.getHsnOrSac() != null) {
			int hsnCount = hsnOrSacRepository
			        .findcountByHsnOrSac(String.valueOf(dto.getHsnOrSac()));
			if (hsnCount == 0) {
				errorMsg.add(dto.getHsnOrSac()
				        + " -> not found from HSN SAC Global Master.");
			}
		}
		if (dto.getRate() == null) {
			errorMsg.add("Rate is empty");
		}

		if (dto.getElgblIndicator() == null) {
			errorMsg.add("Eligibility Indicator is empty");
		}

		if (dto.getElgblIndicator() != null
		        && dto.getElgblIndicator().length() > 6) {
			errorMsg.add("Eligibility Indicator is invalid");
		}
		if (dto.getIfYCirularNotificationNum() != null
		        && dto.getIfYCirularNotificationNum().length() > 100) {
			errorMsg.add("If Y Circular Notification Number is invalid");
		}

		if (dto.getItmCode() != null && dto.getItmCode().length() > 100) {
			errorMsg.add(dto.getItmCode() + " Item Code is invalid");
		}
		if (dto.getItemDesc() != null && dto.getItemDesc().length() > 100) {
			errorMsg.add(dto.getItemDesc() + " Item Description is invalid");
		}
		if (dto.getItmCategory() != null
		        && dto.getItmCategory().length() > 100) {
			errorMsg.add(dto.getItmCategory() + " Item Category is invalid");
		}
	}

	private void supplierStructaralValidation(List<String> errorMsg,
	        MasterItemReqDto dto) {
		if (dto.getGstinPan() == null) {
			errorMsg.add("Gstin Or pan is empty");
		}

		if (dto.getGstinPan() != null && dto.getGstinPan().length() == 15
		        || dto.getGstinPan().length() == 10) {
			if (dto.getGstinPan().length() == 15) {
				String regex = "^[0-9][0-9][A-Za-z][A-Za-z][A-Za-z]"
				        + "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z][A-Za-z0-9]"
				        + "[A-Za-z0-9][A-Za-z0-9]$";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(dto.getGstinPan());
				if (!matcher.matches()) {
					errorMsg.add(dto.getGstinPan() + " Gstin is Invalid");
				}
			}
			if (dto.getGstinPan().length() == 10) {
				String regex = "^[A-Za-z][A-Za-z][A-Za-z]"
				        + "[A-Za-z][A-Za-z][0-9][0-9][0-9][0-9][A-Za-z]$";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(dto.getGstinPan());
				if (!matcher.matches()) {
					errorMsg.add(dto.getGstinPan() + " Pan is Invalid");
				}
			}
		} else {
			errorMsg.add(dto.getGstinPan() + " Gstin Or Pan is invalid");
		}
	}

	public void deleteMasterItem(List<MasterItemReqDto> dtos) {
		List<Long> ids = new ArrayList<>();
		dtos.forEach(dto -> {
			ids.add(dto.getId());
		});
		if (!ids.isEmpty()) {
			masterItemRepository.deleteMasterItem(ids);
		}
	}

	@Override
	public Map<Long, Map<String, List<Pair<Integer, BigDecimal>>>> 
	getAllMasterItems() {
		try {
			List<MasterItemEntity> all = masterItemRepository
					.getAllMasterItems();
			Map<Long, Map<String, List<Pair<Integer, BigDecimal>>>> map = all
					.stream()
					.filter(masterItem -> masterItem.getEntityId() != null
							&& masterItem.getGstinPan() != null)
					.collect(Collectors.groupingBy(
							MasterItemEntity::getEntityId,
							Collectors.groupingBy(
									MasterItemEntity::getGstinPan,
									Collectors.mapping(
											obj -> new Pair<>(
													((MasterItemEntity) obj)
															.getHsnOrSac(),
													((MasterItemEntity) obj)
															.getRate()),
											Collectors.toList()))));
			return map;
		} catch (RuntimeException e) {
			LOGGER.error(
					"Exception while getting getAllMasterItemGstins " + e);
			throw e;
		}
	}
}
