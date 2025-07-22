package com.ey.advisory.common.eyfileutils.tabular;

import java.util.List;
import java.util.Map;

/**
 * This interface represents the contract for building a Map from the Source of 
 * data (Excel, CSV, TSV, PSV etc). The type of DataSource that is used and the
 * mechanism of parsing the data is shielded from this MapBuilder by the
 * TabularDataSourceTraverser object, which is responsible for providing a 
 * row by row feed to this MapBuilder. As and when this MapBuilder encounters
 * a row, it can go on adding the values to the map. Since it has a list of
 * data ranges as input, the TabularDataSourceTraverser needs to parse only 
 * till the maximum row value available in these ranges. 
 * 
 * @param <K> The data type of the DataBlockKey.
 * 
 */
public interface DataBlockMapBuilder<K> {
	
	/**
	 * The implementation of this method is responsible for building a map of
	 * DataBlocks with the map key as DataBlockKey 
	 * (represented by the generic parameter 'K') and value as a List of 
	 * Object[], where each object array in the list is a row in the tabular
	 * data source. The input to this method is the DataSource which is 
	 * represented by the generic parameter 'S', a 'BulkUploadConfig' which
	 * represents the layout of the tabular data in the DataSource (like the
	 * logical names of each column) and the list of row ranges to consider 
	 * while building the map. A row range is represented by the 
	 * 'TabularDataRange' instance.
	 * 
	 * @param traverser the Traverser object responsible for 
	 * 
	 * @param config the BulkUploadConfig instance that represents the layout
	 * 		of the uploaded file. This instance can be used to find out the
	 * 		column numbers for a given column name.
	 * 
	 * @param dataRanges The list of row ranges to parse to load the data. This
	 * 		is usually used when a huge data file is divided into sections and
	 * 		the map is built only from one or more of these sections. The 
	 * 		row ranges specified in these objects should not include header
	 * 		rows. Only valid data rows should be included. This value can be 
	 * 		null OR an empty list. If this parameter is null or an empty list, 
	 * 		we need to consider the entire data sheet. In this case, we need to
	 * 		find the TabularDataRange excluding the header, using the
	 * 		information contained in the BulkUploadConfig object. 
	 * 
	 * @return The Map containing DataBlocks as values and the corresponding
	 * 		DataBlockKey as the key.
	 */
	public Map<K, List<Object[]>> buildDataBlockMapFromSource(
				TabularDataSourceTraverser traverser, TabularDataLayout config, 
				TabularDataRanges dataRanges);
}
