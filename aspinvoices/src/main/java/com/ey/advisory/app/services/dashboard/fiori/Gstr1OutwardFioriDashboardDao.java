package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

import com.ey.advisory.common.AppException;

/**
 * @author Saif.S
 *
 */
public interface Gstr1OutwardFioriDashboardDao {

	List<String> getSupGstinList(String supplierPan) throws AppException;

	List<String> getAllReturnPeriods(String fy) throws AppException;

	List<Object[]> getAllGrossOutwardSupp(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException;

	List<Object[]> getMonthWiseTrendAnalysis(String fy, String valueFlag,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException;

	List<Object[]> getTopCustomerB2BData(String fy, List<String> supplierGstins,
			List<String> retunPeriods) throws AppException;

	List<Object[]> getMajorTaxPayingProducts(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException;

	List<Object[]> getTaxRateWiseDistribution(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException;

	List<Object[]> getTotalLiabilityDetails(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException;

	List<Object[]> getTaxLiabilityDetails(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException;
	
	List<Object[]> getTopb2bAfterToggle(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException;
	
	List<Object[]> getMajorTaxpayData(String fy,
			List<String> supplierGstins, List<String> retunPeriods)
			throws AppException;

}
