package com.ey.advisory.ewb.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RejectEwbReqDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Expose
	@SerializedName("ewbNo")
	private String ewbNo;
	
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("docHeaderId")
	@XmlTransient
	private Long docHeaderId;

}
 