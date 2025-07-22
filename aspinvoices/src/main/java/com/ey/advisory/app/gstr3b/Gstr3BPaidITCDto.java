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
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Gstr3BPaidITCDto {
	
	@Expose
	@SerializedName("trans_typ")
	private Long transType;
	
	@Expose
	@SerializedName("liab_ldg_id")
	private Long ledgId;
	
	@Expose
	@SerializedName("i_pdi")
	private BigDecimal iPDIgst;

	@Expose
	@SerializedName("i_pdc")
	private BigDecimal iPDCgst;
	
	@Expose
	@SerializedName("i_pds")
	private BigDecimal iPDSgst;
	
	@Expose
	@SerializedName("c_pdi")
	private BigDecimal cPDIgst;
	
	@Expose
	@SerializedName("c_pdc")
	private BigDecimal cPDCgst;
	
	@Expose
	@SerializedName("s_pdi")
	private BigDecimal sPDIgst;
	
	@Expose
	@SerializedName("s_pds")
	private BigDecimal sPDSgst;
	
	@Expose
	@SerializedName("cs_pdcs")
	private BigDecimal csPdCess;


}
