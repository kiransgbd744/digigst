package com.ey.advisory.app.data.daos.client;

import java.time.LocalDate;

public class CreditOrDebitNoteDto {
	

	protected String origDocNo;
	
	
	protected LocalDate origDocDate;


	/**
	 * @return the origDocNo
	 */
	public String getOrigDocNo() {
		return origDocNo;
	}


	/**
	 * @param origDocNo the origDocNo to set
	 */
	public void setOrigDocNo(String origDocNo) {
		this.origDocNo = origDocNo;
	}


	/**
	 * @return the origDocDate
	 */
	public LocalDate getOrigDocDate() {
		return origDocDate;
	}


	/**
	 * @param origDocDate the origDocDate to set
	 */
	public void setOrigDocDate(LocalDate origDocDate) {
		this.origDocDate = origDocDate;
	}

}
