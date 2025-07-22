package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author vishal.verma
 *
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierImsSectionDtlsDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("rtnprd")
	private String rtnprd;

	@Expose
	@SerializedName("gstr1")
	private Gstr1ImsDTO gstr1;
	
	@Expose
	@SerializedName("gstr1a")
	private Gstr1ImsDTO gstr1a;

}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class Gstr1ImsDTO {

	@Expose
	@SerializedName("b2b")
	private List<ImsDataDto> b2b;
	
	@Expose
	@SerializedName("b2ba")
	private List<ImsDataDto> b2ba;
	
	@Expose
	@SerializedName("cdnr")
	private List<ImsDataDto> cdnr;
	
	@Expose
	@SerializedName("cdnra")
	private List<ImsDataDto> cdnra;

	@Expose
	@SerializedName("ecom")
	private ImsEcomDataDto ecom;
	
	@Expose
	@SerializedName("ecoma")
	private ImsEcomDataDto ecoma;

}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class ImsEcomDataDto {

	@Expose
	@SerializedName("b2b")
	private List<ImsDataDto> b2b;
	
	@Expose
	@SerializedName("urp2b")
	private List<ImsDataDto> urp2b;
	
	@Expose
	@SerializedName("b2ba")
	private List<ImsDataDto> b2ba;
	
	@Expose
	@SerializedName("urp2ba")
	private List<ImsDataDto> urp2ba;

}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class ImsDataDto {

	@Expose
	@SerializedName("inv")
	private List<Inv1> inv;

	@Expose
	@SerializedName("nt")
	private List<Inv1> nt;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class Inv1 {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName(value = "inum", alternate = {"nt_num"})
	private String inum;

	@Expose
	@SerializedName("imsactn")
	private String imsactn;
	
	public String getImsactn() {
        return (imsactn == null || imsactn.trim().isEmpty()) ? "N" : imsactn;
    }

}
