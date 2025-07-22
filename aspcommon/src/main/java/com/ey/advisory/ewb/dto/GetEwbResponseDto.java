
package com.ey.advisory.ewb.dto; 

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.ey.advisory.common.EwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GetEwbResponseDto implements Serializable
{

    @SerializedName("ewbNo")
    @Expose
	@XmlElement(name = "ewb-no")
    private String ewbNo;
    @SerializedName("ewayBillDate")
    @Expose
	@XmlElement(name = "eway-billdate")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
    private LocalDateTime ewayBillDate;
    @SerializedName("genMode")
    @Expose
	@XmlElement(name = "genmode")
    private String genMode;
    @SerializedName("userGstin")
    @Expose
	@XmlElement(name = "user-gstin")
    private String userGstin;
    @SerializedName("supplyType")
    @Expose
	@XmlElement(name = "supply-type")
    private String supplyType;
    @SerializedName("subSupplyType")
    @Expose
	@XmlElement(name = "subsupply-type")
    private String subSupplyType;
    @SerializedName("docType")
    @Expose
	@XmlElement(name = "doc-type")
    private String docType;
    @SerializedName("docNo")
    @Expose
	@XmlElement(name = "doc-no")
    private String docNo;
    @SerializedName("docDate")
    @Expose
	@XmlElement(name = "doc-date")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
    private LocalDate docDate;
    @SerializedName("fromGstin")
    @Expose
	@XmlElement(name = "from-gstin")
    private String fromGstin;
    @SerializedName("fromTrdName")
    @Expose
	@XmlElement(name = "from-trdname")
    private String fromTrdName;
    @SerializedName("fromAddr1")
    @Expose
	@XmlElement(name = "from-addr1")
    private String fromAddr1;
    @SerializedName("fromAddr2")
    @Expose
	@XmlElement(name = "from-addr2")
    private String fromAddr2;
    @SerializedName("fromPlace")
    @Expose
	@XmlElement(name = "from-place")
    private String fromPlace;
    @SerializedName("fromPincode")
    @Expose
	@XmlElement(name = "from-pincode")
    private Integer fromPincode;
    @SerializedName("fromStateCode")
    @Expose
	@XmlElement(name = "from-statecode")
    private String fromStateCode;
    @SerializedName("toGstin")
    @Expose
	@XmlElement(name = "to-gstin")
    private String toGstin;
    @SerializedName("toTrdName")
    @Expose
	@XmlElement(name = "to-trdname")
    private String toTrdName;
    @SerializedName("toAddr1")
    @Expose
	@XmlElement(name = "to-addr1")
    private String toAddr1;
    @SerializedName("toAddr2")
    @Expose
	@XmlElement(name = "to-addr2")
    private String toAddr2;
    @SerializedName("toPlace")
    @Expose
	@XmlElement(name = "to-place")
    private String toPlace;
    @SerializedName("toPincode")
    @Expose
	@XmlElement(name = "to-pincode")
    private Integer toPincode;
    @SerializedName("toStateCode")
    @Expose
	@XmlElement(name = "to-statecode")
    private String toStateCode;
    @SerializedName("totalValue")
    @Expose
	@XmlElement(name = "total-value")
    private BigDecimal totalValue;
    @SerializedName("totInvValue")
    @Expose
	@XmlElement(name = "totalinv-value")
    private BigDecimal totInvValue;
    @SerializedName("cgstValue")
    @Expose
	@XmlElement(name = "cgst-value")
    private BigDecimal cgstValue;
    @SerializedName("sgstValue")
    @Expose
	@XmlElement(name = "sgst-value")
    private BigDecimal sgstValue;
    @SerializedName("igstValue")
    @Expose
	@XmlElement(name = "igst-value")
    private BigDecimal igstValue;
    @SerializedName("cessValue")
    @Expose
	@XmlElement(name = "cess-value")
    private BigDecimal cessValue;
    @SerializedName("transporterId")
    @Expose
	@XmlElement(name = "transporter-id")
    private String transporterId;
    @SerializedName("transporterName")
    @Expose
	@XmlElement(name = "transporter-name")
    private String transporterName;
    @SerializedName("status")
    @Expose
	@XmlElement(name = "status")
    private String status;
    @SerializedName("actualDist")
    @Expose
	@XmlElement(name = "actual-dist")
    private Integer actualDist;
    @SerializedName("noValidDays")
    @Expose
	@XmlElement(name = "novalid-days")
    private Integer noValidDays;
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
    @SerializedName("validUpto")
    @Expose
	@XmlElement(name = "valid-upto")
    private LocalDateTime validUpto;
    @SerializedName("extendedTimes")
    @Expose
	@XmlElement(name = "extended-times")
    private Integer extendedTimes;
    @SerializedName("rejectStatus")
    @Expose
	@XmlElement(name = "reject-status")
    private String rejectStatus;
    @SerializedName("actFromStateCode")
    @Expose
	@XmlElement(name = "actfrom-stateCode")
    private String actFromStateCode;
    @SerializedName("actToStateCode")
    @Expose
	@XmlElement(name = "actto-stateCode")
    private String actToStateCode;
    @SerializedName("vehicleType")
    @Expose
	@XmlElement(name = "vehicle-type")
    private String vehicleType;
    @SerializedName("transactionType")
    @Expose
	@XmlElement(name = "transaction-type")
    private String transactionType;
    @SerializedName("otherValue")
    @Expose
	@XmlElement(name = "other-value")
    private BigDecimal otherValue;
    @SerializedName("cessNonAdvolValue")
    @Expose
	@XmlElement(name = "cessnonadvol-value")
    private BigDecimal cessNonAdvolValue;
    @SerializedName("subSupplyDesc")
    @Expose
	@XmlElement(name = "subsupply-desc")
    private String subSupplyDesc;
    @SerializedName("itemList")
    @Expose
	@XmlElementWrapper(name = "itemlist")
	@XmlElement(name = "item")
    private List<ItemList> itemList = null;
    @SerializedName("VehiclListDetails")
    @Expose
	@XmlElementWrapper(name = "vehiclelist")
	@XmlElement(name = "vehicle")
    private List<VehiclListDetail> vehiclListDetails = null;
    @SerializedName("errorCode")
    @Expose
	@XmlElement(name = "error-code")
    private String errorCode;
    @SerializedName("errorMessage")
    @Expose
	@XmlElement(name = "error-message")
    private String errorMessage;
    private static final long serialVersionUID = -4168423117565491546L;

}
