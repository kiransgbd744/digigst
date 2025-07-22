/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class TransactionalReportingDto {

	@Expose
	@SerializedName("dateofRep")
	private LocalDate dateOfReporting;
	
	@Expose
	@SerializedName("resp")
	private List<TransactionalDto> data;
	
	
}
