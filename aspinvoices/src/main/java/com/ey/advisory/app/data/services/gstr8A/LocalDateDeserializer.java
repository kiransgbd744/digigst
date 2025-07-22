package com.ey.advisory.app.data.services.gstr8A;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
    
	@Override
	public LocalDate deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {/*
		return LocalDate.parse(json.getAsString(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	*/
		  String dateStr = json.getAsString();

		    // Define multiple date patterns
		    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		    // Try parsing the date with different formats
		    try {
		        return LocalDate.parse(dateStr, formatter1);
		    } catch (Exception e1) {
		        try {
		            return LocalDate.parse(dateStr, formatter2);
		        } catch (Exception e2) {
		            throw new JsonParseException("Unable to parse date: " + dateStr);
		        }
		    }	
	
	}
}
