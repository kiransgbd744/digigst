package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HsnDetail {

	@Expose
	@SerializedName("num")
	private String number;

	@Expose
	@SerializedName("hsn_sc")
	private String hsnCode;

	@Expose
	@SerializedName("desc")
	private String description;
	
	@Expose
	@SerializedName("user_desc")
	private String userDescription;

	@Expose
	@SerializedName("uqc")
	private String unitOfMeasurement;

	@Expose
	@SerializedName("qty")
	private String quantity;

	@Expose
	@SerializedName("val")
	private String totalValue;

	@Expose
	@SerializedName("txval")
	private String taxableValue;

	@Expose
	@SerializedName("iamt")
	private String igstAmount;

	@Expose
	@SerializedName("camt")
	private String cgstAmount;

	@Expose
	@SerializedName("samt")
	private String sgstAmount;

	@Expose
	@SerializedName("csamt")
	private String cessAmount;

	public String getIgstAmount() {
		return igstAmount;
	}

	public String getCessAmount() {
		return cessAmount;
	}

	public String getCgstAmount() {
		return cgstAmount;
	}

	public String getSgstAmount() {
		return sgstAmount;
	}

	public String getNumber() {
		return number;
	}

	public String getHsnCode() {
		return hsnCode;
	}

	public String getDescription() {
		return description;
	}

	public String getUserDescription() {
		return userDescription;
	}
	
	public String getUnitOfMeasurement() {
		return unitOfMeasurement;
	}

	public String getQuantity() {
		return quantity;
	}

	public String getTotalValue() {
		return totalValue;
	}

	public String getTaxableValue() {
		return taxableValue;
	}

	
	@Override
	public String toString() {
		return "HsnDetail [number=" + number + ", hsnCode=" + hsnCode
				+ ", description=" + description + ", unitOfMeasurement="
				+ unitOfMeasurement + ", quantity=" + quantity + ", totalValue="
				+ totalValue + ", taxableValue=" + taxableValue
				+ ", igstAmount=" + igstAmount + ", cgstAmount=" + cgstAmount
				+ ", sgstAmount=" + sgstAmount + ", cessAmount=" + cessAmount
				+ "]";
	}

}
