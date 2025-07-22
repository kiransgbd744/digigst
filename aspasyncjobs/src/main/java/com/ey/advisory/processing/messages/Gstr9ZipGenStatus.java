package com.ey.advisory.processing.messages;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tblGstr9ZipGenStatus")
public class Gstr9ZipGenStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long ID;

	@Column(name = "Gstin")
	private String gstin;

	@Column(name = "FinYear")
	private String finYear;

	@Column(name = "InvoiceType")
	private String invType;

	@Column(name = "ReturnType")
	private String returnType;

	@Column(name = "StTaxPeriod")
	private String stTaxPeriod;

	@Column(name = "EndTaxPeriod")
	private String endTaxPeriod;

	@Column(name = "ZipFilePath")
	private String zipFilePath;

	@Column(name = "CreatedDate")
	private Date createdDate;

	@Column(name = "UpdatedDate")
	private Date updatedDate;
	
	@Column(name = "JobStatus")
	private String jobStatus;

	public Gstr9ZipGenStatus() {
	}

	public Gstr9ZipGenStatus(String gstin, String finYear, String invType,
			String stTaxPeriod, String endTaxPeriod, String zipFilePath,
			String returnType, Date createdDate, Date updatedDate) {
		super();
		this.gstin = gstin;
		this.finYear = finYear;
		this.invType = invType;
		this.stTaxPeriod = stTaxPeriod;
		this.endTaxPeriod = endTaxPeriod;
		this.zipFilePath = zipFilePath;
		this.returnType = returnType;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
	}
	
	public Gstr9ZipGenStatus(String gstin, String finYear, String invType,
			String stTaxPeriod, String endTaxPeriod, String zipFilePath,
			String returnType, Date createdDate, Date updatedDate, 
			String jobStatus) {
		super();
		this.gstin = gstin;
		this.finYear = finYear;
		this.invType = invType;
		this.stTaxPeriod = stTaxPeriod;
		this.endTaxPeriod = endTaxPeriod;
		this.zipFilePath = zipFilePath;
		this.returnType = returnType;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.jobStatus = jobStatus;
				
	}

	public Long getID() {
		return ID;
	}

	public void setID(Long iD) {
		ID = iD;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getFinYear() {
		return finYear;
	}

	public void setFinYear(String finYear) {
		this.finYear = finYear;
	}

	public String getInvType() {
		return invType;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public String getStTaxPeriod() {
		return stTaxPeriod;
	}

	public void setStTaxPeriod(String stTaxPeriod) {
		this.stTaxPeriod = stTaxPeriod;
	}

	public String getEndTaxPeriod() {
		return endTaxPeriod;
	}

	public void setEndTaxPeriod(String endTaxPeriod) {
		this.endTaxPeriod = endTaxPeriod;
	}

	public String getZipFilePath() {
		return zipFilePath;
	}

	public void setZipFilePath(String zipFilePath) {
		this.zipFilePath = zipFilePath;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	@Override
	public String toString() {
		return "Gstr9ZipGenStatus [ID=" + ID + ", gstin=" + gstin + ", finYear="
				+ finYear + ", invType=" + invType + ", returnType="
				+ returnType + ", stTaxPeriod=" + stTaxPeriod
				+ ", endTaxPeriod=" + endTaxPeriod + ", zipFilePath="
				+ zipFilePath + ", createdDate=" + createdDate
				+ ", updatedDate=" + updatedDate + "]";
	}

}
