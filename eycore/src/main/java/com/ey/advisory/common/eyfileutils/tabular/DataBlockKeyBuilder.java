package com.ey.advisory.common.eyfileutils.tabular;

/**
 * This class can be used when tabular format data (like Excel, CSV, 
 * TSV, PSV etc) needs to be parsed and grouped. Usually, the tabular data 
 * files contain chunks of data organized into rows. The first step in 
 * processing these rows is to group these rows into Entities or DataBlocks. So,
 * The file can be considered as a collection of data blocks, each of which in 
 * turn consists of several rows. For example, in the E-way bill application,
 * the upload file consists of Documents. All the rows belonging to a single
 * Document can be called as a DataBlock. So, this file is nothing but a 
 * collection of Document DataBlocks.
 * 
 * In order to group the file data into DataBlocks, we need to have a key that
 * distinguishes one DataBlock rows from another. This key will be dependent
 * on the domain. But, the fact is that this key is embedded in each row within
 * the tabular data file. So, it is safe to assume that there exists a domain
 * specific logic to create the DataBlockKey from each row. This interface 
 * represents the factory method that performs this activity. Concrete 
 * implementations of this interface will be domain specific and will be
 * responsible for the creation of the DataBlockKey from any row in the tabular
 * format file for that domain. Since every row in a tabular format data is 
 * basically a collection of column data, it can be represented as an array
 * of Object. Hence, this interface takes an Object[] and builds the 
 * DataBlockKey of type 'T' from it. The parsing and creation of the Object
 * array should be done by some other component before this interface 
 * implementation is invoked.
 * 
 * For example, the implementation of this interface for the 'GenerateEWB'
 * functionality will have 'EWBDocKey' class as the type of the DataBlockKey and
 * from each row, it will be possible to create the data block key.
 *
 * @param <T> The data type of the DataBlockKey
 */
public interface DataBlockKeyBuilder<T> {
	
	/**
	 * The method responsible for building a DataBlockKey for a row of data
	 * from a tabular data source (like Excel, CSV, TSV, PSV etc). Since every 
	 * row in a tabular format data is basically a collection of column data, 
	 * it can be represented as an array of Object. Hence, this interface 
	 * takes an Object[] and builds the DataBlockKey of type 'T' from it.
	 *
	 * @param arr The array containing the column data belonging to a single 
	 * 		row within a tabular format data source.
	 * 
	 * @param config The BulkUploadConfig instance which contains the layout
	 * 		of the tabular data file. This can be used to find out columns by
	 * 		names, instead of numbers, thereby shielding the implementation
	 * 		from changes caused by addition/removal of columns.
	 * 
	 * @return The DataBlockKey for the row belonging to the tabular 
	 * 		data source.
	 * 
	 */
	public T buildDataBlockKey(Object[] arr, TabularDataLayout config);

	String buildComprehenceDataBlockKey(Object[] arr, TabularDataLayout config);

	public String buildItc04DataBlockKey(Object[] arr,
			TabularDataLayout layout);
}
