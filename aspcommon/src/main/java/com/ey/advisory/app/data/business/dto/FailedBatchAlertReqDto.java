/**
 * 
 */
package com.ey.advisory.app.data.business.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */

@Data
public class FailedBatchAlertReqDto {


	@SerializedName("payloadId")
	@Expose
	private String payloadId;

	@SerializedName("moduleType")
	@Expose
	private String moduleType;

	@SerializedName("groupCode")
	@Expose
	private String groupCode;

	@SerializedName("errMsg")
	@Expose
	private String errMsg;
	
	@SerializedName("primaryEmail")
	@Expose
	private String primaryEmail;

	@SerializedName("secondaryEmails")
	@Expose
	private List<String> secondaryEmails;


}
