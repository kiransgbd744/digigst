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
public class GenEWBByIrnDispNICReqDto implements Serializable {

	/**
	 * @author Siva.Reddy
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("Addr1")
	@XmlElement(name = "Addr1")
	private String addr1;

	@Expose
	@SerializedName("Addr2")
	@XmlElement(name = "Addr2")
	private String addr2;

	@Expose
	@SerializedName("Loc")
	@XmlElement(name = "Loc")
	private String loc;

	@Expose
	@SerializedName("Pin")
	@XmlElement(name = "Pin")
	private Integer pin;

	@Expose
	@SerializedName("Stcd")
	@XmlElement(name = "Stcd")
	private String stcd;
	
	@Expose
	@SerializedName("Nm")
	@XmlElement(name = "Nm")
	private String nm;
	
	public static boolean isEmpty(GenEWBByIrnDispNICReqDto dispDto){
        GenEWBByIrnDispNICReqDto defaultDto = new  GenEWBByIrnDispNICReqDto();
        return defaultDto.hashCode() == dispDto.hashCode();
    }

}
