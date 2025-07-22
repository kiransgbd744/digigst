package com.ey.advisory.app.services.common;

import org.springframework.stereotype.Component;

@Component("ExpDefaultDocumentTypeFactoryImpl")
public class ExpDefaultDocumentTypeFactoryImpl 
                                       implements DocumentTypeMapperFactory{

	@Override
	public String mapDocType(String invoiceData) {
		// TODO Auto-generated method stub
		return "INV";
	}

}
