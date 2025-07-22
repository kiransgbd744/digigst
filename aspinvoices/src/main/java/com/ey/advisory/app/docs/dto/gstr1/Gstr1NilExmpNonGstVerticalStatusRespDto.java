package com.ey.advisory.app.docs.dto.gstr1;

import java.math.BigDecimal;
import java.util.List;

import com.ey.advisory.app.services.validation.b2cs.ErrorDescriptionDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr1NilExmpNonGstVerticalStatusRespDto {
    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("sNo")
    private String sNo = "";
    @Expose
    @SerializedName("gstin")
    private String gstin = "";
    @Expose
    @SerializedName("taxPeriod")
    private String taxPeriod = "";
    @Expose
    @SerializedName("hsn")
    private String hsn = "";
    @Expose
    @SerializedName("desc")
    private String desc = "";
    @Expose
    @SerializedName("uqc")
    private String uqc = "";
    @Expose
    @SerializedName("qunty")
    private String qunty = "";
    @Expose
    @SerializedName("nilInterReg")
    private BigDecimal nilInterReg = BigDecimal.ZERO;
    @Expose
    @SerializedName("nilIntraReg")
    private BigDecimal nilIntraReg = BigDecimal.ZERO;
    @Expose
    @SerializedName("nilInterUnreg")
    private BigDecimal nilInterUnreg = BigDecimal.ZERO;
    @Expose
    @SerializedName("nilIntraUnreg")
    private BigDecimal nilIntraUnreg = BigDecimal.ZERO;
    @Expose
    @SerializedName("extInterReg")
    private BigDecimal extInterReg = BigDecimal.ZERO;
    @Expose
    @SerializedName("extIntraReg")
    private BigDecimal extIntraReg = BigDecimal.ZERO;
    @Expose
    @SerializedName("extInterUnreg")
    private BigDecimal extInterUnreg = BigDecimal.ZERO;
    @Expose
    @SerializedName("extIntraUnreg")
    private BigDecimal extIntraUnreg = BigDecimal.ZERO;
    @Expose
    @SerializedName("nonInterReg")
    private BigDecimal nonInterReg = BigDecimal.ZERO;
    @Expose
    @SerializedName("nonIntraReg")
    private BigDecimal nonIntraReg = BigDecimal.ZERO;
    @Expose
    @SerializedName("nonInterUnreg")
    private BigDecimal nonInterUnreg = BigDecimal.ZERO;
    @Expose
    @SerializedName("nonIntraUnreg")
    private BigDecimal nonIntraUnreg = BigDecimal.ZERO;

    @Expose
    @SerializedName("docKey")
    private String docKey = "";
    
	private List<ErrorDescriptionDto> errorList;
}
