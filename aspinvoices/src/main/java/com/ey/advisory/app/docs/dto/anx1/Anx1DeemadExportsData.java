package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1DeemadExportsData {

	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;
	
	/**
	 * @return the error_msg
	 */
	public String getError_msg() {
		return error_msg;
	}

	/**
	 * @param error_msg the error_msg to set
	 */
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}

	/**
	 * @return the error_cd
	 */
	public String getError_cd() {
		return error_cd;
	}

	/**
	 * @param error_cd the error_cd to set
	 */
	public void setError_cd(String error_cd) {
		this.error_cd = error_cd;
	}

	@Expose
	@SerializedName("ctin")
	private String cgstin;

	@Expose
	@SerializedName("docs")
	private List<Anx1DeemadExportsDocumentData> docs;

	public String getCgstin() {
		return cgstin;
	}

	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	public List<Anx1DeemadExportsDocumentData> getDocs() {
		return docs;
	}

	public void setDocs(List<Anx1DeemadExportsDocumentData> docs) {
		this.docs = docs;
	}

}
