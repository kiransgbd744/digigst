package com.ey.advisory.app.anx2.initiaterecon;

import java.util.Date;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Arun.KA
 *
 */

@Getter
@Setter
public class Anx2FetchInfoDto {
	
	private static final long serialVersionUID = 1L;

	@Expose
	private String lastFetchStatus;
	
	@Expose
	private Date lastFetchDate = null;
	
	@Expose
	private String gstin;
	
	public Anx2FetchInfoDto() { }

	public Anx2FetchInfoDto(String lastFetchStatus, Date lastFetchDate,
			String gstin) {
		super();
		this.lastFetchStatus = lastFetchStatus;
		this.lastFetchDate = lastFetchDate;
		this.gstin = gstin;
		
	}

	@Override
	public String toString() {
		return "Anx2FetchInfoDto [lastFetchStatus=" + lastFetchStatus
				+ ", lastFetchDate=" + lastFetchDate + ", gstin=" + gstin + "]";
	}

	
	
	

}
