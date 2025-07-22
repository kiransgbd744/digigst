/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class PdfPrintReqDto {
	
	@Expose
	@SerializedName("id")
	private String id;
	
	@Expose
	@SerializedName("docNo")
	private String docNo;
	
	@Expose
	@SerializedName("sgstin")
	private String sgstin;
	
	@Expose
	@SerializedName("cEWBNo")
	private String cEWBNo;

}
