package com.ey.advisory.app.azure;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Saif.S
 *
 */
@Data
@AllArgsConstructor
public class GstinsDto {

	@Expose
	@SerializedName("gstins")
	private List<String> gstins;
}
