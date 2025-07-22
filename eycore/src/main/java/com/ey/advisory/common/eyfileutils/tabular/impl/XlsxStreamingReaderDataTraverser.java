package com.ey.advisory.common.eyfileutils.tabular.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ey.advisory.common.eyfileutils.FileProcessingException;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.monitorjbl.xlsx.StreamingReader;

public class XlsxStreamingReaderDataTraverser 
				implements TabularDataSourceTraverser {

	private static final Logger LOGGER = LoggerFactory.getLogger(
					XlsxStreamingReaderDataTraverser.class);
	
	public XlsxStreamingReaderDataTraverser() { /* Empty Constructor */ }

	@Override
	public void traverse(Object dataSource, TabularDataLayout layout,
			RowHandler rowHandler, Map<String, Object> properties) {
		
		String filePath = (String) dataSource;
		File file = new File(filePath);
		
		try(
		Workbook workbook = StreamingReader.builder()
		        .rowCacheSize(100)    
		        .bufferSize(8388608)     
		        .open(file)) {
		
			int rowNo = 0;
			int colNo = 0;
			int lastColNo = layout.getNoOfCols() - 1;
			boolean continueParsing = true;
			
			Object[] rowData = new Object[layout.getNoOfCols()];
			
			// Get the first sheet.
			Sheet sheet = workbook.getSheetAt(0);
			if(LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Processing sheet %s", sheet.getSheetName());
				LOGGER.debug(msg);
			}
			for (Row r : sheet) {			
				colNo = 0;	// Initialize column for each row.
				clearArray(rowData);
				for (Cell c : r) {
					if(colNo > lastColNo) { break; }
					rowData[colNo++] = c.getStringCellValue();
				}
				continueParsing = rowHandler.handleRow(
						rowNo++, rowData, layout);
				if(!continueParsing) { break; }
			}
			
			// Invoke the flush method on the row handler to deal with
			// any unprocessed data within it.
			rowHandler.flush(layout);
		} catch(IOException ex) {
			String msg = String.format(
					"Error while reading from file '%s", file.getPath());
			LOGGER.error(msg, ex);
			throw new FileProcessingException(msg, ex);
		} catch(Exception ex) {
			String msg = String.format(
					"Unexpected error while processing file '%s", 
							file.getPath());
			LOGGER.error(msg, ex);
			throw new FileProcessingException(msg, ex);			
		}
	}

	// Reset all the elements of the array. This has to be called just before
	// populating the contents of the new row extracted from the excel sheet
	// into the array.
	private void clearArray(Object[] arr) {
		for(int i = 0; i < arr.length; i++) {
			arr[i] = null;
		}
	}		
}
