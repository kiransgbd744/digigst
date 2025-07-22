package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;

public interface NCVendorCommHelperService {

	List<VendorMasterUploadEntity> extractAndPopulateVendorProcessedRecords(
			Long entityId, List<String> vendorGstinsList,
			List<String> vendorPans);

	Map<String, Boolean> getValidConfirmantRange(List<String> stampedVendors,
			String returnType, String fy,
			Map<String, String> filingTypeMap, boolean isCurrentFy);

	void populateMonthWiseStatusForGstins(
			List<GstinWiseFilingStatus> gstinWiseFilingStatus,
			List<GstrReturnStatusEntity> returnStatusEntities,
			Map<String, Boolean> map, String returnType);

	void populateMonthWiseStatusForPans(
			List<GstinWiseFilingStatus> gstinWiseFilingStatus,
			List<OverallFilingStatusDto> listOfPans, List<String> taxPeriods);

	OverAllFilingStatusWithFinancialYearInfo getOnlyCompliantOrNonCompliantData(
			String viewType, List<OverallFilingStatusDto> listOfPans,
			String financialYear);

	String getTypeGstFiling(
			List<GstrReturnStatusEntity> persistedGstrReturnEntityList,
			GstrReturnStatusEntity singlePersistedData,
			Map<String, String> filingTypeMap);

	List<NonComplaintVendorReportDto> methodToConvertNonVendorDto(
			List<VendorMasterUploadEntity> uploadEntities,
			List<GstrReturnStatusEntity> retFilingList,
			Map<String, String> filingTypeMap);

	List<NonComplaintVendorReportDto> removeNonComplaintExtraMonthsForQuarterly(
			List<NonComplaintVendorReportDto> nonVendorReportDtos);

	List<NonComplaintVendorReportDto> getOverallNonComplaintsDataBasedOnGstin(
			List<VendorMasterUploadEntity> uploadEntities, String financialYear,
			String reportType);

	Predicate<NonComplaintVendorReportDto> filterConditionForNonomplaint(
			GstrReturnStatusEntity obj1);

	Map<String, String> getfilingTypeMapFromStampedTable(
			List<String> vendorGstin, String fy);

	List<String> getOnlyGstInList(
			List<VendorMasterUploadEntity> uploadEntities);

	List<String> getStampedVendors(List<String> vendorGstins,
			String returnType);

}
