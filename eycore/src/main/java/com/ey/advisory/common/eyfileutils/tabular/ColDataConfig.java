package com.ey.advisory.common.eyfileutils.tabular;

/**
 * This interface represents the configuration for a piece of data that can 
 * appear in the excel sheet cell. The basic idea is that every piece of data
 * will have a destination data type, to which it has to be converted. This 
 * destination data type is the generic parameter that we provide, for 
 * implementation classes. In certain cases, the piece of data is mandatory
 * in a column. In some other cases, if a piece of data is found and is 
 * convertible to the destination data type, then it has to fall in a 
 * certain pre-configured range. This interface contains the necessary methods
 * to check if a data type is convertible to a destination data type, to 
 * convert the input data to the destination data type if possible and to check
 * if the input data falls between a specified range.
 * 
 * This interface can be extended to have additional contracts required to 
 * implement custom validations.
 *
 * @param <T> represents the type of destination data required in the column.
 * 
 */
public interface ColDataConfig<T> {

	/**
	 * Specifies whether the piece of data is required in a cell belonging to
	 * this column.
	 * 
	 * @return
	 */
	public boolean isRequired();

	/**
	 * Specifies the minimum value for a cell belonging to this column. If this
	 * value is null, there will not be a minimum value check.
	 * 
	 * @return
	 */
	public T getMinVal();

	/**
	 * Specifies the maximum value for a cell belonging to this column. If this
	 * value is null, there will not be a maximum check.
	 * 
	 * @return
	 */
	public T getMaxVal();
	
	/**
	 * This method checks if a given value is convertible to the destination
	 * data type. Different implementations will have different strategies to
	 * determine this.
	 * 
	 * @param obj
	 * @return
	 */
	public boolean isConvertible(Object obj);
	
	/**
	 * Method to check if data is present in the required field. Depending
	 * on the implementation, the meaning of 'presence' can be varied. For 
	 * example, for a string data type, we could have an implementation where
	 * null and empty string are treated as 'not present'.
	 * 
	 * @param obj
	 * @return
	 */
	public boolean isPresent(Object obj); 
	
	/**
	 * This method converts the specified object to the required destination
	 * data type. Use this method, if and only if the isConvertible method 
	 * of this interface returns true. If this method is called when the
	 * isConvertible method returns false, the behavior is unpredictable.
	 * 
	 * @param obj
	 * @return
	 */
	public T convert(Object obj);
	
	/**
	 * Check if the specified object falls in the  pre-configured range. This
	 * might not be applicable for some of the implementations of the 
	 * interface. For example, an implementation that deals with an Enum as
	 * a destination data type, will not have any meaning for min and max 
	 * values. Similarly, if an implementation validates the existing excel
	 * cell configuration against a list of values (like master data from a DB),
	 * there might not be any significance for min and max values. An
	 * 'UnSupportedOperationException' can be thrown by the implementation if
	 * this method does not make any sense.
	 * 
	 * Use this method, if and only if the isConvertible method 
	 * of this interface returns true. If this method is called when the
	 * isConvertible method returns false, the behavior is unpredictable.
	 * 
	 * @param obj
	 * @return
	 */
	public boolean isInRange(Object obj);
	
}
