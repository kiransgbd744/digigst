package com.ey.advisory.gstr9.jsontocsv.converters.gstr2X;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Tds {
	
	@SerializedName("accepted")
	@Expose
	AcceptorRejectDto accepted;
	
	@SerializedName("rejected")
	@Expose
	AcceptorRejectDto rejected;
}
