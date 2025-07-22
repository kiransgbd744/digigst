package com.ey.advisory.app.docs.dto.gstr6;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6IsdDocListItems {
	
	@Expose
	@SerializedName("flag")
	private String flag;

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
	@SerializedName("docnum")
	private String docnum;

	@Expose
	@SerializedName("odocnum")
	private String odocnum;

	@Expose
	@SerializedName("docdt")
	private String docdt;

	@Expose
	@SerializedName("rdocdt")
	private String rdocdt;

	@Expose
	@SerializedName("odocdt")
	private String odocdt;

	@Expose
	@SerializedName("crdnum")
	private String crdnum;

	@Expose
	@SerializedName("rcrdnum")
	private String rcrdnum;

	@Expose
	@SerializedName("ocrdnum")
	private String ocrdnum;

	@Expose
	@SerializedName("crddt")
	private String crddt;

	@Expose
	@SerializedName("rcrddt")
	private String rcrddt;

	@Expose
	@SerializedName("ocrddt")
	private String ocrddt;

	@Expose
	@SerializedName("iamti")
	private BigDecimal iamti;

	@Expose
	@SerializedName("iamts")
	private BigDecimal iamts;

	@Expose
	@SerializedName("iamtc")
	private BigDecimal iamtc;

	@Expose
	@SerializedName("samts")
	private BigDecimal samts;

	@Expose
	@SerializedName("samti")
	private BigDecimal samti;

	@Expose
	@SerializedName("camti")
	private BigDecimal camti;

	@Expose
	@SerializedName("camtc")
	private BigDecimal camtc;

	@Expose
	@SerializedName("csamt")
	private BigDecimal csamt;

	
}
