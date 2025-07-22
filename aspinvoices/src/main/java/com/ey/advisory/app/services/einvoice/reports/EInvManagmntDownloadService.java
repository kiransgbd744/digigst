/**
 * 
 */
package com.ey.advisory.app.services.einvoice.reports;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface EInvManagmntDownloadService {

	public void findEInvMngmtdwnld(SearchCriteria criteria, String fullPath);

}
