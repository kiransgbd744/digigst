package com.ey.advisory.common;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Strings;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonEWBLocalDateConverter implements 
				JsonSerializer<LocalDate>, 
				JsonDeserializer<LocalDate>{
	/** Formatter. */
	private static final DateTimeFormatter FORMATTER = 
				DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	private static final DateTimeFormatter FORMATTER1 = 
			DateTimeFormatter.ofPattern("dd-MM-YYYY");
	private static final DateTimeFormatter FORMATTER2 = 
			DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter FORMATTER3 = 
			DateTimeFormatter.ofPattern("dd-MM-yyyy");

	
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
	public JsonElement serialize(LocalDate src, Type typeOfSrc,
			JsonSerializationContext context) {
		try{
		return new JsonPrimitive(FORMATTER.format(src));
		} catch(Exception e) {
			
		}
		
		try{
			return new JsonPrimitive(FORMATTER1.format(src));
			} catch(Exception e) {
				
			}
		
		throw new AppException("cannot parse the date");
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
	public LocalDate deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) {
		
		String dateStr = json.getAsString();
		
		if(Strings.isNullOrEmpty(dateStr))
			return null;
		
		try{
		return LocalDate.parse(json.getAsString(),FORMATTER);
		} catch(Exception e) {
			
		}
		try{
			return LocalDate.parse(json.getAsString(),FORMATTER1);
			} catch(Exception e) {
				
			}
		try{
			return LocalDate.parse(json.getAsString(),FORMATTER2);
			} catch(Exception e) {
				
			}
		try{
			return LocalDate.parse(json.getAsString(),FORMATTER3);
			} catch(Exception e) {
				
			}
		throw new AppException("exception occured while deserializing LocalDate:" + json.getAsString() );
	}
}
