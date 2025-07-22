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
import com.ey.advisory.app.docs.dto.gstr9.Gstr9InwardUserInputDTO;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9OutwardDashboardDTO;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr9OutwardTabServiceImpl")
@Slf4j
public class Gstr9OutwardTabServiceImpl implements Gstr9OutwardTabService {

	@Autowired
	private Gstr9DigiComputeRepository gstr9DigiComputeRepository;

	@Autowired
	private Gstr9AutoCalculateRepository gstr9AutoCalculateRepository;

	@Autowired
	private Gstr9UserInputRepository gstr9UserInputRepository;

	@Autowired
	private Gstr9GetSummaryRepository gstr9GetSummaryRepository;

	@Autowired
	private Gstr9GetCallComputeRepository gstr9GetCallCompRepository;

	@Autowired
	Gstr9OutwardUtil gstr9OutwardUtil;

	private static ImmutableList<String> outwardSectionList = ImmutableList
			.of("4", "5");

	@Override
	public Gstr9OutwardDashboardDTO getgstr9OutwardDashBoardData(String gstin,
			String taxPeriod, String fy) {
		Gstr9OutwardDashboardDTO gstr9OutWardResp = new Gstr9OutwardDashboardDTO(); // create
																					// new
																					// dto
		Map<String, Gstr9GstinInOutwardDashBoardDTO> gstr9map = gstr9OutwardUtil
				.getGstr9OutwardGstinDashboardMap();

		List<Gstr9GstinInOutwardDashBoardDTO> respList = null;
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9OutwardTabServiceImpl"
						+ ".Gstr9OutwardTabServiceImpl begin, fetching the data"
						+ "from compute table for gstin " + gstin
						+ " and taxPeriod" + " " + taxPeriod);
			}
			List<Gstr9DigiComputeEntity> gstr9DigiComputeEntityList = gstr9DigiComputeRepository
					.findByGstinAndSectionInAndRetPeriodAndIsActiveTrue(gstin,
							outwardSectionList,taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9InwardServiceImpl"
						+ ".getgstr9DashBoardList DigiCompute list size = "
						+ gstr9DigiComputeEntityList.size());
			}

			List<Gstr9AutoCalculateEntity> gstr9AutoCalculateEntityList = gstr9AutoCalculateRepository
					.findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(gstin,
							taxPeriod, outwardSectionList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9InwardServiceImpl"
						+ ".getgstr9DashBoardList AutoCalculate list size = "
						+ gstr9AutoCalculateEntityList.size());
			}

			List<Gstr9UserInputEntity> gstr9UserInputEntityList = gstr9UserInputRepository
					.findByGstinAndFyAndSectionInAndIsActiveTrue(gstin, fy,
							outwardSectionList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9InwardServiceImpl"
						+ ".getgstr9DashBoardList UserInput list size = "
						+ gstr9UserInputEntityList.size());
			}

			List<Gstr9GetSummaryEntity> gstr9GetSummaryEntityList = gstr9GetSummaryRepository
					.findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(gstin,
							taxPeriod, outwardSectionList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9InwardServiceImpl"
						+ ".getgstr9DashBoardList GetSummary list size = "
						+ gstr9GetSummaryEntityList.size());
			}

			Integer convertedFy = GenUtil
					.convertFytoIntFromReturnPeriod(taxPeriod);

			List<Gstr9GetCallComputeEntity> gstr9GetCompEntityList = gstr9GetCallCompRepository
					.findByGstinAndFinancialYearAndSectionInAndIsActiveTrue(
							gstin, convertedFy, outwardSectionList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9InwardServiceImpl"
						+ ".getgstr9DashBoardList gstr9GetCompEntityList list size = "
						+ gstr9GetCompEntityList.size());
			}

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

			gstr9map.replaceAll((k, v) -> {
				if (!v.getHeader()) {
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
				}
				return v;
			});

			respList = new ArrayList<>(gstr9map.values());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GStr9 Outward ServiceImpl"
						+ ".GStr9 Outward ServiceImpl returning response"
						+ ", response size = " + respList.size() + "");
			}
			gstr9OutWardResp.setGstr9OutwardDashboard(respList);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return gstr9OutWardResp;

	}

	private BigDecimal defaultToZeroIfNull(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	// Following Method used to Retrieve the Data

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

	private Gstr9InwardUserInputDTO convertuserInputEntityToDto(
			Gstr9UserInputEntity gstr9UserInputEntity) {
		Gstr9InwardUserInputDTO dto = new Gstr9InwardUserInputDTO();
		dto.setSection(gstr9UserInputEntity.getSection());
		dto.setSubSection(gstr9UserInputEntity.getSubSection());
		dto.setTaxableVal(defaultToZeroIfNull(gstr9UserInputEntity.getTxVal()));
		dto.setIgst(defaultToZeroIfNull(gstr9UserInputEntity.getIgst()));
		dto.setCgst(defaultToZeroIfNull(gstr9UserInputEntity.getCgst()));
		dto.setSgst(defaultToZeroIfNull(gstr9UserInputEntity.getSgst()));
		dto.setCess(defaultToZeroIfNull(gstr9UserInputEntity.getCess()));

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
		dto.setTaxableVal(
				defaultToZeroIfNull(gstr9AutoCalculateEntity.getTxVal()));
		dto.setIgst(defaultToZeroIfNull(gstr9AutoCalculateEntity.getIamt()));
		dto.setCgst(defaultToZeroIfNull(gstr9AutoCalculateEntity.getCamt()));
		dto.setSgst(defaultToZeroIfNull(gstr9AutoCalculateEntity.getSamt()));
		dto.setCess(defaultToZeroIfNull(gstr9AutoCalculateEntity.getCsamt()));

		return dto;
	}

	private Gstr9InwardUserInputDTO convertgetSummaryEntityToDto(
			Gstr9GetSummaryEntity gstr9GetSummaryEntity) {
		Gstr9InwardUserInputDTO dto = new Gstr9InwardUserInputDTO();
		dto.setSection(gstr9GetSummaryEntity.getSection());
		dto.setSubSection(gstr9GetSummaryEntity.getSubSection());
		dto.setTaxableVal(
				defaultToZeroIfNull(gstr9GetSummaryEntity.getTxVal()));
		dto.setIgst(defaultToZeroIfNull(gstr9GetSummaryEntity.getIamt()));
		dto.setCgst(defaultToZeroIfNull(gstr9GetSummaryEntity.getCamt()));
		dto.setSgst(defaultToZeroIfNull(gstr9GetSummaryEntity.getSamt()));
		dto.setCess(defaultToZeroIfNull(gstr9GetSummaryEntity.getCsamt()));

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
		gstr9AutoDto.setAutoCalTaxableVal(
				defaultToZeroIfNull(gstr9InwardUserInputDTO.getTaxableVal()));
	}

	private void setGstnValues(Gstr9GstinInOutwardDashBoardDTO gstr9GstnDto,
			Gstr9InwardUserInputDTO gstr9InwarGstnDTO) {
		gstr9GstnDto.setGstinCess(gstr9InwarGstnDTO.getCess());
		gstr9GstnDto.setGstinCgst(gstr9InwarGstnDTO.getCgst());
		gstr9GstnDto.setGstinIgst(gstr9InwarGstnDTO.getIgst());
		gstr9GstnDto.setGstinSgst(gstr9InwarGstnDTO.getSgst());
		gstr9GstnDto.setGstinTaxableVal(gstr9InwarGstnDTO.getTaxableVal());
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

	@Override
	public void saveGstr9OutwardUserInputData(String gstin, String fy,
			List<Gstr9GstinInOutwardDashBoardDTO> userInputList) {

		String taxPeriod = GenUtil.getFinancialPeriodFromFY(fy);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr9InwardServiceImpl"
					+ ".savegstr9DashboardUserInputs begin," + " for gstin : "
					+ gstin + " and taxPeriod : " + " " + taxPeriod);
		}

		String createdBy = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		List<Gstr9UserInputEntity> userInputEntities = userInputList
				.parallelStream()
				.map(o -> convertToEntity(gstin, taxPeriod, fy, createdBy, o))
				.collect(Collectors.toCollection(ArrayList::new));

		/*
		 * soft delete existing entries before saving
		 */

		int deleteCount = gstr9OutwardUtil.softDeleteData(gstin, fy,
				outwardSectionList, createdBy);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr9OutwardServiceImpl"
					+ ".savegstr9DashboardUserInputs soft delete count "
					+ deleteCount);
		}
		gstr9UserInputRepository.saveAll(userInputEntities);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr9InwardServiceImpl"
					+ ".savegstr9DashboardUserInputs Saved UserInput "
					+ "Successfully ");
		}
	}

	// Used for Saving
	private Gstr9UserInputEntity convertToEntity(String gstin, String taxPeriod,
			String fy, String createdBy,
			Gstr9GstinInOutwardDashBoardDTO userInput) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr9OutwardServiceImpl"
					+ ".convertToEntity method :");
		}
		String docKey = CommonUtility.generateGstr9DocKey(gstin, fy,
				userInput.getSubSection());
		Gstr9UserInputEntity userEntity = new Gstr9UserInputEntity();
		userEntity.setGstin(gstin);
		userEntity.setRetPeriod(taxPeriod);
		userEntity.setFy(fy);
		userEntity.setTxVal(userInput.getUserInputTaxableVal());
		userEntity.setIgst(userInput.getUserInputIgst());
		userEntity.setCgst(userInput.getUserInputCgst());
		userEntity.setSgst(userInput.getUserInputSgst());
		userEntity.setCess(userInput.getUserInputCess());
		if (userInput.getSubSection().contains("4")) {
			userEntity.setSection("4");
		} else if (userInput.getSubSection().contains("5")) {
			userEntity.setSection("5");
		}
		userEntity.setSubSection(userInput.getSubSection());
		userEntity.setSource("U");
		userEntity
				.setDerivedRetPeriod(GenUtil.convertTaxPeriodToInt(taxPeriod));
		userEntity.setNatureOfSupplies(Gstr9Util
				.getNatureOfSuppliesForSubSection(userInput.getSubSection()));
		userEntity.setActive(true);
		userEntity.setDocKey(docKey);
		userEntity.setCreatedOn(LocalDateTime.now());
		userEntity.setCreatedBy(createdBy);
		return userEntity;
	}
}
