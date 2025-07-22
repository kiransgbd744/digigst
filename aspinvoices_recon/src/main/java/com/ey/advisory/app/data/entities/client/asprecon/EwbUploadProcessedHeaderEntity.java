/**
 * 
 */
package com.ey.advisory.app.data.entities.client.asprecon;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
@Table(name = "TBL_EWB_FU_HEADER")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EwbUploadProcessedHeaderEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_EWB_FU_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "EWB_HEADER_ID")
	protected Long id;

	@Expose
	@Column(name = "EWB_NUMBER")
	private String ewbNo;

	@Expose
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derviedTaxPeriod;

	@Expose
	@Column(name = "EWB_DATE")
	private LocalDate ewbDate;

	@Expose
	@Column(name = "DOC_DATE")
	protected LocalDate docDate;

	@Expose
	@Column(name = "EWB_TIME")
	private LocalTime time;

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
	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

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
	@Column(name = "CANCEL_DATE")
	private String canDate;

	@Expose
	@Column(name = "SUB_SUPPLY_TYPE")
	private String subSupplyType;

	@Expose
	@Column(name = "DOC_NUM")
	private String docNum;

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
	@Column(name = "IGST_AMT")
	private BigDecimal igstAmount;

	@Expose
	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmount;

	@Expose
	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmount;

	@Expose
	@Column(name = "CESS_AMT_ADV")
	private BigDecimal cessAmountAdv;

	@Expose
	@Column(name = "CESS_AMT_SPEC")
	private BigDecimal cessAmountSpec;

	@Expose
	@Column(name = "INVOICE_VALUE")
	private BigDecimal invoiceValue;

	@Expose
	@Column(name = "MODE_OF_TRANS")
	private String modeOfTrans;

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
	@Column(name = "VEHICLE_NO")
	protected String vehicleNo;

	@Expose
	@Column(name = "VEHICLE_TYPE")
	protected String vehicleType;

	@Expose
	@Column(name = "DATA_TYPE")
	private String dataType;

	@Expose
	@Column(name = "DATA_TYPE_ID")
	private Long dataTypeId;

	@Expose
	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Expose
	@Column(name = "ERROR_DESC")
	private String errorDesc;

	@Expose
	@Column(name = "IS_ERROR")
	private Boolean isError;

	@Expose
	@Column(name = "IS_PROCESSED")
	private Boolean isProcessed;

	@Expose
	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Expose
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

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
	@Column(name = "ASSESSABLE_VAL")
	private BigDecimal assessableVal;

	@Expose
	@Column(name = "OTHER_VAL")
	private BigDecimal otherVal;

	@Expose
	@Column(name = "DATAORIGINTYPECODE", columnDefinition = "varchar(10) default 'E'")
	protected String dataOrigin;

	@Expose
	@Column(name = "GENERATOR_GSTIN")
	protected String genGstin;

	@Expose
	@Column(name = "EXTENDED_TIME")
	protected Integer extendedTime;

	@Expose
	@Column(name = "NO_VALID_DAYS")
	protected Integer noValidDays;

	@Expose
	@Column(name = "REJECTION_STATUS")
	protected String rejectStatus;

	@Expose
	@Column(name = "DISTANCE")
	protected Long distance;

	@OneToMany(mappedBy = "ewbHeaderId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	protected List<EwbUploadProcessedItemEntity> lineItems = new ArrayList<>();

	@OneToMany(mappedBy = "ewbHeaderId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	protected List<EwbUploadProcessedVehicleEntity> vehDtls = new ArrayList<>();

	@Expose
	@Column(name = "EINV_APPL")
	protected boolean eInvAppl;

	@Expose
	@Column(name = "COMPLIANCE_APPL")
	protected boolean complianceAppl;

	@Expose
	@Transient
	protected LocalDateTime ewbDateTime;

}
