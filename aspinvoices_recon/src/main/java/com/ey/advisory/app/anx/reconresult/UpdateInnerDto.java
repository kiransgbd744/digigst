/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.math.BigInteger;

import com.google.gson.annotations.Expose;

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
public class UpdateInnerDto {
	
	@Expose
	private BigInteger a2ReconLinkId;
	
	@Expose
	private BigInteger prReconLinkId;

}
