package com.ey.advisory.common.eyfileutils.tabular;

import java.util.List;

public interface TabularDataLayout {

	public default Integer getHeaderStartRow() { return 0; }

	public default Integer getDataStartRow() { return 1; }

	public default Integer getStartCol() { return 0; }

	public default Integer getColNo(String colName) {
		throw new UnsupportedOperationException(
				"Operation not supported by default");				
	}

	public default ColumnConfig<?> getColConfig(String colName) {
		throw new UnsupportedOperationException(
				"Operation not supported by default");				
	}

	public default ColumnConfig<?> getColConfig(int colNo) {
		throw new UnsupportedOperationException(
				"Operation not supported by default");				
	}

	public Integer getNoOfCols();

	/**
	 * This method stores the trimmed column name and attaches the 
	 * details of the column specified by the ColConfig object to it.
	 * 
	 * @param colName
	 * @param dataConfig
	 * @return
	 */
	public default <T> TabularDataLayout addColConfig(String colName,
			ColDataConfig<T> dataConfig) {
		throw new UnsupportedOperationException(
				"Operation not supported by default");
	}

	public default String getColName(Integer colNo) {
		throw new UnsupportedOperationException(
				"Operation not supported by default");		
	}

	public default List<String> getColName(List<Integer> colNos) {
		throw new UnsupportedOperationException(
				"Operation not supported by default");				
	}

	public default boolean isColPresent(String colName) {
		throw new UnsupportedOperationException(
				"Operation not supported by default");				
	}

}