package com.ey.advisory.app.docs.dto.gstr6;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6LateOffSetDetails {

	@Expose
	@SerializedName("liab_id")
	private Long liabId;

	@Expose
	@SerializedName("tran_cd")
	private Long tranCd;

}
