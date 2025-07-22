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
import com.ey.advisory.app.docs.dto.gstr9.Gstr9CompositionDeemedSupplyDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9CompositionDeemedSupplyMapDto;
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
@Component("Gstr9CompositionDeemedSupplyServiceImpl")
public class Gstr9CompositionDeemedSupplyServiceImpl
		implements Gstr9CompositionDeemedSupplyService {

	@Autowired
	@Qualifier("Gstr9UserInputRepository")
	private Gstr9UserInputRepository gstr9UserInputRepository;

	@Autowired
	@Qualifier("Gstr9GetSummaryRepository")
	private Gstr9GetSummaryRepository gstr9GetSummaryRepository;
	
	@Autowired
	@Qualifier("Gstr9GetCallComputeRepository")
	private Gstr9GetCallComputeRepository gstr9GetCallComputRepo;

	private static ImmutableList<String> section = ImmutableList.of("16");

	@Override
	public List<Gstr9CompositionDeemedSupplyDto> 
	getGstr9CompositionDeemedSupplyDetails(String gstin, String taxPeriod, 
			String formattedFy) {

		List<Gstr9CompositionDeemedSupplyDto> respList = new ArrayList<>();
		try {
			Map<String, Gstr9CompositionDeemedSupplyDto> gstr9Map =
					getDemandAndRefundMap();

			List<Gstr9UserInputEntity> userInputArr = gstr9UserInputRepository
					.findByGstinAndFyAndSectionInAndIsActiveTrue(gstin,
							formattedFy, section);

			List<Gstr9CompositionDeemedSupplyMapDto> userInputList = 
					userInputArr
					.stream().map(o -> convertTaxPaid(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DemandsAndRefundServieImpl"
						+ ".userInputList   size = " + userInputArr.size()
						+ " now fetching data from getSummary table");
			}

			List<Gstr9GetSummaryEntity> getSummaryArr = 
					gstr9GetSummaryRepository
					.findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(gstin,
							taxPeriod, section);

			List<Gstr9CompositionDeemedSupplyMapDto> getSummaryList = 
					getSummaryArr
					.stream().map(o -> convertGstnTaxPaid(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DemandsAndRefundServieImpl"
						+ ".getSummaryList   size = " + getSummaryList.size());
			}
			
			//gstncompute 
			
			Integer fy = GenUtil
					.convertFytoIntFromReturnPeriod(taxPeriod);
			
			List<Gstr9GetCallComputeEntity> getGstnComputeArr = 
					gstr9GetCallComputRepo
							.findByGstinAndFinancialYearAndSectionInAndIsActiveTrue(
									gstin, fy, section);

			List<Gstr9CompositionDeemedSupplyMapDto> getGstnComputeList = 
					getGstnComputeArr
					.stream().map(o -> convertGstnComputeTaxPaid(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9DemandsAndRefundServieImpl"
					+ ".getGstnComputeList size = " + getSummaryList.size());
			}

			/*
			 * The below code will merge the input list by adding the rows which
			 * has same section
			 */
			Map<String, Gstr9CompositionDeemedSupplyMapDto> userMap = 
					userInputList
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
			Map<String, Gstr9CompositionDeemedSupplyMapDto> summaryMap = 
					getSummaryList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addUserDto(a, b)),
									Optional::get)));
			
			Map<String, Gstr9CompositionDeemedSupplyMapDto> gstnComputeMap = 
					getGstnComputeList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addUserDto(a, b)),
									Optional::get)));

			gstr9Map.replaceAll((k, v) -> {
				if (gstnComputeMap.containsKey(k))
					setGstnComputeValues(v, summaryMap.get(k));
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

	private Gstr9CompositionDeemedSupplyMapDto addUserDto(
			Gstr9CompositionDeemedSupplyMapDto a,
			Gstr9CompositionDeemedSupplyMapDto b) {
		Gstr9CompositionDeemedSupplyMapDto obj = new 
				Gstr9CompositionDeemedSupplyMapDto();
		obj.setCess(a.getCess().add(b.getCess()));
		obj.setCgst(a.getCgst().add(b.getCgst()));
		obj.setIgst(a.getIgst().add(b.getIgst()));
		obj.setTaxableValue(a.getTaxableValue().add(b.getTaxableValue()));
		obj.setSgst(a.getSgst().add(b.getSgst()));
		return obj;
	}

	private Gstr9CompositionDeemedSupplyMapDto convertTaxPaid(
			Gstr9UserInputEntity arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9UserInputEntity "
					+ " Gstr9CompositionDeemedSupplyMapDto object";
			LOGGER.debug(str);
		}
		Gstr9CompositionDeemedSupplyMapDto obj = new 
		Gstr9CompositionDeemedSupplyMapDto();
		obj.setSubSection(arr.getSubSection());
		obj.setCess(arr.getCess() != null ? arr.getCess() : BigDecimal.ZERO);
		obj.setCgst(arr.getCgst() != null ? arr.getCgst() : BigDecimal.ZERO);
		obj.setIgst(arr.getIgst() != null ? arr.getIgst() : BigDecimal.ZERO);
		obj.setTaxableValue(
				arr.getTxVal() != null ? arr.getTxVal() : BigDecimal.ZERO);
		obj.setSgst(arr.getSgst() != null ? arr.getSgst() : BigDecimal.ZERO);

		return obj;
	}

	private Gstr9CompositionDeemedSupplyMapDto convertGstnTaxPaid(
			Gstr9GetSummaryEntity arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9GetSummaryEntity "
					+ " Gstr9CompositionDeemedSupplyMapDto object";
			LOGGER.debug(str);
		}
		Gstr9CompositionDeemedSupplyMapDto obj = new 
				Gstr9CompositionDeemedSupplyMapDto();
		obj.setSubSection(arr.getSubSection());
		obj.setCess(arr.getCsamt() != null ? arr.getCsamt() : BigDecimal.ZERO);
		obj.setCgst(arr.getCamt() != null ? arr.getCamt() : BigDecimal.ZERO);
		obj.setIgst(arr.getIamt() != null ? arr.getIamt() : BigDecimal.ZERO);
		obj.setTaxableValue(
				arr.getTxVal() != null ? arr.getTxVal() : BigDecimal.ZERO);
		obj.setSgst(arr.getSamt() != null ? arr.getSamt() : BigDecimal.ZERO);

		return obj;
	}
	
	private Gstr9CompositionDeemedSupplyMapDto convertGstnComputeTaxPaid(
			Gstr9GetCallComputeEntity arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9GetCallComputeEntity "
					+ " Gstr9CompositionDeemedSupplyMapDto object";
			LOGGER.debug(str);
		}
		Gstr9CompositionDeemedSupplyMapDto obj = new 
				Gstr9CompositionDeemedSupplyMapDto();
		obj.setSubSection(arr.getSubSection());
		obj.setCess(arr.getCess() != null ? arr.getCess() : BigDecimal.ZERO);
		obj.setCgst(arr.getCgst() != null ? arr.getCgst() : BigDecimal.ZERO);
		obj.setIgst(arr.getIgst() != null ? arr.getIgst() : BigDecimal.ZERO);
		obj.setTaxableValue(
				arr.getTxVal() != null ? arr.getTxVal() : BigDecimal.ZERO);
		obj.setSgst(arr.getSgst() != null ? arr.getSgst() : BigDecimal.ZERO);

		return obj;
	}

	private Map<String, Gstr9CompositionDeemedSupplyDto> 
						getDemandAndRefundMap() {
		Map<String, Gstr9CompositionDeemedSupplyDto> map = new LinkedHashMap<>();
		map.put("16A", new Gstr9CompositionDeemedSupplyDto("16A"));
		map.put("16B", new Gstr9CompositionDeemedSupplyDto("16B"));
		map.put("16C", new Gstr9CompositionDeemedSupplyDto("16C"));

		return map;
	}

	private void setSummaryValues(Gstr9CompositionDeemedSupplyDto gstr9gstnDto,
			Gstr9CompositionDeemedSupplyMapDto mapDto) {
		gstr9gstnDto.setCessGstn(mapDto.getCess());
		gstr9gstnDto.setCgstGstn(mapDto.getCgst());
		gstr9gstnDto.setIgstGstn(mapDto.getIgst());
		gstr9gstnDto.setTaxableValueGstn(mapDto.getTaxableValue());
		gstr9gstnDto.setSgstGstn(mapDto.getSgst());

	}

	private void setGstnComputeValues(Gstr9CompositionDeemedSupplyDto gstr9gstnDto,
			Gstr9CompositionDeemedSupplyMapDto mapDto) {
		gstr9gstnDto.setCessGstnCompute(mapDto.getCess());
		gstr9gstnDto.setCgstGstnCompute(mapDto.getCgst());
		gstr9gstnDto.setIgstGstnCompute(mapDto.getIgst());
		gstr9gstnDto.setTaxableValueGstnCompute(mapDto.getTaxableValue());
		gstr9gstnDto.setSgstGstnCompute(mapDto.getSgst());

	}
	
	private void setUserInputValues(
			Gstr9CompositionDeemedSupplyDto gstr9gstnDto,
			Gstr9CompositionDeemedSupplyMapDto mapDto) {
		gstr9gstnDto.setCessUserInput(mapDto.getCess());
		gstr9gstnDto.setCgstUserInput(mapDto.getCgst());
		gstr9gstnDto.setIgstUserInput(mapDto.getIgst());
		gstr9gstnDto.setTaxableValueUserInput(mapDto.getTaxableValue());
		gstr9gstnDto.setSgstUserInput(mapDto.getSgst());
	}
	
	@Override
	public String saveGstr9CompositionDeemedSupplyUserInputData(String gstin,
			String fy, String status,
			List<Gstr9CompositionDeemedSupplyMapDto> userInputList) {

		int deleteCount = 0;
		try {
			String taxPeriod = "03" + fy.substring(fy.lastIndexOf('-') + 1);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9CompositionDeemedSupplyServiceImpl"
						+ ".saveGstr9CompositionDeemedSupplyUserInputData begin,"
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
						.softDeleteBasedOnGstinandFy(gstin, fy, section,
								"U", createdBy);
			} 
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9CompositionDeemedSupplyServiceImpl"
						+ ".saveGstr9CompositionDeemedSupplyUserInputData soft "
						+ "delete count " + deleteCount);
			}

			gstr9UserInputRepository.saveAll(userInputEntities);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9CompositionDeemedSupplyServiceImpl"
						+ ".saveGstr9CompositionDeemedSupplyUserInputData "
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
			Gstr9CompositionDeemedSupplyMapDto userInput) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr9CompositionDeemedSupplyServiceImpl"
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
		userEntity.setTxVal(userInput.getTaxableValue());
		
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
