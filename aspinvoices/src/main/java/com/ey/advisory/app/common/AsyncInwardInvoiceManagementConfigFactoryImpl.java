package com.ey.advisory.app.common;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.StaticContextHolder;

/**
 * @author Mahesh.Golla
 *
 */

@Component("AsyncInwardInvoiceManagementConfigFactoryImpl")
public class AsyncInwardInvoiceManagementConfigFactoryImpl
		implements AsyncInvoiceManagementConfigFactory {

	@Override
	public AsyncInvManagementReportConfig getReportConfig() {
		AsyncInvMangmntReportConvertor reportConvertor = null;

		Pair<String, String> mapping = CommonUtility
				.getInwardInvMangmntReportHeaderMapping();
		String headersKey = mapping.getValue0();
		String columnMappingsKey = mapping.getValue1();

		reportConvertor = (AsyncInvMangmntReportConvertor) StaticContextHolder
				.getBean("AsyncInwardInvoicemanagementReportMappingConvertor",
						AsyncInvMangmntReportConvertor.class);

		return new AsyncInvManagementReportConfig(headersKey, columnMappingsKey,
				reportConvertor);
	}
}
