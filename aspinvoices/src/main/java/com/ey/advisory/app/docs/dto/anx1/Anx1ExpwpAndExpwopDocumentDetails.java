package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx1ExpwpAndExpwopDocumentDetails {

	@Expose
	@SerializedName("num")
	private String docNum;

	@Expose
	@SerializedName("dt")
	private String docDate;

	@Expose
	@SerializedName("val")
	private BigDecimal docVal;

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

	public BigDecimal getDocVal() {
		return docVal;
	}

	public void setDocVal(BigDecimal docVal) {
		this.docVal = docVal;
	}

}
