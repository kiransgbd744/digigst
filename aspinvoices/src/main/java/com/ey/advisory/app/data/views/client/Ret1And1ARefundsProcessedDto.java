/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Ret1And1ARefundsProcessedDto {

	private String gstin;
	private String returnPeriod;
	private String description;
	private String tax;
	private String interest;
	private String penalty;
	private String fee;
	private String other;
	private String total;
	private String userDefined1;
	private String userDefined2;
	private String userDefined3;
	private String aspInformationId;
	private String aspInformationDescription;

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public String getPenalty() {
		return penalty;
	}

	public void setPenalty(String penalty) {
		this.penalty = penalty;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getUserDefined1() {
		return userDefined1;
	}

	public void setUserDefined1(String userDefined1) {
		this.userDefined1 = userDefined1;
	}

	public String getUserDefined2() {
		return userDefined2;
	}

	public void setUserDefined2(String userDefined2) {
		this.userDefined2 = userDefined2;
	}

	public String getUserDefined3() {
		return userDefined3;
	}

	public void setUserDefined3(String userDefined3) {
		this.userDefined3 = userDefined3;
	}

	public String getAspInformationId() {
		return aspInformationId;
	}

	public void setAspInformationId(String aspInformationId) {
		this.aspInformationId = aspInformationId;
	}

	public String getAspInformationDescription() {
		return aspInformationDescription;
	}

	public void setAspInformationDescription(String aspInformationDescription) {
		this.aspInformationDescription = aspInformationDescription;
	}

	@Override
	public String toString() {
		return "Ret1And1ARefundsProcessedDto [gstin=" + gstin
				+ ", returnPeriod=" + returnPeriod + ", description="
				+ description + ", tax=" + tax + ", interest=" + interest
				+ ", penalty=" + penalty + ", fee=" + fee + ", other=" + other
				+ ", total=" + total + ", userDefined1=" + userDefined1
				+ ", userDefined2=" + userDefined2 + ", userDefined3="
				+ userDefined3 + ", aspInformationId=" + aspInformationId
				+ ", aspInformationDescription=" + aspInformationDescription
				+ "]";
	}

}
