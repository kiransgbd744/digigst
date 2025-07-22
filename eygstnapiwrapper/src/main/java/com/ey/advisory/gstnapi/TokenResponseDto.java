/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Data
public class TokenResponseDto {
	@Expose
	@SerializedName("fc")
	private String fc;
	
	@Expose
	@SerializedName("ek")
	private String ek;

	@Expose
	@SerializedName("urls")
	private List<TokenUrlDto> urlList;

}
