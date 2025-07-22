package com.ey.advisory.app.services.common;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GSTConstants;

@Component("CdnrDefaultDocumentTypeFactoryImpl")
public class CdnrDefaultDocumentTypeFactoryImpl
		implements DocumentTypeMapperFactory {

	@Override
	public String mapDocType(String invoiceData) {
		String data = null;
		if ("C".equalsIgnoreCase(invoiceData)) {
			data = GSTConstants.CR;
			return data;
		}
		if ("D".equalsIgnoreCase(invoiceData)) {
			data = GSTConstants.DR;
			return data;
		}
		if ("R".equalsIgnoreCase(invoiceData)) {
			data = GSTConstants.RFV;
			return data;
		}
		return data;

	}
}