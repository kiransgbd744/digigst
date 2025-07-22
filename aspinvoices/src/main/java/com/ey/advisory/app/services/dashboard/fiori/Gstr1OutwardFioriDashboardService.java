package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

import com.ey.advisory.core.dto.FinancialYearDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

/**
 * @author Saif.S
 *
 */
public interface Gstr1OutwardFioriDashboardService {

	List<GstinDto> getAllSupplierGstins(List<Long> entityIds);

	List<FinancialYearDto> getAllReturnPeriods(String fy);

	List<Gstr1FioriDashboardChartDto> getGrossOutwardSuppplies(String fy,
			List<String> supplierGstins, List<String> returnPeriods, String flag);

	List<Gstr1FioriDashboardChartDto> getMonthWiseTrendAnalysisList(String fy,
			String valueFlag, List<String> supplierGstins,
			List<String> returnPeriods);

	List<Gstr1FioriDashboardChartDto> getTopCustomersB2BList(String fy,
			List<String> supplierGstins, List<String> returnPeriods);

	List<Gstr1FioriDashboardChartDto> getMajorTaxPayingProductsList(String fy,
			List<String> supplierGstins, List<String> returnPeriods);

	List<TaxRateWiseDistributionDto> getTaxRateWiseDistributionList(String fy,
			List<String> supplierGstins, List<String> returnPeriods);

	TotalLiabilityDetailsDto getTotalLiabilityData(String fy,
			List<String> supplierGstins, List<String> returnPeriods);

	List<TaxLiabilityDetailsDto> getTaxLiabilityData(String fy,
			List<String> supplierGstins, List<String> returnPeriods);
	
	List<TaxLiabilityDetailsFinalDto> getTaxLiabilityIoclData(String fy,
			List<String> supplierGstins, List<String> returnPeriods);
	
	List<MajorTaxPayFinalDto> getMajorTaxpayDetails(String fy,
			List<String> supplierGstins, List<String> returnPeriods);
	
	


}
