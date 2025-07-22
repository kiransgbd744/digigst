/**
 * 
 */
package com.ey.advisory.app.services.doc.gstr1a;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
public interface Gstr1AEInvoiceOutwardSupplyTypeResolver {

	public String resolve(Gstr1AOutwardTransDocument document);

}
