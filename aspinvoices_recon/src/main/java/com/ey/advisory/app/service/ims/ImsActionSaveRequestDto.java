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
public class ImsActionSaveRequestDto {
	
	@Expose
    @SerializedName("rtin")
    private String rtin;
	
	@Expose
    @SerializedName("reqtyp")
    private String reqtyp;
	
	@Expose
    @SerializedName("invdata")
    private ImsInvData invdata;
	
}
