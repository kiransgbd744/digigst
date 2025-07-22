/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.ToString;

/**
 * @author Khalid1.Khan
 *
 */
@Data
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CancelIrnReqList implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SerializedName("req")
	@Expose
	@XmlElement(name = "cancel-einv-req")
	private List<CancelIrnReqDto> reqList;
	
	@XmlElement(name = "id-token")
	private String idToken; 
	
	

}
