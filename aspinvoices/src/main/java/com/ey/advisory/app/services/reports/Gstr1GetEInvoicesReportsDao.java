package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.GSTR1GetEInvoicesTablesDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr1GetEInvoicesReportsDao {
	List<GSTR1GetEInvoicesTablesDto> getGstr1EIReports(SearchCriteria criteria);

}
