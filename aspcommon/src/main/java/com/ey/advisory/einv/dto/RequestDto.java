
package com.ey.advisory.einv.dto;

import java.io.Serializable;

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
public class RequestDto implements Serializable {


	private static final long serialVersionUID = 1L;

	@SerializedName("gstin")
	@Expose
	private String gstin;

	@SerializedName("irnNo")
	@Expose
	@XmlElement(name = "irnNo")
	private String irnNo;

	@SerializedName("source")
	@Expose
	@XmlElement(name = "source")
	private String source;

	public RequestDto(String irnNo, String gstin, String source) {
	        this.irnNo = irnNo;
	        this.gstin = gstin;
	        this.source = source;
	    }	

}


