/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Data
public class Attribute implements Serializable {
	
	private static final long serialVersionUID = -4774096708482074389L;


	/**
	 * Attribute details of the item
	 *
	 */
	@SerializedName("Nm")
	@Expose
	private String nm;
	/**
	 * Attribute value of the item
	 *
	 */
	@SerializedName("Val")
	@Expose
	private String val;
	
	public static boolean isEmpty(Attribute attrDetails) {
		Attribute attrDtls = new Attribute();
		return attrDetails.hashCode() == attrDtls.hashCode();
	}
}
