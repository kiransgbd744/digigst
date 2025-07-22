package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


/**
 * 
 * @author Siva.Nandam
 *
 */
@Entity
@Table(name = "GETGSTR6_ISD_ITC_SUMMARY")

public class Getgstr6IsdItcSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	@Column(name = "BATCH_ID")
	private String batchId;

	@Column(name = "GSTIN")
	private String gstIn;

	@Column(name = "TAX_PERIOD")
	private String taxperiod;

	@Column(name = "TOTAL_ITC")
	private BigDecimal totalItc;
	
	/*@Column(name = "INELG_ITC")
	private BigDecimal inElgItc;*/

	@Column(name = "ISD_ITC_CROSS")
	private BigDecimal isdItcCross;

	@Column(name = "DES")
	private BigDecimal des;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CES_AMT")
	private BigDecimal cessAmt;

	@Column(name = "IGST_AMT_AS_IGST")
	private BigDecimal igstAmtAsIgst;

	@Column(name = "IGST_AMT_AS_SGST")
	private BigDecimal igstAmtAsSgst;

	@Column(name = "IGST_AMT_AS_CGST")
	private BigDecimal igstAmtAsCgst;

	@Column(name = "SGST_AMT_AS_SGST")
	private BigDecimal sgstAmtAsSgst;

	@Column(name = "SGST_AMT_AS_IGST")
	private BigDecimal sgstAmtAsIgst;

	@Column(name = "CGST_AMT_AS_IGST")
	private BigDecimal cgstAmtAsIgst;

	@Column(name = "CGST_AMT_AS_CGST")
	private BigDecimal cgstAmtAscgst;

	@Column(name = "CESS_AMT")
	private BigDecimal cssAmt;

	@Column(name = "IS_TOTAL_ITC_AVAILABLE")
	private boolean Is_ttl_tc_available;

	@Column(name = "IS_ISD_ITC_CROSS")
	private boolean Is_isd_itc_across;

	@Column(name = "IS_INELG_ITC")
	private boolean isInelgItc;

	@Column(name = "IS_ELG_ITC")
	private boolean iElgItc;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "DERIVED_RET_PERIOD")
	private int deriviedReturnPeriod;

	@Column(name = "IS_DELETE")
	private boolean isdelete;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the batchId
	 */
	public String getBatchId() {
		return batchId;
	}

	/**
	 * @param batchId the batchId to set
	 */
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	/**
	 * @return the gstIn
	 */
	public String getGstIn() {
		return gstIn;
	}

	/**
	 * @param gstIn the gstIn to set
	 */
	public void setGstIn(String gstIn) {
		this.gstIn = gstIn;
	}

	/**
	 * @return the taxperiod
	 */
	public String getTaxperiod() {
		return taxperiod;
	}

	/**
	 * @param taxperiod the taxperiod to set
	 */
	public void setTaxperiod(String taxperiod) {
		this.taxperiod = taxperiod;
	}

	/**
	 * @return the totalItc
	 */
	public BigDecimal getTotalItc() {
		return totalItc;
	}

	/**
	 * @param totalItc the totalItc to set
	 */
	public void setTotalItc(BigDecimal totalItc) {
		this.totalItc = totalItc;
	}

	

	/**
	 * @return the isdItcCross
	 */
	public BigDecimal getIsdItcCross() {
		return isdItcCross;
	}

	/**
	 * @param isdItcCross the isdItcCross to set
	 */
	public void setIsdItcCross(BigDecimal isdItcCross) {
		this.isdItcCross = isdItcCross;
	}

	/**
	 * @return the des
	 */
	public BigDecimal getDes() {
		return des;
	}

	/**
	 * @param des the des to set
	 */
	public void setDes(BigDecimal des) {
		this.des = des;
	}

	/**
	 * @return the igstAmt
	 */
	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	/**
	 * @param igstAmt the igstAmt to set
	 */
	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	/**
	 * @return the cgstAmt
	 */
	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}

	/**
	 * @param cgstAmt the cgstAmt to set
	 */
	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	/**
	 * @return the sgstAmt
	 */
	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}

	/**
	 * @param sgstAmt the sgstAmt to set
	 */
	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	/**
	 * @return the cessAmt
	 */
	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	/**
	 * @param cessAmt the cessAmt to set
	 */
	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	/**
	 * @return the igstAmtAsIgst
	 */
	public BigDecimal getIgstAmtAsIgst() {
		return igstAmtAsIgst;
	}

	/**
	 * @param igstAmtAsIgst the igstAmtAsIgst to set
	 */
	public void setIgstAmtAsIgst(BigDecimal igstAmtAsIgst) {
		this.igstAmtAsIgst = igstAmtAsIgst;
	}

	/**
	 * @return the igstAmtAsSgst
	 */
	public BigDecimal getIgstAmtAsSgst() {
		return igstAmtAsSgst;
	}

	/**
	 * @param igstAmtAsSgst the igstAmtAsSgst to set
	 */
	public void setIgstAmtAsSgst(BigDecimal igstAmtAsSgst) {
		this.igstAmtAsSgst = igstAmtAsSgst;
	}

	/**
	 * @return the igstAmtAsCgst
	 */
	public BigDecimal getIgstAmtAsCgst() {
		return igstAmtAsCgst;
	}

	/**
	 * @param igstAmtAsCgst the igstAmtAsCgst to set
	 */
	public void setIgstAmtAsCgst(BigDecimal igstAmtAsCgst) {
		this.igstAmtAsCgst = igstAmtAsCgst;
	}

	/**
	 * @return the sgstAmtAsSgst
	 */
	public BigDecimal getSgstAmtAsSgst() {
		return sgstAmtAsSgst;
	}

	/**
	 * @param sgstAmtAsSgst the sgstAmtAsSgst to set
	 */
	public void setSgstAmtAsSgst(BigDecimal sgstAmtAsSgst) {
		this.sgstAmtAsSgst = sgstAmtAsSgst;
	}

	/**
	 * @return the sgstAmtAsIgst
	 */
	public BigDecimal getSgstAmtAsIgst() {
		return sgstAmtAsIgst;
	}

	/**
	 * @param sgstAmtAsIgst the sgstAmtAsIgst to set
	 */
	public void setSgstAmtAsIgst(BigDecimal sgstAmtAsIgst) {
		this.sgstAmtAsIgst = sgstAmtAsIgst;
	}

	/**
	 * @return the cgstAmtAsIgst
	 */
	public BigDecimal getCgstAmtAsIgst() {
		return cgstAmtAsIgst;
	}

	/**
	 * @param cgstAmtAsIgst the cgstAmtAsIgst to set
	 */
	public void setCgstAmtAsIgst(BigDecimal cgstAmtAsIgst) {
		this.cgstAmtAsIgst = cgstAmtAsIgst;
	}

	/**
	 * @return the cgstAmtAscgst
	 */
	public BigDecimal getCgstAmtAscgst() {
		return cgstAmtAscgst;
	}

	/**
	 * @param cgstAmtAscgst the cgstAmtAscgst to set
	 */
	public void setCgstAmtAscgst(BigDecimal cgstAmtAscgst) {
		this.cgstAmtAscgst = cgstAmtAscgst;
	}

	/**
	 * @return the cssAmt
	 */
	public BigDecimal getCssAmt() {
		return cssAmt;
	}

	/**
	 * @param cssAmt the cssAmt to set
	 */
	public void setCssAmt(BigDecimal cssAmt) {
		this.cssAmt = cssAmt;
	}

	/**
	 * @return the is_ttl_tc_available
	 */
	public boolean isIs_ttl_tc_available() {
		return Is_ttl_tc_available;
	}

	/**
	 * @param is_ttl_tc_available the is_ttl_tc_available to set
	 */
	public void setIs_ttl_tc_available(boolean is_ttl_tc_available) {
		Is_ttl_tc_available = is_ttl_tc_available;
	}

	/**
	 * @return the is_isd_itc_across
	 */
	public boolean isIs_isd_itc_across() {
		return Is_isd_itc_across;
	}

	/**
	 * @param is_isd_itc_across the is_isd_itc_across to set
	 */
	public void setIs_isd_itc_across(boolean is_isd_itc_across) {
		Is_isd_itc_across = is_isd_itc_across;
	}

	/**
	 * @return the isInelgItc
	 */
	public boolean isInelgItc() {
		return isInelgItc;
	}

	/**
	 * @param isInelgItc the isInelgItc to set
	 */
	public void setInelgItc(boolean isInelgItc) {
		this.isInelgItc = isInelgItc;
	}

	/**
	 * @return the iElgItc
	 */
	public boolean isiElgItc() {
		return iElgItc;
	}

	/**
	 * @param iElgItc the iElgItc to set
	 */
	public void setiElgItc(boolean iElgItc) {
		this.iElgItc = iElgItc;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdOn
	 */
	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedOn
	 */
	public LocalDateTime getModifiedOn() {
		return modifiedOn;
	}

	/**
	 * @param modifiedOn the modifiedOn to set
	 */
	public void setModifiedOn(LocalDateTime modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	/**
	 * @return the deriviedReturnPeriod
	 */
	public int getDeriviedReturnPeriod() {
		return deriviedReturnPeriod;
	}

	/**
	 * @param deriviedReturnPeriod the deriviedReturnPeriod to set
	 */
	public void setDeriviedReturnPeriod(int deriviedReturnPeriod) {
		this.deriviedReturnPeriod = deriviedReturnPeriod;
	}

	/**
	 * @return the isdelete
	 */
	public boolean isIsdelete() {
		return isdelete;
	}

	/**
	 * @param isdelete the isdelete to set
	 */
	public void setIsdelete(boolean isdelete) {
		this.isdelete = isdelete;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Getgstr6IsdItcSummaryEntity [id=" + id + ", batchId=" + batchId
				+ ", gstIn=" + gstIn + ", taxperiod=" + taxperiod
				+ ", totalItc=" + totalItc + ", isdItcCross=" + isdItcCross
				+ ", des=" + des + ", igstAmt=" + igstAmt + ", cgstAmt="
				+ cgstAmt + ", sgstAmt=" + sgstAmt + ", cessAmt=" + cessAmt
				+ ", igstAmtAsIgst=" + igstAmtAsIgst + ", igstAmtAsSgst="
				+ igstAmtAsSgst + ", igstAmtAsCgst=" + igstAmtAsCgst
				+ ", sgstAmtAsSgst=" + sgstAmtAsSgst + ", sgstAmtAsIgst="
				+ sgstAmtAsIgst + ", cgstAmtAsIgst=" + cgstAmtAsIgst
				+ ", cgstAmtAscgst=" + cgstAmtAscgst + ", cssAmt=" + cssAmt
				+ ", Is_ttl_tc_available=" + Is_ttl_tc_available
				+ ", Is_isd_itc_across=" + Is_isd_itc_across + ", isInelgItc="
				+ isInelgItc + ", iElgItc=" + iElgItc + ", createdBy="
				+ createdBy + ", createdOn=" + createdOn + ", modifiedBy="
				+ modifiedBy + ", modifiedOn=" + modifiedOn
				+ ", deriviedReturnPeriod=" + deriviedReturnPeriod
				+ ", isdelete=" + isdelete + "]";
	}

	
	

}
