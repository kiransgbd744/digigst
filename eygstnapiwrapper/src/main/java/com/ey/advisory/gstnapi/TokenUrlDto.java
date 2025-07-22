/**
 * 
 */
package com.ey.advisory.gstnapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Data
public class TokenUrlDto {

	@Expose
	@SerializedName("ul")
	private String url;

	@Expose
	@SerializedName("ic")
	private String ic;

	@Expose
	@SerializedName("hash")
	private String hash;
}
