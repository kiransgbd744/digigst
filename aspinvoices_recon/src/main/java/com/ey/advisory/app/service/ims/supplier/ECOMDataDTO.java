/**
 * 
 */
package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ECOMDataDTO {

	@Expose
	@SerializedName("b2b")
	private List<B2BDataDTO> b2b;

	@Expose
	@SerializedName("urp2b")
	private List<B2BDataDTO> urp2b;

}

