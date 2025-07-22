/**
 * 
 */
package com.ey.advisory.app.services.einvoice.reports;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface EInvManagmntScreenDownloadService {

	public void findEInvMngmtScreendwnld(SearchCriteria criteria,
			String fullPath);

}
