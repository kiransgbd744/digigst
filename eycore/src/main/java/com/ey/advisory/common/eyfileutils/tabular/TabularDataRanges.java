package com.ey.advisory.common.eyfileutils.tabular;

/**
 * The implementation of this interface can store information about data ranges
 * in a compressed format. The number of data ranges available in a file/data
 * source can be very huge. For example, for a file containing 1 million
 * invoices, the number of Data Ranges with 200 invoice per range, will be 
 * 5000. In such cases, the data block information can be stored in an efficient
 * manner, by the implementation of this interface and this information can be
 * accessed by invoking the methods of this interface. One of the 
 * implementations of this interface can be a list.
 *
 */
public interface TabularDataRanges {
	
	public int getNoOfRanges();
	
	public TabularDataRange getRangeAt(int i);
	
}
