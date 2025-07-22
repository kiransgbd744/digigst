package com.ey.advisory.app.docs.dto.anx2;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */

public class Anx2GetB2BDESummaryData {
	
	
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
	private BigDecimal netTax ;

	@Expose
	@SerializedName("ttligst")
	private BigDecimal totalIgst ;

	@Expose
	@SerializedName("ttlcgst")
	private BigDecimal totalCgst ;

	@Expose
	@SerializedName("ttlsgst")
	private BigDecimal totalSgst ;

	@Expose
	@SerializedName("ttlcess")
	private BigDecimal totalCess ; 
	
	@Expose
	@SerializedName("actionsum")
	private List<Anx2GetActionSummaryData> actionsum;
	
	
	@Expose
	@SerializedName("cptysum")
	private List<Anx2GetCounterPartySummaryData> cptysum;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getSecnm() {
		return secnm;
	}


	public void setSecnm(String secnm) {
		this.secnm = secnm;
	}


	public String getChksum() {
		return chksum;
	}


	public void setChksum(String chksum) {
		this.chksum = chksum;
	}


	public int getTotalDoc() {
		return totalDoc;
	}


	public void setTotalDoc(int totalDoc) {
		this.totalDoc = totalDoc;
	}


	public int getTotalVal() {
		return totalVal;
	}


	public void setTotalVal(int totalVal) {
		this.totalVal = totalVal;
	}


	public BigDecimal getNetTax() {
		return netTax;
	}


	public void setNetTax(BigDecimal netTax) {
		this.netTax = netTax;
	}


	public BigDecimal getTotalIgst() {
		return totalIgst;
	}


	public void setTotalIgst(BigDecimal totalIgst) {
		this.totalIgst = totalIgst;
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


	public BigDecimal getTotalCess() {
		return totalCess;
	}


	public void setTotalCess(BigDecimal totalCess) {
		this.totalCess = totalCess;
	}


	public List<Anx2GetActionSummaryData> getActionsum() {
		return actionsum;
	}


	public void setActionsum(List<Anx2GetActionSummaryData> actionsum) {
		this.actionsum = actionsum;
	}


	public List<Anx2GetCounterPartySummaryData> getCptysum() {
		return cptysum;
	}


	public void setCptysum(List<Anx2GetCounterPartySummaryData> cptysum) {
		this.cptysum = cptysum;
	}
	
	
	


}
