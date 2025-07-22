package com.ey.advisory.app.docs.dto.anx2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx2DocumentDetails {

	@Expose
	@SerializedName("num")
	private String docNum;

	@Expose
	@SerializedName("dt")
	private String docDate;

	@Expose
	@SerializedName("val")
	private String docVal;

	public String getDocNum() {
		return docNum;
	}

	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}

	public String getDocDate() {
		return docDate;
	}

	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	public String getDocVal() {
		return docVal;
	}

	public void setDocVal(String docVal) {
		this.docVal = docVal;
	}

}
