package com.ey.advisory.common.eyfileutils.tabular;

public class DummyTabularDataLayout implements TabularDataLayout {

	protected int headerStartRow;
	protected int dataStartRow;
	protected int startCol;
	protected int noOfCols;

	public DummyTabularDataLayout(
			int headerStartRow, int dataStartRow, 
			int startCol, int noOfCols) {
		super();
		this.headerStartRow = headerStartRow;
		this.dataStartRow = dataStartRow;
		this.startCol = startCol;
		this.noOfCols = noOfCols;
	}
	
	public DummyTabularDataLayout(int noOfCols) {
		this.headerStartRow = 0;
		this.dataStartRow = 1;
		this.startCol = 0;
		this.noOfCols = noOfCols;
	}
	
	@Override
	public Integer getNoOfCols() {	
		return this.noOfCols;
	}

	@Override
	public Integer getHeaderStartRow() {
		return this.headerStartRow;
	}

	@Override
	public Integer getDataStartRow() {
		return this.dataStartRow;
	}

	@Override
	public Integer getStartCol() {
		return this.startCol;
	}

	@Override
	public String toString() {
		return "DummyTabularDataLayout [headerStartRow=" + headerStartRow
				+ ", dataStartRow=" + dataStartRow + ", startCol=" + startCol
				+ "]";
	}
	
}
