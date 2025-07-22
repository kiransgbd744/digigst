package com.ey.advisory.app.get.notices.handlers;

/**
 * 
 *  @author sakshi.jain
 *
 */


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DocumentDetailsDto {
	
	@Expose
	@SerializedName("dcupdtls")
	private List<DcupdtlsDetailsDto> dcupdtls;
}

