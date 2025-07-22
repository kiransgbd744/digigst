package com.ey.advisory.app.data.services.compliancerating;

import java.io.File;
import java.util.List;

import com.aspose.cells.Workbook;

public interface VendorComplianceRatingService {

	List<GstinWiseComplianceRatingStatus> getVendorComplianceRatingData(
			VendorComplianceRatingRequestDto requestDto);

	Workbook getComplianceRatingTableReport(
			VendorComplianceRatingRequestDto requestDto, String fileName);

	Long createVendorComplianceRatingAsyncReport(
			VendorComplianceRatingRequestDto requestDto, String requestObject);

}
