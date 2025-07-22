package com.ey.advisory.service.vendor.compliance;

import com.ey.advisory.app.data.services.compliancerating.VendorComplianceRatingRequestDto;

public interface VendorTableReportDownloadService {

	public void getData(VendorComplianceRatingRequestDto reqDto,Long id);

}
