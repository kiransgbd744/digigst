package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class JsonValidatorRespDto {

	@Expose
	private String recipientGstin;
	
	@Expose
	private String supplierGstin;
	
	@Expose
	private String docNum;
	
	@Expose
	private String docDate;
	
	@Expose
	private String docType;
	
	@Expose
	private BigDecimal totalInvValue;
	
	@Expose
	private Integer itemCnt;
	
	@Expose
	private List<String> mainHsnCode;
	
	@Expose
	private String irn;
	
	@Expose
	private LocalDateTime irnDate;
	
	@Expose
	private String irnStatus;
	
	@Expose
	private LocalDateTime irnCanDate;
	
	@Expose
	private String pos;
	
	@Expose
	private BigDecimal taxableValue;
	
	@Expose
	private BigDecimal iGst;
	
	@Expose
	private BigDecimal cGst;
	
	@Expose
	private BigDecimal sGst;
	
	@Expose
	private BigDecimal cess;
	
	@Expose
	private BigDecimal totalTax;
	
	@Expose
	private String rcm;
	
	@Expose
	private String purchaseOrderNo;
	
	@Expose
	private List<BigDecimal> quantity;
	
	@Expose
	private List<BigDecimal> unitPrice;
	
	@Expose
	private List<BigDecimal> lineItemAmt;
	
	@Expose
	private String eInvoiceApp;
	
	@Expose 
	private String errMsg;

	//new clob variable to save the irn json payload
	@Expose
	private String irnJsonPayload;


	
}
