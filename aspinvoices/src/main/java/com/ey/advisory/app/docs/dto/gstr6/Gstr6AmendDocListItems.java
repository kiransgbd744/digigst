package com.ey.advisory.app.docs.dto.gstr6;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6AmendDocListItems {

	@Expose
	@SerializedName("cpty")
	private String cpty;

	@Expose
	@SerializedName("statecd")
	private String statecd;

	@Expose
	@SerializedName("chksum")
	private String chkSum;

	@Expose
	@SerializedName("isd_docty")
	private String isdDocty;

	@Expose
	@SerializedName("rdocnum")
	private String rdocnum;

	@Expose
	@SerializedName("rdocdt")
	private String rdocdt;

	@Expose
	@SerializedName("odocnum")
	private String odocnum;

	@Expose
	@SerializedName("odocdt")
	private String odocdt;

	@Expose
	@SerializedName("rcrdnum")
	private String rcrdnum;

	@Expose
	@SerializedName("rcrddt")
	private String rcrddt;

	@Expose
	@SerializedName("ocrdnum")
	private String ocrdnum;

	@Expose
	@SerializedName("ocrddt")
	private String ocrddt;

	@Expose
	@SerializedName("iamti")
	private String iamti;

	@Expose
	@SerializedName("iamts")
	private String iamts;

	@Expose
	@SerializedName("iamtc")
	private String iamtc;

	@Expose
	@SerializedName("samts")
	private String samts;

	@Expose
	@SerializedName("samti")
	private String samti;

	@Expose
	@SerializedName("camti")
	private String camti;

	@Expose
	@SerializedName("camtc")
	private String camtc;

	@Expose
	@SerializedName("csamt")
	private String csamt;

}
