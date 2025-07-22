package com.ey.advisory.common.eyfileutils.tabular.impl;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.
				XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import com.ey.advisory.common.eyfileutils.tabular.HaltProcessingException;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class XlsxXssfDataHandlerImpl implements SheetContentsHandler {
	
	private Object[] rowData;
	private TabularDataLayout layout;
	private RowHandler rowHandler;
	
	private int lastColNo;
	private int dataStartRow;
	
	
	private int curRowNo = -1;
	private int curColNo = -1;

	private boolean continueProcessing = true;
	
	public XlsxXssfDataHandlerImpl(Object[] rowData, 
			TabularDataLayout layout, RowHandler handler) {
		this.rowData = rowData;
		this.layout = layout;
		this.rowHandler = handler;
		
		// Get the total number of columns in the excel sheet that we're about
		// to parse.
		this.lastColNo = layout.getNoOfCols() - 1;
		this.dataStartRow = layout.getDataStartRow();
	}
	
	@Override
	public void cell(String cellReference, 
				String formattedValue, XSSFComment comment) {
		
		// Do not process if cell reference is null. When we encounter the
		// next cell reference that is not null, we need to figure out the
		// missing cell references that we skipped earlier.
		if(cellReference == null) { return; }
		
		// If the current column number value is already greater than the 
		// expected column number, then return immediately. We're not 
		// interested in these columns.
		if(curColNo > lastColNo) { return; }
				
        // Get the current column number from the non-null cell reference.
        int thisCol = (new CellReference(cellReference)).getCol();
        int noOfMissedCells = thisCol - curColNo - 1;
        
    	// populate the rowData array for these missed indexes with null.        
        for(int i = 0; i < noOfMissedCells; i++) {
        	curColNo++;
        	if(curColNo <= lastColNo) {
        		rowData[curColNo] = null;
        	} 
        }        
        
        // Set the current column number to the value of the current cell
        // number that XSSF has returned.
        curColNo = thisCol;
        
        // Attach the formatted value to the current column.
        if(curColNo <= lastColNo) {        	
        	rowData[curColNo] = formattedValue;
        }
	}
	
	@Override
	public void startRow(int rowNum) {

		// If we decide to process the row, then clear all the data gathered
		// from the previous row.
		clearArray(rowData);

		// Reset the current column number to -1.
		this.curColNo = -1;
		
		// First Process the missing rows. XSSF can skip the blank rows, based
		// on whether it is available in the underlying xml saved by Excel.
		int noOfMissedRows = rowNum - curRowNo - 1;
		
		for(int i = 0; i < noOfMissedRows; i++) {
			curRowNo++;
			
			// Skip any row before data start row.
			if(curRowNo < dataStartRow) continue;
			
			continueProcessing = rowHandler.handleRow(
						curRowNo, rowData, layout);
			// If the RowHandler returns a false, then stop processing. All
			// rows in this excel file need to be skipped. Throw a 
			// HaltProcessingException to return the control back to the caller.
			if(!continueProcessing) {
				throw new HaltProcessingException();
			}
		}
		
		// By this time, curRowNo will represent the previous row that was 
		// processed. Reset it to the current row number that we're about to
		// process.
		this.curRowNo = rowNum; 
	}	

	@Override
	public void endRow(int rowNum) {

		// If the current row number is before the data start row, then
		// don't do anything.
		if(rowNum < dataStartRow) return;
		
		// Account for any missing columns after the last processed column.
		// Fill the rowData elements for these columns with null values.
		for (int i = curColNo + 1; i <= lastColNo; i++) {
            rowData[i] = null;
        }		
		// Invoke the Row Handler using the data gathered from the row.
		continueProcessing = rowHandler.handleRow(
				curRowNo, rowData, layout);
		
		// Check if the continueProcessing variable is false. If it is false, 
		// then terminate the processing by throwing a HaltProcessingException. 
		// This exception needs to be handled by the caller.
		if(!continueProcessing) {
			throw new HaltProcessingException();
		}
		
	}

	@Override
	public void headerFooter(String text, boolean isHeader, String tagName) {
		// Leaving this method empty as there are no headers/footers to
		// be handled in our excel sheet.
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
