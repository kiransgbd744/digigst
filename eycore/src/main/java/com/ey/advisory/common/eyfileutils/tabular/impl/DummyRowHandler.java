package com.ey.advisory.common.eyfileutils.tabular.impl;

import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public final class DummyRowHandler implements RowHandler {

	private int totalNoOfRows;
	private int lastRowNo;
	
	/**
	 * Store the TabularDataLayout instance from the initialize method. This can
	 * be used later during processing.
	 */
	private TabularDataLayout layout;
	
	public boolean handleRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		// Increment the row number for every row handled.
		totalNoOfRows++;
		this.lastRowNo = rowNo;
		
		return true;
	}
	
	
	public int getTotalNoOfRows() {
		return totalNoOfRows;
	}
	
	public int getLastRowNo() {
		return lastRowNo;
	}

	public TabularDataLayout getLayout() {
		return layout;
	}

}
