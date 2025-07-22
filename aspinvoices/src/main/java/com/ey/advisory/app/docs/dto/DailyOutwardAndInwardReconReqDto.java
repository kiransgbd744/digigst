/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class DailyOutwardAndInwardReconReqDto {

	@Expose
	@SerializedName("accVoucherDateFrom")
	private LocalDate accVoucherDateFrom;

	@Expose
	@SerializedName("accVoucherDateTo")
	private LocalDate accVoucherDateTo;
	
	@Expose
	@SerializedName("extractionDateFrom")
	private LocalDate extractionDateFrom;

	@Expose
	@SerializedName("extractionDateTo")
	private LocalDate extractionDateTo;

}
