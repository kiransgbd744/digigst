package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1ImpsData {

	@Expose
	@SerializedName("docs")
	private List<Anx1ImpsDocumentData> docs;

	public List<Anx1ImpsDocumentData> getDocs() {
		return docs;
	}

	public void setDocs(List<Anx1ImpsDocumentData> docs) {
		this.docs = docs;
	}

}
