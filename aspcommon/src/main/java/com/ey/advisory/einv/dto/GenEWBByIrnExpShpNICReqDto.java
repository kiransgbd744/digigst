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
public class GenEWBByIrnExpShpNICReqDto implements Serializable {

	/**
	 * @author Siva.Reddy
	 *
	 */
	private static final long serialVersionUID = 1L;


	@Expose
	@SerializedName("Addr1")
	@XmlElement(name = "addr1")
	private String addr1;

	@Expose
	@SerializedName("Addr2")
	@XmlElement(name = "addr2")
	private String addr2;

	@Expose
	@SerializedName("Loc")
	@XmlElement(name = "loc")
	private String loc;

	@Expose
	@SerializedName("Pin")
	@XmlElement(name = "pin")
	private Integer pin;

	@Expose
	@SerializedName("Stcd")
	@XmlElement(name = "stcd")
	private String stcd;
	
	public static boolean isEmpty(GenEWBByIrnExpShpNICReqDto shpDto){
        GenEWBByIrnExpShpNICReqDto defaultDto = new  GenEWBByIrnExpShpNICReqDto();
        return defaultDto.hashCode() == shpDto.hashCode();
    }
}
