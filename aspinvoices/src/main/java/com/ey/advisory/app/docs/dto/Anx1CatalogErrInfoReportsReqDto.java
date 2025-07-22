/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Laxmi.Salukuti
 *
 */
@Setter
@Getter
@ToString
public class Anx1CatalogErrInfoReportsReqDto extends SearchCriteria{

	public Anx1CatalogErrInfoReportsReqDto() {
		super(SearchTypeConstants.REPORTS_SEARCH);
	}

	@Expose
	@SerializedName("type")
	private String type;
	
	@Expose
	@SerializedName("dataType")
	private String dataType;

}
