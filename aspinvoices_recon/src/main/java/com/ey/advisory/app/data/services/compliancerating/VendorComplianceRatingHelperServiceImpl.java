package com.ey.advisory.app.data.services.compliancerating;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorDueDateEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorMasterApiEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorMasterConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorRatingCriteriaEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.VendorReturnTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorDueDateRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterConfigEntityRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorRatingCriteriaRepository;
import com.ey.advisory.app.data.services.noncomplaintvendor.NCVendorCommHelperService;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterApiService;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Slf4j
@Component("VendorComplianceRatingHelperServiceImpl")
public class VendorComplianceRatingHelperServiceImpl
		implements VendorComplianceRatingHelperService {

	@Autowired
	@Qualifier("VendorReturnTypeRepository")
	private VendorReturnTypeRepository vendorReturnTypeRepo;

	@Autowired
	private NCVendorCommHelperService helperService;

	@Autowired
	private VendorMasterApiService helperApiService;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnStatusRepo;

	@Autowired
	@Qualifier("VendorDueDateRepository")
	private VendorDueDateRepository dueDateRepo;

	@Autowired
	@Qualifier("VendorRatingCriteriaRepository")
	private VendorRatingCriteriaRepository ratingCriteriaRepo;

	@Autowired
	private DocRepository docRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	@Autowired
	@Qualifier("VendorComplianceRatingHelperServiceImpl")
	private VendorComplianceRatingHelperService ratingHelperService;

	@Autowired
	private EntityConfigPrmtRepository entityConfigRepo;

	@Autowired
	@Qualifier("VendorMasterConfigEntityRepository")
	private VendorMasterConfigEntityRepository vendorMasterConfigRepo;

	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	@Override
	public VendorRatingOverAllDbDataDto getAllRequiredData(
			VendorComplianceRatingRequestDto requestDto) {

		try {
			Long entityId = requestDto.getEntityId();
			String returnType = requestDto.getReturnType();
			String fy = requestDto.getFy();
			String source = requestDto.getSource();
			List<String> vendorPans = requestDto.getVendorPans();
			List<String> vendorGstins = requestDto.getVendorGstins();
			List<String> uploadedGstins = new ArrayList<>();
			List<VendorMasterUploadEntity> uploadEntities = new ArrayList<>();
			Map<String, VendorRegDateDto> dateMap = new HashMap<>();
			List<VendorMasterApiEntity> uploadedApiEntities = new ArrayList<>();

			if (VendorRatingCriteriaDefaultUtil.VENDOR
					.equalsIgnoreCase(source)) {
				// get all vendor master uploaded entities
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Start time for extractAndPopulateVendorProcessedRecords is {}",
							LocalDateTime.now());
				}

				uploadEntities = helperService
						.extractAndPopulateVendorProcessedRecords(entityId,
								vendorGstins, vendorPans);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"End for getting extractAndPopulateVendorProcessedRecords at {}",
							LocalDateTime.now());
				}
				if (CollectionUtils.isEmpty(uploadEntities)) {
					String msg = String.format(
							"There is no vendor Data in vendor master for entity: %s",
							entityId);
					LOGGER.error(msg);
//					throw new AppException(msg);
				} else {

					uploadedGstins = helperService
							.getOnlyGstInList(uploadEntities);

				}

				uploadedApiEntities = helperApiService
						.extractAndPopulateVendorProcessedRecords(entityId,
								vendorGstins, vendorPans);
				if (CollectionUtils.isEmpty(uploadedApiEntities)) {
					String msg = String.format(
							"There is no vendor Data in vendor master for entity: %s",
							entityId);
					LOGGER.error(msg);
					// throw new AppException(msg);
				} else {
					uploadedGstins.addAll(helperApiService
							.getOnlyGstInList(uploadedApiEntities));
				}
				if (CollectionUtils.isEmpty(uploadedGstins)) {
					String msg = String.format(
							"There is no vendor Data in vendor master for entity: %s",
							entityId);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
				uploadedGstins = uploadedGstins.stream().distinct()
						.collect(Collectors.toList());
			} else if (VendorRatingCriteriaDefaultUtil.CUSTOMER
					.equalsIgnoreCase(source)) {

				if (vendorGstins.isEmpty()) {
					uploadedGstins = getListOfCustomerGstin(vendorPans);

				} else {
					uploadedGstins = vendorGstins;
				}
			} else {
				if (vendorGstins.isEmpty()) {
					uploadedGstins = gstNDetailRepository
							.findgstinByEntityIdWithRegTypeForGstr1(entityId);
				} else {
					uploadedGstins = vendorGstins;
				}
			}

			String rType = returnType;
			if ("GSTR1,GSTR3B".equalsIgnoreCase(returnType))
				rType = "GSTR1";
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Start time for getStampedVendors is {}",
						LocalDateTime.now());
			}
			// fetch stamped vendorGstins w.r.t. returnType
			List<String> stampedVendors = helperService
					.getStampedVendors(uploadedGstins, rType);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("End for getting getStampedVendors at {}",
						LocalDateTime.now());
			}
			// overall taxPeriods for a fy
			List<String> taxPeriods = GenUtil.extractTaxPeriodsFromFY(fy,
					returnType);

			List<GstrReturnStatusEntity> returnStatusEntities = new ArrayList<>();

			/**
			 * fetch return status entries, dueDates, Rating criteria based on
			 * gstins, returnType List, taxperiod List
			 **/
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Start time for returnStatusEntities is {} for gstin count {}",
						LocalDateTime.now(), stampedVendors.size());
			}
			List<String> returnTypeList = Arrays.asList(returnType.split(","));
			List<List<String>> returnChunks = Lists.partition(stampedVendors,
					2000);
			for (List<String> chunk : returnChunks) {
				dateMap.putAll(convertRegDatesToMap(chunk));

				if (VendorRatingCriteriaDefaultUtil.MY_COMPLIANCE
						.equalsIgnoreCase(source)) {
					returnStatusEntities.addAll(returnStatusRepo
							.findByGstinInAndReturnTypeInAndTaxPeriodInAndIsCounterPartyGstinFalse(
									chunk, returnTypeList, taxPeriods));
				} else {
					returnStatusEntities.addAll(returnStatusRepo
							.findByGstinInAndReturnTypeInAndTaxPeriodInAndIsCounterPartyGstinTrue(
									chunk, returnTypeList, taxPeriods));
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"End for getting returnStatusEntities at {} for gstin count {}",
						LocalDateTime.now(), stampedVendors.size());
			}
			List<VendorDueDateEntity> dueDates = dueDateRepo
					.findByReturnTypeInAndEntityIdAndTaxPeriodInAndIsDeleteFalse(
							returnTypeList, entityId, taxPeriods);

			List<VendorRatingCriteriaEntity> ratingCriteria = ratingCriteriaRepo
					.findByEntityIdAndReturnTypeInAndIsDeleteFalseAndSource(
							entityId, returnTypeList,
							VendorRatingCriteriaDefaultUtil.SOURCEMAP
									.get(source));

			/**
			 * Convert dueDate enties to map where key is the KEY from table
			 * which is KEY(ReturnType|TaxPeriod|EntityId|SateCode) and value is
			 * Due Date which is LocalDate
			 */
			Map<String, LocalDate> dueDateMap = dueDates.stream()
					.collect(Collectors.toMap(VendorDueDateEntity::getKey,
							VendorDueDateEntity::getDueDate));

			Map<String, String> filingTypeMap = new HashMap<>();

			/**
			 * getting filingType map from filingType table where key is
			 * "Gstin-ReturnType" and value is FilingType(TypeOfGstFiling)
			 */
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Start time for getfilingTypeMapFromStampedTable is {} for gstin count {}",
						LocalDateTime.now(), stampedVendors.size());
			}

			filingTypeMap = helperService
					.getfilingTypeMapFromStampedTable(stampedVendors, fy);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"End for getting getfilingTypeMapFromStampedTable at {} for gstin count {}",
						LocalDateTime.now(), stampedVendors.size());
			}

			return new VendorRatingOverAllDbDataDto(stampedVendors, taxPeriods,
					ratingCriteria, dueDateMap, returnStatusEntities,
					filingTypeMap, uploadEntities, dateMap,
					uploadedApiEntities);
		} catch (Exception ee) {
			String msg = "Exception occured while fetching all required data"
					+ " from db to calculate vendor rating";
			LOGGER.error(msg, ee);
			throw new AppException(ee.getMessage());
		}
	}

	/**
	 * This method returns filing status and rate for a taxPeriod
	 */
	private Triplet<String, BigDecimal, Boolean> getFilingStatusAndRating(
			String gstin, String returnType, String taxPeriod,
			LocalDate dueDate, Map<String, LocalDate> returnStatusMap,
			List<VendorRatingCriteriaEntity> ratingCriteria, int month, int day,
			int year,String filingType) {

		LocalDate dateOfFiling = null;
		String status = OverAllGstinStatus.NOT_COMPLIANT.getStatus();

		String retrunStatusKey = gstin + "|" + taxPeriod + "|" + returnType;
		if (returnStatusMap.containsKey(retrunStatusKey)) {
			dateOfFiling = returnStatusMap.get(retrunStatusKey);
			status = OverAllGstinStatus.COMPLIANT.getStatus();
		}

		BigDecimal rate = BigDecimal.ZERO;
		boolean isFiledOnTime = false;
		List<String> quarterMonths = new ArrayList<>();
		quarterMonths.add("06");
		quarterMonths.add("09");
		quarterMonths.add("12");
		quarterMonths.add("03");
		String taxPeriodMonth = taxPeriod.substring(0,2);

		if((!quarterMonths.contains(taxPeriodMonth)) && filingType.equalsIgnoreCase("Quarterly")){
			status = OverAllGstinStatus.UNKNOWN.getStatus();
		} else if (isDueDateCompleted(dueDate, dateOfFiling, month, day, year)) {

			Pair<BigDecimal, Boolean> rateStatPair = findDueTypeAndValidRating(
					returnType, taxPeriod, status, dueDate, dateOfFiling,
					ratingCriteria);
			rate = rateStatPair.getValue0();
			isFiledOnTime = rateStatPair.getValue1();
		} else {
			/*
			 * If dueDate is not completed then its a future taxPeriod and we
			 * still have time for filing so we mark the status as unknown
			 */
			status = OverAllGstinStatus.UNKNOWN.getStatus();
		}
		return new Triplet<>(status, rate, isFiledOnTime);
	}

	/**
	 * This method returns true if dueDate is completed Or if a gstin is
	 * filed(Compliant).
	 * 
	 * @param dueDate-
	 *            for a perticular taxPeriod
	 * @param dateOfFiling
	 *            - will be localDate if filed else it will be null
	 * @param month
	 *            - current month
	 * @param day
	 *            - current day of month
	 * @param year
	 *            - current year
	 * @return boolean
	 */
	private boolean isDueDateCompleted(LocalDate dueDate,
			LocalDate dateOfFiling, int month, int day, int year) {

		/*
		 * return true if already filed
		 */
		if (dateOfFiling != null)
			return true;
		/*
		 * return false if due date is not available for a taxperiod in dueDate
		 * master
		 */
		else if (dueDate == null)
			return false;

		/*
		 * If dueDate is present then check if current date has crossed dueDate.
		 * If yes then return true else return false as still due Date is not
		 * crossed, it will be considered as future date.
		 */
		else {
			int dueDateYear = Integer
					.parseInt(dueDate.toString().substring(0, 4));
			int dueDateMonth = Integer
					.parseInt(dueDate.toString().substring(5, 7));
			int dueDateDay = Integer
					.parseInt(dueDate.toString().substring(8, 10));
			if (year > dueDateYear) {
				return true;
			} else if (year == dueDateYear) {
				if (month > dueDateMonth) {
					return true;
				} else if (month == dueDateMonth) {
					if (day > dueDateDay)
						return true;
					else
						return false;
				} else
					return false;
			}
			return false;
		}
	}

	private Set<String> getLastThreeTaxPeriods(String returnType, Long entityId,
			String stateCode, Map<String, LocalDate> dueDateMap) {

		List<String> prevTaxPeriodList = new ArrayList<>();
		String taxPeriod = GenUtil.getCurrentTaxPeriod();

		try {

			LocalDate dueDate = null;
			String dueDateKeyWithSateCode = String.format("%s|%s|%s|%s",
					returnType, taxPeriod, entityId, stateCode);

			if (dueDateMap.containsKey(dueDateKeyWithSateCode))
				dueDate = dueDateMap.get(dueDateKeyWithSateCode);

			String dueDateKey = String.format("%s|%s|%s", returnType, taxPeriod,
					entityId);

			if (dueDateMap.containsKey(dueDateKey))
				dueDate = dueDateMap.get(dueDateKey);

			if (dueDate == null) {
				String msg = String.format(
						"No Due Date found in Due Date Master for the key: %s",
						dueDateKey);
				LOGGER.error(msg);
				return Collections.emptySet();
			}
			LocalDate today = EYDateUtil.toISTDateTimeFromUTC(LocalDate.now());

			int compareValue = today.compareTo(dueDate);

			if (compareValue > 0) {
				LocalDate lastMonth = today.minusMonths(1);
				prevTaxPeriodList = GenUtil
						.listOfPrevtaxPeriodForDate(lastMonth, 3);
			} else if (compareValue < 0 || compareValue == 0) {
				LocalDate lastMonth = today.minusMonths(2);
				prevTaxPeriodList = GenUtil
						.listOfPrevtaxPeriodForDate(lastMonth, 3);
			}
			return new HashSet<>(prevTaxPeriodList);

		} catch (Exception e) {
			LOGGER.error("Error while getting previous 3 taxperiods", e);
			return Collections.emptySet();
		}
	}

	private LocalDate getDueDate(String returnType, String taxPeriod,
			String entityId, String stateCode,
			Map<String, LocalDate> dueDateMap, String returnFilingFrequency) {

		/*
		 * First finding dueDate with state code on Priority. If not present
		 * then find it without stateCode in KEY.
		 * 
		 * If no dueDate found then it returns null.
		 */
		String dueDateKeyWithSateCode = String.format("%s|%s|%s|%s|%s",
				returnType, taxPeriod, returnFilingFrequency, entityId,
				stateCode);
		if (dueDateMap.containsKey(dueDateKeyWithSateCode))
			return dueDateMap.get(dueDateKeyWithSateCode);

		String dueDateKey = String.format("%s|%s|%s|%s", returnType, taxPeriod,
				returnFilingFrequency, entityId);
		if (dueDateMap.containsKey(dueDateKey))
			return dueDateMap.get(dueDateKey);

		LOGGER.error("No Due Date found in Due Date Master for the key:{}",
				dueDateKey);
		return null;
	}

	/**
	 * This method finds dueType based on the difference in dueDate and
	 * DateOfFiling. From DueType it get the rating from rating Criteria. If the
	 * criteria is not matching then it returns 0 as rating.
	 */
	@Override
	public Pair<BigDecimal, Boolean> findDueTypeAndValidRating(
			String returnType, String taxPeriod, String status,
			LocalDate dueDate, LocalDate dateOfFiling,
			List<VendorRatingCriteriaEntity> ratingCriteria) {
		int numberOfDays = 0;
		String dueType = null;
		BigDecimal rate = BigDecimal.ZERO;
		boolean isFiledOnDate = false;
		if (!status.equals(OverAllGstinStatus.UNKNOWN.getStatus())) {
			if (status.equals(OverAllGstinStatus.COMPLIANT.getStatus())) {
				if (dateOfFiling == null || dueDate == null) {
					String msg = String.format(
							"Filing Date/Due Date is null"
									+ " for ReturnType: %s and taxPeriod: %s",
							returnType, taxPeriod);
					LOGGER.error(msg);
					return new Pair<>(rate, isFiledOnDate);
				}
				long daysBetween = ChronoUnit.DAYS.between(dueDate,
						dateOfFiling);
				if (daysBetween > 0) {
					dueType = "AFTER";
					numberOfDays = (int) daysBetween;
				} else if (daysBetween < 0) {
					dueType = "PRIOR";
					numberOfDays = (int) Math.abs(daysBetween);
					isFiledOnDate = true;
				} else if (daysBetween == 0) {
					dueType = "ON";
					isFiledOnDate = true;
				}
			} else if (status
					.equals(OverAllGstinStatus.NOT_COMPLIANT.getStatus())) {
				dueType = "NOT";
			}

			rate = getValidRate(returnType, dueType, numberOfDays, rate,
					ratingCriteria);
			if (rate == BigDecimal.ZERO) {
				String msg = String.format(
						"No Rating Criteria was found in Rating Criteria table"
								+ " for ReturnType: %s,TaxPeriod: %s,"
								+ " DueType: %s, NumberOfDays: %s",
						returnType, taxPeriod, dueType, numberOfDays);
				LOGGER.error(msg);
			}
		} else {
			String msg = String.format(
					"Filing Status is Unknown"
							+ " for ReturnType: %s, DueType: %s. "
							+ "Hence rate = 0 for respective taxPeriod: %s",
					returnType, dueType, taxPeriod);
			LOGGER.error(msg);
		}
		return new Pair<>(rate, isFiledOnDate);
	}

	/**
	 * find rate based on dueType from rating criteria table. If no criteria is
	 * matching then default rate will be 0
	 */
	private BigDecimal getValidRate(String returnType, String dueType,
			int numberOfDays, BigDecimal rate,
			List<VendorRatingCriteriaEntity> ratingCriteria) {
		List<VendorRatingCriteriaEntity> sortedCriteria = new ArrayList<>();
		for (VendorRatingCriteriaEntity criteria : ratingCriteria) {
			if (returnType.equalsIgnoreCase(criteria.getReturnType())
					&& dueType.equalsIgnoreCase(criteria.getDueType())) {
				sortedCriteria.add(criteria);
			}
		}
		if (sortedCriteria != null) {
			Collections.sort(sortedCriteria,
					new Comparator<VendorRatingCriteriaEntity>() {
						public int compare(VendorRatingCriteriaEntity o1,
								VendorRatingCriteriaEntity o2) {
							return (o1.getFromDay().compareTo(o2.getFromDay()));
						}
					});

			for (VendorRatingCriteriaEntity criteria : sortedCriteria) {
				if (numberOfDays == 0 && "ON".equalsIgnoreCase(dueType)) {
					rate = criteria.getRating();
					break;
				} else if (numberOfDays == 0
						&& "NOT".equalsIgnoreCase(dueType)) {
					rate = criteria.getRating();
					break;
				} else if (numberOfDays > 0
						&& ("PRIOR".equalsIgnoreCase(dueType)
								|| "AFTER".equalsIgnoreCase(dueType))) {
					if (numberOfDays >= criteria.getFromDay()
							&& numberOfDays <= criteria.getToDay()) {
						rate = criteria.getRating();
						break;
					}
				}
			}
		}
		return rate;
	}

	@Override
	public void getGstinWiseRatingStatusDtos(
			List<GstinWiseComplianceRatingStatus> gstinWiseRatingstatus,
			VendorRatingOverAllDbDataDto allDatadto, String returnType,
			String entityId, int month, int day, int year, String source) {

		List<String> stampedVendors = allDatadto.getStampedVendors();
		List<String> taxPeriods = allDatadto.getTaxPeriods();
		List<VendorRatingCriteriaEntity> ratingCriteria = allDatadto
				.getRatingCriteria();
		Map<String, LocalDate> dueDateMap = allDatadto.getDueDateMap();
		Map<String, String> filingTypeMap = allDatadto
				.getFilingTypeMap();

		Map<String, LocalDate> returnStatusMap = convertToReturnStatusMap(
				allDatadto.getReturnStatusEntities());

		Map<String, VendorRegDateDto> dateMap = allDatadto.getDateMap();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Start time for getGstinWiseRatingStatusDtos is {} and gsting count {}",
					LocalDateTime.now(), stampedVendors.size());
		}
		for (String gstin : stampedVendors) {
			String key = String.format("%s-%s", gstin, "GSTR1");
			String filingType = filingTypeMap.containsKey(key)
					? filingTypeMap.get(key).toString()
					: "";

			GstinWiseComplianceRatingStatus gstinWise = new GstinWiseComplianceRatingStatus();
			gstinWise.setVendorGstin(gstin);
			gstinWise.setFilingType(filingType);
			Integer ttlRetFiled = 0;
			Integer retFiledInTime = 0;
			Integer lateRetFiled = 0;
			Integer ttlRetNotFiled = 0;
			Boolean isCancelled = false;
			List<MonthWiseComplianceRatingStatus> monthlyStatus = new ArrayList<>();
			for (String eachTaxPeriod : taxPeriods) {
				MonthWiseComplianceRatingStatus monthWise = new MonthWiseComplianceRatingStatus();
				monthWise.setMonth(eachTaxPeriod);
				String updatedFilingType = getFilingFrequency(eachTaxPeriod,filingType);
				
				if (dateMap.get(gstin) == null || (dateMap.get(gstin) != null
						&& dateMap.get(gstin).getRegDate() == null)) {
					monthWise.setGstr1Rating(BigDecimal.ZERO);
					monthWise.setGstr3BRating(BigDecimal.ZERO);
					monthWise.setGstr1Status(
							OverAllGstinStatus.UNKNOWN.getStatus());
					monthWise.setGstr3bStatus(
							OverAllGstinStatus.UNKNOWN.getStatus());
					monthlyStatus.add(monthWise);
					continue;
				} else {
					LocalDate regDate = dateMap.get(gstin).getRegDate();
					LocalDate canDate = dateMap.get(gstin).getCanDate();
					String regDateTaxPeriod = regDate != null
							? GenUtil.listOfPrevtaxPeriodForDate(regDate, 1)
									.get(0)
							: null;
					String canDateTaxPeriod = canDate != null
							? GenUtil.listOfPrevtaxPeriodForDate(canDate, 1)
									.get(0)
							: null;

					Integer derivedTaxPeriod = GenUtil
							.getDerivedTaxPeriod(eachTaxPeriod);

					Integer regDateDerivedTaxPeriod = regDateTaxPeriod != null
							? GenUtil.getDerivedTaxPeriod(regDateTaxPeriod)
							: null;

					Integer canDateDerivedTaxPeriod = canDateTaxPeriod != null
							? GenUtil.getDerivedTaxPeriod(canDateTaxPeriod)
							: null;

					if ((regDateDerivedTaxPeriod != null
							&& derivedTaxPeriod < regDateDerivedTaxPeriod)
							|| (canDateDerivedTaxPeriod != null
									&& derivedTaxPeriod > canDateDerivedTaxPeriod)) {
						// monthWise.setRating(BigDecimal.ZERO);
						monthWise.setGstr1Rating(BigDecimal.ZERO);
						monthWise.setGstr3BRating(BigDecimal.ZERO);
						monthWise.setGstr1Status(
								OverAllGstinStatus.UNKNOWN.getStatus());
						monthWise.setGstr3bStatus(
								OverAllGstinStatus.UNKNOWN.getStatus());
						monthlyStatus.add(monthWise);
						isCancelled = true;
						continue;
					}
				}

				if ("GSTR1".equalsIgnoreCase(returnType)
						|| "GSTR3B".equalsIgnoreCase(returnType)) {

					LocalDate dueDate = getDueDate(returnType, eachTaxPeriod,
							entityId, gstin.substring(0, 2), dueDateMap,
							updatedFilingType.toUpperCase());

					Triplet<String, BigDecimal, Boolean> statRateTriplet = getFilingStatusAndRating(
							gstin, returnType, eachTaxPeriod, dueDate,
							returnStatusMap, ratingCriteria, month, day, year,updatedFilingType);

					if ("GSTR1".equalsIgnoreCase(returnType)) {
						monthWise.setGstr1Status(statRateTriplet.getValue0());
						monthWise.setGstr1Rating(statRateTriplet.getValue1());
						LocalDate gstr1FilingDate = returnStatusMap
								.get(gstin.concat("|").concat(eachTaxPeriod)
										.concat("|").concat(returnType));
						if (gstr1FilingDate != null) {
							monthWise.setGstr1ReturnFilingDate(
									gstr1FilingDate.format(formatter).toString());
						}
					} else {
						monthWise.setGstr3bStatus(statRateTriplet.getValue0());
						monthWise.setGstr3BRating(statRateTriplet.getValue1());
						LocalDate gstr3BFilingDate = returnStatusMap
								.get(gstin.concat("|").concat(eachTaxPeriod)
										.concat("|").concat(returnType));
						if (gstr3BFilingDate != null) {
							monthWise.setGstr3BReturnFilingDate(
									gstr3BFilingDate.format(formatter).toString());
						}
					}
					// monthWise.setRating(statRateTriplet.getValue1());
					if (statRateTriplet.getValue2()) {
						ttlRetFiled++;
						retFiledInTime++;
						if ("GSTR1".equalsIgnoreCase(returnType))
							monthWise.setFiledOnTime(true);
						else
							monthWise.setGstr3BFiledOnTime(true);
					} else {
						if (OverAllGstinStatus.COMPLIANT.getStatus()
								.equalsIgnoreCase(
										statRateTriplet.getValue0())) {
							lateRetFiled++;
							ttlRetFiled++;
							if ("GSTR1".equalsIgnoreCase(returnType))
								monthWise.setFiledOnTime(false);
							else
								monthWise.setGstr3BFiledOnTime(false);
						}
					}
					if (OverAllGstinStatus.NOT_COMPLIANT.getStatus()
							.equalsIgnoreCase(statRateTriplet.getValue0())) {
						ttlRetNotFiled++;
					}
				} else {

					LocalDate gstr1DueDate = getDueDate("GSTR1", eachTaxPeriod,
							entityId, gstin.substring(0, 2), dueDateMap,
							updatedFilingType.toUpperCase());

					LocalDate gstr3bDueDate = getDueDate("GSTR3B",
							eachTaxPeriod, entityId, gstin.substring(0, 2),
							dueDateMap, updatedFilingType.toUpperCase());

					Triplet<String, BigDecimal, Boolean> statRateTripletGstr1 = getFilingStatusAndRating(
							gstin, "GSTR1", eachTaxPeriod, gstr1DueDate,
							returnStatusMap, ratingCriteria, month, day, year,updatedFilingType);

					monthWise.setGstr1Status(statRateTripletGstr1.getValue0());
					// BigDecimal rating = statRateTripletGstr1.getValue1();
					monthWise.setGstr1Rating(statRateTripletGstr1.getValue1());

					if (statRateTripletGstr1.getValue2()) {
						retFiledInTime++;
						ttlRetFiled++;
						monthWise.setFiledOnTime(true);
					} else {
						if (OverAllGstinStatus.COMPLIANT.getStatus()
								.equalsIgnoreCase(
										statRateTripletGstr1.getValue0())) {
							lateRetFiled++;
							ttlRetFiled++;
							monthWise.setFiledOnTime(false);
						}
					}
					if (OverAllGstinStatus.NOT_COMPLIANT.getStatus()
							.equalsIgnoreCase(
									statRateTripletGstr1.getValue0())) {
						ttlRetNotFiled++;
					}
					LocalDate gstr1FilingDate = returnStatusMap
							.get(gstin.concat("|").concat(eachTaxPeriod)
									.concat("|").concat("GSTR1"));
					if (gstr1FilingDate != null) {
						monthWise.setGstr1ReturnFilingDate(
								gstr1FilingDate.format(formatter).toString());
					}
					Triplet<String, BigDecimal, Boolean> statRateTripletGstr3B = getFilingStatusAndRating(
							gstin, "GSTR3B", eachTaxPeriod, gstr3bDueDate,
							returnStatusMap, ratingCriteria, month, day, year,updatedFilingType);

					monthWise
							.setGstr3bStatus(statRateTripletGstr3B.getValue0());
					monthWise
							.setGstr3BRating(statRateTripletGstr3B.getValue1());

					// rating = rating.add(statRateTripletGstr3B.getValue1());
					// monthWise.setRating(rating);

					if (statRateTripletGstr3B.getValue2()) {
						retFiledInTime++;
						ttlRetFiled++;
						monthWise.setGstr3BFiledOnTime(true);

					} else {
						if (OverAllGstinStatus.COMPLIANT.getStatus()
								.equalsIgnoreCase(
										statRateTripletGstr3B.getValue0())) {
							lateRetFiled++;
							ttlRetFiled++;
							monthWise.setGstr3BFiledOnTime(false);
						}
					}
					if (OverAllGstinStatus.NOT_COMPLIANT.getStatus()
							.equalsIgnoreCase(
									statRateTripletGstr3B.getValue0())) {
						ttlRetNotFiled++;
					}
					LocalDate gstr3BFilingDate = returnStatusMap
							.get(gstin.concat("|").concat(eachTaxPeriod)
									.concat("|").concat("GSTR3B"));
					if (gstr3BFilingDate != null) {
						monthWise.setGstr3BReturnFilingDate(
								gstr3BFilingDate.format(formatter).toString());
					}
				}
				monthlyStatus.add(monthWise);
			}

			gstinWise.setMonthWiseStatusCombination(monthlyStatus);
			gstinWise.setRetFiledInTime(retFiledInTime);
			gstinWise.setLateRetFiled(lateRetFiled);
			gstinWise.setTtlRetFiled(ttlRetFiled);
			gstinWise.setTtlRetNotFiled(ttlRetNotFiled);
			gstinWise.setCancelled(isCancelled);
			gstinWiseRatingstatus.add(gstinWise);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"End time for getGstinWiseRatingStatusDtos is {} and gsting count {}",
						LocalDateTime.now(), stampedVendors.size());
			}
		}
	}

	private Map<String, LocalDate> convertToReturnStatusMap(
			List<GstrReturnStatusEntity> returnStatusEntities) {
		return returnStatusEntities.stream()
				.filter(a -> a.getGstin() != null && a.getTaxPeriod() != null
						&& a.getReturnType() != null
						&& a.getFilingDate() != null)
				.collect(Collectors.toMap(
						s -> s.getGstin().concat("|").concat(s.getTaxPeriod())
								.concat("|").concat(s.getReturnType()),
						s -> s.getFilingDate()));
	}

	@Override
	public void getDefaultReportDtos(
			List<VendorComplianceRatingReportDto> defaultList,
			VendorRatingOverAllDbDataDto allDatadto, String returnType,
			String entityId, int month, int day, int year, String source) {

		List<String> stampedVendors = allDatadto.getStampedVendors();
		List<String> taxPeriods = allDatadto.getTaxPeriods();
		List<VendorRatingCriteriaEntity> ratingCriteria = allDatadto
				.getRatingCriteria();
		Map<String, LocalDate> dueDateMap = allDatadto.getDueDateMap();
		Map<String, String> filingTypeMap = allDatadto
				.getFilingTypeMap();
		boolean isChannelOneClient = ratingHelperService
				.getChannelOneClientInfo(Long.valueOf(entityId));
		Map<String, LocalDate> returnStatusMap = convertToReturnStatusMap(
				allDatadto.getReturnStatusEntities());
		Map<String, VendorRegDateDto> dateMap = allDatadto.getDateMap();
		for (String gstin : stampedVendors) {
			String key = String.format("%s-%s", gstin, "GSTR1");
			String filingFrequency = filingTypeMap.containsKey(key)
					? filingTypeMap.get(key).toString() : "";
							
					
			for (String eachTaxPeriod : taxPeriods) {
				String filingType = getFilingFrequency(
						eachTaxPeriod, filingFrequency);
				String updatedFilingType = getFilingFrequency(eachTaxPeriod,filingType);
				VendorComplianceRatingReportDto reportDto = new VendorComplianceRatingReportDto();
				reportDto.setVendorGSTIN(gstin);
				reportDto.setVendorName(null);
				reportDto.setQuarterlyorMonthlyfiler(filingType);
				reportDto.setReturnType(returnType);
				reportDto.setTaxPeriod(eachTaxPeriod);
				if ("vendor".equalsIgnoreCase(source)) {
					reportDto.setSourceofGSTIN("Vendor Master");
				} else if ("customer".equalsIgnoreCase(source)) {
					reportDto.setSourceofGSTIN("Outward");
				} else {
					reportDto.setSourceofGSTIN("Onboarding");
				}

				if (dateMap.get(gstin) == null || (dateMap.get(gstin) != null
						&& dateMap.get(gstin).getRegDate() == null)) {

					reportDto.setGstr1ArnNo(null);
					reportDto.setGstr3BArnNo(null);
					reportDto.setGstr1FilingDate(null);
					reportDto.setGstr1StatusofReturnFiling(
							OverAllGstinStatus.UNKNOWN.getStatus());
					reportDto.setGstr3BFilingDate(null);
					reportDto.setGstr3BStatusofReturnFiling(
							OverAllGstinStatus.UNKNOWN.getStatus());
					reportDto.setRating(BigDecimal.ZERO);
					defaultList.add(reportDto);
					continue;
				} else {
					LocalDate regDate = dateMap.get(gstin).getRegDate();
					LocalDate canDate = dateMap.get(gstin).getCanDate();
					String regDateTaxPeriod = regDate != null
							? GenUtil.listOfPrevtaxPeriodForDate(regDate, 1)
									.get(0)
							: null;
					String canDateTaxPeriod = canDate != null
							? GenUtil.listOfPrevtaxPeriodForDate(canDate, 1)
									.get(0)
							: null;

					Integer derivedTaxPeriod = GenUtil
							.getDerivedTaxPeriod(eachTaxPeriod);

					Integer regDateDerivedTaxPeriod = regDateTaxPeriod != null
							? GenUtil.getDerivedTaxPeriod(regDateTaxPeriod)
							: null;

					Integer canDateDerivedTaxPeriod = canDateTaxPeriod != null
							? GenUtil.getDerivedTaxPeriod(canDateTaxPeriod)
							: null;

					if ((regDateDerivedTaxPeriod != null
							&& derivedTaxPeriod < regDateDerivedTaxPeriod)
							|| (canDateDerivedTaxPeriod != null
									&& derivedTaxPeriod > canDateDerivedTaxPeriod)) {
						reportDto.setGstr1ArnNo(null);
						reportDto.setGstr3BArnNo(null);
						reportDto.setGstr1FilingDate(null);
						reportDto.setGstr1StatusofReturnFiling(
								OverAllGstinStatus.UNKNOWN.getStatus());
						reportDto.setGstr3BFilingDate(null);
						reportDto.setGstr3BStatusofReturnFiling(
								OverAllGstinStatus.UNKNOWN.getStatus());
						reportDto.setRating(BigDecimal.ZERO);
						defaultList.add(reportDto);
						continue;
					}
				}

				if ("GSTR1".equalsIgnoreCase(returnType)) {
					LocalDate dueDate = getDueDate(returnType, eachTaxPeriod,
							entityId, gstin.substring(0, 2), dueDateMap,
							filingType.toUpperCase());
					Triplet<String, BigDecimal, Boolean> statRateTriplet = getFilingStatusAndRating(
							gstin, returnType, eachTaxPeriod, dueDate,
							returnStatusMap, ratingCriteria, month, day, year,updatedFilingType);

					reportDto.setGstr1StatusofReturnFiling(
							statRateTriplet.getValue0());
					if (!isChannelOneClient) {
						reportDto.setRating(statRateTriplet.getValue1());
					}
				} else if ("GSTR3B".equalsIgnoreCase(returnType)) {

					LocalDate dueDate = getDueDate(returnType, eachTaxPeriod,
							entityId, gstin.substring(0, 2), dueDateMap,
							filingType.toUpperCase());
					Triplet<String, BigDecimal, Boolean> statRateTriplet = getFilingStatusAndRating(
							gstin, returnType, eachTaxPeriod, dueDate,
							returnStatusMap, ratingCriteria, month, day, year,updatedFilingType);

					reportDto.setGstr3BStatusofReturnFiling(
							statRateTriplet.getValue0());
					if (!isChannelOneClient) {
						reportDto.setRating(statRateTriplet.getValue1());
					}
				} else if ("GSTR1,GSTR3B".equalsIgnoreCase(returnType)) {

					LocalDate gstr1DueDate = getDueDate("GSTR1", eachTaxPeriod,
							entityId, gstin.substring(0, 2), dueDateMap,
							filingType.toUpperCase());

					LocalDate gstr3bDueDate = getDueDate("GSTR3B",
							eachTaxPeriod, entityId, gstin.substring(0, 2),
							dueDateMap, filingType.toUpperCase());

					Triplet<String, BigDecimal, Boolean> statRateTripletGstr1 = getFilingStatusAndRating(
							gstin, "GSTR1", eachTaxPeriod, gstr1DueDate,
							returnStatusMap, ratingCriteria, month, day, year,updatedFilingType);

					reportDto.setGstr1StatusofReturnFiling(
							statRateTripletGstr1.getValue0());
					BigDecimal rating = statRateTripletGstr1.getValue1();

					Triplet<String, BigDecimal, Boolean> statRateTripletGstr3B = getFilingStatusAndRating(
							gstin, "GSTR3B", eachTaxPeriod, gstr3bDueDate,
							returnStatusMap, ratingCriteria, month, day, year,updatedFilingType);

					reportDto.setGstr3BStatusofReturnFiling(
							statRateTripletGstr3B.getValue0());
					rating = rating.add(statRateTripletGstr3B.getValue1());
					if (!isChannelOneClient) {
						reportDto.setRating(rating);
					}
					reportDto.setReturnType(returnType);
				}
				defaultList.add(reportDto);
			}
		}
	}

	@Override
	public void getAllCompliantReportDtos(
			List<VendorComplianceRatingReportDto> compliantList,
			VendorRatingOverAllDbDataDto allDatadto, String returnType,
			String entityId, String source) {

		List<GstrReturnStatusEntity> returnStatusEntities = allDatadto
				.getReturnStatusEntities();
		List<VendorRatingCriteriaEntity> ratingCriteria = allDatadto
				.getRatingCriteria();
		Map<String, LocalDate> dueDateMap = allDatadto.getDueDateMap();
		Map<String, String> filingTypeMap = allDatadto
				.getFilingTypeMap();
		Map<String, VendorRegDateDto> dateMap = allDatadto.getDateMap();
		boolean isChannelOneClient = ratingHelperService
				.getChannelOneClientInfo(Long.valueOf(entityId));
		returnStatusEntities.forEach(eachObj -> {
			VendorComplianceRatingReportDto compliantVendorsRatingReport = new VendorComplianceRatingReportDto();

			String gstin = eachObj.getGstin();

			if ("vendor".equalsIgnoreCase(source)) {
				compliantVendorsRatingReport.setSourceofGSTIN("Vendor Master");
			} else if ("customer".equalsIgnoreCase(source)) {
				compliantVendorsRatingReport.setSourceofGSTIN("Outward");
			} else {
				compliantVendorsRatingReport.setSourceofGSTIN("Onboarding");
			}
			compliantVendorsRatingReport.setRating(BigDecimal.ZERO);

			if (dateMap.get(gstin) == null || (dateMap.get(gstin) != null
					&& dateMap.get(gstin).getRegDate() == null)) {

				compliantVendorsRatingReport
						.setTaxPeriod(eachObj.getTaxPeriod());
				compliantVendorsRatingReport.setVendorGSTIN(gstin);
				compliantVendorsRatingReport.setReturnType(returnType);
				compliantVendorsRatingReport.setGstr1StatusofReturnFiling(
						OverAllGstinStatus.UNKNOWN.getStatus());
				compliantVendorsRatingReport.setGstr1ArnNo(null);
				compliantVendorsRatingReport.setGstr1FilingDate(null);
				compliantVendorsRatingReport.setGstr3BStatusofReturnFiling(
						OverAllGstinStatus.UNKNOWN.getStatus());
				compliantVendorsRatingReport.setGstr3BArnNo(null);
				compliantVendorsRatingReport.setGstr3BFilingDate(null);
				compliantVendorsRatingReport.setRating(BigDecimal.ZERO);
				compliantList.add(compliantVendorsRatingReport);
				if ("vendor".equalsIgnoreCase(source)) {
					compliantVendorsRatingReport
							.setSourceofGSTIN("Vendor Master");
				} else if ("customer".equalsIgnoreCase(source)) {
					compliantVendorsRatingReport.setSourceofGSTIN("Outward");
				} else {
					compliantVendorsRatingReport.setSourceofGSTIN("Onboarding");
				}
				compliantVendorsRatingReport.setRating(BigDecimal.ZERO);
				compliantList.add(compliantVendorsRatingReport);
				return;
			} else {
				LocalDate regDate = dateMap.get(gstin).getRegDate();
				LocalDate canDate = dateMap.get(gstin).getCanDate();
				String regDateTaxPeriod = regDate != null
						? GenUtil.listOfPrevtaxPeriodForDate(regDate, 1).get(0)
						: null;
				String canDateTaxPeriod = canDate != null
						? GenUtil.listOfPrevtaxPeriodForDate(canDate, 1).get(0)
						: null;

				Integer derivedTaxPeriod = GenUtil
						.getDerivedTaxPeriod(eachObj.getTaxPeriod());

				Integer regDateDerivedTaxPeriod = regDateTaxPeriod != null
						? GenUtil.getDerivedTaxPeriod(regDateTaxPeriod)
						: null;

				Integer canDateDerivedTaxPeriod = canDateTaxPeriod != null
						? GenUtil.getDerivedTaxPeriod(canDateTaxPeriod)
						: null;

				if ((regDateDerivedTaxPeriod != null
						&& derivedTaxPeriod < regDateDerivedTaxPeriod)
						|| (canDateDerivedTaxPeriod != null
								&& derivedTaxPeriod > canDateDerivedTaxPeriod)) {
					compliantVendorsRatingReport
							.setTaxPeriod(eachObj.getTaxPeriod());
					compliantVendorsRatingReport.setVendorGSTIN(gstin);
					compliantVendorsRatingReport.setReturnType(returnType);
					compliantVendorsRatingReport.setGstr1StatusofReturnFiling(
							OverAllGstinStatus.UNKNOWN.getStatus());
					compliantVendorsRatingReport.setGstr1ArnNo(null);
					compliantVendorsRatingReport.setGstr1FilingDate(null);
					compliantVendorsRatingReport.setGstr3BStatusofReturnFiling(
							OverAllGstinStatus.UNKNOWN.getStatus());
					compliantVendorsRatingReport.setGstr3BArnNo(null);
					compliantVendorsRatingReport.setGstr3BFilingDate(null);
					compliantVendorsRatingReport.setRating(BigDecimal.ZERO);
					compliantList.add(compliantVendorsRatingReport);
					if ("vendor".equalsIgnoreCase(source)) {
						compliantVendorsRatingReport
								.setSourceofGSTIN("Vendor Master");
					} else if ("customer".equalsIgnoreCase(source)) {
						compliantVendorsRatingReport
								.setSourceofGSTIN("Outward");
					} else {
						compliantVendorsRatingReport
								.setSourceofGSTIN("Onboarding");
					}
					compliantVendorsRatingReport.setRating(BigDecimal.ZERO);
					compliantList.add(compliantVendorsRatingReport);
					return;
				}
			}

			compliantVendorsRatingReport.setTaxPeriod(eachObj.getTaxPeriod());
			compliantVendorsRatingReport.setVendorGSTIN(gstin);
			String key = String.format("%s-%s", gstin, "GSTR1");
			
			String filingFrequency = filingTypeMap.containsKey(key)
					? filingTypeMap.get(key).toString() : "";
			String filingType = getFilingFrequency(
					eachObj.getTaxPeriod(), filingFrequency);
			
		
//			String filingType = filingTypeMap.containsKey(key)
//					? filingTypeMap.get(key).toString()
//					: TypeOfGstFiling.MONTHLY.toString();

			compliantVendorsRatingReport.setQuarterlyorMonthlyfiler(filingType);

			if ("GSTR1".equalsIgnoreCase(eachObj.getReturnType())
					&& ("GSTR1".equalsIgnoreCase(returnType)
							|| "GSTR1,GSTR3B".equalsIgnoreCase(returnType))) {
				compliantVendorsRatingReport.setReturnType(returnType);
				compliantVendorsRatingReport
						.setGstr1StatusofReturnFiling(eachObj.getStatus());
				compliantVendorsRatingReport.setGstr1ArnNo(eachObj.getArnNo());
				compliantVendorsRatingReport
						.setGstr1FilingDate(eachObj.getFilingDate() != null
								? getFormattedDate(
										eachObj.getFilingDate().toString())
								: null);
				LocalDate dueDate = getDueDate(eachObj.getReturnType(),
						eachObj.getTaxPeriod(), entityId, gstin.substring(0, 2),
						dueDateMap, filingType.toUpperCase());

				Pair<BigDecimal, Boolean> rateStatPair = findDueTypeAndValidRating(
						eachObj.getReturnType(), eachObj.getTaxPeriod(),
						OverAllGstinStatus.COMPLIANT.getStatus(), dueDate,
						eachObj.getFilingDate(), ratingCriteria);
				if (!isChannelOneClient) {
					compliantVendorsRatingReport
							.setRating(rateStatPair.getValue0());
				}
			} else if ("GSTR3B".equalsIgnoreCase(eachObj.getReturnType())
					&& ("GSTR3B".equalsIgnoreCase(returnType)
							|| "GSTR1,GSTR3B".equalsIgnoreCase(returnType))) {
				compliantVendorsRatingReport.setReturnType(returnType);
				compliantVendorsRatingReport
						.setGstr3BStatusofReturnFiling(eachObj.getStatus());
				compliantVendorsRatingReport.setGstr3BArnNo(eachObj.getArnNo());
				compliantVendorsRatingReport
						.setGstr3BFilingDate(eachObj.getFilingDate() != null
								? getFormattedDate(
										eachObj.getFilingDate().toString())
								: null);
				LocalDate dueDate = getDueDate(eachObj.getReturnType(),
						eachObj.getTaxPeriod(), entityId, gstin.substring(0, 2),
						dueDateMap, filingType.toUpperCase());
				Pair<BigDecimal, Boolean> rateStatPair = findDueTypeAndValidRating(
						eachObj.getReturnType(), eachObj.getTaxPeriod(),
						OverAllGstinStatus.COMPLIANT.getStatus(), dueDate,
						eachObj.getFilingDate(), ratingCriteria);
				if (!isChannelOneClient) {
					compliantVendorsRatingReport
							.setRating(rateStatPair.getValue0());
				}
			}
			compliantList.add(compliantVendorsRatingReport);

		});

	}

	private String getFormattedDate(String date) {
		try {
			LocalDate ld = LocalDate.parse(date);
			return ld.format(formatter);
		} catch (Exception ee) {
			String msg = String
					.format("Exception occered while formatting date %s", date);
			LOGGER.error(msg, ee);
			return null;
		}
	}

	@Override
	public List<String> getListOfCustomerGstin(List<String> listOfcustPans) {
		try {
			List<String> gstins = new ArrayList<>();

			if (!listOfcustPans.isEmpty()) {
				List<List<String>> panChunks = Lists.partition(listOfcustPans,
						2000);
				for (List<String> chunk : panChunks) {
					gstins.addAll(docRepo.getDistinctCustomerGstin(chunk));
				}
			} else {

				List<String> custPans = docRepo.getDistinctCustomerPans();
				List<List<String>> panChunks = Lists.partition(custPans, 2000);

				for (List<String> chunk : panChunks) {
					gstins.addAll(docRepo.getDistinctCustomerGstin(chunk));
				}
			}

			return gstins;
		} catch (Exception ee) {
			String msg = "Exception occered while getting cutomer Gstin";
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	@Override
	public boolean getChannelOneClientInfo(Long entityId) {
		boolean isChannelOneClient = false;
		try {
			EntityConfigPrmtEntity entityConfig = entityConfigRepo
					.findByGroupCodeAndEntityIdAndparamkryIdAndIsDeleteFalse(
							TenantContext.getTenantId(), entityId, "G21");
			String paramValue = entityConfig != null
					? entityConfig.getParamValue()
					: "B";
			if ("A".equals(paramValue)) {
				isChannelOneClient = true;
			}
			return isChannelOneClient;
		} catch (Exception ee) {
			String msg = "Exception occered while getting cutomer Gstin";
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	private Pair<Set<String>, Set<String>> getLastThreeTaxperiodForGstin(
			Map<String, LocalDate> dueDateMap, List<String> gstinList,
			Long entityId) {
		Set<String> gstr1TaxPeriods = new HashSet<>();
		Set<String> gstr3BTaxPeriods = new HashSet<>();

		gstinList.forEach(e -> {
			gstr1TaxPeriods.addAll(getLastThreeTaxPeriods("GSTR1", entityId,
					e.substring(0, 2), dueDateMap));
			gstr3BTaxPeriods.addAll(getLastThreeTaxPeriods("GSTR3B", entityId,
					e.substring(0, 2), dueDateMap));
		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Last three taxperiods to calculate the counts are GSTR1 {} GSTR3B {} ",
					gstr1TaxPeriods, gstr3BTaxPeriods);
		}
		return new Pair<>(gstr1TaxPeriods, gstr3BTaxPeriods);
	}

	@Override
	public VendorComplianceCountDto getVendorCountDetails(
			List<GstinWiseComplianceRatingStatus> dtoList, String returnType,
			Long entityId, String source) {
		VendorComplianceCountDto countDto = new VendorComplianceCountDto();

		try {
			Integer vgstinCount = dtoList.size();
			Integer vgstinNonCompliantCount = 0;
			Integer vgstinUnkwCount = 0;
			Integer gstr1NonCompliantCount = 0;
			Integer gstr3BNonCompliantCount = 0;
			List<String> gstr1NonCompliantVgstins = new ArrayList<>();
			List<String> gstr3BNonCompliantVgstins = new ArrayList<>();
			Map<String, BigInteger> gstr1CountMap = new HashMap<>();
			Map<String, BigInteger> gstr3BCountMap = new HashMap<>();

			List<String> gstinList = dtoList.stream()
					.map(GstinWiseComplianceRatingStatus::getVendorGstin)
					.collect(Collectors.toList());
			List<String> returnTypeList = Arrays.asList(returnType.split(","));

			String taxPeriod = GenUtil.getCurrentTaxPeriod();
			List<VendorDueDateEntity> dueDates = dueDateRepo
					.findByReturnTypeInAndEntityIdAndTaxPeriodInAndIsDeleteFalse(
							returnTypeList, entityId, Arrays.asList(taxPeriod));

			Map<String, LocalDate> dueDateMap = dueDates.stream()
					.collect(Collectors.toMap(VendorDueDateEntity::getKey,
							VendorDueDateEntity::getDueDate));

			Pair<Set<String>, Set<String>> taxPeriodsPair = getLastThreeTaxperiodForGstin(
					dueDateMap, gstinList, entityId);

			List<List<String>> gstinChunk = Lists
					.partition(new ArrayList<String>(gstinList), 2000);
			if ("GSTR1,GSTR3B".equalsIgnoreCase(returnType)) {

				if (VendorRatingCriteriaDefaultUtil.MY_COMPLIANCE
						.equalsIgnoreCase(source)) {

					for (List<String> gstinListChunk : gstinChunk) {
						if (!taxPeriodsPair.getValue0().isEmpty()) {
							gstr1CountMap.putAll(returnStatusRepo
									.getFilingCountForGstin(gstinListChunk,
											new ArrayList<>(
													taxPeriodsPair.getValue0()),
											"GSTR1", false)
									.stream().collect(
											Collectors.toMap(a -> (String) a[0],
													a -> GenUtil.getBigInteger(a[1]))));
						}
						if (!taxPeriodsPair.getValue1().isEmpty()) {

							gstr3BCountMap.putAll(returnStatusRepo
									.getFilingCountForGstin(gstinListChunk,
											new ArrayList<>(
													taxPeriodsPair.getValue1()),
											"GSTR3B", false)
									.stream().collect(
											Collectors.toMap(a -> (String) a[0],
													a -> GenUtil.getBigInteger(a[1]))));
						}
					}
				} else {
					for (List<String> gstinListChunk : gstinChunk) {
						if (!taxPeriodsPair.getValue0().isEmpty()) {

							gstr1CountMap.putAll(returnStatusRepo
									.getFilingCountForGstin(gstinListChunk,
											new ArrayList<>(
													taxPeriodsPair.getValue0()),
											"GSTR1", true)
									.stream().collect(
											Collectors.toMap(a -> (String) a[0],
													a -> GenUtil.getBigInteger(a[1]))));
						}
						if (!taxPeriodsPair.getValue1().isEmpty()) {

							gstr3BCountMap.putAll(returnStatusRepo
									.getFilingCountForGstin(gstinListChunk,
											new ArrayList<>(
													taxPeriodsPair.getValue1()),
											"GSTR3B", true)
									.stream().collect(
											Collectors.toMap(a -> (String) a[0],
													a -> GenUtil.getBigInteger(a[1]))));
						}
					}
				}
			} else {

				if (VendorRatingCriteriaDefaultUtil.MY_COMPLIANCE
						.equalsIgnoreCase(source)) {

					if ("GSTR1".equalsIgnoreCase(returnType)) {
						if (!taxPeriodsPair.getValue0().isEmpty()) {

							for (List<String> gstinListChunk : gstinChunk) {

								gstr1CountMap.putAll(returnStatusRepo
										.getFilingCountForGstin(gstinListChunk,
												new ArrayList<>(taxPeriodsPair
														.getValue0()),
												returnType, false)
										.stream()
										.collect(Collectors.toMap(
												a -> (String) a[0],
												a -> GenUtil.getBigInteger(a[1]))));
							}
						}
					} else {
						if (!taxPeriodsPair.getValue1().isEmpty()) {

							for (List<String> gstinListChunk : gstinChunk) {

								gstr3BCountMap.putAll(returnStatusRepo
										.getFilingCountForGstin(gstinListChunk,
												new ArrayList<>(taxPeriodsPair
														.getValue1()),
												returnType, false)
										.stream()
										.collect(Collectors.toMap(
												a -> (String) a[0],
												a -> GenUtil.getBigInteger(a[1]))));
							}
						}
					}
				} else {

					if ("GSTR1".equalsIgnoreCase(returnType)) {
						if (!taxPeriodsPair.getValue0().isEmpty()) {

							for (List<String> gstinListChunk : gstinChunk) {

								gstr1CountMap.putAll(returnStatusRepo
										.getFilingCountForGstin(gstinListChunk,
												new ArrayList<>(taxPeriodsPair
														.getValue0()),
												returnType, true)
										.stream()
										.collect(Collectors.toMap(
												a -> (String) a[0],
												a -> GenUtil.getBigInteger(a[1]))));
							}
						}
					} else {
						if (!taxPeriodsPair.getValue1().isEmpty()) {

							for (List<String> gstinListChunk : gstinChunk) {
								gstr3BCountMap.putAll(returnStatusRepo
										.getFilingCountForGstin(gstinListChunk,
												new ArrayList<>(taxPeriodsPair
														.getValue1()),
												returnType, true)
										.stream()
										.collect(Collectors.toMap(
												a -> (String) a[0],
												a -> GenUtil.getBigInteger(a[1]))));
							}
						}
					}

				}

			}
			for (GstinWiseComplianceRatingStatus gstinDto : dtoList) {

				int gstr1Count = 0;
				int gstr3BCount = 0;

				if ("GSTR1,GSTR3B".equalsIgnoreCase(returnType)) {

					gstr1Count = gstr1CountMap.get(
							gstinDto.getVendorGstin()) != null ? gstr1CountMap
									.get(gstinDto.getVendorGstin()).intValue()
									: 0;
					gstr3BCount = gstr3BCountMap.get(
							gstinDto.getVendorGstin()) != null ? gstr3BCountMap
									.get(gstinDto.getVendorGstin()).intValue()
									: 0;

					if (gstr1Count == 0) {
						if (!gstinDto.isCancelled()) {
							gstr1NonCompliantCount++;
							gstr1NonCompliantVgstins
									.add(gstinDto.getVendorGstin());
						}

					}
					if (gstr3BCount == 0) {
						if (!gstinDto.isCancelled()) {
							gstr3BNonCompliantCount++;
							gstr3BNonCompliantVgstins
									.add(gstinDto.getVendorGstin());
						}
					}

				} else {

					if ("GSTR1".equalsIgnoreCase(returnType)) {
						gstr1Count = gstr1CountMap
								.get(gstinDto.getVendorGstin()) != null
										? gstr1CountMap
												.get(gstinDto.getVendorGstin())
												.intValue()
										: 0;
					} else {
						gstr1Count = gstr3BCountMap
								.get(gstinDto.getVendorGstin()) != null
										? gstr3BCountMap
												.get(gstinDto.getVendorGstin())
												.intValue()
										: 0;

					}

					if (gstr1Count == 0) {
						if ("GSTR1".equalsIgnoreCase(returnType)) {
							if (!gstinDto.isCancelled()) {
								gstr1NonCompliantCount++;
								gstr1NonCompliantVgstins
										.add(gstinDto.getVendorGstin());
							}
						} else if ("GSTR3B".equalsIgnoreCase(returnType)) {
							if (!gstinDto.isCancelled()) {
								gstr3BNonCompliantCount++;
								gstr3BNonCompliantVgstins
										.add(gstinDto.getVendorGstin());
							}
						}
					}
				}

				List<MonthWiseComplianceRatingStatus> monthDtoList = gstinDto
						.getMonthWiseStatusCombination();
				Set<String> gstr1StatusSet = monthDtoList.stream()
						.filter(obj -> obj.getGstr1Status() != null)
						.map(MonthWiseComplianceRatingStatus::getGstr1Status)
						.collect(Collectors.toSet());

				Set<String> gstr3bStatusSet = monthDtoList.stream()
						.filter(obj -> obj.getGstr3bStatus() != null)
						.map(MonthWiseComplianceRatingStatus::getGstr3bStatus)
						.collect(Collectors.toSet());

				Set<String> combinedStatusSet = Sets.union(gstr1StatusSet,
						gstr3bStatusSet);

				if (("GSTR1".equalsIgnoreCase(returnType) && gstr1StatusSet
						.contains(OverAllGstinStatus.NOT_COMPLIANT.getStatus()))
						|| ("GSTR3B".equalsIgnoreCase(returnType)
								&& gstr3bStatusSet.contains(
										OverAllGstinStatus.NOT_COMPLIANT
												.getStatus()))
						|| (("GSTR1,GSTR3B".equalsIgnoreCase(returnType))
								&& combinedStatusSet.contains(
										OverAllGstinStatus.NOT_COMPLIANT
												.getStatus()))) {

					vgstinNonCompliantCount++;

				}

				if (combinedStatusSet.size() == 1 && combinedStatusSet
						.contains(OverAllGstinStatus.UNKNOWN.getStatus())) {
					vgstinUnkwCount++;
				}
			}

			countDto.setTtlVendors(vgstinCount);
			countDto.setNCompliantVendors(vgstinNonCompliantCount);
			countDto.setCompliantVendors(
					vgstinCount - vgstinNonCompliantCount - vgstinUnkwCount);
			countDto.setGstr1NcomVendors(gstr1NonCompliantCount);
			countDto.setGstr3BNcomVendors(gstr3BNonCompliantCount);
			countDto.setGstr1NonCompliantVgstins(gstr1NonCompliantVgstins);
			countDto.setGstr3BNonCompliantVgstins(gstr3BNonCompliantVgstins);
			return countDto;
		} catch (Exception e) {
			LOGGER.error(
					"Error while calculating counts in vendor rating and setting counts to 0",
					e);
			countDto.setTtlVendors(0);
			countDto.setNCompliantVendors(0);
			countDto.setCompliantVendors(0);
			countDto.setGstr1NcomVendors(0);
			countDto.setGstr3BNcomVendors(0);
			countDto.setGstr1NonCompliantVgstins(null);
			countDto.setGstr3BNonCompliantVgstins(null);
			return countDto;

		}
	}

	private Map<String, VendorRegDateDto> convertRegDatesToMap(
			List<String> vendorGstins) {

		Map<String, VendorRegDateDto> dateMap = new HashMap<>();

		List<VendorMasterConfigEntity> entities = vendorMasterConfigRepo
				.findByVendorGstinInAndErrorCodeIsNullAndErrorDescriptionIsNullAndIsActiveTrue(
						vendorGstins);
		entities.forEach(e -> {
			VendorRegDateDto dto = new VendorRegDateDto();
			dto.setRegDate(e.getDateOfRegistration());
			dto.setCanDate(e.getDateOfCancellation());
			dateMap.put(e.getVendorGstin(), dto);

		});
		return dateMap;
	}
	
	private String getFilingFrequency(String eachTaxPeriod,String filingType){
		String quarter = "";
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
		for(int i=0;i<fileTypes.length;i++){
			if(fileTypes[i].contains(quarter)){
				if(fileTypes[i].contains("M"))
					return "Monthly";
				else
					return "Quarterly";
			}
				
		}
		return "";
	}
}
