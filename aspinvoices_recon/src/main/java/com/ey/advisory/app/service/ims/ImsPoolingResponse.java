/**
 * 
 */
package com.ey.advisory.app.service.ims;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vsihal.verma
 *
 */

@Data
public class ImsPoolingResponse {

	@Expose
	@SerializedName("status_cd")
	private String status_cd;

	@Expose
	@SerializedName("transTypCd")
	private String transTypCd;

	@Expose
	@SerializedName("proc_cnt")
	private String proc_cnt;

	@Expose
	@SerializedName("err_cnt")
	private String err_cnt;

	@Expose
	@SerializedName("err_cd")
	private String err_cd;

	@Expose
	@SerializedName("err_msg")
	private String err_msg;

	@Expose
	@SerializedName("error_report")
	private ImsErrorReport error_report;

}
