package com.ey.advisory.common;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;

public class BigDecimalPlainAdapter implements JsonSerializer<BigDecimal> {
	
	 @Override
	    public JsonElement serialize(BigDecimal src, Type typeOfSrc, JsonSerializationContext context) {
	        return src == null 
	            ? JsonNull.INSTANCE 
	            : new JsonPrimitive(src.stripTrailingZeros().toPlainString());
	    }
}

