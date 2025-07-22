package com.ey.advisory.common.eyfileutils.tabular.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ey.advisory.common.eyfileutils.tabular.Chunk;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class DBMetadataBuilderRowHander<K> implements RowHandler {
	
	private Integer curChunkNo = 0;
	private Map<Integer, Chunk> chunkMap = new LinkedHashMap<>();
	private Chunk curChunk = null;
	
	private int startRow = 0;
	
	private static final int MAX_NO_OF_ROWS_IN_CHUNK = 10000;
		
	@Override
	public boolean handleRow(int rowNo, Object[] row,
			TabularDataLayout layout) {	
		
		// The 2nd element of the object array will contain the count of 
		// rows in the result set.
		Integer count = ((Long) row[1]).intValue();
		
		// allocate a new chunk
		if(curChunk == null) { 
			curChunk = new Chunk(0); 
			chunkMap.put(curChunkNo, curChunk);
		}
		
		int remRowsInChunk =  MAX_NO_OF_ROWS_IN_CHUNK - curChunk.getNoOfRows();
		if(remRowsInChunk == MAX_NO_OF_ROWS_IN_CHUNK ||
							count <= remRowsInChunk) { 
			curChunk.addDataBlock(count, startRow, true);
			startRow += count;
			return true;
		}

		// Increment the chunk number, allocate a new chunk and add it to 
		// the map.
		curChunkNo++; // Increment the chunk count for the new chunk
		curChunk = new Chunk(curChunkNo);
		curChunk.addDataBlock(count, startRow, true);
		chunkMap.put(curChunkNo, curChunk);		
		
		// Add the total row count to determine the next start row.
		startRow += count;

		return true;
	}
		
	public Map<Integer, Chunk> getChunkMap() {
		return chunkMap;
	}	

}
