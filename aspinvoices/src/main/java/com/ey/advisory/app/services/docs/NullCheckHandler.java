/**
 * 
 */
package com.ey.advisory.app.services.docs;

import java.lang.reflect.Field;

import org.springframework.stereotype.Component;

/**
 * @author Mahesh.Golla
 *
 *
 *         This class is meant for checking the object has all null values or
 *         not.
 * 
 *         This class need to be removed if we find a way to delete the empty
 *         rows from excel sheet while reading the data.
 * 
 *         Till that time use this null check handler to check object is not
 *         null
 * 
 */

@Component("NullCheckHandler")
public class NullCheckHandler {

	/**
	 * 
	 * @param obj
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public boolean checkNullProperties(Object obj)
			throws IllegalAccessException {

		Field[] fields = obj.getClass().getDeclaredFields();

		for (Field field : fields) {
			field.setAccessible(true);
			if (field.get(obj) != null) {
				return false;
			}
		}
		return true;

	}
}
