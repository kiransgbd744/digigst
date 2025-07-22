package com.ey.advisory.app.services.common;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.B2BInvoiceData;

@Component("B2bDefaultDocumentTypeFactoryImpl")
public class B2bDefaultDocumentTypeFactoryImpl
		implements DocumentTypeMapperFactory {

	/**
	 * Right now, since we're dealing with only B2B documents, the invoice type
	 * will always be 'INV'. 
	 */
	@Override
	public String mapDocType(String invoiceData) {
		return "INV";
	}

}
