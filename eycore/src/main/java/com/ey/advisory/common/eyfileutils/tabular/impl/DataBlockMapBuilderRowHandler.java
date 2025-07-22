package com.ey.advisory.common.eyfileutils.tabular.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.common.eyfileutils.FileProcessingException;
import com.ey.advisory.common.eyfileutils.tabular.Chunk;
import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataRange;

/**
 * This class is responsible for building a map from the data blocks available
 * within a chunk. The chunk information is passed in the constructor of this
 * class. A traverser will scan the file and invoke the instance of
 * this class, for each row it encounters. This class will check if the row
 * is a part of the chunk under consideration; and if so, it calculates the 
 * data block key for the row and adds it to the map. Once the chunk ranges 
 * are over, the handleRow method will return false to the caller, to indicate
 * it to stop further processing of the tabular data source.  
 *  
 * @author Sai.Pakanati
 *
 * @param <K>
 */
public final class DataBlockMapBuilderRowHandler<K> implements RowHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(
			DataBlockMapBuilderRowHandler.class);
	
	private List<TabularDataRange> ranges;
	
	private Iterator<TabularDataRange> rangeIter;
	
	private TabularDataRange curRange;
	
	private DataBlockKeyBuilder<K> keyBuilder;
	
	/**
	 * The map to be populated with the the data from the file.
	 */
	private Map<K, List<Object[]>> dataBlockMap = new LinkedHashMap<>();
	
	public DataBlockMapBuilderRowHandler(Chunk chunk, 
					DataBlockKeyBuilder<K> keyBuilder) {
		
		this.keyBuilder = keyBuilder;
		
		ranges = chunk.getRanges();
		rangeIter = ranges.iterator();
		
		// If there are no data ranges within the chunk, then throw an
		// exception.
		if(ranges.isEmpty()) {
			String msg = "Invalid Chunk Encountered with no Data Ranges";
			LOGGER.error(msg);
			throw new FileProcessingException(msg);
		}
		
		// Initialize the current range.
		if(rangeIter.hasNext()) { curRange = rangeIter.next(); }
		
	}
	
	@Override
	public boolean handleRow(int rowNo, Object[] row,
					TabularDataLayout layout) {

		// If the current row is outside the current range, then switch to the
		// next range, if available. If no other range is available, set the
		// current range to null.
		if(rowNo > curRange.getEndRow()) {
			curRange = rangeIter.hasNext() ? rangeIter.next() : null;
		}
		
		// If there is no current range, then return false so that the 
		// traverser can stop processing any further lines.
		if(curRange == null) { return false; }
		
		// If the current row is within one of the ranges in the chunk, then
		// extract the current row and add it to the map array.
		if((rowNo >= curRange.getStartRow()) && 
				(rowNo <= curRange.getEndRow())) {
			// First clone the row data array. This is required as the 
			// traverser may reuse the Object[] that is passed to the 
			// handleRow method.
			Object[] arr = row.clone();
			K key = keyBuilder.buildDataBlockKey(arr, layout);
			
			// Add the new key and cloned array to the map.
			dataBlockMap.computeIfAbsent(
					key, k -> new ArrayList<Object[]>())
						.add(arr);
		}
		
		return true;
	}

	public Map<K, List<Object[]>> getDataBlockMap() {
		// We're returning the mutable map here. It's the caller's 
		// responsibility not to alter this map. Since several such maps
		// will be created and destroyed in the async executor app, creating
		// and returning immutable maps is a considerable overhead.
		return this.dataBlockMap;
	}

}
