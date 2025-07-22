package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;

public class Anx1DataStatusRespDto {
	
	
	private LocalDate docDate;
	private LocalDate receivedDate;
	private Integer sapTotal=0;
	private Integer totalRecords=0;
	private Integer diff=0;
	private Integer processedActive=0;
	private Integer processedInactive=0;
	private Integer errorActive=0;
	private Integer errorInactive=0;
	private Integer infoActive=0;
	private Integer infoInactive=0;
	public LocalDate getDocDate() {
		return docDate;
	}
	public void setDocDate(LocalDate docDate) {
		this.docDate = docDate;
	}
	public LocalDate getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(LocalDate receivedDate) {
		this.receivedDate = receivedDate;
	}
	public Integer getSapTotal() {
		return sapTotal;
	}
	public void setSapTotal(Integer sapTotal) {
		this.sapTotal = sapTotal;
	}
	public Integer getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}
	public Integer getDiff() {
		return diff;
	}
	public void setDiff(Integer diff) {
		this.diff = diff;
	}
	public Integer getProcessedActive() {
		return processedActive;
	}
	public void setProcessedActive(Integer processedActive) {
		this.processedActive = processedActive;
	}
	public Integer getProcessedInactive() {
		return processedInactive;
	}
	public void setProcessedInactive(Integer processedInactive) {
		this.processedInactive = processedInactive;
	}
	public Integer getErrorActive() {
		return errorActive;
	}
	public void setErrorActive(Integer errorActive) {
		this.errorActive = errorActive;
	}
	public Integer getErrorInactive() {
		return errorInactive;
	}
	public void setErrorInactive(Integer errorInactive) {
		this.errorInactive = errorInactive;
	}
	public Integer getInfoActive() {
		return infoActive;
	}
	public void setInfoActive(Integer infoActive) {
		this.infoActive = infoActive;
	}
	public Integer getInfoInactive() {
		return infoInactive;
	}
	public void setInfoInactive(Integer infoInactive) {
		this.infoInactive = infoInactive;
	}
	
		

}
