/**
 * 
 */
package com.ey.advisory.ewb.reverseinteg;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Khalid.Khan
 *
 */
@Getter
@Setter
@ToString
public class ReverseIntegParamsDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("localDate")
	private LocalDate localDate;
	
	@Expose
	@SerializedName("scenarioId")
	private Long scenarioId;

}
