package com.ey.advisory.app.data.services.gstr9;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9AutoCalculateEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9DigiComputeEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9GetCallComputeEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9GetSummaryEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9UserInputEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9AutoCalculateRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9DigiComputeRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetCallComputeRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetSummaryRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9UserInputRepository;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9GstinInOutwardDashBoardDTO;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9InwardDashboardDTO;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9InwardUserInputDTO;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Jithendra.B
 *
 */

@Slf4j
@Component("Gstr9InwardServiceImpl")
public class Gstr9InwardServiceImpl implements Gstr9InwardService {

	@Autowired
	private Gstr9DigiComputeRepository gstr9DigiComputeRepository;

	@Autowired
	private Gstr9AutoCalculateRepository gstr9AutoCalculateRepository;

	@Autowired
	private Gstr9UserInputRepository gstr9UserInputRepository;

	@Autowired
	private Gstr9GetSummaryRepository gstr9GetSummaryRepository;

	@Autowired
	Gstr9InwardUtil gstr9InwardUtil;

	@Autowired
	private Gstr9GetCallComputeRepository gstr9GetCallCompRepository;

	private static ImmutableList<String> inwardSectionList = ImmutableList
			.of("6", "7", "8");

	private static ImmutableList<String> inwardSubSectionList = ImmutableList
			.of("7H");

	private static ImmutableList<String> inwardAutoCalcList = ImmutableList
			.of("6A", "8A");

	@Override
	public Gstr9InwardDashboardDTO getgstr9InwardDashBoardData(String gstin,
			String fy) {

		Gstr9InwardDashboardDTO gstr9InwardResp = new Gstr9InwardDashboardDTO();

		String taxPeriod = Gstr9InwardUtil.getTaxperiodFromFY(fy);
		Map<String, Gstr9GstinInOutwardDashBoardDTO> gstr9map = gstr9InwardUtil
				.getGstr3BGstinDashboardMap();

		List<Gstr9GstinInOutwardDashBoardDTO> respList = null;

		String formattedFY = GenUtil.getFormattedFy(fy);

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9InwardDashboardServiceImpl"
						+ ".getgstr9InwardDashBoardList begin, fetching the data"
						+ "from compute table for gstin " + gstin
						+ " and taxPeriod" + " " + taxPeriod);
			}

			/*
			 * Get Data for all the required section
			 */
			List<Gstr9DigiComputeEntity> gstr9DigiComputeEntityList = gstr9DigiComputeRepository
					.findByGstinAndSectionInAndRetPeriodAndIsActiveTrue(gstin,
							inwardSectionList,taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9InwardServiceImpl"
						+ ".getgstr9DashBoardList DigiCompute list size = "
						+ gstr9DigiComputeEntityList.size());
			}

			List<Gstr9AutoCalculateEntity> gstr9AutoCalculateEntityList = gstr9AutoCalculateRepository
					.findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(gstin,
							taxPeriod, inwardSectionList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9InwardServiceImpl"
						+ ".getgstr9DashBoardList AutoCalculate list size = "
						+ gstr9AutoCalculateEntityList.size());
			}

			List<Gstr9UserInputEntity> gstr9UserInputEntityList = gstr9UserInputRepository
					.findByGstinAndFyAndSectionInAndIsActiveTrue(gstin,
							formattedFY, inwardSectionList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9InwardServiceImpl"
						+ ".getgstr9DashBoardList UserInput list size = "
						+ gstr9UserInputEntityList.size());
			}

			List<Gstr9GetSummaryEntity> gstr9GetSummaryEntityList = gstr9GetSummaryRepository
					.findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(gstin,
							taxPeriod, inwardSectionList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9InwardServiceImpl"
						+ ".getgstr9DashBoardList GetSummary list size = "
						+ gstr9GetSummaryEntityList.size());
			}
			Integer convertedFy = GenUtil
					.convertFytoIntFromReturnPeriod(taxPeriod);

			List<Gstr9GetCallComputeEntity> gstr9GetCompEntityList = gstr9GetCallCompRepository
					.findByGstinAndFinancialYearAndSectionInAndIsActiveTrue(
							gstin, convertedFy, inwardSectionList);

			/*
			 * convert all the entity lists to Gstr9InwardUserInputDTO list
			 */
			List<Gstr9InwardUserInputDTO> digiComputeDtoList = gstr9DigiComputeEntityList
					.stream().map(o -> convertDigiComputeEntityToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			List<Gstr9InwardUserInputDTO> autoCalculateDtoList = gstr9AutoCalculateEntityList
					.stream().map(o -> convertAutoCalcEntityToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			List<Gstr9InwardUserInputDTO> userInputDtoList = gstr9UserInputEntityList
					.stream().map(o -> convertuserInputEntityToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			List<Gstr9InwardUserInputDTO> getSummaryDtoList = gstr9GetSummaryEntityList
					.stream().map(o -> convertgetSummaryEntityToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			List<Gstr9InwardUserInputDTO> getCompDtoList = gstr9GetCompEntityList
					.stream().map(o -> convertGetCallCompEntityToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			/*
			 * Assign Auto Calculated 6A and 8A values to User edited 6A and 8A
			 */
			List<Gstr9InwardUserInputDTO> autoCalcInuserInputDtoList = convertAutoCalcEntityToUserinputDto(
					gstr9AutoCalculateEntityList);
			if (autoCalcInuserInputDtoList != null) {
				userInputDtoList.addAll(autoCalcInuserInputDtoList);
			}

			/*
			 * The below code will merge the dto list by adding the rows which
			 * has same section and makes it map
			 */
			Map<String, Gstr9InwardUserInputDTO> digiComputeMap = digiComputeDtoList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			Map<String, Gstr9InwardUserInputDTO> autoCalculateMap = autoCalculateDtoList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			Map<String, Gstr9InwardUserInputDTO> userInputMap = userInputDtoList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			Map<String, Gstr9InwardUserInputDTO> getSummaryMap = getSummaryDtoList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			Map<String, Gstr9InwardUserInputDTO> getCompDataMap = getCompDtoList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			/*
			 * Hardcoded gstr9map will be replaced with the respective values if
			 * avalaible. if Header is true, only section remains and values
			 * will be set to default
			 */
			gstr9map.replaceAll((k, v) -> {
				if (digiComputeMap.containsKey(k))
					setDigiComputeValues(v, digiComputeMap.get(k));
				if (autoCalculateMap.containsKey(k))
					setAutoCalValues(v, autoCalculateMap.get(k));
				if (userInputMap.containsKey(k))
					setUserInputValues(v, userInputMap.get(k));
				if (getSummaryMap.containsKey(k))
					setGstnValues(v, getSummaryMap.get(k));
				if (getCompDataMap.containsKey(k))
					setGetCallCompValues(v, getCompDataMap.get(k));

				return v;
			});

			respList = new ArrayList<>(gstr9map.values());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinDashboardServiceImpl"
						+ ".getgstr3bgstnDashBoardList returning response"
						+ ", response size = " + respList.size() + "");
			}
			gstr9InwardResp.setGstr9InwardDashboard(respList);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return gstr9InwardResp;
	}

	/*
	 * Below same method being used for inward dashboard save and for section7H
	 * -otherReversals.Status "SAVE" for dashboard data, "7H_SAVE" for
	 * otherReversals
	 * 
	 */
	@Override
	@Transactional(value = "clientTransactionManager")
	public void saveGstr9InwardUserInputData(String gstin, String fy,
			String status, List<Gstr9InwardUserInputDTO> userInputList) {
		int deleteCount = 0;
		try {
			String taxPeriod = Gstr9InwardUtil.getTaxperiodFromFY(fy);
			String formattedFY = GenUtil.getFormattedFy(fy);
			Map<String, String> itcTypeMap = gstr9InwardUtil.getItcTypeMap();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9InwardServiceImpl"
						+ ".savegstr9DashboardUserInputs begin,"
						+ " for gstin : " + gstin + " and taxPeriod : " + " "
						+ taxPeriod);
			}
			String createdBy = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			List<Gstr9UserInputEntity> userInputEntities = userInputList
					.parallelStream()
					.map(o -> convertToEntity(gstin, taxPeriod, formattedFY,
							createdBy, status, o, itcTypeMap))
					.collect(Collectors.toCollection(ArrayList::new));

			/*
			 * soft delete existing entries before saving
			 */

			if (status.equals("SAVE")) {
				deleteCount = gstr9UserInputRepository
						.softDeleteBasedOnGstinandFy(gstin, formattedFY,
								inwardSectionList, "U", createdBy);
			} else if (status.equals("7H_SAVE")) {
				deleteCount = gstr9UserInputRepository
						.softDeleteBasedOnsubSection(gstin, formattedFY,
								createdBy, "U", "7H");
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9InwardServiceImpl"
						+ ".savegstr9DashboardUserInputs soft delete count "
						+ deleteCount);
			}

			gstr9UserInputRepository.saveAll(userInputEntities);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9InwardServiceImpl"
						+ ".savegstr9DashboardUserInputs Saved UserInput "
						+ "Successfully ");
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Gstr9 user Data ";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	@Override
	public List<Gstr9InwardUserInputDTO> getGstr9Inward7HPopUpData(String gstin,
			String fy) {

		List<Gstr9InwardUserInputDTO> userInputOtherReversalDtoList = null;

		try {

			String taxPeriod = Gstr9InwardUtil.getTaxperiodFromFY(fy);
			String formattedFY = GenUtil.getFormattedFy(fy);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9InwardDashboardServiceImpl"
						+ ".getGstr9Inward7HPopUpData begin, fetching the data"
						+ "from compute table for gstin " + gstin
						+ " and taxPeriod" + " " + formattedFY);
			}

			List<Gstr9UserInputEntity> gstr9UserInputEntityList = gstr9UserInputRepository
					.findByGstinAndFyAndSubSectionInAndIsActiveTrue(gstin,
							formattedFY, inwardSubSectionList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9InwardServiceImpl"
						+ ".getGstr9Inward7HPopUpData UserInput list size = "
						+ gstr9UserInputEntityList.size());
			}

			userInputOtherReversalDtoList = gstr9UserInputEntityList.stream()
					.map(o -> convertuserInputEntityToOtherReversal7HDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			String msg = "Unexpected error while getting Gstr9 other Reversal user Data ";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return userInputOtherReversalDtoList;
	}

	private Gstr9UserInputEntity convertToEntity(String gstin, String taxPeriod,
			String fy, String createdBy, String status,
			Gstr9InwardUserInputDTO userInput, Map<String, String> itcTypeMap) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr9InwardServiceImpl"
					+ ".convertToEntity method :");
		}

		String docKey = CommonUtility.generateGstr9DocKey(gstin, fy,
				userInput.getSubSection());

		Gstr9UserInputEntity userEntity = new Gstr9UserInputEntity();

		if (status.equals("7H_SAVE")) {
			userEntity.setDesc(userInput.getParticulers());
		}

		userEntity.setGstin(gstin);
		userEntity.setRetPeriod(taxPeriod);
		userEntity.setFy(fy);
		userEntity.setTxVal(userInput.getTaxableVal());
		userEntity.setIgst(userInput.getIgst());
		userEntity.setCgst(userInput.getCgst());
		userEntity.setSgst(userInput.getSgst());
		userEntity.setCess(userInput.getCess());
		userEntity.setItcTyp(itcTypeMap.get(userInput.getSubSection()));
		if (userInput.getSubSection().contains("6")) {
			userEntity.setSection("6");
		} else if (userInput.getSubSection().contains("7")) {
			userEntity.setSection("7");
		} else if (userInput.getSubSection().contains("8")) {
			userEntity.setSection("8");
		}
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

	private Gstr9InwardUserInputDTO addDto(Gstr9InwardUserInputDTO a,
			Gstr9InwardUserInputDTO b) {
		Gstr9InwardUserInputDTO dto = new Gstr9InwardUserInputDTO();
		dto.setCess(a.getCess().add(b.getCess()));
		dto.setCgst(a.getCgst().add(b.getCgst()));
		dto.setIgst(a.getIgst().add(b.getIgst()));
		dto.setSgst(a.getSgst().add(b.getSgst()));
		dto.setTaxableVal(a.getTaxableVal().add(b.getTaxableVal()));
		return dto;
	}

	private List<Gstr9InwardUserInputDTO> convertAutoCalcEntityToUserinputDto(
			List<Gstr9AutoCalculateEntity> gstr9AutoCalculateEntityList) {
		List<Gstr9InwardUserInputDTO> list = null;

		for (Gstr9AutoCalculateEntity gstr9AutoCalculateEntity : gstr9AutoCalculateEntityList) {
			if (inwardAutoCalcList
					.contains(gstr9AutoCalculateEntity.getSubSection())) {
				if (list == null)
					list = new ArrayList<>();

				Gstr9InwardUserInputDTO dto = new Gstr9InwardUserInputDTO();
				dto.setSection(gstr9AutoCalculateEntity.getSection());
				dto.setSubSection(gstr9AutoCalculateEntity.getSubSection());
				dto.setTaxableVal(gstr9AutoCalculateEntity.getTxVal());
				dto.setIgst(gstr9AutoCalculateEntity.getIamt());
				dto.setCgst(gstr9AutoCalculateEntity.getCamt());
				dto.setSgst(gstr9AutoCalculateEntity.getSamt());
				dto.setCess(gstr9AutoCalculateEntity.getCsamt());
				list.add(dto);
			}
		}
		return list;
	}

	private Gstr9InwardUserInputDTO convertGetCallCompEntityToDto(
			Gstr9GetCallComputeEntity gstr9GetCallCompEntity) {
		Gstr9InwardUserInputDTO dto = new Gstr9InwardUserInputDTO();
		dto.setSection(gstr9GetCallCompEntity.getSection());
		dto.setSubSection(gstr9GetCallCompEntity.getSubSection());
		dto.setTaxableVal(
				defaultToZeroIfNull(gstr9GetCallCompEntity.getTxVal()));
		dto.setIgst(defaultToZeroIfNull(gstr9GetCallCompEntity.getIgst()));
		dto.setCgst(defaultToZeroIfNull(gstr9GetCallCompEntity.getCgst()));
		dto.setSgst(defaultToZeroIfNull(gstr9GetCallCompEntity.getSgst()));
		dto.setCess(defaultToZeroIfNull(gstr9GetCallCompEntity.getCess()));

		return dto;
	}

	private Gstr9InwardUserInputDTO convertDigiComputeEntityToDto(
			Gstr9DigiComputeEntity gstr9AutoCalculateEntity) {
		Gstr9InwardUserInputDTO dto = new Gstr9InwardUserInputDTO();
		dto.setSection(gstr9AutoCalculateEntity.getSection());
		dto.setSubSection(gstr9AutoCalculateEntity.getSubSection());
		dto.setTaxableVal(defaultToZeroIfNull(
				gstr9AutoCalculateEntity.getTaxableValue()));
		dto.setIgst(
				defaultToZeroIfNull(gstr9AutoCalculateEntity.getIgstAmount()));
		dto.setCgst(
				defaultToZeroIfNull(gstr9AutoCalculateEntity.getCgstAmount()));
		dto.setSgst(
				defaultToZeroIfNull(gstr9AutoCalculateEntity.getSgstAmount()));
		dto.setCess(
				defaultToZeroIfNull(gstr9AutoCalculateEntity.getCessAmount()));

		return dto;
	}

	private Gstr9InwardUserInputDTO convertAutoCalcEntityToDto(
			Gstr9AutoCalculateEntity gstr9AutoCalculateEntity) {
		Gstr9InwardUserInputDTO dto = new Gstr9InwardUserInputDTO();
		dto.setSection(gstr9AutoCalculateEntity.getSection());
		dto.setSubSection(gstr9AutoCalculateEntity.getSubSection());
		dto.setTaxableVal(gstr9AutoCalculateEntity.getTxVal());
		dto.setIgst(gstr9AutoCalculateEntity.getIamt());
		dto.setCgst(gstr9AutoCalculateEntity.getCamt());
		dto.setSgst(gstr9AutoCalculateEntity.getSamt());
		dto.setCess(gstr9AutoCalculateEntity.getCsamt());

		return dto;
	}

	private Gstr9InwardUserInputDTO convertuserInputEntityToOtherReversal7HDto(
			Gstr9UserInputEntity gstr9UserInputEntity) {
		Gstr9InwardUserInputDTO dto = new Gstr9InwardUserInputDTO();

		dto.setSubSection(
				!Strings.isNullOrEmpty(gstr9UserInputEntity.getSubSection())
						? gstr9UserInputEntity.getSubSection() : "7H");
		dto.setParticulers(
				!Strings.isNullOrEmpty(gstr9UserInputEntity.getDesc())
						? gstr9UserInputEntity.getDesc() : "");

		dto.setTaxableVal(gstr9UserInputEntity.getTxVal());
		dto.setIgst(gstr9UserInputEntity.getIgst());
		dto.setCgst(gstr9UserInputEntity.getCgst());
		dto.setSgst(gstr9UserInputEntity.getSgst());
		dto.setCess(gstr9UserInputEntity.getCess());

		return dto;
	}

	private Gstr9InwardUserInputDTO convertuserInputEntityToDto(
			Gstr9UserInputEntity gstr9UserInputEntity) {
		Gstr9InwardUserInputDTO dto = new Gstr9InwardUserInputDTO();
		dto.setSection(gstr9UserInputEntity.getSection());
		dto.setSubSection(gstr9UserInputEntity.getSubSection());
		dto.setTaxableVal(gstr9UserInputEntity.getTxVal());
		dto.setIgst(gstr9UserInputEntity.getIgst());
		dto.setCgst(gstr9UserInputEntity.getCgst());
		dto.setSgst(gstr9UserInputEntity.getSgst());
		dto.setCess(gstr9UserInputEntity.getCess());

		return dto;
	}

	private Gstr9InwardUserInputDTO convertgetSummaryEntityToDto(
			Gstr9GetSummaryEntity gstr9GetSummaryEntity) {
		Gstr9InwardUserInputDTO dto = new Gstr9InwardUserInputDTO();
		dto.setSection(gstr9GetSummaryEntity.getSection());
		dto.setSubSection(gstr9GetSummaryEntity.getSubSection());
		dto.setTaxableVal(gstr9GetSummaryEntity.getTxVal());
		dto.setIgst(gstr9GetSummaryEntity.getIamt());
		dto.setCgst(gstr9GetSummaryEntity.getCamt());
		dto.setSgst(gstr9GetSummaryEntity.getSamt());
		dto.setCess(gstr9GetSummaryEntity.getCsamt());

		return dto;
	}

	private void setUserInputValues(
			Gstr9GstinInOutwardDashBoardDTO gst9UserInputDto,
			Gstr9InwardUserInputDTO gstr9InwardUserInputDTO) {
		gst9UserInputDto.setUserInputCess(gstr9InwardUserInputDTO.getCess());
		gst9UserInputDto.setUserInputCgst(gstr9InwardUserInputDTO.getCgst());
		gst9UserInputDto.setUserInputIgst(gstr9InwardUserInputDTO.getIgst());
		gst9UserInputDto.setUserInputSgst(gstr9InwardUserInputDTO.getSgst());
		gst9UserInputDto.setUserInputTaxableVal(
				gstr9InwardUserInputDTO.getTaxableVal());

	}


	private void setDigiComputeValues(
			Gstr9GstinInOutwardDashBoardDTO gstr9DigiComputeDto,
			Gstr9InwardUserInputDTO gstr9InwardDigiInputDTO) {
		gstr9DigiComputeDto.setDigiComputeCess(gstr9InwardDigiInputDTO.getCess());
		gstr9DigiComputeDto.setDigiComputeCgst(gstr9InwardDigiInputDTO.getCgst());
		gstr9DigiComputeDto.setDigiComputeIgst(gstr9InwardDigiInputDTO.getIgst());
		gstr9DigiComputeDto.setDigiComputeSgst(gstr9InwardDigiInputDTO.getSgst());
		gstr9DigiComputeDto.setDigiComputeTaxableVal(
				defaultToZeroIfNull(gstr9InwardDigiInputDTO.getTaxableVal()));
	}

	private void setAutoCalValues(Gstr9GstinInOutwardDashBoardDTO gstr9AutoDto,
			Gstr9InwardUserInputDTO gstr9InwardUserInputDTO) {
		gstr9AutoDto.setAutoCalCess(gstr9InwardUserInputDTO.getCess());
		gstr9AutoDto.setAutoCalCgst(gstr9InwardUserInputDTO.getCgst());
		gstr9AutoDto.setAutoCalIgst(gstr9InwardUserInputDTO.getIgst());
		gstr9AutoDto.setAutoCalSgst(gstr9InwardUserInputDTO.getSgst());
		gstr9AutoDto
				.setAutoCalTaxableVal(gstr9InwardUserInputDTO.getTaxableVal());
	}

	private void setGstnValues(Gstr9GstinInOutwardDashBoardDTO gstr9GstnDto,
			Gstr9InwardUserInputDTO gstr9InwarGstnDTO) {
		gstr9GstnDto
				.setGstinCess(defaultToZeroIfNull(gstr9InwarGstnDTO.getCess()));
		gstr9GstnDto
				.setGstinCgst(defaultToZeroIfNull(gstr9InwarGstnDTO.getCgst()));
		gstr9GstnDto
				.setGstinIgst(defaultToZeroIfNull(gstr9InwarGstnDTO.getIgst()));
		gstr9GstnDto
				.setGstinSgst(defaultToZeroIfNull(gstr9InwarGstnDTO.getSgst()));
		gstr9GstnDto.setGstinTaxableVal(
				defaultToZeroIfNull(gstr9InwarGstnDTO.getTaxableVal()));
	}

	private void setGetCallCompValues(
			Gstr9GstinInOutwardDashBoardDTO gstr9GstnDto,
			Gstr9InwardUserInputDTO gstr9InwarGstnDTO) {
		gstr9GstnDto.setGetCompCess(gstr9InwarGstnDTO.getCess());
		gstr9GstnDto.setGetCompCgst(gstr9InwarGstnDTO.getCgst());
		gstr9GstnDto.setGetCompIgst(gstr9InwarGstnDTO.getIgst());
		gstr9GstnDto.setGetCompSgst(gstr9InwarGstnDTO.getSgst());
		gstr9GstnDto.setGetCompTaxableVal(gstr9InwarGstnDTO.getTaxableVal());
	}

	private BigDecimal defaultToZeroIfNull(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

}
