package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr1ReviewSummaryTaxTypeDto {

	private String retPer;

	private String state;

	private String gstinNum;

	private String dataType;

	private String taxCate;

	private String taxTable;

	private String docType;

	private BigDecimal aspTbval;

	private BigDecimal aspTxval;

	private BigDecimal aspInval;

	private BigDecimal aspIgstval;

	private BigDecimal aspCgstval;

	private BigDecimal aspSgstval;

	private BigDecimal aspCessval;

	private Integer gstnTotDoc;

	private Integer gstnCount;

	private BigDecimal gstnTbval;

	private BigDecimal gstnTxval;

	private BigDecimal gstnInval;

	private BigDecimal gstnIgstval;

	private BigDecimal gstnCgstval;

	private BigDecimal gstnSgstval;

	private BigDecimal gstnCessval;

	private String erDate;

	private String currentTime;

	private Integer aspCount;

	private Integer memoTotDoc;

	private BigDecimal memoTbval;

	private BigDecimal memoTxval;

	private BigDecimal memoInval;

	private BigDecimal memoIgstval;

	private BigDecimal memoCgstval;

	private BigDecimal memoSgstval;

	private BigDecimal memoCessval;

	private Integer diffTotDoc;

	private Integer diffCount;

	private BigDecimal diffTbval;

	private BigDecimal diffTxval;

	private BigDecimal diffInval;

	private BigDecimal diffIgstval;

	private BigDecimal diffCgstval;

	private BigDecimal diffSgstval;

	private BigDecimal diffCessval;

	private BigDecimal diffCancel;

	private BigDecimal diffNetissue;

	private BigDecimal diffNilRsup;

	private BigDecimal diffExpSup;

	private BigDecimal diffNonGsup;

	private BigDecimal memoSuppmode;

	private BigDecimal memoSuppreturn;

	private BigDecimal memoSuppnet;

	private String profitCenter;

	private String plantCode;

	private String salesOrganization;

	private String distChannel;

	private String division;

	private String useraccess1;

	private String useraccess2;

	private String useraccess3;

	private String useraccess4;

	private String useraccess5;

	private String useraccess6;

	private String location;

}
