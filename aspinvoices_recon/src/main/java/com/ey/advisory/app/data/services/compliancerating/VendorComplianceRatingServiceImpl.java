package com.ey.advisory.app.data.services.compliancerating;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorMasterApiEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorRatingCriteriaEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterApiEntityRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TypeOfGstFiling;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VendorComplianceRatingServiceImpl")
public class VendorComplianceRatingServiceImpl
		implements VendorComplianceRatingService {

	@Autowired
	@Qualifier("VendorComplianceRatingHelperServiceImpl")
	private VendorComplianceRatingHelperService ratingHelperService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

	@Autowired
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	@Qualifier("VendorMasterApiEntityRepository")
	private VendorMasterApiEntityRepository vendorMasterApiRepo;

	@Autowired
	private DocRepository docRepo;

	private static final List<String> QUARTERLY_MONTHS = ImmutableList.of("06",
			"09", "12", "03");

	private static final MonthDay dueDateLogic1 = MonthDay.of(11, 30);

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Override
	public List<GstinWiseComplianceRatingStatus> getVendorComplianceRatingData(
			VendorComplianceRatingRequestDto requestDto) {
		try {
			/**
			 * This method gives all the db data that is required to do the
			 * operations
			 */
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Started for getting getAllRequiredData details at {}",
						LocalDateTime.now());
			}

			VendorRatingOverAllDbDataDto allDatadto = ratingHelperService
					.getAllRequiredData(requestDto);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("End for getting getAllRequiredData details at {}",
						LocalDateTime.now());
			}

			List<VendorMasterUploadEntity> uploadEntities = allDatadto
					.getUploadEntities();

			List<VendorMasterApiEntity> uploadApiEntities = allDatadto
					.getUploadApiEntities();

			LocalDate currDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");
			String currentMonthAndYr = currDate.format(formatter);
			int month = getValidMonthFromTaxPeriod(
					currentMonthAndYr.substring(0, 2));
			int day = currDate.getDayOfMonth();
			int year = Integer.parseInt(currentMonthAndYr.substring(2));

			List<GstinWiseComplianceRatingStatus> gstinWiseRatingstatus = new ArrayList<>();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Start time for getGstinWiseRatingStatusDtos rating is {}",
						LocalDateTime.now());
			}
			/**
			 * creating overall dtos and setting all values
			 */
			ratingHelperService.getGstinWiseRatingStatusDtos(
					gstinWiseRatingstatus, allDatadto,
					requestDto.getReturnType(),
					requestDto.getEntityId().toString(), month, day, year,
					requestDto.getSource());

			/**
			 * setting vendorName for all records
			 */
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"End for getting getGstinWiseRatingStatusDtos details at {}",
						LocalDateTime.now());
			}
			Map<String, GstinWiseComplianceRatingStatus> gstinWiseRatingstatusPrevFyMap = new HashMap<>();
			List<GstinWiseComplianceRatingStatus> gstinWiseRatingstatusPrevFy = new ArrayList<>();
			if (VendorRatingCriteriaDefaultUtil.VENDOR
					.equalsIgnoreCase(requestDto.getSource())) {

				VendorComplianceRatingRequestDto prevFyDto = new VendorComplianceRatingRequestDto();
				BeanUtils.copyProperties(requestDto, prevFyDto);

				// Calculate previous FY
				String currentFy = requestDto.getFy(); // e.g., "2025-26"
				if (currentFy != null && currentFy.matches("\\d{4}-\\d{2}")) {
					int startYear = Integer.parseInt(currentFy.substring(0, 4))
							- 1;
					int endYear = Integer.parseInt(currentFy.substring(5, 7))
							- 1;
					if (endYear < 0)
						endYear += 100; // handle "00" case
					String prevFy = String.format("%d-%02d", startYear,
							endYear);
					prevFyDto.setFy(prevFy);
				}

				VendorRatingOverAllDbDataDto allDatadtoPrevFy = ratingHelperService
						.getAllRequiredData(prevFyDto);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Start time for getGstinWiseRatingStatusDtos rating is {}",
							LocalDateTime.now());
				}
				/**
				 * creating overall dtos and setting all values
				 */
				ratingHelperService.getGstinWiseRatingStatusDtos(
						gstinWiseRatingstatusPrevFy, allDatadtoPrevFy,
						requestDto.getReturnType(),
						requestDto.getEntityId().toString(), month, day, year,
						requestDto.getSource());

				gstinWiseRatingstatusPrevFyMap = gstinWiseRatingstatusPrevFy
						.stream()
						.collect(Collectors.toMap(
								GstinWiseComplianceRatingStatus::getVendorGstin,
								obj -> obj, (existing, replacement) -> existing // handle
																				// duplicate
																				// keys
				));
				for (GstrReturnStatusEntity returnEntity : allDatadtoPrevFy.getReturnStatusEntities()) {
					LOGGER.debug("returnStatusEntity Prev Fy : {}", returnEntity);
				}
				gstinWiseRatingstatus.forEach(eachObj -> {
					Optional<VendorMasterUploadEntity> masterUploadEntity = uploadEntities
							.stream()
							.filter(eachEntity -> eachEntity.getVendorGstin()
									.equals(eachObj.getVendorGstin()))
							.findFirst();
					if (masterUploadEntity.isPresent()) {
						eachObj.setVendorName(
								masterUploadEntity.get().getVendorName());
					} else {
						Optional<VendorMasterApiEntity> masterApiEntity = uploadApiEntities
								.stream()
								.filter(eachEntity -> eachEntity
										.getVendorGstin()
										.equals(eachObj.getVendorGstin()))
								.findFirst();
						if (masterApiEntity.isPresent()) {
							eachObj.setVendorName(
									masterApiEntity.get().getVendorName());
						}
					}
				});
			} else if (VendorRatingCriteriaDefaultUtil.CUSTOMER
					.equalsIgnoreCase(requestDto.getSource())) {

				List<String> vendorGstinsList = gstinWiseRatingstatus.stream()
						.map(GstinWiseComplianceRatingStatus::getVendorGstin)
						.collect(Collectors.toList());
				Map<String, String> nameMap = getCustomerNameMap(
						vendorGstinsList);

				gstinWiseRatingstatus.forEach(eachObj -> {
					eachObj.setVendorName(
							nameMap.get(eachObj.getVendorGstin()));
				});

			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Start time for handleNonQuaterlyMonthsAndCalculateAvgRating is {}",
						LocalDateTime.now());
			}
			/**
			 * This method is to calculate average ranking and if a gstin is of
			 * quarterlyType then mark all nonQuarterly months as Status unknown
			 * and rate as 0
			 */
			handleNonQuaterlyMonthsAndCalculateAvgRating(gstinWiseRatingstatus,
					gstinWiseRatingstatusPrevFyMap, requestDto.getReturnType(),
					requestDto.getFy());
			/**
			 * This method is to sort the data based on ViewType. If viewType is
			 * top 10/20 compliant the the sorting will be in descending order
			 * on averageRanking column If viewType is top 10/20 not-compliant
			 * the the sorting will be in ascending order on averageRanking
			 * column
			 */
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"End for handleNonQuaterlyMonthsAndCalculateAvgRating at {}",
						LocalDateTime.now());
			}
			return getRatingStatusBasedOnViewType(gstinWiseRatingstatus,
					requestDto.getViewType());
		} catch (Exception ee) {
			String msg = "Exception Occured while calculating vendor compliance"
					+ " rating";
			LOGGER.error(msg, ee);
			throw new AppException(ee.getMessage());
		}
	}

	private void handleNonQuaterlyMonthsAndCalculateAvgRating(
			List<GstinWiseComplianceRatingStatus> gstinWiseRatingstatus,
			Map<String, GstinWiseComplianceRatingStatus> gstinWiseRatingstatusPrevFyMap,
			String returnType, String fy) {
		gstinWiseRatingstatus.stream().forEach((obj) -> {
			BigDecimal gstr1Rating = BigDecimal.ZERO;
			BigDecimal gstr3bRating = BigDecimal.ZERO;
			BigDecimal gstr1RatingWithPrevFy = BigDecimal.ZERO;
			BigDecimal gstr3bRatingWithPrevFy = BigDecimal.ZERO;

			int countGstr1 = 0;
			int countGstr3B = 0;
			int countGstr1WithPrevFy = 0;
			int countGstr3BWithPrevFy = 0;

			List<MonthWiseComplianceRatingStatus> monthlyStatus = obj
					.getMonthWiseStatusCombination();

			for (MonthWiseComplianceRatingStatus period : monthlyStatus) {
				LOGGER.debug("period: {}", period);
				String quarteredFilingType = getFilingFrequency(
						period.getMonth(), obj.getFilingType());
				if (quarteredFilingType.equalsIgnoreCase("Quarterly")) {
					if ("GSTR1".equalsIgnoreCase(returnType)) {
						if (period.getGstr1Rating() != BigDecimal.ZERO) {
							gstr1Rating = gstr1Rating
									.add(period.getGstr1Rating()
											.multiply(BigDecimal.valueOf(3)));
							countGstr1 = countGstr1 + 3;
						}

					} else if ("GSTR3B".equalsIgnoreCase(returnType)) {
						if (period.getGstr3BRating() != BigDecimal.ZERO) {
							gstr3bRating = gstr3bRating
									.add(period.getGstr3BRating()
											.multiply(BigDecimal.valueOf(3)));
							countGstr3B = countGstr3B + 3;
						}

					} else if ("GSTR1,GSTR3B".equalsIgnoreCase(returnType)) {
						if (period.getGstr1Rating() != BigDecimal.ZERO) {
							gstr1Rating = gstr1Rating
									.add(period.getGstr1Rating()
											.multiply(BigDecimal.valueOf(3)));
							countGstr1 = countGstr1 + 3;
						}
						if (period.getGstr3BRating() != BigDecimal.ZERO) {
							gstr3bRating = gstr3bRating
									.add(period.getGstr3BRating()
											.multiply(BigDecimal.valueOf(3)));
							countGstr3B = countGstr3B + 3;
						}
					}
				} else if (quarteredFilingType.equalsIgnoreCase("Monthly")) {
					if ("GSTR1".equalsIgnoreCase(returnType)) {
						if (period.getGstr1Rating() != BigDecimal.ZERO) {
							gstr1Rating = gstr1Rating
									.add(period.getGstr1Rating());
							countGstr1++;
						}

					} else if ("GSTR3B".equalsIgnoreCase(returnType)) {
						if (period.getGstr3BRating() != BigDecimal.ZERO) {
							gstr3bRating = gstr3bRating
									.add(period.getGstr3BRating());
							countGstr3B++;
						}

					} else if ("GSTR1,GSTR3B".equalsIgnoreCase(returnType)) {
						if (period.getGstr1Rating() != BigDecimal.ZERO) {
							gstr1Rating = gstr1Rating
									.add(period.getGstr1Rating());
							countGstr1++;
						}
						if (period.getGstr3BRating() != BigDecimal.ZERO) {
							gstr3bRating = gstr3bRating
									.add(period.getGstr3BRating());
							countGstr3B++;
						}
					}
				}
			}

			gstr1RatingWithPrevFy.add(gstr1Rating);
			gstr3bRatingWithPrevFy.add(gstr3bRating);
			countGstr1WithPrevFy = countGstr1WithPrevFy + countGstr1;
			countGstr3BWithPrevFy = countGstr3BWithPrevFy + countGstr3B;

			GstinWiseComplianceRatingStatus objPrevFy = gstinWiseRatingstatusPrevFyMap
					.get(obj.getVendorGstin());
			List<MonthWiseComplianceRatingStatus> monthlyStatusPrevFy = objPrevFy
					.getMonthWiseStatusCombination();
			for (MonthWiseComplianceRatingStatus period : monthlyStatusPrevFy) {
				String quarteredFilingType = getFilingFrequency(
						period.getMonth(), objPrevFy.getFilingType());
				if (quarteredFilingType.equalsIgnoreCase("Quarterly")) {
					if ("GSTR1".equalsIgnoreCase(returnType)) {
						if (period.getGstr1Rating() != BigDecimal.ZERO) {
							gstr1RatingWithPrevFy = gstr1RatingWithPrevFy
									.add(period.getGstr1Rating()
											.multiply(BigDecimal.valueOf(3)));
							countGstr1WithPrevFy = countGstr1WithPrevFy + 3;
						}

					} else if ("GSTR3B".equalsIgnoreCase(returnType)) {
						if (period.getGstr3BRating() != BigDecimal.ZERO) {
							gstr3bRatingWithPrevFy = gstr3bRatingWithPrevFy
									.add(period.getGstr3BRating()
											.multiply(BigDecimal.valueOf(3)));
							countGstr3BWithPrevFy = countGstr3BWithPrevFy + 3;
						}

					} else if ("GSTR1,GSTR3B".equalsIgnoreCase(returnType)) {
						if (period.getGstr1Rating() != BigDecimal.ZERO) {
							gstr1RatingWithPrevFy = gstr1RatingWithPrevFy
									.add(period.getGstr1Rating()
											.multiply(BigDecimal.valueOf(3)));
							countGstr1WithPrevFy = countGstr1WithPrevFy + 3;
						}
						if (period.getGstr3BRating() != BigDecimal.ZERO) {
							gstr3bRatingWithPrevFy = gstr3bRatingWithPrevFy
									.add(period.getGstr3BRating()
											.multiply(BigDecimal.valueOf(3)));
							countGstr3BWithPrevFy = countGstr3BWithPrevFy + 3;
						}
					}
				} else if (quarteredFilingType.equalsIgnoreCase("Monthly")) {
					if ("GSTR1".equalsIgnoreCase(returnType)) {
						if (period.getGstr1Rating() != BigDecimal.ZERO) {
							gstr1RatingWithPrevFy = gstr1RatingWithPrevFy
									.add(period.getGstr1Rating());
							countGstr1WithPrevFy++;
						}

					} else if ("GSTR3B".equalsIgnoreCase(returnType)) {
						if (period.getGstr3BRating() != BigDecimal.ZERO) {
							gstr3bRatingWithPrevFy = gstr3bRatingWithPrevFy
									.add(period.getGstr3BRating());
							countGstr3BWithPrevFy++;
						}

					} else if ("GSTR1,GSTR3B".equalsIgnoreCase(returnType)) {
						if (period.getGstr1Rating() != BigDecimal.ZERO) {
							gstr1RatingWithPrevFy = gstr1RatingWithPrevFy
									.add(period.getGstr1Rating());
							countGstr1WithPrevFy++;
						}
						if (period.getGstr3BRating() != BigDecimal.ZERO) {
							gstr3bRatingWithPrevFy = gstr3bRatingWithPrevFy
									.add(period.getGstr3BRating());
							countGstr3BWithPrevFy++;
						}
					}
				}
			}

			obj.setMonthWiseStatusCombination(monthlyStatus);
			BigDecimal avgRating = BigDecimal.ZERO;
			BigDecimal avgRatingWithPrevFy = BigDecimal.ZERO;

			if ("GSTR1".equalsIgnoreCase(returnType)) {
				if (countGstr1 != 0) {
					avgRating = gstr1Rating.divide(
							BigDecimal.valueOf(countGstr1), 2,
							RoundingMode.HALF_UP);
				}
				if (countGstr1WithPrevFy != 0) {
					avgRatingWithPrevFy = gstr1RatingWithPrevFy.divide(
							BigDecimal.valueOf(countGstr1WithPrevFy), 2,
							RoundingMode.HALF_UP);
				}
			} else if ("GSTR3B".equalsIgnoreCase(returnType)) {

				if (countGstr3B != 0) {
					avgRating = gstr3bRating.divide(
							BigDecimal.valueOf(countGstr3B), 2,
							RoundingMode.HALF_UP);
				}
				if (countGstr3BWithPrevFy != 0) {
					avgRatingWithPrevFy = gstr3bRatingWithPrevFy.divide(
							BigDecimal.valueOf(countGstr3BWithPrevFy), 2,
							RoundingMode.HALF_UP);
				}
			} else if ("GSTR1,GSTR3B".equalsIgnoreCase(returnType)) {
				BigDecimal avgRatingGstr1 = BigDecimal.ZERO;
				BigDecimal avgRatingGstr3b = BigDecimal.ZERO;
				BigDecimal avgRatingGstr1WithPrevFy = BigDecimal.ZERO;
				BigDecimal avgRatingGstr3bWithPrevFy = BigDecimal.ZERO;

				if (countGstr1 != 0) {
					avgRatingGstr1 = gstr1Rating.divide(
							BigDecimal.valueOf(countGstr1),
							MathContext.DECIMAL64);
				}

				if (countGstr3B != 0) {
					avgRatingGstr3b = gstr3bRating.divide(
							BigDecimal.valueOf(countGstr3B),
							MathContext.DECIMAL64);
				}

				if (countGstr1WithPrevFy != 0) {
					avgRatingGstr1WithPrevFy = gstr1RatingWithPrevFy.divide(
							BigDecimal.valueOf(countGstr1WithPrevFy),
							MathContext.DECIMAL64);
				}

				if (countGstr3BWithPrevFy != 0) {
					avgRatingGstr3bWithPrevFy = gstr3bRatingWithPrevFy.divide(
							BigDecimal.valueOf(countGstr3BWithPrevFy),
							MathContext.DECIMAL64);
				}

				avgRating = avgRatingGstr1.add(avgRatingGstr3b).setScale(2,
						RoundingMode.HALF_UP);

				avgRatingWithPrevFy = avgRatingGstr1WithPrevFy
						.add(avgRatingGstr3bWithPrevFy)
						.setScale(2, RoundingMode.HALF_UP);
			}

			obj.setAverageRating(avgRating);

			Integer year = Integer.parseInt(fy.substring(0, 4));
			LocalDate dueDateForLogic1 = dueDateLogic1.atYear(year);
			LocalDate currentDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDate.now());
			String currentFyYear = GenUtil.getCurrentFinancialYear();
			if (currentDate.isAfter(dueDateForLogic1)
					|| (!fy.equalsIgnoreCase(currentFyYear))) {
				obj.setAverageRatingWithPrevFy(avgRating);
			} else {
				obj.setAverageRatingWithPrevFy(avgRatingWithPrevFy);
			}

		});
	}

	private int getValidMonthFromTaxPeriod(String taxPeriod) {
		String month = taxPeriod.substring(0, 2);
		return Integer.parseInt(month);
	}

	@Override
	public Workbook getComplianceRatingTableReport(
			VendorComplianceRatingRequestDto requestDto,String fileName) {

		try {
			/**
			 * This method gives all the db data that is required to do the
			 * operations
			 */
			VendorRatingOverAllDbDataDto allDatadto = ratingHelperService
					.getAllRequiredData(requestDto);

			List<VendorMasterUploadEntity> uploadEntities = allDatadto
					.getUploadEntities();

			List<VendorMasterApiEntity> uploadApiEntities = allDatadto
					.getUploadApiEntities();
			LocalDate currDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");
			String currentMonthAndYr = currDate.format(formatter);
			int month = getValidMonthFromTaxPeriod(
					currentMonthAndYr.substring(0, 2));
			int day = currDate.getDayOfMonth();
			int year = Integer.parseInt(currentMonthAndYr.substring(2));

			List<VendorComplianceRatingReportDto> defaultList = new ArrayList<>();

			/**
			 * creating default report dtos and default status as Not filed and
			 * Unknown( for future)
			 */
			ratingHelperService.getDefaultReportDtos(defaultList, allDatadto,
					requestDto.getReturnType(),
					requestDto.getEntityId().toString(), month, day, year,
					requestDto.getSource());

			List<VendorComplianceRatingReportDto> compliantList = new ArrayList<>();

			/**
			 * creating report dtos for filed records
			 */
			ratingHelperService.getAllCompliantReportDtos(compliantList,
					allDatadto, requestDto.getReturnType(),
					requestDto.getEntityId().toString(),
					requestDto.getSource());

			/**
			 * For GSTR1 and GSTR3B report, we will get 2 filed records for a
			 * gstin and taxPeriod , we need to merge it in one row to show in
			 * excel
			 */
			boolean isChannelOneClient = ratingHelperService
					.getChannelOneClientInfo(requestDto.getEntityId());
			if ("GSTR1,GSTR3B".equalsIgnoreCase(requestDto.getReturnType())) {
				mergeDefaultRecordsAndCompliantRecords(compliantList,
						allDatadto.getRatingCriteria(), isChannelOneClient);
			}
			/**
			 * Copy all filed records and replace with the default records for a
			 * gstin and taxPeriod
			 */
			defaultList.forEach(eachObj -> {
				Optional<VendorComplianceRatingReportDto> eachEntry = compliantList
						.stream().filter(filterCondition(eachObj)).findFirst();
				if (eachEntry.isPresent()) {
					BeanUtils.copyProperties(eachEntry.get(), eachObj);
				}
			});

			/**
			 * setting vendorName for all records
			 */
			if (VendorRatingCriteriaDefaultUtil.VENDOR
					.equalsIgnoreCase(requestDto.getSource())) {
				defaultList.forEach(eachObj -> {
					Optional<VendorMasterUploadEntity> masterUploadEntity = uploadEntities
							.stream()
							.filter(eachEntity -> eachEntity.getVendorGstin()
									.equals(eachObj.getVendorGSTIN()))
							.findFirst();
					if (masterUploadEntity.isPresent()) {
						eachObj.setVendorName(
								masterUploadEntity.get().getVendorName());
					}
				});
			} else if (VendorRatingCriteriaDefaultUtil.CUSTOMER
					.equalsIgnoreCase(requestDto.getSource())) {

				List<String> vendorGstinsList = defaultList.stream()
						.map(VendorComplianceRatingReportDto::getVendorGSTIN)
						.distinct().collect(Collectors.toList());
				Map<String, String> nameMap = getCustomerNameMap(
						vendorGstinsList);

				defaultList.forEach(eachObj -> {
					eachObj.setVendorName(
							nameMap.get(eachObj.getVendorGSTIN()));
				});

			}
			/**
			 * This method will remove all nonQuarterly month records for
			 * Quarterly filing type gstin
			 */

			defaultList.forEach(eachObj -> {
				Optional<VendorMasterApiEntity> masterApiEntity = uploadApiEntities
						.stream()
						.filter(eachEntity -> eachEntity.getVendorGstin()
								.equals(eachObj.getVendorGSTIN()))
						.findFirst();
				if (masterApiEntity.isPresent()) {
					eachObj.setVendorName(
							masterApiEntity.get().getVendorName());
				}
			});

			List<VendorComplianceRatingReportDto> reportDtos = removeExtraMonthsForQuarterly(
					defaultList);
			return writeToExcel(reportDtos, requestDto.getSource(),fileName);
		} catch (Exception ee) {
			String msg = "Error while generating complaince rating table report";
			LOGGER.error(msg, ee);
			throw new AppException(ee.getMessage());
		}
	}

	private Workbook writeToExcel(
			List<VendorComplianceRatingReportDto> reportDtos, String source,
			String fileName) {

		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (reportDtos != null && !reportDtos.isEmpty()) {

			String template = null;
			String[] invoiceHeaders = null;

			if ("vendor".equalsIgnoreCase(source)) {
				invoiceHeaders = commonUtility
						.getProp("vendor.master.compliance.rating.data")
						.split(",");
				template = "VendorRatingTableReport.xlsx";
			} else if ("customer".equalsIgnoreCase(source)) {
				invoiceHeaders = commonUtility
						.getProp("vendor.master.compliance.rating.data")
						.split(",");
				template = "CustomerRatingTableReport.xlsx";
			} else {
				invoiceHeaders = commonUtility
						.getProp("my.compliance.rating.data").split(",");
				template = "MyComplianceRatingTableReport.xlsx";

			}
			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", template);
			if (LOGGER.isDebugEnabled()) {
				String msg = "VendorComplianceRatingServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			reportCells.importCustomObjects(reportDtos, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn, reportDtos.size(),
					true, "yyyy-mm-dd", false);
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "VendorComplianceRatingServiceImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(fileName, SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}
			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "saving excel sheet into folder, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}

		} else {
			throw new AppException("No records found, cannot generate report");
		}
		return workbook;
	}

	private List<VendorComplianceRatingReportDto> removeExtraMonthsForQuarterly(
			List<VendorComplianceRatingReportDto> nonVendorReportDtos) {
		Iterator<VendorComplianceRatingReportDto> it = nonVendorReportDtos
				.iterator();
		while (it.hasNext()) {
			VendorComplianceRatingReportDto eachEntry = it.next();
			if (TypeOfGstFiling.QUARTERLY.toString()
					.equalsIgnoreCase(eachEntry.getQuarterlyorMonthlyfiler())
					&& !QUARTERLY_MONTHS.contains(
							eachEntry.getTaxPeriod().substring(0, 2))) {
				it.remove();
			}
		}
		return nonVendorReportDtos;
	}

	private Predicate<VendorComplianceRatingReportDto> filterCondition(
			VendorComplianceRatingReportDto obj1) {
		return n -> n.getVendorGSTIN().equals(obj1.getVendorGSTIN())
				&& n.getTaxPeriod().equals(obj1.getTaxPeriod())
				&& n.getReturnType().equals(obj1.getReturnType());
	}

	private void mergeDefaultRecordsAndCompliantRecords(
			List<VendorComplianceRatingReportDto> compliantList,
			List<VendorRatingCriteriaEntity> ratingCriteria,
			boolean isChannelOne) {

		Map<String, BigDecimal> notFiledCriteria = ratingCriteria.stream()
				.filter(x -> x.getDueType().contains("NOT"))
				.collect(Collectors.toMap(
						VendorRatingCriteriaEntity::getReturnType,
						VendorRatingCriteriaEntity::getRating));

		Map<String, List<VendorComplianceRatingReportDto>> groupedRecords = compliantList
				.stream().collect(Collectors.groupingBy(
						v -> v.getVendorGSTIN().concat(v.getTaxPeriod())));
		compliantList = null;
		List<VendorComplianceRatingReportDto> mergedObjs = new ArrayList<>();
		for (Entry<String, List<VendorComplianceRatingReportDto>> pair : groupedRecords
				.entrySet()) {
			List<VendorComplianceRatingReportDto> list = pair.getValue();
			BigDecimal avgRating;
			if (isChannelOne) {
				avgRating = null;
			} else {
				avgRating = BigDecimal.ZERO;
			}
			if (list.size() == 2) {
				VendorComplianceRatingReportDto obj1 = list.get(0);
				VendorComplianceRatingReportDto obj2 = list.get(1);
				if (!isChannelOne) {
					avgRating = obj1.getRating().add(obj2.getRating());
				}
				if (obj1.getGstr1FilingDate() != null) {
					obj1.setGstr3BFilingDate(obj2.getGstr3BFilingDate());
					obj1.setGstr3BArnNo(obj2.getGstr3BArnNo());
					obj1.setGstr3BStatusofReturnFiling(
							obj2.getGstr3BStatusofReturnFiling());
				} else {
					obj1.setGstr1FilingDate(obj2.getGstr1FilingDate());
					obj1.setGstr1ArnNo(obj2.getGstr1ArnNo());
					obj1.setGstr1StatusofReturnFiling(
							obj2.getGstr1StatusofReturnFiling());
				}
				obj1.setRating(avgRating);
				mergedObjs.add(obj1);
			} else if (list.size() == 1) {
				VendorComplianceRatingReportDto obj1 = list.get(0);

				if (obj1.getGstr1FilingDate() != null) {
					obj1.setGstr3BStatusofReturnFiling(
							OverAllGstinStatus.NOT_COMPLIANT.getStatus());
					if (!isChannelOne) {
						obj1.setRating(obj1.getRating()
								.add(notFiledCriteria.containsKey("GSTR3B")
										? notFiledCriteria.get("GSTR3B")
										: BigDecimal.ZERO));
					}
				} else {
					obj1.setGstr1StatusofReturnFiling(
							OverAllGstinStatus.NOT_COMPLIANT.getStatus());
					if (!isChannelOne) {
						obj1.setRating(obj1.getRating()
								.add(notFiledCriteria.containsKey("GSTR1")
										? notFiledCriteria.get("GSTR1")
										: BigDecimal.ZERO));
					}
				}
				mergedObjs.add(obj1);
			}
		}
		compliantList = mergedObjs;
	}

	private List<GstinWiseComplianceRatingStatus> getRatingStatusBasedOnViewType(
			List<GstinWiseComplianceRatingStatus> gstinWiseRatingstatus,
			String viewType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Start time for getRatingStatusBasedOnViewType rating is {}",
					LocalDateTime.now());
		}
		try {
			if (gstinWiseRatingstatus.isEmpty()) {
				throw new AppException(
						"Could not generate vendor rating status records");
			}
			List<GstinWiseComplianceRatingStatus> dtoList = new ArrayList<>();
			if (viewType.equalsIgnoreCase("All")
					|| viewType.equalsIgnoreCase("Top 10 Compliant")
					|| viewType.equalsIgnoreCase("Top 20 Compliant")) {

				if (viewType.equalsIgnoreCase("All"))
					return gstinWiseRatingstatus;

				if (viewType.equalsIgnoreCase("Top 10 Compliant")
						|| viewType.equalsIgnoreCase("Top 20 Compliant")) {

					int limit = viewType.equalsIgnoreCase("Top 10 Compliant")
							? 10 : 20;
					Map<BigDecimal, List<GstinWiseComplianceRatingStatus>> complianceRatingTop10Map = gstinWiseRatingstatus
							.stream()
							.collect(Collectors.groupingBy(
									GstinWiseComplianceRatingStatus::getAverageRating,
									() -> new TreeMap<>(
											Collections.reverseOrder()),
									Collectors.toList()));

					complianceRatingTop10Map.entrySet().stream().limit(limit)
							.forEach(e -> dtoList.addAll(e.getValue()));

					return dtoList;
				}
			} else if (viewType.equalsIgnoreCase("Top 10 Non-Compliant")
					|| viewType.equalsIgnoreCase("Top 20 Non-Compliant")) {

				int limit = viewType.equalsIgnoreCase("Top 10 Non-Compliant")
						? 10 : 20;

				Map<BigDecimal, List<GstinWiseComplianceRatingStatus>> nonComplianceRatingTop10Map = gstinWiseRatingstatus
						.stream()
						.collect(Collectors.groupingBy(
								GstinWiseComplianceRatingStatus::getAverageRating,
								TreeMap::new, Collectors.toList()));

				nonComplianceRatingTop10Map.entrySet().stream().limit(limit)
						.forEach(e -> dtoList.addAll(e.getValue()));

				return dtoList;
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("End for getting count details at {}",
						LocalDateTime.now());
			}
			return gstinWiseRatingstatus;
		} catch (Exception ee) {
			String msg = "Error occured while fetching top values based on"
					+ " viewType";
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}
	}

	@Override
	public Long createVendorComplianceRatingAsyncReport(
			VendorComplianceRatingRequestDto requestDto, String requestObject) {
		try {

			Long entityId = requestDto.getEntityId();
			// String returnType = requestDto.getReturnType();
			String fy = requestDto.getFy();
			List<String> vendorPans = requestDto.getVendorPans();
			List<String> vendorGstins = requestDto.getVendorGstins();
			String viewType = requestDto.getViewType();
			String source = requestDto.getSource();

			String reportType = null;

			List<String> uploadedGstins = new ArrayList<>();
			if (VendorRatingCriteriaDefaultUtil.VENDOR
					.equalsIgnoreCase(source)) {
				List<String> recipientPanList = entityInfoRepository
						.findPanByEntityId(entityId);

				if ("Vendor Compliance Rating"
						.equalsIgnoreCase(requestDto.getReportType())) {
					reportType = "Vendor Compliance Rating";
				} else {
					reportType = "Vendor Compliance Summary";
				}
				List<VendorMasterApiEntity> uploadEntities = new ArrayList<>();
				if (vendorGstins.isEmpty() && vendorPans.isEmpty()) {
					uploadedGstins = vendorMasterUploadEntityRepository
							.getAllActiveVendorGstinByRecipientPan(
									recipientPanList);

					uploadedGstins.addAll(vendorMasterApiRepo
							.getAllActiveVendorGstinByRecipientPan(
									recipientPanList));

				}

				else if (vendorGstins.isEmpty() && !vendorPans.isEmpty()) {
					List<List<String>> chunks = Lists.partition(vendorPans,
							2000);
					for (List<String> chunk : chunks) {
						uploadedGstins.addAll(vendorMasterUploadEntityRepository
								.getAllActiveVendorGstinByVendorPans(chunk,
										recipientPanList));

						uploadedGstins.addAll(vendorMasterApiRepo
								.getAllActiveVendorGstinByVendorPans(chunk,
										recipientPanList));
					}
				} else {
					uploadedGstins = vendorGstins;
				}

				if (CollectionUtils.isEmpty(uploadedGstins)) {
					String msg = String.format(
							"There are no vendor Gstins in vendor master for entity: %s",
							entityId);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			} else if (VendorRatingCriteriaDefaultUtil.CUSTOMER
					.equalsIgnoreCase(source)) {

				if ("Vendor Compliance Rating"
						.equalsIgnoreCase(requestDto.getReportType())) {
					reportType = "Customer Compliance Rating";
				} else {
					reportType = "Customer Compliance Summary";
				}

				if (vendorGstins.isEmpty()) {
					uploadedGstins = ratingHelperService
							.getListOfCustomerGstin(vendorPans);
				} else {
					uploadedGstins = vendorGstins;
				}
			} else {

				if ("Vendor Compliance Rating"
						.equalsIgnoreCase(requestDto.getReportType())) {
					reportType = "My Compliance Rating";
				} else {
					reportType = "My Compliance Summary";
				}
				if (vendorGstins.isEmpty()) {
					uploadedGstins = gstNDetailRepository
							.findgstinByEntityIdWithRegTypeForGstr1(entityId);
				} else {
					uploadedGstins = vendorGstins;
				}
			}
			uploadedGstins = new ArrayList<>(new HashSet<>(uploadedGstins));
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();

			FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();

			// entity.setReqPayload(requestObject.toString());
			entity.setCreatedBy(userName);
			entity.setCreatedDate(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			entity.setReportStatus(ReportStatusConstants.INITIATED);
			entity.setReportCateg("GSTR1,GSTR3B");
			entity.setDataType(viewType);
			entity.setReportType(reportType);
			entity.setFyYear(fy);
			entity.setEntityId(entityId);
			entity.setTableType(
					VendorRatingCriteriaDefaultUtil.SOURCEMAP.get(source));
			String gstinString = String.join(",", uploadedGstins);
			entity.setGstins(GenUtil.convertStringToClob(gstinString));
			// entity.setGstins(gstinString);
			entity = fileStatusDownloadReportRepo.save(entity);

			Long id = entity.getId();

			String groupCode = TenantContext.getTenantId();

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("id", id);
			jobParams.addProperty("source", source);
			jobParams.addProperty("entityId", entityId);
			if ("Vendor Compliance Rating".equalsIgnoreCase(reportType)
					|| "Customer Compliance Rating".equalsIgnoreCase(reportType)
					|| "My Compliance Rating".equalsIgnoreCase(reportType)) {
				asyncJobsService.createJob(groupCode,
						JobConstants.VENDOR_COMPLIANCE_RATING_REPORT,
						jobParams.toString(), userName, 1L, null, null);
			} else if ("Vendor Compliance Summary".equalsIgnoreCase(reportType)
					|| "Customer Compliance Summary"
							.equalsIgnoreCase(reportType)
					|| "My Compliance Summary".equalsIgnoreCase(reportType)) {
				asyncJobsService.createJob(groupCode,
						JobConstants.VENDOR_COMPLIANCE_SUMMARY_REPORT,
						jobParams.toString(), userName, 1L, null, null);
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Successfully created a job for %s" + "with id: %s",
						reportType, id);
				LOGGER.debug(msg);
			}
			return id;
		} catch (Exception ee) {
			String msg = "Error while creating complaince rating async job";
			LOGGER.error(msg, ee);
			throw new AppException(ee.getMessage());
		}
	}

	private Map<String, String> getCustomerNameMap(List<String> vGstins) {

		List<Object[]> resultSet = new ArrayList<>();
		Map<String, String> nameMap = new HashMap<>();

		try {
			List<List<String>> gstinChunks = Lists.partition(vGstins, 2000);
			for (List<String> chunk : gstinChunks) {
				resultSet.addAll(docRepo.findCustomerNameByGstin(chunk));
			}
			resultSet.forEach(o -> nameMap.put((String) o[0],
					o[1] != null ? (String) o[1] : null));
			return nameMap;
		} catch (Exception e) {
			LOGGER.error("Error while getting customer name", e);
			return nameMap;
		}
	}

	private String getFilingFrequency(String eachTaxPeriod, String filingType) {
		String quarter = "";
		if (eachTaxPeriod.startsWith("04") || eachTaxPeriod.startsWith("05")
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
		String[] fileTypes = filingType.split(" ");
		for (int i = 0; i < fileTypes.length; i++) {
			if (fileTypes[i].contains(quarter)) {
				if (fileTypes[i].contains("M"))
					return "Monthly";
				else
					return "Quarterly";
			}

		}
		return quarter;
	}
}
