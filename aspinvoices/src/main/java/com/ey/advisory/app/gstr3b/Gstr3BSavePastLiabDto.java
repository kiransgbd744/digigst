/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */

@Data
public class Gstr3BSavePastLiabDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("ret_period")
	private String retPeriod;

	@Expose
	@SerializedName("breakup")
	private List<Gstr3BSavePastLibBrkUpDto> breakUp;

}
