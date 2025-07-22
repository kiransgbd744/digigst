package com.ey.advisory.gstr9.jsontocsv.processing.tasks;

import java.io.BufferedWriter;
import java.util.List;

import com.ey.advisory.processing.messages.JsonFileArrivalMessage;
import com.google.gson.stream.JsonReader;

/**
 * 
 * NOTE: The implementation of this interface should be a prototype bean (as 
 * we store several data members within the class). DO NOT autowire the 
 * implementation of this interface as a Singleton Spring Bean.
 * 
 * @author Sai.Pakanati
 *
 */

public interface CsvConversionFileSystemStrategy {
	
	public void performPreProcessing(JsonFileArrivalMessage msg);
	
	public List<? extends Object> listInputJsons(JsonFileArrivalMessage msg);
	
	public void flushOutputWriters(List<BufferedWriter> writers);
	
	public List<BufferedWriter> createOutputCsvWriters(int writersCount, 
			JsonFileArrivalMessage msg);
	
	public JsonReader createJsonReader(Object inputJson);
	
	public void cleanupInputJsonReader(JsonReader reader);
	
	public boolean cleanupOutputCsvWriters(List<BufferedWriter> writers);

	/**
	 * 
	 * @param msg
	 */
	public void performPostProcessingOnSuccess(JsonFileArrivalMessage msg);
	
	public void performPostProcessingOnError();	
}
