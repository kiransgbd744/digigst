package com.ey.advisory.common.multitenancy;

public class TenantDBConfig {
	
	protected String username;
	
	protected String password;
	
	protected String url;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "TenantSpecificPoolConfig [username=" + username + ", password="
				+ password + ", url=" + url + "]";
	}
	
}
