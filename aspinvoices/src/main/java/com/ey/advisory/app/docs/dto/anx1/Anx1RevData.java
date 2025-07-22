package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1RevData {

	@Expose
	@SerializedName("ctin")
	private String cgstin;
	
	@Expose
	@SerializedName("type")
	private String type;

	@Expose
	@SerializedName("docs")
	private List<Anx1RevDocumentData> docs;

	public String getCgstin() {
		return cgstin;
	}

	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Anx1RevDocumentData> getDocs() {
		return docs;
	}

	public void setDocs(List<Anx1RevDocumentData> docs) {
		this.docs = docs;
	}

}
