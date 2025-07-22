
package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ShippingDetails implements Serializable {

	private static final long serialVersionUID = -6310798126212688881L;
	/**
	 * GSTIN of entity to whom goods are shipped
	 *
	 */
	@SerializedName("Gstin")
	@Expose
	public String gstin;
	/**
	 * Legal Name
	 *
	 */
	@SerializedName("LglNm")
	@Expose
	public String lglNm;
	/**
	 * Trade Name
	 *
	 */
	@SerializedName("TrdNm")
	@Expose
	public String trdNm;
	/**
	 * Address1 of the entity to whom the supplies are shipped to.
	 * (Building/Flat no., Road/Street etc.)
	 *
	 */
	@SerializedName("Addr1")
	@Expose
	public String addr1;
	/**
	 * Address 2 of the entity to whom the supplies are shipped to. (Floor no.,
	 * Name of the premises/building).
	 *
	 */
	@SerializedName("Addr2")
	@Expose
	public String addr2;
	/**
	 * Place (City,Town,Village) entity to whom the supplies are shipped to.
	 *
	 */
	@SerializedName("Loc")
	@Expose
	public String loc;
	/**
	 * Pincode
	 *
	 */
	@SerializedName("Pin")
	@Expose
	public Integer pin;
	/**
	 * State Code to which supplies are shipped to.
	 *
	 */
	@SerializedName("Stcd")
	@Expose
	public String stcd;

	public static boolean isEmpty(ShippingDetails shipDtls) {
		ShippingDetails shipDetails = new ShippingDetails();
		return shipDtls.hashCode() == shipDetails.hashCode();
	}

}
