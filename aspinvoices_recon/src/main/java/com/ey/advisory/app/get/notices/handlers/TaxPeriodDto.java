package com.ey.advisory.app.get.notices.handlers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.ToString;

/**
 * 
 *  @author sakshi.jain
 *
 */

@Data
@ToString
public class TaxPeriodDto {
	
	@Expose
    @SerializedName("fromMonth")
    private String fromMonth;

    @Expose
    @SerializedName("toMonth")
    private String toMonth;

    @Expose
    @SerializedName("fromYear")
    private String fromYear;

    @Expose
    @SerializedName("toYear")
    private String toYear;
    
}
