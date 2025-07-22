package com.ey.advisory.common.eyfileutils.tabular.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.eyfileutils.FileProcessingException;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * This class uses Open CSV to parse a CSV file, spit each line into an array
 * of strings (by the specified delimiter - need not be a comma always) and 
 * invoke the RowHandler with the Object[] corresponding to each row. Note that
 * the Object[] here will in fact be a String[] (which is different from the
 * Aspose case, where we get other data types as well in this array).
 * 
 * @author Sai.Pakanati
 * 
 */
@Component("OpenCSVCharSepValuesFileTraverser")
public class OpenCSVCharSepValuesFileTraverser 
					implements TabularDataSourceTraverser {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OpenCSVCharSepValuesFileTraverser.class);
	
	private static final String DEFAULT_DELIMITER = ",";
	
	private static final int DEFAULT_BUFFER_SIZE = 8192;
	
	private static final String FILE_DELIMITER_KEY = "csv.file.delimiter";
	
	private static final String BUFFER_SIZE_KEY = "csv.file.buffersize";
	
	
	@Override
	public void traverse(Object dataSource, TabularDataLayout layout,
			RowHandler rowHandler, Map<String, Object> properties) {
		
		int headerStartRow = layout.getHeaderStartRow();
				
		// Get the delimiter character. If no delimiters are configured, then
		// use comma as the delimiter.
		String delimiter = (properties != null) ? 
				(String) properties.get(FILE_DELIMITER_KEY) : null;
		delimiter = (delimiter != null) ? delimiter : DEFAULT_DELIMITER;		
		
		// Get the buffer size to use to read from the csv file.
		Integer bufferSize = (properties != null) ?
				(Integer) properties.get(BUFFER_SIZE_KEY) : null;
		int bufSize = (bufferSize != null) ? bufferSize.intValue() : 
						DEFAULT_BUFFER_SIZE;
				
		// Here, we assume that the data source is a File, we open the file
		// using the CSVReader Constructor and stream the lines to memory.
		
		// Firstly, allocate a row that will be used store a row data. The 
		// number of columns within the data source will be the size of the
		// single dimensional array. Every time, this class fetches a new row
		// of information from the excel sheet, it clears this array and loads
		// the new row contents into it and invokes the row cell parser.
		Object[] rowData = new Object[layout.getNoOfCols()];		
		

		CSVParser parser = new CSVParserBuilder().
				withSeparator(delimiter.charAt(0)).build();
		
		CSVReader reader = null;
		
		try {
			
			reader = createBufferedReader(
					dataSource, parser, bufSize);
			
			// Iterate over the lines returned by the reader and invoke the
			// handleRow method.
			int rowNo = 0;
			for(String[] arr: reader) {
								
				clearArray(rowData);
				// This will ensure that the array passed to the Row Handler
				// will have exact number of columns as mentioned in the 
				// tabular data layout. Hence a copy is required. Otherwise,
				// there might be extra or less number of rows.
				copyToDestArr(arr,  rowData);
				
				// Continue with reading the header row.
				if (rowNo == headerStartRow)  {
					rowHandler.handleHeaderRow(rowNo++, rowData, layout);
					continue;
				}
				
				// If the data start row is not yet reached, then do not 
				// process the row. Instead, keep advancing.
				if (rowNo < layout.getDataStartRow()) {
					rowNo++;
					continue;
				}
				
				// Invoke the row handler object, get the return value to check
				// whether to proceed or not. If not, break the loop and 
				// finish processing the file.
				boolean proceed = rowHandler.handleRow(
						rowNo++, rowData, layout);
				if(!proceed) { break; }
			}
			
			// After the row handler processing is over, invoke the flush 
			// method
			rowHandler.flush(layout);
			
		} catch(FileNotFoundException ex) {
			// This exception will be thrown only if a file path is passed as 
			// as a string. In case of InputStream, we will never encounter 
			// this error.
			String filePath = (String) dataSource;
			String msg = String.format(
					"The file '%s' is not found or not accessible", filePath);
			LOGGER.error(msg, ex);
			throw new FileProcessingException(msg, ex);
		} catch(IOException ex) {
			// This exception can be thrown in both scenarios (i.e. when the
			// data source is a file OR the data source is a file path)
			String dataSourceStr = (dataSource instanceof String) ? 
					String.format(" file: '%s'", (String) dataSource) : 
						"InputStream";
			String msg = String.format(
					"Error while reading from the ", dataSourceStr);
			LOGGER.error(msg, ex);
			throw new FileProcessingException(msg, ex);			
		} finally {			
			if (reader != null) {
				try {
					// This will close the CSV Reader and the chained underlying
					// resources ... InputStreamReader, BufferedInputStream
					// and the underlying input stream.
					reader.close();	
				} catch(Exception ex) {
					LOGGER.error("Closing of CSV Reader "
							+ "failed. This might result in severe "
							+ "resource utilization issues.");
				}
				
			}
		}
	}
	
	
	private CSVReader createBufferedReader(
			Object dataSource, CSVParser parser, int bufSize) 
					throws FileNotFoundException, IOException {
		if (dataSource instanceof String) {
			String filePath = (String) dataSource;
			File file = new File(filePath);
			return new CSVReaderBuilder(
					new BufferedReader(new FileReader(file), bufSize)).
							withCSVParser(parser).build();
		}
		
		if (dataSource instanceof InputStream) {
			InputStream stream = (InputStream) dataSource;
			return new CSVReaderBuilder(new InputStreamReader(
					new BufferedInputStream(stream, bufSize))).
							withCSVParser(parser).build();			
		}
		
		throw new AppException("The data source for CSV upload should either"
				+ " be an InputStream or a file path.");
	}
	

	private void clearArray(Object[] arr) {
		for(int i = 0; i < arr.length; i++) {
			arr[i] = null;
		}
	}		
	
	private void copyToDestArr(String[] source, Object[] dest) {
		int size = dest.length;
		
		for(int i = 0; i < source.length; i++) {			
			// Fill only upto the capacity of the dest arrray
			if(i >= size) break;			
			dest[i] = source[i];
		}		
	}
}
