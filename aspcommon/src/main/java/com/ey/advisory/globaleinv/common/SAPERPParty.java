package com.ey.advisory.globaleinv.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SAPERPParty{
	

	@SerializedName("partytype")
	@Expose
    public String partytype;
	
	@SerializedName("partyname")
	@Expose
    public String partyname;
	
	@SerializedName("taxnumber")
	@Expose
    public String taxnumber;
	
	@SerializedName("grouptaxno")
	@Expose
    public String grouptaxno;
	
	@SerializedName("otherpartyid")
	@Expose
    public String otherpartyid;
	
	@SerializedName("street")
	@Expose
    public String street;
	
	@SerializedName("addstreet")
	@Expose
    public String addstreet;
	
	@SerializedName("buildno")
	@Expose
    public String buildno;
	
	@SerializedName("addno")
	@Expose
    public String addno;
	
	@SerializedName("city")
	@Expose
    public String city;
	
	@SerializedName("postalcode")
	@Expose
    public String postalcode;
	
	@SerializedName("provincestate")
	@Expose
    public String provincestate;
	
	@SerializedName("district")
	@Expose
    public String district;
	
	@SerializedName("countrycode")
	@Expose
    public String countrycode;
}
