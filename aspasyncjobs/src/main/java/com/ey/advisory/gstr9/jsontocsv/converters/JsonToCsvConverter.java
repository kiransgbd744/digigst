package com.ey.advisory.gstr9.jsontocsv.converters;

import java.io.BufferedWriter;
import java.io.IOException;

import com.google.gson.stream.JsonReader;

public interface JsonToCsvConverter {

	/**
	 * Return the CSV Header String for the Chosen Converter. This should
	 * be overridden for all converters.
	 * @return
	 */
	public String[] getCsvHeaderStrings();
	
	/**
	 * Convert the json string in the reader to Csv and write to the Csv Writer
	 * buffer and the merged buffer. The mergedFileWriter can be null, if there
	 * is only one json involved. 
	 *  
	 *  
	 * @param inputFilePath
	 * @param outputFilePath
	 * @param mergedFileWriter
	 * @param isFirstFile
	 */
	public void convertJsonTOCsv(JsonReader reader, 
			BufferedWriter... csvWriters) throws IOException;
	
	
	/**
	 * This method returns how many outputs this converter will return after
	 * the json to csv conversion process. Some converters can change one 
	 * input json to multiple output csv files. Such converters should override
	 * this method and return the appropriate count.
	 * 
	 * @return
	 */
	public default int getNoOfConvOutputs() { return 1; }
	
}
