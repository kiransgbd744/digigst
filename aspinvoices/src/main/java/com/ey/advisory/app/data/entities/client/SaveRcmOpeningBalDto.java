package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaveRcmOpeningBalDto {

	
    @Expose
    @SerializedName(value = "entityId")
    private Long entityId;

    @Expose
    @SerializedName(value = "igst")
    private BigDecimal igst;

    @Expose
    @SerializedName(value = "cgst")
    private BigDecimal cgst;

    @Expose
    @SerializedName(value = "sgst")
    private BigDecimal sgst;

    @Expose
    @SerializedName(value = "cess")
    private BigDecimal cess;

    @Expose
    @SerializedName(value = "gstin")
    private String gstin;

    @Expose
    @SerializedName(value = "isAmended")
    private String isAmended;

    @Expose
    @SerializedName(value = "ledgerType")
    private String ledgerType;
    
    @Expose
    @SerializedName(value = "saveStatus")
    private String saveStatus;
    
    
    @Expose
    @SerializedName(value = "errMsg")
    private String errMsg;
    
    @Expose
    @SerializedName(value = "updatedOn")
    private String updatedOn;
    


}