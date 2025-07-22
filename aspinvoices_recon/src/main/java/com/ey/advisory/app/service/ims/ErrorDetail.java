/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ErrorDetail {

	@Expose
	@SerializedName("stin")
	private String stin;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;

	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("inv")
	private List<Invoice> inv;

}
