package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

import com.ey.advisory.core.dto.FinancialYearDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;

public interface Gstr1InwardFioriDashboardService {

	List<GstinDto> getAllRecepientGstins(List<Long> entityIds);

	List<FinancialYearDto> getAllReturnPeriods(String fy);

	List<Gstr1FioriDashboardChartDto> getGrossInwardSuppplies(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds, String flag);

	List<Gstr1FioriDashboardChartDto> getMonthWiseTrendAnalysisList(String fy,
			String valueFlag, List<String> listOfRecepGstin,
			List<String> listOfReturnPrds);

	List<Gstr1FioriDashboardChartDto> getTopCustomersB2BList(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods);

	List<Gstr1FioriDashboardInwardChartDto> getMajorGoodsProcurred(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods);

	List<TaxRateWiseDistributionDto> getTaxRateWiseDistributionList(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods);

	List<TaxInwardDetailsDto> getTaxInwardData(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	TotalItcDetailsDto getTotalItcData(String fy, List<String> listOfRecepGstin,
			List<String> listOfReturnPrds);

	List<LastUpdatedOnDto> getLastUpdatedOn(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

}
