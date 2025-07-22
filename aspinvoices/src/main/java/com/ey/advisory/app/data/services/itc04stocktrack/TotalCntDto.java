/**
 * 
 */
package com.ey.advisory.app.data.services.itc04stocktrack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
@AllArgsConstructor
public class TotalCntDto {
	@Expose
	@SerializedName("totmftojwcnt")
	private int totmftojwcnt;

	@Expose
	@SerializedName("totjwtomfcnt")
	private int totjwtomfcnt;

	@Expose
	@SerializedName("totjwtojwcnt")
	private int totjwtojwcnt;

	@Expose
	@SerializedName("totsoldjwcnt")
	private int totsoldjwcnt;
}
