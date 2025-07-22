package com.ey.advisory.app.docs.dto.gstr6;

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
public class Gstr6AmenIsdDetailsDto {

	@Expose
	@SerializedName("elglst")
	private List<Gstr6AmendElglstDto> elglst;

	@Expose
	@SerializedName("inelglst")
	private List<Gstr6AmendElglstDto> inelglst;

	

}
