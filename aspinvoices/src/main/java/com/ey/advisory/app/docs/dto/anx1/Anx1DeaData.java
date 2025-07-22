package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Anand3.M
 *
 */
public class Anx1DeaData {
	@Expose
	@SerializedName("docs")
	private List<Anx1B2baDocumentData> docs;

	public List<Anx1B2baDocumentData> getDocs() {
		return docs;
	}

	public void setDocs(List<Anx1B2baDocumentData> docs) {
		this.docs = docs;
	}

}
