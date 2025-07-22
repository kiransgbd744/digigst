package com.ey.advisory.app.gstr3b.itc10perc.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputDto;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputEntity;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3BITC10PerSaveChangesServiceImpl")
public class Gstr3BITC10PerSaveChangesServiceImpl
		implements Gstr3BITC10PerSaveChangesService{

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository gstr3bRepo;

	@Override
	public void saveItcChangsToUserInputs(String gstin, String taxPeriod,
			List<Gstr3BGstinAspUserInputDto> userInputList, String status) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3BITC10PerSaveChangesServiceImpl"
					+ ".saveItcChangsToUserInputs begin," + " for gstin : "
					+ gstin + " and taxPeriod : " + " " + taxPeriod);
		}
		List<Gstr3BGstinAspUserInputEntity> userInputEntities = null;

		userInputEntities = userInputList.stream()
				.map(o -> convertToEntity(gstin, taxPeriod, o))
				.collect(Collectors.toCollection(ArrayList::new));

		List<String> sectionNameList = null;
		if ("SAVE".equalsIgnoreCase(status)) {
			sectionNameList = userInputList.stream()
					.map(o -> o.getSectionName())
					.collect(Collectors.toCollection(ArrayList::new));
		}
		
		Map<String, Gstr3BGstinAspUserInputDto> userInputSave = userInputList
				.stream()
				.collect(Collectors.toMap(
						Gstr3BGstinAspUserInputDto::getSectionName,
						Function.identity()));
		if (sectionNameList.contains("4(a)(5)(5.1)")
				&& sectionNameList.contains("4(a)(5)(5.2)")) {
			Gstr3BGstinAspUserInputDto dto = addSections(
					userInputSave.get("4(a)(5)(5.1)"),
					userInputSave.get("4(a)(5)(5.2)"), "4(a)(5)", "AOITC",
					"All other ITC");
			userInputEntities.add(convertToEntity(gstin, taxPeriod, dto));
			sectionNameList.add("4(a)(5)");
		} 
		
		/*sectionNameList.add("4(a)(5)(b)(1)");
		sectionNameList.add("4(a)(5)(b)(2)");
		sectionNameList.add("4(a)(5)(c)(1)");
		sectionNameList.add("4(a)(5)(c)(2)");
		*/if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3BITC10PerSaveChangesServiceImpl"
					+ ".saveItcChangsToUserInputs fetching sectionNameList :"
					+ " : " + sectionNameList);
		}
		gstr3bRepo.updateActiveFlag(taxPeriod, gstin, sectionNameList);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3BITC10PerSaveChangesServiceImpl"
					+ ".saveItcChangsToUserInputs updated Active flag ");
		}

		if (status.equalsIgnoreCase("SAVE"))
			gstr3bRepo.saveAll(userInputEntities);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3BITC10PerSaveChangesServiceImpl"
					+ ".saveItcChangsToUserInputs Saved UserInput "
					+ "Successfully ");
		}
	}

	private Gstr3BGstinAspUserInputEntity convertToEntity(String gstin,
			String taxPeriod, Gstr3BGstinAspUserInputDto userInput) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr3BITC10PerSaveChangesServiceImpl"
					+ ".convertToEntity method :");
		}

		BigDecimal zeroVal = BigDecimal.ZERO;
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		Gstr3BGstinAspUserInputEntity userEntity = 
							new Gstr3BGstinAspUserInputEntity();

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
		userEntity.setIsITCActive(true);
		userEntity.setPos(null);
		userEntity.setRowName(userInput.getRowName());
		userEntity.setRadioFlag(userInput.getRadioFlag());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Gstr3BITC10PerSaveChangesServiceImpl"
					+ ".convertToEntity, before returning "
					+ "Gstr3BGstinAspUserInputEntity userEntity :"
					+ userEntity);
		}

		return userEntity;

	}
	
	private Gstr3BGstinAspUserInputDto addSections(
			Gstr3BGstinAspUserInputDto s1, Gstr3BGstinAspUserInputDto s2,
			String sectionName, String subSectionName, String rowName) {
		Gstr3BGstinAspUserInputDto dto = new Gstr3BGstinAspUserInputDto();
		dto.setCess(s1.getCess().add(s2.getCess()));
		dto.setCgst(s1.getCgst().add(s2.getCgst()));
		dto.setSgst(s1.getSgst().add(s2.getSgst()));
		dto.setIgst(s1.getIgst().add(s2.getIgst()));
		dto.setSectionName(sectionName);
		dto.setSubSectionName(subSectionName);
		dto.setRowName(rowName);
		return dto;
	}


}