package com.ey.advisory.app.data.services.savetogstn.gstr9;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import com.ey.advisory.app.docs.dto.gstr9.Gst9SaveStatusDto;
import com.ey.advisory.app.docs.dto.gstr9.Gst9StatusTimeStampsDto;

public interface Gstr9SaveStatus {

	public List<Gst9SaveStatusDto> getSaveStatus(String gstin, String fy);
	
	public void downloadSaveStatusDetails(Long id,
			HttpServletResponse response);
	
	public Gst9StatusTimeStampsDto getStatusTimeStamps(String gstin, String fy);

}
