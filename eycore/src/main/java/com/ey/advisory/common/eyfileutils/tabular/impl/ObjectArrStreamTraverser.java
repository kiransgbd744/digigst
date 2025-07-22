package com.ey.advisory.common.eyfileutils.tabular.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.common.eyfileutils.FileProcessingException;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;

/**
 * This class is responsible traversing an object list and invoking the
 * RowHandler object for each row obtained. This is useful to iterate over
 * projected values obtained from Spring repository methods/HQL results. 
 * 
 * @author Sai.Pakanati
 *
 */
public class ObjectArrStreamTraverser implements TabularDataSourceTraverser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(
					ObjectArrStreamTraverser.class);
	@Override
	public void traverse(Object dataSource, TabularDataLayout layout,
			RowHandler rowHandler, Map<String, Object> properties) {
		
		@SuppressWarnings("unchecked")
		Stream<Object[]> stream = (Stream<Object[]>) dataSource;
		if(stream == null) {
			String msg = "The Object Stream to be traversed cannot be null.";
			LOGGER.error(msg);
			throw new FileProcessingException();
		}

		// Get the iterator for the stream and execute the Row Handler. Since
		// we're extracting the iterator from the stream and doing a manual
		// iteration, we cannot leverage the parallel nature of stream 
		// processing. This is required here to provide the row index to the
		// handleRow method. If processed in parallel, each subset of the
		// stream elements will start with 0 index and increment its count.
		Iterator<Object[]> iter = stream.iterator();
		int idx = 0;
		if (iter != null) {
		 	
		while(iter.hasNext()) {
			boolean proceed= rowHandler.handleRow(idx++, iter.next(), layout);
			if(!proceed) { break; }
		}
		}
		
		// Invoke the flush method on the RowHandler to flush any unprocessed
		// data stored within the row handler.
		rowHandler.flush(layout);
	}
}
