package com.ey.advisory.app.services.dashboard.fiori;

import java.util.List;

import com.ey.advisory.app.gstr3b.Gstr3bMonthlyTrendTaxAmountsEntity;

public interface Gstr3BOutwardFioriDashboardService {

	 Gstr3bHeaderGraphDetailsDto getHeaderGraphData(String fy,
			List<String> listOfRecepGstin, List<String> listOfReturnPrds);
	 
	 List<Gstr3bMonthlyTrendTaxAmountsEntity> getTableData(String fy,
				List<String> listOfRecepGstin, List<String> listOfReturnPrds);

	
}
