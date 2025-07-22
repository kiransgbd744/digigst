package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;

public class B2BFilingDto {
	
	private String sec_nm;
	private String chksum;
	private Long ttl_rec;
	private BigDecimal ttl_val;
	private BigDecimal ttl_igst;
	private BigDecimal ttl_cgst;
	private BigDecimal ttl_sgst;
	private BigDecimal ttl_cess;
	private BigDecimal ttl_tax;
	
	private List<B2BItemFilingDto> cpty_sum;

	/**
	 * @return the sec_nm
	 */
	public String getSec_nm() {
		return sec_nm;
	}

	/**
	 * @param sec_nm the sec_nm to set
	 */
	public void setSec_nm(String sec_nm) {
		this.sec_nm = sec_nm;
	}

	/**
	 * @return the chksum
	 */
	public String getChksum() {
		return chksum;
	}

	/**
	 * @param chksum the chksum to set
	 */
	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	/**
	 * @return the ttl_rec
	 */
	public Long getTtl_rec() {
		return ttl_rec;
	}

	/**
	 * @param ttl_rec the ttl_rec to set
	 */
	public void setTtl_rec(Long ttl_rec) {
		this.ttl_rec = ttl_rec;
	}

	/**
	 * @return the ttl_val
	 */
	public BigDecimal getTtl_val() {
		return ttl_val;
	}

	/**
	 * @param ttl_val the ttl_val to set
	 */
	public void setTtl_val(BigDecimal ttl_val) {
		this.ttl_val = ttl_val;
	}

	/**
	 * @return the ttl_igst
	 */
	public BigDecimal getTtl_igst() {
		return ttl_igst;
	}

	/**
	 * @param ttl_igst the ttl_igst to set
	 */
	public void setTtl_igst(BigDecimal ttl_igst) {
		this.ttl_igst = ttl_igst;
	}

	/**
	 * @return the ttl_cgst
	 */
	public BigDecimal getTtl_cgst() {
		return ttl_cgst;
	}

	/**
	 * @param ttl_cgst the ttl_cgst to set
	 */
	public void setTtl_cgst(BigDecimal ttl_cgst) {
		this.ttl_cgst = ttl_cgst;
	}

	/**
	 * @return the ttl_sgst
	 */
	public BigDecimal getTtl_sgst() {
		return ttl_sgst;
	}

	/**
	 * @param ttl_sgst the ttl_sgst to set
	 */
	public void setTtl_sgst(BigDecimal ttl_sgst) {
		this.ttl_sgst = ttl_sgst;
	}

	/**
	 * @return the ttl_cess
	 */
	public BigDecimal getTtl_cess() {
		return ttl_cess;
	}

	/**
	 * @param ttl_cess the ttl_cess to set
	 */
	public void setTtl_cess(BigDecimal ttl_cess) {
		this.ttl_cess = ttl_cess;
	}

	/**
	 * @return the ttl_tax
	 */
	public BigDecimal getTtl_tax() {
		return ttl_tax;
	}

	/**
	 * @param ttl_tax the ttl_tax to set
	 */
	public void setTtl_tax(BigDecimal ttl_tax) {
		this.ttl_tax = ttl_tax;
	}

	/**
	 * @return the cpty_sum
	 */
	public List<B2BItemFilingDto> getCpty_sum() {
		return cpty_sum;
	}

	/**
	 * @param cpty_sum the cpty_sum to set
	 */
	public void setCpty_sum(List<B2BItemFilingDto> cpty_sum) {
		this.cpty_sum = cpty_sum;
	}
	
	
	
	
}
