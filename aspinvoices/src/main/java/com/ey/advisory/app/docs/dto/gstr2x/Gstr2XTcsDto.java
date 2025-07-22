package com.ey.advisory.app.docs.dto.gstr2x;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author SriBhavya
 *
 */
@Data
public class Gstr2XTcsDto {

    @Expose
    @SerializedName("ctin")
    private String ctin;

    @Expose
    @SerializedName("pos")
    private String pos;

    @Expose
    @SerializedName("month")
    private String month;

    @Expose
    @SerializedName("chksum")
    private String chksum;

    @Expose
    @SerializedName("flag")
    private String flag;

    @Expose
    @SerializedName("remarks")
    private String remarks; 

    @Expose
    @SerializedName("comment")
    private String comment;  

    @Expose
    @SerializedName("error_msg")
    private String error_msg;

    @Expose
    @SerializedName("error_cd")
    private String error_cd;}
