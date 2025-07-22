package com.ey.advisory.app.services.docs.gstr7;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.common.ProcessingContext;

/**
 * The implementation of this class is responsible for bifurcating the document.
 * Bifurcation is the process of tagging the document with 2 different 
 * attributes - One is the table section in the government GST returns form 
 * (e.g. 4A, 6B, 5A etc) and the second is the section name under which the 
 * GST Tax data for the document will be included in the final JSON used for
 * saving the tax returns to GSTN site (e.g. B2B, B2CS, B2CL, CDNR, CDNRA etc). 
 * 
 * @author Siva.Reddy
 *
 * @param <T>
 */
public interface Gstr7TransDocBifurcator<T extends Gstr7TransDocHeaderEntity> {
	
	/**
	 * 
	 * @param document
	 * @param context
	 * @return
	 */
	public abstract T bifurcate(
				T document, ProcessingContext context);
	
	public boolean isBifurcated(T doc);

}
