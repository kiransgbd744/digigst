package com.ey.advisory.app.itcmatching.vendorupload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Rajesh N K
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class VendorGstinDto {

	@Expose
	@SerializedName("vendorGstin")
	private String vendorGstin;

	@Expose
	@SerializedName("vendorName")
	private String vendorName;

	@Expose
	@SerializedName("vendorPan")
	private String vendorPan;

	@Expose
	@SerializedName("vendorCode")
	private String vendorCode;

}
