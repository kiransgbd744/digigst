package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.FinancialYearDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.ibm.icu.text.DecimalFormat;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Service("Gstr1OutwardFioriDashboardServiceImpl")
@Slf4j
public class Gstr1OutwardFioriDashboardServiceImpl
		implements Gstr1OutwardFioriDashboardService {

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	private Gstr1OutwardFioriDashboardDao dashboardDao;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;
	
	@Autowired
	DefaultStateCache defaultStateCache;

	private static final String B2B_OTHER_THAN_STOCK_TRANSFER = "B2B(Other than stock transfer)";
	private static final String B2B_STOCK_TRANSFER = "B2B(Stock transfer)";

	@Override
	public List<GstinDto> getAllSupplierGstins(List<Long> entityIds) {

		/*
		 * List<String> supplierPanList = entityInfoRepository
		 * .findPanByEntityId(entityId);
		 * 
		 * String supplierPan = supplierPanList.get(0);
		 */

		// List<String> list = dashboardDao.getSupGstinList(supplierPan);

		List<String> reqGstins = gSTNDetailRepository
				.findgstinByEntityIdWithRegTypeForGstr1(entityIds);

		/*
		 * if (LOGGER.isDebugEnabled()) { String msg =
		 * String.format("Fetched %s supplierGstins for pan %s",
		 * reqGstins.size(), supplierPan); LOGGER.debug(msg); }
		 */
		return reqGstins.stream().map(o -> convertToGstins(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private GstinDto convertToGstins(String gstin) {
		GstinDto obj = new GstinDto();
		obj.setGstin((String) gstin);
		String stateCode = (obj.getGstin()).substring(0, 2);
		String stateName = defaultStateCache.getStateName(stateCode);
		obj.setStateName(stateName);
		return obj;
	}

	@Override
	public List<FinancialYearDto> getAllReturnPeriods(String fy) {
		List<String> list = dashboardDao.getAllReturnPeriods(fy);
		Collections.sort(list, (a, b) -> a.compareTo(b));
		String fyYear = fy.substring(0, 4);

		String pivotDate = "04" + fyYear;
		int pivotIndex = list.indexOf(pivotDate);
		if (pivotIndex >= 0) {
			List<String> rotatedList = new ArrayList<>(
					list.subList(pivotIndex, list.size()));
			rotatedList.addAll(list.subList(0, pivotIndex));
			list = rotatedList;
		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s returnPeriods for" + " fy %s", list.size(), fy);
			LOGGER.debug(msg);
		}
		return list.stream().map(o -> convertToReturnPeriods(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private FinancialYearDto convertToReturnPeriods(String returnPeriod) {
		FinancialYearDto obj = new FinancialYearDto();
		obj.setFy((String) returnPeriod);
		return obj;
	}

	@Override
	public List<Gstr1FioriDashboardChartDto> getGrossOutwardSuppplies(String fy,
			List<String> supplierGstins, List<String> returnPeriods,
			String flag) {
		 
		
		List<Object[]> grossOutwardObjects = dashboardDao
				.getAllGrossOutwardSupp(fy, supplierGstins, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s grossOutwardObjects for fy %s"
							+ ", supplierGstins %s , returnPeriods %s ",
					grossOutwardObjects.size(), fy, supplierGstins,
					returnPeriods);
			LOGGER.debug(msg);
		}
		List<Gstr1FioriDashboardChartDto> respList = grossOutwardObjects
				.stream().map(o -> convertToChartDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		if (!respList.isEmpty())
			Collections.sort(respList,
					(a, b) -> a.getOrder().compareToIgnoreCase(b.getOrder()));

		List<Gstr1FioriDashboardChartDto> resp = new ArrayList<>();
		for (Gstr1FioriDashboardChartDto dto : respList) {
			if ("off".equalsIgnoreCase(flag)) {
				if (!B2B_STOCK_TRANSFER.equalsIgnoreCase(dto.getXAxis())
						&& !B2B_OTHER_THAN_STOCK_TRANSFER
								.equalsIgnoreCase(dto.getXAxis())) {
					resp.add(dto);
				}
			} else {
				if (!"B2B".equalsIgnoreCase(dto.getXAxis())) {
					resp.add(dto);
				}
			}
		}
		return resp;
	}

	private static List<String> convertDates(List<String> originalDates) {
	    List<String> convertedDates = new ArrayList<>();
	    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMyyyy");
	    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMM");

	    for (String date : originalDates) {
	        if (!date.equalsIgnoreCase("All")) {
	            try {
	                YearMonth yearMonth = YearMonth.parse(date, inputFormatter);
	                String formattedDate = yearMonth.format(outputFormatter);
	                convertedDates.add(formattedDate);
	            } catch (DateTimeParseException e) {
	                // Handle parsing exception if needed
	                System.err.println("Error parsing date: " + date);
	            }
	        } 
	    }

	    return convertedDates;
	}


	private Gstr1FioriDashboardChartDto convertToChartDto(Object[] obj) {
		Gstr1FioriDashboardChartDto dto = new Gstr1FioriDashboardChartDto();
		String type = (String) obj[0];
		dto.setXAxis(type);
		if ("B2B".equalsIgnoreCase(type)) {
			dto.setOrder("A");
		} else if (B2B_OTHER_THAN_STOCK_TRANSFER.equalsIgnoreCase(type)) {
			dto.setOrder("B");
		} else if (B2B_STOCK_TRANSFER.equalsIgnoreCase(type)) {
			dto.setOrder("C");
		} else if ("B2C".equalsIgnoreCase(type)) {
			dto.setOrder("D");
		} else if ("Exports".equalsIgnoreCase(type)) {
			dto.setOrder("E");
		} else if ("Advances".equalsIgnoreCase(type)) {
			dto.setOrder("F");
		} else if ("Others".equalsIgnoreCase(type)) {
			dto.setOrder("G");
			dto.setXAxis("NIL/NON/EXT");
		} else {
			dto.setOrder("H");
		}
		dto.setYAxis((BigDecimal) obj[1]);
		return dto;
	}

	private Gstr1FioriDashboardChartDto convertToChartDo(Object[] obj) {
		Gstr1FioriDashboardChartDto dto = new Gstr1FioriDashboardChartDto();
		String type = (String) obj[0];
		dto.setXAxis(type);
		if ("B2B".equalsIgnoreCase(type)) {
			dto.setOrder("A");
		} else if (B2B_OTHER_THAN_STOCK_TRANSFER.equalsIgnoreCase(type)) {
			dto.setOrder("B");
		} else if (B2B_STOCK_TRANSFER.equalsIgnoreCase(type)) {
			dto.setOrder("C");
		} else if ("B2C".equalsIgnoreCase(type)) {
			dto.setOrder("D");
		} else if ("Exports".equalsIgnoreCase(type)) {
			dto.setOrder("E");
		} else if ("Advances".equalsIgnoreCase(type)) {
			dto.setOrder("F");
		} else if ("Others".equalsIgnoreCase(type)) {
			dto.setOrder("G");
			dto.setXAxis("NIL/NON/EXT");
		} else {
			dto.setOrder("H");
		}
		dto.setYAxis((BigDecimal) obj[3]);
		return dto;
	}

	@Override
	public List<Gstr1FioriDashboardChartDto> getMonthWiseTrendAnalysisList(
			String fy, String valueFlag, List<String> supplierGstins,
			List<String> returnPeriods) {
		List<Object[]> monthWiseTrendObjects = dashboardDao
				.getMonthWiseTrendAnalysis(fy, valueFlag, supplierGstins,
						returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s monthWiseTrendObjects for fy %s"
							+ ", supplierGstins %s , returnPeriods %s ",
					monthWiseTrendObjects.size(), fy, supplierGstins,
					returnPeriods);
			LOGGER.debug(msg);
		}
		return monthWiseTrendObjects.stream().map(o -> convertToChartDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<Gstr1FioriDashboardChartDto> getTopCustomersB2BList(String fy,
			List<String> supplierGstins, List<String> returnPeriods) {
		returnPeriods = convertDates(returnPeriods);
		List<Object[]> topCustomersB2bObjects = dashboardDao
				.getTopCustomerB2BData(fy, supplierGstins, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s topCustomersB2bObjects for fy %s"
							+ ", supplierGstins %s , returnPeriods %s ",
					topCustomersB2bObjects.size(), fy, supplierGstins,
					returnPeriods);
			LOGGER.debug(msg);
		}
		return topCustomersB2bObjects.stream().map(o -> convertToChartDo(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<Gstr1FioriDashboardChartDto> getMajorTaxPayingProductsList(
			String fy, List<String> supplierGstins,
			List<String> returnPeriods) {
		returnPeriods = convertDates(returnPeriods);
		
		List<Object[]> majorTaxPayingProductObjects = dashboardDao
				.getMajorTaxPayingProducts(fy, supplierGstins, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s majorTaxPayingProductObjects for fy %s"
							+ ", supplierGstins %s , returnPeriods %s ",
					majorTaxPayingProductObjects.size(), fy, supplierGstins,
					returnPeriods);
			LOGGER.debug(msg);
		}
		return majorTaxPayingProductObjects.stream()
				.map(o -> convertToChartDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<TaxRateWiseDistributionDto> getTaxRateWiseDistributionList(
			String fy, List<String> supplierGstins,
			List<String> returnPeriods) {
		 returnPeriods = convertDates(returnPeriods);
		List<Object[]> taxRateWiseDistributionObjects = dashboardDao
				.getTaxRateWiseDistribution(fy, supplierGstins, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s taxRateWiseDistributionObjects for fy %s"
							+ ", supplierGstins %s , returnPeriods %s ",
					taxRateWiseDistributionObjects.size(), fy, supplierGstins,
					returnPeriods);
			LOGGER.debug(msg);
		}
		return taxRateWiseDistributionObjects.stream()
				.map(o -> convertToTaxRateDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private TaxRateWiseDistributionDto convertToTaxRateDto(Object[] obj) {
		TaxRateWiseDistributionDto dto = new TaxRateWiseDistributionDto();
		dto.setTaxRate((BigDecimal) obj[0]);
		dto.setInvoiceValue((BigDecimal) obj[1]);
		return dto;
	}

	@Override
	public TotalLiabilityDetailsDto getTotalLiabilityData(String fy,
			List<String> supplierGstins, List<String> returnPeriods) {
		 returnPeriods = convertDates(returnPeriods);
		
		List<Object[]> list = dashboardDao.getTotalLiabilityDetails(fy,
				supplierGstins, returnPeriods);
		Object[] obj = list.get(0);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s TotalLiabilityObject for fy %s"
							+ ", supplierGstins %s ",
					list.size(), fy, supplierGstins);
			LOGGER.debug(msg);
		}
		TotalLiabilityDetailsDto dto = new TotalLiabilityDetailsDto();
		dto.setNetLiability((BigDecimal) obj[0]);
		dto.setNetItcAvailable((BigDecimal) obj[1]);
		dto.setTotalLiability((BigDecimal) obj[2]);
		String refreshedOn = obj[3] != null ? (String) obj[3] : null;
		if (refreshedOn != null) {
			refreshedOn = refreshedOn.replace(" ", "T");
			refreshedOn = refreshedOn.substring(0, 22);
			String itc = EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.parse(refreshedOn))
					.toString();
			itc = itc.substring(0, 19);
			itc = itc.replace("T", " ");
			dto.setLastRefreshedOn(itc);
		}

		return dto;
	}

	@Override
	public List<TaxLiabilityDetailsDto> getTaxLiabilityData(String fy,
			List<String> supplierGstins, List<String> returnPeriods) {
		List<Object[]> taxLiabilityObjects = dashboardDao
				.getTaxLiabilityDetails(fy, supplierGstins, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s taxLiabilityObjects for fy %s"
							+ ", supplierGstins %s , returnPeriods %s ",
					taxLiabilityObjects.size(), fy, supplierGstins,
					returnPeriods);
			LOGGER.debug(msg);
		}
		List<String> transTypeList = new ArrayList<String>();
		transTypeList.add("B2B");
		transTypeList.add(B2B_OTHER_THAN_STOCK_TRANSFER);
		transTypeList.add(B2B_STOCK_TRANSFER);
		transTypeList.add("B2C");
		transTypeList.add("Exports");
		transTypeList.add("Advances");
		transTypeList.add("Others");
		List<TaxLiabilityDetailsDto> defaultList = transTypeList.stream()
				.map(o -> convertToTaxLiabilityDefaultDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		List<TaxLiabilityDetailsDto> respList = defaultList.stream()
				.map(o -> convertToTaxLiabilityDto(o, taxLiabilityObjects))
				.collect(Collectors.toCollection(ArrayList::new));
		Collections.sort(respList,
				(a, b) -> a.getOrder().compareToIgnoreCase(b.getOrder()));
		return respList;
	}

	@Override
	public List<TaxLiabilityDetailsFinalDto> getTaxLiabilityIoclData(String fy,
			List<String> supplierGstins, List<String> returnPeriods) {
		returnPeriods = convertDates(returnPeriods);
		List<Object[]> taxLiabilityObjects = dashboardDao
				.getTopb2bAfterToggle(fy, supplierGstins, returnPeriods);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Fetched %s taxLiabilityObjects for fy %s"
							+ ", supplierGstins %s , returnPeriods %s ",
					taxLiabilityObjects.size(), fy, supplierGstins,
					returnPeriods);
			LOGGER.debug(msg);
		}
		List<TaxLiabilityDto> taxList = taxLiabilityObjects.stream()
				.map(o -> convertTaxlLiable(o))
				.collect(Collectors.toCollection(ArrayList::new));

		Map<String, List<TaxLiabilityDto>> map = taxList.stream().collect(
				Collectors.groupingBy(TaxLiabilityDto::getCustomerGstn));

		List<TaxLiabilityDetailsFinalDto> taxLiabilityDetailsFinalDto = new ArrayList<TaxLiabilityDetailsFinalDto>();

		map.keySet().forEach(gstn -> {
			List<TaxLiabilityDto> groupByEachGstn = map.get(gstn);
			TaxLiabilityDetailsFinalDto dto = new TaxLiabilityDetailsFinalDto();
			dto.setTransactionType(gstn);
			dto.setInvoiceValue(groupByEachGstn.get(0).getGstnTotal());
			dto.setLevel("L1");
			dto.setOrder("1");
			dto.setTaxableValue(groupByEachGstn.get(0).getTotaltaxable());
			List<TaxLiabilityDetailsHsnDto> hsnDtoList = new ArrayList<>();

			for (TaxLiabilityDto newHsndto : groupByEachGstn) {
				TaxLiabilityDetailsHsnDto hsnDto = new TaxLiabilityDetailsHsnDto();
				hsnDto.setInvoiceValue(newHsndto.getTotalValue());
				hsnDto.setTaxableValue(newHsndto.getTaxableValue());
				hsnDto.setTransactionType(newHsndto.getHsnsac());
				hsnDto.setOrder(newHsndto.getRn().toString());
				hsnDto.setLevel("L2");

				hsnDtoList.add(hsnDto);
			}
			dto.setHsndto(hsnDtoList);
			taxLiabilityDetailsFinalDto.add(dto);

		});
		Collections.sort(taxLiabilityDetailsFinalDto,
				(a, b) -> b.getInvoiceValue().compareTo(a.getInvoiceValue()));

		return taxLiabilityDetailsFinalDto;

	}

	@Override
	public List<MajorTaxPayFinalDto> getMajorTaxpayDetails(String fy,
			List<String> supplierGstins, List<String> returnPeriods) {

		List<MajorTaxPayFinalDto> hsnDtoList = new ArrayList<>();
		returnPeriods = convertDates(returnPeriods);
		List<Object[]> MajorTaxPayingObject = dashboardDao
				.getMajorTaxpayData(fy, supplierGstins, returnPeriods);

		List<MajorTaxPayingDto> taxPayingList = MajorTaxPayingObject.stream()
				.map(o -> convertMajorPayingData(o))
				.collect(Collectors.toCollection(ArrayList::new));

		Map<String, List<MajorTaxPayingDto>> map = taxPayingList.stream()
				.collect(Collectors.groupingBy(MajorTaxPayingDto::getHsnsac));

		//map.keySet().forEach(hsn -> {
		for (String hsn : map.keySet()) {
			
			List<MajorTaxPayingDto> groupByEachHsn = map.get(hsn);
			//String hsnSac = hsn;
			BigDecimal per0 = BigDecimal.ZERO;
			BigDecimal per1 = BigDecimal.ZERO;
			BigDecimal per1_5 = BigDecimal.ZERO;
			BigDecimal per3 = BigDecimal.ZERO;
			BigDecimal per5 = BigDecimal.ZERO;
			BigDecimal per7 = BigDecimal.ZERO;
			BigDecimal per7_5 = BigDecimal.ZERO;
			BigDecimal per12 = BigDecimal.ZERO;
			BigDecimal per18 = BigDecimal.ZERO;
			BigDecimal per28 = BigDecimal.ZERO;
			MajorTaxPayFinalDto dto = new MajorTaxPayFinalDto();
			for (MajorTaxPayingDto newHsndto : groupByEachHsn) {
						

				String percentage1 = newHsndto.getTaxRate().toString();
				double percant = Double.valueOf(percentage1);
				DecimalFormat format = new DecimalFormat("0.#");
				String percentage = format.format(percant);
				dto.setDrn(newHsndto.getDrn());
				dto.setHsnTotalValue(
						newHsndto.getHsnTotalTaxValue().toString());
				if (percentage.equalsIgnoreCase("0")) {
					//per0.add(newHsndto.getTotalValue());
					 per0=newHsndto.getTotalValue();
				}

				else if (percentage.equalsIgnoreCase("1")) {
					//dto.setPer1(newHsndto.getTotalValue().toString());
					 per1=newHsndto.getTotalValue();
				}

				else if (percentage.equalsIgnoreCase("1.5")) {
					 per1_5=newHsndto.getTotalValue();
				}

				else if (percentage.equalsIgnoreCase("3")) {
					 per3=newHsndto.getTotalValue();
				}

				else if (percentage.equalsIgnoreCase("5")) {
					 per5=newHsndto.getTotalValue();
				}

				else if (percentage.equalsIgnoreCase("7")) {
					 per7=newHsndto.getTotalValue();
				}

				else if (percentage.equalsIgnoreCase("7.5")) {
					 per7_5=newHsndto.getTotalValue();
				}

				else if (percentage.equalsIgnoreCase("12")) {
					 per12=newHsndto.getTotalValue();
				}

				else if (percentage.equalsIgnoreCase("18")) {
					 per18=newHsndto.getTotalValue();
					
				} else if (percentage.equalsIgnoreCase("28")) {
					 per28=newHsndto.getTotalValue();
				}

				
			}
	
			
			dto.setHsnsac(hsn);
			dto.setPer0(per0.toString());
			dto.setPer1(per1.toString());
			dto.setPer1_5(per1_5.toString());
			dto.setPer3(per3.toString());
			dto.setPer5(per5.toString());
			dto.setPer7(per7.toString());
			dto.setPer7_5(per7_5.toString());
			dto.setPer12(per12.toString());
			dto.setPer18(per18.toString());
			dto.setPer28(per28.toString());
			
			hsnDtoList.add(dto);
		}
		//});
		Collections.sort(hsnDtoList,
				(a, b) -> a.getDrn().compareTo(b.getDrn()));

		// hsnDtoList.sort(Comparator.comparing(MajorTaxPayFinalDto::getDrn));
		return hsnDtoList;

	}

	private TaxLiabilityDto convertTaxlLiable(Object[] arr) {

		TaxLiabilityDto dto = new TaxLiabilityDto();
		dto.setCustomerGstn((String) arr[0]);
		dto.setTotalValue((BigDecimal) arr[1]);
		dto.setHsnsac((String) arr[2]);
		dto.setGstnTotal((BigDecimal) arr[3]);
		// dto.setDrn(GenUtil.getBigInteger(arr[4]));
		dto.setRn(GenUtil.getBigInteger(arr[4]));
		dto.setTaxableValue((BigDecimal) arr[5]);
		dto.setTotaltaxable((BigDecimal) arr[6]);

		return dto;
	}

	private MajorTaxPayingDto convertMajorPayingData(Object[] arr) {

		MajorTaxPayingDto dto = new MajorTaxPayingDto();
		dto.setHsnsac((String) arr[0]);
		dto.setTaxRate((BigDecimal) arr[1]);
		dto.setTotalValue((BigDecimal) arr[2]);
		dto.setHsnTotalTaxValue((BigDecimal) arr[3]);
		dto.setDrn(GenUtil.getBigInteger(arr[4]));
		dto.setRn(GenUtil.getBigInteger(arr[5]));
		return dto;
	}

	private TaxLiabilityDetailsDto convertToTaxLiabilityDto(
			TaxLiabilityDetailsDto defaultDto, List<Object[]> objList) {
		for (Object[] obj : objList) {
			String transType = (String) obj[0];
			if (transType.equalsIgnoreCase(defaultDto.getTransactionType())) {
				defaultDto.setInvoiceValue(
						GenUtil.defaultToZeroIfNull((BigDecimal) obj[1]));
				defaultDto.setTaxableValue(
						GenUtil.defaultToZeroIfNull((BigDecimal) obj[2]));
				defaultDto.setIgstAmt(
						GenUtil.defaultToZeroIfNull((BigDecimal) obj[3]));
				defaultDto.setCgstAmt(
						GenUtil.defaultToZeroIfNull((BigDecimal) obj[4]));
				defaultDto.setSgstAmt(
						GenUtil.defaultToZeroIfNull((BigDecimal) obj[5]));
				defaultDto.setCessAmt(
						GenUtil.defaultToZeroIfNull((BigDecimal) obj[6]));
			}
		}
		return defaultDto;
	}

	private TaxLiabilityDetailsDto convertToTaxLiabilityDefaultDto(
			String transType) {
		TaxLiabilityDetailsDto dto = new TaxLiabilityDetailsDto();
		if (transType.equalsIgnoreCase("B2B")) {
			dto.setTransactionType(transType);
			dto.setOrder("A");
			dto.setLevel("L1");
		} else if (transType.equalsIgnoreCase(B2B_OTHER_THAN_STOCK_TRANSFER)) {
			dto.setTransactionType(transType);
			dto.setOrder("B");
			dto.setLevel("L2");
		} else if (transType.equalsIgnoreCase(B2B_STOCK_TRANSFER)) {
			dto.setTransactionType(transType);
			dto.setOrder("C");
			dto.setLevel("L2");
		} else if (transType.equalsIgnoreCase("B2C")) {
			dto.setTransactionType(transType);
			dto.setOrder("D");
			dto.setLevel("L1");
		} else if (transType.equalsIgnoreCase("Exports")) {
			dto.setTransactionType(transType);
			dto.setOrder("E");
			dto.setLevel("L1");
		} else if (transType.equalsIgnoreCase("Advances")) {
			dto.setTransactionType(transType);
			dto.setOrder("F");
			dto.setLevel("L1");
		} else if (transType.equalsIgnoreCase("Others")) {
			dto.setTransactionType(transType);
			dto.setOrder("G");
			dto.setLevel("L1");
		}

		dto.setInvoiceValue(BigDecimal.ZERO);
		dto.setTaxableValue(BigDecimal.ZERO);
		dto.setIgstAmt(BigDecimal.ZERO);
		dto.setCgstAmt(BigDecimal.ZERO);
		dto.setSgstAmt(BigDecimal.ZERO);
		dto.setCessAmt(BigDecimal.ZERO);
		return dto;
	}
}
