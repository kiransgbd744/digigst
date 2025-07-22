package com.ey.advisory.app.itcmatching.vendorupload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author kiran s
 *
 */
@Getter
@Setter
@ToString
public class GstinDetailsdto {
	
	@Expose
	@SerializedName(value = "gstin")
	private String gstin;

}

