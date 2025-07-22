/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class AuditTrailScreenReqDto {

	@Expose
	@SerializedName("docNum")
	private String docNum;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;

	@Expose
	@SerializedName("docType")
	private String docType;

	@Expose
	@SerializedName("docDate")
	private LocalDate docDate;

	@Expose
	@SerializedName("type")
	private String type;
	
	@Expose
	@SerializedName("docKey")
	private String docKey;
	
	@Expose
	@SerializedName("sgstin")
	private String sgstin;

	@Expose
	@SerializedName("legalName")
	private String legalName;


}
