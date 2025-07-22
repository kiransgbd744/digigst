package com.ey.advisory.jsontocsv.services;

import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;

public interface JsonToCsvConvertorFactory {
	
	public JsonToCsvConverter getConvertor(String msg);

}
