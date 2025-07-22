/**
 * 
 */
package com.ey.advisory.app.docs.dto.ret;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class SaveRet {

	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("rtnprd")
	private String rtnprd;
	
	@Expose
	@SerializedName("isnil")
	private Boolean isnil;
	
	@Expose
	@SerializedName("reset")
	private Boolean reset;
	
	//////////////GET////////////////
	@Expose
	@SerializedName("arn")
	private String arn;
	
	@Expose
	@SerializedName("arn_dt")
	private String arn_dt;
	
	@Expose
	@SerializedName("anx1chksum")
	private String anx1chksum;
	
	@Expose
	@SerializedName("anx2chksum")
	private String anx2chksum;
	
	@Expose
	@SerializedName("tbl3a")
	private RetTbl3aDto tbl3a;
	
	@Expose
	@SerializedName("tbl3b")
	private RetTbl3bDto tbl3b;
	
	@Expose
	@SerializedName("tbl3c")
	private RetTbl3cDto tbl3c;
	
	@Expose
	@SerializedName("tbl3d")
	private RetTbl3dDto tbl3d;
	
	@Expose
	@SerializedName("tbl4a")
	private RetTbl4aDto tbl4a;
	
	@Expose
	@SerializedName("tbl4b")
	private RetTbl4bDto tbl4b;
	
	@Expose
	@SerializedName("tbl4itc")
	private RetTbl4itcDto tbl4itc;
	
	@Expose
	@SerializedName("tbl5")
	private RetTbl5Dto tbl5;
	
	@Expose
	@SerializedName("tbl6")
	private RetTbl6Dto tbl6;
	
	@Expose
	@SerializedName("paymenttax")
	private RetPaymentTaxDto paymenttax;
	
}
