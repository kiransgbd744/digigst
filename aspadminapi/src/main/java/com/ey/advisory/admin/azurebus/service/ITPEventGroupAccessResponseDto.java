
package com.ey.advisory.admin.azurebus.service;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

import lombok.Data;


@Data
public class ITPEventGroupAccessResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Expose
	private String userName;
	
	
	private boolean g1;
	
	
	private boolean g2;
	
	
	private boolean g3;
	
	
	private boolean g4;

}
