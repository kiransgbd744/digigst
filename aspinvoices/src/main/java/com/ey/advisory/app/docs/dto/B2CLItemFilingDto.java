package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

public class B2CLItemFilingDto {
	
	private String pos;
	private String chksum;
	private Long ttl_rec;
	private BigDecimal ttl_val;
	private BigDecimal ttl_igst;
	private BigDecimal ttl_cess;
	private BigDecimal ttl_tax;
	
	
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getChksum() {
		return chksum;
	}
	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
	public Long getTtl_rec() {
		return ttl_rec;
	}
	public void setTtl_rec(Long ttl_rec) {
		this.ttl_rec = ttl_rec;
	}
	public BigDecimal getTtl_val() {
		return ttl_val;
	}
	public void setTtl_val(BigDecimal ttl_val) {
		this.ttl_val = ttl_val;
	}
	public BigDecimal getTtl_igst() {
		return ttl_igst;
	}
	public void setTtl_igst(BigDecimal ttl_igst) {
		this.ttl_igst = ttl_igst;
	}
	public BigDecimal getTtl_cess() {
		return ttl_cess;
	}
	public void setTtl_cess(BigDecimal ttl_cess) {
		this.ttl_cess = ttl_cess;
	}
	public BigDecimal getTtl_tax() {
		return ttl_tax;
	}
	public void setTtl_tax(BigDecimal ttl_tax) {
		this.ttl_tax = ttl_tax;
	}
	
	

}
