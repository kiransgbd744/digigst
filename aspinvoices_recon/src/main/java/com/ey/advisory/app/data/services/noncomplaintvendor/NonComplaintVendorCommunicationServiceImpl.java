package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
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
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.common.TypeOfGstFiling;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Service("NonComplaintVendorCommunicationServiceImpl")
@Slf4j
public class NonComplaintVendorCommunicationServiceImpl
		implements NonComplaintVendorCommunicationService {

	private static final String IS_SIDE_THE_METHOD_OF_GET_NON_COMPLAINT_VENDOR_REPORT = "is side the method of getNonComplaintVendorReport() ";

	private static final String NON_COMPLIANT = "non-compliant";

	private static final List<String> QUARTERLY_MONTHS = ImmutableList.of("06",
			"09", "12", "03");

	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");
	@Autowired
	private GstnReturnFilingStatus gstnReturnFiling;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnStatusRepo;

	@Autowired
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;

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
	private Environment env;

	@Autowired
	@Qualifier("VendorGstinFilingTypeRepository")
	private VendorGstinFilingTypeRepository vendorGstinFilingTypeRepo;
	
	@Autowired
	private NCVendorCommHelperService helperService;

	@Override
	public void persistGstnApiForSelectedFinancialYear(String finYear,
			List<String> vendorGstins,String complianceType) {
		try {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"inside the method of persistGstnApiForSelectedFinancialYear "
								+ "of class "
								+ "NonComplaintVendorCommunicationServiceImpl "
								+ "for selected year:{} and vendorGstins:{}",
						finYear, vendorGstins);
			List<String> customerGstins = new ArrayList<>();

			if (vendorGstins == null) {
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
						PublicApiConstants.NCOM_VENDOR_RET);

				customerGstins = vendorMasterUploadEntityRepository
						.findVendorGstin();
			} else {
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
						PublicApiConstants.VENDOR_RATING_RET);

				customerGstins.addAll(vendorGstins);
			}
			if (CollectionUtils.isEmpty(customerGstins)) {
				String errMsg = "VendorMaster GSTINS are not "
						+ "available to initiate Get Filing Status";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			if (complianceType != null) {
				if (complianceType.equalsIgnoreCase("MyCompliance")) {
					List<ReturnFilingGstnResponseDto> retFilingList = gstnReturnFiling
							.callGstnApi(customerGstins, finYear, false);
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"About to persist Counter party Return Filling status "
										+ "sanbox response data into table from MyCompliance");
					gstnReturnFiling.persistReturnFillingStatus(retFilingList,
							false);
				} else{
					List<ReturnFilingGstnResponseDto> retFilingList = gstnReturnFiling
							.callGstnApi(customerGstins, finYear, true);
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"About to persist Counter party Return Filling status "
										+ "sanbox response data into table from %s",complianceType);
					gstnReturnFiling.persistReturnFillingStatus(retFilingList,
							true);
				
				}
			}

		} catch (Exception e) {
			String errMsg = String.format(
					"Error while saving the return filling status of vendors for FY %s",
					finYear);
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	@Override
	public Workbook getNonComplaintVendorReport(String financialYear,
			String reportType, Long entityId,File tempDir,Long batchId) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("");
			String msg = String.format(
					IS_SIDE_THE_METHOD_OF_GET_NON_COMPLAINT_VENDOR_REPORT
							+ "Parameter financialYear %s:: annd reportType is %s:: ",
					financialYear, reportType);
			LOGGER.debug(msg);
		}
		List<String> recipientPanList = entityInfoRepository
				.findPanByEntityId(entityId);

		List<VendorMasterUploadEntity> uploadEntities = vendorMasterUploadEntityRepository
				.findByIsDeleteFalseAndRecipientPANIn(recipientPanList);

		List<String> vendorGstins = helperService
				.getOnlyGstInList(uploadEntities);

		List<VendorReturnTypeEntity> invalidgstins = new ArrayList<>();
		List<List<String>> chunks = Lists.partition(vendorGstins, 2000);
		for (List<String> chunk : chunks) {
			invalidgstins.addAll(vendorReturnTypeRepo
					.findByGstinInAndResponseIsNotNull(chunk));
		}
		LocalDateTime timeOfGeneration = LocalDateTime.now();
		LocalDateTime convertISDDate = EYDateUtil
				.toISTDateTimeFromUTC(timeOfGeneration);
		DateTimeFormatter format = DateTimeFormatter
				.ofPattern("yyyyMMddHHmmss");
		String fileName = tempDir.getAbsolutePath() + File.separator
				+ "Vendors_Compliance_Report_FY-" + financialYear + "_" + batchId + "_"
				+ format.format(convertISDDate) + ".xlsx";
		List<NonComplaintVendorReportDto> nonComplaintVendorsReport = helperService
				.getOverallNonComplaintsDataBasedOnGstin(uploadEntities,
						financialYear, reportType);
		nonComplaintVendorsReport.stream().filter(dto -> dto.getReturnType().
				equalsIgnoreCase("ITC04")).forEach(dto -> {
			dto.setTaxPeriod(itc04TaxPeriod(dto.getTaxPeriod()));
		});
		if (reportType.equalsIgnoreCase("Compliant")) {
			return writeToExcelVendorReport(nonComplaintVendorsReport, "compliant",
					"Compliant",fileName);
		} else {

			List<NonComplaintVendorReportDto> overallNonVendorReports = addingInvalidGstins(
					invalidgstins, nonComplaintVendorsReport);
			return writeToExcelVendorReport(overallNonVendorReports, NON_COMPLIANT, "All",fileName);
		}

	}

	private Workbook writeToExcelVendorReport(
			List<NonComplaintVendorReportDto> gstr2ReconSummary,
			String typeOfFlag, String fileType,String fileName) {

		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (gstr2ReconSummary != null && !gstr2ReconSummary.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp(
							"itc." + typeOfFlag + ".vendor.master.process.data")
					.split(",");
			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", fileType + "VendorsReport.xlsx");
			if (LOGGER.isDebugEnabled()) {
				String msg = "NonComplaintVendorCommunicationServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			reportCells.importCustomObjects(gstr2ReconSummary, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					gstr2ReconSummary.size(), true, "yyyy-mm-dd", false);
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "NonComplaintVendorCommunicationServiceImpl.writeToExcel "
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
//			throw new AppException("No records found, cannot generate report");
		}
		return workbook;
	}

	private Workbook writeToExcel(
			List<NonComplaintVendorReportDto> gstr2ReconSummary,
			String typeOfFlag, String fileName) {

		Workbook workbook = null;
		int startRow = 1;
		int startcolumn = 0;
		boolean isHeaderRequired = false;

		if (gstr2ReconSummary != null && !gstr2ReconSummary.isEmpty()) {

			String[] invoiceHeaders = commonUtility
					.getProp(
							"itc." + typeOfFlag + ".vendor.master.process.data")
					.split(",");

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", fileName + "VendorsReport.xlsx");
			if (LOGGER.isDebugEnabled()) {
				String msg = "NonComplaintVendorCommunicationServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			reportCells.importCustomObjects(gstr2ReconSummary, invoiceHeaders,
					isHeaderRequired, startRow, startcolumn,
					gstr2ReconSummary.size(), true, "yyyy-mm-dd", false);
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = "NonComplaintVendorCommunicationServiceImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.VENDORDATAUPLOAD,
						SaveFormat.XLSX);
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

	@Override
	public Workbook getNonComplaintVendorTableReport(String financialYear,
			String returnType, String viewType, Long entityId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("");
			String msg = String.format(
					IS_SIDE_THE_METHOD_OF_GET_NON_COMPLAINT_VENDOR_REPORT
							+ "Parameter financialYear %s::, returnType %s:: ,viewType %s:: ,entityId %s:: ",
					financialYear, returnType, viewType, entityId);
			LOGGER.debug(msg);
		}
		List<String> recipientPanList = entityInfoRepository
				.findPanByEntityId(entityId);

		List<VendorMasterUploadEntity> uploadEntities = vendorMasterUploadEntityRepository
				.findByIsDeleteFalseAndRecipientPANIn(recipientPanList);

		List<String> vendorGstins = uploadEntities.stream()
				.map(VendorMasterUploadEntity::getVendorGstin).distinct()
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(vendorGstins)) {
			String errMsg = String.format(
					"Vendor Master is empty for group - %s and entityId - %d",
					TenantContext.getTenantId(), entityId);
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}

		// fetch stamped Vendor for a returnType
		List<String> stampedVendors = helperService
				.getStampedVendors(vendorGstins, returnType);

		List<String> taxPeriods = GenUtil.extractTaxPeriodsFromFY(financialYear,
				returnType);

		List<GstrReturnStatusEntity> returnStatusEntities = new ArrayList<>();
		List<List<String>> retChunks = Lists.partition(stampedVendors, 2000);
		for (List<String> chunk : retChunks) {
			returnStatusEntities = returnStatusRepo
					.findByGstinInAndReturnTypeAndTaxPeriodInAndIsCounterPartyGstin(
							chunk, returnType, taxPeriods, true);
		}
		String currentFy = GenUtil.getCurrentFinancialYear();
		boolean isCurrentFy = currentFy.equals(financialYear);
		List<GstrReturnStatusEntity> retFilingList = new ArrayList<>();
		// set month wise all gstins 12 months making as unknown or
		// nonCompliant based on FY
		setDefaultFilingStatusForReports(stampedVendors, taxPeriods,
				isCurrentFy, returnType, retFilingList);

		Map<String, String> filingTypeMap = new HashMap<>();
		if (returnType.equals("GSTR1") || returnType.equals("GSTR3B")) {
			filingTypeMap = getfilingTypeMapFromStampedTable(
					stampedVendors, financialYear,returnType);
		}
		// get valid confirmed taxPeriods
		Map<String, Boolean> map = helperService.getValidConfirmantRange(
				stampedVendors, returnType, financialYear, filingTypeMap,
				isCurrentFy);

		if (viewType.equalsIgnoreCase("Compliant")) {
			if (CollectionUtils.isEmpty(returnStatusEntities))
				throw new AppException("Compliant gstins not found");
			List<NonComplaintVendorReportDto> nonVendorReportDtos = helperService
					.methodToConvertNonVendorDto(uploadEntities,
							returnStatusEntities, filingTypeMap);
			nonVendorReportDtos.stream().filter(dto -> dto.getReturnType().
					equalsIgnoreCase("ITC04")).forEach(dto -> {
				dto.setTaxPeriod(itc04TaxPeriod(dto.getTaxPeriod()));
			});
			return writeToExcel(nonVendorReportDtos, "compliant", "Compliant");
		} else if (viewType.equalsIgnoreCase("All")) {
			getComplaintVendorReportBasedOnReportType(returnStatusEntities,
					retFilingList, "All", map);
			List<NonComplaintVendorReportDto> nonVendorReportDtos = helperService
					.methodToConvertNonVendorDto(uploadEntities, retFilingList,
							filingTypeMap);
			List<NonComplaintVendorReportDto> nonVendorReports = helperService
					.removeNonComplaintExtraMonthsForQuarterly(
							nonVendorReportDtos);
			nonVendorReports.stream().filter(dto -> dto.getReturnType().
					equalsIgnoreCase("ITC04")).forEach(dto -> {
				dto.setTaxPeriod(itc04TaxPeriod(dto.getTaxPeriod()));
			});
			return writeToExcel(nonVendorReports, NON_COMPLIANT, "All");
		} else {
			getComplaintVendorReportBasedOnReportType(returnStatusEntities,
					retFilingList, "All", map);
		}
		List<NonComplaintVendorReportDto> nonVendorReportDtos = helperService
				.methodToConvertNonVendorDto(uploadEntities, retFilingList,
						filingTypeMap);

		returnStatusEntities.forEach(eachObj -> {
			Optional<NonComplaintVendorReportDto> eachEntry = nonVendorReportDtos
					.stream()
					.filter(helperService
							.filterConditionForNonomplaint(eachObj))
					.findFirst();
			if (eachEntry.isPresent()) {
				nonVendorReportDtos.remove(eachEntry.get());
			}
		});
		List<NonComplaintVendorReportDto> nonVendorReports = helperService
				.removeNonComplaintExtraMonthsForQuarterly(nonVendorReportDtos);
		nonVendorReports.stream().filter(dto -> dto.getReturnType().
				equalsIgnoreCase("ITC04")).forEach(dto -> {
			dto.setTaxPeriod(itc04TaxPeriod(dto.getTaxPeriod()));
		});
		return writeToExcel(nonVendorReports, NON_COMPLIANT, "Non-Compliant");

	}

	private void setDefaultFilingStatusForReports(List<String> stampedVendors,
			List<String> taxPeriods, boolean isCurrentFy, String returnType,
			List<GstrReturnStatusEntity> retFilingList) {

		String defaultStatus = isCurrentFy
				? MonthWiseGstinStatus.UNKNOWN.getStatus()
				: MonthWiseGstinStatus.NOT_FILED.getStatus();
		stampedVendors.forEach(eactGst -> taxPeriods.forEach(eachTax -> {
			GstrReturnStatusEntity returnFilingGstnResponseDto = new GstrReturnStatusEntity();
			returnFilingGstnResponseDto.setReturnType(returnType);
			returnFilingGstnResponseDto.setTaxPeriod(eachTax);
			returnFilingGstnResponseDto.setGstin(eactGst);
			returnFilingGstnResponseDto.setStatus(defaultStatus);
			retFilingList.add(returnFilingGstnResponseDto);

		}));
	}

	private void getComplaintVendorReportBasedOnReportType(
			List<GstrReturnStatusEntity> persistedEntites,
			List<GstrReturnStatusEntity> preparedGstIn, String reportType,
			Map<String, Boolean> map) {

		if (reportType.equalsIgnoreCase("All")) {
			preparedGstIn.stream().forEach(prepareddata -> {
				String key = prepareddata.getGstin().concat(prepareddata
						.getTaxPeriod().concat(prepareddata.getReturnType()));
				if (map.containsKey(key) && Boolean.TRUE.equals(map.get(key))) {
					prepareddata.setStatus(
							MonthWiseGstinStatus.NOT_FILED.getStatus());
				}
				for (GstrReturnStatusEntity persistedData : persistedEntites) {
					if (persistedData.getGstin().equals(prepareddata.getGstin())
							&& persistedData.getTaxPeriod()
									.equals(prepareddata.getTaxPeriod())) {
						prepareddata.setStatus(persistedData.getStatus());
						BeanUtils.copyProperties(persistedData, prepareddata);
					}
				}

			});
		} else if (reportType.equalsIgnoreCase("NonCompliant")) {
			persistedEntites.stream().forEach(persistedData -> {
				if (preparedGstIn.contains(persistedData)) {
					preparedGstIn.remove(persistedData);
				}
			});
		}

	}

	@Override
	public OverAllFilingStatusWithFinancialYearInfo getOverallReturnFilingStatus(
			String financialYear, String returnType, String viewType,
			Long entityId, List<String> vendorPans,
			List<String> vendorGstinsList) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"NCV UI Inside getOverallReturnFilingStatus method and "
								+ "fy %s::, returnType %s:: ,viewType %s::,entityId %s:: ",
						financialYear, returnType, viewType, entityId);
				LOGGER.debug(msg);
			}
			// get all vendor master uploaded entities
			List<VendorMasterUploadEntity> uploadEntities = helperService
					.extractAndPopulateVendorProcessedRecords(entityId,
							vendorGstinsList, vendorPans);

			if (CollectionUtils.isEmpty(uploadEntities)) {
				String msg = String.format(
						"For the parameter fy %s::, returnType %s:: "
								+ ",viewType %s:: the data is not there",
						financialYear, returnType, viewType);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			List<String> vendorGstins = helperService
					.getOnlyGstInList(uploadEntities);

			// fetch stamped Vendor for a returnType
			List<String> stampedVendors = helperService
					.getStampedVendors(vendorGstins, returnType);

			Map<String, List<VendorMasterUploadEntity>> vendorPANGroupMap = uploadEntities
					.stream()
					.filter(o -> stampedVendors.contains(o.getVendorGstin()))
					.collect(Collectors.groupingBy(
							VendorMasterUploadEntity::getVendorPAN));

			List<OverallFilingStatusDto> listOfPans = new ArrayList<>();

			getListOfFilingStatusDto(vendorPANGroupMap, listOfPans);

			// overall taxPeriods
			List<String> taxPeriods = GenUtil
					.extractTaxPeriodsFromFY(financialYear, returnType);

			// Filed vendors or compliant vendors
			List<GstrReturnStatusEntity> returnStatusEntities = returnStatusRepo
					.findByGstinInAndReturnTypeAndTaxPeriodInAndIsCounterPartyGstin(
							stampedVendors, returnType, taxPeriods, true);

			List<GstinWiseFilingStatus> gstinWiseFilingStatus = new ArrayList<>();
			Map<String, String> filingTypeMap = new HashMap<>();
			if (returnType.equals("GSTR1") || returnType.equals("GSTR3B")) {
				filingTypeMap = helperService.getfilingTypeMapFromStampedTable(
						stampedVendors, financialYear);
			}
			for (GstrReturnStatusEntity obj : returnStatusEntities) {
				helperService.getTypeGstFiling(returnStatusEntities, obj,
						filingTypeMap);
			}

			String currentFy = GenUtil.getCurrentFinancialYear();
			boolean isCurrentFy = currentFy.equals(financialYear);

			// get valid confirmed taxPeriods
			Map<String, Boolean> map = helperService.getValidConfirmantRange(
					stampedVendors, returnType, financialYear, filingTypeMap,
					isCurrentFy);

			// set month wise all gstins 12 months making as unknown or
			// nonCompliant based on FY
			setDefaultFilingStatus(stampedVendors, taxPeriods,
					gstinWiseFilingStatus, isCurrentFy, filingTypeMap,
					returnType);

			helperService.populateMonthWiseStatusForGstins(
					gstinWiseFilingStatus, returnStatusEntities, map,
					returnType);

			helperService.populateMonthWiseStatusForPans(gstinWiseFilingStatus,
					listOfPans, taxPeriods);

			return helperService.getOnlyCompliantOrNonCompliantData(viewType,
					listOfPans, financialYear);

		} catch (Exception e) {
			LOGGER.error(
					"exeption while fetching the vendor return status along with"
							+ " table data:",
					e);
			OverAllFilingStatusWithFinancialYearInfo allFilingStatusWithFinancialYearInfo = new OverAllFilingStatusWithFinancialYearInfo();
			Pageable pageReq = PageRequest.of(0, 1, Direction.DESC,
					"modifiedOn");
			List<ReturnDataStorageStatusEntity> returnDataStorageStatusEntity = returnDataStorageStatusRepo
					.findByFinancialYear(financialYear, pageReq);
			if (returnDataStorageStatusEntity != null
					&& !returnDataStorageStatusEntity.isEmpty()) {
				allFilingStatusWithFinancialYearInfo
						.setFinanicalYear(returnDataStorageStatusEntity.get(0)
								.getFinancialYear());
				allFilingStatusWithFinancialYearInfo.setStatus(
						returnDataStorageStatusEntity.get(0).getStatus());
				LocalDateTime istCreatedDate = EYDateUtil.toISTDateTimeFromUTC(
						returnDataStorageStatusEntity.get(0).getModifiedOn());

				String formatDateTime = istCreatedDate.format(format);
				allFilingStatusWithFinancialYearInfo
						.setModifedOn(formatDateTime);

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
	}

	private void getListOfFilingStatusDto(
			Map<String, List<VendorMasterUploadEntity>> map,
			List<OverallFilingStatusDto> listOfPans) {
		for (Map.Entry<String, List<VendorMasterUploadEntity>> eachGstIn : map
				.entrySet()) {
			OverallFilingStatusDto overallFilingStatusDto = new OverallFilingStatusDto();
			overallFilingStatusDto.setVendorPan(eachGstIn.getKey());
			overallFilingStatusDto
					.setVendorName(eachGstIn.getValue().get(0).getVendorName());
			listOfPans.add(overallFilingStatusDto);
		}
	}

	@SuppressWarnings("unused")
	private List<String> getReturnTypes() {
		String returnTypeStr = env
				.getProperty("vendor.return.filling.status.types");
		if (returnTypeStr == null) {
			LOGGER.error(
					"Return Types are not configured in Application.properties,"
							+ " Hence Default return types are fetched");
			return Arrays.asList("GSTR1", "GSTR3B", "ITC04", "GSTR6");
		}
		String[] returnTypes = returnTypeStr.split(",");
		return Arrays.asList(returnTypes);
	}

	private List<NonComplaintVendorReportDto> addingInvalidGstins(
			List<VendorReturnTypeEntity> invalidgstins,
			List<NonComplaintVendorReportDto> nonVendorReports) {

		invalidgstins.forEach(eachObj -> {
			NonComplaintVendorReportDto nonComplaintVendorReportDto = new NonComplaintVendorReportDto();
			nonComplaintVendorReportDto.setVendorGSTIN(eachObj.getGstin());
			nonComplaintVendorReportDto.setErrorfromGSTN(eachObj.getResponse());
			nonVendorReports.add(nonComplaintVendorReportDto);
		});
		return nonVendorReports;
	}

	@Override
	public Map<String, Set<String>> getNonComplaintComVendorGstins(
			String financialYear, List<String> vendorGstinsList, Long entityId,
			String reportType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("");
			String msg = String.format(
					"Inside getNonComplaintComVendorGstins method"
							+ "Parameter financialYear %s::, vendorGstinsList %s:: ,entityId %s:: ",
					financialYear, vendorGstinsList, entityId);
			LOGGER.debug(msg);
		}
		List<String> recipientPanList = entityInfoRepository
				.findPanByEntityId(entityId);
		List<VendorMasterUploadEntity> uploadEntities = new ArrayList<>();
		List<List<String>> chunks = Lists.partition(vendorGstinsList, 2000);
		for (List<String> chunk : chunks) {
			uploadEntities.addAll(vendorMasterUploadEntityRepository
					.findByIsDeleteFalseAndRecipientPANInAndVendorGstinInAndIsNonComplaintComTrue(
							recipientPanList, chunk));
		}

		List<NonComplaintVendorReportDto> nonVendorReports = helperService
				.getOverallNonComplaintsDataBasedOnGstin(uploadEntities,
						financialYear, reportType);
		Map<String, Set<String>> validStampedNonComplaintGstins = new HashMap<>();

		for (NonComplaintVendorReportDto vendor : nonVendorReports) {
			Set<String> distinctReturnTypes = new HashSet<>();
			if (validStampedNonComplaintGstins
					.containsKey(vendor.getVendorGSTIN())) {

				distinctReturnTypes.addAll(validStampedNonComplaintGstins
						.get(vendor.getVendorGSTIN()));
				if (!distinctReturnTypes.contains(vendor.getReturnType())) {
					distinctReturnTypes.add(vendor.getReturnType());

					validStampedNonComplaintGstins.put(vendor.getVendorGSTIN(),
							distinctReturnTypes);
				}
			} else {
				distinctReturnTypes.add(vendor.getReturnType());
				validStampedNonComplaintGstins.put(vendor.getVendorGSTIN(),
						distinctReturnTypes);
			}
		}
		return validStampedNonComplaintGstins;

	}

	private void setDefaultFilingStatus(List<String> stampedVendors,
			List<String> taxPeriods,
			List<GstinWiseFilingStatus> gstinWiseFilingStatus,
			boolean isCurrentFy, Map<String, String> filingTypeMap,
			String returnType) {

		String defaultStatus = isCurrentFy
				? MonthWiseGstinStatus.UNKNOWN.getStatus()
				: MonthWiseGstinStatus.NOT_FILED.getStatus();
		stampedVendors.stream().forEach(gstin -> {
			String key = String.format("%s-%s", gstin, returnType);
			String filingType = filingTypeMap.containsKey(key)
					? filingTypeMap.get(key).toString()
					: TypeOfGstFiling.MONTHLY.toString();
			GstinWiseFilingStatus gstinWise = new GstinWiseFilingStatus();
			gstinWise.setGstin(gstin);
			gstinWise.setFilingType(filingType);
			List<MonthWiseGstinFilingStatus> monthlyStatus = new ArrayList<>();
			taxPeriods.stream().forEach(eachTaxPeriod -> {
				String getFilingType = getFilingFrequency(eachTaxPeriod, filingType);
				MonthWiseGstinFilingStatus monthWise = new MonthWiseGstinFilingStatus();
				monthWise.setMonth(eachTaxPeriod);
				if (getFilingType != null) {
					monthWise.setStatus(!isCurrentFy
							&& getFilingType.equalsIgnoreCase(
									TypeOfGstFiling.QUARTERLY.toString())
							&& !QUARTERLY_MONTHS
									.contains(eachTaxPeriod.substring(0, 2))
											? MonthWiseGstinStatus.UNKNOWN
													.getStatus()
											: defaultStatus);
				} else {
					monthWise.setStatus(!isCurrentFy
							&& filingType.equals(
									TypeOfGstFiling.QUARTERLY.toString())
							&& !QUARTERLY_MONTHS
									.contains(eachTaxPeriod.substring(0, 2))
											? MonthWiseGstinStatus.UNKNOWN
													.getStatus()
											: defaultStatus);
				}
				monthlyStatus.add(monthWise);
			});
			gstinWise.setEachGstinwiseStatusCombination(monthlyStatus);
			gstinWiseFilingStatus.add(gstinWise);
		});
	}

	public String itc04TaxPeriod(String month){
		String taxPeriod = "";
		if(month.startsWith("13")){
			taxPeriod = month.replace("13", "Q1");
		} else if(month.startsWith("14")){
			taxPeriod = month.replace("14", "Q2");
		} else if(month.startsWith("15")){
			taxPeriod = month.replace("15", "Q3");
		} else if(month.startsWith("16")){
			taxPeriod = month.replace("16", "Q4");
		} else if(month.startsWith("17")){
			taxPeriod = month.replace("17", "H1");
		} else if(month.startsWith("18")){
			taxPeriod = month.replace("18", "H2");
		} else{
			taxPeriod = month;
		}
			
		return taxPeriod;	
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
						return "Monthly";
					else
						return "Quarterly";
				}

			}
		}
		return quarter;
	}

	public Map<String, String> getfilingTypeMapFromStampedTable(
			List<String> vendorGstins, String fy, String returnType) {
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
				for (VendorGstinFilingTypeEntity filingEntity : filingTypeList) {
					String key = filingEntity.getGstin().concat("-")
							.concat(returnType);
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
						if (i++ != (entityList.size() - 1)) {
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
}
