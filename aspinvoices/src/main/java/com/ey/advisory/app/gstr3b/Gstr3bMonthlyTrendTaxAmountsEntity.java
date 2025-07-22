package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Setter
@Getter
@Entity
@Table(name = "TBL_MONTHLY_TREND_TAXAMOUNTS")
public class Gstr3bMonthlyTrendTaxAmountsEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="ID",  nullable = false)
	private Long id;
	
	@Column(name = "MONTH",  nullable = false)
	private String taxPeriod;
	
	@Column(name = "API_CALLSTATUS")
	private String status;
	
	@Column(name = "API_DATETIME")
	private LocalDateTime apiDateTime;
	
	@Expose
	@Column(name = "SUPP_GSTIN",   nullable = false)
	private String suppGstin;
	
	@Expose
	@Column(name = "TAXPAY_IGST")
	private BigDecimal taxPayIgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "TAXPAY_CGST")
	private BigDecimal taxPayCgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "TAXPAY_CESS")
	private BigDecimal taxPayCess = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "TAXPAY_SGST")
	private BigDecimal taxPaySgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "TAXPAY_TOTAL")
	private BigDecimal taxPayTotal = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "ITC_IGST")
	private BigDecimal itcIgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "ITC_CGST")
	private BigDecimal itcCgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "ITC_SGST")
	private BigDecimal itcSgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "ITC_TOTAL")
	private BigDecimal itcTotal = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "CASH_IGST")
	private BigDecimal cashIgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "CASH_CGST")
	private BigDecimal cashCgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "CASH_SGST")
	private BigDecimal cashSgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "CASH_TOTAL")
	private BigDecimal cashTotal = BigDecimal.ZERO;
	//new added
	
	
	@Expose
	@Column(name = "ITC_IGST_VS_IGST_OTHERS")
	private BigDecimal igstVsIgstOthers = BigDecimal.ZERO;

	@Expose
	@Column(name = "ITC_IGST_VS_CGST_OTHERS")
	private BigDecimal igstVsCgstOthers = BigDecimal.ZERO;

	@Expose
	@Column(name = "ITC_IGST_VS_SGST_OTHERS")
	private BigDecimal igstVsSgstOthers = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "ITC_CGST_VS_IGST_OTHERS")
	private BigDecimal cgstVsIgstOthers = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "ITC_CGST_VS_CGST_OTHERS")
	private BigDecimal cgstVsCgstOthers = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "ITC_SGST_VS_IGST_OTHERS")
	private BigDecimal sgstVsIgstOthers = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "ITC_SGST_VS_SGST_OTHERS")
	private BigDecimal sgstVsSgstOthers = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "ITC_CESS_VS_CESS_OTHERS")
	private BigDecimal cessVsCessOthers = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "TAX_CASH_IGST_OTHERS")
	private BigDecimal taxCashOthrsIgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "TAX_CASH_CGST_OTHERS")
	private BigDecimal taxCashOthrsCgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "TAX_CASH_SGST_OTHERS")
	private BigDecimal taxCashOthrsSgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "TAX_CASH_CESS_OTHERS")
	private BigDecimal taxCashOthrsCess = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "INTEREST_CASH_IGST_OTHERS")
	private BigDecimal intrstCashOthrsIgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "INTEREST_CASH_CGST_OTHERS")
	private BigDecimal intrstCashOthrsCgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "INTEREST_CASH_SGST_OTHERS")
	private BigDecimal intrstCashOthrsSgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "INTEREST_CASH_CESS_OTHERS")
	private BigDecimal intrstCashOthrsCess = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "LATE_FEE_CGST_OTHERS")
	private BigDecimal lateFeeOthrsCgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "LATE_FEE_SGST_OTHERS")
	private BigDecimal lateFeeOthrsSgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "TAX_CASH_IGST_REVCHRG")
	private BigDecimal taxCashRevIgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "TAX_CASH_CGST_REVCHRG")
	private BigDecimal taxCashRevCgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "TAX_CASH_SGST_REVCHRG")
	private BigDecimal taxCashRevSgst = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "TAX_CASH_CESS_REVCHRG")
	private BigDecimal taxCashRevCess = BigDecimal.ZERO;
	
	
	@Expose
	@Column(name = "created_on")
	private LocalDateTime createDate;
	
	@Expose
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Expose
	@Column(name = "ITC_CESS")
	private BigDecimal itcCess = BigDecimal.ZERO;
	
	@Expose
	@Column(name = "CASH_CESS")
	private BigDecimal cashCess = BigDecimal.ZERO;
	
	
}
