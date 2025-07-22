package com.ey.advisory.app.anx2.initiaterecon;

import java.io.Serializable;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author vishal.Verma
 *
 */

@Data
public class ReconResponseDataStatusReqDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Expose
	private String dataType;
	
	@Expose
	private String fileType;
	
	@Expose
	private String returnType;
	
	@Expose
	private String uploadFromDate;
	
	@Expose
	private String uploadToDate;
	
}
