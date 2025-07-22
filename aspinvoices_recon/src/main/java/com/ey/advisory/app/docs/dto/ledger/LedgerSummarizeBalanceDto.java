package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
@ToString
public class LedgerSummarizeBalanceDto {

    @Expose
    protected String gstin;
    
    @Expose
    protected String state;
    
    @Expose
    protected String status;
    
    @Expose
    @SerializedName(value = "cashigst_tot_bal")
    protected BigDecimal cashIgstTotBal = BigDecimal.ZERO;;
    
    @Expose
    @SerializedName(value = "cashcgst_tot_bal")
    protected BigDecimal cashCgstTotBal = BigDecimal.ZERO;;
    
    @Expose
    @SerializedName(value = "cashsgst_tot_bal")
    protected BigDecimal cashSgstTotBal = BigDecimal.ZERO;;
    
    @Expose
    @SerializedName(value = "cashcess_tot_bal")
    protected BigDecimal cashCessTotBal = BigDecimal.ZERO;;
    
    @Expose
    @SerializedName(value = "itccgst_totbal")
    protected BigDecimal itcCgstTotBal  = BigDecimal.ZERO;
    
    @Expose
    @SerializedName(value = "itcsgst_totbal")
    protected BigDecimal itcSgstTotBal = BigDecimal.ZERO;
    
    @Expose 
    @SerializedName(value = "itcigst_totbal")
    protected BigDecimal itcIgstTotBal = BigDecimal.ZERO;
    
    @Expose
    @SerializedName(value = "itccess_totbal")
    protected BigDecimal itcCessTotBal  = BigDecimal.ZERO;
    
    
    @Expose
    @SerializedName(value = "libigst_totbal")
    protected BigDecimal libIgstTotBal  = BigDecimal.ZERO;
    
    @Expose
    @SerializedName(value = "libcgst_totbal")
    protected BigDecimal libCgstTotBal  = BigDecimal.ZERO;
    
    @Expose
    @SerializedName(value = "libsgst_totbal")
    protected BigDecimal libSgstTotBal = BigDecimal.ZERO;;
    
    @Expose
    @SerializedName(value = "libcess_totbal")
    protected BigDecimal libCessTotBal = BigDecimal.ZERO;;
    
    @Expose
    @SerializedName(value = "lastupdated_date")
    protected String lastUpdatedDate;
	
	
}
