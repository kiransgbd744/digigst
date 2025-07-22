package com.ey.advisory.app.data.daos.client;

public class DocIssueDto {
	private String secNum;
	private String chksum;
	private Integer totalNumber = 0;
	private Integer cancelled = 0;
	private Integer netNumber = 0;
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
	 * @return the totalNumber
	 */
	public Integer getTotalNumber() {
		return totalNumber;
	}
	/**
	 * @param totalNumber the totalNumber to set
	 */
	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}
	/**
	 * @return the cancelled
	 */
	public Integer getCancelled() {
		return cancelled;
	}
	/**
	 * @param cancelled the cancelled to set
	 */
	public void setCancelled(Integer cancelled) {
		this.cancelled = cancelled;
	}
	/**
	 * @return the netNumber
	 */
	public Integer getNetNumber() {
		return netNumber;
	}
	/**
	 * @param netNumber the netNumber to set
	 */
	public void setNetNumber(Integer netNumber) {
		this.netNumber = netNumber;
	}
	

}
