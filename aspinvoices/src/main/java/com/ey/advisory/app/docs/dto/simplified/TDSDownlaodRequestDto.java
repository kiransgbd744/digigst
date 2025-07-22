package com.ey.advisory.app.docs.dto.simplified;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class TDSDownlaodRequestDto {

    @Expose
    @SerializedName("entityId")
    private List<Long> entityId = new ArrayList<>();

    @Expose
    @SerializedName("taxPeriod")
    private String taxPeriod;

    @Expose
    @SerializedName("gstin")
    private List<String> gstin = new ArrayList<>();

    @Expose
    @SerializedName("type")
    private String type;

}
