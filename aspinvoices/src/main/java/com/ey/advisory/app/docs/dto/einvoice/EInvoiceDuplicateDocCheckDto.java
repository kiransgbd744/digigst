/**
 * 
 */
package com.ey.advisory.app.docs.dto.einvoice;

import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;

/**
 * This class represents transferring the duplicate Doc Ids after checking for
 * them
 * 
 * @author Laxmi.Salukuti
 *
 */
public class EInvoiceDuplicateDocCheckDto {

	private List<OutwardTransDocument> docs;

	private List<OutwardTransDocument> gstnSubmittedDocs;

	/**
	 * @return the docs
	 */
	public List<OutwardTransDocument> getDocs() {
		return docs;
	}

	/**
	 * @param docs
	 *            the docs to set
	 */
	public void setDocs(List<OutwardTransDocument> docs) {
		this.docs = docs;
	}

	/**
	 * @return the gstnSubmittedDocs
	 */
	public List<OutwardTransDocument> getGstnSubmittedDocs() {
		return gstnSubmittedDocs;
	}

	/**
	 * @param gstnSubmittedDocs
	 *            the gstnSubmittedDocs to set
	 */
	public void setGstnSubmittedDocs(
			List<OutwardTransDocument> gstnSubmittedDocs) {
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
