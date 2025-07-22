package com.ey.advisory.app.docs.dto.simplified;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author SriBhavya
 *
 */
@Data
public class Gstr1AdvancedDeleteReqDto {

	@Expose
	@SerializedName("docType")
	private String docType;

	@Expose
	@SerializedName("id")
	private Integer id;

}
