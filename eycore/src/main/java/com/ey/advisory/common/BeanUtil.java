package com.ey.advisory.common;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.collect.Streams;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeanUtil {

	private BeanUtil() {}
	
	/**
	 * This method can be used copy a set of properties from the source object
	 * to the destination object. The property names of the destination object
	 * may vary from the corresponding properties of the source object. 
	 * 
	 * @param src The source object from which the property values are to be 
	 * 		retrieved, by executing the getter method for that property.
	 * @param dest The destination object to which the property values are to
	 * 		be set, by executing the setter method for that property, with the
	 * 		value obtained by executing the getter property mentioned above.
	 * @param srcProps The list of properties for which values are to be 
	 * 		retrieved from the source object (for setting to the 
	 * 		destination object)
	 * @param destProps The list of properties for which values are to be 
	 * 		set to the destination object.
	 */
	public static void copyProperties(
					@NonNull Object src, 
					@NonNull Object dest, 
					@NonNull List<String> srcProps, 
					@NonNull List<String> destProps) {
		
		// Make sure that the srcProps and destProps are not empty and have
		// the same length.
		if (srcProps.isEmpty() || destProps.isEmpty() || 
				srcProps.size() != destProps.size()) {
			String msg = String.format("The source and destination "
					+ "properties should not be empty "
					+ "and should have the same number of elements "
					+ "[SrcLen: %d, DestLen: %d]", 
					srcProps.size(), destProps.size());
			AppException ex = new AppException(msg);
			LOGGER.error(msg, ex);
			throw ex;
		}
		
		// Get the list of values to be set to the destination properties.
		// If the getter method is not available or if it throws an error, 
		// then the getProperty method will fail with an exception.
		List<Object> values = srcProps.stream()
			.map(p -> getProperty(src, p))
			.collect(Collectors.toList());
		
		// Now that we have the values to be set to each property of the 
		// destination object, invoke the copyValues method to set each value
		// to the property of the destination object. Any exceptions that occur
		// while setting the value, will be caught and re-thrown as an
		// AppException
		copyValues(dest, destProps, values);
		
	}
	
	/**
	 * This method can be used to set the specified properties on the specified
	 * object with the specified values. The properties array and values array
	 * need to match in size. The values array should contain the values of the
	 * properties, in the order of the properties mentioned in the 'properties'
	 * array.
	 * 
	 * @param obj The object on which certain values are to be set, for the 
	 * 		specified properties.
	 * @param properties The properties that need to be set on the object, with
	 * 		the specified values.
	 * @param values The values to be set for the specified properties.
	 */
	public static void copyValues(
					@NonNull Object obj, 
					@NonNull List<String> properties, 
					@NonNull List<Object> values) {
		
		// Set the  values to the destination properties. If the 
		// setter method is not present in the dest object or if it throws  
		// an exception, then the setProperty method will fail with 
		// an exception.
		Streams.forEachPair(
				properties.stream(), 
				values.stream(), 
				(p, v) -> setProperty(obj, p, v));		
	}

	/**
	 * This is a wrapper method to convert any checked exceptions that occur
	 * during the getter method invocation on the source bean, to AppException,
	 * so that this can be safely used within a lambda expression.
	 * 
	 * @param obj The bean on which the getter method has to be invoked.
	 * @param propName The name of the property representing the getter method.
	 * @return the result of invocation of the getter method. (i.e. the return
	 * 		value of the getter method)
	 */
	private static Object getProperty(Object obj, String propName) {
		try {
			return PropertyUtils.getProperty(obj,  propName);
		} catch(Exception ex) {
			String msg = String.format("Error occured while getting the "
					+ "property '%s' from the source bean.", propName);
			LOGGER.error(msg);
			throw new AppException(msg, ex);			
		}
	}
	
	/**
	 * This is a wrapper method to convert any checked exceptions that occur
	 * during the setter method invocation on the source bean, to AppException,
	 * so that this can be safely used within a lambda expression.
	 * 
	 * @param obj The bean on which the setter method has to be invoked.
	 * @param propName The name of the property representing the setter method.
	 * @param value The value to the set using the setter method, to the 
	 * 		destination bean.
	 */	
	private static void setProperty(Object obj, 
				String propName, Object value) {
		try {
			PropertyUtils.setProperty(obj,  propName, value);
		} catch(Exception ex) {
			String msg = String.format("Error occured while setting the "
					+ "property '%s' from to the destination bean.", propName);
			LOGGER.error(msg);
			throw new AppException(msg, ex);			
		}
	}	
	
}
