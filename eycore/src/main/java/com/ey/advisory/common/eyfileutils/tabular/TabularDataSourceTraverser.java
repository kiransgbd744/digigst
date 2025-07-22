package com.ey.advisory.common.eyfileutils.tabular;

import java.util.HashMap;
import java.util.Map;

import com.ey.advisory.common.eyfileutils.tabular.impl.TraverserConstants;

/**
 * The implementations of this interface is responsible for traversing through
 * a TabularDataSoruce, extracting a row of information as an Object[] and 
 * invoking a rowHandler to process it. In our application, there are several
 * instances where we have to traverse through a TabularDataSource (like excel,
 * CSV, PSV, TSV etc) and do various kinds of processing. Some of the processing
 * in different applications involves, building a DataBlock Map by parsing the
 * TabularDataSource OR building MetaData information about DataBlock 
 * distribution within the TabularDataSource, that can be used later to 
 * optimally chunk and process the data files.
 * 
 * The traversing mechanism used can be dependent on the type of DataSource 
 * (Excel file, CSV file etc) and the type of loading used - like loading the 
 * entire file into memory OR using a streaming parser to load cell by cell or
 * line by line. The purpose of this interface is to shield the client from 
 * such details and provide a mechanism by which the client can receive rows 
 * from the Traverser in the form of an Object[]. All the client needs to do
 * is to choose an appropriate TabularDataSourceTraverser instance and invoke
 * the traverse() method by passing the RowHandler object which knows what to
 * do with the row data obtained from the Traverser.
 *
 * There will be different implementations of this interface depending on the 
 * nature of the underlying tabular data source. The only capability required
 * from the underlying data source is a way by which the data source can be 
 * sequentially iterated and rows can be extracted and split into Object[]. An
 * implementation of this interface can be defined for any data source that 
 * supports the above capability.
 * 
 */
public interface TabularDataSourceTraverser {	
	
	/**
	 * This method is responsible for extracting row data from the DataSource
	 * and handing it over to the RowHandler object for processing, by invoking
	 * the handleRow() method of the RowHandler. If that method returns false,
	 * then the traversing is terminated. This gives control to the caller to
	 * parse the DataSource only till the point required and will be useful for
	 * very large DataSource objects.
	 * 
	 * @param dataSource The object representing a data source (e.g. 
	 * 		Aspose Worksheet object)
	 * 
	 * @param layout The layout of the tabular data source.
	 * 
	 * @param Any additional input to be passed to the traverser
	 * 
	 * @param rowHandler
	 * 
	 */
	public void traverse(Object dataSource, 
			TabularDataLayout layout, RowHandler rowHandler, 
			Map<String, Object> properties);
	
	
	public default void traverseHeaderOnly(Object dataSource, 
			TabularDataLayout layout, RowHandler rowHandler, 
			Map<String, Object> properties) {
		Map<String, Object> props = (properties == null) ? 
				new HashMap<>() : properties;
		// Set the readOnlyHeader property, so that the traverser
		// can retrun immediately after reading the header row.
		props.put(TraverserConstants.READ_ONLY_HEADER_ROW, true);
		traverse(dataSource, layout, rowHandler, props);
	}

}
