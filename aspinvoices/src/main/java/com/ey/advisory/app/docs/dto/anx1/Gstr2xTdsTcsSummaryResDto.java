package com.ey.advisory.app.docs.dto.anx1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Data
public class Gstr2xTdsTcsSummaryResDto {

	@Expose
	@SerializedName("accepted")
	private Gstr2xAcceptAndrejectResDto accepted = new Gstr2xAcceptAndrejectResDto();

	@Expose
	@SerializedName("rejected")
	private Gstr2xAcceptAndrejectResDto rejected = new Gstr2xAcceptAndrejectResDto();

}
