package com.ey.advisory.ewb.client.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.ey.advisory.common.LocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 
 * @author Arun.KA
 *
 */
@Entity
@Table(name = "EWB_HEADER")
@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EWBHeader {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@XmlTransient
	private Long id;

	@Column(name = "EWB_NO")
	@Expose
	@SerializedName("ewbNo")
	@XmlElement(name = "EWB_NO")
	private String ewbNo;

	@Column(name = "EWAY_BILL_DATE")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "EWAY_BILL_DATE")
	private LocalDateTime ewbBillDate;

	@Column(name = "GEN_MODE")
	@XmlElement(name = "GEN_MODE")
	private String genMode;
	
	@Column(name = "USER_GSTIN")
	@XmlElement(name = "USER_GSTIN")
	private String userGstin;
	
	@Column(name = "SUPPLY_TYPE")
	@XmlElement(name = "SUPPLY_TYPE")
	private String supplyType;
	
	@Column(name = "SUB_SUPPLY_TYPE")
	@XmlElement(name = "SUB_SUPPLY_TYPE")
	private String subSupplyType;

	@Column(name = "DOC_TYPE")
	@Expose
	@SerializedName("docType")
	@XmlElement(name = "DOC_TYPE")
	private String docType;

	@Column(name = "DOC_NO")
	@Expose
	@SerializedName("docNo")
	@XmlElement(name = "DOC_NO")
	private String docNo;

	@Column(name = "DOC_DATE")
	@Expose
	@SerializedName("docDate")
	@XmlJavaTypeAdapter(value = LocalDateAdapter.class)
	@XmlElement(name = "DOC_DATE")
	private LocalDate docDate;

	@Column(name = "FROM_GSTIN")
	@Expose
	@SerializedName("fromGstin")
	@XmlElement(name = "FROM_GSTIN")
	private String fromGstin;
	
	@Column(name = "CLIENT_GSTIN")
	@Expose
	@SerializedName("clientGstin")
	@XmlElement(name = "CLIENT_GSTIN")
	private String clientGstin;

	@Column(name = "FROM_TRD_NAME")
	@Expose
	@SerializedName("fromTradeName")
	@XmlElement(name = "FROM_TRD_NAME")
	private String fromTradeName;

	@Column(name = "FROM_ADDR1")
	@Expose
	@SerializedName("fromAddr1")
	@XmlElement(name = "FROM_ADDR1")
	private String fromAddr1;

	@Column(name = "FROM_ADDR2")
	@Expose
	@SerializedName("fromAddr2")
	@XmlElement(name = "FROM_ADDR2")
	private String fromAddr2;

	@Column(name = "FROM_PLACE")
	@Expose
	@SerializedName("fromPlace")
	@XmlElement(name = "FROM_PLACE")
	private String fromPlace;

	@Column(name = "FROM_PINCODE")
	@Expose
	@SerializedName("fromPincode")
	@XmlElement(name = "FROM_PINCODE")
	private Integer fromPincode;

	@Column(name = "FROM_STATE_CODE")
	@Expose
	@SerializedName("fromStateCode")
	@XmlElement(name = "FROM_STATE_CODE")
	private String fromStateCode;

	@Column(name = "TO_GSTIN")
	@Expose
	@SerializedName("toGstin")
	@XmlElement(name = "TO_GSTIN")
	private String toGstin;

	@Column(name = "TO_TRD_NAME")
	@Expose
	@SerializedName("toTrdName")
	@XmlElement(name = "TO_TRD_NAME")
	private String toTrdName;

	@Column(name = "TO_ADDR_1")
	@Expose
	@SerializedName("toAddr1")
	@XmlElement(name = "TO_ADDR_1")
	private String toAddr1;

	@Column(name = "TO_ADDR_2")
	@Expose
	@SerializedName("toAddr2")
	@XmlElement(name = "TO_ADDR_2")
	private String toAddr2;

	@Column(name = "TO_PLACE")
	@Expose
	@SerializedName("toPlace")
	@XmlElement(name = "TO_PLACE")
	private String toPlace;

	@Column(name = "TO_PINCODE")
	@Expose
	@SerializedName("toPincode")
	@XmlElement(name = "TO_PINCODE")
	private Integer toPincode;

	@Column(name = "TO_STATE_CODE")
	@Expose
	@SerializedName("toStateCode")
	@XmlElement(name = "TO_STATE_CODE")
	private String toStateCode;

	@Column(name = "TOTAL_VALUE")
	@Expose
	@SerializedName("totalVal")
	@XmlElement(name = "TOTAL_VALUE")
	private BigDecimal totalVal = BigDecimal.ZERO;

	@Column(name = "TOT_INV_VALUE")
	@Expose
	@SerializedName("totInvoiceVal")
	@XmlElement(name = "TOT_INV_VALUE")
	private BigDecimal totInvoiceVal = BigDecimal.ZERO;

	@Column(name = "CGST_VALUE")
	@Expose
	@SerializedName("cgst")
	@XmlElement(name = "CGST_VALUE")
	private BigDecimal cgst = BigDecimal.ZERO;

	@Column(name = "SGST_VALUE")
	@Expose
	@SerializedName("sgst")
	@XmlElement(name = "SGST_VALUE")
	private BigDecimal sgst = BigDecimal.ZERO;

	@Column(name = "IGST_VALUE")
	@Expose
	@SerializedName("igst")
	@XmlElement(name = "IGST_VALUE")
	private BigDecimal igst = BigDecimal.ZERO;

	@Column(name = "CESS_VALUE")
	@Expose
	@SerializedName("cess")
	@XmlElement(name = "CESS_VALUE")
	private BigDecimal cess = BigDecimal.ZERO;

	@Column(name = "GEN_GSTIN")
	@Expose
	@SerializedName("genGstin")
	@XmlElement(name = "GEN_GSTIN")
	private String genGstin;

	@Column(name = "TRANSPORTER_ID")
	@Expose
	@SerializedName("transpoterId")
	@XmlElement(name = "TRANSPORTERID")
	private String transpoterId;

	@Column(name = "TRANSPORTER_NAME")
	@Expose
	@SerializedName("transpoterName")
	@XmlElement(name = "TRANSPORTERNAME")
	private String transpoterName;

	@Column(name = "STATUS")
	@Expose
	@XmlElement(name = "STATUS")
	private String status;
	
	@Column(name = "ACTUAL_DIST")
	@Expose
	@XmlElement(name = "ACTUAL_DIST")
	private Integer actualDist;
	
	@Column(name = "NO_VALID_DAYS")
	@Expose
	@XmlElement(name = "NO_VALID_DAYS")
	private Integer noValidDays;

	@Column(name = "VALID_UPTO")
	@Expose
	@SerializedName("validUpto")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "VALID_UPTO")
	private LocalDateTime validUpto;

	@Column(name = "EXTENDED_TIMES")
	@Expose
	@XmlElement(name = "EXTENDED_TIMES")
	@SerializedName("extendedTimes")
	private Integer extendedTimes;

	@Column(name = "REJECT_STATUS")
	@Expose
	@SerializedName("rejectStatus")
	@XmlElement(name = "REJECT_STATUS")
	private String rejectStatus;

	@Column(name = "ACT_FROM_STATE_CODE")
	@Expose
	@SerializedName("actFromStateCode")
	@XmlElement(name = "ACTFROMSTATECODE")
	private String actFromStateCode;

	@Column(name = "ACT_TO_STATE_CODE")
	@Expose
	@SerializedName("actToStateCode")
	@XmlElement(name = "ACTTOSTATECODE")
	private String actToStateCode;

	@Column(name = "VEHICLE_TYPE")
	@XmlElement(name = "VEHICLE_TYPE")
	@Expose
	@SerializedName("vehicleType")
	private String vehicleType;

	@Column(name = "TRANSACTION_TYPE")
	@Expose
	@SerializedName("transactionType")
	@XmlElement(name = "TRANSACTIONTYPE")
	private String transactionType;

	@Column(name = "OTHER_VALUE")
	@Expose
	@SerializedName("otherValue")
	@XmlElement(name = "OTHER_VALUE")
	private BigDecimal otherValue = BigDecimal.ZERO;

	@Column(name = "CESS_NON_ADVOL_VALUE")
	@Expose
	@SerializedName("cessNonAdvolValue")
	@XmlElement(name = "CESSNONADVOLVAL")
	private BigDecimal cessNonAdvolValue = BigDecimal.ZERO;
	
	@Column(name = "SUB_SUPPLY_DESC")
	@Expose
	@SerializedName("subSupplyDesc")
	@XmlElement(name = "SUB_SUPPLY_DESC")
	private String subSupplyDesc;

	@Column(name = "CREATED_BY")
	@Expose
	@SerializedName("createdBy")
	@XmlTransient
	private String createdBy;

	@Column(name = "CREATED_ON")
	@XmlTransient
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	@XmlTransient
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	@XmlTransient
	private LocalDateTime modifiedOn;

	@Column(name = "IS_DELETE")
	@XmlTransient
	private Boolean isDelete;
	
	@OneToMany(mappedBy = "eWayBill")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@Expose
	@LazyCollection(LazyCollectionOption.FALSE)
	@SerializedName("itemList")
	@XmlElementWrapper(name= "ITEM_DETAILS")
	@XmlElement(name= "item")
	private List<EWBItem> items = new ArrayList<>();

	/**
	 * Use the batch size annotation so that hibernate will reduce the number of
	 * queries executed to fetch the transportaiton list (especially in a search
	 * situation)
	 */
	@OneToMany(mappedBy = "eWayBill")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@Expose
	@LazyCollection(LazyCollectionOption.FALSE)
	@SerializedName("vehiclesDetailsList")
	@XmlElementWrapper(name= "TRANS_DET")
	@XmlElement(name= "item")
	private List<EWBVehicle> transportationList = new ArrayList<>() ;

}
