package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BGetLiabilityAutoCalcRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspComputeRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.app.gstr3b.itc10perc.service.Gstr3BITCInnerDto;
import com.ey.advisory.app.gstr3b.itc10perc.service.Gstr3BItc10PercDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("Gstr3bTable4OtherReversalServiceImpl")
public class Gstr3bTable4OtherReversalServiceImpl
		implements Gstr3bTable4OtherReversalService {

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository userRepo;

	@Autowired
	@Qualifier("Gstr3BGstinAspComputeRepository")
	private Gstr3BGstinAspComputeRepository computeRepo;

	@Autowired
	@Qualifier("Gstr3BGetLiabilityAutoCalcRepository")
	private Gstr3BGetLiabilityAutoCalcRepository autoCalRepo;

	@Override
	public List<Gstr3BItc10PercDto> getOtherReversalData(String gstin,
			String taxPeriod) {

		List<Gstr3BItc10PercDto> list = null;

		List<String> sectionNameList = Arrays.asList("4(b)(2)", "4(b)(2)(2.1)",
				"4(b)(2)(2.2)", "4(b)(2)(2.3)", "4(b)(2)(2.3.a)", "4(b)(2)(2.3.b)","4(b)(2)(2.3.c)");
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

			List<Gstr3BGetLiabilityAutoCalcEntity> autoCalData = autoCalRepo
					.findBySectionNameList(taxPeriod, gstin, sectionNameList);

			List<Gstr3BITCInnerDto> autoCalResp = autoCalData.stream()
					.map(o -> convertAutoCalDto(o))
					.collect(Collectors.toList());

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

			Map<String, Gstr3BITCInnerDto> AutoCalMap = autoCalResp.stream()
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
				if (AutoCalMap.containsKey(k))
					setAutoCalValues(v, AutoCalMap.get(k));
				return v;
			});

			List<Gstr3BItc10PercDto> respList = new ArrayList<>(
					itcMap.values());

			list = respList.stream().map(o -> setLevel(o))
					.collect(Collectors.toList());

		} catch (Exception ex) {
			String msg = String.format(
					"error occured in Gstr3bTable4OtherReversalServiceImpl",
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

	private Gstr3BITCInnerDto convertAutoCalDto(
			Gstr3BGetLiabilityAutoCalcEntity obj) {

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
		map.put("4(b)(2)", new Gstr3BItc10PercDto("4(b)(2)", "IR_OTHERS"));
		map.put("4(b)(2)(2.1)", new Gstr3BItc10PercDto("4(b)(2)(2.1)", "OR_RFU"));
		map.put("4(b)(2)(2.2)",
				new Gstr3BItc10PercDto("4(b)(2)(2.2)", "OR_180RRFU"));

		map.put("4(b)(2)(2.3)", new Gstr3BItc10PercDto("4(b)(2)(2.3)", "OR_175"));
		map.put("4(b)(2)(2.3.a)",
				new Gstr3BItc10PercDto("4(b)(2)(2.3.a)", "OR_175_4A2_4"));
		map.put("4(b)(2)(2.3.b)",
				new Gstr3BItc10PercDto("4(b)(2)(2.3.b)", "OR_175_4A1_4"));
	
		map.put("4(b)(2)(2.3.c)",
				new Gstr3BItc10PercDto("4(b)(2)(2.3.c)", "OR_175_4A5"));
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

	private void setAutoCalValues(Gstr3BItc10PercDto v,
			Gstr3BITCInnerDto inputDto) {

		v.setCessAutoCal(inputDto.getCess());
		v.setCgstAutoCal(inputDto.getCgst());
		v.setIgstAutoCal(inputDto.getIgst());
		v.setSgstAutoCal(inputDto.getSgst());
	}

	private Gstr3BItc10PercDto setLevel(Gstr3BItc10PercDto v) {

		if (v.getSectionName().equalsIgnoreCase("4(b)(2)(2.3.a)")
				|| v.getSectionName().equalsIgnoreCase("4(b)(2)(2.3.b)")||
				v.getSectionName().equalsIgnoreCase("4(b)(2)(2.3.c)")) {
			v.setLevel("L2");
		}
		return v;

	}

	@Override
	public void saveOthReversalChangesToUserInput(String gstin,
			String taxPeriod, List<Gstr3BGstinAspUserInputDto> userInputList) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3bTable4OtherReversalServiceImpl"
						+ ".saveOthReversalChangesToUserInput begin, for gstin : "
						+ gstin + " and taxPeriod : " + " " + taxPeriod);
			}
			List<Gstr3BGstinAspUserInputEntity> userInputEntities = null;

			userInputEntities = userInputList.stream()
					.map(o -> convertToEntity(gstin, taxPeriod, o))
					.collect(Collectors.toCollection(ArrayList::new));

			List<String> sectionNameList = userInputList.stream()
					.map(o -> o.getSectionName())
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3bTable4OtherReversalServiceImpl"
						+ ".saveOthReversalChangesToUserInput fetching sectionNameList :"
						+ " : " + sectionNameList);
			}
			userRepo.updateActiveFlag(taxPeriod, gstin, sectionNameList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3bTable4OtherReversalServiceImpl"
						+ ".saveOthReversalChangesToUserInput updated Active flag");
			}
			userRepo.saveAll(userInputEntities);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3bTable4OtherReversalServiceImpl"
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
			LOGGER.info(" Inside Gstr3BITC10PerSaveChangesServiceImpl"
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

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Gstr3BITC10PerSaveChangesServiceImpl"
					+ ".convertToEntity, before returning "
					+ "Gstr3BGstinAspUserInputEntity userEntity :"
					+ userEntity);
		}

		return userEntity;

	}
}
