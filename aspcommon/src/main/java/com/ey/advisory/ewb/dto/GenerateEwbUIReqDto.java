/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */

@Data
public class GenerateEwbUIReqDto {
	
	@Expose
	@SerializedName("docIdList")
	private List<Long> docIdList;
	

}
