package com.ey.advisory.app.gstr3b;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3BGstinDetailedSummaryServiceImpl")
public class Gstr3BGstinDetailedSummaryServiceImpl
		implements Gstr3BGstinDetailedSummaryService {

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository gstr3bRepo;

	@Override
	public void saveGstinDashboardUserInputs(String gstin, String taxPeriod,
			List<Gstr3BGstinAspUserInputDto> userInputList, String status) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3BGstinDetailedSummaryServiceImpl"
					+ ".saveGstinDashboardUserInputs begin," + " for gstin : "
					+ gstin + " and taxPeriod : " + " " + taxPeriod);
		}
		List<Gstr3BGstinAspUserInputEntity> userInputEntities = null;

		userInputEntities = userInputList.parallelStream()
				.map(o -> convertToEntity(gstin, taxPeriod, o))
				.collect(Collectors.toCollection(ArrayList::new));

		List<String> sectionNameList = null;
		if ("SAVE".equalsIgnoreCase(status)) {
			sectionNameList = userInputList.stream()
					.map(o -> o.getSectionName())
					.collect(Collectors.toCollection(ArrayList::new));
		}

		else if ("3.2_SAVE".equalsIgnoreCase(status)) {
			sectionNameList = new ArrayList<>();
			sectionNameList.add(Gstr3BConstants.Table3_2_A);
			sectionNameList.add(Gstr3BConstants.Table3_2_B);
			sectionNameList.add(Gstr3BConstants.Table3_2_C);
		}

		else if ("7.1_SAVE".equalsIgnoreCase(status)) {
			sectionNameList = new ArrayList<>();
			sectionNameList.add(Gstr3BConstants.Table7_1);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3BGstinDetailedSummaryServiceImpl"
					+ ".saveGstinDashboardUserInputs fetching sectionNameList :"
					+ " : " + sectionNameList);
		}

		/*
		 * if(sectionNameList.contains(Gstr3BConstants.Table3_2_A) ||
		 * sectionNameList.contains(Gstr3BConstants.Table3_2_B) ||
		 * sectionNameList.contains(Gstr3BConstants.Table3_2_C)){
		 * 
		 * sectionNameList = new ArrayList<>();
		 * sectionNameList.add(Gstr3BConstants.Table3_2_A);
		 * sectionNameList.add(Gstr3BConstants.Table3_2_B);
		 * sectionNameList.add(Gstr3BConstants.Table3_2_C); }
		 */

		gstr3bRepo.updateActiveFlag(taxPeriod, gstin, sectionNameList);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3BGstinDetailedSummaryServiceImpl"
					+ ".saveGstinDashboardUserInputs updated Active flag ");
		}

		// if (status.equalsIgnoreCase("SAVE"))
		gstr3bRepo.saveAll(userInputEntities);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3BGstinDetailedSummaryServiceImpl"
					+ ".saveGstinDashboardUserInputs Saved UserInput "
					+ "Successfully ");
		}
	}

	private Gstr3BGstinAspUserInputEntity convertToEntity(String gstin,
			String taxPeriod, Gstr3BGstinAspUserInputDto userInput) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr3BGstinDetailedSummaryServiceImpl"
					+ ".convertToEntity method :");
		}

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		if (userInput.getSectionName().equalsIgnoreCase(Gstr3BConstants.Table5A)
				|| userInput.getSectionName()
						.equalsIgnoreCase(Gstr3BConstants.Table5B))
			userInput.setTaxableVal(
					userInput.getInterState().add(userInput.getIntraState()));
		Gstr3BGstinAspUserInputEntity userEntity = new Gstr3BGstinAspUserInputEntity();

		userEntity.setUserRetPeriod(userInput.getUserRetPeriod());
		userEntity.setCess(userInput.getCess());
		userEntity.setCgst(userInput.getCgst());
		userEntity.setIgst(userInput.getIgst());
		userEntity.setSgst(userInput.getSgst());
		userEntity.setTaxableVal(userInput.getTaxableVal());
		userEntity.setGstin(gstin);
		userEntity.setTaxPeriod(taxPeriod);
		userEntity.setCreateDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		userEntity.setCreatedBy(userName);
		userEntity.setUpdateDate(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		userEntity.setUpdatedBy(userName);
		userEntity.setInterState(userInput.getInterState());
		userEntity.setIntraState(userInput.getIntraState());
		userEntity.setSectionName(userInput.getSectionName());
		userEntity.setSubSectionName(userInput.getSubSectionName());
		userEntity.setIsActive(true);
		userEntity.setIsITCActive(true);
		userEntity.setPos(userInput.getPos());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Gstr3BGstinDetailedSummaryServiceImpl"
					+ ".convertToEntity, before returning "
					+ "Gstr3BGstinAspUserInputEntity userEntity :"
					+ userEntity);
		}

		return userEntity;

	}

	@Override
	public void deleteGstinDashboardUserInputs(String gstin, String taxPeriod,
			List<Gstr3BGstinAspUserInputDto> userInput, String status) {
		List<String> sectionNameList = null;
		/*
		 * if ("DELETE".equalsIgnoreCase(status)) { sectionNameList =
		 * userInputList.stream() .map(o -> o.getSectionName())
		 * .collect(Collectors.toCollection(ArrayList::new)); }
		 */

		if ("3.2_DELETE".equalsIgnoreCase(status)) {
			sectionNameList = new ArrayList<>();
			sectionNameList.add(Gstr3BConstants.Table3_2_A);
			sectionNameList.add(Gstr3BConstants.Table3_2_B);
			sectionNameList.add(Gstr3BConstants.Table3_2_C);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("fetching sectionNameList for deleting: %s"
					+ sectionNameList);
		}

		if (sectionNameList == null)
			gstr3bRepo.updateActiveFlagforAllSections(taxPeriod, gstin);
		else
			gstr3bRepo.updateActiveFlag(taxPeriod, gstin, sectionNameList);

		List<Gstr3BGstinAspUserInputEntity> userInputEntities = null;

		userInputEntities = userInput.parallelStream()
				.map(o -> convertToEntity(gstin, taxPeriod, o))
				.collect(Collectors.toCollection(ArrayList::new));

		gstr3bRepo.saveAll(userInputEntities);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("updated active flag ");
		}

	}

}
