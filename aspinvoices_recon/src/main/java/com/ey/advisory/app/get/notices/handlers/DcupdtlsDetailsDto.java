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
public class DcupdtlsDetailsDto {
    @Expose
    @SerializedName("ct")
    private String contentType;

    @Expose
    @SerializedName("docName")
    private String docName;

    @Expose
    @SerializedName("hash")
    private String hash;

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("ty")
    private String type;
}
