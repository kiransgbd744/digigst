package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

import com.ey.advisory.common.AppException;

/**
 * @author Saif.S
 *
 */
public interface Gstr1Outward2FioriDashboardDao {

	List<Object[]> getAllHeadersData(String fy, List<String> supplierGstins,
			List<String> retunPeriods) throws AppException;

	List<Object[]> getPsdVsErrData(String fy, List<String> supplierGstins,
			List<String> retunPeriods) throws AppException;

	List<Object[]> getRevenueComparativeAnalysis(String fy, String valueFlag,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException;

	List<Object[]> getOutwardSupplyDetails(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException;

	List<String> getSupGstinList(String supplierPan) throws AppException;

	List<String> getAllReturnPeriods(String fy) throws AppException;

	List<Object[]> getTotalTurnOverAndTax(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException;
}
