/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class FileGstr1Headers {

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;
	
	@Expose
	@SerializedName("ip-usr")
	private String ipUsr;
	
	@Expose
	private String clientid;
	
	@Expose
	@SerializedName("client-secret")
	private String clientSecret;
	
	@Expose
	private String txn;
	
	@Expose
	@SerializedName("state-cd")
	private String stateCode;
	
	@Expose
	private String gstin;
	
	@Expose
	@SerializedName("Content-Type")
	private String contentType;
	
	@Expose
	@SerializedName("auth-token")
	private String authToken;
	
	@Expose
	private String username;
}
