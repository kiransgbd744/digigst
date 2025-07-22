/**
 * 
 */
package com.ey.advisory.app.services.doc.gstr1a;

import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;

/**
 * This class represents transferring the duplicate Doc Ids after checking for
 * them
 * 
 * @author Shashikant.Shukla
 *
 */
public class Gstr1AEInvoiceDuplicateDocCheckDto {

	private List<Gstr1AOutwardTransDocument> docs;

	private List<Gstr1AOutwardTransDocument> gstnSubmittedDocs;

	/**
	 * @return the docs
	 */
	public List<Gstr1AOutwardTransDocument> getDocs() {
		return docs;
	}

	/**
	 * @param docs
	 *            the docs to set
	 */
	public void setDocs(List<Gstr1AOutwardTransDocument> docs) {
		this.docs = docs;
	}

	/**
	 * @return the gstnSubmittedDocs
	 */
	public List<Gstr1AOutwardTransDocument> getGstnSubmittedDocs() {
		return gstnSubmittedDocs;
	}

	/**
	 * @param gstnSubmittedDocs
	 *            the gstnSubmittedDocs to set
	 */
	public void setGstnSubmittedDocs(
			List<Gstr1AOutwardTransDocument> gstnSubmittedDocs) {
		this.gstnSubmittedDocs = gstnSubmittedDocs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"DuplicateDocCheckDto [docs=%s, gstnSubmittedDocs=%s]", docs,
				gstnSubmittedDocs);
	}

}
