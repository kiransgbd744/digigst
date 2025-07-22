package com.ey.advisory.common.eyfileutils.tabular.impl;

/**
 * This exception is triggered by our LightCellsDataHandler implementation 
 * class, whenever we need to explicitly stop the parsing of the excel.
 * 
 * @author Arun KA
 *
 */
public class ExcelReadingTerminatedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ExcelReadingTerminatedException(String msg) {
		super(msg);
	}
}
