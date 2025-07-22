/**
 * 
 */
package com.ey.advisory.app.services.docs.einvoice;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;

/**
 * 
 * @author Laxmi.Salukuti
 *
 */
public interface EInvoiceOutwardSupplyTypeResolver {

	public String resolve(OutwardTransDocument document);

}
