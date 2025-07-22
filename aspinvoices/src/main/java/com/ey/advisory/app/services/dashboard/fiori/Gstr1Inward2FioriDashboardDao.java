package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

public interface Gstr1Inward2FioriDashboardDao {

	List<Object[]> getAllHeadersData(String fy, List<String> listOfRecepGstin,
			List<String> listOfReturnPrds);

	List<Object[]> getprVsGstr2aRecords(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<Object[]> getprVsGstr2bRecords(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<Object[]> getPurchaseRegisterVsGstr2b(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<Object[]> get2aVs2bVsPrSummary(String fy, List<String> recepientGstins,
			List<String> returnPeriods);

	List<Object[]> getPr2a2bData(String fy, List<String> listOfRecepGstin,
			List<String> returnPeriods);

	List<Object[]> getLastRefereshedOn(String fy, List<String> listOfRecepGstin,
			List<String> returnPeriods);

	List<Object[]> getPurchaseRegisterVsGstr2a(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<Object[]> get2aVs2bVsPrSummarySuppliers(String fy,
			List<String> recepientGstins, List<String> returnPeriods);

	List<Object[]> get2aVs2bVsPrSummaryTaxable(String fy,
			List<String> recepientGstins, List<String> returnPeriods);

	List<Object[]> get2aVs2bVsPrSummaryTotalTax(String fy,
			List<String> recepientGstins, List<String> returnPeriods);

	List<String> getReconLastUpdatedOn(String fy, List<String> listOfRecepGstin,
			List<String> returnPeriods);

}
