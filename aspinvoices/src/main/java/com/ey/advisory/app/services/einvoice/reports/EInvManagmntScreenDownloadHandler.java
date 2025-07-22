/**
 * 
 */
package com.ey.advisory.app.services.einvoice.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.search.SearchCriteria;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("EInvManagmntScreenDownloadHandler")
public class EInvManagmntScreenDownloadHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EInvManagmntScreenDownloadHandler.class);

	@Autowired
	@Qualifier("EInvManagmntScreenDownloadServiceImpl")
	private EInvManagmntScreenDownloadService einvManagmntScreenDownloadService;

	public void downloadEInvMngmtScreen(SearchCriteria criteria,
			String fullPath) {

		einvManagmntScreenDownloadService.findEInvMngmtScreendwnld(criteria,
				fullPath);

	}
}
