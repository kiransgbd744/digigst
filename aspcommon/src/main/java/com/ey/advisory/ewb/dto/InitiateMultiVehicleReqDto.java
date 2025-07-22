package com.ey.advisory.ewb.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InitiateMultiVehicleReqDto implements Serializable
{
	
	@XmlElement(name = "id-token")
	private String idToken;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
    /**
     * Ewaybill Number
     * (Required)
     * 
     */
    @SerializedName("ewbNo")
    @Expose
    @XmlElement(name = "ewb-no")
    private String ewbNo;
    /**
     * Reason Code
     * (Required)
     * 
     */
    @SerializedName("reasonCode")
    @Expose
    @XmlElement(name = "rsn-code")
    private String reasonCode;
    /**
     * Remarks
     * (Required)
     * 
     */
    @SerializedName("reasonRem")
    @Expose
    @XmlElement(name = "rsn-rem")
    private String reasonRem;
    /**
     * From Place
     * (Required)
     * 
     */
    @SerializedName("fromPlace")
    @Expose
    @XmlElement(name = "from-pace")
    private String fromPlace;
    /**
     * From State
     * (Required)
     * 
     */
    @SerializedName("fromState")
    @Expose
    @XmlElement(name = "from-state")
    private String fromState;
    /**
     * To Place
     * (Required)
     * 
     */
    @SerializedName("toPlace")
    @Expose
    @XmlElement(name = "to-place")
    private String toPlace;
    /**
     * From State
     * (Required)
     * 
     */
    @SerializedName("toState")
    @Expose
    @XmlElement(name = "to-state")
    private String toState;
    /**
     * Mode of transport (Road-1, Rail-2, Air-3, Ship-4) 
     * (Required)
     * 
     */
    @SerializedName("transMode")
    @Expose
    @XmlElement(name = "trans-mode")
    private String transMode;
    /**
     * Total Quantity
     * (Required)
     * 
     */
    @SerializedName("totalQuantity")
    @Expose
    @XmlElement(name = "tot-quantity")
    private BigDecimal totalQuantity;
    /**
     * Unit Code
     * (Required)
     * 
     */
    @SerializedName("unitCode")
    @Expose
    @XmlElement(name = "unit-code")
    private String unitCode;
    private static final  long serialVersionUID = 714540679394975126L;



}
