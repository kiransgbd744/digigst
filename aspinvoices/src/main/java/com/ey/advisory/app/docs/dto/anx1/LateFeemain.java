package com.ey.advisory.app.docs.dto.anx1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Nandam
 *
 */
@Data
public class LateFeemain {

	@Expose
	@SerializedName("lateFee")
	private LateFee lateFee;
}
