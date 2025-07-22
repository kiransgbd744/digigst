package com.ey.advisory.app.data.services.itc04stocktrack;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ey.advisory.core.dto.ITC04RequestDto;

public interface Itc04StockTrackService {

	ResponseEntity<String> getScreenDetails(ITC04RequestDto reqDto);
	
	ResponseEntity<String> triggerInitiateReport(ITC04RequestDto reqDto);

	void computeInitiateReport(List<Long> ids);

}
