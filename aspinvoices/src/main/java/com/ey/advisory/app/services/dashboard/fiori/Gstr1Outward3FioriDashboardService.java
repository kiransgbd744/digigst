package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

import com.ey.advisory.core.dto.FinancialYearDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

public interface Gstr1Outward3FioriDashboardService {

	Outward3HeaderDetailsDto getOutward3HeaderData(String fy,
			List<String> supplierGstins, List<String> taxPeriods);

	List<Gstr1FioriDashboardChartDto> getUtilizationSummData(String fy,
			List<String> supplierGstins, List<String> taxPeriods);

	List<Gstr1FioriDashboardChartDto> getGstNetLiabDetails(String fy,
			List<String> supplierGstins, List<String> taxPeriods);

	List<GetLiabTableDto> getLiabilityTbleDetails(String fy,
			List<String> supplierGstins, List<String> taxPeriods);

	List<GstinDto> getAllSupplierGstins(Long entityId);

	List<FinancialYearDto> getAllReturnPeriods(String fy);
}
