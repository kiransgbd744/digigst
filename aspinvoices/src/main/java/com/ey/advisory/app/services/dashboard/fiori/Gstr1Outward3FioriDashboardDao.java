package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

import com.ey.advisory.common.AppException;

public interface Gstr1Outward3FioriDashboardDao {

	List<Object[]> getAllHeadersData(String fy, List<String> supplierGstins,
			List<String> taxPeriods) throws AppException;

	List<Object[]> getUtilizationSummaryData(String fy,
			List<String> supplierGstins, List<String> taxPeriods)
			throws AppException;

	List<Object[]> getGstNetLiabilityData(String fy,
			List<String> supplierGstins, List<String> taxPeriods)
			throws AppException;

	List<Object[]> getRevenueAndLiabCompAnalysis(String fy,
			List<String> supplierGstins, List<String> taxPeriods)
			throws AppException;

	List<Object[]> getLiabilityTableData(String fy, List<String> supplierGstins,
			List<String> taxPeriods) throws AppException;

	List<String> getSupGstinList(String supplierPan) throws AppException;

	List<String> getAllReturnPeriods(String fy) throws AppException;

}
