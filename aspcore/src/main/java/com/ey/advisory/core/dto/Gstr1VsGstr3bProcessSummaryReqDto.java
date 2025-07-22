package com.ey.advisory.core.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class Gstr1VsGstr3bProcessSummaryReqDto {

    @Expose
    @SerializedName("entityId")
    private List<Long> entityId = new ArrayList<>();

    @Expose
    @SerializedName("taxPeriodFrom")
    private String taxPeriodFrom;

    @Expose
    @SerializedName("taxPeriodTo")
    private String taxPeriodTo;

    @Expose
    @SerializedName("dataSecAttrs")
    private Map<String, List<String>> dataSecAttrs = new HashMap<>();

    @Expose
    @SerializedName("type")
    private String type;
    
    private Long configId;
    
    @Expose
    @SerializedName("reconType")
    private String reconType;

}
