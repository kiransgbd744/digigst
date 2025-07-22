/**
 * 
 */
package com.ey.advisory.app.common;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 *
 * @author Sakshi.jain
 */
@Component
@Data
public class GetCallErrorMessageDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("section")
	private String section;
	
	@Expose
	@SerializedName("errMsg")
	private String errMsg;

}
