/**
 * 
 */
package com.ey.advisory.app.data.services.drc;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class DrcSaveToGstnDto {

	@Expose
	@SerializedName("frmtyp")
	private String formType;
	
	@Expose
	@SerializedName("refid")
	private String referenceId;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("drc03arn")
	private List<String> drc03Arn;
	
	@Expose
	@SerializedName("rtnprd")
	private String returnPeriod;
	
	@Expose
	@SerializedName("reasons")
	private List<ReasonDto> reasons;
	
	@Expose
	@SerializedName("deldrc03arn")
	private List<String> deletedDrc03Arn;

}

