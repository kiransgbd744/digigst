package com.ey.advisory.app.gstr1a.einv;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author Rajesh N K
 *
 */
@Data
public class Gstr1AEinvRequesIdWiseDownloadTabDto {
	
	@Expose
	private String  reportName;
	
	@Expose
	private String path;
	
	@Expose
	private boolean flag;
	
	@Expose
	private String docId;

}
