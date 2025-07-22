/**
 * 
 */
package com.ey.advisory.app.services.gstr3b.qtr.filing.apipush;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
public class Gstr3bQtrFilingReqDtoList {

	@Expose
	@SerializedName("req")
	private List<Gstr3bQtrFilingReqDto> reqDtoList;

}
