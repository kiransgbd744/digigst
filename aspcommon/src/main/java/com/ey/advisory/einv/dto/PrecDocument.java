/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Khalid1.Khan
 *
 */
@Data
@EqualsAndHashCode
public class PrecDocument implements Serializable{
	
	private final static long serialVersionUID = 8686892113489871212L;

	
	/**
	* Reference of original invoice, if any.
	* (Required)
	*
	*/
	@SerializedName("InvNo")
	@Expose
	private String invNo;
	/**
	* Date of preceding invoice
	* (Required)
	*
	*/
	@SerializedName("InvDt")
	@Expose
	private LocalDate invDt;
	/**
	* Other Reference
	*
	*/
	@SerializedName("OthRefNo")
	@Expose
	private String othRefNo;

	public static boolean isEmpty(PrecDocument preDocDtls) {
		PrecDocument attrDtls = new PrecDocument();
		return preDocDtls.hashCode() == attrDtls.hashCode();
	}

}
