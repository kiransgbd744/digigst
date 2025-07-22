package com.ey.advisory.common.eyfileutils.tabular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the configuration of the uploaded data (or in other 
 * words, this class describes the layout & properties of the uploaded data). 
 * This class assumes that the uploaded data is available in a tabular format 
 * (i.e 2D Array). This config can be used to describe any data in a 2D tabular 
 * format - like spreadsheets or in memory 2D arrays.
 *
 */
public class DefaultTabularDataLayout implements TabularDataLayout {
	
	protected int headerStartRow;
	protected int dataStartRow;
	protected int startCol;

	public DefaultTabularDataLayout(
			int headerStartRow, int dataStartRow, int startCol) {
		super();
		this.headerStartRow = headerStartRow;
		this.dataStartRow = dataStartRow;
		this.startCol = startCol;
	}

	protected List<ColumnConfig<?>> colConfigs = 
			new ArrayList<ColumnConfig<?>>();
	protected Map<String, Integer> nameToIdxMap = 
			new HashMap<String, Integer>();
	protected Map<String, ColumnConfig<?>> nameToConfigMap = 
			new HashMap<String, ColumnConfig<?>>();

	public Integer getHeaderStartRow() {
		return headerStartRow;
	}

	public void setHeaderStartRow(int headerStartRow) {
		this.headerStartRow = headerStartRow;
	}
	
	public Integer getDataStartRow() {
		return dataStartRow;
	}

	public void setDataStartRow(int dataStartRow) {
		this.dataStartRow = dataStartRow;
	}

	public Integer getStartCol() {
		return startCol;
	}

	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}

	public Integer getColNo(String colName) {
		String columnName = (colName != null) ? colName.trim() : null;		
		return nameToIdxMap.get(columnName);
	}

	public ColumnConfig<?> getColConfig(String colName) {
		String columnName = (colName != null) ? colName.trim() : null;
		return nameToConfigMap.get(columnName);
	}

	public ColumnConfig<?> getColConfig(int colNo) {
		return colConfigs.get(colNo);
	}

	public Integer getNoOfCols() {
		return colConfigs.size();
	}
	
	/**
	 * This method stores the trimmed column name and attaches the 
	 * details of the column specified by the ColConfig object to it.
	 * 
	 * @param colName
	 * @param dataConfig
	 * @return
	 */
	public <T> TabularDataLayout addColConfig(String colName,
			ColDataConfig<T> dataConfig) {
		String columnName = (colName != null) ? colName.trim() : null;
		ColumnConfig<T> config = new ColumnConfig<T>();
		config.setColName(columnName);
		config.setColDataConfig(dataConfig);
		colConfigs.add(config);
		nameToIdxMap.put(columnName, colConfigs.size() - 1);
		nameToConfigMap.put(columnName, config);
		return this;
	}
	
	public String getColName(Integer colNo) {
		ColumnConfig<?> config = getColConfig(colNo);
		String colName = config.getColName();
		return colName;
	}
	
	public List<String> getColName(List<Integer> colNos) {
		List<String> colNames = new ArrayList<String>();
		for(Integer colNo: colNos) {
			ColumnConfig<?> config = getColConfig(colNo);
			colNames.add(config.getColName());
		}
		return colNames;
 	}

	/**
	 * Return a copy of the name to index map, for debugging purposes.
	 * The method returns a copy of the existing map so that the internal 
	 * state of the SpreadhsheetUploadConfig object is not altered by the 
	 * caller.
	 * 
	 * @return
	 */
	public Map<String, Integer> getNameToIdxMap() {
		return Collections.unmodifiableMap(nameToIdxMap);
	}
	
	public boolean isColPresent(String colName) {
		String columnName = (colName != null) ? colName.trim() : null;
		return nameToIdxMap.containsKey(columnName);
	}
}
