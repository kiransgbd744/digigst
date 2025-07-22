package com.ey.advisory.app.services.reports;

import java.util.List;

import com.ey.advisory.app.data.views.client.GSTR1GetEInvoicesEntityDto;
import com.ey.advisory.core.search.SearchCriteria;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr1GetEInvoicesEntityDao {
	List<GSTR1GetEInvoicesEntityDto> getGstr1EIReports(SearchCriteria criteria);

}
