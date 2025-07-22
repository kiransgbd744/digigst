package com.ey.advisory.app.docs.dto.gstr6;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6SummaryDto {

	@Expose
	@SerializedName("totalItc")
	private Gstr6SummaryDetails totalItc;

	@Expose
	@SerializedName("inelgitc")
	private Gstr6SummaryDetails inelgItc;

	@Expose
	@SerializedName("elgitc")
	private Gstr6SummaryDetails elgItc;

	@Expose
	@SerializedName("isdItcCross")
	private Gstr6SummaryItcCrossDetails isdItcCross;

	@Expose
	@SerializedName("lateFeemain")
	private Gstr6SummaryLateFeeMainDto lateFeemain;

	public Gstr6SummaryDetails getTotalItc() {
		return totalItc;
	}

	public void setTotalItc(Gstr6SummaryDetails totalItc) {
		this.totalItc = totalItc;
	}

	public Gstr6SummaryDetails getInelgItc() {
		return inelgItc;
	}

	public void setInelgItc(Gstr6SummaryDetails inelgItc) {
		this.inelgItc = inelgItc;
	}

	public Gstr6SummaryDetails getElgItc() {
		return elgItc;
	}

	public void setElgItc(Gstr6SummaryDetails elgItc) {
		this.elgItc = elgItc;
	}

	public Gstr6SummaryItcCrossDetails getIsdItcCross() {
		return isdItcCross;
	}

	public void setIsdItcCross(Gstr6SummaryItcCrossDetails isdItcCross) {
		this.isdItcCross = isdItcCross;
	}

	public Gstr6SummaryLateFeeMainDto getLateFeemain() {
		return lateFeemain;
	}

	public void setLateFeemain(Gstr6SummaryLateFeeMainDto lateFeemain) {
		this.lateFeemain = lateFeemain;
	}

}
