package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;

/**
 * 
 * @author Anand3.M
 *
 */
public class DuplicateInwardDocCheckDto {
	
	private List<InwardTransDocument> docs;
	
	private List<InwardTransDocument> gstnSubmittedDocs;

	/**
	 * @return the docs
	 */
	public List<InwardTransDocument> getDocs() {
		return docs;
	}

	/**
	 * @param docs the docs to set
	 */
	public void setDocs(List<InwardTransDocument> docs) {
		this.docs = docs;
	}

	/**
	 * @return the gstnSubmittedDocs
	 */
	public List<InwardTransDocument> getGstnSubmittedDocs() {
		return gstnSubmittedDocs;
	}

	/**
	 * @param gstnSubmittedDocs the gstnSubmittedDocs to set
	 */
	public void setGstnSubmittedDocs(
			List<InwardTransDocument> gstnSubmittedDocs) {
		this.gstnSubmittedDocs = gstnSubmittedDocs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"DuplicateDocCheckDto [docs=%s, gstnSubmittedDocs=%s]", docs,
				gstnSubmittedDocs);
	}
	
}
