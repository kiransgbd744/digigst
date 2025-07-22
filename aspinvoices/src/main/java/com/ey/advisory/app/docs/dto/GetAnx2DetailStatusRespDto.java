package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.core.dto.GetAnx2DetailsStatusItemsDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAnx2DetailStatusRespDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("items")
	private List<GetAnx2DetailsStatusItemsDto> items = new ArrayList<GetAnx2DetailsStatusItemsDto>();

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public List<GetAnx2DetailsStatusItemsDto> getItems() {
		return items;
	}

	public void setItems(List<GetAnx2DetailsStatusItemsDto> items) {
		this.items = items;
	}

}
