/**
 * 
 */
package com.ey.advisory.gstr2.initiaterecon;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EWB3WaySummaryInitiateReconLineItemDto {
	
	private String docTypes;
	private BigInteger eInvCount;
	private BigInteger eWBCount;
	private BigInteger gSTR1Count;
	
	

}
