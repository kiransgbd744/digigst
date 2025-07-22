/**
 * 
 */
package com.ey.advisory.app.service.ims;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Invoice {

	@Expose
	@SerializedName("rtnprd")
	private String rtnprd;

	@Expose
	@SerializedName(value = "inum", alternate = ("nt_num"))
	private String inum;
}
