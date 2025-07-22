package com.ey.advisory.app.docs.dto.erp;

import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="section")
@XmlAccessorType(XmlAccessType.FIELD)
public class Gstr1DocSummaryJsonToXmldto {

	@SerializedName("sec_nm")
	@XmlElement(name="sec_nm")
	private String secName ;
	
//	@SerializedName("chksum")
//	@XmlElement(name="gstin")
//	private String secChkSum ;
	
	@SerializedName("ttl_rec")
	@XmlElement(name="ttl_rec")
	private int ttlRec ;
	
	@SerializedName("ttl_tax")
	@XmlElement(name="ttl_tax")
	private int ttlTax ;
	
	@SerializedName("ttl_igst")
	@XmlElement(name="ttl_igst")
	private int ttlIgst ;
	
	@SerializedName("ttl_sgst")
	@XmlElement(name="ttl_sgst")
	private int ttlSgst ;
	
	@SerializedName("ttl_cgst")
	@XmlElement(name="ttl_cgst")
	private int ttlCgst ;
	
	@SerializedName("ttl_val")
	@XmlElement(name="ttl_val")
	private int ttlVal ;
	
	@SerializedName("ttl_cess")
	@XmlElement(name="ttl_cess")
	private int ttlCess ;
	
	@SerializedName("ttl_doc_issued")
	@XmlElement(name="ttl_doc_issued")
	private int ttlDocIssued ;
	
	@SerializedName("ttl_doc_cancelled")
	@XmlElement(name="ttl_doc_cancelled")
	private int ttlDocCancelled ;
	
	@SerializedName("net_doc_issued")
	@XmlElement(name="net_doc_issued")
	private int netDocIssued ;

	@SerializedName("ttl_expt_amt")
	@XmlElement(name="ttl_expt_amt")
	private int ttlExptAmt ;
	
	@SerializedName("ttl_ngsup_amt")
	@XmlElement(name="ttl_ngsup_amt")
	private int ttlNgsupAmt ;
	
	@SerializedName("ttl_nilsup_amt")
	@XmlElement(name="ttl_nilsup_amt")
	private int ttlNilsupAmt ;

	public String getSecName() {
		return secName;
	}

	public int getTtlRec() {
		return ttlRec;
	}

	public int getTtlTax() {
		return ttlTax;
	}

	public int getTtlIgst() {
		return ttlIgst;
	}

	public int getTtlSgst() {
		return ttlSgst;
	}

	public int getTtlCgst() {
		return ttlCgst;
	}

	public int getTtlVal() {
		return ttlVal;
	}

	public int getTtlCess() {
		return ttlCess;
	}

	public int getTtlDocIssued() {
		return ttlDocIssued;
	}

	public int getTtlDocCancelled() {
		return ttlDocCancelled;
	}

	public int getNetDocIssued() {
		return netDocIssued;
	}

	public int getTtlExptAmt() {
		return ttlExptAmt;
	}

	public int getTtlNgsupAmt() {
		return ttlNgsupAmt;
	}

	public int getTtlNilsupAmt() {
		return ttlNilsupAmt;
	}

	public void setSecName(String secName) {
		this.secName = secName;
	}

	public void setTtlRec(int ttlRec) {
		this.ttlRec = ttlRec;
	}

	public void setTtlTax(int ttlTax) {
		this.ttlTax = ttlTax;
	}

	public void setTtlIgst(int ttlIgst) {
		this.ttlIgst = ttlIgst;
	}

	public void setTtlSgst(int ttlSgst) {
		this.ttlSgst = ttlSgst;
	}

	public void setTtlCgst(int ttlCgst) {
		this.ttlCgst = ttlCgst;
	}

	public void setTtlVal(int ttlVal) {
		this.ttlVal = ttlVal;
	}

	public void setTtlCess(int ttlCess) {
		this.ttlCess = ttlCess;
	}

	public void setTtlDocIssued(int ttlDocIssued) {
		this.ttlDocIssued = ttlDocIssued;
	}

	public void setTtlDocCancelled(int ttlDocCancelled) {
		this.ttlDocCancelled = ttlDocCancelled;
	}

	public void setNetDocIssued(int netDocIssued) {
		this.netDocIssued = netDocIssued;
	}

	public void setTtlExptAmt(int ttlExptAmt) {
		this.ttlExptAmt = ttlExptAmt;
	}

	public void setTtlNgsupAmt(int ttlNgsupAmt) {
		this.ttlNgsupAmt = ttlNgsupAmt;
	}

	public void setTtlNilsupAmt(int ttlNilsupAmt) {
		this.ttlNilsupAmt = ttlNilsupAmt;
	}

	
}
