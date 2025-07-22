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
public class Gstr6AmendElglstDto {

	@Expose
	@SerializedName("typ")
	private String typ;

	@Expose
	@SerializedName("cpty")
	private String cpty;

	@Expose
	@SerializedName("rcpty")
	private String rcpty;

	@Expose
	@SerializedName("statecd")
	private String statecd;

	@Expose
	@SerializedName("rstatecd")
	private String rstatecd;

	@Expose
	@SerializedName("doclst")
	private List<Gstr6AmendDocListItems> doclst;

}
