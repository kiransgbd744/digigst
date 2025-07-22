package com.ey.advisory.app.itcmatching.vendorupload;

import java.util.List;


public class GstinSearchResponse  {
	
	private List<VendorMappingRespDto> paginatedList;
    private List<VendorMappingRespDto> activeGstinStatusSummeryResult;
    private List<VendorMappingRespDto> suspendedGstinStatusSummeryResult;
    private List<VendorMappingRespDto> cancelledGstinStatusSummeryResult;
    private List<VendorMappingRespDto> inActiveGstinStatusSummeryResult;
    private List<VendorMappingRespDto> eInvApplicableStatusSummeryResult;
    private List<VendorMappingRespDto> eInvNotApplicableStatusSummeryResult;
    private int totalCount;

    // Constructors, getters, and setters

    public List<VendorMappingRespDto> getPaginatedList() {
		return paginatedList;
	}

	public List<VendorMappingRespDto> getActiveGstinStatusSummeryResult() {
		return activeGstinStatusSummeryResult;
	}

	public List<VendorMappingRespDto> getSuspendedGstinStatusSummeryResult() {
		return suspendedGstinStatusSummeryResult;
	}

	public List<VendorMappingRespDto> getCancelledGstinStatusSummeryResult() {
		return cancelledGstinStatusSummeryResult;
	}

	public List<VendorMappingRespDto> getInActiveGstinStatusSummeryResult() {
		return inActiveGstinStatusSummeryResult;
	}

	public List<VendorMappingRespDto> getEInvApplicableStatusSummeryResult() {
		return eInvApplicableStatusSummeryResult;
	}

	public List<VendorMappingRespDto> getEInvNotApplicableStatusSummeryResult() {
		return eInvNotApplicableStatusSummeryResult;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setPaginatedList(List<VendorMappingRespDto> paginatedList) {
		this.paginatedList = paginatedList;
	}

	public void setActiveGstinStatusSummeryResult(
			List<VendorMappingRespDto> activeGstinStatusSummeryResult) {
		this.activeGstinStatusSummeryResult = activeGstinStatusSummeryResult;
	}

	public void setSuspendedGstinStatusSummeryResult(
			List<VendorMappingRespDto> suspendedGstinStatusSummeryResult) {
		this.suspendedGstinStatusSummeryResult = suspendedGstinStatusSummeryResult;
	}

	public void setCancelledGstinStatusSummeryResult(
			List<VendorMappingRespDto> cancelledGstinStatusSummeryResult) {
		this.cancelledGstinStatusSummeryResult = cancelledGstinStatusSummeryResult;
	}

	public void setInActiveGstinStatusSummeryResult(
			List<VendorMappingRespDto> inActiveGstinStatusSummeryResult) {
		this.inActiveGstinStatusSummeryResult = inActiveGstinStatusSummeryResult;
	}

	public void seteInvApplicableStatusSummeryResult(
			List<VendorMappingRespDto> eInvApplicableStatusSummeryResult) {
		this.eInvApplicableStatusSummeryResult = eInvApplicableStatusSummeryResult;
	}

	public void seteInvNotApplicableStatusSummeryResult(
			List<VendorMappingRespDto> eInvNotApplicableStatusSummeryResult) {
		this.eInvNotApplicableStatusSummeryResult = eInvNotApplicableStatusSummeryResult;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public GstinSearchResponse(List<VendorMappingRespDto> paginatedList,
                               List<VendorMappingRespDto> activeGstinStatusSummeryResult,
                               List<VendorMappingRespDto> suspendedGstinStatusSummeryResult,
                               List<VendorMappingRespDto> cancelledGstinStatusSummeryResult,
                               List<VendorMappingRespDto> inActiveGstinStatusSummeryResult,
                               List<VendorMappingRespDto> eInvApplicableStatusSummeryResult,
                               List<VendorMappingRespDto> eInvNotApplicableStatusSummeryResult,
                               int totalCount) {
        this.paginatedList = paginatedList;
        this.activeGstinStatusSummeryResult = activeGstinStatusSummeryResult;
        this.suspendedGstinStatusSummeryResult = suspendedGstinStatusSummeryResult;
        this.cancelledGstinStatusSummeryResult = cancelledGstinStatusSummeryResult;
        this.inActiveGstinStatusSummeryResult = inActiveGstinStatusSummeryResult;
        this.eInvApplicableStatusSummeryResult = eInvApplicableStatusSummeryResult;
        this.eInvNotApplicableStatusSummeryResult = eInvNotApplicableStatusSummeryResult;
        this.totalCount = totalCount;
    }
}
