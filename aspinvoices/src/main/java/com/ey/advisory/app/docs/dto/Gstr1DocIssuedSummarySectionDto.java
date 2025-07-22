package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr1DocIssuedSummarySectionDto {

	@Expose
	@SerializedName("records")
	private int records = 0;
	
	@Expose
	@SerializedName("tableSection")
	private String tableSection;

	@Expose
	@SerializedName("totalIssued")
	private int totalIssued = 0;

	@Expose
	@SerializedName("netIssued")
	private int netIssued = 0;

	@Expose
	@SerializedName("cancelled")
	private int cancelled =0;

	/*public Gstr1DocIssuedSummarySectionDto(Integer records2) {
		// TODO Auto-generated constructor stub
	}
	*/
	
	public Gstr1DocIssuedSummarySectionDto() {
		super();
	}
	public Gstr1DocIssuedSummarySectionDto(String section) {
		this.tableSection = section;
	}


	public Gstr1DocIssuedSummarySectionDto(String section,Integer recordCount,
			Integer totalIssued, Integer netIssued, Integer cancelled) {
		
		Integer count = recordCount.intValue();
		this.tableSection = section;
		this.records = (count != null) ? count : 0;
		this.totalIssued = (totalIssued != null) ? totalIssued : 0;
		this.netIssued = (netIssued != null) ? netIssued : 0;
		this.cancelled = (cancelled != null) ? cancelled : 0;
		}
	
	public Gstr1DocIssuedSummarySectionDto add(Gstr1DocIssuedSummarySectionDto other) {
		this.records = this.records + other.records;
		this.tableSection = other.tableSection;
		this.totalIssued = this.totalIssued + other.totalIssued;
		this.netIssued = this.netIssued + other.netIssued;
		this.cancelled = this.cancelled + other.cancelled;
		return this;
	}
	/**
	 * @return the records
	 */
	public int getRecords() {
		return records;
	}
	/**
	 * @param records the records to set
	 */
	public void setRecords(int records) {
		this.records = records;
	}
	/**
	 * @return the tableSection
	 */
	public String getTableSection() {
		return tableSection;
	}
	/**
	 * @param tableSection the tableSection to set
	 */
	public void setTableSection(String tableSection) {
		this.tableSection = tableSection;
	}
	/**
	 * @return the totalIssued
	 */
	public int getTotalIssued() {
		return totalIssued;
	}
	/**
	 * @param totalIssued the totalIssued to set
	 */
	public void setTotalIssued(int totalIssued) {
		this.totalIssued = totalIssued;
	}
	/**
	 * @return the netIssued
	 */
	public int getNetIssued() {
		return netIssued;
	}
	/**
	 * @param netIssued the netIssued to set
	 */
	public void setNetIssued(int netIssued) {
		this.netIssued = netIssued;
	}
	/**
	 * @return the cancelled
	 */
	public int getCancelled() {
		return cancelled;
	}
	/**
	 * @param cancelled the cancelled to set
	 */
	public void setCancelled(int cancelled) {
		this.cancelled = cancelled;
	}
	
}
