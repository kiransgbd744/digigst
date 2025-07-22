package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;

public class NilFillingDto {

	private String secNum;
	private String chksum;
	private BigDecimal ttl_nil;
	private BigDecimal ttl_non;
	private BigDecimal ttl_expt;
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
	 * @return the ttl_nil
	 */
	public BigDecimal getTtl_nil() {
		return ttl_nil;
	}
	/**
	 * @param ttl_nil the ttl_nil to set
	 */
	public void setTtl_nil(BigDecimal ttl_nil) {
		this.ttl_nil = ttl_nil;
	}
	/**
	 * @return the ttl_non
	 */
	public BigDecimal getTtl_non() {
		return ttl_non;
	}
	/**
	 * @param ttl_non the ttl_non to set
	 */
	public void setTtl_non(BigDecimal ttl_non) {
		this.ttl_non = ttl_non;
	}
	/**
	 * @return the ttl_expt
	 */
	public BigDecimal getTtl_expt() {
		return ttl_expt;
	}
	/**
	 * @param ttl_expt the ttl_expt to set
	 */
	public void setTtl_expt(BigDecimal ttl_expt) {
		this.ttl_expt = ttl_expt;
	}
	
	
}
