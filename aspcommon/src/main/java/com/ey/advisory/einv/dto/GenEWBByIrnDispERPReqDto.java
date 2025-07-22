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
public class GenEWBByIrnDispERPReqDto implements Serializable {

	/**
	 * @author Siva.Reddy
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("addr1")
	@XmlElement(name = "addr1")
	private String addr1;

	@Expose
	@SerializedName("addr2")
	@XmlElement(name = "addr2")
	private String addr2;

	@Expose
	@SerializedName("loc")
	@XmlElement(name = "loc")
	private String loc;

	@Expose
	@SerializedName("pin")
	@XmlElement(name = "pin")
	private Integer pin;

	@Expose
	@SerializedName("stcd")
	@XmlElement(name = "stcd")
	private String stcd;
	
	@Expose
	@SerializedName("nm")
	@XmlElement(name = "nm")
	private String nm;
}
