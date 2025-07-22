package com.ey.advisory.app.service.bc.api;

import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * @author vishal.verma
 *
 */

import lombok.Data;
/**
 * 
 * @author vishal.verma
 *
 */
@Data
public class CopyNICCredentialDto {

	@Expose
	private List<NICCredentialDto> nicDetails;
	
	@Expose
	private String copyNICFlag;// E-Invoice to E-WayBill / E-WayBill to E-Invoice

	
}
