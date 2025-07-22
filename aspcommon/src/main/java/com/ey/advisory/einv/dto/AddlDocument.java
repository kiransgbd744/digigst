/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.io.Serializable;

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
public class AddlDocument implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Supporting document URL
	 *
	 */
	@SerializedName("Url")
	@Expose
	private String url;
	/**
	 * Supporting document in Base64 Format
	 *
	 */
	@SerializedName("Docs")
	@Expose
	private String docs;
	/**
	 * Any additional information
	 *
	 */
	@SerializedName("Info")
	@Expose
	private String info;
	
	public static boolean isEmpty(AddlDocument attrDetails) {
		AddlDocument attrDtls = new AddlDocument();
		return attrDetails.hashCode() == attrDtls.hashCode();
	}


}
