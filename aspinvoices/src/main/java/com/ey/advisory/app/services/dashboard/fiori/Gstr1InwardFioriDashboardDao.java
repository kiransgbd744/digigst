package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

import com.ey.advisory.common.AppException;

/**
 * @author Ravindra V S
 *
 */
public interface Gstr1InwardFioriDashboardDao {

	List<String> getRecGstinList(String recepientPan) throws AppException;

	List<String> getAllReturnPeriods(String fy);

	List<Object[]> getAllGrossInwardSupp(String fy,
			List<String> recepientGstins, List<String> returnPeriods);

	List<Object[]> getMonthWiseTrendAnalysis(String fy, String valueFlag,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<Object[]> getTopCustomerB2BData(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods);

	List<Object[]> getMajorGoodsProcurred(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods);

	List<Object[]> getTaxRateWiseDistribution(String fy,
			List<String> listOfRecepGstin, List<String> returnPeriods);

	List<Object[]> getTaxInwardData(String fy, List<String> listOfRecepGstin,
			List<String> returnPeriods);

	TotalItcDetailsDto getTotalItcDetails(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	List<String> getLastUpdatedOn(String fy, List<String> listOfRecepGstin,
			List<String> returnPeriods);

}
