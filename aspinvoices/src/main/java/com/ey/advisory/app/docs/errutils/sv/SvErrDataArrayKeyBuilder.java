package com.ey.advisory.app.docs.errutils.sv;

/**
 * 
 * @author Mohana.Dasari
 *
 * @param <T>
 */
public interface SvErrDataArrayKeyBuilder<T> {

	/**
	 * The method responsible for building a Data Key for a row of data
	 * @param arr
	 * @return
	 */
	public T buildDataArrayKey(Object[] arr);
}
