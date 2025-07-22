/**
 * 
 */
package com.ey.advisory.app.data.entities.client.asprecon;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Entity
@Table(name = "TBL_EWB_FU_ITEM_ERROR")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EwbUploadErrorItemEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "EWB_ITEM_ID")
	protected Long id;

	@ManyToOne
	@JoinColumn(name = "EWB_HEADER_ID", referencedColumnName = "EWB_HEADER_ID")
	protected EwbUploadErrorHeaderEntity ewbHeaderId;

	@Expose
	@Column(name = "EWB_NUMBER")
	protected String ewbNumber;

	@Expose
	@Column(name = "EWB_DATE")
	protected String ewbDate;

	@Expose
	@Column(name = "EWB_TIME")
	protected String ewbTime;

	@Expose
	@Column(name = "EWB_STATUS")
	protected String ewbStatus;

	@Expose
	@Column(name = "CANCEL_REASON")
	protected String cancelReason;

	@Expose
	@Column(name = "CANCEL_REMARK")
	protected String cancelRemark;

	@Expose
	@Column(name = "VALID_TILL_DATE")
	protected String validTillDate;

	@Expose
	@Column(name = "TRANS_TYPE")
	protected String transactionType;

	@Expose
	@Column(name = "DOC_CATEGORY")
	protected String documentCategory;

	@Expose
	@Column(name = "DOC_TYPE")
	protected String docType;

	@Expose
	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	@Expose
	@Column(name = "OTH_PARTY_GSTIN")
	protected String otherPartyGstin;

	@Expose
	@Column(name = "FROM_GSTIN_INFO")
	protected String fromGstinInfo;

	@Expose
	@Column(name = "TO_GSTIN_INFO")
	protected String toGstinInfo;

	@Expose
	@Column(name = "MODE_OF_GEN")
	protected String modeofGeneration;

	@Expose
	@Column(name = "CANCEL_BY")
	protected String cancelBy;

	@Expose
	@Column(name = "CANCEL_DATE")
	protected String cancelDate;

	@Expose
	@Column(name = "SUB_SUPPLY_TYPE")
	protected String subSupplyType;

	@Expose
	@Column(name = "OTH_SUPPLY_TYPE")
	protected String otherSupplyType;

	@Expose
	@Column(name = "DOC_NUM")
	protected String docNumber;

	@Expose
	@Column(name = "DOC_DATE")
	protected String docDate;

	@Expose
	@Column(name = "SUPP_GSTIN")
	protected String supplierGstin;

	@Expose
	@Column(name = "SUPP_TRADE_NAME")
	protected String supplierTradeName;

	@Expose
	@Column(name = "SUPP_ADD1")
	protected String supplierAdd1;

	@Expose
	@Column(name = "SUPP_ADD2")
	protected String supplierAdd2;

	@Expose
	@Column(name = "SUPP_LOCATION")
	protected String supplierLocation;

	@Expose
	@Column(name = "SUPP_PINCODE")
	protected String supplierPincode;

	@Expose
	@Column(name = "SUPP_STATECODE")
	protected String supplierStatecode;

	@Expose
	@Column(name = "CUST_GSTIN")
	protected String customerGstin;

	@Expose
	@Column(name = "CUST_TRADENAME")
	protected String customerTradename;

	@Expose
	@Column(name = "CUST_ADDR1")
	protected String customerAddr1;

	@Expose
	@Column(name = "CUST_ADDR2")
	protected String customerAddr2;

	@Expose
	@Column(name = "CUST_LOCATION")
	protected String customerLocation;

	@Expose
	@Column(name = "CUST_PINCODE")
	protected String customerPincode;

	@Expose
	@Column(name = "CUST_STATECODE")
	protected String customerStatecode;

	@Expose
	@Column(name = "DISP_GSTIN")
	protected String dispGstin;

	@Expose
	@Column(name = "DISP_TRADENAME")
	protected String dispTradename;

	@Expose
	@Column(name = "DISP_ADDR1")
	protected String dispAddr1;

	@Expose
	@Column(name = "DISP_ADDR2")
	protected String dispAddr2;

	@Expose
	@Column(name = "DISP_LOCATION")
	protected String dispLocation;

	@Expose
	@Column(name = "DISP_PINCODE")
	protected String dispPincode;

	@Expose
	@Column(name = "DISP_STATECODE")
	protected String dispStatecode;

	@Expose
	@Column(name = "SHIP_TO_GSTIN")
	protected String shipToGstin;

	@Expose
	@Column(name = "SHIP_TO_TRADENAME")
	protected String shipToTradename;

	@Expose
	@Column(name = "SHIP_TO_ADDR1")
	protected String shipToAaddr1;

	@Expose
	@Column(name = "SHIP_To_ADDR2")
	protected String shipT0Aaddr2;

	@Expose
	@Column(name = "SHIP_TO_LOCATION")
	protected String shipToLocation;

	@Expose
	@Column(name = "SHIP_TO_PINCODE")
	protected String shipToPincode;

	@Expose
	@Column(name = "SHIP_TO_STATECODE")
	protected String shipToStatecode;

	@Expose
	@Column(name = "ITEM_SER_NO")
	protected String itemSerialNo;

	@Expose
	@Column(name = "PROD_NAME")
	protected String productName;

	@Expose
	@Column(name = "PROD_DESC")
	protected String productDesc;

	@Expose
	@Column(name = "HSN")
	protected String hsn;

	@Expose
	@Column(name = "UQC")
	protected String uqc;

	@Expose
	@Column(name = "QUANTIY")
	protected String quantity;

	@Expose
	@Column(name = "ITEM_ASSE_AMT")
	protected String itemAsseAmount;

	@Expose
	@Column(name = "IGST_RATE")
	protected String igstRate;

	@Expose
	@Column(name = "IGST_AMT")
	protected String igstAmount;

	@Expose
	@Column(name = "CGST_RATE")
	protected String cgstRate;

	@Expose
	@Column(name = "CGST_AMT")
	protected String cgstAmount;

	@Expose
	@Column(name = "SGST_RATE")
	protected String sgstRate;

	@Expose
	@Column(name = "SGST_AMT")
	protected String sgstAmount;

	@Expose
	@Column(name = "CESS_RATE_ADV")
	protected String cessRateAdv;

	@Expose
	@Column(name = "CESS_AMT_ADV")
	protected String cessAmountAdv;

	@Expose
	@Column(name = "CESS_RATE_SPEC")
	protected String cessRateSpec;

	@Expose
	@Column(name = "CESS_AMT_SPEC")
	protected String cessAmountSpec;

	@Expose
	@Column(name = "OTH_VALUE")
	protected String otherValue;

	@Expose
	@Column(name = "INVOICE_VALUE")
	protected String invoiceValue;

	@Expose
	@Column(name = "MODE_OF_TRANS")
	protected String modeOfTrans;

	@Expose
	@Column(name = "TRANS_ID")
	protected String transactionId;

	@Expose
	@Column(name = "TRANS_NAME")
	protected String transactionName;

	@Expose
	@Column(name = "TRANS_DOC_NO")
	protected String transactionDocNo;

	@Expose
	@Column(name = "TRANS_DOC_DATE")
	protected String transactionDocDate;

	@Expose
	@Column(name = "DISTANCE")
	protected String distance;

	@Expose
	@Column(name = "VEHICLE_NO")
	protected String vehicleNo;

	@Expose
	@Column(name = "VEHICLE_TYPE")
	protected String vehicleType;

	@Expose
	@Column(name = "PLANT_CODE")
	protected String plantCode;

	@Expose
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@Column(name = "PROFIT_CENTRE1")
	protected String profitCentre1;

	@Expose
	@Column(name = "PROFIT_CENTRE2")
	protected String profitCentre2;

	@Expose
	@Column(name = "USERDEFINED_FIELD1")
	protected String userDefinedField1;

	@Expose
	@Column(name = "USERDEFINED_FIELD2")
	protected String userDefinedField2;

	@Expose
	@Column(name = "USERDEFINED_FIELD3")
	protected String userDefinedField3;

	@Expose
	@Column(name = "USERDEFINED_FIELD4")
	protected String userDefinedField4;

	@Expose
	@Column(name = "USERDEFINED_FIELD5")
	protected String userDefinedField5;

	@Expose
	@Column(name = "ERROR_CODE")
	protected String errorCode;

	@Expose
	@Column(name = "ERROR_DESC")
	protected String errorDesc;

	@Expose
	@Column(name = "CREATED_DATE")
	protected LocalDateTime createdDate;

	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;
	
	@Expose
	@Column(name = "DOC_KEY")
	protected String docKey;

	@Expose
	@Column(name = "FILE_ID")
	protected Long fileId;
}
