/**
 * 
 */
package com.ey.advisory.app.data.entities.client.asprecon;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
@Table(name = "TBL_EWB_FU_ITEM")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EwbUploadProcessedItemEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_EWB_FU_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "EWB_ITEM_ID")
	protected Long id;

	@ManyToOne
	@JoinColumn(name = "EWB_HEADER_ID", referencedColumnName = "EWB_HEADER_ID")
	protected EwbUploadProcessedHeaderEntity ewbHeaderId;

	@Expose
	@Column(name = "EWB_NUMBER")
	protected Long ewbNo;

	@Expose
	@Column(name = "Supply_Type")
	protected String supplyType;

	@Expose
	@Column(name = "EWB_DATE")
	protected LocalDate ewbDate;

	@Expose
	@Column(name = "EWB_TIME")
	protected LocalTime ewbTime;

	@Expose
	@Column(name = "EWB_STATUS")
	private String ewbStatus;

	@Expose
	@Column(name = "CANCEL_REASON")
	private String canReason;

	@Expose
	@Column(name = "CANCEL_REMARK")
	private String canRemark;
	@Expose
	@Column(name = "VALID_TILL_DATE")
	private String validTillDate;

	@Expose
	@Column(name = "TRANS_TYPE")
	private String transType;

	@Expose
	@Column(name = "DOC_CATEGORY")
	private String docCatg;

	@Expose
	@Column(name = "DOC_TYPE")
	private String docType;

	@Expose
	@Column(name = "OTH_PARTY_GSTIN")
	private String othPartyGstin;

	@Expose
	@Column(name = "FROM_GSTIN_INFO")
	private String fromGstinInfo;

	@Expose
	@Column(name = "TO_GSTIN_INFO")
	private String toGstinInfo;

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
	protected LocalDate docDate;

	@Expose
	@Column(name = "SUPP_GSTIN")
	private String supplierGstin;

	@Expose
	@Column(name = "SUPP_TRADE_NAME")
	private String supplierTradeName;

	@Expose
	@Column(name = "SUPP_ADD1")
	private String supplierAddress1;

	@Expose
	@Column(name = "SUPP_ADD2")
	private String supplyAdd2;

	@Expose
	@Column(name = "SUPP_LOCATION")
	private String supplyLocation;

	@Expose
	@Column(name = "SUPP_PINCODE")
	private String supplyPincode;

	@Expose
	@Column(name = "SUPP_STATECODE")
	private String supplyStatecode;

	@Expose
	@Column(name = "CUST_GSTIN")
	private String customerGstin;

	@Expose
	@Column(name = "CUST_TRADENAME")
	private String customerTradename;

	@Expose
	@Column(name = "CUST_ADDR1")
	private String customerAddr1;

	@Expose
	@Column(name = "CUST_ADDR2")
	private String customerAddr2;

	@Expose
	@Column(name = "CUST_LOCATION")
	private String customerLocation;

	@Expose
	@Column(name = "CUST_PINCODE")
	private String customerPincode;

	@Expose
	@Column(name = "CUST_STATECODE")
	private String customerStatecode;

	@Expose
	@Column(name = "DISP_GSTIN")
	private String dispGstin;

	@Expose
	@Column(name = "DISP_TRADENAME")
	private String dispTradename;

	@Expose
	@Column(name = "DISP_ADDR1")
	private String dispAddr1;

	@Expose
	@Column(name = "DISP_ADDR2")
	private String dispAddr2;

	@Expose
	@Column(name = "DISP_LOCATION")
	private String dispLocation;

	@Expose
	@Column(name = "DISP_PINCODE")
	private String dispPincode;

	@Expose
	@Column(name = "DISP_STATECODE")
	private String dispStatecode;

	@Expose
	@Column(name = "SHIP_TO_GSTIN")
	private String shipToGstin;

	@Expose
	@Column(name = "SHIP_TO_TRADENAME")
	private String shipToTradename;

	@Expose
	@Column(name = "SHIP_TO_ADDR1")
	private String shipToAaddr1;

	@Expose
	@Column(name = "SHIP_To_ADDR2")
	private String shipT0Aaddr2;

	@Expose
	@Column(name = "SHIP_TO_LOCATION")
	private String shipToLocation;

	@Expose
	@Column(name = "SHIP_TO_PINCODE")
	private String shipToPincode;

	@Expose
	@Column(name = "SHIP_TO_STATECODE")
	private String shipToStatecode;

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
	protected BigDecimal quantity;

	@Expose
	@Column(name = "ITEM_ASSE_AMT")
	protected BigDecimal itemAsseAmount;

	@Expose
	@Column(name = "IGST_RATE")
	protected BigDecimal igstRate;

	@Expose
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmount;

	@Expose
	@Column(name = "CGST_RATE")
	protected BigDecimal cgstRate;

	@Expose
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmount;

	@Expose
	@Column(name = "SGST_RATE")
	protected BigDecimal sgstRate;

	@Expose
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmount;

	@Expose
	@Column(name = "CESS_RATE_ADV")
	protected BigDecimal cessRateAdv;

	@Expose
	@Column(name = "CESS_AMT_ADV")
	protected BigDecimal cessAmountAdv;

	@Expose
	@Column(name = "CESS_RATE_SPEC")
	protected BigDecimal cessRateSpec;

	@Expose
	@Column(name = "CESS_AMT_SPEC")
	protected BigDecimal cessAmountSpec;

	@Expose
	@Column(name = "OTH_VALUE")
	protected BigDecimal otherValue;

	@Expose
	@Column(name = "INVOICE_VALUE")
	protected BigDecimal invoiceValue;

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
	protected Long distance;

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

	@Expose
	@Column(name = "DATAORIGINTYPECODE",columnDefinition = "varchar(10) default 'E'")
	protected String dataOrigin;
}
