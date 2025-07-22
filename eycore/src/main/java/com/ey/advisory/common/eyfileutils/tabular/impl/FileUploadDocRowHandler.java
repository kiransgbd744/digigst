package com.ey.advisory.common.eyfileutils.tabular.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class FileUploadDocRowHandler<K> implements RowHandler {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileUploadDocRowHandler.class);

	/**
	 * The map to be populated with the the data from the file.
	 */
	private List<Object[]> fileObjList = new ArrayList<>();
	
	private Object[] headerData = null;
	
	public FileUploadDocRowHandler() {
	}

	@Override
	public boolean handleRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		LOGGER.debug("Row No in FileUploadDocRowHandler " + rowNo);
		LOGGER.debug("Row in FileUploadDocRowHandler " + row);
		
		// First check if all the elements of the row array 
		Object[] arr = row.clone();
		boolean isRowEmpty = true;
		for (Object object : arr) {
			if(object != null){
				isRowEmpty = false;
			}
		}
		if(!isRowEmpty){
			fileObjList.add(arr);
		}
		return true;
	}

	public List<Object[]> getFileUploadList() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Files Upload List in FileUploadDocRowHandler"
					+ fileObjList.toString());
		}
		return fileObjList;
	}

	// Get the header data for the application.
	public Object[] getHeaderRow() {
		return headerData;
	}
	
	@Override
	public void handleHeaderRow(int rowNo, Object[] row, TabularDataLayout layout) {
		 // clone the shared array passed by the framework.		
		Object[] arr = row.clone();
		headerData = arr;
	}
}
