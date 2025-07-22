package com.ey.advisory.app.data.views.client;

import lombok.Data;

/**
 * @author Mahesh.Golla
 *
 */
@Data
public class AspProcessVsSubmitAdditionalGstnDto {
	private String format;
	private String irn;
	private String irnDate;
	private String eInvStatus;
	private String autoDraftStatus;
	private String autoDraftDate;
	private String errorCode;
	private String errorMessage;
	private String retPeriod;
	private String supplierGstn;
	private String custGstin;
	private String custTradeName;
	private String documentType;
	private String suuplyType;
	private String documentNumber;
	private String documnetDate;
	private String billingPos;
	private String portCode;
	private String shippingBillNumber;
	private String shippingBillDate;
	private String revChargeFlag;
	private String ecomGstin;
	private String itemSerialNumber;
	private String itemAccessableAmt;
	private String taxRate;
	private String igstAmt;
	private String cgstAmt;
	private String sgstAmt;
	private String cessAmt;
	private String invValue;
	private String sourceTypeIrn;
	private String tableType;
	private String derRetPeriod;
}
