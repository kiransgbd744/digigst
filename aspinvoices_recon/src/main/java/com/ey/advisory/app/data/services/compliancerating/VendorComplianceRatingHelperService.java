package com.ey.advisory.app.data.services.compliancerating;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.data.entities.client.asprecon.VendorRatingCriteriaEntity;

public interface VendorComplianceRatingHelperService {

	VendorRatingOverAllDbDataDto getAllRequiredData(
			VendorComplianceRatingRequestDto requestDto);

	Pair<BigDecimal, Boolean> findDueTypeAndValidRating(String returnType,
			String taxPeriod, String status, LocalDate dueDate,
			LocalDate dateOfFiling,
			List<VendorRatingCriteriaEntity> ratingCriteria);

	void getGstinWiseRatingStatusDtos(
			List<GstinWiseComplianceRatingStatus> gstinWiseRatingstatus,
			VendorRatingOverAllDbDataDto allDatadto, String returnType,
			String entityId, int month, int day, int year, String source);

	void getDefaultReportDtos(List<VendorComplianceRatingReportDto> defaultList,
			VendorRatingOverAllDbDataDto allDatadto, String returnType,
			String entityId, int month, int day, int year, String source);

	void getAllCompliantReportDtos(
			List<VendorComplianceRatingReportDto> compliantList,
			VendorRatingOverAllDbDataDto allDatadto, String returnType,
			String entityId, String source);

	public List<String> getListOfCustomerGstin(List<String> vendorPans);

	public boolean getChannelOneClientInfo(Long entityId);

	public VendorComplianceCountDto getVendorCountDetails(
			List<GstinWiseComplianceRatingStatus> dtoList, String returnType,
			Long entityId, String source);
}
