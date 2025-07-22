package com.ey.advisory.ewb.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RejectEwbRequestparamDto {

	@Expose
	@SerializedName("req")
	@XmlElement(name = "reject-ewb-req")
	private RejectEwbReqDto rejectEwbReqDto;
	
	@XmlElement(name = "id-token")
	private String idToken;
	
	


}


