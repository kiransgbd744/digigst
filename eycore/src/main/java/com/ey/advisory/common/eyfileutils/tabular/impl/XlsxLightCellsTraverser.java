package com.ey.advisory.common.eyfileutils.tabular.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aspose.cells.LoadOptions;
import com.aspose.cells.Workbook;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.eyfileutils.FileProcessingException;
import com.ey.advisory.common.eyfileutils.tabular.ExtendedLightCellsDataHandler;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;

@Component("XlsxLightCellsTraverser")
public class XlsxLightCellsTraverser implements TabularDataSourceTraverser {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(XlsxLightCellsTraverser.class);
	
	private boolean traverseOnlyHeader(Map<String, Object> properties) {
		return (properties != null) && 
				properties.containsKey(TraverserConstants.READ_ONLY_HEADER_ROW);
	}
	
	public void traverse(Object dataSource, TabularDataLayout layout,
			RowHandler rowHandler, Map<String, Object> properties) {
		
		// Here, we assume that the data source is a File, we open the file
		// using an AsposeWorkbook object and start iterating over the rows
		// using Aspose LightCells.
		
		// Firstly, allocate a row that will be used store a row data. The 
		// number of columns within the data source will be the size of the
		// single dimensional array. Every time, this class fetches a new row
		// of information from the excel sheet, it clears this array and loads
		// the new row contents into it and invokes the row cell parser.
		Object[] rowData = new Object[layout.getNoOfCols()];
	
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Row Data Size is: " + rowData.length);
		}
		
		// Set Aspose License.
		GenUtil.setAsposeLicense();
		
		// Initialize the LoadOptions with the LightCellsDataHandler class.
		LoadOptions opts = new LoadOptions();
		ExtendedLightCellsDataHandler lcHandler = 
				new XlsxLightCellsDataHandlerImpl(
						rowData, layout, rowHandler, 
						traverseOnlyHeader(properties));
		opts.setLightCellsDataHandler(lcHandler);
		
		// Parse the contents of the workbook with LightCells. This is done by
		// passing the opts as parameter to the Workbook constructor. The 
		// Workbook constructor will complete only when LightCells has 
		// traversed and notified us about all the data in the Workbook.
		createWorbookFromDataSource(dataSource,opts);
		try {
			lcHandler.handleUnprocessedData();
		}  catch(Exception ex) {
			String msg = String.format(
					"Error occured while processing file");		
			LOGGER.error(msg, ex);
			throw new FileProcessingException(msg, ex);
		}
				
	}
	
	private void createWorbookFromDataSource(Object dataSource,
			LoadOptions opts) {
		// Get the full file path, so that we can use it to open a workbook.
		
		if (dataSource instanceof InputStream) {
			InputStream inputStream = (InputStream) dataSource;
			try {
				// We don't care about the return workbook, as all the 
				// processing will be done in the DataHandler itself
				new Workbook(inputStream, opts);
				return;
			} catch (IOException ex) {
				// Handle IOExcepton separately to inform the caller whether
				// it's a file path/permission related issue.
				String msg = String
						.format("Unable to load/read from Input Stream");
				LOGGER.error(msg, ex);
				throw new FileProcessingException(msg, ex);
			} catch (ExcelReadingTerminatedException ex) {
				// Need not do anything here. This is just a way of escaping
				// from the LightCellsDataHandler implementation.
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Excel Reading Terminated: Reason is -> %s", 
							ex.getMessage()), ex);					
				}
				return;
			} catch (Exception ex) {
				String msg = String
						.format("Error occured while processing Input Stream");
				LOGGER.error(msg, ex);
				throw new FileProcessingException(msg, ex);
			} 
			
		}

		if (dataSource instanceof String) {
			String filePath = (String) dataSource;
			try {
				// We don't care about the return workbook, as all the 
				// processing will be done in the DataHandler itself								
				new Workbook(filePath, opts);
				return;
			} catch (IOException ex) {
				// Handle IOExcepton separately to inform the caller whether
				// it's a file path/permission related issue.
				String msg = String.format(
						"Unable to load/read from the file: '%s'", filePath);
				LOGGER.error(msg, ex);
				throw new FileProcessingException(msg, ex);
			}  catch (ExcelReadingTerminatedException ex) {
				// Need not do anything here. This is just a way of escaping
				// from the LightCellsDataHandler implementation.
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format(
							"Excel Reading Terminated: Reason is -> %s", 
							ex.getMessage()), ex);					
				}
				return;
			} catch (Exception ex) {
				String msg = String.format(
						"Error occured while processing file: '%s'", filePath);
				LOGGER.error(msg, ex);
				throw new FileProcessingException(msg, ex);
			}
		}
		String msg = String
				.format("Invalid Data Type encountered from DataSource");
		throw new FileProcessingException(msg);
	}
}
