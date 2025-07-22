/**
 * 
 */
package com.ey.advisory.app.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Laxmi.Salukuti
 *
 */
@Setter
@Getter
public class AsyncInvManagementReportConfig {

	private String headersKey;

	private String columnMappingsKey;

	private AsyncInvMangmntReportConvertor reportConvertor;

	public AsyncInvManagementReportConfig() {
	}

	public AsyncInvManagementReportConfig(String headersKey,
			String columnMappingsKey,
			AsyncInvMangmntReportConvertor reportConvertor) {

		super();
		this.headersKey = headersKey;
		this.columnMappingsKey = columnMappingsKey;
		this.reportConvertor = reportConvertor;
	}

}
