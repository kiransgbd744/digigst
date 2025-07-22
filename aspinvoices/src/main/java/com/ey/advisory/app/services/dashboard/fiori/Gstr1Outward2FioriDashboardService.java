package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

import com.ey.advisory.core.dto.FinancialYearDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

/**
 * @author Saif.S
 *
 */
public interface Gstr1Outward2FioriDashboardService {

	Outward2HeaderDetailsDto getOutward2HeaderData(String fy,
			List<String> supplierGstins, List<String> returnPeriods);

	List<B2bCntDto> getPsdVsErrRecords(String fy, List<String> supplierGstins,
			List<String> returnPeriods);

	List<Gstr1FioriDashboardChartDto> getRevenueComparativeAnalysis(String fy,
			String valueFlag, List<String> supplierGstins,
			List<String> retunPeriods);

	List<B2bCntDto> getRevenueComparativeAnalysisForB2b(String fy,
			String valueFlag, List<String> supplierGstins,
			List<String> retunPeriods);

	List<OutwardSupplyDetailsDto> getOutwardSuppliyDetails(String fy,
			List<String> supplierGstins, List<String> returnPeriods);

	List<GstinDto> getAllSupplierGstins(List<Long> entityId);

	List<FinancialYearDto> getAllReturnPeriods(String fy);
	
	Outward2TurnOverDto getTotalTurnOverAndTax(String fy,
			List<String> supplierGstins, List<String> returnPeriods);
}
