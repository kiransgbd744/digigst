/**
 * 
 */
package com.ey.advisory.app.data.services.itc04stocktrack;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Siva.Reddy
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Itc04StockTrackingDownloadDto {
	private String tableNumber;
	private String fiscalYear;
	private String returnPeriod;
	private String supplierGSTIN;
	private String deliveryChallanNumber;
	private String deliveryChallanDate;
	private String jobWorkerGSTIN;
	private String jobWorkerStateCode;
	private String jobWorkerType;
	private String jobWorkerID;
	private String jobWorkerName;
	private String typeOfGoods;
	private String itemSerialNumber;
	private String productDescription;
	private String productCode;
	private String natureOfJW;
	private String HSN;
	private String UQC;
	private BigDecimal quantity;
	private String lossesUQC;
	private BigDecimal lossesQuantity;
	private BigDecimal itemAssessableAmount;
	private BigDecimal igstRate;
	private BigDecimal igstAmount;
	private BigDecimal cgstRate;
	private BigDecimal cgstAmount;
	private BigDecimal sgstRate;
	private BigDecimal sgstAmount;
	private BigDecimal cessAdvaloremRate;
	private BigDecimal cessAdvaloremAmount;
	private BigDecimal cessSpecificRate;
	private BigDecimal cessSpecificAmount;
	private BigDecimal stateCessAdvaloremRate;
	private BigDecimal stateCessAdvaloremAmount;
	private BigDecimal stateCessSpecificRate;
	private BigDecimal stateCessSpecificAmount;
	private BigDecimal totalValue;
	private String postingDate;
	private String userID;
	private String companyCode;
	private String plantCode;
	private String division;
	private String profitCentre1;
	private String profitCentre2;
	private String accountingVoucherNumber;
	private String accountingVoucherDate;
	private String userDefinedField1;
	private String userDefinedField2;
	private String userDefinedField3;
	private String status;
	private String goodsReceived;
	private String jwDeliveryChallanNumber;
	private String jwDeliveryChallanDate;
	private String goodsReceivingDate;
	private String invoiceNumber;
	private String invoiceDate;
	private String agingInDays;
}
