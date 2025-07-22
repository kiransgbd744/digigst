/**
 * 
 */
package com.ey.advisory.app.data.entities.client.asprecon;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "TBL_EWB_FU_HEADER_ERROR")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EwbUploadErrorHeaderEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "EWB_HEADER_ID")
	protected Long id;

	@Expose
	@Column(name = "DOC_DATE")
	protected String docDate;
	
	@Expose
	@Column(name = "RETURN_PERIOD")
	protected String returnPeriod;

	@Expose
	@Column(name = "DERIVED_RET_PERIOD")
	protected String derivedRetPeriod;

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
	protected String TransType;

	@Expose
	@Column(name = "DOC_CATEGORY")
	protected String docCategory;

	@Expose
	@Column(name = "DOC_TYPE")
	protected String docType;

	@Expose
	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	@Expose
	@Column(name = "OTH_PARTY_GSTIN")
	protected String othPartyGstin;

	@Expose
	@Column(name = "FROM_GSTIN_INFO")
	protected String fromGstinInfo;

	@Expose
	@Column(name = "TO_GSTIN_INFO")
	protected String toGstinInfo;

	@Expose
	@Column(name = "CANCEL_DATE")
	protected String CancelDate;

	@Expose
	@Column(name = "SUB_SUPPLY_TYPE")
	protected String subSupplyType;

	@Expose
	@Column(name = "DOC_NUM")
	protected String docNum;

	@Expose
	@Column(name = "SUPP_GSTIN")
	protected String suppGstin;

	@Expose
	@Column(name = "SUPP_TRADE_NAME")
	protected String suppTradeName;

	@Expose
	@Column(name = "SUPP_ADD1")
	protected String suppAdd1;

	@Expose
	@Column(name = "SUPP_ADD2")
	protected String suppAdd2;

	@Expose
	@Column(name = "SUPP_LOCATION")
	protected String suppLocation;

	@Expose
	@Column(name = "SUPP_PINCODE")
	protected String suppPincode;

	@Expose
	@Column(name = "SUPP_STATECODE")
	protected String suppStatecode;

	@Expose
	@Column(name = "CUST_GSTIN")
	protected String custGstin;

	@Expose
	@Column(name = "CUST_TRADENAME")
	protected String custTradename;

	@Expose
	@Column(name = "CUST_ADDR1")
	protected String custAddr1;

	@Expose
	@Column(name = "CUST_ADDR2")
	protected String custAddr2;

	@Expose
	@Column(name = "CUST_LOCATION")
	protected String custLocation;

	@Expose
	@Column(name = "CUST_PINCODE")
	protected String cust_Pincode;

	@Expose
	@Column(name = "CUST_STATECODE")
	protected String cust_Statecode;

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
	@Column(name = "IGST_AMT")
	protected String igstAmt;

	@Expose
	@Column(name = "CGST_AMT")
	protected String cgstAmt;

	@Expose
	@Column(name = "SGST_AMT")
	protected String sgstAmt;

	@Expose
	@Column(name = "CESS_AMT_ADV")
	protected String cessAmtAdv;

	@Expose
	@Column(name = "CESS_AMT_SPEC")
	protected String cessAmtSpec;

	@Expose
	@Column(name = "INVOICE_VALUE")
	protected String invoiceValue;

	@Expose
	@Column(name = "MODE_OF_TRANS")
	protected String modeOfTrans;

	@Expose
	@Column(name = "DATA_TYPE")
	protected String dataType;

	@Expose
	@Column(name = "DATA_TYPE_ID")
	protected String dataTypeID;

	@Expose
	@Column(name = "ERROR_CODE")
	protected String errorCode;

	@Expose
	@Column(name = "ERROR_DESC")
	protected String errorDesc;

	@Expose
	@Column(name = "IS_ERROR")
	protected String isError;

	@Expose
	@Column(name = "IS_PROCESSED")
	protected String isProcessed;

	@Expose
	@Column(name = "CREATED_DATE")
	protected LocalDateTime createdDate;

	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@Column(name = "IS_DELETE")
	protected boolean isDelete;
	
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
	
	@OneToMany(mappedBy = "ewbHeaderId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected List<EwbUploadErrorItemEntity> lineItems = new ArrayList<>();


}
