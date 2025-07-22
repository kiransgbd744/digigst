package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1ExpwpAndExpwopData {

	@Expose
	@SerializedName("errorDetails")
    private GstnErrorCodeDto errorDetails;
	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;
	@Expose
	@SerializedName("doctyp")
	private String doctyp;
	
	/**
	 * @return the doctyp
	 */
	public String getDoctyp() {
		return doctyp;
	}

	/**
	 * @return the errorDetails
	 */
	public GstnErrorCodeDto getErrorDetails() {
		return errorDetails;
	}

	/**
	 * @param errorDetails the errorDetails to set
	 */
	public void setErrorDetails(GstnErrorCodeDto errorDetails) {
		this.errorDetails = errorDetails;
	}

	/**
	 * @param doctyp the doctyp to set
	 */
	public void setDoctyp(String doctyp) {
		this.doctyp = doctyp;
	}

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
	@SerializedName("docs")
	private List<Anx1ExpwpAndExpwopDocumentData> docs;

	@Expose
	@SerializedName("doc")
	private Anx1ExpwpAndExpwopDocumentDetails doc;


	/**
	 * @return the docs
	 */
	public List<Anx1ExpwpAndExpwopDocumentData> getDocs() {
		return docs;
	}

	/**
	 * @param docs the docs to set
	 */
	public void setDocs(List<Anx1ExpwpAndExpwopDocumentData> docs) {
		this.docs = docs;
	}

	/**
	 * @return the doc
	 */
	public Anx1ExpwpAndExpwopDocumentDetails getDoc() {
		return doc;
	}

	/**
	 * @param doc the doc to set
	 */
	public void setDoc(Anx1ExpwpAndExpwopDocumentDetails doc) {
		this.doc = doc;
	}
	

}
