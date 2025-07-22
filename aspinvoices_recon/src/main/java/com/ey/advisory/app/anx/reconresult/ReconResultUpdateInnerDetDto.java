/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.math.BigInteger;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Nikhil.Duseja
 *
 */

@ToString
@Getter
@Setter
public class ReconResultUpdateInnerDetDto {
	
	@Expose
	private BigInteger reconLinkId;
	
	@Expose
	private String prkey;
	
	@Expose
	private String a2key;
	
	@Expose
	private String gstin;
	
	
}
