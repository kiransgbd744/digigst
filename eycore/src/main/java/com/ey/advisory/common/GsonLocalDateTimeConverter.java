package com.ey.advisory.common;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonLocalDateTimeConverter implements 
				JsonSerializer<LocalDateTime>, 
				JsonDeserializer<LocalDateTime>{
	/** Formatter. */
	private static final DateTimeFormatter FORMATTER = 
				DateTimeFormatter.ofPattern("yyyy-MM-dd");
	/*private static final DateTimeFormatter FORMATTER_1 = 
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	
	private static final DateTimeFormatter FORMATTER_2 = 
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.mmm'Z'");*/
	
	private static final DateTimeFormatter FORMATTER_3 = 
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	private static final DateTimeFormatter FORMATTER_4 = 
			DateTimeFormatter.ISO_DATE_TIME;
	
	private static DateTimeFormatter FORMATTER_5 =  DateTimeFormatter
			.ofPattern("dd/MM/yyyy hh:mm:ss a");
	
	

	
	/**
	 * Gson invokes this call-back method during serialization when it
	 * encounters a field of the specified type.
	 * <p>
	 *
	 * In the implementation of this call-back method, you should consider
	 * invoking {@link JsonSerializationContext#serialize(Object, Type)} method
	 * to create JsonElements for any non-trivial field of the {@code src}
	 * object. However, you should never invoke it on the {@code src} object
	 * itself since that will cause an infinite loop (Gson will call your
	 * call-back method again).
	 *
	 * @param src
	 *            the object that needs to be converted to Json.
	 * @param typeOfSrc
	 *            the actual type (fully genericized version) of the source
	 *            object.
	 * @return a JsonElement corresponding to the specified object.
	 */
	@Override
	public JsonElement serialize(LocalDateTime src, Type typeOfSrc,
			JsonSerializationContext context) {
		return new JsonPrimitive(FORMATTER.format(src));
	}

	/**
	 * Gson invokes this call-back method during deserialization when it
	 * encounters a field of the specified type.
	 * <p>
	 *
	 * In the implementation of this call-back method, you should consider
	 * invoking
	 * {@link JsonDeserializationContext#deserialize(JsonElement, Type)} method
	 * to create objects for any non-trivial field of the returned object.
	 * However, you should never invoke it on the the same type passing
	 * {@code json} since that will cause an infinite loop (Gson will call your
	 * call-back method again).
	 *
	 * @param json
	 *            The Json data being deserialized
	 * @param typeOfT
	 *            The type of the Object to deserialize to
	 * @return a deserialized object of the specified type typeOfT which is a
	 *         subclass of {@code T}
	 * @throws JsonParseException
	 *             if json is not in the expected format of {@code typeOfT}
	 */
	@Override
	public LocalDateTime deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		
		String valToParse = json.getAsString();
		
			
		// If the control reaches here, then the first formatter failed. Now,
		// try with the second formatter.		
		try {
			LocalDateTime date = LocalDateTime.parse(valToParse, FORMATTER_4);
			return date;
		} catch (Exception ex) {
			// DO Nothing.. Move on to the next formatter.
		}
		
		// List of formatters to try.
				try {
					LocalDate date = LocalDate.parse(valToParse, FORMATTER_3);
					return LocalDateTime.of(date, LocalTime.MIN); 
				} catch (Exception ex) {
					// DO Nothing.. Move on to the next formatter.
				}
				
				try {
					LocalDate date = LocalDate.parse(valToParse, FORMATTER_5);
					return LocalDateTime.of(date, LocalTime.MIN); 
				} catch (Exception ex) {
					// DO Nothing.. Move on to the next formatter.
				}
		
		// If the control reaches here, then all the above formatters failed.
		// Throw an exception in this case.
		throw new AppException("Invalid Date/Time Format "
				+ "encountered: val = " + valToParse);
	}

}
