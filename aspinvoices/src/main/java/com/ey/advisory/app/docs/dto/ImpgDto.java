package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class ImpgDto {

	@Expose
	@SerializedName("impg")
	private List<ImpgItemDto> impgItem;
	
	@Expose
	@SerializedName("impgsez")
	private List<ImpgSezItemDto> impgSezItem;

}
