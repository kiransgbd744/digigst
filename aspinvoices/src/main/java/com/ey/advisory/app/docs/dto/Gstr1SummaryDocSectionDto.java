/**
 * 
 */
package com.ey.advisory.app.docs.dto;

/**
 * @author BalaKrishna S
 *
 */
public class Gstr1SummaryDocSectionDto {

	private String taxDocType;
	private Integer total = 0;
	private Integer docCancelled = 0;
	private Integer netIssued = 0;
	
	private Integer srNo = 0;
	private String docName;
	
	private String seriesFrom;
	private String seriesTo;
	
	// private String taxDocType;
	 private Integer aspTotal;
	 private Integer aspCancelled;
	 private Integer aspNetIssued;
	 private Integer gstnTotal;
	 private Integer gstnCancelled;
	 private Integer gstnNetIssued;
	
	
	

	/**
	 * @return the taxDocType
	 */
	public String getTaxDocType() {
		return taxDocType;
	}
	/**
	 * @param taxDocType the taxDocType to set
	 */
	public void setTaxDocType(String taxDocType) {
		this.taxDocType = taxDocType;
	}
	/**
	 * @return the total
	 */
	public Integer getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
	/**
	 * @return the docCancelled
	 */
	public Integer getDocCancelled() {
		return docCancelled;
	}
	/**
	 * @param docCancelled the docCancelled to set
	 */
	public void setDocCancelled(Integer docCancelled) {
		this.docCancelled = docCancelled;
	}
	/**
	 * @return the netIssued
	 */
	public Integer getNetIssued() {
		return netIssued;
	}
	/**
	 * @param netIssued the netIssued to set
	 */
	public void setNetIssued(Integer netIssued) {
		this.netIssued = netIssued;
	}
	
	public Gstr1SummaryDocSectionDto() {
	}

	public Gstr1SummaryDocSectionDto(String taxDocType) {
		this.taxDocType = taxDocType;
	}
	
	
	
	
	public Integer getSrNo() {
		return srNo;
	}
	public void setSrNo(Integer srNo) {
		this.srNo = srNo;
	}
	public String getDocName() {
		return docName;
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}
	
	public String getSeriesFrom() {
		return seriesFrom;
	}
	public void setSeriesFrom(String seriesFrom) {
		this.seriesFrom = seriesFrom;
	}
	public String getSeriesTo() {
		return seriesTo;
	}
	public void setSeriesTo(String seriesTo) {
		this.seriesTo = seriesTo;
	}
	/**
	 * @param taxDocType
	 * @param total
	 * @param docCancelled
	 * @param netIssued
	 */
	public Gstr1SummaryDocSectionDto(String taxDocType, Integer total,
			Integer docCancelled, Integer netIssued) {
		super();
		this.taxDocType = taxDocType;
		this.total = (total != null) ? total : 0;
		this.docCancelled = (docCancelled != null) ? docCancelled : 0;
		this.netIssued = (netIssued != null) ? netIssued : 0;
		
	}
	@Override
	public String toString() {
		return "Gstr1SummaryDocSectionDto [taxDocType=" + taxDocType
				+ ", total=" + total + ", docCancelled=" + docCancelled
				+ ", netIssued=" + netIssued + "]";
	}
	public Integer getAspTotal() {
		return aspTotal;
	}
	public void setAspTotal(Integer aspTotal) {
		this.aspTotal = aspTotal;
	}
	public Integer getAspCancelled() {
		return aspCancelled;
	}
	public void setAspCancelled(Integer aspCancelled) {
		this.aspCancelled = aspCancelled;
	}
	public Integer getAspNetIssued() {
		return aspNetIssued;
	}
	public void setAspNetIssued(Integer aspNetIssued) {
		this.aspNetIssued = aspNetIssued;
	}
	public Integer getGstnTotal() {
		return gstnTotal;
	}
	public void setGstnTotal(Integer gstnTotal) {
		this.gstnTotal = gstnTotal;
	}
	public Integer getGstnCancelled() {
		return gstnCancelled;
	}
	public void setGstnCancelled(Integer gstnCancelled) {
		this.gstnCancelled = gstnCancelled;
	}
	public Integer getGstnNetIssued() {
		return gstnNetIssued;
	}
	public void setGstnNetIssued(Integer gstnNetIssued) {
		this.gstnNetIssued = gstnNetIssued;
	}
	
	
	
	
	
}
