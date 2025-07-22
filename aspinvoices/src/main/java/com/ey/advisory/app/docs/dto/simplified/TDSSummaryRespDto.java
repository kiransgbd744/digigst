package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
public class TDSSummaryRespDto {
	
	private String gstinDeductee;
	private String mofDeductorUpld;
	private String orgMonOfDeductorUpld;
	private String docKey;
	
//	private String authToken;
//	private String deducteeName;
	private BigDecimal totamount;
	private BigDecimal amountIgst;
	private BigDecimal amountCgst;
	private BigDecimal amountSgst;
	private String userAction;
	private String savedAction;
	
	private String gstinOfColectr;
	private String monOfcollectorUpld;
	private String orgmonOfcollectorUpld;
	private BigDecimal SuppliesToRegisteredBuyers;
	private BigDecimal SuppliesReturnedbyRegisteredBuyers;
	private BigDecimal SuppliestoURbuyers;
	private BigDecimal SuppliesReturnedbyURbuyers;
	
	private String pos;
	private String digiGstRemarks;
	private String digiGstComment;
	private String gstnRemarks;
	private String gstnComment;
	

		
	
}
