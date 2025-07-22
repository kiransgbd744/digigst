package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.ey.advisory.gstr2.userdetails.GstinDto;


/**
 * This class is responsible for transferring list of gstins for Document Correction UI screen
 * @author Mohana.Dasari
 *
 */
public class GstinRespDto {

	private List<GstinDto> gstins;

	/**
	 * @return the gstins
	 */
	public List<GstinDto> getGstins() {
		return gstins;
	}

	/**
	 * @param gstins the gstins to set
	 */
	public void setGstins(List<GstinDto> gstins) {
		this.gstins = gstins;
	}
	
}
