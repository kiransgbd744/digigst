package com.ey.advisory.app.docs.dto.ret;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class RetIntAlertDto {

	@Expose
	@SerializedName("latefilepmt08")
	private Boolean latefilepmt08;

	@Expose
	@SerializedName("lateretfile")
	private Boolean lateretfile;

	@Expose
	@SerializedName("laterepdoc")
	private Boolean laterepdoc;

	@Expose
	@SerializedName("rejdoc")
	private Boolean rejdoc;

	@Expose
	@SerializedName("amenddoc")
	private Boolean amenddoc;

	
}
