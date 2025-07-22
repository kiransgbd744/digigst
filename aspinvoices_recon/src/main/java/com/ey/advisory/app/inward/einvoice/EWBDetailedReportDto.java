/**
 * 
 */
package com.ey.advisory.app.inward.einvoice;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ravindra V S
 *
 */

@Getter
@Setter
public class EWBDetailedReportDto {

	private String ewbNo;
	private String ewbDate;
	private String status;
	private String validUpto;
	private String supplyType;
	private String transactionType;
	private String docType;
	private String subSupplyType;
	private String docNo;
	private String docDate;
	private String fromGstin;
	private String fromTrdName;
	private String fromAddr1;
	private String fromAddr2;
	private String fromPlace;
	private String fromPincode;
	private String fromStateCode;
	private String toGstin;
	private String toTrdName;
	private String toAddr1;
	private String toAddr2;
	private String toPlace;
	private String toPincode;
	private String toStateCode;
	private String dispatcherGstin;
	private String dispatcherTrdName;
	private String dispatcherAddr1;
	private String dispatcherAddr2;
	private String dispatcherPlace;
	private String dispatcherPincode;
	private String dispatcherStateCode;
	private String shipToGstin;
	private String shipToTrdName;
	private String shipToAddr1;
	private String shipToAddr2;
	private String shipToPlace;
	private String shipToPincode;
	private String shipToStateCode;
	private String itemNo;
	private String productName;
	private String productDesc;
	private String hsnCode;
	private String qtyUnit;
	private String quantity;
	private String taxableAmount;
	private String igstRate;
	private String cgstRate;
	private String sgstRate;
	private String cessRate;
	private String cessNonAdvol;
	private String otherValue;
	private String igstValue;
	private String cgstValue;
	private String sgstValue;
	private String cessValue;
	private String totInvValue;
	private String transMode;
	private String transporterId;
	private String transporterName; 
	private String transDocNo;
	private String transDocDate;
	private String actualDist;
	private String vehicleNo;
	private String vehicleType;
	private String userGstin;
	private String extendedTimes;
	private String rejectStatus;
	private String genMode;
	private String modeOfDataProcessing;
}
