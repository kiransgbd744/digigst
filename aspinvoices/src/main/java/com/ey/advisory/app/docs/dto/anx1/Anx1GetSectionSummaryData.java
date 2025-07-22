/**
 * 
 */
package com.ey.advisory.app.docs.dto.anx1;

import java.math.BigDecimal;
import java.util.Set;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Laxmi.Salukuti
 *
 */
public class Anx1GetSectionSummaryData {

	
	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("secnm")
	private String secnm;

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("ttldoc")
	private int totalDoc;

	@Expose
	@SerializedName("ttlval")
	private int totalVal;

	@Expose
	@SerializedName("nettax")
	private BigDecimal netTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("ttligst")
	private BigDecimal totalIgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("ttlcgst")
	private BigDecimal totalCgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("ttlsgst")
	private BigDecimal totalSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("ttlcess")
	private BigDecimal totalCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("actionsum")
	private Set<Anx1GetActionSummaryData> actionsum;

	@Expose
	@SerializedName("cptysum")
	private Set<Anx1GetCounterPartySummaryData> cptysum;

	@Expose
	@SerializedName("doctypsum")
	private Set<Anx1GetDocumentTypeSummaryData> doctypsum;

	/**
	 * @return the secnm
	 */
	public String getSecnm() {
		return secnm;
	}

	/**
	 * @param secnm
	 *            the secnm to set
	 */
	public void setSecnm(String secnm) {
		this.secnm = secnm;
	}

	/**
	 * @return the chksum
	 */
	public String getChksum() {
		return chksum;
	}

	/**
	 * @param chksum
	 *            the chksum to set
	 */
	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	/**
	 * @return the totalDoc
	 */
	public int getTotalDoc() {
		return totalDoc;
	}

	/**
	 * @param totalDoc
	 *            the totalDoc to set
	 */
	public void setTotalDoc(int totalDoc) {
		this.totalDoc = totalDoc;
	}

	/**
	 * @return the totalVal
	 */
	public int getTotalVal() {
		return totalVal;
	}

	/**
	 * @param totalVal
	 *            the totalVal to set
	 */
	public void setTotalVal(int totalVal) {
		this.totalVal = totalVal;
	}

	/**
	 * @return the netTax
	 */
	public BigDecimal getNetTax() {
		return netTax;
	}

	/**
	 * @param netTax
	 *            the netTax to set
	 */
	public void setNetTax(BigDecimal netTax) {
		this.netTax = netTax;
	}

	/**
	 * @return the totalIgst
	 */
	public BigDecimal getTotalIgst() {
		return totalIgst;
	}

	/**
	 * @param totalIgst
	 *            the totalIgst to set
	 */
	public void setTotalIgst(BigDecimal totalIgst) {
		this.totalIgst = totalIgst;
	}

	/**
	 * @return the totalCess
	 */
	public BigDecimal getTotalCess() {
		return totalCess;
	}

	/**
	 * @param totalCess
	 *            the totalCess to set
	 */
	public void setTotalCess(BigDecimal totalCess) {
		this.totalCess = totalCess;
	}

	/**
	 * @return the actionsum
	 */
	public Set<Anx1GetActionSummaryData> getActionsum() {
		return actionsum;
	}

	/**
	 * @param actionsum the actionsum to set
	 */
	public void setActionsum(Set<Anx1GetActionSummaryData> actionsum) {
		this.actionsum = actionsum;
	}

	/**
	 * @return the cptysum
	 */
	public Set<Anx1GetCounterPartySummaryData> getCptysum() {
		return cptysum;
	}

	/**
	 * @param cptysum the cptysum to set
	 */
	public void setCptysum(Set<Anx1GetCounterPartySummaryData> cptysum) {
		this.cptysum = cptysum;
	}

	/**
	 * @return the doctypsum
	 */
	public Set<Anx1GetDocumentTypeSummaryData> getDoctypsum() {
		return doctypsum;
	}

	/**
	 * @param doctypsum the doctypsum to set
	 */
	public void setDoctypsum(Set<Anx1GetDocumentTypeSummaryData> doctypsum) {
		this.doctypsum = doctypsum;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getTotalCgst() {
		return totalCgst;
	}

	public void setTotalCgst(BigDecimal totalCgst) {
		this.totalCgst = totalCgst;
	}

	public BigDecimal getTotalSgst() {
		return totalSgst;
	}

	public void setTotalSgst(BigDecimal totalSgst) {
		this.totalSgst = totalSgst;
	}
	
	
	

	
}
