package com.ey.advisory.app.docs.dto.gstr2;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2NilBasicSummarySectionDto {
	
	
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

	/**
	 * @return the recordCount
	 */
	public Integer getRecordCount() {
		return recordCount;
	}

	/**
	 * @param recordCount the recordCount to set
	 */
	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
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
	 * @return the totalNilRated
	 */
	public BigDecimal getTotalNilRated() {
		return totalNilRated;
	}

	/**
	 * @param totalNilRated the totalNilRated to set
	 */
	public void setTotalNilRated(BigDecimal totalNilRated) {
		this.totalNilRated = totalNilRated;
	}

	/**
	 * @return the totalExempted
	 */
	public BigDecimal getTotalExempted() {
		return totalExempted;
	}

	/**
	 * @param totalExempted the totalExempted to set
	 */
	public void setTotalExempted(BigDecimal totalExempted) {
		this.totalExempted = totalExempted;
	}

	/**
	 * @return the totalNonGST
	 */
	public BigDecimal getTotalNonGST() {
		return totalNonGST;
	}

	/**
	 * @param totalNonGST the totalNonGST to set
	 */
	public void setTotalNonGST(BigDecimal totalNonGST) {
		this.totalNonGST = totalNonGST;
	}
	
	
	


}
