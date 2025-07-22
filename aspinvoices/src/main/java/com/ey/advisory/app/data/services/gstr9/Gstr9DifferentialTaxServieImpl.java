package com.ey.advisory.app.data.services.gstr9;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr9.Gstr9GetCallComputeEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9GetSummaryEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9UserInputEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetCallComputeRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetSummaryRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9UserInputRepository;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9DiffTaxDBDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9DiffTaxDashboardDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9DiffTaxMapDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9DiffTaxSaveDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr9DifferentialTaxServieImpl")
public class Gstr9DifferentialTaxServieImpl
		implements Gstr9DifferentialTaxServie {

	@Autowired
	@Qualifier("Gstr9UserInputRepository")
	private Gstr9UserInputRepository gstr9UserInputRepository;

	@Autowired
	@Qualifier("Gstr9GetSummaryRepository")
	private Gstr9GetSummaryRepository gstr9GetSummaryRepository;

	@Autowired
	@Qualifier("Gstr9GetCallComputeRepository")
	private Gstr9GetCallComputeRepository gstr9GetCallComputRepo;

	private static ImmutableList<String> section = ImmutableList.of("14");
	private static ImmutableList<String> subSection = ImmutableList.of("14(1)",
			"14(2)");

	BigDecimal zeroVal = BigDecimal.ZERO;

	@Override
	public List<Gstr9DiffTaxDashboardDto> getGstr9DiffTaxDetails(String gstin,
			String taxPeriod, String formattedFy) {

		List<Gstr9DiffTaxDashboardDto> respList = new ArrayList<>();
		try {
			Map<String, Gstr9DiffTaxDashboardDto> gstr9Map = getDiffTaxMap();

			List<Gstr9UserInputEntity> userInputArr = gstr9UserInputRepository
					.findByGstinAndFyAndSectionInAndIsActiveTrue(gstin,
							formattedFy, section);

			List<Gstr9DiffTaxDBDto> userInputDtoList = new ArrayList<>();

			if (userInputArr == null || userInputArr.isEmpty()) {
				userInputDtoList = subSection.stream()
						.map(o -> createDefault(o))
						.collect(Collectors.toCollection(ArrayList::new));
			} else {
				userInputDtoList = userInputArr.stream()
						.map(o -> convertGstr9DiffTaxDBDto(o))
						.collect(Collectors.toCollection(ArrayList::new));
			}
			List<Gstr9DiffTaxMapDto> userInputList = null;
			if (userInputDtoList != null && !userInputDtoList.isEmpty()) {
				userInputList = convertTaxPaid(userInputDtoList);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DifferentialTaxServieImpl"
						+ ".userInputList now fetching data from getSummary table");
			}

			List<Gstr9GetSummaryEntity> getSummaryArr = gstr9GetSummaryRepository
					.findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(gstin,
							taxPeriod, section);

			List<Gstr9DiffTaxMapDto> getSummaryList = getSummaryArr.stream()
					.map(o -> convertGstnTaxPaid(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DifferentialTaxServieImpl"
						+ ".getSummaryList   size = " + getSummaryList.size());
			}

			// GstnAutoCal
			Integer fy = GenUtil.convertFytoIntFromReturnPeriod(taxPeriod);

			List<Gstr9GetCallComputeEntity> gstnAutoCalArr = gstr9GetCallComputRepo
					.findByGstinAndFinancialYearAndSectionInAndIsActiveTrue(
							gstin, fy, section);

			List<Gstr9DiffTaxDBDto> gstnAutoCalDtoList = new ArrayList<>();

			if (gstnAutoCalArr == null || gstnAutoCalArr.isEmpty()) {
				gstnAutoCalDtoList = subSection.stream()
						.map(o -> createDefault(o))
						.collect(Collectors.toCollection(ArrayList::new));
			} else {
				gstnAutoCalDtoList = gstnAutoCalArr.stream()
						.map(o -> convertGstr9DiffTaxGstAutoDBDto(o))
						.collect(Collectors.toCollection(ArrayList::new));

			}

			List<Gstr9DiffTaxMapDto> gstnAutoCalList = new ArrayList<>();

			if (gstnAutoCalDtoList != null && !gstnAutoCalDtoList.isEmpty()) {
				gstnAutoCalList = convertTaxPaid(gstnAutoCalDtoList);

			}

			/*
			 * The below code will merge the input list by adding the rows which
			 * has same section
			 */
			Map<String, Gstr9DiffTaxMapDto> userMap = userInputList.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addUserDto(a, b)),
									Optional::get)));

			/*
			 * The below code will merge the user SUMM list by adding the rows
			 * which has same section
			 */
			Map<String, Gstr9DiffTaxMapDto> summaryMap = getSummaryList.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addUserDto(a, b)),
									Optional::get)));

			Map<String, Gstr9DiffTaxMapDto> gstnAutoCalMap = gstnAutoCalList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addUserDto(a, b)),
									Optional::get)));

			gstr9Map.replaceAll((k, v) -> {

				if (gstnAutoCalMap.containsKey(k))
					setgstnAutoCalValues(v, gstnAutoCalMap.get(k));
				if (userMap.containsKey(k))
					setUserInputValues(v, userMap.get(k));
				if (summaryMap.containsKey(k))
					setSummaryValues(v, summaryMap.get(k));
				return v;
			});

			respList = new ArrayList<>(gstr9Map.values());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DifferentialTaxServieImpl"
						+ ".getGstr9DiffTaxDetails returning response"
						+ ", response size = " + respList.size() + "");
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr9 DiffTax Data ";
			LOGGER.error(msg, ex);

		}
		return respList;
	}

	private Gstr9DiffTaxMapDto addUserDto(Gstr9DiffTaxMapDto a,
			Gstr9DiffTaxMapDto b) {
		Gstr9DiffTaxMapDto dto = new Gstr9DiffTaxMapDto();
		dto.setTaxPaid(a.getTaxPaid().add(b.getTaxPaid()));
		dto.setTaxPayable(a.getTaxPayable().add(b.getTaxPayable()));
		return dto;
	}

	private List<Gstr9DiffTaxMapDto> convertTaxPaid(
			List<Gstr9DiffTaxDBDto> dbDto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9UserInputEntity "
					+ " Gstr9DiffTaxMapDto object";
			LOGGER.debug(str);
		}
		List<Gstr9DiffTaxMapDto> dtoList = new ArrayList<>();

		for (Gstr9DiffTaxDBDto dto : dbDto) {

			Gstr9DiffTaxMapDto obj1 = new Gstr9DiffTaxMapDto();
			obj1.setSubSection("14A");
			obj1.setTaxPaid(dto.getIgstPaid());
			obj1.setTaxPayable(dto.getIgstPayable());
			dtoList.add(obj1);

			Gstr9DiffTaxMapDto obj2 = new Gstr9DiffTaxMapDto();
			obj2.setSubSection("14B");
			obj2.setTaxPaid(dto.getCgstPaid());
			obj2.setTaxPayable(dto.getCgstPayable());
			dtoList.add(obj2);

			Gstr9DiffTaxMapDto obj3 = new Gstr9DiffTaxMapDto();
			obj3.setSubSection("14C");
			obj3.setTaxPaid(dto.getSgstPaid());
			obj3.setTaxPayable(dto.getSgstPayable());
			dtoList.add(obj3);

			Gstr9DiffTaxMapDto obj4 = new Gstr9DiffTaxMapDto();
			obj4.setSubSection("14D");
			obj4.setTaxPaid(dto.getCessPaid());
			obj4.setTaxPayable(dto.getCessPayable());
			dtoList.add(obj4);

			Gstr9DiffTaxMapDto obj5 = new Gstr9DiffTaxMapDto();
			obj5.setSubSection("14E");
			obj5.setTaxPaid(dto.getInterestPaid());
			obj5.setTaxPayable(dto.getInterestPayable());
			dtoList.add(obj5);
		}

		return dtoList;
	}

	private Gstr9DiffTaxMapDto convertGstnTaxPaid(Gstr9GetSummaryEntity arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9GetSummaryEntity "
					+ " Gstr9DiffTaxMapDto object";
			LOGGER.debug(str);
		}
		Gstr9DiffTaxMapDto obj = new Gstr9DiffTaxMapDto();
		obj.setSubSection(arr.getSubSection());
		obj.setTaxPaid(arr.getTxPaid());
		obj.setTaxPayable(arr.getTxPyble());
		return obj;
	}

	private Map<String, Gstr9DiffTaxDashboardDto> getDiffTaxMap() {
		Map<String, Gstr9DiffTaxDashboardDto> map = new LinkedHashMap<>();
		map.put("14A", new Gstr9DiffTaxDashboardDto("14A"));
		map.put("14B", new Gstr9DiffTaxDashboardDto("14B"));
		map.put("14C", new Gstr9DiffTaxDashboardDto("14C"));
		map.put("14D", new Gstr9DiffTaxDashboardDto("14D"));
		map.put("14E", new Gstr9DiffTaxDashboardDto("14E"));
		return map;
	}

	private void setSummaryValues(Gstr9DiffTaxDashboardDto gstr9gstnDto,
			Gstr9DiffTaxMapDto mapDto) {
		gstr9gstnDto.setGstinTaxPaid(mapDto.getTaxPaid());
		gstr9gstnDto.setGstinTaxPayble(mapDto.getTaxPayable());

	}

	private void setUserInputValues(Gstr9DiffTaxDashboardDto gstr9gstnDto,
			Gstr9DiffTaxMapDto mapDto) {
		gstr9gstnDto.setUserInputTaxPaid(mapDto.getTaxPaid());
		gstr9gstnDto.setUserInputTaxPayble(mapDto.getTaxPayable());
	}

	private void setgstnAutoCalValues(Gstr9DiffTaxDashboardDto gstr9gstnDto,
			Gstr9DiffTaxMapDto mapDto) {
		gstr9gstnDto.setGstnAutoCalTaxPaid(mapDto.getTaxPaid());
		gstr9gstnDto.setGstnAutoCalPayble(mapDto.getTaxPayable());
	}

	@Override
	public String saveGstr9DiffTaxUserInputData(String gstin, String fy,
			String status, List<Gstr9DiffTaxMapDto> userInputList) {

		int deleteCount = 0;
		try {
			String taxPeriod = "03" + fy.substring(fy.lastIndexOf('-') + 1);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DifferentialTaxServieImpl"
						+ ".saveGstr9DiffTaxUserInputData begin,"
						+ " for gstin : " + gstin + " and taxPeriod : " + " "
						+ taxPeriod);
			}
			String createdBy = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			List<Gstr9DiffTaxSaveDto> userInputSaveDto = convertToSaveDato(
					userInputList);

			List<Gstr9UserInputEntity> userInputEntities = userInputSaveDto
					.stream()
					.map(o -> convertToEntity(gstin, taxPeriod, fy, createdBy,
							status, o))
					.collect(Collectors.toCollection(ArrayList::new));

			/*
			 * soft delete existing entries before saving
			 */

			if (status.equals("SAVE")) {
				deleteCount = gstr9UserInputRepository
						.softDeleteBasedOnGstinandFy(gstin, fy, section, "U",
								createdBy);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DifferentialTaxServieImpl"
						+ ".saveGstr9DiffTaxUserInputData soft delete count "
						+ deleteCount);
			}

			gstr9UserInputRepository.saveAll(userInputEntities);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DifferentialTaxServieImpl"
						+ ".saveGstr9DiffTaxUserInputData Saved UserInput "
						+ "Successfully ");
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Gstr9 user Data ";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return "success";
	}

	private List<Gstr9DiffTaxSaveDto> convertToSaveDato(
			List<Gstr9DiffTaxMapDto> oList) {

		Gstr9DiffTaxSaveDto saveDto1 = new Gstr9DiffTaxSaveDto();
		Gstr9DiffTaxSaveDto saveDto2 = new Gstr9DiffTaxSaveDto();

		List<Gstr9DiffTaxSaveDto> saveDtoList = new ArrayList<>();

		for (Gstr9DiffTaxMapDto o : oList) {

			if (o.getSubSection().equalsIgnoreCase("14A")) {
				saveDto2.setSubSection("14(1)");
				saveDto2.setIgst(o.getTaxPayable());
				saveDto1.setSubSection("14(2)");
				saveDto1.setIgst(o.getTaxPaid());
			}
			if (o.getSubSection().equalsIgnoreCase("14B")) {
				saveDto1.setCgst(o.getTaxPaid());
				saveDto2.setCgst(o.getTaxPayable());
			}
			if (o.getSubSection().equalsIgnoreCase("14C")) {
				saveDto1.setSgst(o.getTaxPaid());
				saveDto2.setSgst(o.getTaxPayable());
			}
			if (o.getSubSection().equalsIgnoreCase("14D")) {
				saveDto1.setCess(o.getTaxPaid());
				saveDto2.setCess(o.getTaxPayable());

			}

			if (o.getSubSection().equalsIgnoreCase("14E")) {
				saveDto1.setIntr(o.getTaxPaid());
				saveDto2.setIntr(o.getTaxPayable());
			}
		}

		saveDtoList.add(saveDto1);
		saveDtoList.add(saveDto2);
		return saveDtoList;
	}

	private Gstr9DiffTaxDBDto convertGstr9DiffTaxDBDto(
			Gstr9UserInputEntity entity) {

		Gstr9DiffTaxDBDto dbDto = new Gstr9DiffTaxDBDto();
		if (entity.getSubSection().equalsIgnoreCase("14(1)")) {
			dbDto.setCessPayable(
					entity.getCess() != null ? entity.getCess() : zeroVal);
			dbDto.setCgstPayable(
					entity.getCgst() != null ? entity.getCgst() : zeroVal);
			dbDto.setIgstPayable(
					entity.getIgst() != null ? entity.getIgst() : zeroVal);
			dbDto.setInterestPayable(
					entity.getIntr() != null ? entity.getIntr() : zeroVal);
			dbDto.setSgstPayable(
					entity.getSgst() != null ? entity.getSgst() : zeroVal);

		} else if (entity.getSubSection().equalsIgnoreCase("14(2)")) {
			dbDto.setCessPaid(
					entity.getCess() != null ? entity.getCess() : zeroVal);
			dbDto.setCgstPaid(
					entity.getCgst() != null ? entity.getCgst() : zeroVal);
			dbDto.setIgstPaid(
					entity.getIgst() != null ? entity.getIgst() : zeroVal);
			dbDto.setInterestPaid(
					entity.getIntr() != null ? entity.getIntr() : zeroVal);
			dbDto.setSgstPaid(
					entity.getSgst() != null ? entity.getSgst() : zeroVal);
		}

		return dbDto;

	}

	private Gstr9DiffTaxDBDto convertGstr9DiffTaxGstAutoDBDto(
			Gstr9GetCallComputeEntity entity) {
		Gstr9DiffTaxDBDto dbDto = new Gstr9DiffTaxDBDto();
		if (entity.getSubSection().equalsIgnoreCase("14(1)")) {
			dbDto.setCessPayable(
					entity.getCess() != null ? entity.getCess() : zeroVal);
			dbDto.setCgstPayable(
					entity.getCgst() != null ? entity.getCgst() : zeroVal);
			dbDto.setIgstPayable(
					entity.getIgst() != null ? entity.getIgst() : zeroVal);
			dbDto.setInterestPayable(
					entity.getIntr() != null ? entity.getIntr() : zeroVal);
			dbDto.setSgstPayable(
					entity.getSgst() != null ? entity.getSgst() : zeroVal);

		} else if (entity.getSubSection().equalsIgnoreCase("14(2)")) {
			dbDto.setCessPaid(
					entity.getCess() != null ? entity.getCess() : zeroVal);
			dbDto.setCgstPaid(
					entity.getCgst() != null ? entity.getCgst() : zeroVal);
			dbDto.setIgstPaid(
					entity.getIgst() != null ? entity.getIgst() : zeroVal);
			dbDto.setInterestPaid(
					entity.getIntr() != null ? entity.getIntr() : zeroVal);
			dbDto.setSgstPaid(
					entity.getSgst() != null ? entity.getSgst() : zeroVal);
		}

		return dbDto;

	}

	private Gstr9UserInputEntity convertToEntity(String gstin, String taxPeriod,
			String fy, String createdBy, String status,
			Gstr9DiffTaxSaveDto userInput) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr9DifferentialTaxServieImpl"
					+ ".convertToEntity method :");
		}

		String docKey = CommonUtility.generateGstr9DocKey(gstin, fy,
				userInput.getSubSection());

		Gstr9UserInputEntity userEntity = new Gstr9UserInputEntity();

		userEntity.setGstin(gstin);
		userEntity.setRetPeriod(taxPeriod);
		userEntity.setFy(fy);

		userEntity.setIgst(userInput.getIgst());
		userEntity.setCgst(userInput.getCgst());
		userEntity.setSgst(userInput.getSgst());
		userEntity.setCess(userInput.getCess());
		userEntity.setIntr(userInput.getIntr());
		userEntity.setSection(section.get(0));
		userEntity.setSubSection(userInput.getSubSection());
		userEntity.setSource("U");
		userEntity
				.setDerivedRetPeriod(GenUtil.convertTaxPeriodToInt(taxPeriod));
		userEntity.setActive(true);
		userEntity.setDocKey(docKey);
		userEntity.setNatureOfSupplies(Gstr9Util
				.getNatureOfSuppliesForSubSection(userInput.getSubSection()));
		userEntity.setCreatedOn(LocalDateTime.now());
		userEntity.setCreatedBy(createdBy);
		return userEntity;
	}

	private Gstr9DiffTaxDBDto createDefault(String subSection) {

		Gstr9DiffTaxDBDto dbDto = new Gstr9DiffTaxDBDto();
		if (subSection.equalsIgnoreCase("14(1)")) {
			dbDto.setCessPayable(zeroVal);
			dbDto.setCgstPayable(zeroVal);
			dbDto.setIgstPayable(zeroVal);
			dbDto.setInterestPayable(zeroVal);
			dbDto.setSgstPayable(zeroVal);

		} else if (subSection.equalsIgnoreCase("14(2)")) {
			dbDto.setCessPaid(zeroVal);
			dbDto.setCgstPaid(zeroVal);
			dbDto.setIgstPaid(zeroVal);
			dbDto.setInterestPaid(zeroVal);
			dbDto.setSgstPaid(zeroVal);
		}

		return dbDto;

	}

}
