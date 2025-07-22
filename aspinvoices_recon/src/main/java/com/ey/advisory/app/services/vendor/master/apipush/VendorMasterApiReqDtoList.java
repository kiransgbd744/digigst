/**
 *
 */
package com.ey.advisory.app.services.vendor.master.apipush;

import com.ey.advisory.app.itcmatching.vendorupload.VendorAPIPushListDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
public class VendorMasterApiReqDtoList {

	@Expose
	@SerializedName("req")
	private List<VendorAPIPushListDto> reqDtoList;

}
