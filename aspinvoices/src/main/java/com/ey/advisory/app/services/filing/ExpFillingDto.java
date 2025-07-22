package com.ey.advisory.app.services.filing;

import java.math.BigDecimal;

public class ExpFillingDto {

	private String secNum;
	private String chksum;
	private Long ttl_rec;
	private BigDecimal ttl_val;
	private BigDecimal ttl_igst;
	private BigDecimal ttl_tax;
	/**
	 * @return the secNum
	 */
	public String getSecNum() {
		return secNum;
	}
	/**
	 * @param secNum the secNum to set
	 */
	public void setSecNum(String secNum) {
		this.secNum = secNum;
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
	
}
