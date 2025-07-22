package com.ey.advisory.app.data.services.qrcodevalidator;

import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QRSearchParams {

	@Expose
	@SerializedName("recipientGstins")
	private List<String> recipientGstins;

	@Expose
	@SerializedName("vendorGstin")
	private List<String> vendorGstin;

	@Expose
	@SerializedName("validatedFrom")
	private LocalDate validatedFrom;

	@Expose
	@SerializedName("validatedTo")
	private LocalDate validatedTo;

	@Expose
	@SerializedName("recordStatus")
	private List<String> recordStatus;
	
	@Expose
	@SerializedName("entityId")
	private String entityId;
	
	public String getEntityId() {
	    return entityId;
	}

	public void setEntityId(String entityId) {
	    this.entityId = entityId;
	}

	public List<String> getRecipientGstins() {
		return recipientGstins;
	}

	public void setRecipientGstins(List<String> recipientGstins) {
		this.recipientGstins = recipientGstins;
	}

	public List<String> getVendorGstin() {
		return vendorGstin;
	}

	public void setVendorGstin(List<String> vendorGstin) {
		this.vendorGstin = vendorGstin;
	}

	public LocalDate getValidatedFrom() {
		return validatedFrom;
	}

	public void setValidatedFrom(LocalDate validatedFrom) {
		this.validatedFrom = validatedFrom;
	}

	public LocalDate getValidatedTo() {
		return validatedTo;
	}

	public void setValidatedTo(LocalDate validatedTo) {
		this.validatedTo = validatedTo;
	}

	public List<String> getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(List<String> recordStatus) {
		this.recordStatus = recordStatus;
	}

	@Override
	public String toString() {
		return "QRSearchParams [recipientGstins=" + recipientGstins + ", vendorGstin=" + vendorGstin
				+ ", validatedFrom=" + validatedFrom + ", validatedTo=" + validatedTo + ", recordStatus=" + recordStatus
				+ ", entityId=" + entityId + "]";
	}

}
