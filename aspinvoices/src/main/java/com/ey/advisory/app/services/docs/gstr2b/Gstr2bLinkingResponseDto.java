/**
 * 
 */
package com.ey.advisory.app.services.docs.gstr2b;

import java.util.List;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */
@Data
public class Gstr2bLinkingResponseDto {

	private String gstin;
	
	private String stateName;
	
	private String authToken;

	List<Gstr2bLinkingRespDto> item;

}
