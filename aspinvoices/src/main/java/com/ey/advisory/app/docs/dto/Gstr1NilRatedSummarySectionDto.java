package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class represents the GSTR1 summary data for Nil Rated/ Non GST and
 * Exempted categories.
 * 
 * @author Mahesh.Golla
 *
 */
public class Gstr1NilRatedSummarySectionDto {

	/**
	 * Represents the total number of documents that are summmed up to get this
	 * summary. Since the user provides different search criteria for getting
	 * the summary, he/she would like to know the number of invoices/documents
	 * that were used to arrive at this summary values, based on the search
	 * criteria. This variable represents this total count of
	 * invoices/documents.
	 */
	@Expose
	@SerializedName("taxableValue")
	private Integer recordCount = 0;
	
	@Expose
	@SerializedName("tableSection")
	private String tableSection;

	@Expose
	@SerializedName("totalNilRated")
	private BigDecimal totalNilRated = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalExempted")
	private BigDecimal totalExempted = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("totalNonGST")
	private BigDecimal totalNonGST = BigDecimal.ZERO;

	public Gstr1NilRatedSummarySectionDto(String section) {
		// TODO Auto-generated constructor stub
	}

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}

	public BigDecimal getTotalNilRated() {
		return totalNilRated;
	}

	public void setTotalNilRated(BigDecimal totalNilRated) {
		this.totalNilRated = totalNilRated;
	}

	public BigDecimal getTotalExempted() {
		return totalExempted;
	}

	public void setTotalExempted(BigDecimal totalExempted) {
		this.totalExempted = totalExempted;
	}

	public BigDecimal getTotalNonGST() {
		return totalNonGST;
	}

	public void setTotalNonGST(BigDecimal totalNonGST) {
		this.totalNonGST = totalNonGST;
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

	public Gstr1NilRatedSummarySectionDto(String section,Integer recordCount,
			BigDecimal totalNilRated, BigDecimal totalExempted,
			BigDecimal totalNonGST) {
		Integer count = recordCount.intValue();
		this.tableSection = section;
		this.recordCount = (count != null) ? count : 0;
		
		this.totalNilRated = (totalNilRated != null) ? totalNilRated
				: BigDecimal.ZERO;
		this.totalExempted = (totalExempted != null) ? totalExempted
				: BigDecimal.ZERO;
		this.totalNonGST = (totalNonGST != null) ? totalNonGST
				: BigDecimal.ZERO;
		}
	
	public Gstr1NilRatedSummarySectionDto() {
		// TODO Auto-generated constructor stub
	}

	public Gstr1NilRatedSummarySectionDto add(
			Gstr1NilRatedSummarySectionDto other) {
		this.recordCount = this.recordCount + other.recordCount;
		this.tableSection = other.tableSection;
		this.totalNilRated = this.totalNilRated.add(other.totalNilRated);
		this.totalExempted = this.totalExempted.add(other.totalExempted);
		this.totalNonGST = this.totalNonGST.add(other.totalNonGST);
		return this;
	}


	

}
