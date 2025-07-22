package com.ey.advisory.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter
		implements AttributeConverter<LocalDateTime, Date> {
    @Override
    public Date convertToDatabaseColumn(LocalDateTime locDateTime) {
    	if (locDateTime == null) return null;
    	return java.util.Date
    		.from(locDateTime.atZone(ZoneId.systemDefault())
    				.toInstant());
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Date date) {
    	if (date == null) return null;
		return date.toInstant()
			      .atZone(ZoneId.systemDefault())
			      .toLocalDateTime();
    }
}
