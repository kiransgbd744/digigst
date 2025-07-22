/**
 * 
 */
package com.ey.advisory.app.common;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.StaticContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */

@Component("AsyncInvoiceManagementConfigFactoryImpl")
@Slf4j
public class AsyncInvoiceManagementConfigFactoryImpl
		implements AsyncInvoiceManagementConfigFactory {

	@Override
	public AsyncInvManagementReportConfig getReportConfig() {
		AsyncInvMangmntReportConvertor reportConvertor = null;

		Pair<String, String> mapping = CommonUtility
				.getInvMangmntReportHeaderMapping();
		String headersKey = mapping.getValue0();
		String columnMappingsKey = mapping.getValue1();

		reportConvertor = (AsyncInvMangmntReportConvertor) StaticContextHolder
				.getBean("AsyncInvoicemanagementReportMappingConvertor",
						AsyncInvMangmntReportConvertor.class);

		return new AsyncInvManagementReportConfig(headersKey, columnMappingsKey,
				reportConvertor);
	}
}
