package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.ey.advisory.app.services.gen.EntityDto;

/**
 * This class is responsible for transferring list of gstins for Document Correction UI screen
 * @author Mohana.Dasari
 *
 */
public class EntityRespDto {

	private List<EntityDto> resp;

	/**
	 * @return the resp
	 */
	public List<EntityDto> getResp() {
		return resp;
	}

	/**
	 * @param resp the resp to set
	 */
	public void setResp(List<EntityDto> resp) {
		this.resp = resp;
	}
	
}
