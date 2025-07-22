/**
 * 
 */
package com.ey.advisory.app.services.einvoice.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.search.SearchCriteria;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("EInvManagmntDownloadHandler")
@Slf4j
public class EInvManagmntDownloadHandler {

	@Autowired
	@Qualifier("EInvManagmntDownloadServiceImpl")
	private EInvManagmntDownloadService eInvManagmntDownloadService;

	public void downloadEInvMngmtScreen(SearchCriteria criteria,
			String fullPath) {

		eInvManagmntDownloadService.findEInvMngmtdwnld(criteria, fullPath);

	}
}