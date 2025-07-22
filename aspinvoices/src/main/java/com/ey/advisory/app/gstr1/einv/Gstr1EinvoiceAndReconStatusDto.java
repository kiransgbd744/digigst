package com.ey.advisory.app.gstr1.einv;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Rajesh N K
 *
 */
@Data
public class Gstr1EinvoiceAndReconStatusDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("eInvoiceStatus")
	private String eInvoiceStatus;
	
	@Expose
	@SerializedName("reconStatus")
	private String reconStatus;
	
	@Expose
	@SerializedName("einvCreatedOn")
	private String einvCreatedOn;
	
	@Expose
	@SerializedName("reconCreatedOn")
	private String reconCreatedOn;
	
	@Expose
	@SerializedName("additionalAOrPGSTR1")
	private Integer additionalAOrPGSTR1;
	
	@Expose
	@SerializedName("errorsInAOrPGSTR1NotAvl")
	private Integer errorsInAOrPGSTR1NotAvl;
	
	@Expose
	@SerializedName("errorsInAOrPGSTR1")
	private Integer errorsInAOrPGSTR1;
	
	@Expose
	@SerializedName("additionalInDigiGSTNotAvl")
	private Integer additionalInDigiGSTNotAvl;
	
	@Expose
	@SerializedName("avlInDigiGstAndAvlInAorPGSTR")
	private Integer avlInDigiGstAndAvlInAorPGSTR;
	
	@Expose
	@SerializedName("reportCategory")
	private Integer reportCategory;
	
	@Expose
	@SerializedName("reportCount")
	private Integer reportCount;
	
	@Expose
	@SerializedName("reconResponseStatus")
	private String reconResponseStatus;
	
	@Expose
	@SerializedName("reconResponseCreatedOn")
	private String reconResponseCreatedOn;
	
	@Expose
	@SerializedName("reconConfigId")
	private Long reconConfigId;
	
	@Expose
	@SerializedName("deleteCount")
	private Integer deleteCount;
	
	

}
