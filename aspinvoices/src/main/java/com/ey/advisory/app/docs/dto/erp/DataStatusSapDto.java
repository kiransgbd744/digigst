/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Hemasundar.J
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class DataStatusSapDto {

	@Expose
	private String gstin;
	
	@Expose
	private Long erpId;

	@Expose
	protected String dataType;

	@Expose
	private LocalDate receivedDate;

	@Expose
	private String returnPeriod;

	@Expose
	private Long sapTotal;

}
