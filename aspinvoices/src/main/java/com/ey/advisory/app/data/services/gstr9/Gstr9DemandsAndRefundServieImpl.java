package com.ey.advisory.app.data.services.gstr9;

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
import com.ey.advisory.app.docs.dto.gstr9.Gstr9DemandAndRefundMapDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9DemandAndrefundDashboardDto;
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
@Component("Gstr9DemandsAndRefundServieImpl")
public class Gstr9DemandsAndRefundServieImpl
		implements Gstr9DemandsAndRefundServie {

	@Autowired
	@Qualifier("Gstr9UserInputRepository")
	private Gstr9UserInputRepository gstr9UserInputRepository;

	@Autowired
	@Qualifier("Gstr9GetSummaryRepository")
	private Gstr9GetSummaryRepository gstr9GetSummaryRepository;

	@Autowired
	@Qualifier("Gstr9GetCallComputeRepository")
	private Gstr9GetCallComputeRepository gstr9GetCallComputRepo;

	private static ImmutableList<String> section = ImmutableList.of("15");

	@Override
	public List<Gstr9DemandAndrefundDashboardDto> getGstr9DemandsAndRefundDetails(
			String gstin, String taxPeriod, String formattedFy) {

		List<Gstr9DemandAndrefundDashboardDto> respList = new ArrayList<>();
		try {
			Map<String, Gstr9DemandAndrefundDashboardDto> gstr9Map = getDemandAndRefundMap();

			List<Gstr9UserInputEntity> userInputArr = gstr9UserInputRepository
					.findByGstinAndFyAndSectionInAndIsActiveTrue(gstin,
							formattedFy, section);

			List<Gstr9DemandAndRefundMapDto> userInputList = userInputArr
					.stream().map(o -> convertTaxPaid(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DemandsAndRefundServieImpl"
						+ ".userInputList   size = " + userInputArr.size()
						+ " now fetching data from getSummary table");
			}

			List<Gstr9GetSummaryEntity> getSummaryArr = gstr9GetSummaryRepository
					.findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(gstin,
							taxPeriod, section);

			List<Gstr9DemandAndRefundMapDto> getSummaryList = getSummaryArr
					.stream().map(o -> convertGstnTaxPaid(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DemandsAndRefundServieImpl"
						+ ".getSummaryList   size = " + getSummaryList.size());
			}

			// gstn auto call
			Integer fy = GenUtil.convertFytoIntFromReturnPeriod(taxPeriod);

			List<Gstr9GetCallComputeEntity> getGstnAutoCalArr = gstr9GetCallComputRepo
					.findByGstinAndFinancialYearAndSectionInAndIsActiveTrue(
							gstin, fy, section);

			List<Gstr9DemandAndRefundMapDto> getGstnAutoCalList = getGstnAutoCalArr
					.stream().map(o -> convertGstnAutoCalTaxPaid(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DemandsAndRefundServieImpl"
						+ ".getGstnAutoCalList size = "
						+ getGstnAutoCalList.size());
			}

			/*
			 * The below code will merge the input list by adding the rows which
			 * has same section
			 */
			Map<String, Gstr9DemandAndRefundMapDto> userMap = userInputList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addUserDto(a, b)),
									Optional::get)));

			/*
			 * The below code will merge the user SUMM list by adding the rows
			 * which has same section
			 */
			Map<String, Gstr9DemandAndRefundMapDto> summaryMap = getSummaryList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addUserDto(a, b)),
									Optional::get)));

			// gstn Auto call
			Map<String, Gstr9DemandAndRefundMapDto> gstnAutoCalMap = getGstnAutoCalList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addUserDto(a, b)),
									Optional::get)));

			gstr9Map.replaceAll((k, v) -> {

				if (gstnAutoCalMap.containsKey(k))
					setGstnAutoCalValues(v, gstnAutoCalMap.get(k));
				if (userMap.containsKey(k))
					setUserInputValues(v, userMap.get(k));
				if (summaryMap.containsKey(k))
					setSummaryValues(v, summaryMap.get(k));

				return v;
			});

			respList = new ArrayList<>(gstr9Map.values());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DemandsAndRefundServieImpl"
						+ ".getGstr9DemandsAndRefundDetails returning response"
						+ ", response size = " + respList.size() + "");
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr9 "
					+ "Gstr9DemandsAndRefundDetails Data ";
			LOGGER.error(msg, ex);

		}
		return respList;
	}

	private Gstr9DemandAndRefundMapDto addUserDto(Gstr9DemandAndRefundMapDto a,
			Gstr9DemandAndRefundMapDto b) {
		Gstr9DemandAndRefundMapDto obj = new Gstr9DemandAndRefundMapDto();
		obj.setCess(a.getCess().add(b.getCess()));
		obj.setCgst(a.getCgst().add(b.getCgst()));
		obj.setIgst(a.getIgst().add(b.getIgst()));
		obj.setInterest(a.getInterest().add(b.getInterest()));
		obj.setLateFee(a.getLateFee().add(b.getLateFee()));
		obj.setPenalty(a.getPenalty().add(b.getPenalty()));
		obj.setSgst(a.getSgst().add(b.getSgst()));
		return obj;
	}

	private Gstr9DemandAndRefundMapDto convertTaxPaid(
			Gstr9UserInputEntity arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9UserInputEntity "
					+ " Gstr9DemandAndRefundMapDto object";
			LOGGER.debug(str);
		}
		Gstr9DemandAndRefundMapDto obj = new Gstr9DemandAndRefundMapDto();
		obj.setSubSection(arr.getSubSection());
		obj.setCess(arr.getCess());
		obj.setCgst(arr.getCgst());
		obj.setIgst(arr.getIgst());
		obj.setInterest(arr.getIntr());
		obj.setLateFee(arr.getFee());
		obj.setPenalty(arr.getPen());
		obj.setSgst(arr.getSgst());
		return obj;
	}

	private Gstr9DemandAndRefundMapDto convertGstnTaxPaid(
			Gstr9GetSummaryEntity arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9GetSummaryEntity "
					+ " Gstr9DemandAndRefundMapDto object";
			LOGGER.debug(str);
		}
		Gstr9DemandAndRefundMapDto obj = new Gstr9DemandAndRefundMapDto();
		obj.setSubSection(arr.getSubSection());
		obj.setCess(arr.getCsamt());
		obj.setCgst(arr.getCamt());
		obj.setIgst(arr.getIamt());
		obj.setInterest(arr.getIntr());
		obj.setLateFee(arr.getFee());
		obj.setPenalty(arr.getPen());
		obj.setSgst(arr.getSamt());

		return obj;
	}

	private Gstr9DemandAndRefundMapDto convertGstnAutoCalTaxPaid(
			Gstr9GetCallComputeEntity arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9GetCallComputeEntity "
					+ " Gstr9DemandAndRefundMapDto object";
			LOGGER.debug(str);
		}
		Gstr9DemandAndRefundMapDto obj = new Gstr9DemandAndRefundMapDto();
		obj.setSubSection(arr.getSubSection());
		obj.setCess(arr.getCess());
		obj.setCgst(arr.getCgst());
		obj.setIgst(arr.getIgst());
		obj.setInterest(arr.getIntr());
		obj.setLateFee(arr.getFee());
		obj.setPenalty(arr.getPen());
		obj.setSgst(arr.getSgst());

		return obj;
	}

	private Map<String, Gstr9DemandAndrefundDashboardDto> getDemandAndRefundMap() {
		Map<String, Gstr9DemandAndrefundDashboardDto> map = new LinkedHashMap<>();
		map.put("15A", new Gstr9DemandAndrefundDashboardDto("15A"));
		map.put("15B", new Gstr9DemandAndrefundDashboardDto("15B"));
		map.put("15C", new Gstr9DemandAndrefundDashboardDto("15C"));
		map.put("15D", new Gstr9DemandAndrefundDashboardDto("15D"));
		map.put("15E", new Gstr9DemandAndrefundDashboardDto("15E"));
		map.put("15F", new Gstr9DemandAndrefundDashboardDto("15F"));
		map.put("15G", new Gstr9DemandAndrefundDashboardDto("15G"));

		return map;
	}

	private void setSummaryValues(Gstr9DemandAndrefundDashboardDto gstr9gstnDto,
			Gstr9DemandAndRefundMapDto mapDto) {
		gstr9gstnDto.setCessGstn(mapDto.getCess());
		gstr9gstnDto.setCgstGstn(mapDto.getCgst());
		gstr9gstnDto.setIgstGstn(mapDto.getIgst());
		gstr9gstnDto.setInterestGstn(mapDto.getInterest());
		gstr9gstnDto.setLateFeeGstn(mapDto.getLateFee());
		gstr9gstnDto.setPenaltyGstn(mapDto.getPenalty());
		gstr9gstnDto.setSgstGstn(mapDto.getSgst());

	}

	private void setUserInputValues(
			Gstr9DemandAndrefundDashboardDto gstr9gstnDto,
			Gstr9DemandAndRefundMapDto mapDto) {
		gstr9gstnDto.setCessUserInput(mapDto.getCess());
		gstr9gstnDto.setCgstUserInput(mapDto.getCgst());
		gstr9gstnDto.setIgstUserInput(mapDto.getIgst());
		gstr9gstnDto.setInterestUserInput(mapDto.getInterest());
		gstr9gstnDto.setLateFeeUserInput(mapDto.getLateFee());
		gstr9gstnDto.setPenaltyUserInput(mapDto.getPenalty());
		gstr9gstnDto.setSgstUserInput(mapDto.getSgst());
	}

	private void setGstnAutoCalValues(
			Gstr9DemandAndrefundDashboardDto gstr9gstnDto,
			Gstr9DemandAndRefundMapDto mapDto) {
		gstr9gstnDto.setCessGstnCompute(mapDto.getCess());
		gstr9gstnDto.setCgstGstnCompute(mapDto.getCgst());
		gstr9gstnDto.setIgstGstnCompute(mapDto.getIgst());
		gstr9gstnDto.setInterestGstnCompute(mapDto.getInterest());
		gstr9gstnDto.setLateFeeGstnCompute(mapDto.getLateFee());
		gstr9gstnDto.setPenaltyGstnCompute(mapDto.getPenalty());
		gstr9gstnDto.setSgstGstnCompute(mapDto.getSgst());
	}

	@Override
	public String saveGstr9DemandsAndRefundsUserInputData(String gstin,
			String fy, String status,
			List<Gstr9DemandAndRefundMapDto> userInputList) {
		int deleteCount = 0;
		try {
			String taxPeriod = "03" + fy.substring(fy.lastIndexOf('-') + 1);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DemandsAndRefundServieImpl"
						+ ".saveGstr9DemandsAndRefundsUserInputData begin,"
						+ " for gstin : " + gstin + " and taxPeriod : " + " "
						+ taxPeriod);
			}
			String createdBy = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			List<Gstr9UserInputEntity> userInputEntities = userInputList
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
				LOGGER.info("Gstr9DemandsAndRefundServieImpl"
						+ ".saveGstr9DemandsAndRefundsUserInputData soft "
						+ "delete count " + deleteCount);
			}

			gstr9UserInputRepository.saveAll(userInputEntities);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DemandsAndRefundServieImpl"
						+ ".saveGstr9DemandsAndRefundsUserInputData "
						+ "Saved UserInput Successfully ");
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Gstr9 user Data ";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return "success";
	}

	private Gstr9UserInputEntity convertToEntity(String gstin, String taxPeriod,
			String fy, String createdBy, String status,
			Gstr9DemandAndRefundMapDto userInput) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr9DemandsAndRefundServieImpl"
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
		userEntity.setIntr(userInput.getInterest());
		userEntity.setPen(userInput.getPenalty());
		userEntity.setFee(userInput.getLateFee());

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

}
