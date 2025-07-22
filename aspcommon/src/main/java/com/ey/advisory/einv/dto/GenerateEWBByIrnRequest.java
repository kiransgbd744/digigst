package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateEWBByIrnRequest implements Serializable {

	/**
	 * @author Siva.Reddy
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("req")
	@XmlElement(name = "generate-ewbbyirn-req")
	private GenerateEWBByIrnERPReqDto generateEWBReq;

	
	@XmlElement(name = "id-token")
	private String idToken; 

}
