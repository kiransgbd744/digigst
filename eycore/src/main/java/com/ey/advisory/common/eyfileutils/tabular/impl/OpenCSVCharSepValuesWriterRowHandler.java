package com.ey.advisory.common.eyfileutils.tabular.impl;

import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class OpenCSVCharSepValuesWriterRowHandler implements RowHandler {

	
	public OpenCSVCharSepValuesWriterRowHandler(
			String filePath, String fileName, char separator) {
	}
	
	@Override
	public boolean handleRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		
		return false;
	}

	@Override
	public void flush(TabularDataLayout layout) {
		// Ignore this method.
	}

}
