/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class AnxDataStatusReqDto {

	@Expose
	private String dataType;

	@Expose
	private LocalDate dataRecvFrom;

	@Expose
	private LocalDate dataRecvTo;
	
//	@Expose
//	private List<String> gstins;
	
	@Expose
	private String gstin;
	
}
