package com.ey.advisory.common.eyfileutils.tabular.impl;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspose.cells.Cell;
import com.aspose.cells.Row;
import com.aspose.cells.Worksheet;
import com.ey.advisory.common.eyfileutils.tabular.ExtendedLightCellsDataHandler;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

/**
 * This class is the LightCellsDataHandler implementation for processing 
 * very large excel sheets (with around 1 million rows). This class 
 * handles the events raised by Aspose LightCells API for each cell and 
 * accumulates the data required for a single row and then it invokes the
 * RowHandler implementation to process the  data row. This class maintains
 * a single copy of an array for accumulating the data from the excel sheet.
 * It doesn't create Object[] for each row. If the RowHandler needs the data,
 * then it has to copy the data from the input array passed by this class and
 * save it elsewhere. Never should the RowHandler maintain a reference to the
 * Object[] passed to it by this class, as it keeps changing when each row is
 * traversed by this class.
 * 
 * @author Sai.Pakanati
 *
 */
public class XlsxLightCellsDataHandlerImpl 
				implements ExtendedLightCellsDataHandler {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(XlsxLightCellsDataHandlerImpl.class);	
	
	/**
	 * Maintain a single dimensional array for 
	 */
	private Object[] rowData;
	private TabularDataLayout layout;
	private RowHandler rowHandler;
	
	private int lastColNo;
	private int dataStartRow;
	private int headerStartRow;
	private int curRowNo;
	
	private boolean continueProcessing = true;
	
	/**
	 * If this is set to true, then after reading the first row, processing
	 * will be terminated. The purpose of this is to do validations on the 
	 * header row before checking the data values. For huge excel sheets, this
	 * is a big advantage because we don't have to load the entire data before
	 * knowing that the header is valid.
	 */
	private boolean readOnlyHeaderRow = false;
	
	
	public XlsxLightCellsDataHandlerImpl(
			Object[] rowData, TabularDataLayout layout, RowHandler handler) {
		this.rowData = rowData;
		this.layout = layout;
		this.rowHandler = handler;
		
		// Get the total number of columns in the excel sheet that we're about
		// to parse.
		this.lastColNo = layout.getNoOfCols() - 1;
		this.dataStartRow = layout.getDataStartRow();
		this.headerStartRow = layout.getHeaderStartRow();
	}
	
	public XlsxLightCellsDataHandlerImpl(
			Object[] rowData, TabularDataLayout layout, 
			RowHandler handler, boolean readOnlyHeaderRow) {
		this.rowData = rowData;
		this.layout = layout;
		this.rowHandler = handler;
		
		// Get the total number of columns in the excel sheet that we're about
		// to parse.
		this.lastColNo = layout.getNoOfCols() - 1;
		this.dataStartRow = layout.getDataStartRow();
		this.headerStartRow = layout.getHeaderStartRow();
		this.readOnlyHeaderRow = readOnlyHeaderRow;
	}	

	@Override
	public boolean processCell(Cell cell) {
		int colNo = cell.getColumn();
		this.rowData[colNo] = cell.getValue();
		
		// The return value of this method tells Aspose whether to cache the
		// data in memory for later retrieval. Since, we don't need to store
		// anything for later use, we can inform Aspose to dispose the data
		// by returning false.
		return false;
	}

	@Override
	public boolean processRow(Row row) {
		return true;
	}
	
	@Override
	public boolean startCell(int colNo) {
		// Do not parse beyond the number of columns in the excel sheet.
		// This can happen if some row at the end of the excel sheet is 
		// formatted. Aspose might detect the formatting change and start 
		// parsing till that cell to get it's contents.
		return (colNo <= lastColNo);
	}
	
	@Override
	public boolean startRow(int rowNo) {
		curRowNo = rowNo; // Set the value of the current row number.

		// Continue with reading the header row.
		if (curRowNo == headerStartRow) return true;
		
		// Get the header row and store it in a separate header
		// Pass the row header to the data handler. Read the header row when
		// the next row is available.
		if (curRowNo == headerStartRow + 1) {
			rowHandler.handleHeaderRow(rowNo - 1, rowData, layout);
			
			// If the readOnlyHeaderRow variable is set, then terminate the
			// processing by throwing an exception.
			if (readOnlyHeaderRow) {
				throw new ExcelReadingTerminatedException(
						"Terminating excel read operation as "
						+ "the 'readOnlyHeaderRow' flag is set.");
			}
			
			// Clear the data and be ready for the next row.
			clearArray(rowData);			
		}
		
		// Skip the rows before the data start row.	Also skip the rows 
		// if the RowHandler object has returned false.
		if(!continueProcessing ||  (curRowNo < dataStartRow)) return false;
		
		// Since aspose doesn't give us an 'endRow' event, we need to do 
		// process the previous row at the beginning of the current row. We
		// start processing rows from the start of the 2nd data row onwards.
		// We cannot determine the end of a row by checking the count of 
		// columns in the startCell or processCell method, as aspose might 
		// raise lesser OR more number of startCell/processCell events than
		// the expected number of columns. This is because, the expected number
		// of columns is based on the TabularLayout class that we define, based
		// on the expected excel sheet format. But the actual cells within a 
		// row is represented as xml tags within the underlying excel file 
		// format. Excel might decide to store these tags only till the last
		// cell in the row where data/formatting is available. If this cell is
		// before the expected no. of columns, there will be lesser cell tags
		// within the file, for the row and if it's after the expected no.
		// of columns, then there will be more cell tags for the row. Since,
		// aspose generates events corresponding to the tags that it encounters
		// within the excel sheet, it will raise more/lesser number of events
		// than the expected number of columns, in a row. So, it's absolutely
		// necessary that the full data that's gathered in the previous row
		// should be processed here, at the start of the next row.
		
		if (curRowNo > dataStartRow) {
			// Any exception here will terminate the entire  process. We're
			// not introducing a try/catch/finally block overhead here as this 
			// method might get invoked more than a million times per sheet, 
			// during the execution. Hence, it's very important that the row 
			// handler performs minimal in-memory activities.
			continueProcessing = 
					rowHandler.handleRow(rowNo - 1, rowData, layout);
			
			// After processing the previous row, clear the array in order to
			// gather data from the current row.
			clearArray(rowData);
			
			return continueProcessing;
		} 

		// Process the first data row separately (as we cannot invoke the
		// row handler there).
		return (curRowNo == dataStartRow);
	}

	@Override
	public boolean startSheet(Worksheet sheet) {
		// Return true for the first sheet, so that it's contents will be
		// processed. Ignore the other sheets, by returning false for them.
		return (sheet.getIndex() == 0);
	}
	
	// Reset all the elements of the array. This has to be called just before
	// populating the contents of the new row extracted from the excel sheet
	// into the array.
	private void clearArray(Object[] arr) {
		for(int i = 0; i < arr.length; i++) {
			arr[i] = null;
		}
	}	
	
	/**
	 * Since we're doing the processing for the previous row at the start of
	 * the subsequent row, the last row will remain unprocessed, as there is
	 * no subsequent row. So, this method has to be invoked by the client
	 * to ensure that the last row is processed properly.
	 */
	@Override
	public void handleUnprocessedData() {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Handling unprocessed data... lastRow = " + 
					curRowNo);
			LOGGER.debug(Arrays.toString(rowData));
		}
		
		// If processing was not cancelled in between any rows, then the
		// last row will remain unprocessed. This has to be processed here.
		if(continueProcessing) {
			// curRowNo will be the last processed rowNo for the last row.
			rowHandler.handleRow(curRowNo, rowData, layout);
		}
		
		// After handling all the rows, invoke the flush method.
		rowHandler.flush(layout);
	}
	
}
