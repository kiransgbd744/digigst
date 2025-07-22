package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

public interface Gstr1Inward2FioriDashboardService {

	List<Inward2HeaderDetailsDto> getInward2HeaderData(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<Top10SuppliersDto> getprVsGstr2aRecords(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<Top10SuppliersDto> getprVsGstr2bRecords(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<Gstr2aVsPurchaseRegisterDto> getPurchaseRegisterVsGstr2b(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<PrSummary2a2bDto> get2aVs2bVsPrSummary(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<Pr2a2bDataDto> getPr2a2bData(String fy, List<String> listOfRecepGstin,
			List<String> listOfReturnPrds);

	List<LastRefreshedOnDto> getLastRefereshedOn(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<Gstr2aVsPurchaseRegisterDto> getPurchaseRegisterVsGstr2a(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<B2bCntDto> get2aVs2bVsPrSummarySuppliers(String fy, String valueFlag,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<Gstr1FioriDashboardChartDto> get2aVs2bVsPrSummaryTaxable(String fy,
			String valueFlag, List<String> listOfRecepGstin,
			List<String> listOfReturnPrds);

	List<Gstr1FioriDashboardChartDto> get2aVs2bVsPrSummaryTotalTax(String fy,
			String valueFlag, List<String> listOfRecepGstin,
			List<String> listOfReturnPrds);

	List<LastUpdatedOnDto> getReconLastUpdatedOn(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

}
