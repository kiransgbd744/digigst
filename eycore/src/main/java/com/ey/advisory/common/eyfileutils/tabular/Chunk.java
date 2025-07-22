package com.ey.advisory.common.eyfileutils.tabular;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class represents a chunk that can be processed independently. A chunk 
 * may not always be contiguous. Since a tabular data source contains a
 * collection of DataBlocks, a chunk can become non contiguous, if a Datablock
 * by itself is non-contiguous (i.e. the data belonging to the DataBlock is
 * scattered in the file/data source). For example, the line items of an invoice
 * are scattered in the file.
 *
 */
public class Chunk {
	
	/**
	 * Get the unique number of the chunk. The chunk builder takes care of
	 * assigning unique number to the chunk, while scanning the Tabular 
	 * Data Source and building the meta data.
	 */
	
	@Expose @SerializedName("cNo")
	private int chunkNo;
	
	@Expose @SerializedName("blkCnt")
	private int noOfDataBlocks;
	
	@Expose @SerializedName("rCnt")
	private int noOfRows;
	
	@Expose @SerializedName("scat")
	private boolean isScattered = false;
	
	@Expose @SerializedName("rngs")
	private List<TabularDataRange> ranges = new ArrayList<>();
	
	/**
	 * Constructor to create a chunk with a chunk number.
	 * 
	 * @param chunkNo the unique number by which a chunk can be identified 
	 * 		for a file. The chunk builder should take care that duplicate 
	 * 		numbers are not assigned for chunks.
	 */
	public Chunk(int chunkNo) { this.chunkNo = chunkNo; }
	
	/**
	 * This method can be used to add a list of Data Blocks to the chunk.
	 * 
	 * @param noOfDataBlocks
	 * @param noOfRows
	 * @param startRow
	 */
	public Chunk addDataBlock(int noOfRows, int startRow, boolean newInvoice) {
		if(newInvoice) { this.noOfDataBlocks++; }
		this.noOfRows += noOfRows;
		
		// Get the existing last range and check if the new startRow is one
		// more than the existing end row. If so, we don't need to create a 
		// new range. Instead, we can just modify the existing range end row.
		
		boolean startNewRange = true;
		if(!ranges.isEmpty()) {
			TabularDataRange lastRange = ranges.get(ranges.size() - 1);
			int lastRow = lastRange.getEndRow();
			if(lastRow == startRow - 1) { 
				startNewRange = false; 
				lastRange.incrementNoOfRows(noOfRows);
			}			
		}

		// If a new range needs to be created, then create it and add it to the
		// list of ranges.
		if(startNewRange) {
			TabularDataRange range = new TabularDataRange(startRow, noOfRows);
			ranges.add(range);
		} 
		
		// If the number of ranges in the chunk is greater than one, then it 
		// means that invoices within the chunk are scattered within different
		// ranges of the file.
		if(ranges.size() > 1) { this.isScattered = true; }
		return this;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	public int getNoOfDataBlocks() {
		return noOfDataBlocks;
	}

	public int getNoOfRows() {
		return noOfRows;
	}

	public boolean isScattered() {
		return isScattered;
	}

	public List<TabularDataRange> getRanges() {
		return ranges;
	}

	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		String msg = "Chunk [chunkNo=" + chunkNo + ", noOfDataBlocks="
				+ noOfDataBlocks + ", noOfRows=" + noOfRows + ", isScattered="
				+ isScattered;
		builder.append(msg);
		builder.append("\n");
		for(TabularDataRange range: ranges) {
			builder.append("\t");
			builder.append(range.toString());
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
}
