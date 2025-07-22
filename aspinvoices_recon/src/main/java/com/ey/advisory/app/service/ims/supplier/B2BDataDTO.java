/**
 * 
 */
package com.ey.advisory.app.service.ims.supplier;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class B2BDataDTO {

	@Expose
	@SerializedName("ctin")
	private String ctin;

	@Expose
	@SerializedName("cfs")
	private String cfs;

	/*@Expose
	@SerializedName("nt")
	private List<B2BInvoiceDetailDTO> nt;*/

	@Expose
	@SerializedName(value = "inv", alternate = ("nt"))
	private List<B2BInvoiceDetailDTO> inv;

	@Expose
	@SerializedName("rtin")
	private String rtin;

	@Expose
	@SerializedName("ortin")
	private String ortin; // ecomA urp2b

	@Expose
	@SerializedName("stin")
	private String stin;// ecom b2b 

}

@Data
class B2BInvoiceDetailDTO {

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("updby")
	private String updby;

	@Expose
	@SerializedName("inum")
	private String inum;

	@Expose
	@SerializedName("idt")
	private String idt;

	@Expose
	@SerializedName("val")
	private BigDecimal val;

	@Expose
	@SerializedName("rchrg")
	private String rchrg;

	@Expose
	@SerializedName("etin")
	private String etin;

	@Expose
	@SerializedName("inv_typ")
	private String inv_typ;

	@Expose
	@SerializedName("flag")
	private String flag;

	@Expose
	@SerializedName("cflag")
	private String cflag;

	@Expose
	@SerializedName("diff_percent")
	private BigDecimal diff_percent;

	@Expose
	@SerializedName("opd")
	private String opd;

	@Expose
	@SerializedName("srctyp")
	private String srctyp;

	@Expose
	@SerializedName("irn")
	private String irn;

	@Expose
	@SerializedName("irngendate")
	private String irngendate;

	@Expose
	@SerializedName("imsactn")
	private String imsactn;

	@Expose
	@SerializedName("oinum")
	private String oinum;// B2BA

	@Expose
	@SerializedName("oidt")
	private String oidt;// B2BA

	@Expose
	@SerializedName("remarks")
	private String remarks;

	@Expose
	@SerializedName("ntty")
	private String ntty;

	@Expose
	@SerializedName("nt_num")
	private String nt_num;

	@Expose
	@SerializedName("nt_dt")
	private String nt_dt;

	@Expose
	@SerializedName("p_gst")
	private String p_gst;

	@Expose
	@SerializedName("d_flag")
	private String d_flag;

	@Expose
	@SerializedName("ont_num")
	private String ont_num;// CDNRA

	@Expose
	@SerializedName("ont_dt")
	private String ont_dt;// CDNRA

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("itms")
	private List<ItemDTO> itms;

	@Expose
	@SerializedName("sply_ty")
	private String sply_ty;

}

@Data
class ItemDTO {

	@Expose
	@SerializedName("num")
	private Long num;

	@Expose
	@SerializedName("itm_det")
	private ItemDetailDTO itm_det;
}

@Data
class ItemDetailDTO {

	@Expose
	@SerializedName("rt")
	private BigDecimal rt;

	@Expose
	@SerializedName("txval")
	private BigDecimal txval;

	@Expose
	@SerializedName("iamt")
	private BigDecimal iamt;

	@Expose
	@SerializedName("camt")
	private BigDecimal camt;

	@Expose
	@SerializedName("samt")
	private BigDecimal samt;

	@Expose
	@SerializedName("csamt")
	private BigDecimal csamt;
}
