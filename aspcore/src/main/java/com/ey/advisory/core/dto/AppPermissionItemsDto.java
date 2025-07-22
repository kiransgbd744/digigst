package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppPermissionItemsDto {

	@Expose
	@SerializedName("id")
	private Long userId;
	
	@Expose
	@SerializedName("userName")
	private String userName;
	
	@Expose
	@SerializedName("gstr1FileUpload")
	private boolean gstr1FileUpload;
	
	@Expose
	@SerializedName("gstr1Save")
	private boolean gstr1Save;
	
	@Expose
	@SerializedName("gstr1SubmitFile")
	private boolean gstr1SubmitFile;
	
	@Expose
	@SerializedName("gstr1Reports")
	private boolean gstr1Reports;
	
	@Expose
	@SerializedName("gstr2FileUpload")
	private boolean gstr2FileUpload;
	
	@Expose
	@SerializedName("gstr22AVsPr")
	private boolean gstr22AVsPr;
	
	@Expose
	@SerializedName("gstr2GetGstr2a")
	private boolean gstr2GetGstr2a;
	
	@Expose
	@SerializedName("gstr2Reports")
	private boolean gstr2Reports;
	
	@Expose
	@SerializedName("gstr3bFileUpload")
	private boolean gstr3bFileUpload;
	
	@Expose
	@SerializedName("gstr3Save")
	private boolean gstr3Save;
	
	@Expose
	@SerializedName("gstr3SubmitFile")
	private boolean gstr3SubmitFile;
	
	@Expose
	@SerializedName("gstr3Reports")
	private boolean gstr3Reports;
	
	@Expose
	@SerializedName("gstr6FileUpload")
	private boolean gstr6FileUpload;
	
	@Expose
	@SerializedName("gstr66AVsPr")
	private boolean gstr66AVsPr;
	
	@Expose
	@SerializedName("gstr6GetGstr6a")
	private boolean gstr6GetGstr6a;
	
	@Expose
	@SerializedName("gstr6Reports")
	private boolean gstr6Reports;
	
	@Expose
	@SerializedName("gstr9And9cYesOrNo")
	private boolean gstr9And9cYesOrNo;
	
	@Expose
	@SerializedName("dashBoardYesOrNo")
	private boolean dashBoardYesOrNo;
	
	@Expose
	@SerializedName("intutDashBoardYesOrNo")
	private boolean intutDashBoardYesOrNo;

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the gstr1FileUpload
	 */
	public boolean isGstr1FileUpload() {
		return gstr1FileUpload;
	}

	/**
	 * @param gstr1FileUpload the gstr1FileUpload to set
	 */
	public void setGstr1FileUpload(boolean gstr1FileUpload) {
		this.gstr1FileUpload = gstr1FileUpload;
	}

	/**
	 * @return the gstr1Save
	 */
	public boolean isGstr1Save() {
		return gstr1Save;
	}

	/**
	 * @param gstr1Save the gstr1Save to set
	 */
	public void setGstr1Save(boolean gstr1Save) {
		this.gstr1Save = gstr1Save;
	}

	/**
	 * @return the gstr1SubmitFile
	 */
	public boolean isGstr1SubmitFile() {
		return gstr1SubmitFile;
	}

	/**
	 * @param gstr1SubmitFile the gstr1SubmitFile to set
	 */
	public void setGstr1SubmitFile(boolean gstr1SubmitFile) {
		this.gstr1SubmitFile = gstr1SubmitFile;
	}

	/**
	 * @return the gstr1Reports
	 */
	public boolean isGstr1Reports() {
		return gstr1Reports;
	}

	/**
	 * @param gstr1Reports the gstr1Reports to set
	 */
	public void setGstr1Reports(boolean gstr1Reports) {
		this.gstr1Reports = gstr1Reports;
	}

	/**
	 * @return the gstr2FileUpload
	 */
	public boolean isGstr2FileUpload() {
		return gstr2FileUpload;
	}

	/**
	 * @param gstr2FileUpload the gstr2FileUpload to set
	 */
	public void setGstr2FileUpload(boolean gstr2FileUpload) {
		this.gstr2FileUpload = gstr2FileUpload;
	}

	/**
	 * @return the gstr22AVsPr
	 */
	public boolean isGstr22AVsPr() {
		return gstr22AVsPr;
	}

	/**
	 * @param gstr22aVsPr the gstr22AVsPr to set
	 */
	public void setGstr22AVsPr(boolean gstr22aVsPr) {
		gstr22AVsPr = gstr22aVsPr;
	}

	/**
	 * @return the gstr2GetGstr2a
	 */
	public boolean isGstr2GetGstr2a() {
		return gstr2GetGstr2a;
	}

	/**
	 * @param gstr2GetGstr2a the gstr2GetGstr2a to set
	 */
	public void setGstr2GetGstr2a(boolean gstr2GetGstr2a) {
		this.gstr2GetGstr2a = gstr2GetGstr2a;
	}

	/**
	 * @return the gstr2Reports
	 */
	public boolean isGstr2Reports() {
		return gstr2Reports;
	}

	/**
	 * @param gstr2Reports the gstr2Reports to set
	 */
	public void setGstr2Reports(boolean gstr2Reports) {
		this.gstr2Reports = gstr2Reports;
	}

	/**
	 * @return the gstr3bFileUpload
	 */
	public boolean isGstr3bFileUpload() {
		return gstr3bFileUpload;
	}

	/**
	 * @param gstr3bFileUpload the gstr3bFileUpload to set
	 */
	public void setGstr3bFileUpload(boolean gstr3bFileUpload) {
		this.gstr3bFileUpload = gstr3bFileUpload;
	}

	/**
	 * @return the gstr3Save
	 */
	public boolean isGstr3Save() {
		return gstr3Save;
	}

	/**
	 * @param gstr3Save the gstr3Save to set
	 */
	public void setGstr3Save(boolean gstr3Save) {
		this.gstr3Save = gstr3Save;
	}

	/**
	 * @return the gstr3SubmitFile
	 */
	public boolean isGstr3SubmitFile() {
		return gstr3SubmitFile;
	}

	/**
	 * @param gstr3SubmitFile the gstr3SubmitFile to set
	 */
	public void setGstr3SubmitFile(boolean gstr3SubmitFile) {
		this.gstr3SubmitFile = gstr3SubmitFile;
	}

	/**
	 * @return the gstr3Reports
	 */
	public boolean isGstr3Reports() {
		return gstr3Reports;
	}

	/**
	 * @param gstr3Reports the gstr3Reports to set
	 */
	public void setGstr3Reports(boolean gstr3Reports) {
		this.gstr3Reports = gstr3Reports;
	}

	/**
	 * @return the gstr6FileUpload
	 */
	public boolean isGstr6FileUpload() {
		return gstr6FileUpload;
	}

	/**
	 * @param gstr6FileUpload the gstr6FileUpload to set
	 */
	public void setGstr6FileUpload(boolean gstr6FileUpload) {
		this.gstr6FileUpload = gstr6FileUpload;
	}

	/**
	 * @return the gstr66AVsPr
	 */
	public boolean isGstr66AVsPr() {
		return gstr66AVsPr;
	}

	/**
	 * @param gstr66aVsPr the gstr66AVsPr to set
	 */
	public void setGstr66AVsPr(boolean gstr66aVsPr) {
		gstr66AVsPr = gstr66aVsPr;
	}

	/**
	 * @return the gstr6GetGstr6a
	 */
	public boolean isGstr6GetGstr6a() {
		return gstr6GetGstr6a;
	}

	/**
	 * @param gstr6GetGstr6a the gstr6GetGstr6a to set
	 */
	public void setGstr6GetGstr6a(boolean gstr6GetGstr6a) {
		this.gstr6GetGstr6a = gstr6GetGstr6a;
	}

	/**
	 * @return the gstr6Reports
	 */
	public boolean isGstr6Reports() {
		return gstr6Reports;
	}

	/**
	 * @param gstr6Reports the gstr6Reports to set
	 */
	public void setGstr6Reports(boolean gstr6Reports) {
		this.gstr6Reports = gstr6Reports;
	}

	/**
	 * @return the gstr9And9cYesOrNo
	 */
	public boolean isGstr9And9cYesOrNo() {
		return gstr9And9cYesOrNo;
	}

	/**
	 * @param gstr9And9cYesOrNo the gstr9And9cYesOrNo to set
	 */
	public void setGstr9And9cYesOrNo(boolean gstr9And9cYesOrNo) {
		this.gstr9And9cYesOrNo = gstr9And9cYesOrNo;
	}

	/**
	 * @return the dashBoardYesOrNo
	 */
	public boolean isDashBoardYesOrNo() {
		return dashBoardYesOrNo;
	}

	/**
	 * @param dashBoardYesOrNo the dashBoardYesOrNo to set
	 */
	public void setDashBoardYesOrNo(boolean dashBoardYesOrNo) {
		this.dashBoardYesOrNo = dashBoardYesOrNo;
	}

	/**
	 * @return the intutDashBoardYesOrNo
	 */
	public boolean isIntutDashBoardYesOrNo() {
		return intutDashBoardYesOrNo;
	}

	/**
	 * @param intutDashBoardYesOrNo the intutDashBoardYesOrNo to set
	 */
	public void setIntutDashBoardYesOrNo(boolean intutDashBoardYesOrNo) {
		this.intutDashBoardYesOrNo = intutDashBoardYesOrNo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AppPermissionItemsDto [userName=" + userName
				+ ", gstr1FileUpload=" + gstr1FileUpload + ", gstr1Save="
				+ gstr1Save + ", gstr1SubmitFile=" + gstr1SubmitFile
				+ ", gstr1Reports=" + gstr1Reports + ", gstr2FileUpload="
				+ gstr2FileUpload + ", gstr22AVsPr=" + gstr22AVsPr
				+ ", gstr2GetGstr2a=" + gstr2GetGstr2a + ", gstr2Reports="
				+ gstr2Reports + ", gstr3bFileUpload=" + gstr3bFileUpload
				+ ", gstr3Save=" + gstr3Save + ", gstr3SubmitFile="
				+ gstr3SubmitFile + ", gstr3Reports=" + gstr3Reports
				+ ", gstr6FileUpload=" + gstr6FileUpload + ", gstr66AVsPr="
				+ gstr66AVsPr + ", gstr6GetGstr6a=" + gstr6GetGstr6a
				+ ", gstr6Reports=" + gstr6Reports + ", gstr9And9cYesOrNo="
				+ gstr9And9cYesOrNo + ", dashBoardYesOrNo=" + dashBoardYesOrNo
				+ ", intutDashBoardYesOrNo=" + intutDashBoardYesOrNo + "]";
	}
	
}
