package com.ey.advisory.app.docs.dto.gstr7;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author SriBhavya
 *
 */
@Data
public class Gstr7ErrorReport {
	@Expose
	@SerializedName("tds")
	private List<Gstr7TdsDto> tdsInvoice;

	@Expose
	@SerializedName("tdsa")
	private List<Gstr7TdsaDto> tdsaInvoice;
}
