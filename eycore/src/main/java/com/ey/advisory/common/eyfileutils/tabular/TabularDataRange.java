package com.ey.advisory.common.eyfileutils.tabular;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class represents the start and end rows of a contiguous range of 
 * information in a TabularData source. An collection of instances of this 
 * class can be passed on to a processor code to inform the ranges of data on 
 * which processing should be done.
 *
 */
public class TabularDataRange {
	
	/**
	 * Zero based index of the start row within the tabular data source.
	 */
	@Expose @SerializedName("st")
	private int startRow;
	
	/**
	 * One based index of the count of the rows in this range.
	 */
	@Expose @SerializedName("cnt")
	private int noOfRows;
	
	public TabularDataRange(int startRow, int noOfRows) {
		super();
		this.startRow = startRow;
		this.noOfRows = noOfRows;
	}

	public int getStartRow() {
		return startRow;
	}

	public int getNoOfRows() {
		return noOfRows;
	}

	public int getEndRow() {
		return startRow + noOfRows - 1;
	}

	public void incrementNoOfRows(int noOfRows) {
		this.noOfRows += noOfRows;
	}
	
	@Override
	public String toString() {
		return "TDR: [startRow=" + startRow + ", noOfRows="
				+ noOfRows + ", endRow=" + getEndRow() + "]";
	}

}
