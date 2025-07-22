package com.ey.advisory.app.services.refidpolling.gstr1;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.common.DocumentTypeMapperFactory;
import com.ey.advisory.common.GSTConstants;

/**
 * @author Siva.Nandam
 *
 */
@Component("CdnraDefaultDocumentTypeFactoryImpl")
public class CdnraDefaultDocumentTypeFactoryImpl 
              implements DocumentTypeMapperFactory {

	@Override
	public String mapDocType(String invoiceData) {
		String data = null;
		if ("C".equalsIgnoreCase(invoiceData)) {
			data = GSTConstants.RCR;
			return data;
		}
		if ("D".equalsIgnoreCase(invoiceData)) {
			data = GSTConstants.RDR;
			return data;
		}
		if ("R".equalsIgnoreCase(invoiceData)) {
			data = GSTConstants.RRFV;
			return data;
		}
		return data;

	}
}