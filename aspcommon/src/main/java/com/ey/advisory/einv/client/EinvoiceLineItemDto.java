
package com.ey.advisory.einv.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EinvoiceLineItemDto {

    @SerializedName("linenumber")
    @Expose
    private Integer linenumber;
    @SerializedName("supplytype")
    @Expose
    private String supplytype;
    @SerializedName("productname")
    @Expose
    private String productname;
    @SerializedName("productdescription")
    @Expose
    private String productdescription;
    @SerializedName("salesorganisation")
    @Expose
    private String salesorganisation;
    @SerializedName("distributionchannel")
    @Expose
    private String distributionchannel;
    @SerializedName("hsn")
    @Expose
    private String hsn;
    @SerializedName("unitofmeasurement")
    @Expose
    private String unitofmeasurement;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("unitprice")
    @Expose
    private BigDecimal unitprice;
    @SerializedName("itemamount")
    @Expose
    private BigDecimal itemamount;
    @SerializedName("itemassessableamount")
    @Expose
    private BigDecimal itemassessableamount;
    @SerializedName("itemothercharges")
    @Expose
    private BigDecimal itemothercharges;
    @SerializedName("igstrate")
    @Expose
    private BigDecimal igstrate;
    @SerializedName("igstamount")
    @Expose
    private BigDecimal igstamount;
    @SerializedName("cgstrate")
    @Expose
    private BigDecimal cgstrate;
    @SerializedName("cgstamount")
    @Expose
    private BigDecimal cgstamount;
    @SerializedName("sgstrate")
    @Expose
    private BigDecimal sgstrate;
    @SerializedName("sgstamount")
    @Expose
    private BigDecimal sgstamount;
    @SerializedName("cessadvaloremrate")
    @Expose
    private BigDecimal cessadvaloremrate;
    @SerializedName("cessadvaloremamt")
    @Expose
    private BigDecimal cessadvaloremamt;
    @SerializedName("cessspecificrate")
    @Expose
    private BigDecimal cessspecificrate;
    @SerializedName("cessspecificamt")
    @Expose
    private BigDecimal cessspecificamt;
    @SerializedName("statecessrate")
    @Expose
    private BigDecimal statecessrate;
    @SerializedName("statecessamount")
    @Expose
    private BigDecimal statecessamount;
    @SerializedName("totalitemamount")
    @Expose
    private BigDecimal totalitemamount;
    @SerializedName("invoicevalue")
    @Expose
    private BigDecimal invoicevalue;
    @SerializedName("oridocno")
    @Expose
    private String oridocno;
    @SerializedName("oridocdate")
    @Expose
    private LocalDate oridocdate;
    @SerializedName("originalinvoiceno")
    @Expose
    private String originalinvoiceno;
    @SerializedName("originalinvoicedt")
    @Expose
    private LocalDate originalinvoicedt;
    @SerializedName("preceedinginvoiceno")
    @Expose
    private String preceedinginvoiceno;
    @SerializedName("preceedinginvoicedt")
    @Expose
    private LocalDate preceedinginvoicedt;
    @SerializedName("productcode")
    @Expose
    private Integer productcode;
    @SerializedName("catofproduct")
    @Expose
    private String catofproduct;
    @SerializedName("itcflag")
    @Expose
    private String itcflag;
    @SerializedName("fob")
    @Expose
    private String fob;
    @SerializedName("exportduty")
    @Expose
    private String exportduty;
    @SerializedName("tcsigstamount")
    @Expose
    private BigDecimal tcsigstamount;
    @SerializedName("reasoncreditdebitnote")
    @Expose
    private String reasoncreditdebitnote;
    @SerializedName("profitcentre1")
    @Expose
    private String profitcentre1;
    @SerializedName("plantcode")
    @Expose
    private String plantcode;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("profitcentre3")
    @Expose
    private String profitcentre3;
    @SerializedName("profitcentre4")
    @Expose
    private String profitcentre4;
    @SerializedName("profitcentre5")
    @Expose
    private String profitcentre5;
    @SerializedName("profitcentre6")
    @Expose
    private String profitcentre6;
    @SerializedName("profitcentre7")
    @Expose
    private String profitcentre7;
    @SerializedName("profitcentre8")
    @Expose
    private String profitcentre8;
    @SerializedName("glassessablevalue")
    @Expose
    private BigDecimal glassessablevalue;
    @SerializedName("glcodeigst")
    @Expose
    private BigDecimal glcodeigst;
    @SerializedName("glcodecgst")
    @Expose
    private BigDecimal glcodecgst;
    @SerializedName("glcodesgst")
    @Expose
    private BigDecimal glcodesgst;
    @SerializedName("glcodeadvcess")
    @Expose
    private BigDecimal glcodeadvcess;
    @SerializedName("glcodespecess")
    @Expose
    private BigDecimal glcodespecess;
    @SerializedName("glcodestatcess")
    @Expose
    private BigDecimal glcodestatcess;
    @SerializedName("eligibilityindicator")
    @Expose
    private String eligibilityindicator;
    @SerializedName("availableigst")
    @Expose
    private BigDecimal availableigst;
    @SerializedName("availablecgst")
    @Expose
    private BigDecimal availablecgst;
    @SerializedName("availablesgst")
    @Expose
    private BigDecimal availablesgst;
    @SerializedName("availablecess")
    @Expose
    private BigDecimal availablecess;
    @SerializedName("userdefinedfield1")
    @Expose
    private String userdefinedfield1;
    @SerializedName("userdefinedfield2")
    @Expose
    private String userdefinedfield2;
    @SerializedName("userdefinedfield3")
    @Expose
    private String userdefinedfield3;
    @SerializedName("userdefinedfield4")
    @Expose
    private String userdefinedfield4;
    @SerializedName("userdefinedfield5")
    @Expose
    private String userdefinedfield5;
    @SerializedName("userdefinedfield6")
    @Expose
    private String userdefinedfield6;
    @SerializedName("userdefinedfield7")
    @Expose
    private String userdefinedfield7;
    @SerializedName("userdefinedfield8")
    @Expose
    private String userdefinedfield8;
    @SerializedName("userdefinedfield9")
    @Expose
    private String userdefinedfield9;
    @SerializedName("userdefinedfield10")
    @Expose
    private String userdefinedfield10;
    @SerializedName("userdefinedfield11")
    @Expose
    private String userdefinedfield11;
    @SerializedName("userdefinedfield12")
    @Expose
    private String userdefinedfield12;
    @SerializedName("userdefinedfield13")
    @Expose
    private String userdefinedfield13;
    @SerializedName("userdefinedfield14")
    @Expose
    private String userdefinedfield14;
    @SerializedName("userdefinedfield15")
    @Expose
    private String userdefinedfield15;

}
