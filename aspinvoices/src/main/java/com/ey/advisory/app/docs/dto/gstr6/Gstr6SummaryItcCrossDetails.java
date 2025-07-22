package com.ey.advisory.app.docs.dto.gstr6;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6SummaryItcCrossDetails {

	@Expose
	@SerializedName("iamti")
	private BigDecimal iamti;

	@Expose
	@SerializedName("iamtc")
	private BigDecimal iamtc;

	@Expose
	@SerializedName("iamts")
	private BigDecimal iamts;

	@Expose
	@SerializedName("camti")
	private BigDecimal camti;

	@Expose
	@SerializedName("camtc")
	private BigDecimal camtc;

	@Expose
	@SerializedName("samti")
	private BigDecimal samti;

	@Expose
	@SerializedName("samts")
	private BigDecimal samts;

	@Expose
	@SerializedName("cess")
	private BigDecimal cess;

	@Expose
	@SerializedName("iamt")
	private BigDecimal iamt;

	@Expose
	@SerializedName("camt")
	private BigDecimal camt;

	@Expose
	@SerializedName("samt")
	private BigDecimal sgstAmount;

	@Expose
	@SerializedName("csamt")
	private BigDecimal cessAmount;

	public BigDecimal getIamti() {
		return iamti;
	}

	public void setIamti(BigDecimal iamti) {
		this.iamti = iamti;
	}

	public BigDecimal getIamtc() {
		return iamtc;
	}

	public void setIamtc(BigDecimal iamtc) {
		this.iamtc = iamtc;
	}

	public BigDecimal getIamts() {
		return iamts;
	}

	public void setIamts(BigDecimal iamts) {
		this.iamts = iamts;
	}

	public BigDecimal getCamti() {
		return camti;
	}

	public void setCamti(BigDecimal camti) {
		this.camti = camti;
	}

	public BigDecimal getCamtc() {
		return camtc;
	}

	public void setCamtc(BigDecimal camtc) {
		this.camtc = camtc;
	}

	public BigDecimal getSamti() {
		return samti;
	}

	public void setSamti(BigDecimal samti) {
		this.samti = samti;
	}

	public BigDecimal getSamts() {
		return samts;
	}

	public void setSamts(BigDecimal samts) {
		this.samts = samts;
	}

	public BigDecimal getCess() {
		return cess;
	}

	public void setCess(BigDecimal cess) {
		this.cess = cess;
	}

	public BigDecimal getIamt() {
		return iamt;
	}

	public void setIamt(BigDecimal iamt) {
		this.iamt = iamt;
	}

	public BigDecimal getCamt() {
		return camt;
	}

	public void setCamt(BigDecimal camt) {
		this.camt = camt;
	}

	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public BigDecimal getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(BigDecimal cessAmount) {
		this.cessAmount = cessAmount;
	}

}
