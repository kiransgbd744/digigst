/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.io.Serializable;

import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSecDetailsDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */

@Data
public class Gstr3BSavePastLibBrkUpDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;

	@Expose
	@SerializedName("liability")
	private Gstr3BSecDetailsDTO liability;

}
