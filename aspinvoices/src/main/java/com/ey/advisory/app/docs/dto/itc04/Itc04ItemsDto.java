package com.ey.advisory.app.docs.dto.itc04;

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
public class Itc04ItemsDto {

	@Expose
	@SerializedName("goods_ty")
	private String goodsType;

	@Expose
	@SerializedName("desc")
	private String desc;

	@Expose
	@SerializedName("uqc")
	private String uqc;

	@Expose
	@SerializedName("qty")
	private BigDecimal qty;

	@Expose
	@SerializedName("txval")
	private BigDecimal txval;

	@Expose
	@SerializedName("tx_c")
	private BigDecimal txc;

	@Expose
	@SerializedName("tx_s")
	private BigDecimal txs;

	@Expose
	@SerializedName("tx_i")
	private BigDecimal txi;

	@Expose
	@SerializedName("tx_cs")
	private BigDecimal txcs;

	@Expose
	@SerializedName("o_chnum")
	private String ochnum;

	@Expose
	@SerializedName("o_chdt")
	private String ochdt;
	
	@Expose
	@SerializedName("inum")
	private String inum;

	@Expose
	@SerializedName("idt")
	private String idt;

	@Expose
	@SerializedName("jw2_chnum")
	private String jw2Chnum;

	@Expose
	@SerializedName("jw2_chdt")
	private String jw2Chdate;

	@Expose
	@SerializedName("nat_jw")
	private String natjw;

	@Expose
	@SerializedName("lwqty")
	private BigDecimal lwqty;

	@Expose
	@SerializedName("lwuqc")
	private String lwuqc;
	
	@Expose
	@SerializedName("flag")
	private String flag;

}
