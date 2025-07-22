package com.ey.advisory.app.common.cf;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class HANACredentials {
	
	@SerializedName("certificate")
	private String certificate;
	
	@SerializedName("driver")
	private String driver;
	
	@SerializedName("hdi_user")
	private String hdiUser;
	
	@SerializedName("hdi_password")
	private String hdiPassword;
	
	@SerializedName("user")
	private String dbUserName;
	
	@SerializedName("password")
	private String dbPassword;
	
	@SerializedName("host")
	private String host;
	
	@SerializedName("port")
	private String port;
	
	@SerializedName("schema")
	private String schema;
	
	@SerializedName("url")
	private String jdbcUrl;
		
}
