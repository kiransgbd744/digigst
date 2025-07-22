/*package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GETGSTR1_DOCISSUED_DETAILS")
public class GetGstr1DocIssuedInvoicesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String sgstin;

	@Column(name = "RET_PERIOD")
	private String returnPeriod;

	@Column(name = "FLAG")
	private String invoiceStatus;

	@Column(name = "CHKSUM")
	private String checkSum;

	@Column(name = "DOC_NUM")
	private Integer docNum;

	@Column(name = "SERIAL_NUM")
	private Integer serialNum;

	@Column(name = "FROM_SERRIAL_NUM")
	private String fromSerialNum;

	@Column(name = "TO_SERRIAL_NUM")
	private String toSerialNum;

	@Column(name = "TOT_NUM")
	private Integer totalNum;

	@Column(name = "CANCEL")
	private Integer cancelled;

	@Column(name = "NET_ISSUE")
	private Integer netIssued;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	*//**
	 * @return the id
	 *//*
	public Long getId() {
		return id;
	}

	*//**
	 * @param id
	 *            the id to set
	 *//*
	public void setId(Long id) {
		this.id = id;
	}

	*//**
	 * @return the sgstin
	 *//*
	public String getSgstin() {
		return sgstin;
	}

	*//**
	 * @param sgstin
	 *            the sgstin to set
	 *//*
	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	*//**
	 * @return the returnPeriod
	 *//*
	public String getReturnPeriod() {
		return returnPeriod;
	}

	*//**
	 * @param returnPeriod
	 *            the returnPeriod to set
	 *//*
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	*//**
	 * @return the invoiceStatus
	 *//*
	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	*//**
	 * @param invoiceStatus
	 *            the invoiceStatus to set
	 *//*
	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	*//**
	 * @return the checkSum
	 *//*
	public String getCheckSum() {
		return checkSum;
	}

	*//**
	 * @param checkSum
	 *            the checkSum to set
	 *//*
	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	*//**
	 * @return the docNum
	 *//*
	public Integer getDocNum() {
		return docNum;
	}

	*//**
	 * @param docNum
	 *            the docNum to set
	 *//*
	public void setDocNum(Integer docNum) {
		this.docNum = docNum;
	}

	*//**
	 * @return the serialNum
	 *//*
	public Integer getSerialNum() {
		return serialNum;
	}

	*//**
	 * @param serialNum
	 *            the serialNum to set
	 *//*
	public void setSerialNum(Integer serialNum) {
		this.serialNum = serialNum;
	}

	*//**
	 * @return the fromSerialNum
	 *//*
	public String getFromSerialNum() {
		return fromSerialNum;
	}

	*//**
	 * @param fromSerialNum
	 *            the fromSerialNum to set
	 *//*
	public void setFromSerialNum(String fromSerialNum) {
		this.fromSerialNum = fromSerialNum;
	}

	*//**
	 * @return the toSerialNum
	 *//*
	public String getToSerialNum() {
		return toSerialNum;
	}

	*//**
	 * @param toSerialNum
	 *            the toSerialNum to set
	 *//*
	public void setToSerialNum(String toSerialNum) {
		this.toSerialNum = toSerialNum;
	}

	*//**
	 * @return the totalNum
	 *//*
	public Integer getTotalNum() {
		return totalNum;
	}

	*//**
	 * @param totalNum
	 *            the totalNum to set
	 *//*
	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

	*//**
	 * @return the cancelled
	 *//*
	public Integer getCancelled() {
		return cancelled;
	}

	*//**
	 * @param cancelled
	 *            the cancelled to set
	 *//*
	public void setCancelled(Integer cancelled) {
		this.cancelled = cancelled;
	}

	*//**
	 * @return the netIssued
	 *//*
	public Integer getNetIssued() {
		return netIssued;
	}

	*//**
	 * @param netIssued
	 *            the netIssued to set
	 *//*
	public void setNetIssued(Integer netIssued) {
		this.netIssued = netIssued;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 

	@Override
	public String toString() {
		return "GetGstr1DocIssuedInvoicesEntity [id=" + id + ", sgstin="
				+ sgstin + ", returnPeriod=" + returnPeriod + ", invoiceStatus="
				+ invoiceStatus + ", checkSum=" + checkSum + ", docNum="
				+ docNum + ", serialNum=" + serialNum + ", fromSerialNum="
				+ fromSerialNum + ", toSerialNum=" + toSerialNum + ", totalNum="
				+ totalNum + ", cancelled=" + cancelled + ", netIssued="
				+ netIssued + ", isDelete=" + isDelete + "]";
	}

}
*/