package com.ey.advisory.common.eyfileutils.tabular;


public class ColumnConfig<T> {
	
	protected String colName;
	protected ColDataConfig<T> colDataConfig;
	
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public ColDataConfig<T> getColDataConfig() {
		return colDataConfig;
	}
	public void setColDataConfig(ColDataConfig<T> colDataConfig) {
		this.colDataConfig = colDataConfig;
	}
	
}
