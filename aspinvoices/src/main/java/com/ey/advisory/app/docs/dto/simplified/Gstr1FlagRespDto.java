package com.ey.advisory.app.docs.dto.simplified;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@Data
public class Gstr1FlagRespDto {

	@Expose
	@SerializedName("isNilUserInput")
	private Boolean isNilUserInput;
	@Expose
	@SerializedName("isHsnUserInput")
	private Boolean isHsnUserInput;
	
	
}
