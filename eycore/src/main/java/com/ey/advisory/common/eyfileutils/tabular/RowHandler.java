package com.ey.advisory.common.eyfileutils.tabular;

/**
 * The implementation of this interface is responsible for handling a Row 
 * returned by the TabularDataSourceTraverser object. The components that need
 * to iterate over the rows in a tabular data source, can choose an appropriate
 * TabularDataSourceTraverser instance that represents the data source and 
 * initiate the traversing by invoking the traverse() method on the 
 * TabularDataSourceTraverser by passing an instance of a RowHandler to it. This
 * RowHandler instance is responsible for processing the rows that are given
 * by the traverser during the parsing process. 
 * 
 * The RowHandler instance can decide on when the traversing has to stop and 
 * indicate the TabularDataSourceTraverser to stop traversing by returning false
 * from the handleRow method that is overridden. One of the uses of this is to
 * decide whether data end has reached. For example a particular implementation
 * of the RowHandler can decide that the data has ended once it encounters a 
 * row with certain columns (or all columns) as null values.
 *
 */
@FunctionalInterface
public interface RowHandler {
	
	/**
	 * The callback method responsible for handling the row data encountered
	 * by the TabularDataSourceTraverser object. 
	 *  
	 * @param rowNo The zero based row index that represents the number of the
	 * 		row within the tabular data source. This can be used to determine 
	 * 		if a certain row number has reached to encounter
	 * 
	 * @param row the Object[] that represents the data for a row. Each element
	 * 		of the array represents the column data. Using the BulkUpdateConfig
	 * 		instance, column numbers can be located using column names.
	 * 
	 * @param layout The TabularDataLayout instance that describes the layout
	 * 		of the tabular data source. This parameter can be used if the 
	 * 		RowHandler implementation is stateless. It is better to have a 
	 * 		stateful implementation to process huge tabular files, so that
	 * 		the initialize method can be used to initialize the layout at 
	 * 		first.
	 * 
	 * @return true to indicate that the TabularDataSourceTraverser can continue
	 * 		traversing and invoking this instance. Return 'false' to get the
	 * 		Traverser to stop traversing.
	 * 
	 */
	public boolean handleRow(int rowNo, Object[] row, TabularDataLayout layout);
	
	/**
	 * This method will be invoked after all the rows are handled within the
	 * Tabular DataSource, so that any pending operation can be completed.
	 */
	public default void flush(TabularDataLayout layout) {}
	
	/**
	 * Method to handle the header row if required.
	 * @param rowNo
	 * @param row
	 */
	public default void handleHeaderRow(
			int rowNo, Object[] row, TabularDataLayout layout) {}

}
