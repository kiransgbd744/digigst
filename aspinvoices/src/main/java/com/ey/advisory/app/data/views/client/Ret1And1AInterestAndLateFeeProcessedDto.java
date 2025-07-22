/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class Ret1And1AInterestAndLateFeeProcessedDto {

	private String returnType;
	private String gstin;
	private String returnPeriod;
	private String returnTable;
	private String interestIGST;
	private String interestCGST;
	private String interestSGST;
	private String interestCess;
	private String lateFeeCGST;
	private String lateFeeSGST;
	private String userDefined1;
	private String userDefined2;
	private String userDefined3;
	private String aspInformationId;
	private String aspInformationDescription;

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

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

	public String getReturnTable() {
		return returnTable;
	}

	public void setReturnTable(String returnTable) {
		this.returnTable = returnTable;
	}

	public String getInterestIGST() {
		return interestIGST;
	}

	public void setInterestIGST(String interestIGST) {
		this.interestIGST = interestIGST;
	}

	public String getInterestCGST() {
		return interestCGST;
	}

	public void setInterestCGST(String interestCGST) {
		this.interestCGST = interestCGST;
	}

	public String getInterestSGST() {
		return interestSGST;
	}

	public void setInterestSGST(String interestSGST) {
		this.interestSGST = interestSGST;
	}

	public String getInterestCess() {
		return interestCess;
	}

	public void setInterestCess(String interestCess) {
		this.interestCess = interestCess;
	}

	public String getLateFeeCGST() {
		return lateFeeCGST;
	}

	public void setLateFeeCGST(String lateFeeCGST) {
		this.lateFeeCGST = lateFeeCGST;
	}

	public String getLateFeeSGST() {
		return lateFeeSGST;
	}

	public void setLateFeeSGST(String lateFeeSGST) {
		this.lateFeeSGST = lateFeeSGST;
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
		return "Ret1And1AInterestAndLateFeeProcessedDto [returnType="
				+ returnType + ", gstin=" + gstin + ", returnPeriod="
				+ returnPeriod + ", returnTable=" + returnTable
				+ ", interestIGST=" + interestIGST + ", interestCGST="
				+ interestCGST + ", interestSGST=" + interestSGST
				+ ", interestCess=" + interestCess + ", lateFeeCGST="
				+ lateFeeCGST + ", lateFeeSGST=" + lateFeeSGST
				+ ", userDefined1=" + userDefined1 + ", userDefined2="
				+ userDefined2 + ", userDefined3=" + userDefined3
				+ ", aspInformationId=" + aspInformationId
				+ ", aspInformationDescription=" + aspInformationDescription
				+ "]";
	}

}
