/**
 * 
 */
package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ECOMADataDTO {

	@Expose
	@SerializedName("b2ba")
	private List<EcomaB2BInvoiceDTO> b2ba;

	@Expose
	@SerializedName("urp2ba")
	private List<EcomaB2BInvoiceDTO> urp2ba;

	// Getters and setters
}

@Data
class EcomaB2BInvoiceDTO {

	@Expose
	@SerializedName("rtin")
	private String rtin;
	
	@Expose
	@SerializedName("ortin")
	private String ortin;// not in b2ba

	@Expose
	@SerializedName("stin")
	private String stin;// not in urp2b
	
	@Expose
	@SerializedName("inv")
	private List<EcomaB2BInvoiceDetailDTO> inv;

	// Getters and setters
}

@Data
class EcomaB2BInvoiceDetailDTO {

	@Expose
	@SerializedName("flag")
	private String flag;

	@Expose
	@SerializedName("inum")
	private String inum;

	@Expose
	@SerializedName("idt")
	private String idt;

	@Expose
	@SerializedName("val")
	private Double val;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("inv_typ")
	private String inv_typ;

	@Expose
	@SerializedName("itms")
	private List<ItemDTO> itms;

	@Expose
	@SerializedName("sply_ty")
	private String sply_ty;

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("imsactn")
	private String imsactn;
	
	@Expose
	@SerializedName("oinum")
	private String oinum;
	
	@Expose
	@SerializedName("oidt")
	private String oidt;
	
}
