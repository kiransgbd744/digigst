package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LedgerDetailsDto {
	
	@Expose
	@SerializedName("desc")
	private String desc;

	@Expose
	@SerializedName("i")
	private BigDecimal i;

	@Expose
	@SerializedName("c")
	private BigDecimal c;

	@Expose
	@SerializedName("s")
	private BigDecimal s;

	@Expose
	@SerializedName("cs")
	private BigDecimal cs;

	@Expose
	@SerializedName("cri")
	private BigDecimal cri;

	@Expose
	@SerializedName("crc")
	private BigDecimal crc;

	@Expose
	@SerializedName("crs")
	private BigDecimal crs;
	
	@Expose
	@SerializedName("crcs")
	private BigDecimal crcs;
	
	// freeze changes
	@Expose
	@SerializedName("crTotal")
	private BigDecimal crTotal;
	
	@Expose
	@SerializedName("total")
	private BigDecimal Total;
	
	//Negative Liability
	@Expose
	@SerializedName("nlbIgst")
	private BigDecimal nlbIgst;

	@Expose
	@SerializedName("nlbCgst")
	private BigDecimal nlbCgst;

	@Expose
	@SerializedName("nlbSgst")
	private BigDecimal nlbSgst;
	
	@Expose
	@SerializedName("nlbCess")
	private BigDecimal nlbCess;
	
	// freeze changes
	@Expose
	@SerializedName("nlbTotal")
	private BigDecimal nlbTotal;
	

}

