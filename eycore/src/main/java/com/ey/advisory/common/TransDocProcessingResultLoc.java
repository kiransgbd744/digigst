package com.ey.advisory.common;

import java.util.Arrays;

/**
 * This class is responsbile for storing the location of errors for data that
 * can be represented in tabular format. This can be used for storing 
 * validation resulsts for a transaction document (which usually is in a 
 * master/detail format). So, error 
 * 
 * @author Sai.Pakanati
 *
 */
public class TransDocProcessingResultLoc {
	
	/**
	 * The line number where error has occured.
	 */
	protected Integer lineNo;
	
	/**
	 * The list of column identifiers (either as names or column numbers). 
	 * If it's a JSON document, then column names is fine. But, if it's 
	 * excel document, then column numbers will be useful.
	 */
	protected Object[] fieldIdentifiers = new Object[] {};

	public TransDocProcessingResultLoc(Object[] fieldIdentifiers) {
		super();
		this.fieldIdentifiers = fieldIdentifiers;
	}
	
	public TransDocProcessingResultLoc(Integer lineNo, 
				Object[] fieldIdentifiers) {
		super();
		this.lineNo = lineNo;
		this.fieldIdentifiers = (fieldIdentifiers != null) ? 
					fieldIdentifiers : new Object[] {};
	}

	public Integer getLineNo() {
		return lineNo;
	}

	public Object[] getFieldIdentifiers() {
		return fieldIdentifiers;
	}

	@Override
	public String toString() {
		return "TransDocProcessingResultLoc [lineNo=" + lineNo
				+ ", fieldIdentifiers=" + Arrays.toString(fieldIdentifiers)
				+ "]";
	}
	
}
