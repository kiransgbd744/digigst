package com.ey.advisory.common.eyfileutils.tabular.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.
					SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.ey.advisory.common.eyfileutils.FileProcessingException;
import com.ey.advisory.common.eyfileutils.tabular.HaltProcessingException;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;

public class XlsxXssfDataTraverser implements TabularDataSourceTraverser {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(XlsxXssfDataTraverser.class);
	
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
	
		if(LOGGER.isInfoEnabled()) {
			String msg = String.format(
					"Row Data Size is: '%d'", rowData.length);
			LOGGER.info(msg);
		}

		// Get the full file path, so that we can use it to open a workbook.
		String filePath = (String) dataSource;
		File file = new File((String) dataSource);
		
		// Check if the file exists. If not, throw an exception and terminate
		// processing.
		if(!file.exists()) {
			String msg = String.format(
					"The file '%s' does not exist. "
					+ "Skipping file traversal", filePath);
			LOGGER.error(msg);
			throw new FileProcessingException(msg);
		}
		
		// Process the file using Apache POI XSSF. If any failure happens 
		// while processing, a FileProcessingException is thrown after closing
		// all open resources.
		processXlsxFile(file, layout, rowHandler);
		
	}
	
	/**
	 * This method initiates the processing of the file. Since OPCPackage is
	 * an Autocloseable resource, if any exception occurs within the try block,
	 * the open OPCPackage will be closed and all resources freed and the 
	 * exception will be propagated to the caller.
	 * 
	 * @param file
	 * @throws Exception
	 */
	private void processXlsxFile(File file, 
				TabularDataLayout layout, RowHandler rowHandler) {
		// To tell the unzip process that the file that it's unzipping can 
		// result in a very huge file.
		ZipSecureFile.setMinInflateRatio(0);
		
		try(OPCPackage pkg = OPCPackage.open(file, PackageAccess.READ)) {
			 process(file, pkg, layout, rowHandler);
		} catch(Exception ex) {
			// The catch block will run after the Auto-closeable OPCPackage 
			// resource declared within the try-with-resources is closed.
			String msg = String.format(
					"Exception while processing the file '%s'", file.getPath());
			LOGGER.error(msg, ex);
			// Throw back a FileProcessingException which is derived from the
			// common Application exception of our Application.
			throw new FileProcessingException(ex);			
		}
	}
	
	private void process(File file, OPCPackage xlsxPackage, 
			TabularDataLayout layout, RowHandler rowHandler) 
					throws IOException, OpenXML4JException, 
						SAXException, ParserConfigurationException {
		
		System.out.println("About to load strings table...");
        ReadOnlySharedStringsTable strings = 
        		new ReadOnlySharedStringsTable(xlsxPackage);
//        System.out.println("Total String count = " + strings.getCount());
//        System.out.println("Unique string count = " + strings.getUniqueCount());
//        System.out.println("Loaded strings table!!");
//        System.out.println("Last value = " + strings.getEntryAt(1042292));
        
        XSSFReader xssfReader = new XSSFReader(xlsxPackage);
        StylesTable styles = xssfReader.getStylesTable();
        XSSFReader.SheetIterator iter = 
        		(XSSFReader.SheetIterator) xssfReader.getSheetsData();
        
        
		// Firstly, allocate a row that will be used store a row data. The 
		// number of columns within the data source will be the size of the
		// single dimensional array. Every time, this class fetches a new row
		// of information from the excel sheet, it clears this array and loads
		// the new row contents into it and invokes the row cell parser.
		Object[] rowData = new Object[layout.getNoOfCols()];
		
        // Process only the first sheet. Using an 'if' instead of a 'while'.
        // In future, if we need to process multiple sheets, we can convert
        // this into a while. Currently, this will process only the first 
        // sheet.
        if(iter.hasNext()) {
            try (InputStream stream = iter.next()) {
            	// Use an 8MB buffer size
            	BufferedInputStream bis = 
            			new BufferedInputStream(stream, 8192);
            	SheetContentsHandler handler = new XlsxXssfDataHandlerImpl(
            			rowData, layout, rowHandler);
            	LOGGER.debug("About to process the sheet");
                processSheet(styles, strings, handler, bis);
                LOGGER.debug("Processed the sheet");
                // Perform a flush for any unprocessed data within the
                // Row Handler.
                rowHandler.flush(layout);
            }
        } else {
        	// If no sheets are available for processing, then throw a 
        	// FileProcessingException.
        	String msg = String.format(
        			"The file '%s' does not have any sheets.", file.getPath());
        	LOGGER.error(msg);
        	throw new FileProcessingException(msg);
        }
	}
	
	private void processSheet(StylesTable styles,
			ReadOnlySharedStringsTable strings,
			SheetContentsHandler sheetHandler, InputStream sheetInputStream)
			throws IOException, SAXException, ParserConfigurationException {
		
		DataFormatter formatter = new DataFormatter();
		InputSource sheetSource = new InputSource(sheetInputStream);

		XMLReader sheetParser = SAXHelper.newXMLReader();
		  sheetParser.setFeature("http://xml.org/sax/features/external-general-entities", false);
		  	    sheetParser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		  			    sheetParser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			
		ContentHandler handler = new XSSFSheetXMLHandler(styles, null,
				strings, sheetHandler, formatter, false);
		sheetParser.setContentHandler(handler);
		try {
			LOGGER.debug("About to parse the Excel sheet...");
			sheetParser.parse(sheetSource);
			LOGGER.debug("Done parsing the Excel sheet...");
		} catch(HaltProcessingException ex) {
			// This exception occurs when the Content handler decides to
			// no longer parse the file because of some business condition.
			// Since this is not an exceptional condition, we need not
			// take any action here. Log this event and proceed.
			if(LOGGER.isDebugEnabled()) {
				String msg = "HaltProcessingException "
						+ "encountered because of business rules evaluation. ";	
				LOGGER.debug(msg);
			}
		}

	}
	
}
