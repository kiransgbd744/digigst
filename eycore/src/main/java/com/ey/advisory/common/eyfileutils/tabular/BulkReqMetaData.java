package com.ey.advisory.common.eyfileutils.tabular;

import java.util.LinkedHashMap;
import java.util.Map;

public class BulkReqMetaData {
	
	/**
	 * Usually, huge file/data processing involves recording the file details/
	 * data details first, before starting the processing and assigning an ID
	 * to the file or the request. Since we're deailing with different types of
	 * requests, we call this field bulkReqNo instead of fileId or fileNo. 
	 */
	private int bulkReqNo;
	
	/**
	 * If the bulk request  is a file processing request, then this field
	 * represents the name of the file.
	 */
	private int fileName;
	
	/**
	 * The type of content that we're processing. Some of the common formats are
	 * xls, xlsx, xlsb, xlsm, csv, tsv, psv etc.
	 */
	private String mimeType;
	
	/**
	 * Total number of chunks within this file
	 */
	private int noOfChunks;
	
	/**
	 * No. of data blocks available in this file. This is the sum of the number
	 * of data blocks available within each of the chunk of the distribution.
	 */
	private int noOfDataBlocks;
	
	
	/**
	 * Total No. of Rows within the Tabular Data source. This should be equal
	 * to the number of data rows within the file.
	 */
	private int noOfRows;
	
	/**
	 * No of non-contiguous data blocks within the file.
	 */
	private int noOfScatteredDataBlocks;
	
	/**
	 * The map of all the chunks for the file. The key is the chunk number and
	 * the value is the actual chunk.
	 */
	private Map<Integer, Chunk> chunkMap = new LinkedHashMap<>();

	/**
	 * Instantiate the ChunkDistribution with the basic file Details.
	 * 
	 * @param bulkReqNo
	 * @param fileName
	 * @param mimeType
	 */
	public BulkReqMetaData(int bulkReqNo, int fileName, String mimeType) {
		this.bulkReqNo = bulkReqNo;
		this.fileName = fileName;
		this.mimeType = mimeType;
	}
	
	public int getBulkReqNo() {
		return bulkReqNo;
	}

	public int getFileName() {
		return fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public int getNoOfChunks() {
		return noOfChunks;
	}

	public int getNoOfDataBlocks() {
		return noOfDataBlocks;
	}

	public int getNoOfRows() {
		return noOfRows;
	}

	public int getNoOfScatteredDataBlocks() {
		return noOfScatteredDataBlocks;
	}
	
	public Chunk getChunk(int chunkNo) {
		return chunkMap.get(chunkNo);
	}
}
