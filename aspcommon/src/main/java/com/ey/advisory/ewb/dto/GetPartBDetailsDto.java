/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Khalid1.Khan
 *
 */
@Getter
@Setter
public class GetPartBDetailsDto extends UpdatePartBEwbRequestDto implements
Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	private String transporterId;
	
	@Expose
	private String transporterName;

}
