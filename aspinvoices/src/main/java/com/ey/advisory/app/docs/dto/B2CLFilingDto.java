package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;

public class B2CLFilingDto {
	
	private String sec_nm;
	private String chksum;
	private Long ttl_rec;
	private BigDecimal ttl_val;
	private BigDecimal ttl_igst;
	private BigDecimal ttl_cess;
	private BigDecimal ttl_tax;
	
	private List<B2CLItemFilingDto> cpty_sum;
	
	public String getSec_nm() {
		return sec_nm;
	}
	public void setSec_nm(String sec_nm) {
		this.sec_nm = sec_nm;
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
	public List<B2CLItemFilingDto> getCpty_sum() {
		return cpty_sum;
	}
	public void setCpty_sum(List<B2CLItemFilingDto> cpty_sum) {
		this.cpty_sum = cpty_sum;
	}
	
	
	

}
