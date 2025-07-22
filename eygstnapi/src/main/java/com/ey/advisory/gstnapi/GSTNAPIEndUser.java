package com.ey.advisory.gstnapi;

import com.ey.advisory.core.api.impl.APIEndUser;
import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.domain.master.GstnAPIGroupConfig;

/**
 * This class contains all the end user related information that needs to be 
 * present for invoking the API. We have 2 modes of hitting the GSTN API - one 
 * is to directly hit the API through our external proxy (that's white listed
 * with GSTN) and the second is to hit GSTN through the GSP server (which 
 * performs certain additional authentication on the request). The GSP server
 * is supposed to cater for other ASPs registered with us. But since EY is 
 * both a GSP and an ASP, we always have the flexibility to directly hit GSTN
 * without using GSP.
 * 
 * @author Sai.Pakanati
 *
 */
public class GSTNAPIEndUser implements APIEndUser {
	
	/**
	 * The GSTIN related information for the end user. Some part of this is
	 * required by GSTN (which is our final API provider) and some other part 
	 * is required by the GSP server through which we route the API request 
	 * to GSTN.
	 */
	private GstnAPIGstinConfig gstinConfig;
	
	
	/**
	 * Any Group level information that's needed for executing a GSTN API. 
	 * This is required since all the calls that are routed through GSP server
	 * needs to authenticate themselves with certain group level information.
	 * Even though GSTN (which is the final API provider) doesn't need this 
	 * information, the GSP server needs this. So, while preparing the HTTP
	 * headers and URL params, we need to insert these details there, depending
	 * on whether the API request is routed through GSP or we're directly 
	 * hitting GSTN.
	 */
	private GstnAPIGroupConfig groupConfig;


	public GSTNAPIEndUser(GstnAPIGstinConfig gstinConfig,
			GstnAPIGroupConfig groupConfig) {
		super();
		this.gstinConfig = gstinConfig;
		this.groupConfig = groupConfig;
	}


	public GstnAPIGstinConfig getGstinConfig() {
		return gstinConfig;
	}


	public GstnAPIGroupConfig getGroupConfig() {
		return groupConfig;
	}


	@Override
	public String toString() {
		return "GSTNAPIEndUser [gstinConfig=" + gstinConfig + ", groupConfig="
				+ groupConfig + "]";
	}
	
 }
