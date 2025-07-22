package com.ey.advisory.app.dashboard.mergefiles;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FileInfo {
	
	private final String gstin;
	private final String taxPeriod;
	private final String section;
	
	
	public FileInfo(String gstin, String taxPeriod, String section) {
		super();
		this.gstin = gstin;
		this.taxPeriod = taxPeriod;
		this.section = section;
	}
	
	

}
