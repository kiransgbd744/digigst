package com.ey.advisory.app.gstr3b.itc10perc.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspComputeRepository;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspComputeEntity;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Component("Gstr3BITC10PerSaveAspChangesServiceImpl")
@Slf4j
public class Gstr3BITC10PerSaveAspChangesServiceImpl
		implements Gstr3BITC10PerSaveASpChangesService {
	
	@Autowired
	@Qualifier("Gstr3BGstinAspComputeRepository")
	private Gstr3BGstinAspComputeRepository aspRepo;

	@Override
	public void saveItcChangsToAsp(String gstin, String taxPeriod,
			List<Gstr3BGstinAspUserInputDto> userInputList, String status) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3BITC10PerSaveAspChangesServiceImpl"
					+ ".saveItcChangsToAsp begin," + " for gstin : "
					+ gstin + " and taxPeriod : " + " " + taxPeriod);
		}
		List<Gstr3BGstinAspComputeEntity> aspInputEntities = null;

		aspInputEntities = userInputList.stream()
				.map(o -> convertToEntity(gstin, taxPeriod, o))
				.collect(Collectors.toCollection(ArrayList::new));

		List<String> sectionNameList = null;
		if ("SAVE".equalsIgnoreCase(status)) {
			sectionNameList = userInputList.stream()
					.map(o -> o.getSectionName())
					.collect(Collectors.toCollection(ArrayList::new));
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3BITC10PerSaveAspChangesServiceImpl"
					+ ".saveItcChangsToAsp fetching sectionNameList :"
					+ " : " + sectionNameList);
		}
		aspRepo.updateIdGstr3BAspComputeBySectionName(taxPeriod, gstin,
				sectionNameList);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3BITC10PerSaveAspChangesServiceImpl"
					+ ".saveItcChangsToAsp updated Active flag ");
		}

		if (status.equalsIgnoreCase("SAVE"))
			aspRepo.saveAll(aspInputEntities);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3BITC10PerSaveAspChangesServiceImpl"
					+ ".saveItcChangsToAsp Saved AspInput "
					+ "Successfully ");
		}
	}

	private Gstr3BGstinAspComputeEntity convertToEntity(String gstin,
			String taxPeriod, Gstr3BGstinAspUserInputDto userInput) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr3BITC10PerSaveAspChangesServiceImpl"
					+ ".convertToEntity method :");
		}

		BigDecimal zeroVal = BigDecimal.ZERO;
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		Gstr3BGstinAspComputeEntity userEntity = 
							new Gstr3BGstinAspComputeEntity();

		userEntity.setCess(
				userInput.getCess() != null ? userInput.getCess() : zeroVal);
		userEntity.setCgst(
				userInput.getCgst() != null ? userInput.getCgst() : zeroVal);
		userEntity.setIgst(
				userInput.getIgst() != null ? userInput.getIgst() : zeroVal);
		userEntity.setSgst(
				userInput.getSgst() != null ? userInput.getSgst() : zeroVal);
		userEntity.setTaxableVal(zeroVal);
		userEntity.setGstin(gstin);
		userEntity.setTaxPeriod(taxPeriod);
		userEntity.setCreateDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		userEntity.setCreatedBy(userName);
		userEntity.setUpdateDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		userEntity.setUpdatedBy(userName);
		userEntity.setInterState(zeroVal);
		userEntity.setIntraState(zeroVal);
		userEntity.setSectionName(userInput.getSectionName());
		userEntity.setSubSectionName(userInput.getSubSectionName());
		userEntity.setIsActive(true);
		userEntity.setPos(null);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Gstr3BITC10PerSaveAspChangesServiceImpl"
					+ ".convertToEntity, before returning "
					+ "Gstr3BGstinAspComputeEntity userEntity :"
					+ userEntity);
		}

		return userEntity;

	}

}
