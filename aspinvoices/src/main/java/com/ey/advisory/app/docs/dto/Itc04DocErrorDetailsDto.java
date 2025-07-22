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
public class Itc04DocErrorDetailsDto {

	@Expose
	@SerializedName("index")
	private Integer index;

	@Expose
	@SerializedName("itemNo")
	private Integer itemNo;

	@Expose
	@SerializedName("errorCode")
	private String errorCode;

	@Expose
	@SerializedName("errorDesc")
	private String errorDesc;

	@Expose
	@SerializedName("errorFields")
	private String errorFields;// comma separated fields

	@Expose
	@SerializedName("errorType")
	private String errorType;

}
