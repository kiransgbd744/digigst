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

import com.ey.advisory.app.data.entities.gstr9.Gstr9GetCallComputeEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9UserInputEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9AutoCalculateRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetCallComputeRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetSummaryRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9UserInputRepository;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9TaxPaidDashboardDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9TaxPaidMapDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr9TaxPaidTabServiceImpl")
@Slf4j
public class Gstr9TaxPaidTabServiceImpl implements Gstr9TaxPaidTabService {

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
	@Qualifier("Gstr9GetCallComputeRepository")
	private Gstr9GetCallComputeRepository gstr9GetCallComputeRepository;

	private static final String updatedSource = "U";

	public List<Gstr9TaxPaidDashboardDto> getGstr9TaxPaidDetails(String gstin,
			String fy) {
		List<Gstr9TaxPaidDashboardDto> respList = null;
		try {

			String taxPeriod = "03" + fy.substring(fy.lastIndexOf('-') + 1);

			Map<String, Gstr9TaxPaidDashboardDto> gstr9Map = Gstr9TaxPaidUtil
					.getGstr9TaxPaidDashboardMap();

			Gstr9UserInputEntity userInputEntity = gstr9UserInputRepository
					.findByGstinAndFyAndSectionAndIsActiveTrue(gstin, fy,
							Gstr9TaxPaidConstants.Table_9);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9TaxPaidTabServiceImpl" + ".userInputEntity"
						+ " now fetching data from getSummary table");
			}

			List<Object[]> getSummaryArr = gstr9GetSummaryRepository
					.getGstr9TaxPaidGetSummData(gstin, taxPeriod,
							Gstr9TaxPaidConstants.Table_9);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9TaxPaidTabServiceImpl"
						+ ".getSummaryArr   size = " + getSummaryArr.size()
						+ " now fetching data from autoCalculate table");
			}

			List<Object[]> autoCalculateArr = gstr9AutoCalculateRepository
					.getGstr9TaxPaidAutoCalData(gstin, taxPeriod,
							Gstr9TaxPaidConstants.Table_9);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9TaxPaidTabServiceImpl"
						+ ".autoCalculateArr   size = "
						+ autoCalculateArr.size()
						+ " now fetching data from Get Call Compute table");
			}

			Integer convertedFy = GenUtil
					.convertFytoIntFromReturnPeriod(taxPeriod);

			List<Gstr9GetCallComputeEntity> getCallCompEntities = gstr9GetCallComputeRepository
					.findByGstinAndFinancialYearAndSectionAndIsActiveTrue(
							gstin, convertedFy, Gstr9TaxPaidConstants.Table_9);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9TaxPaidTabServiceImpl" + ".getCallCompArr ");
			}
			List<Gstr9TaxPaidMapDto> userInputList = new ArrayList<>();
			convertTaxPaidUserInput(userInputEntity, userInputList);

			List<Gstr9TaxPaidMapDto> getSummaryList = getSummaryArr.stream()
					.map(o -> convertTaxPaid(o))
					.collect(Collectors.toCollection(ArrayList::new));

			List<Gstr9TaxPaidMapDto> autoCalculateList = autoCalculateArr
					.stream().map(o -> convertTaxPaid(o))
					.collect(Collectors.toCollection(ArrayList::new));

			List<Gstr9TaxPaidMapDto> getCallCompList = getCallCompEntities
					.stream().map(o -> fromGetCalComp(o))
					.collect(Collectors.toCollection(ArrayList::new));

			Map<String, Gstr9TaxPaidMapDto> userMap = userInputList.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			Map<String, Gstr9TaxPaidMapDto> summaryMap = getSummaryList.stream()
					.collect(Collectors.toMap(o -> o.getSubSection(),
							Function.identity()));

			Map<String, Gstr9TaxPaidMapDto> autoCalMap = autoCalculateList
					.stream().collect(Collectors.toMap(o -> o.getSubSection(),
							Function.identity()));

			Map<String, Gstr9TaxPaidMapDto> getCallCompMap = getCallCompList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			gstr9Map.replaceAll((k, v) -> {

				if (userMap.containsKey(k))
					setUserInputValues(v, userMap.get(k));
				if (summaryMap.containsKey(k))
					setSummaryValues(v, summaryMap.get(k));
				if (autoCalMap.containsKey(k))
					setAutoCalcValues(v, autoCalMap.get(k));
				if (getCallCompMap.containsKey(k))
					setComputedValues(v, getCallCompMap.get(k));
				return v;
			});

			respList = new ArrayList<>(gstr9Map.values());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9TaxPaidTabServiceImpl"
						+ ".getGstr9TaxPaidDetails returning response"
						+ ", response size = " + respList.size() + "");
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr9 TaxPaid Data ";
			LOGGER.error(msg, ex);

		}
		return respList;
	}

	private Gstr9TaxPaidMapDto addDto(Gstr9TaxPaidMapDto a,
			Gstr9TaxPaidMapDto b) {
		Gstr9TaxPaidMapDto dto = new Gstr9TaxPaidMapDto();
		dto.setTaxableVal(defaultToZeroIfNull(a.getTaxableVal())
				.add(defaultToZeroIfNull(b.getTaxableVal())));
		dto.setPaidThrCash(defaultToZeroIfNull(a.getPaidThrCash())
				.add(defaultToZeroIfNull(b.getPaidThrCash())));
		dto.setPaidThrItcIgst(defaultToZeroIfNull(a.getPaidThrItcIgst())
				.add(defaultToZeroIfNull(b.getPaidThrItcIgst())));
		dto.setPaidThrItcCgst(defaultToZeroIfNull(a.getPaidThrItcCgst())
				.add(defaultToZeroIfNull(b.getPaidThrItcCgst())));
		dto.setPaidThrItcSgst(defaultToZeroIfNull(a.getPaidThrItcSgst())
				.add(defaultToZeroIfNull(b.getPaidThrItcSgst())));
		dto.setPaidThrItcCess(defaultToZeroIfNull(a.getPaidThrItcCess())
				.add(defaultToZeroIfNull(b.getPaidThrItcCess())));
		return dto;
	}

	private BigDecimal defaultToZeroIfNull(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	private void convertTaxPaidUserInput(Gstr9UserInputEntity entity,
			List<Gstr9TaxPaidMapDto> userInputList) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic convertTaxPaidUserInput object to"
					+ " Gstr9TaxPaidMapDto object";
			LOGGER.debug(str);
		}

		BigDecimal defaultValue = BigDecimal.ZERO;

		Gstr9TaxPaidMapDto obj1 = new Gstr9TaxPaidMapDto();
		Gstr9TaxPaidMapDto obj2 = new Gstr9TaxPaidMapDto();
		Gstr9TaxPaidMapDto obj3 = new Gstr9TaxPaidMapDto();
		Gstr9TaxPaidMapDto obj4 = new Gstr9TaxPaidMapDto();
		Gstr9TaxPaidMapDto obj5 = new Gstr9TaxPaidMapDto();
		Gstr9TaxPaidMapDto obj6 = new Gstr9TaxPaidMapDto();
		Gstr9TaxPaidMapDto obj7 = new Gstr9TaxPaidMapDto();
		Gstr9TaxPaidMapDto obj8 = new Gstr9TaxPaidMapDto();
		if (entity == null) {
			obj1.setTaxableVal(defaultValue);
			obj2.setTaxableVal(defaultValue);
			obj3.setTaxableVal(defaultValue);
			obj4.setTaxableVal(defaultValue);
			obj5.setTaxableVal(defaultValue);
			obj6.setTaxableVal(defaultValue);
			obj7.setTaxableVal(defaultValue);
			obj8.setTaxableVal(defaultValue);
		} else {
			obj1.setTaxableVal(
					entity.getIgst() != null ? entity.getIgst() : defaultValue);
			obj2.setTaxableVal(
					entity.getCgst() != null ? entity.getCgst() : defaultValue);
			obj3.setTaxableVal(
					entity.getSgst() != null ? entity.getSgst() : defaultValue);
			obj4.setTaxableVal(
					entity.getCess() != null ? entity.getCess() : defaultValue);
			obj5.setTaxableVal(
					entity.getIntr() != null ? entity.getIntr() : defaultValue);
			obj6.setTaxableVal(
					entity.getFee() != null ? entity.getFee() : defaultValue);
			obj7.setTaxableVal(
					entity.getPen() != null ? entity.getPen() : defaultValue);
			obj8.setTaxableVal(
					entity.getOth() != null ? entity.getOth() : defaultValue);
		}
		obj1.setSubSection(Gstr9TaxPaidConstants.Table_9A);
		obj2.setSubSection(Gstr9TaxPaidConstants.Table_9B);
		obj3.setSubSection(Gstr9TaxPaidConstants.Table_9C);
		obj4.setSubSection(Gstr9TaxPaidConstants.Table_9D);
		obj5.setSubSection(Gstr9TaxPaidConstants.Table_9E);
		obj6.setSubSection(Gstr9TaxPaidConstants.Table_9F);
		obj7.setSubSection(Gstr9TaxPaidConstants.Table_9G);
		obj8.setSubSection(Gstr9TaxPaidConstants.Table_9H);

		userInputList.add(obj1);
		userInputList.add(obj2);
		userInputList.add(obj3);
		userInputList.add(obj4);
		userInputList.add(obj5);
		userInputList.add(obj6);
		userInputList.add(obj7);
		userInputList.add(obj8);

	}

	private Gstr9TaxPaidMapDto convertTaxPaid(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " Gstr9TaxPaidMapDto object";
			LOGGER.debug(str);
		}
		Gstr9TaxPaidMapDto obj = new Gstr9TaxPaidMapDto();
		obj.setTaxableVal((BigDecimal) arr[0]);
		obj.setSubSection((String) arr[1]);
		obj.setPaidThrCash((BigDecimal) arr[2]);
		obj.setPaidThrItcIgst((BigDecimal) arr[3]);
		obj.setPaidThrItcCgst((BigDecimal) arr[4]);
		obj.setPaidThrItcSgst((BigDecimal) arr[5]);
		obj.setPaidThrItcCess((BigDecimal) arr[6]);
		return obj;
	}

	private Gstr9TaxPaidMapDto fromGetCalComp(
			Gstr9GetCallComputeEntity entity) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9GetCallComputeEntity object to"
					+ " Gstr9TaxPaidMapDto object";
			LOGGER.debug(str);
		}
		Gstr9TaxPaidMapDto obj = new Gstr9TaxPaidMapDto();
		obj.setTaxableVal(entity.getTxPyble());
		obj.setSubSection(entity.getSubSection());
		if (entity.getSubSection().equals("9A")) {
			obj.setPaidThrCash(entity.getIgst());
		} else if (entity.getSubSection().equals("9B")) {
			obj.setPaidThrCash(entity.getCgst());
		} else if (entity.getSubSection().equals("9C")) {
			obj.setPaidThrCash(entity.getSgst());
		} else if (entity.getSubSection().equals("9D")) {
			obj.setPaidThrCash(entity.getCess());
		}
		obj.setPaidThrItcIgst(entity.getTaxPaidItcIamt());
		obj.setPaidThrItcCgst(entity.getTaxPaidItcCamt());
		obj.setPaidThrItcSgst(entity.getTaxPaidItcSamt());
		obj.setPaidThrItcCess(entity.getTaxPaidItcCSamt());
		return obj;
	}

	private void setAutoCalcValues(Gstr9TaxPaidDashboardDto gstr9gstnDto,
			Gstr9TaxPaidMapDto gstr9TaxPaidMapDto) {
		gstr9gstnDto.setAutoCalPaidThrItcCess(
				gstr9TaxPaidMapDto.getPaidThrItcCess());
		gstr9gstnDto.setAutoCalPaidThrItcCgst(
				gstr9TaxPaidMapDto.getPaidThrItcCgst());
		gstr9gstnDto.setAutoCalPaidThrItcIgst(
				gstr9TaxPaidMapDto.getPaidThrItcIgst());
		gstr9gstnDto.setAutoCalPaidThrItcSgst(
				gstr9TaxPaidMapDto.getPaidThrItcSgst());
		gstr9gstnDto.setAutoCalTaxableVal(gstr9TaxPaidMapDto.getTaxableVal());
		gstr9gstnDto.setAutoCalPaidThrCash(gstr9TaxPaidMapDto.getPaidThrCash());
	}

	private void setComputedValues(Gstr9TaxPaidDashboardDto gstr9gstnDto,
			Gstr9TaxPaidMapDto gstr9TaxPaidMapDto) {
		gstr9gstnDto.setComputedPaidThrItcCess(
				gstr9TaxPaidMapDto.getPaidThrItcCess());
		gstr9gstnDto.setComputedPaidThrItcCgst(
				gstr9TaxPaidMapDto.getPaidThrItcCgst());
		gstr9gstnDto.setComputedPaidThrItcIgst(
				gstr9TaxPaidMapDto.getPaidThrItcIgst());
		gstr9gstnDto.setComputedPaidThrItcSgst(
				gstr9TaxPaidMapDto.getPaidThrItcSgst());
		gstr9gstnDto.setComputedTaxableVal(gstr9TaxPaidMapDto.getTaxableVal());
		gstr9gstnDto
				.setComputedPaidThrCash(gstr9TaxPaidMapDto.getPaidThrCash());
	}

	private void setSummaryValues(Gstr9TaxPaidDashboardDto gstr9gstnDto,
			Gstr9TaxPaidMapDto gstr9TaxPaidMapDto) {
		gstr9gstnDto.setGstinCess(gstr9TaxPaidMapDto.getPaidThrItcCess());
		gstr9gstnDto.setGstinCgst(gstr9TaxPaidMapDto.getPaidThrItcCgst());
		gstr9gstnDto.setGstinIgst(gstr9TaxPaidMapDto.getPaidThrItcIgst());
		gstr9gstnDto.setGstinSgst(gstr9TaxPaidMapDto.getPaidThrItcSgst());
		gstr9gstnDto.setGstinTaxableVal(gstr9TaxPaidMapDto.getTaxableVal());
		gstr9gstnDto.setGstinPaidThrCash(gstr9TaxPaidMapDto.getPaidThrCash());
	}

	private void setUserInputValues(Gstr9TaxPaidDashboardDto gstr9gstnDto,
			Gstr9TaxPaidMapDto gstr9TaxPaidMapDto) {
		gstr9gstnDto.setUserInputTaxableVal(gstr9TaxPaidMapDto.getTaxableVal());
	}

	@Override
	@Transactional(value = "clientTransactionManager")
	public void saveGstr9TaxPaidUserInput(List<Gstr9TaxPaidMapDto> userInput,
			String gstin, String fy) {
		try {
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			String taxPeriod = "03" + fy.substring(fy.lastIndexOf('-') + 1);

			Gstr9UserInputEntity entity = convertToEntity(userInput, gstin, fy,
					userName, taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9TaxPaidTabServiceImpl : entity to be "
						+ "saved ");
			}
			gstr9UserInputRepository.softDeleteBasedOnSection(gstin, fy,
					userName, updatedSource, Gstr9TaxPaidConstants.Table_9);

			gstr9UserInputRepository.save(entity);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr9TaxPaidTabServiceImpl : "
						+ "successfully updated user input table");
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Gstr9 TaxPaid Data ";
			LOGGER.error(msg, ex);
			throw new AppException();
		}

	}

	private Gstr9UserInputEntity convertToEntity(
			List<Gstr9TaxPaidMapDto> userInput, String gstin, String fy,
			String userName, String taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" Inside Gstr9TaxPaidTabServiceImpl"
					+ ".convertToEntity method :");
		}
		Gstr9UserInputEntity userEntity = new Gstr9UserInputEntity();
		userInput.forEach((input) -> {
			if (input.getSubSection().equalsIgnoreCase("9A"))
				userEntity.setIgst(input.getTaxableVal());
			else if (input.getSubSection().equalsIgnoreCase("9B"))
				userEntity.setCgst(input.getTaxableVal());
			else if (input.getSubSection().equalsIgnoreCase("9C"))
				userEntity.setSgst(input.getTaxableVal());
			else if (input.getSubSection().equalsIgnoreCase("9D"))
				userEntity.setCess(input.getTaxableVal());
			else if (input.getSubSection().equalsIgnoreCase("9E"))
				userEntity.setIntr(input.getTaxableVal());
			else if (input.getSubSection().equalsIgnoreCase("9F"))
				userEntity.setFee(input.getTaxableVal());
			else if (input.getSubSection().equalsIgnoreCase("9G"))
				userEntity.setPen(input.getTaxableVal());
			else if (input.getSubSection().equalsIgnoreCase("9H"))
				userEntity.setOth(input.getTaxableVal());
		});
		String docKey = CommonUtility.generateGstr9DocKey(gstin, fy,
				Gstr9TaxPaidConstants.Table_9);
		userEntity.setDocKey(docKey);
		userEntity.setRetPeriod(taxPeriod);
		userEntity.setGstin(gstin);
		userEntity.setFy(fy);
		userEntity.setCreatedOn(LocalDateTime.now());
		userEntity.setCreatedBy(userName);
		userEntity.setSection(Gstr9TaxPaidConstants.Table_9);
		userEntity.setSubSection(Gstr9TaxPaidConstants.Table_9);
		userEntity
				.setNatureOfSupplies(Gstr9Util.getNatureOfSuppliesForSubSection(
						Gstr9TaxPaidConstants.Table_9));
		userEntity.setActive(true);
		return userEntity;
	}

}
