package com.ey.advisory.admin.services.onboarding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.MasterProductEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.HsnOrSacRepository;
import com.ey.advisory.admin.data.repositories.client.MasterProductRepository;
import com.ey.advisory.core.dto.MasterProductReqDto;
import com.ey.advisory.core.dto.MasterProductRespDto;
import com.ey.advisory.core.dto.Messages;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Umesha.M
 *
 */
@Slf4j
@Component("masterProductService")
public class MasterProductServiceImpl implements MasterProductService {

	@Autowired
	@Qualifier("masterProductRepository")
	private MasterProductRepository masterProductRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Autowired
	@Qualifier("hsnOrSacRepository")
	private HsnOrSacRepository hsnOrSacRepository;

	private static final List<String> conditionalList = Arrays.asList("Y", "N",
	        "");

	private static final List<String> itcflag = Arrays.asList("T1", "T3");

	@Override
	public List<MasterProductRespDto> getMasterProduct(final Long entityId) {
		LOGGER.debug("MasterProductServiceImpl getMasterProduct begin");
		List<MasterProductEntity> masterProductEntities = masterProductRepository
		        .getAllMasterProductDetails(entityId);
		List<MasterProductRespDto> dtos = new ArrayList<>();
		if (!masterProductEntities.isEmpty()) {
			masterProductEntities.forEach(masterProductEntity -> {
				MasterProductRespDto dto = new MasterProductRespDto();
				dto.setId(masterProductEntity.getId());
				dto.setGstinPan(masterProductEntity.getGstinPan());
				dto.setProductCode(masterProductEntity.getProductCode());
				dto.setProductDesc(masterProductEntity.getProductDesc());
				dto.setProductCategory(
				        masterProductEntity.getProductCategory());
				dto.setHsnOrSac(masterProductEntity.getHsnOrSac());
				dto.setUom(masterProductEntity.getUom());
				dto.setReverseChargeFlag(
				        masterProductEntity.getReverseChargeFlag());
				dto.setTdsFlag(masterProductEntity.getTdsFlag());
				dto.setDiffPercent(masterProductEntity.getDiffPercent());
				dto.setNilOrNonOrExmt(masterProductEntity.getNilOrNonOrExmt());
				dto.setIfYCircularNotificationNum(
				        masterProductEntity.getIfYCircularNotificationNum());
				dto.setNotificationDate(
				        masterProductEntity.getNotificationDate());
				dto.setEfCircularDate(masterProductEntity.getEfCircularDate());
				dto.setRate(masterProductEntity.getRate());
				dto.setItcFlag(masterProductEntity.getItcFlag());
				dtos.add(dto);
			});
		}
		LOGGER.debug("MasterProductServiceImpl getMasterProduct end");
		return dtos;
	}

	@Override
	public Messages updateMasterProduct(List<MasterProductReqDto> dtos) {
		LOGGER.debug("MasterProductServiceImpl updateMasterProduct begin");
		Messages errorMessages = new Messages();

		Pair<List<String>, List<MasterProductEntity>> pairMaster = convertProduct(
		        dtos);

		List<String> errorMsg = pairMaster.getValue0();
		List<MasterProductEntity> entities = pairMaster.getValue1();
		if (errorMsg != null && !errorMsg.isEmpty()) {
			errorMessages.setMessages(errorMsg);
		}
		if (entities != null && !entities.isEmpty()) {
			masterProductRepository.saveAll(entities);
		}
		LOGGER.debug("MasterProductServiceImpl updateMasterProduct end");
		return errorMessages;
	}

	private Pair<List<String>, List<MasterProductEntity>> convertProduct(
	        List<MasterProductReqDto> dtos) {
		List<MasterProductEntity> entities = new ArrayList<>();
		List<String> errorMsgs = new ArrayList<>();

		dtos.forEach(dto -> {

			if (dto.getGstinPan() == null || dto.getGstinPan().equals(" ")) {
				errorMsgs.add(dto.getGstinPan() + " -> GSTIN is mandatory.");

			}
			if (dto.getGstinPan() != null && dto.getGstinPan().length() != 15) {
				errorMsgs.add(dto.getGstinPan() + " ->Invalid GSTIN.");
			}
			int count = gstnDetailRepository.findgstin(dto.getGstinPan());
			if (count == 0) {
				errorMsgs.add(dto.getGstinPan()
				        + " -> not found as per Onboarding .");
			}

			if (dto.getHsnOrSac() == null) {
				errorMsgs.add(
				        dto.getHsnOrSac() + "HSNorSAC field is mandatory.");
			}

			if (dto.getHsnOrSac() != null) {
				int hsnCount = hsnOrSacRepository
				        .findcountByHsnOrSac(String.valueOf(dto.getHsnOrSac()));
				if (hsnCount == 0) {
					errorMsgs.add(dto.getHsnOrSac()
					        + " -> not found from HSN SAC Global Master.");
				}
			}

			if (dto.getUom() != null && dto.getUom().length() > 100) {
				errorMsgs.add(dto.getUom() + " -> Invalid uom field.");
			}

			if (dto.getReverseChargeFlag() != null
			        && !conditionalList.contains(dto.getReverseChargeFlag())) {
				errorMsgs.add(dto.getReverseChargeFlag()
				        + " -> ReverseChargeFlag field is invalid.");
			}

			if (dto.getTdsFlag() != null
			        && !conditionalList.contains(dto.getTdsFlag())) {
				errorMsgs.add(
				        dto.getTdsFlag() + " -> TDSFlag field is invalid.");
			}

			if (dto.getDiffPercent() != null && !Arrays.asList("L65", "N", "")
			        .contains(dto.getDiffPercent())) {
				errorMsgs.add(dto.getDiffPercent()
				        + " -> Differential%Flag field is invalid.");
			}

			if (dto.getNilOrNonOrExmt() != null
			        && !Arrays.asList("NIL", "NON", "Exempt")
			                .contains(dto.getNilOrNonOrExmt())) {
				errorMsgs.add(dto.getNilOrNonOrExmt()
				        + " -> NIL/NON/Exempt field is invalid.");
			}

			if (dto.getItcFlag() != null
			        && !itcflag.contains(dto.getItcFlag())) {
				errorMsgs.add(dto.getItcFlag() + " -> Invalid Itcflag field.");
			}
			if (dto.getRate() == null) {
				errorMsgs.add("Rate field mandatory.");
			}

			if (dto.getRate() != null) {
				boolean isValid = is3digValidDec(dto.getRate());
				if (!isValid) {
					errorMsgs.add(dto.getRate() + " -> Invalid Rate.");
				}
			}

			if (dto.getGstinPan() != null && dto.getHsnOrSac() != null
			        && dto.getRate() != null) {
				MasterProductEntity entity = new MasterProductEntity();
				entity.setId(dto.getId());
				entity.setGstinPan(dto.getGstinPan());
				entity.setProductCode(dto.getProductCode());
				entity.setProductDesc(dto.getProductDesc());
				entity.setProductCategory(dto.getProductCategory());
				entity.setHsnOrSac(dto.getHsnOrSac());
				entity.setUom(dto.getUom());
				entity.setReverseChargeFlag(dto.getReverseChargeFlag());
				entity.setTdsFlag(dto.getTdsFlag());
				entity.setDiffPercent(dto.getDiffPercent());
				entity.setNilOrNonOrExmt(dto.getNilOrNonOrExmt());
				entity.setIfYCircularNotificationNum(
				        dto.getIfYCircularNotificationNum());
				entity.setNotificationDate(dto.getNotificationDate());
				entity.setEfCircularDate(dto.getEfCircularDate());
				entity.setRate(dto.getRate());
				entity.setItcFlag(dto.getItcFlag());
				entity.setDelete(false);
				entities.add(entity);
			}

		});
		return new Pair<>(errorMsgs, entities);
	}

	private boolean is3digValidDec(BigDecimal obj) {
		BigDecimal check = new BigDecimal("999999999999.999");
		return check.compareTo(obj) > 0 ? true : false;
	}

	public void deleteMasterProduct(List<MasterProductReqDto> dtos) {
		List<Long> ids = new ArrayList<>();
		dtos.forEach(dto -> {
			ids.add(dto.getId());
		});

		if (!ids.isEmpty()) {
			masterProductRepository.deleteMasterProduct(ids);
		}
	}

	@Override
	public Map<Long, Map<String, List<Pair<Integer, BigDecimal>>>> 
	getAllMasterProducts() {
		try {
			List<MasterProductEntity> all = masterProductRepository
					.getAllMasterProducts();
			Map<Long, Map<String, List<Pair<Integer, BigDecimal>>>> map = all
					.stream()
					.filter(masterProduct -> masterProduct.getEntityId() != null
							&& masterProduct.getGstinPan() != null)
					.collect(Collectors.groupingBy(
							MasterProductEntity::getEntityId,
							Collectors.groupingBy(
									MasterProductEntity::getGstinPan,
									Collectors.mapping(
											obj -> new Pair<>(
													((MasterProductEntity) obj)
															.getHsnOrSac(),
													((MasterProductEntity) obj)
															.getRate()),
											Collectors.toList()))));
			return map;
		} catch (RuntimeException e) {
			LOGGER.error(
					"Exception while getting getAllMasterProductGstins " + e);
			throw e;
		}
	}
}
