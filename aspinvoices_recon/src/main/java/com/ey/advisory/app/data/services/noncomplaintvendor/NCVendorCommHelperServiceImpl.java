package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.VendorGstinFilingTypeEntity;
import com.ey.advisory.app.data.entities.client.VendorReturnTypeEntity;
import com.ey.advisory.app.data.entities.client.asprecon.FrequencyDataStorageStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReturnDataStorageStatusEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.VendorGstinFilingTypeRepository;
import com.ey.advisory.app.data.repositories.client.VendorReturnTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.FrequencyDataStorageStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReturnDataStorageStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.TypeOfGstFiling;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("NCVendorCommHelperServiceImpl")
public class NCVendorCommHelperServiceImpl
		implements NCVendorCommHelperService {

	@Autowired
	@Qualifier("DefaultRangeCalculatorFactoryImpl")
	private ConfirmantRangeCalculatorFactory defaultCalcFactory;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	@Qualifier("ReturnDataStorageStatusRepository")
	private ReturnDataStorageStatusRepository returnDataStorageStatusRepo;

	@Autowired
	@Qualifier("FrequencyDataStorageStatusRepository")
	private FrequencyDataStorageStatusRepository freqDataStorageStatusRepo;
	
	@Autowired
	@Qualifier("VendorReturnTypeRepository")
	private VendorReturnTypeRepository vendorReturnTypeRepo;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnStatusRepo;

	@Autowired
	@Qualifier("VendorGstinFilingTypeRepository")
	private VendorGstinFilingTypeRepository vendorGstinFilingTypeRepo;

	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	private static final List<String> QUARTERLY_MONTHS = ImmutableList.of("06",
			"09", "12", "03");

	private static final List<String> NON_QUARTERLY_MONTHS = ImmutableList
			.of("04", "05", "07", "08", "10", "11", "01", "02");

	@Override
	public List<VendorMasterUploadEntity> extractAndPopulateVendorProcessedRecords(
			Long entityId, List<String> vendorGstinsList,
			List<String> vendorPans) {
		List<VendorMasterUploadEntity> uploadEntities = new ArrayList<>();
		List<String> recipientPanList = entityInfoRepository
				.findPanByEntityId(entityId);

		if (vendorGstinsList.isEmpty() && vendorPans.isEmpty()) {
			uploadEntities = vendorMasterUploadEntityRepository
					.findByIsDeleteFalseAndRecipientPANIn(recipientPanList);
		}

		else if (vendorGstinsList.isEmpty() && !vendorPans.isEmpty()) {
			List<List<String>> chunks = Lists.partition(vendorPans, 2000);
			for (List<String> chunk : chunks) {
				uploadEntities.addAll(vendorMasterUploadEntityRepository
						.findByIsDeleteFalseAndRecipientPANInAndVendorPANIn(
								recipientPanList, chunk));
			}
		} else {
			List<List<String>> chunks = Lists.partition(vendorGstinsList, 2000);
			for (List<String> chunk : chunks) {
				uploadEntities.addAll(vendorMasterUploadEntityRepository
						.findByIsDeleteFalseAndRecipientPANInAndVendorGstinIn(
								recipientPanList, chunk));
			}
		}
		return uploadEntities;
	}

	@Override
	public Map<String, Boolean> getValidConfirmantRange(
			List<String> stampedVendors, String returnType, String fy,
			Map<String, String> filingTypeMap, boolean isCurrentFy) {

		Map<String, Boolean> map = new HashMap<>();
		if (!isCurrentFy)
			return map;
		ConfirmantRangeCalculator calculator = defaultCalcFactory
				.getCalculator(returnType);
		stampedVendors.forEach(gstin -> {
			String key = gstin + "-" + returnType;
			String filingType = filingTypeMap.get(key) == null
					? TypeOfGstFiling.MONTHLY.toString()
					: filingTypeMap.get(key).toString();

			ConfirmantRangeDto dto = calculator.calculate(returnType,
					filingType, fy);
			if (dto == null)
				return;
			List<String> manList = dto.getMandatoryTaxPeriods();
			List<String> optList = dto.getOptionalTaxPeriods();
			manList.forEach(taxPeriod -> {
				map.put(gstin + taxPeriod + returnType, true);
			});
			optList.forEach(taxPeriod -> {
				map.put(gstin + taxPeriod + returnType, false);
			});
		});

		return map;
	}

	@Override
	public void populateMonthWiseStatusForGstins(
			List<GstinWiseFilingStatus> gstinWiseFilingStatus,
			List<GstrReturnStatusEntity> returnStatusEntities,
			Map<String, Boolean> map, String returnType) {
		gstinWiseFilingStatus.forEach(gstinWise -> {
			String curGstin = gstinWise.getGstin();
			List<String> filedRecs = returnStatusEntities.stream()
					.filter(record -> record.getGstin().equals(curGstin))
					.map(GstrReturnStatusEntity::getTaxPeriod)
					.collect(Collectors.toList());

			if (!CollectionUtils.isEmpty(filedRecs)) {
				gstinWise.getEachGstinwiseStatusCombination()
						.forEach(monthWise -> {
							String curTaxprd = monthWise.getMonth();

							if (filedRecs.contains(curTaxprd)) {
								monthWise.setStatus(
										MonthWiseGstinStatus.FILED.getStatus());
							} else {
								String key = curGstin
										.concat(monthWise.getMonth())
										.concat(returnType);
								if (map.containsKey(key)
										&& Boolean.TRUE.equals(map.get(key))) {
									monthWise.setStatus(
											MonthWiseGstinStatus.NOT_FILED
													.getStatus());
								}
							}
						});
			} else {
				gstinWise.getEachGstinwiseStatusCombination()
						.forEach(monthWise -> {
							String key = curGstin.concat(monthWise.getMonth())
									.concat(returnType);
							if (map.containsKey(key)
									&& Boolean.TRUE.equals(map.get(key))) {
								monthWise.setStatus(
										MonthWiseGstinStatus.NOT_FILED
												.getStatus());
							}
						});
			}
		});
	}

	@Override
	public void populateMonthWiseStatusForPans(
			List<GstinWiseFilingStatus> gstinWiseFilingStatus,
			List<OverallFilingStatusDto> listOfPans, List<String> taxPeriods) {
		Map<String, List<GstinWiseFilingStatus>> panMap = gstinWiseFilingStatus
				.stream().collect(Collectors
						.groupingBy(obj -> obj.getGstin().substring(2, 12)));

		// logic for PAN wise filling status
		listOfPans.forEach(eachPan -> {
			List<GstinWiseFilingStatus> gstinWiseList = panMap
					.get(eachPan.getVendorPan());

			eachPan.setGstinWiseFilingStatusMonthwise(gstinWiseList);
			List<OverAllPanStatusDto> overAllPanList = new ArrayList<>();
			taxPeriods.forEach(eachTaxperiod -> {
				OverAllPanStatusDto panDto = new OverAllPanStatusDto();
				panDto.setMonth(eachTaxperiod);
				panDto.setStaus(OverAllPanStatus.UNKNOWN.getStatus());
				overAllPanList.add(panDto);
			});
			eachPan.setOverAllPanStatus(overAllPanList);

			for (OverAllPanStatusDto panObj : eachPan.getOverAllPanStatus()) {
				Map<String, List<String>> panLvlStatusMap = new HashMap<>();
				for (GstinWiseFilingStatus eachGstin : gstinWiseList) {
					boolean isQuaterly = eachGstin.getFilingType()
							.equals(TypeOfGstFiling.QUARTERLY.toString());
					eachGstin.getEachGstinwiseStatusCombination().stream()
							.filter(obj -> (!isQuaterly) || (isQuaterly
									&& !NON_QUARTERLY_MONTHS.contains(
											obj.getMonth().substring(0, 2))))
							.filter(obj -> obj.getMonth()
									.equals(panObj.getMonth()))
							.forEach(obj -> panLvlStatusMap
									.computeIfAbsent(obj.getStatus(),
											v -> new ArrayList<>())
									.add(obj.getMonth()));
				}

				if (panLvlStatusMap.keySet().size() == 1) {
					if (panLvlStatusMap.keySet().contains(
							MonthWiseGstinStatus.NOT_FILED.getStatus()))
						panObj.setStaus(
								OverAllPanStatus.NOT_COMPLIANT.getStatus());
					else if (panLvlStatusMap.keySet()
							.contains(MonthWiseGstinStatus.FILED.getStatus()))
						panObj.setStaus(OverAllPanStatus.COMPLIANT.getStatus());
					else
						panObj.setStaus(OverAllPanStatus.UNKNOWN.getStatus());
				} else if (panLvlStatusMap.keySet()
						.contains(MonthWiseGstinStatus.FILED.getStatus())) {
					panObj.setStaus(
							OverAllPanStatus.PARTIALLY_COMPLIANT.getStatus());
				} else {
					panObj.setStaus(OverAllPanStatus.NOT_COMPLIANT.getStatus());
				}
			}
		});
	}

	@Override
	public OverAllFilingStatusWithFinancialYearInfo getOnlyCompliantOrNonCompliantData(
			String viewType, List<OverallFilingStatusDto> listOfPans,
			String financialYear) {
		if (viewType.equalsIgnoreCase(OverAllPanStatus.COMPLIANT.getStatus())) {
			List<String> compPanList = new ArrayList<>();

			listOfPans.stream().forEach(eachPan -> {
				boolean isCompliant = true;
				for (OverAllPanStatusDto panStatus : eachPan
						.getOverAllPanStatus()) {
					if (!panStatus.getStaus()
							.equals(OverAllPanStatus.COMPLIANT.toString())) {
						isCompliant = false;
						break;
					}
				}
				if (isCompliant)
					compPanList.add(eachPan.getVendorPan());

			});
			if (!compPanList.isEmpty()) {
				listOfPans.removeIf(eachPan -> !compPanList
						.contains(eachPan.getVendorPan()));
			} else {
				String errMsg = "There are no compliant records for selected FY";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			return convertToDto(listOfPans, financialYear);
		} else if (viewType.equalsIgnoreCase("NonCompliant")) {
			List<String> pansList = new ArrayList<>();
			listOfPans.stream().forEach(eachPan -> {
				boolean isCompliant = true;
				for (OverAllPanStatusDto panStatus : eachPan
						.getOverAllPanStatus()) {
					if (!panStatus.getStaus()
							.equals(OverAllPanStatus.COMPLIANT.toString())) {
						isCompliant = false;
						break;
					}
				}
				if (!isCompliant)
					pansList.add(eachPan.getVendorPan());
			});
			if (pansList.isEmpty()) {
				String errMsg = "There are no non-compliant records for selected FY";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			} else {
				listOfPans.removeIf(
						eachPan -> !pansList.contains(eachPan.getVendorPan()));
			}
			return convertToDto(listOfPans, financialYear);
		} else
			return convertToDto(listOfPans, financialYear);
	}

	private OverAllFilingStatusWithFinancialYearInfo convertToDto(
			List<OverallFilingStatusDto> listOfPans, String financialYear) {
		OverAllFilingStatusWithFinancialYearInfo allFilingStatusWithFinancialYearInfo = new OverAllFilingStatusWithFinancialYearInfo();
		allFilingStatusWithFinancialYearInfo.setOverallFilingStatusDtos(
				listOfPans != null ? listOfPans : new ArrayList<>());
		Pageable pageReq = PageRequest.of(0, 1, Direction.DESC, "modifiedOn");
		List<ReturnDataStorageStatusEntity> returnDataStorageStatusEntity = returnDataStorageStatusRepo
				.findByFinancialYear(financialYear, pageReq);
		if (returnDataStorageStatusEntity != null
				&& !returnDataStorageStatusEntity.isEmpty()) {
			allFilingStatusWithFinancialYearInfo.setFinanicalYear(
					returnDataStorageStatusEntity.get(0).getFinancialYear());
			allFilingStatusWithFinancialYearInfo.setStatus(
					returnDataStorageStatusEntity.get(0).getStatus());
			LocalDateTime istCreatedDate = EYDateUtil.toISTDateTimeFromUTC(
					returnDataStorageStatusEntity.get(0).getModifiedOn());

			String formatDateTime = istCreatedDate.format(format);
			allFilingStatusWithFinancialYearInfo.setModifedOn(formatDateTime);
		} 
		List<FrequencyDataStorageStatusEntity> freqStorageStatusRepo = freqDataStorageStatusRepo
				.findByFinancialYear(financialYear, pageReq);
		if (freqStorageStatusRepo != null
				&& !freqStorageStatusRepo.isEmpty()) {
			allFilingStatusWithFinancialYearInfo
					.setFinanicalYear(freqStorageStatusRepo.get(0)
							.getFinancialYear());
			allFilingStatusWithFinancialYearInfo.setRetFrequencyStatus(
					freqStorageStatusRepo.get(0).getStatus());
			LocalDateTime istCreatedDate = EYDateUtil.toISTDateTimeFromUTC(
					freqStorageStatusRepo.get(0).getModifiedOn());

			String formatDateTime = istCreatedDate.format(format);
			allFilingStatusWithFinancialYearInfo
					.setRetFrequencyTime(formatDateTime);
		}
		return allFilingStatusWithFinancialYearInfo;
	}

	@Override
	public List<NonComplaintVendorReportDto> getOverallNonComplaintsDataBasedOnGstin(
			List<VendorMasterUploadEntity> uploadEntities, String financialYear,
			String reportType) {

		List<NonComplaintVendorReportDto> nonComplaintVendorsReport = new ArrayList<>();

		List<String> vendorGstins = uploadEntities.stream()
				.map(VendorMasterUploadEntity::getVendorGstin).distinct()
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(vendorGstins))
			throw new AppException("Gstins not found for vendor master data");

		List<VendorReturnTypeEntity> stampedVendors = new ArrayList<>();
		List<List<String>> chunks = Lists.partition(vendorGstins, 2000);
		for (List<String> chunk : chunks) {
			stampedVendors.addAll(
					vendorReturnTypeRepo.findByGstinInAndResponseIsNull(chunk));
		}
		if (CollectionUtils.isEmpty(stampedVendors)) {
			String errMsg = String
					.format("There are no eligible Vendor Gstins");
			LOGGER.error(errMsg);
			return nonComplaintVendorsReport;
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Total Vendors found in stamped Vendor"
							+ " Gstin for all returnTypes are %s",
					stampedVendors.size());
			LOGGER.debug(msg);
		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Total Vendors found in stamped Vendor"
							+ " Gstin for all returnTypes are %s",
					stampedVendors);
			LOGGER.debug(msg);
		}
		List<String> stampedvendorGstins = stampedVendors.stream()
				.map(VendorReturnTypeEntity::getGstin)
				.collect(Collectors.toList());

		Map<String, String> gstinwithReturnType = stampedVendors.stream()
				.collect(Collectors.toMap(VendorReturnTypeEntity::getGstin,
						VendorReturnTypeEntity::getReturnTypes));

		List<String> allTaxPeriods = GenUtil
				.extractTaxPeriodsFromFY(financialYear, "ALL");

		List<GstrReturnStatusEntity> returnStatusEntities = new ArrayList<>();
		List<List<String>> retChunks = Lists.partition(stampedvendorGstins,
				2000);
		for (List<String> chunk : retChunks) {
			returnStatusEntities.addAll(returnStatusRepo
					.findByGstinInAndTaxPeriodInAndIsCounterPartyGstinTrueAndArnNoIsNotNull(
							chunk, allTaxPeriods));
		}
		Map<String, String> filingTypeMap = new HashMap<>();

		filingTypeMap = getfilingTypeMapFromStampedTable(stampedvendorGstins,
				financialYear);

		if (reportType.equalsIgnoreCase("Compliant")) {
			nonComplaintVendorsReport = methodToConvertNonVendorDto(
					uploadEntities, returnStatusEntities, filingTypeMap);
			return nonComplaintVendorsReport;
		}

		String currentFy = GenUtil.getCurrentFinancialYear();
		boolean isCurrentFy = currentFy.equals(financialYear);

		String defaultStatus = isCurrentFy
				? MonthWiseGstinStatus.UNKNOWN.getStatus()
				: MonthWiseGstinStatus.NOT_FILED.getStatus();

		List<GstrReturnStatusEntity> defaultFilingList = new ArrayList<>();

		// set default filing status for all monthly returnTypes
		setDefaultFilingStatusForMonthlyReturnTypes(defaultStatus,
				gstinwithReturnType, defaultFilingList, financialYear);

		// set default filing status for all Quaterly returnType = ITC04
		setDefaultFilingStatusForQuaterlyReturnTypes(defaultStatus,
				gstinwithReturnType, defaultFilingList, financialYear, "ITC04");

		// set default filing status for all Quaterly returnType = CMP08
		setDefaultFilingStatusForQuaterlyReturnTypes(defaultStatus,
				gstinwithReturnType, defaultFilingList, financialYear, "CMP08");

		// set default filing status for all Annual returnTypes
		setDefaultFilingStatusForAnnualReturnTypes(defaultStatus,
				gstinwithReturnType, defaultFilingList, financialYear);

		Map<String, Boolean> ConfirmedTpMap = new HashMap<>();
		// get valid confirmed taxPeriods for each ReturnType
		for (Map.Entry<String, String> entry : gstinwithReturnType.entrySet()) {
			String returnTypes = entry.getValue();
			List<String> returnTypesList = Arrays
					.asList(returnTypes.split(","));
			Map<String, Boolean> map = null;
			if (!CollectionUtils.isEmpty(returnTypesList)) {
				for (String returnType : returnTypesList) {
					map = getValidConfirmantRange(stampedvendorGstins,
							returnType, financialYear, filingTypeMap,
							isCurrentFy);
					ConfirmedTpMap.putAll(map);
				}
			}
		}

		if (reportType.equalsIgnoreCase("All")) {
			defaultFilingList.forEach(eachObj -> {
				Optional<GstrReturnStatusEntity> eachEntry = returnStatusEntities
						.stream().filter(filterCondition(eachObj)).findFirst();
				if (eachEntry.isPresent()) {
					BeanUtils.copyProperties(eachEntry.get(), eachObj);
				}
			});
			getNonComplaintReportBasedOnConfirmedDueDates(defaultFilingList,
					ConfirmedTpMap);
			List<NonComplaintVendorReportDto> nonVendorReportDtos = methodToConvertNonVendorDto(
					uploadEntities, defaultFilingList, filingTypeMap);
			nonComplaintVendorsReport = removeNonComplaintExtraMonthsForQuarterly(
					nonVendorReportDtos);
			// List<NonComplaintVendorReportDto> overallNonVendorReports =
			// addingInvalidGstins(
			// invalidgstins, nonVendorReports);
			return nonComplaintVendorsReport;
			// writeToExcel(overallNonVendorReports, NON_COMPLAINT, "All");
		} else {
			defaultFilingList.forEach(eachObj -> {
				Optional<GstrReturnStatusEntity> eachEntry = returnStatusEntities
						.stream().filter(filterCondition(eachObj)).findFirst();
				if (eachEntry.isPresent()) {
					BeanUtils.copyProperties(eachEntry.get(), eachObj);
				}
			});

			getNonComplaintReportBasedOnConfirmedDueDates(defaultFilingList,
					ConfirmedTpMap);
			List<NonComplaintVendorReportDto> nonVendorReportDtos = methodToConvertNonVendorDto(
					uploadEntities, defaultFilingList, filingTypeMap);

			returnStatusEntities.forEach(eachObj -> {
				Optional<NonComplaintVendorReportDto> eachEntry = nonVendorReportDtos
						.stream().filter(filterConditionForNonomplaint(eachObj))
						.findFirst();
				if (eachEntry.isPresent()) {
					nonVendorReportDtos.remove(eachEntry.get());
				}
			});
			nonComplaintVendorsReport = removeNonComplaintExtraMonthsForQuarterly(
					nonVendorReportDtos);
			return nonComplaintVendorsReport;
		}
	}

	private void getNonComplaintReportBasedOnConfirmedDueDates(
			List<GstrReturnStatusEntity> preparedGstIn,
			Map<String, Boolean> map) {

		preparedGstIn.stream().forEach(prepareddata -> {
			String key = prepareddata.getGstin().concat(prepareddata
					.getTaxPeriod().concat(prepareddata.getReturnType()));
			String status = prepareddata.getStatus()
					.equalsIgnoreCase(MonthWiseGstinStatus.FILED.getStatus())
							? prepareddata.getStatus()
							: MonthWiseGstinStatus.NOT_FILED.getStatus();
			if (map.containsKey(key) && Boolean.TRUE.equals(map.get(key))) {
				prepareddata.setStatus(status);
			}
		});
	}

	private void setDefaultFilingStatusForMonthlyReturnTypes(
			String defaultStatus, Map<String, String> gstinwithReturnType,
			List<GstrReturnStatusEntity> defaultFilingList,
			String financialYear) {

		List<String> monthlyTaxPeriods = GenUtil
				.extractTaxPeriodsFromFY(financialYear, "");

		for (Map.Entry<String, String> entry : gstinwithReturnType.entrySet()) {
			String returnTypes = entry.getValue();
			List<String> returnTypesList = Arrays
					.asList(returnTypes.split(","));

			if (!CollectionUtils.isEmpty(returnTypesList)) {
				monthlyTaxPeriods.forEach(
						eachTax -> returnTypesList.forEach(eachReturnType -> {
							if (!eachReturnType.equals("ITC04")
									&& !eachReturnType.equals("GSTR4")
									&& !eachReturnType.equals("GSTR9A")
									&& !eachReturnType.equals("GSTR9")
									&& !eachReturnType.equals("CMP08")) {
								GstrReturnStatusEntity returnFilingGstnResponseDto = new GstrReturnStatusEntity();
								returnFilingGstnResponseDto
										.setReturnType(eachReturnType);
								returnFilingGstnResponseDto
										.setTaxPeriod(eachTax);
								returnFilingGstnResponseDto
										.setGstin(entry.getKey());
								returnFilingGstnResponseDto
										.setStatus(defaultStatus);
								defaultFilingList
										.add(returnFilingGstnResponseDto);
							}
						}));
			}
		}
	}

	private void setDefaultFilingStatusForQuaterlyReturnTypes(
			String defaultStatus, Map<String, String> gstinwithReturnType,
			List<GstrReturnStatusEntity> defaultFilingList,
			String financialYear, String returnType) {

		List<String> quaterlyTaxPeriods = GenUtil
				.extractTaxPeriodsFromFY(financialYear, returnType);
		for (Map.Entry<String, String> entry : gstinwithReturnType.entrySet()) {
			String returnTypes = entry.getValue();
			List<String> returnTypesList = Arrays
					.asList(returnTypes.split(","));

			if (!CollectionUtils.isEmpty(returnTypesList)) {
				quaterlyTaxPeriods.forEach(
						eachTax -> returnTypesList.forEach(eachReturnType -> {
							if (eachReturnType.equals(returnType)) {
								GstrReturnStatusEntity returnFilingGstnResponseDto = new GstrReturnStatusEntity();
								returnFilingGstnResponseDto
										.setReturnType(eachReturnType);
								returnFilingGstnResponseDto
										.setTaxPeriod(eachTax);
								returnFilingGstnResponseDto
										.setGstin(entry.getKey());
								returnFilingGstnResponseDto
										.setStatus(defaultStatus);
								defaultFilingList
										.add(returnFilingGstnResponseDto);
							}
						}));
			}

		}
	}

	private void setDefaultFilingStatusForAnnualReturnTypes(
			String defaultStatus, Map<String, String> gstinwithReturnType,
			List<GstrReturnStatusEntity> defaultFilingList,
			String financialYear) {

		List<String> annualyTaxPeriods = GenUtil
				.extractTaxPeriodsFromFY(financialYear, "GSTR4");
		for (Map.Entry<String, String> entry : gstinwithReturnType.entrySet()) {
			String returnTypes = entry.getValue();
			List<String> returnTypesList = Arrays
					.asList(returnTypes.split(","));

			if (!CollectionUtils.isEmpty(returnTypesList)) {
				returnTypesList.forEach(eachReturnType -> {
					if (eachReturnType.equals("GSTR4")
							|| eachReturnType.equals("GSTR9A")
							|| eachReturnType.equals("GSTR9")) {
						GstrReturnStatusEntity returnFilingGstnResponseDto = new GstrReturnStatusEntity();
						returnFilingGstnResponseDto
								.setReturnType(eachReturnType);
						returnFilingGstnResponseDto
								.setTaxPeriod(annualyTaxPeriods.get(0));
						returnFilingGstnResponseDto.setGstin(entry.getKey());
						returnFilingGstnResponseDto.setStatus(defaultStatus);
						defaultFilingList.add(returnFilingGstnResponseDto);
					}
				});
			}

		}
	}

	@Override
	public Predicate<NonComplaintVendorReportDto> filterConditionForNonomplaint(
			GstrReturnStatusEntity obj1) {
		return n -> n.getVendorGSTIN().equals(obj1.getGstin())
				&& n.getTaxPeriod().equals(obj1.getTaxPeriod())
				&& n.getReturnType().equals(obj1.getReturnType());
	}

	private Predicate<GstrReturnStatusEntity> filterCondition(
			GstrReturnStatusEntity obj1) {
		return n -> n.getGstin().equals(obj1.getGstin())
				&& n.getTaxPeriod().equals(obj1.getTaxPeriod())
				&& n.getReturnType().equals(obj1.getReturnType());
	}

	@Override
	public List<NonComplaintVendorReportDto> methodToConvertNonVendorDto(
			List<VendorMasterUploadEntity> uploadEntities,
			List<GstrReturnStatusEntity> retFilingList,
			Map<String, String> filingTypeMap) {
		List<NonComplaintVendorReportDto> nonVendorReportDtos = new ArrayList<>();

		retFilingList.forEach(eachObj -> {
			NonComplaintVendorReportDto nonComplaintVendorReportDto = new NonComplaintVendorReportDto();
			nonComplaintVendorReportDto.setArnNo(eachObj.getArnNo());
			nonComplaintVendorReportDto.setReturnType(eachObj.getReturnType());
			nonComplaintVendorReportDto.setTaxPeriod(eachObj.getTaxPeriod());
			nonComplaintVendorReportDto.setFilingDate(eachObj.getFilingDate());
			nonComplaintVendorReportDto.setVendorGSTIN(eachObj.getGstin());
			nonComplaintVendorReportDto
					.setStatusofReturnFiling(eachObj.getStatus());
			nonComplaintVendorReportDto.setSourceofGSTIN("Vendor Master");
			if (eachObj.getReturnType().equals("GSTR4")
					|| eachObj.getReturnType().equals("GSTR9")
					|| eachObj.getReturnType().equals("GSTR9A")) {
				nonComplaintVendorReportDto
						.setQuarterlyorMonthlyfiler(TypeOfGstFiling.ANNUALLY.toString());
			} else if (eachObj.getReturnType().equals("ITC04")) {
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("TAXPERIOD "+eachObj.getTaxPeriod());
				}
				Integer taxPeriodEnd = Integer.parseInt(eachObj.getTaxPeriod().substring(2));
				if (taxPeriodEnd < 2021) {
					if(LOGGER.isDebugEnabled()){
						LOGGER.debug("taxPeriodEnd<2021 "+taxPeriodEnd);
					}
					nonComplaintVendorReportDto
					.setQuarterlyorMonthlyfiler(TypeOfGstFiling.QUARTERLY.toString());
				} else if (taxPeriodEnd == 2021) {
					String taxPeriodStart = eachObj.getTaxPeriod().substring(0,2);
					if(LOGGER.isDebugEnabled()){
						LOGGER.debug("taxPeriodStart with 2021 "+taxPeriodStart);
					}
					if(taxPeriodStart.equalsIgnoreCase("18") || 
							taxPeriodStart.equalsIgnoreCase("H2")){
						if(LOGGER.isDebugEnabled()){
							LOGGER.debug("HALF_YEARLY ");
						}
						nonComplaintVendorReportDto
						.setQuarterlyorMonthlyfiler(TypeOfGstFiling.HALF_YEARLY.toString());
					} else {
						if(LOGGER.isDebugEnabled()){
							LOGGER.debug("QUARTERLY ");
						}
						nonComplaintVendorReportDto
						.setQuarterlyorMonthlyfiler(TypeOfGstFiling.QUARTERLY.toString());
					}
				} else {
					if(LOGGER.isDebugEnabled()){
						LOGGER.debug("taxPeriodEnd>2021 "+taxPeriodEnd);
					}
					nonComplaintVendorReportDto
					.setQuarterlyorMonthlyfiler(TypeOfGstFiling.HALF_YEARLY.toString());
				}
			} else if (eachObj.getReturnType().equals("CMP08")) {
				nonComplaintVendorReportDto
						.setQuarterlyorMonthlyfiler(TypeOfGstFiling.QUARTERLY.toString());
			} else {
				String filingType = getTypeGstFiling(retFilingList, eachObj,
						filingTypeMap);
				String updatedFilingtype = getFilingFrequency(
						eachObj.getTaxPeriod(), filingType);
				if (updatedFilingtype != null) {
					nonComplaintVendorReportDto
							.setQuarterlyorMonthlyfiler(updatedFilingtype);
				} else {
//					nonComplaintVendorReportDto
//							.setQuarterlyorMonthlyfiler(filingType);
				}
			}
			nonComplaintVendorReportDto.setErrorfromGSTN("");
			nonVendorReportDtos.add(nonComplaintVendorReportDto);
		});
		nonVendorReportDtos.forEach(eachObj -> {
			Optional<VendorMasterUploadEntity> masterUploadEntity = uploadEntities
					.stream().filter(eachEntity -> eachEntity.getVendorGstin()
							.equals(eachObj.getVendorGSTIN()))
					.findFirst();
			if (masterUploadEntity.isPresent()) {
				eachObj.setVendorName(masterUploadEntity.get().getVendorName());
			}
		});
		return nonVendorReportDtos;
	}

	@Override
	public String getTypeGstFiling(
			List<GstrReturnStatusEntity> persistedGstrReturnEntityList,
			GstrReturnStatusEntity singlePersistedData,
			Map<String, String> filingTypeMap) {
		String gstin = singlePersistedData.getGstin();
		String returnType = singlePersistedData.getReturnType();
		String key = String.format("%s-%s", gstin, returnType);
		if (filingTypeMap.containsKey(key)) {
			return filingTypeMap.get(key);
		}
		List<GstrReturnStatusEntity> totalEntities = persistedGstrReturnEntityList
				.stream()
				.filter(eachEntity -> eachEntity.getGstin().equals(gstin)
						&& eachEntity.getReturnType().equals(returnType))
				.collect(Collectors.toList());
		List<GstrReturnStatusEntity> filedEntities = totalEntities.stream()
				.filter(eachEntity -> eachEntity.getStatus()
						.equalsIgnoreCase("filed"))
				.collect(Collectors.toList());

		if (filedEntities.isEmpty() || filedEntities.size() > 4) {
			filingTypeMap.put(key, TypeOfGstFiling.MONTHLY.toString());
			return TypeOfGstFiling.MONTHLY.toString();
		}
		List<String> monthList = Arrays.asList("06", "09", "12", "03");
		for (GstrReturnStatusEntity peristedEntity : filedEntities) {
			if (!monthList
					.contains(peristedEntity.getTaxPeriod().substring(0, 2))) {
				filingTypeMap.put(key, TypeOfGstFiling.MONTHLY.toString());
				return TypeOfGstFiling.MONTHLY.toString();
			}
		}
		filingTypeMap.put(key, TypeOfGstFiling.QUARTERLY.toString());
		return TypeOfGstFiling.QUARTERLY.toString();
	}

	@Override
	public List<NonComplaintVendorReportDto> removeNonComplaintExtraMonthsForQuarterly(
			List<NonComplaintVendorReportDto> nonVendorReportDtos) {
		Iterator<NonComplaintVendorReportDto> it = nonVendorReportDtos
				.iterator();
		while (it.hasNext()) {
			NonComplaintVendorReportDto eachEntry = it.next();
			if (eachEntry.getQuarterlyorMonthlyfiler() != null) {
				if (!eachEntry.getReturnType().equals("ITC04")
						&& (eachEntry.getQuarterlyorMonthlyfiler()
								.equals(TypeOfGstFiling.QUARTERLY)
								|| eachEntry.getQuarterlyorMonthlyfiler()
										.equalsIgnoreCase(
												(TypeOfGstFiling.QUARTERLY)
														.toString()))
						&& !QUARTERLY_MONTHS.contains(
								eachEntry.getTaxPeriod().substring(0, 2))) {
					it.remove();
				}
			}
		}
		return nonVendorReportDtos;
	}

	@Override
	public Map<String, String> getfilingTypeMapFromStampedTable(
			List<String> vendorGstins, String fy) {
		try {
			Map<String, String> filingTypeMap = new HashMap<>();
			List<VendorGstinFilingTypeEntity> filingTypeList = new ArrayList<>();
			List<List<String>> chunks = Lists.partition(vendorGstins, 2000);
			for (List<String> chunk : chunks) {
				filingTypeList.addAll(vendorGstinFilingTypeRepo
						.findByGstinInAndFyOrderByQuarterAsc(chunk, fy));
			}
			if (filingTypeList.isEmpty()) {
				LOGGER.error("No selected Gstins were found stamped in vendor "
						+ "filing type table");

			} else {
				Map<String, List<VendorGstinFilingTypeEntity>> map = new HashMap<>();
//				filingTypeMap = filingTypeList.stream()
//						.collect(Collectors.toMap(
//								s -> s.getGstin().concat("-")
//										.concat(s.getReturnType()),
//								s -> TypeOfGstFiling
//										.valueOf(s.getFilingType())));//A-Gstr1 -> 4 row
				for (VendorGstinFilingTypeEntity filingEntity : filingTypeList) {
					String key = filingEntity.getGstin().concat("-")
							.concat(filingEntity.getReturnType());
					map.computeIfAbsent(key, k -> new ArrayList<>())
							.add(filingEntity);
				}
				for (String filingKey : map.keySet()) {
					List<VendorGstinFilingTypeEntity> entityList = map
							.get(filingKey);
					String value = "";
					int i = 0;
					for (VendorGstinFilingTypeEntity filing : entityList) {
						value = value + filing.getQuarter() + "->"
								+ filing.getFilingType().substring(0, 1);
						 if(i++ != (entityList.size() - 1)){
						        value = value + ", ";
						    }
					}
					filingTypeMap.put(filingKey, value);
				}
			}
			return filingTypeMap;
		} catch (Exception ee) {
			String msg = "Exception occured while finding filing type for gstins";
			LOGGER.error(msg);
		}
		return null;
	}

	@Override
	public List<String> getOnlyGstInList(
			List<VendorMasterUploadEntity> uploadEntities) {
		return uploadEntities.stream()
				.map(VendorMasterUploadEntity::getVendorGstin).distinct()
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getStampedVendors(List<String> vendorGstins,
			String returnType) {
		try {
			List<VendorReturnTypeEntity> stampedVendorsList = new ArrayList<>();
			List<List<String>> chunks = Lists.partition(vendorGstins, 2000);
			for (List<String> chunk : chunks) {
				stampedVendorsList.addAll(vendorReturnTypeRepo
						.findByGstinInAndReturnTypesContaining(chunk,
								returnType));
			}
			if (CollectionUtils.isEmpty(stampedVendorsList)) {
				String errMsg = String.format(
						"There are no eligible Vendor Gstins for ReturnType- %s",
						returnType);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Vendors found in stamped Vendor"
								+ " Gstin for returnType: %s are %s",
						returnType, stampedVendorsList.size());
				LOGGER.debug(msg);
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Vendors found in stamped Vendor"
								+ " Gstin for returnType: %s are %s",
						returnType, stampedVendorsList);
				LOGGER.debug(msg);
			}
			return stampedVendorsList.stream()
					.map(VendorReturnTypeEntity::getGstin)
					.collect(Collectors.toList());
		} catch (Exception ee) {
			String msg = "Error while fetching gstins from stamped vendor"
					+ " Gstin table";
			LOGGER.error(msg, ee);
			throw new AppException(ee.getMessage());
		}
	}
	private String getFilingFrequency(String eachTaxPeriod,String filingType){
		String quarter = null;
		if (eachTaxPeriod.startsWith("04")
				|| eachTaxPeriod.startsWith("05")
				|| eachTaxPeriod.startsWith("06"))
			quarter = "Q1";
		else if (eachTaxPeriod.startsWith("07")
				|| eachTaxPeriod.startsWith("08")
				|| eachTaxPeriod.startsWith("09"))
			quarter = "Q2";
		else if (eachTaxPeriod.startsWith("10")
				|| eachTaxPeriod.startsWith("11")
				|| eachTaxPeriod.startsWith("12"))
			quarter = "Q3";
		else 
			quarter = "Q4";
		String[] fileTypes= filingType.split(" ");
		if (quarter != null) {
			for (int i = 0; i < fileTypes.length; i++) {
				if (fileTypes[i].contains(quarter)) {
					if (fileTypes[i].contains("M"))
						return "MONTHLY";
					else
						return "QUARTERLY";
				}

			}
		}
		return null;
	}
}
