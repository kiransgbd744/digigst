/**
 * 
 */
package com.ey.advisory.common;

import com.ey.advisory.app.data.business.dto.OutwardTransDocument;

/**
 * 
 * @author Laxmi.Salukuti
 *
 */
public interface EInvoiceOutwardSupplyTypeResolver {

	public String resolve(OutwardTransDocument document);

}
