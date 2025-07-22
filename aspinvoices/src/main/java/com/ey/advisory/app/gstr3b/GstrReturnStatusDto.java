/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class GstrReturnStatusDto implements Serializable{


	private static final long serialVersionUID = 1L;
	
	@Expose
	protected String gstin;

	@Expose
	protected String taxPeriod;
		
	@Expose
	protected String status;
		
	@Expose
	protected LocalDateTime updatedOn;

	@Expose
	protected LocalDate filedDate;

}
