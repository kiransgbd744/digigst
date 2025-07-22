package com.ey.advisory.common.eyfileutils.tabular.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.common.eyfileutils.tabular.Chunk;
import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

public class MetaDataBuilderRowHandler<K> implements RowHandler {
	
	private Map<Integer, Chunk> chunkMap = new HashMap<>();
	
	private Map<K, Integer> dataBlockMap = new HashMap<>();
	
	private DataBlockKeyBuilder<K> keyBuilder;
	
	private K curDataBlockKey = null;
	
	private int lineItemCount = 0;
	
	private int chunkNumber = -1;
	
	private int startRow = 0;
	
	private static final int MAX_NO_OF_ROWS_IN_CHUNK = 10000;
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(MetaDataBuilderRowHandler.class);
	
	
	public MetaDataBuilderRowHandler(DataBlockKeyBuilder<K> keyBuilder) {
		this.keyBuilder = keyBuilder;
	}
	
	@Override
	public boolean handleRow(int rowNo, Object[] row,
			TabularDataLayout layout) {

		// Skip all the rows above the first data row.
		if(rowNo < layout.getDataStartRow()) { return true; }
		
		// If the invoice key is null, return a false value.
		K dataBlockKey = keyBuilder.buildDataBlockKey(row, layout);
		if(dataBlockKey == null) { return false; }
		
		if (curDataBlockKey == null) {
			startRow = rowNo;
			curDataBlockKey = dataBlockKey;
			lineItemCount++;
			allocateNewChunk(); // Allocate a chunk for the first time.
			return true;
		}

		// If the invoice is not changed, keep incrementing the 
		// line item count for the invoice.
		if (dataBlockKey.equals(curDataBlockKey)) {
			lineItemCount++;
			return true;
		} 

		processNewDataBlock(rowNo, dataBlockKey);

		return true;
	}

	private void processNewDataBlock(int rowNo, K invoiceKey) {
		// If the invoice has changed, then reset the startRowNo to the 
		// current row number.		
		if(dataBlockMap.containsKey(curDataBlockKey)) {
			int chunkNo = dataBlockMap.get(curDataBlockKey);
			if(chunkMap.containsKey(chunkNo)) {
				Chunk chunk = chunkMap.get(chunkNo);
				chunk.addDataBlock(lineItemCount, startRow, false);
			} else {
				String msg = String.format(
						"Chunk Map does not contain an "
						+ "entry for ChunkNo: '%d'", chunkNo);
				LOGGER.error(msg);
				throw new IllegalStateException(msg);
			}
		} else {		
			
			Chunk chunk = getCurrentChunk();
			int remainingRows = MAX_NO_OF_ROWS_IN_CHUNK - chunk.getNoOfRows();
			// Allocate a new chunk only if the existing chunk has some data.
			// If it's a fresh chunk, then use the same chunk to add the 
			// data block
			boolean allocNewChunk = (remainingRows < MAX_NO_OF_ROWS_IN_CHUNK) 
					&& (lineItemCount > remainingRows);
			if(allocNewChunk) { chunk = allocateNewChunk(); }
			
			chunk.addDataBlock(lineItemCount, startRow, true);
			
			dataBlockMap.put(curDataBlockKey, chunkNumber);
		}
		
		startRow = rowNo;
		lineItemCount = 1;
		curDataBlockKey = invoiceKey;
	}	
	
	private Chunk allocateNewChunk() {
		chunkNumber++;
		Chunk chunk = new Chunk(chunkNumber);
		chunkMap.put(chunkNumber, chunk);
		return chunk;
	}
	
	private Chunk getCurrentChunk() {
		return chunkMap.get(chunkNumber);
	}
	

	public Map<Integer, Chunk> getChunkMap() {
		return chunkMap;
	}

	public void setChunkMap(Map<Integer, Chunk> chunkMap) {
		this.chunkMap = chunkMap;
	}

	@Override
	public void flush(TabularDataLayout layout) {
		int rowNo = startRow + lineItemCount;
		processNewDataBlock(rowNo, null);
		dataBlockMap = null;
	}

}
