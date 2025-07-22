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
public class ImsPoolingErrorReportDto {

	@Expose
	@SerializedName("b2b" )
	private List<ErrorDetail> b2bErrDtl;
}
