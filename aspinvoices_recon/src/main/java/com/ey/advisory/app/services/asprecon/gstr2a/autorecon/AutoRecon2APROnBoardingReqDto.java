package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AutoRecon2APROnBoardingReqDto {

	@Expose
	@SerializedName("entityId")
	public String entityId;

	@Expose
	@SerializedName("req")
	public List<AutoRecon2APRReqList> req;
}
