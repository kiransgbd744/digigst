/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ImsHeaderDto {

	private Long id;
	private String recipientGstin;
	private String supplierGstin;
	private String supplierLegalName;
	private String supplierTradeName;
	private String invoiceNumber;
	private String invoiceType;
	private Date invoiceDate;
	private String action;
	private String isPendingActionBlocked;
	private String formType;
	private String returnPeriod;
	private Long derivedRetPeriod;
	private String filingStatus;
	private BigDecimal invoiceValue;
	private BigDecimal taxableValue;
	private BigDecimal igstAmt;
	private BigDecimal cgstAmt;
	private BigDecimal sgstAmt;
	private BigDecimal cessAmt;
	private String pos;
	private String chksum;
	private String docKey;
	private LocalDateTime createdOn;
	private String tableType;
	private String orgInvoiceNumber;
	private Date orgInvoiceDate;
	private String gstnInvType;
	private String linkingDocKey;
	

}
