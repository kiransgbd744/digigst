package com.ey.advisory.app.gstr3b.itc10perc.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspComputeRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspComputeEntity;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputDto;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr3BRule38OtherReversalServiceImpl")
public class Gstr3BRule38OtherReversalServiceImpl
		implements Gstr3BRule38OtherReversalService {

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository userRepo;

	@Autowired
	@Qualifier("Gstr3BGstinAspComputeRepository")
	private Gstr3BGstinAspComputeRepository computeRepo;

	@Override
	public List<Gstr3BItc10PercDto> getOtherReversalData(String gstin,
			String taxPeriod) {

		List<Gstr3BItc10PercDto> list = null;

		List<String> sectionNameList = Arrays.asList("4(b)(1)", "4(b)(1)(1.1)",
				"4(b)(1)(1.2)", "4(b)(1)(1.2.a)", "4(b)(1)(1.2.b)",
				"4(b)(1)(1.2.c)");
		try {

			List<Gstr3BGstinAspComputeEntity> aspResp = computeRepo
					.findBySectionNameList(taxPeriod, gstin, sectionNameList);

			List<Gstr3BITCInnerDto> psdResp = aspResp.stream()
					.map(o -> convertComputeDto(o))
					.collect(Collectors.toList());

			List<Gstr3BGstinAspUserInputEntity> userData = userRepo
					.getITC10PercSectionData(taxPeriod, gstin, sectionNameList);

			List<Gstr3BITCInnerDto> userResp = userData.stream()
					.map(o -> convertDto(o)).collect(Collectors.toList());

			Map<String, Gstr3BITCInnerDto> userMap = userResp.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			Map<String, Gstr3BITCInnerDto> computeMap = psdResp.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			Map<String, Gstr3BItc10PercDto> itcMap = gstr3BOthReversalMap();

			itcMap.replaceAll((k, v) -> {
				if (computeMap.containsKey(k))
					setAspValues(v, computeMap.get(k));
				if (userMap.containsKey(k))
					setUserValues(v, userMap.get(k));
				return v;
			});

			List<Gstr3BItc10PercDto> respList = new ArrayList<>(
					itcMap.values());

			list = respList.stream().map(o -> setLevel(o))
					.collect(Collectors.toList());

		} catch (Exception ex) {
			String msg = String.format(
					"error occured in Gstr3BRule38OtherReversalServiceImpl",
					ex);
			LOGGER.error(msg);
			throw new AppException(ex);
		}
		return list;
	}

	private Gstr3BITCInnerDto addDto(Gstr3BITCInnerDto a, Gstr3BITCInnerDto b) {
		Gstr3BITCInnerDto dto = new Gstr3BITCInnerDto();
		dto.setCess(a.getCess().add(b.getCess()));
		dto.setCgst(a.getCgst().add(b.getCgst()));
		dto.setIgst(a.getIgst().add(b.getIgst()));
		dto.setSgst(a.getSgst().add(b.getSgst()));
		return dto;
	}

	private Gstr3BITCInnerDto convertDto(Gstr3BGstinAspUserInputEntity obj) {

		Gstr3BITCInnerDto dto = new Gstr3BITCInnerDto();

		dto.setSectionName(obj.getSectionName());
		dto.setSubSectionName(obj.getSubSectionName());
		dto.setCess(obj.getCess());
		dto.setCgst(obj.getCgst());
		dto.setIgst(obj.getIgst());
		dto.setSgst(obj.getSgst());
		dto.setRowSection(obj.getRowName());
		return dto;
	}

	private Gstr3BITCInnerDto convertComputeDto(
			Gstr3BGstinAspComputeEntity obj) {

		Gstr3BITCInnerDto dto = new Gstr3BITCInnerDto();

		dto.setSectionName(obj.getSectionName());
		dto.setSubSectionName(obj.getSubSectionName());
		dto.setCess(obj.getCess());
		dto.setCgst(obj.getCgst());
		dto.setIgst(obj.getIgst());
		dto.setSgst(obj.getSgst());
		return dto;

	}

	private Map<String, Gstr3BItc10PercDto> gstr3BOthReversalMap() {
		Map<String, Gstr3BItc10PercDto> map = new LinkedHashMap<>();
		map.put("4(b)(1)", new Gstr3BItc10PercDto("4(b)(1)", "total_4b1"));
		map.put("4(b)(1)(1.1)",
				new Gstr3BItc10PercDto("4(b)(1)(1.1)", "rule38_42_43"));
		map.put("4(b)(1)(1.2)",
				new Gstr3BItc10PercDto("4(b)(1)(1.2)", "Ineligible_ITC_175"));

		map.put("4(b)(1)(1.2.a)",
				new Gstr3BItc10PercDto("4(b)(1)(1.2.a)", "Table_4(A)(2)"));
		map.put("4(b)(1)(1.2.b)",
				new Gstr3BItc10PercDto("4(b)(1)(1.2.b)", "Table_4(A)(1)"));

		map.put("4(b)(1)(1.2.c)",
				new Gstr3BItc10PercDto("4(b)(1)(1.2.c)", "Table_4(A)(5)"));
		return map;
	}

	private void setUserValues(Gstr3BItc10PercDto v,
			Gstr3BITCInnerDto inputDto) {
		v.setCessUser(inputDto.getCess());
		v.setCgstUser(inputDto.getCgst());
		v.setIgstUser(inputDto.getIgst());
		v.setSgstUser(inputDto.getSgst());
		v.setRowName(inputDto.getRowSection());
	}

	private void setAspValues(Gstr3BItc10PercDto v,
			Gstr3BITCInnerDto inputDto) {

		v.setCessAsp(inputDto.getCess());
		v.setCgstAsp(inputDto.getCgst());
		v.setIgstAsp(inputDto.getIgst());
		v.setSgstAsp(inputDto.getSgst());
	}

	private Gstr3BItc10PercDto setLevel(Gstr3BItc10PercDto v) {

		if (v.getSectionName().equalsIgnoreCase("4(b)(1)(1.2.a)")
				|| v.getSectionName().equalsIgnoreCase("4(b)(1)(1.2.b)")
				|| v.getSectionName().equalsIgnoreCase("4(b)(1)(1.2.c)")) {
			v.setLevel("L2");
		}
		return v;

	}

	@Override
	public void saveOthReversalChangesToUserInput(String gstin,
			String taxPeriod, List<Gstr3BGstinAspUserInputDto> userInputList) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BRule38OtherReversalServiceImpl"
						+ ".saveOthReversalChangesToUserInput begin, for gstin : "
						+ gstin + " and taxPeriod : " + " " + taxPeriod);
			}
			List<Gstr3BGstinAspUserInputEntity> userInputEntities = null;

			userInputEntities = userInputList.stream()
					.map(o -> convertToEntity(gstin, taxPeriod, o))
					.collect(Collectors.toCollection(ArrayList::new));

			Map<String, Gstr3BGstinAspUserInputDto> userInputSave = userInputList
					.stream()
					.collect(Collectors.toMap(
							Gstr3BGstinAspUserInputDto::getSectionName,
							Function.identity()));

			List<String> sectionNameList = userInputList.stream()
					.map(o -> o.getSectionName())
					.collect(Collectors.toCollection(ArrayList::new));

			if (sectionNameList.contains("4(a)(1)(1.1)")
					&& sectionNameList.contains("4(a)(1)(1.2)")) {
				Gstr3BGstinAspUserInputDto dto = addSections(
						userInputSave.get("4(a)(1)(1.1)"),
						userInputSave.get("4(a)(1)(1.2)"), "4(a)(1)", "IOG",
						"Import of goods");
				userInputEntities.add(convertToEntity(gstin, taxPeriod, dto));
				sectionNameList.add("4(a)(1)");
			} else if (sectionNameList.contains("4(a)(3)(3.1)")
					&& sectionNameList.contains("4(a)(3)(3.2)")) {
				Gstr3BGstinAspUserInputDto dto = addSections(
						userInputSave.get("4(a)(3)(3.1)"),
						userInputSave.get("4(a)(3)(3.2)"), "4(a)(3)", "ISLRC",
						"Inward supplies liable to reverse charge (other than 1 & 2 above)");
				userInputEntities.add(convertToEntity(gstin, taxPeriod, dto));
				sectionNameList.add("4(a)(3)");
			} else if (sectionNameList.contains("4(a)(4)(4.1)")
					|| sectionNameList.contains("4(a)(4)(4.2)")
					|| sectionNameList.contains("4(a)(4)(4.3)")) {

				Gstr3BGstinAspUserInputDto dto = add3Sections(
						userInputSave.get("4(a)(4)(4.1)"),
						userInputSave.get("4(a)(4)(4.2)"),
						userInputSave.get("4(a)(4)(4.3)"), "4(a)(4)", "ISFISD",
						" Inward supplies from ISD");
				userInputEntities.add(convertToEntity(gstin, taxPeriod, dto));
				sectionNameList.add("4(a)(4)");
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BRule38OtherReversalServiceImpl"
						+ ".saveOthReversalChangesToUserInput fetching sectionNameList :"
						+ " : " + sectionNameList);
			}
			userRepo.updateActiveFlag(taxPeriod, gstin, sectionNameList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BRule38OtherReversalServiceImpl"
						+ ".saveOthReversalChangesToUserInput updated Active flag");
			}
			userRepo.saveAll(userInputEntities);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BRule38OtherReversalServiceImpl"
						+ ".saveOthReversalChangesToUserInput Saved UserInput "
						+ "Successfully ");
			}
		} catch (Exception ee) {
			String msg = "Error while saving Gstr3b table 4(b)(2)"
					+ " Other reversal changes to User Input table";
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}
	}

	private Gstr3BGstinAspUserInputEntity convertToEntity(String gstin,
			String taxPeriod, Gstr3BGstinAspUserInputDto userInput) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr3BRule38OtherReversalServiceImpl"
					+ ".convertToEntity method :");
		}

		BigDecimal zeroVal = BigDecimal.ZERO;
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		Gstr3BGstinAspUserInputEntity userEntity = new Gstr3BGstinAspUserInputEntity();

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
			LOGGER.info(" Gstr3BRule38OtherReversalServiceImpl"
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

	private Gstr3BGstinAspUserInputDto add3Sections(
			Gstr3BGstinAspUserInputDto s1, Gstr3BGstinAspUserInputDto s2,
			Gstr3BGstinAspUserInputDto s3, String sectionName,
			String subSectionName, String rowName) {
		Gstr3BGstinAspUserInputDto dto = new Gstr3BGstinAspUserInputDto();
		if (s1.getRadioFlag()) {
			dto = s1;
		} else if (s2.getRadioFlag() == true) {
			dto = s2;
		} else {
			dto = s3;
		}
		dto.setSectionName(sectionName);
		dto.setSubSectionName(subSectionName);
		dto.setRowName(rowName);
		dto.setRadioFlag(true);
		return dto;
	}
}
