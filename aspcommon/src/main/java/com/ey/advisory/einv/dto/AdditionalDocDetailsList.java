/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Data
public class AdditionalDocDetailsList {
	
	@SerializedName("AddlDocDtls")
	@Expose
	private List<AddlDocument> addlDocDtls;

}
