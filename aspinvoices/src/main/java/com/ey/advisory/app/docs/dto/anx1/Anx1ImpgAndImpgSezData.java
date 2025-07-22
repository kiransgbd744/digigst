package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1ImpgAndImpgSezData {

	@Expose
	@SerializedName("ctin")
	private String cgstin;

	@Expose
	@SerializedName("pos")
	private String impgPos;
	
	@Expose
	@SerializedName("docs")
	private List<Anx1ImpgAndImpgSezDocumentData> docs;

	public String getCgstin() {
		return cgstin;
	}

	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	public String getImpgPos() {
		return impgPos;
	}

	public void setImpgPos(String impgPos) {
		this.impgPos = impgPos;
	}

	public List<Anx1ImpgAndImpgSezDocumentData> getDocs() {
		return docs;
	}

	public void setDocs(List<Anx1ImpgAndImpgSezDocumentData> docs) {
		this.docs = docs;
	}

}
