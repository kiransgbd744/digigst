package com.ey.advisory.ewb.dto;

import java.io.Serializable;

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
public class ChangeMultiVehicleDetaiilsReqDto implements Serializable
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
     * Group Number
     * (Required)
     * 
     */
    @SerializedName("groupNo")
    @Expose
    @XmlElement(name = "group-no")
    private String groupNo;
    /**
     * Old Vehicle Number
     * (Required)
     * 
     */
    @SerializedName("oldvehicleNo")
    @Expose
    @XmlElement(name = "old-veh-no")
    private String oldvehicleNo;
    /**
     * New Vehicle Number
     * (Required)
     * 
     */
    @SerializedName("newVehicleNo")
    @Expose
    @XmlElement(name = "new-veh-no")
    private String newVehicleNo;
    /**
     * Old Tran Number
     * (Required)
     * 
     */
    @SerializedName("oldTranNo")
    @Expose
    @XmlElement(name = "old-tran-no")
    private String oldTranNo;
    /**
     * New Tran Number
     * (Required)
     * 
     */
    @SerializedName("newTranNo")
    @Expose
    @XmlElement(name = "new-tran-no")
    private String newTranNo;
    /**
     * From Place
     * (Required)
     * 
     */
    @SerializedName("fromPlace")
    @Expose
    @XmlElement(name = "from-place")
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
     * Reason Code
     * (Required)
     * 
     */
    @SerializedName("reasonCode")
    @Expose
    @XmlElement(name = "reason-code")
    private String reasonCode;
    /**
     * Remarks
     * (Required)
     * 
     */
    @SerializedName("reasonRem")
    @Expose
    @XmlElement(name = "reason-rem")
    private String reasonRem;
    private static final  long serialVersionUID = 7311066787734827898L;

}
