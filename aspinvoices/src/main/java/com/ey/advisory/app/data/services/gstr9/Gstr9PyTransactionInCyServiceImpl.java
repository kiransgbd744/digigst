package com.ey.advisory.app.data.services.gstr9;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9UserInputEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9AutoCalculateRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9DigiComputeRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetCallComputeRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetSummaryRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9UserInputRepository;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9PyTransInCyDashboardDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9PyTransactionInCyMapDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr9PyTransactionInCyServiceImpl")
@Slf4j
public class Gstr9PyTransactionInCyServiceImpl
		implements Gstr9PyTransactionInCyService {

	@Autowired
	@Qualifier("Gstr9UserInputRepository")
	private Gstr9UserInputRepository gstr9UserInputRepository;

	@Autowired
	@Qualifier("Gstr9GetSummaryRepository")
	private Gstr9GetSummaryRepository gstr9GetSummaryRepository;

	@Autowired
	@Qualifier("Gstr9AutoCalculateRepository")
	private Gstr9AutoCalculateRepository gstr9AutoCalculateRepository;

	@Autowired
	private Gstr9PyTransInCyUtil pyTransInCyUtil;

	@Autowired
	@Qualifier("Gstr9GetCallComputeRepository")
	private Gstr9GetCallComputeRepository gstr9GetCallComputeRepository;

	private static ImmutableList<String> pyTransInCySectionList = ImmutableList
			.of("10", "11", "12", "13");

	private static final String updatedSource = "U";
	
	@Autowired
	private Gstr9DigiComputeRepository gstr9DigiComputeRepository;

	@Override
	public List<Gstr9PyTransInCyDashboardDto> getGstr9PyTransInCyDetails(
			String gstin, String fy) {
		List<Gstr9PyTransInCyDashboardDto> respList = null;
		try {

			String taxPeriod = "03" + fy.substring(fy.lastIndexOf('-') + 1);

			Map<String, Gstr9PyTransInCyDashboardDto> gstr9Map = pyTransInCyUtil
					.getGstr9PyTransInCyDashboardMap();
			
			List<Object[]> aspArr = gstr9DigiComputeRepository
					.getPyTransInCyASPData(gstin, taxPeriod,
							pyTransInCySectionList);
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9PyTransactionInCyServiceImpl"
						+ ".AsptArr   size = " + aspArr.size()
						+ " now fetching data from aspCompute table");
			}

			List<Object[]> userInputArr = gstr9UserInputRepository
					.getPyTransInCyUserInputData(gstin, fy,
							pyTransInCySectionList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9PyTransactionInCyServiceImpl"
						+ ".userInputArr   size = " + userInputArr.size()
						+ " now fetching data from getSummary " + "table");
			}

			List<Object[]> getSummaryArr = gstr9GetSummaryRepository
					.getPyTransInCySummData(gstin, taxPeriod,
							pyTransInCySectionList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9PyTransactionInCyServiceImpl"
						+ ".getSummaryArr   size = " + getSummaryArr.size()
						+ " now fetching data from autoCalculate " + "table");
			}

			// List<Object[]> autoCalculateArr = gstr9AutoCalculateRepository
			// .getGstr9PyTransInCyAutoCalData(gstin, taxPeriod,
			// pyTransInCySectionList);

			Integer convertedFy = GenUtil
					.convertFytoIntFromReturnPeriod(taxPeriod);

			List<Object[]> getCallCompuArr = gstr9GetCallComputeRepository
					.getPyTransInCyCompuData(gstin, convertedFy,
							pyTransInCySectionList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9PyTransactionInCyServiceImpl"
						+ ".getCallCompuArr   size = "
						+ getCallCompuArr.size());
			}
			
			List<Gstr9PyTransactionInCyMapDto> aspList = aspArr
					.stream().map(o -> convertPyTransInCy(o))
					.collect(Collectors.toCollection(ArrayList::new));
			
			List<Gstr9PyTransactionInCyMapDto> userInputList = userInputArr
					.stream().map(o -> convertPyTransInCy(o))
					.collect(Collectors.toCollection(ArrayList::new));

			List<Gstr9PyTransactionInCyMapDto> getSummaryList = getSummaryArr
					.stream().map(o -> convertPyTransInCy(o))
					.collect(Collectors.toCollection(ArrayList::new));

			// List<Gstr9PyTransactionInCyMapDto> autoCalculateList =
			// autoCalculateArr
			// .stream().map(o -> convertPyTransInCy(o))
			// .collect(Collectors.toCollection(ArrayList::new));

			List<Gstr9PyTransactionInCyMapDto> getCallCompuList = getCallCompuArr
					.stream().map(o -> convertPyTransInCy(o))
					.collect(Collectors.toCollection(ArrayList::new));
			
			
			Map<String, Gstr9PyTransactionInCyMapDto> aspMap = aspList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			Map<String, Gstr9PyTransactionInCyMapDto> userMap = userInputList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			Map<String, Gstr9PyTransactionInCyMapDto> summaryMap = getSummaryList
					.stream().collect(Collectors.toMap(o -> o.getSection(),
							Function.identity()));

			// Map<String, Gstr9PyTransactionInCyMapDto> autoCalMap =
			// autoCalculateList
			// .stream().collect(Collectors.toMap(o -> o.getSection(),
			// Function.identity()));

			Map<String, Gstr9PyTransactionInCyMapDto> getCallCompuMap = getCallCompuList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			gstr9Map.replaceAll((k, v) -> {
				
				if (aspMap.containsKey(k))
					setAspValues(v, aspMap.get(k));
				if (userMap.containsKey(k))
					setUserInputValues(v, userMap.get(k));
				if (summaryMap.containsKey(k))
					setSummaryValues(v, summaryMap.get(k));
				// if (autoCalMap.containsKey(k))
				// setAutoCalcValues(v, autoCalMap.get(k));
				if (getCallCompuMap.containsKey(k))
					setComputValues(v, getCallCompuMap.get(k));
				return v;
			});

			respList = new ArrayList<>(gstr9Map.values());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9PyTransactionInCyServiceImpl"
						+ ".getGstr9TaxPaidDetails returning response"
						+ ", response size = " + respList.size() + "");
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr9 TaxPaid Data ";
			LOGGER.error(msg, ex);

		}
		return respList;
	}

	private Gstr9PyTransactionInCyMapDto addDto(Gstr9PyTransactionInCyMapDto a,
			Gstr9PyTransactionInCyMapDto b) {
		Gstr9PyTransactionInCyMapDto dto = new Gstr9PyTransactionInCyMapDto();
		dto.setTaxableVal(a.getTaxableVal().add(b.getTaxableVal()));
		dto.setIgst(a.getIgst().add(b.getIgst()));
		dto.setCgst(a.getCgst().add(b.getCgst()));
		dto.setSgst(a.getSgst().add(b.getSgst()));
		dto.setCess(a.getCess().add(b.getCess()));
		return dto;
	}

	private void setComputValues(Gstr9PyTransInCyDashboardDto gstr9gstnDto,
			Gstr9PyTransactionInCyMapDto gstr9PyTransInCyMapDto) {
		gstr9gstnDto.setComputedCess(gstr9PyTransInCyMapDto.getCess());
		gstr9gstnDto.setComputedCgst(gstr9PyTransInCyMapDto.getCgst());
		gstr9gstnDto.setComputedIgst(gstr9PyTransInCyMapDto.getIgst());
		gstr9gstnDto.setComputedSgst(gstr9PyTransInCyMapDto.getSgst());
		gstr9gstnDto
				.setComputedTaxableVal(gstr9PyTransInCyMapDto.getTaxableVal());
	}

	// private void setAutoCalcValues(Gstr9PyTransInCyDashboardDto gstr9gstnDto,
	// Gstr9PyTransactionInCyMapDto gstr9PyTransInCyMapDto) {
	// gstr9gstnDto.setAutoCalCess(gstr9PyTransInCyMapDto.getCess());
	// gstr9gstnDto.setAutoCalCgst(gstr9PyTransInCyMapDto.getCgst());
	// gstr9gstnDto.setAutoCalIgst(gstr9PyTransInCyMapDto.getIgst());
	// gstr9gstnDto.setAutoCalSgst(gstr9PyTransInCyMapDto.getSgst());
	// gstr9gstnDto
	// .setAutoCalTaxableVal(gstr9PyTransInCyMapDto.getTaxableVal());
	// }

	private void setSummaryValues(Gstr9PyTransInCyDashboardDto gstr9gstnDto,
			Gstr9PyTransactionInCyMapDto gstr9PyTransInCyMapDto) {
		gstr9gstnDto.setGstinCess(gstr9PyTransInCyMapDto.getCess());
		gstr9gstnDto.setGstinCgst(gstr9PyTransInCyMapDto.getCgst());
		gstr9gstnDto.setGstinIgst(gstr9PyTransInCyMapDto.getIgst());
		gstr9gstnDto.setGstinSgst(gstr9PyTransInCyMapDto.getSgst());
		gstr9gstnDto.setGstinTaxableVal(gstr9PyTransInCyMapDto.getTaxableVal());
	}

	private void setUserInputValues(Gstr9PyTransInCyDashboardDto gstr9gstnDto,
			Gstr9PyTransactionInCyMapDto gstr9PyTransInCyMapDto) {
		gstr9gstnDto
				.setUserInputTaxableVal(gstr9PyTransInCyMapDto.getTaxableVal());
		gstr9gstnDto.setUserInputIgst(gstr9PyTransInCyMapDto.getIgst());
		gstr9gstnDto.setUserInputCgst(gstr9PyTransInCyMapDto.getCgst());
		gstr9gstnDto.setUserInputSgst(gstr9PyTransInCyMapDto.getSgst());
		gstr9gstnDto.setUserInputCess(gstr9PyTransInCyMapDto.getCess());
	}
	
	private void setAspValues(Gstr9PyTransInCyDashboardDto gstr9gstnDto,
			Gstr9PyTransactionInCyMapDto gstr9PyTransInCyMapDto) {
		gstr9gstnDto
				.setDigiComputeTaxableVal(gstr9PyTransInCyMapDto.getTaxableVal());
		gstr9gstnDto.setDigiComputeIgst(gstr9PyTransInCyMapDto.getIgst());
		gstr9gstnDto.setDigiComputeCgst(gstr9PyTransInCyMapDto.getCgst());
		gstr9gstnDto.setDigiComputeSgst(gstr9PyTransInCyMapDto.getSgst());
		gstr9gstnDto.setDigiComputeCess(gstr9PyTransInCyMapDto.getCess());
	}

	private Gstr9PyTransactionInCyMapDto convertPyTransInCy(Object[] arr) {
		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Gstr9PyTransactionInCyMapDto object";
			LOGGER.debug(str);
		}
		Gstr9PyTransactionInCyMapDto obj = new Gstr9PyTransactionInCyMapDto();
		obj.setTaxableVal((BigDecimal) arr[0]);
		obj.setSection((String) arr[1]);
		obj.setIgst((BigDecimal) arr[2]);
		obj.setCgst((BigDecimal) arr[3]);
		obj.setSgst((BigDecimal) arr[4]);
		obj.setCess((BigDecimal) arr[5]);
		return obj;
	}

	@Override
	@Transactional(value = "clientTransactionManager")
	public void saveGstr9PyTransInCyUserInput(
			List<Gstr9PyTransactionInCyMapDto> userInput, String gstin,
			String fy) {
		try {

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			String taxPeriod = "03" + fy.substring(fy.lastIndexOf('-') + 1);

			List<Gstr9UserInputEntity> entities = userInput.stream().map(
					o -> convertToEntity(o, gstin, fy, userName, taxPeriod))
					.collect(Collectors.toList());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9PyTransactionInCyServiceImpl : entity to be "
						+ "saved %s", entities.size());
			}

			gstr9UserInputRepository.softDeleteBasedOnSectionList(gstin,
					fy, userName, updatedSource, pyTransInCySectionList);

			gstr9UserInputRepository.saveAll(entities);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9PyTransactionInCyServiceImpl : "
						+ "successfully updated user input table");
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Gstr9 TaxPaid Data ";
			LOGGER.error(msg, ex);
			throw new AppException();

		}

	}

	private Gstr9UserInputEntity convertToEntity(
			Gstr9PyTransactionInCyMapDto userInput, String gstin, String fy,
			String userName, String taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Inside Gstr9PyTransactionInCyServiceImpl"
					+ ".convertToEntity method :");
		}
		String docKey = CommonUtility.generateGstr9DocKey(gstin, fy,
				userInput.getSection());
		Gstr9UserInputEntity userEntity = new Gstr9UserInputEntity();
		userEntity.setGstin(gstin);
		userEntity.setFy(fy);
		userEntity.setRetPeriod(taxPeriod);
		userEntity.setTxVal(userInput.getTaxableVal());
		userEntity.setCess(userInput.getCess());
		userEntity.setCgst(userInput.getCgst());
		userEntity.setIgst(userInput.getIgst());
		userEntity.setSgst(userInput.getSgst());
		userEntity.setDocKey(docKey);
		userEntity.setCreatedOn(LocalDateTime.now());
		userEntity.setCreatedBy(userName);
		userEntity.setSection(userInput.getSection());
		userEntity.setSubSection(userInput.getSection());
		userEntity.setNatureOfSupplies(Gstr9Util
				.getNatureOfSuppliesForSubSection(userInput.getSection()));
		userEntity.setActive(true);
		return userEntity;
	}

}
