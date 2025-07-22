/**
 * 
 */
package com.ey.advisory.app.data.services.itc04stocktrack;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
public class Itc04StockTrackingFinalDto {

	@Expose
	@SerializedName("resp")
	private List<Itc04StockTrackingRespDto> resp;

	@Expose
	@SerializedName("totalCnt")
	private TotalCntDto totalCnt;	

	@Expose
	@SerializedName("errMsg")
	String errMsg;
}
