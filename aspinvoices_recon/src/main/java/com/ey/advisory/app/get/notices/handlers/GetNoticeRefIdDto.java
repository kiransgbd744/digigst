package com.ey.advisory.app.get.notices.handlers;

import java.util.List;

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
public class GetNoticeRefIdDto {
	
	@Expose
    @SerializedName("refId")
    private String refId;

    @Expose
    @SerializedName("arn")
    private String arn;

    @Expose
    @SerializedName("moduleCd")
    private String moduleCd;

    @Expose
    @SerializedName("alertCd")
    private String alertCd;

    @Expose
    @SerializedName("dateOfIssue")
    private String dateOfIssue;

    @Expose
    @SerializedName("dateOfRespond")
    private String dateOfRespond;
    
    @Expose
    @SerializedName("noticeType")
    private String noticeType;
    
    @Expose
    @SerializedName("dueDateOfReply")
    private String dueDateOfReply;
    
    @Expose
    @SerializedName("taxPeriod")
    private TaxPeriodDto taxPeriod;
    
    @Expose
    @SerializedName("maindocs")
    private List<DocumentDetailsDto> maindocs;
    
    @Expose
    @SerializedName("suppdocs")
    private List<DocumentDetailsDto> suppdocs;
}
