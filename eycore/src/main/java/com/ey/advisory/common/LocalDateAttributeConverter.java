package com.ey.advisory.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateAttributeConverter implements 
				AttributeConverter<LocalDate, Date> {
    @Override
    public Date convertToDatabaseColumn(LocalDate locDate) {
    	if (locDate ==  null) return null;
    	return Date.from(locDate.atStartOfDay()
    			      .atZone(ZoneId.systemDefault())
    			      .toInstant());    	
    }

    @Override
    public LocalDate convertToEntityAttribute(Date date) {
    	if (date == null) return null;
    	return Instant.ofEpochMilli(date.getTime())
    		    		.atZone(ZoneId.systemDefault()).toLocalDate();    	
    }

}
