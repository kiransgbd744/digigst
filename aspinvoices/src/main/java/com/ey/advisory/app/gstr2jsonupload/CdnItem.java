/**
 * 
 */
package com.ey.advisory.app.gstr2jsonupload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CdnItem {
	
	@SerializedName("num")
	@Expose
	public Integer num;
	
	@SerializedName("itm_det")
	@Expose
	public CdnItemDetails itmDet;


}
