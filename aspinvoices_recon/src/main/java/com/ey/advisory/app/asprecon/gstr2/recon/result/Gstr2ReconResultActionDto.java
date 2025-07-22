/**
 * 
 */
package com.ey.advisory.app.asprecon.gstr2.recon.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Gstr2ReconResultActionDto {

	private Long a2Id;
	
	private Long prId;
	
	private String actionTaken;
	
	private Long reconLinkId;
}
