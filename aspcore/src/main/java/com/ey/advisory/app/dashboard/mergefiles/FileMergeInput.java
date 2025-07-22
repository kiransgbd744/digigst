package com.ey.advisory.app.dashboard.mergefiles;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FileMergeInput {
	
	private final String gstin;
	private final String taxPeriod;
	private final List<String> section;
	
	
	public FileMergeInput(String gstin, String taxPeriod,
			List<String> section) {
		super();
		this.gstin = gstin;
		this.taxPeriod = taxPeriod;
		this.section = section;
	}	
}
