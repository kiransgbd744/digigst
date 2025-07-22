package com.ey.advisory.app.docs.dto;

import java.util.List;

public class Gstr1FilingDto {
	
	private String gstin;
	private String ret_period;
	private String summ_typ;
	
	private List<Object> sec_sum;

	/**
	 * @return the gstin
	 */
	public String getGstin() {
		return gstin;
	}

	/**
	 * @param gstin the gstin to set
	 */
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	/**
	 * @return the ret_period
	 */
	public String getRet_period() {
		return ret_period;
	}

	/**
	 * @param ret_period the ret_period to set
	 */
	public void setRet_period(String ret_period) {
		this.ret_period = ret_period;
	}

	/**
	 * @return the summ_typ
	 */
	public String getSumm_typ() {
		return summ_typ;
	}

	/**
	 * @param summ_typ the summ_typ to set
	 */
	public void setSumm_typ(String summ_typ) {
		this.summ_typ = summ_typ;
	}

	/**
	 * @return the sec_sum
	 */
	public List<Object> getSec_sum() {
		return sec_sum;
	}

	/**
	 * @param sec_sum the sec_sum to set
	 */
	public void setSec_sum(List<Object> sec_sum) {
		this.sec_sum = sec_sum;
	}

	
	
	

}
