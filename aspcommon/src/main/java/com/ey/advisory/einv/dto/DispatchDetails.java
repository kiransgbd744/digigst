
package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class DispatchDetails implements Serializable {
	private static final long serialVersionUID = 3667449835974724319L;

	/**
	 * Name of the company from which the goods are dispatched
	 *
	 */
	@SerializedName("Nm")
	@Expose
	public String nm;
	/**
	 * Address 1 of the entity from which goods are dispatched.(Building/Flat
	 * no.Road/Street etc.)
	 *
	 */
	@SerializedName("Addr1")
	@Expose
	public String addr1;
	/**
	 * Address 2 of the entity from which goods are dispatched. (Floor no., Name
	 * of the premises/building)
	 *
	 */
	@SerializedName("Addr2")
	@Expose
	public String addr2;
	/**
	 * Location
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
	 * State Code
	 *
	 */
	@SerializedName("Stcd")
	@Expose
	public String stcd;

	public static boolean isEmpty(DispatchDetails dispdetails) {
		DispatchDetails dispatchDetails = new DispatchDetails();
		return dispatchDetails.hashCode() == dispdetails.hashCode();
	}

}
