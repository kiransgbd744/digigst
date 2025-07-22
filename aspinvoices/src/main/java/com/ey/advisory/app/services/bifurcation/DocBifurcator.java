package com.ey.advisory.app.services.bifurcation;

import com.ey.advisory.common.Document;
import com.ey.advisory.common.ProcessingContext;

/**
 * The implementation of this class is responsible for bifurcating the document.
 * Bifurcation is the process of tagging the document with 2 different 
 * attributes - One is the table section in the government GST returns form 
 * (e.g. 4A, 6B, 5A etc) and the second is the section name under which the 
 * GST Tax data for the document will be included in the final JSON used for
 * saving the tax returns to GSTN site (e.g. B2B, B2CS, B2CL, CDNR, CDNRA etc). 
 * 
 * @author Sai.Pakanati
 *
 * @param <T>
 */
public interface DocBifurcator<T extends Document> {
	
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
